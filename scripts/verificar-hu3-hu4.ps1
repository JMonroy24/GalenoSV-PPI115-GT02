#Requires -Version 7.0
param(
    [string] $ApiBase = 'http://127.0.0.1:9080/galenoSV/api/v1',
    [string] $DatabaseContainer = 'GalenoSV-db'
)
$ErrorActionPreference = 'Stop'
$ApiBase = $ApiBase.TrimEnd('/')
$checks = 0
$consultaTemporal = $null
$examenTemporal = [guid]::NewGuid().ToString()
$marca = 'HU34-HTTP-' + [guid]::NewGuid().ToString('N')

function Sql([string] $sql) {
    $salida = $sql | docker exec -i $DatabaseContainer sh -c 'exec psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -v ON_ERROR_STOP=1 -At'
    if ($LASTEXITCODE -ne 0) { throw 'Falló la comprobación SQL local.' }
    return $salida
}
function Check([bool] $condicion, [string] $mensaje) {
    if (-not $condicion) { throw $mensaje }
    $script:checks++
}
function Api([string] $metodo, [string] $ruta, [int] $status = 200, $body = $null, [string] $etag = '') {
    $parametros = @{
        Uri = "$ApiBase$ruta"; Method = $metodo; SkipHttpErrorCheck = $true; NoProxy = $true; TimeoutSec = 25
        Headers = @{ Origin = 'http://localhost:3000' }
    }
    if ($etag) { $parametros.Headers['If-Match'] = $etag }
    if ($null -ne $body) { $parametros.Body = $body | ConvertTo-Json -Depth 10 -Compress; $parametros.ContentType = 'application/json; charset=utf-8' }
    $r = Invoke-WebRequest @parametros
    Check ($r.StatusCode -eq $status) "$metodo $ruta esperaba $status, devolvió $($r.StatusCode): $($r.Content)"
    Check ([string]$r.Headers['Access-Control-Allow-Origin'] -eq '*') "Falta CORS en $metodo $ruta"
    $json = if ($r.Content -and $r.Content.TrimStart().StartsWith('{')) { $r.Content | ConvertFrom-Json } else { $null }
    return [pscustomobject]@{ Body = $json; ETag = [string]$r.Headers.ETag; Headers = $r.Headers }
}

try {
    $contexto = (Sql @'
SELECT row_to_json(t) FROM (
 SELECT cp.id_procedimiento AS "idProcedimiento", pp.id_procedimiento_paso AS "idPaso",
 c.id_persona_rol AS "pacienteId", pr.id_persona_rol AS "responsableId",
 paciente.id_clinica AS "idClinica", pp.id_rol AS "idRol"
 FROM public.consulta_procedimiento cp JOIN public.consulta c USING (id_consulta)
 JOIN public.persona_rol paciente ON paciente.id_persona_rol = c.id_persona_rol
 JOIN public.clinica cl ON cl.id_clinica = paciente.id_clinica AND cl.activo
 JOIN public.procedimiento proc ON proc.id_procedimiento = cp.id_procedimiento AND proc.activo
 JOIN public.procedimiento_paso pp ON pp.id_procedimiento = cp.id_procedimiento
 JOIN public.rol r ON r.id_rol = pp.id_rol AND r.activo
 JOIN public.persona_rol pr ON pr.id_rol = pp.id_rol AND pr.id_clinica = paciente.id_clinica
 WHERE NOT EXISTS (SELECT 1 FROM public.procedimiento_paso_secuencia s
   WHERE s.id_procedimiento_paso_referencia = pp.id_procedimiento_paso AND upper(s.tipo_secuencia)='SIGUIENTE')
 ORDER BY cp.id_consulta_procedimiento, pr.id_persona_rol LIMIT 1
) t;
'@) | ConvertFrom-Json
    Check ($null -ne $contexto) 'No hay configuración clínica inicial para comprobar el flujo.'
    $existentes = (Api GET '/consultas-procedimientos-pasos?max=100').Body.items
    Check (@($existentes | Where-Object { -not $_.idProcedimientoPaso }).Count -eq 0) 'Hay FK nulas después de V6.'
    Check (@($existentes | Where-Object { $_.estado -eq 'COMPLETADO' -and -not $_.fechaFin }).Count -eq 0) 'Hay completados sin fechaFin.'
    foreach ($p in $existentes) {
        $detalle = (Api GET "/consultas-procedimientos-pasos/$($p.id)").Body
        Check ($detalle.idProcedimientoPaso -eq $p.idProcedimientoPaso) 'Listado y detalle difieren en la definición.'
    }
    $consulta = (Api POST '/consultas' 201 @{
        idPersonaRol = $contexto.pacienteId; referenciaExterna = $marca
        fechaInicio = [DateTime]::UtcNow.AddMinutes(-1).ToString("yyyy-MM-dd'T'HH:mm:ss'Z'")
    }).Body
    $consultaTemporal = [guid]::Parse($consulta.id).ToString()
    $cp = (Api POST '/consultas-procedimientos' 201 @{
        idConsulta = $consultaTemporal; idProcedimiento = $contexto.idProcedimiento
    }).Body
    $nuevoPaso = @{ idConsultaProcedimiento = $cp.id; idPersonaRol = $contexto.responsableId; estado = 'PENDIENTE'; valor = '{}' }
    $null = Api POST '/consultas-procedimientos-pasos' 422 $nuevoPaso
    $nuevoPaso.idProcedimientoPaso = $contexto.idPaso
    $paso = (Api POST '/consultas-procedimientos-pasos' 201 $nuevoPaso).Body
    Check ($paso.idProcedimientoPaso -eq $contexto.idPaso) 'POST no conservó la definición.'
    $null = Api POST '/consultas-procedimientos-pasos' 422 $nuevoPaso
    $ruta = "/consultas-procedimientos-pasos/$($paso.id)"
    $inicial = Api GET $ruta
    $null = Api PATCH $ruta 428 @{ estadoPaso = 'EN_CURSO'; valor = '{}' }
    $null = Api PATCH $ruta 422 @{ estadoPaso = 'PENDIENTE'; valor = '{}'; accion = 'RETROCEDER' } $inicial.ETag
    $enCurso = Api PATCH $ruta 200 @{ estadoPaso = 'EN_CURSO'; valor = '{"nota":"HTTP"}' } $inicial.ETag
    $null = Api PATCH $ruta 412 @{ estadoPaso = 'COMPLETADO'; valor = '{}' } $inicial.ETag
    $completado = Api PATCH $ruta 200 @{ estadoPaso = 'COMPLETADO'; valor = '{}' } $enCurso.ETag
    Check ($null -ne $completado.Body.fechaFin) 'Completar no registró fechaFin.'
    $finOriginal = [string]$completado.Body.fechaFin
    $detalle = (Api GET "/consultas/$consultaTemporal").Body
    Check (@($detalle.pasos).Count -eq 2) 'Completar no creó exactamente un sucesor.'
    $activo = @($detalle.pasos | Where-Object estado -eq 'EN_CURSO')[0]
    Check ($null -ne $activo) 'El siguiente paso no quedó activo.'
    $definicion = (Api GET "/procedimientos-pasos/$($activo.idProcedimientoPaso)").Body
    $filtros = "idClinica=$($contexto.idClinica)&idRol=$($definicion.idRol)&pacienteId=$($contexto.pacienteId)&desde=2000-01-01&hasta=2099-12-31&q=$marca&max=1"
    $filtrada = Api GET "/consultas?$filtros"
    Check ($filtrada.Body.total -eq 1 -and @($filtrada.Body.items).Count -eq 1) 'Falló el filtrado combinado antes de paginar.'
    Check ([string]$filtrada.Headers['total-registros'] -eq '1') 'El total de la cabecera difiere del total filtrado.'
    $paginaVacia = (Api GET "/consultas?$filtros&first=1").Body
    Check ($paginaVacia.total -eq 1 -and @($paginaVacia.items).Count -eq 0) 'La segunda página perdió el conteo filtrado.'
    Check ((Api GET "/consultas?idRol=$($contexto.idRol)&q=$marca").Body.total -eq 0) 'El filtro incluyó un rol de un paso histórico.'
    $null = Api GET '/consultas?idRol=invalido' 400
    $null = Api GET '/consultas?desde=2026-02-30' 400
    $null = Api GET '/consultas?desde=2026-11-24&hasta=2026-11-01' 400
    $guardado = Api PATCH $ruta 200 @{ estadoPaso = 'COMPLETADO'; valor = '{"nota":"editada"}' } $completado.ETag
    Check ([string]$guardado.Body.fechaFin -eq $finOriginal) 'Una actualización alteró fechaFin.'
    Check (@((Api GET "/consultas/$consultaTemporal").Body.pasos).Count -eq 2) 'Reintentar completar duplicó el sucesor.'
    $rutaActivo = "/consultas-procedimientos-pasos/$($activo.id)"
    $actual = Api GET $rutaActivo
    $null = Api PATCH $rutaActivo 200 @{ estadoPaso = 'EN_CURSO'; valor = '{}'; accion = 'RETROCEDER' } $actual.ETag
    Check ((Api GET $ruta).Body.estado -eq 'EN_CURSO') 'El retroceso no reactivó el anterior.'
    $null = Api PATCH $rutaActivo 412 @{ estadoPaso = 'COMPLETADO'; valor = '{}' } $actual.ETag
    $previo = Api GET $ruta
    $null = Api PATCH $ruta 200 @{ estadoPaso = 'COMPLETADO'; valor = '{}' } $previo.ETag
    $detalle = (Api GET "/consultas/$consultaTemporal").Body
    Check (@($detalle.pasos).Count -eq 2 -and @($detalle.pasos | Where-Object estado -eq 'EN_CURSO')[0].id -eq $activo.id) 'El avance no reutilizó el sucesor.'
    $null = Sql "INSERT INTO public.examen (id_examen,nombre,activo) VALUES ('$examenTemporal','$marca',false);"
    $null = Api POST '/ordenes-examen' 422 @{ idConsultaProcedimientoPaso = $activo.id; idExamen = $examenTemporal; indicaciones = 'Prueba' }
    for ($i = 0; $i -lt 10; $i++) {
        $detalle = (Api GET "/consultas/$consultaTemporal").Body
        $vigentes = @($detalle.pasos | Where-Object estado -eq 'EN_CURSO')
        if ($vigentes.Count -eq 0) { break }
        Check ($vigentes.Count -eq 1) 'Hay más de un sucesor activo.'
        $r = "/consultas-procedimientos-pasos/$($vigentes[0].id)"
        $get = Api GET $r
        $null = Api PATCH $r 200 @{ estadoPaso = 'COMPLETADO'; valor = '{}' } $get.ETag
    }
    $cerrada = (Api GET "/consultas/$consultaTemporal").Body
    Check ($null -ne $cerrada.fechaFin) 'El paso final no cerró persistentemente la consulta.'
    $terminal = @($cerrada.pasos | Where-Object { $_.idProcedimientoPaso -ne $contexto.idPaso })[-1]
    $terminalGet = Api GET "/consultas-procedimientos-pasos/$($terminal.id)"
    $null = Api PATCH "/consultas-procedimientos-pasos/$($terminal.id)" 200 @{ estadoPaso = 'COMPLETADO'; valor = '{}'; accion = 'RETROCEDER' } $terminalGet.ETag
    Check ($null -eq (Api GET "/consultas/$consultaTemporal").Body.fechaFin) 'El retroceso final no reabrió la consulta.'
    $null = Api GET '/consultas/38f051cd-a699-4652-af8d-e76ea6ef8dc2'
    $preflight = Invoke-WebRequest -Uri "$ApiBase$ruta" -Method Options -NoProxy -SkipHttpErrorCheck -Headers @{
        Origin = 'http://localhost:3000'; 'Access-Control-Request-Method' = 'PATCH'; 'Access-Control-Request-Headers' = 'content-type,if-match'
    }
    Check ($preflight.StatusCode -eq 200 -and [string]$preflight.Headers['Access-Control-Allow-Origin'] -eq '*') 'Falló el preflight CORS.'
    $openapiUri = ([uri]$ApiBase).GetLeftPart([System.UriPartial]::Authority) + '/openapi?format=JSON'
    $openapi = Invoke-RestMethod -Uri $openapiUri -NoProxy -TimeoutSec 20
    $nombres = @($openapi.paths.'/api/v1/consultas'.get.parameters.name)
    foreach ($nombre in @('idClinica','idRol','desde','hasta','pacienteId','first','max','q')) {
        Check ($nombre -in $nombres) "OpenAPI no documenta $nombre."
    }
    Write-Output "OK: $checks comprobaciones HTTP/SQL; flujo, historial, filtros, ETag 412, activos, detalle 200, OpenAPI y CORS."
} finally {
    if ($consultaTemporal) {
        $consultaTemporal = [guid]::Parse($consultaTemporal).ToString()
        $null = Sql "BEGIN; DELETE FROM public.consulta_procedimiento_paso WHERE id_consulta_procedimiento IN (SELECT id_consulta_procedimiento FROM public.consulta_procedimiento WHERE id_consulta='$consultaTemporal'); DELETE FROM public.consulta_procedimiento WHERE id_consulta='$consultaTemporal'; DELETE FROM public.consulta WHERE id_consulta='$consultaTemporal'; COMMIT;"
    }
    $null = Sql "DELETE FROM public.examen WHERE id_examen='$examenTemporal';"
}

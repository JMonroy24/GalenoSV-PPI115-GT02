#Requires -Version 7.0
param(
    [string] $ApiBase = 'http://localhost:9080/galenoSV/api/v1'
)
$ErrorActionPreference = 'Stop'
$ApiBase = $ApiBase.TrimEnd('/')

# Prueba HTTP de solo lectura: detecta también rutas heredadas que omitan filtros.
$documentos = Invoke-RestMethod -Uri "$ApiBase/documentos?first=0&max=100" -TimeoutSec 20
$documento = $documentos.items | Where-Object {
    $_.idPersona -and $_.idTipoDocumento -and $_.valor
} | Select-Object -First 1
if (-not $documento) {
    throw 'Se requiere un documento existente con tipo y persona para verificar el filtro.'
}
$tipo = [Uri]::EscapeDataString([string] $documento.idTipoDocumento)
$valor = [Uri]::EscapeDataString([string] $documento.valor)
$respuesta = Invoke-RestMethod -Uri "$ApiBase/personas?tipoDocumentoId=$tipo&valor=$valor" -TimeoutSec 20
if (@($respuesta.items).Count -ne 1 -or $respuesta.total -ne 1 -or
        $respuesta.items[0].id -ne $documento.idPersona) {
    throw 'El filtro por tipo y valor exacto no devuelve únicamente la persona del documento.'
}

$inexistente = [Uri]::EscapeDataString("inexistente-$([Guid]::NewGuid())")
$vacia = Invoke-RestMethod -Uri "$ApiBase/personas?tipoDocumentoId=$tipo&valor=$inexistente" -TimeoutSec 20
if (@($vacia.items).Count -ne 0 -or $vacia.total -ne 0) {
    throw 'Un valor inexistente debe devolver una lista vacía y total cero.'
}

$soloValor = Invoke-RestMethod -Uri "$ApiBase/personas?valor=$valor" -TimeoutSec 20
if ($documento.idPersona -notin @($soloValor.items.id)) {
    throw 'El filtro por valor sin tipo no devuelve la persona esperada.'
}
$soloValorInexistente = Invoke-RestMethod -Uri "$ApiBase/personas?valor=$inexistente" -TimeoutSec 20
if (@($soloValorInexistente.items).Count -ne 0 -or $soloValorInexistente.total -ne 0) {
    throw 'El filtro por valor debe aplicarse también sin tipoDocumentoId.'
}
Write-Output 'OK: documento exacto devuelve una persona; valor inexistente devuelve cero, con y sin tipo.'

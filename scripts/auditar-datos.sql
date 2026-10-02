-- Lectura: devuelve sólo conteos, sin documentos ni información clínica.

SELECT 'clinica: campos inválidos' AS problema, count(*) AS cantidad FROM clinica WHERE nombre IS NULL OR (nombre IS NOT NULL AND NOT (length(btrim(nombre)) > 0)) OR NOT (char_length(nombre) <= 255) OR activo IS NULL OR NOT (char_length(tipo) <= 20) OR NOT (char_length(comentarios) <= 2000);

SELECT 'consulta: campos inválidos' AS problema, count(*) AS cantidad FROM consulta WHERE fecha_inicio IS NULL OR NOT (char_length(referencia_externa) <= 255) OR NOT (char_length(observaciones) <= 2000) OR id_persona_rol IS NULL OR NOT (fecha_fin IS NULL OR fecha_fin >= fecha_inicio);

SELECT 'consulta_procedimiento: campos inválidos' AS problema, count(*) AS cantidad FROM consulta_procedimiento WHERE fecha_inicio IS NULL OR NOT (char_length(observaciones) <= 2000) OR id_consulta IS NULL OR id_procedimiento IS NULL OR NOT (fecha_fin IS NULL OR fecha_fin >= fecha_inicio);

SELECT 'consulta_procedimiento_paso: campos inválidos' AS problema, count(*) AS cantidad FROM consulta_procedimiento_paso WHERE fecha_inicio IS NULL OR estado IS NULL OR (estado IS NOT NULL AND NOT (length(btrim(estado)) > 0)) OR NOT (char_length(estado) <= 20) OR id_consulta_procedimiento IS NULL OR id_persona_rol IS NULL OR NOT (fecha_fin IS NULL OR fecha_fin >= fecha_inicio);

SELECT 'documento: campos inválidos' AS problema, count(*) AS cantidad FROM documento WHERE valor IS NULL OR (valor IS NOT NULL AND NOT (length(btrim(valor)) > 0)) OR NOT (char_length(valor) <= 50) OR NOT (char_length(ruta_fisica) <= 500) OR id_persona IS NULL OR id_tipo_documento IS NULL;

SELECT 'examen: campos inválidos' AS problema, count(*) AS cantidad FROM examen WHERE nombre IS NULL OR (nombre IS NOT NULL AND NOT (length(btrim(nombre)) > 0)) OR NOT (char_length(nombre) <= 255) OR activo IS NULL OR NOT (char_length(observaciones) <= 2000);

SELECT 'examen_resultado: campos inválidos' AS problema, count(*) AS cantidad FROM examen_resultado WHERE fecha_creacion IS NULL OR resultado IS NULL OR (resultado IS NOT NULL AND NOT (length(btrim(resultado)) > 0)) OR NOT (char_length(resultado) <= 4000) OR interpretacion IS NULL OR (interpretacion IS NOT NULL AND NOT (length(btrim(interpretacion)) > 0)) OR NOT (char_length(interpretacion) <= 4000) OR NOT (char_length(ruta_atestado) <= 500) OR id_orden_examen IS NULL;

SELECT 'examen_tipo_examen: campos inválidos' AS problema, count(*) AS cantidad FROM examen_tipo_examen WHERE fecha_creacion IS NULL OR NOT (char_length(observaciones) <= 2000) OR id_examen IS NULL OR id_tipo_examen IS NULL;

SELECT 'medio_contacto: campos inválidos' AS problema, count(*) AS cantidad FROM medio_contacto WHERE valor IS NULL OR (valor IS NOT NULL AND NOT (length(btrim(valor)) > 0)) OR NOT (char_length(valor) <= 255) OR fecha_creacion IS NULL OR id_persona IS NULL OR id_tipo_medio_contacto IS NULL;

SELECT 'orden_examen: campos inválidos' AS problema, count(*) AS cantidad FROM orden_examen WHERE fecha_creacion IS NULL OR indicaciones IS NULL OR (indicaciones IS NOT NULL AND NOT (length(btrim(indicaciones)) > 0)) OR NOT (char_length(indicaciones) <= 2000) OR id_consulta_procedimiento_paso IS NULL;

SELECT 'persona: campos inválidos' AS problema, count(*) AS cantidad FROM persona WHERE nombres IS NULL OR (nombres IS NOT NULL AND NOT (length(btrim(nombres)) > 0)) OR NOT (char_length(nombres) <= 255) OR apellidos IS NULL OR (apellidos IS NOT NULL AND NOT (length(btrim(apellidos)) > 0)) OR NOT (char_length(apellidos) <= 255) OR fecha_creacion IS NULL;

SELECT 'persona_rol: campos inválidos' AS problema, count(*) AS cantidad FROM persona_rol WHERE fecha_creacion IS NULL OR id_persona IS NULL OR id_rol IS NULL;

SELECT 'procedimiento: campos inválidos' AS problema, count(*) AS cantidad FROM procedimiento WHERE nombre IS NULL OR (nombre IS NOT NULL AND NOT (length(btrim(nombre)) > 0)) OR NOT (char_length(nombre) <= 155) OR activo IS NULL OR NOT (char_length(observaciones) <= 2000);

SELECT 'procedimiento_paso: campos inválidos' AS problema, count(*) AS cantidad FROM procedimiento_paso WHERE nombre IS NULL OR (nombre IS NOT NULL AND NOT (length(btrim(nombre)) > 0)) OR NOT (char_length(nombre) <= 155) OR indica_fin IS NULL OR id_procedimiento IS NULL;

SELECT 'procedimiento_paso_examen: campos inválidos' AS problema, count(*) AS cantidad FROM procedimiento_paso_examen WHERE fecha_creacion IS NULL OR activo IS NULL OR NOT (char_length(observaciones) <= 2000) OR id_examen IS NULL OR id_procedimiento_paso IS NULL;

SELECT 'procedimiento_paso_secuencia: campos inválidos' AS problema, count(*) AS cantidad FROM procedimiento_paso_secuencia WHERE id_procedimiento_paso_referencia IS NULL OR tipo_secuencia IS NULL OR (tipo_secuencia IS NOT NULL AND NOT (length(btrim(tipo_secuencia)) > 0)) OR NOT (char_length(tipo_secuencia) <= 20) OR id_procedimiento_paso IS NULL;

SELECT 'rol: campos inválidos' AS problema, count(*) AS cantidad FROM rol WHERE nombre IS NULL OR (nombre IS NOT NULL AND NOT (length(btrim(nombre)) > 0)) OR NOT (char_length(nombre) <= 155) OR activo IS NULL OR NOT (char_length(observaciones) <= 2000);

SELECT 'tipo_documento: campos inválidos' AS problema, count(*) AS cantidad FROM tipo_documento WHERE nombre IS NULL OR (nombre IS NOT NULL AND NOT (length(btrim(nombre)) > 0)) OR NOT (char_length(nombre) <= 155) OR NOT (char_length(indicaciones) <= 2000) OR NOT (char_length(expresion_regular) <= 500) OR activo IS NULL;

SELECT 'tipo_examen: campos inválidos' AS problema, count(*) AS cantidad FROM tipo_examen WHERE nombre IS NULL OR (nombre IS NOT NULL AND NOT (length(btrim(nombre)) > 0)) OR NOT (char_length(nombre) <= 255) OR activo IS NULL OR NOT (char_length(observaciones) <= 2000);

SELECT 'tipo_medio_contacto: campos inválidos' AS problema, count(*) AS cantidad FROM tipo_medio_contacto WHERE nombre IS NULL OR (nombre IS NOT NULL AND NOT (length(btrim(nombre)) > 0)) OR NOT (char_length(nombre) <= 155) OR NOT (char_length(indicaciones) <= 2000) OR NOT (char_length(expresion_regular) <= 500) OR activo IS NULL;

SELECT 'uq_rol_nombre: grupos duplicados' AS problema, count(*) AS cantidad FROM (SELECT lower(btrim(nombre)) FROM rol GROUP BY lower(btrim(nombre)) HAVING count(*) > 1) d;

SELECT 'uq_clinica_nombre: grupos duplicados' AS problema, count(*) AS cantidad FROM (SELECT lower(btrim(nombre)) FROM clinica GROUP BY lower(btrim(nombre)) HAVING count(*) > 1) d;

SELECT 'uq_procedimiento_nombre: grupos duplicados' AS problema, count(*) AS cantidad FROM (SELECT lower(btrim(nombre)) FROM procedimiento GROUP BY lower(btrim(nombre)) HAVING count(*) > 1) d;

SELECT 'uq_examen_nombre: grupos duplicados' AS problema, count(*) AS cantidad FROM (SELECT lower(btrim(nombre)) FROM examen GROUP BY lower(btrim(nombre)) HAVING count(*) > 1) d;

SELECT 'uq_tipo_examen_nombre: grupos duplicados' AS problema, count(*) AS cantidad FROM (SELECT lower(btrim(nombre)) FROM tipo_examen GROUP BY lower(btrim(nombre)) HAVING count(*) > 1) d;

SELECT 'uq_tipo_documento_nombre: grupos duplicados' AS problema, count(*) AS cantidad FROM (SELECT lower(btrim(nombre)) FROM tipo_documento GROUP BY lower(btrim(nombre)) HAVING count(*) > 1) d;

SELECT 'uq_tipo_medio_contacto_nombre: grupos duplicados' AS problema, count(*) AS cantidad FROM (SELECT lower(btrim(nombre)) FROM tipo_medio_contacto GROUP BY lower(btrim(nombre)) HAVING count(*) > 1) d;

SELECT 'uq_documento_tipo_valor: grupos duplicados' AS problema, count(*) AS cantidad FROM (SELECT id_tipo_documento, lower(btrim(valor)) FROM documento GROUP BY id_tipo_documento, lower(btrim(valor)) HAVING count(*) > 1) d;

SELECT 'uq_medio_contacto: grupos duplicados' AS problema, count(*) AS cantidad FROM (SELECT id_persona, id_tipo_medio_contacto, lower(btrim(valor)) FROM medio_contacto GROUP BY id_persona, id_tipo_medio_contacto, lower(btrim(valor)) HAVING count(*) > 1) d;

SELECT 'uq_persona_rol: grupos duplicados' AS problema, count(*) AS cantidad FROM (SELECT id_persona, id_rol, id_clinica FROM persona_rol GROUP BY id_persona, id_rol, id_clinica HAVING count(*) > 1) d;

SELECT 'uq_paso_nombre: grupos duplicados' AS problema, count(*) AS cantidad FROM (SELECT id_procedimiento, lower(btrim(nombre)) FROM procedimiento_paso GROUP BY id_procedimiento, lower(btrim(nombre)) HAVING count(*) > 1) d;

SELECT 'uq_paso_examen: grupos duplicados' AS problema, count(*) AS cantidad FROM (SELECT id_procedimiento_paso, id_examen FROM procedimiento_paso_examen GROUP BY id_procedimiento_paso, id_examen HAVING count(*) > 1) d;

SELECT 'uq_examen_tipo: grupos duplicados' AS problema, count(*) AS cantidad FROM (SELECT id_examen, id_tipo_examen FROM examen_tipo_examen GROUP BY id_examen, id_tipo_examen HAVING count(*) > 1) d;

SELECT 'uq_secuencia: grupos duplicados' AS problema, count(*) AS cantidad FROM (SELECT id_procedimiento_paso, id_procedimiento_paso_referencia, lower(btrim(tipo_secuencia)) FROM procedimiento_paso_secuencia GROUP BY id_procedimiento_paso, id_procedimiento_paso_referencia, lower(btrim(tipo_secuencia)) HAVING count(*) > 1) d;

SELECT 'secuencias: destino inexistente u otro procedimiento' AS problema, count(*) AS cantidad FROM procedimiento_paso_secuencia s JOIN procedimiento_paso p ON p.id_procedimiento_paso = s.id_procedimiento_paso LEFT JOIN procedimiento_paso r ON r.id_procedimiento_paso = s.id_procedimiento_paso_referencia WHERE r.id_procedimiento_paso IS NULL OR p.id_procedimiento <> r.id_procedimiento;

WITH RECURSIVE rutas AS (
 SELECT id_procedimiento_paso AS origen, id_procedimiento_paso_referencia AS destino,
 ARRAY[id_procedimiento_paso] AS visitados, id_procedimiento_paso = id_procedimiento_paso_referencia AS ciclo
 FROM procedimiento_paso_secuencia
 UNION ALL
 SELECT r.origen, s.id_procedimiento_paso_referencia, r.visitados || r.destino,
 s.id_procedimiento_paso_referencia = ANY(r.visitados || r.destino)
 FROM rutas r JOIN procedimiento_paso_secuencia s ON s.id_procedimiento_paso = r.destino
 WHERE NOT r.ciclo
)
SELECT 'secuencias: rutas cíclicas' AS problema, count(*) AS cantidad FROM rutas WHERE ciclo;

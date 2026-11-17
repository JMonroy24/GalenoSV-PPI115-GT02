# Fase 0: CORS del backend

La URL base confirmada por `server.xml` y `RestApplication` es
`http://localhost:9080/galenoSV/api/v1`. El origen permitido del frontend es
exactamente `http://localhost:3000` (sin barra final). Otros puertos, dominios y
`127.0.0.1` son orígenes distintos y no reciben autorización CORS.

`CorsFilter` se registra automáticamente con `@Provider`. Implementa
`ContainerResponseFilter` para añadir los encabezados a las respuestas REST y
`ContainerRequestFilter` con `@PreMatching` para responder al preflight con 204
antes de seleccionar un recurso. Un preflight de otro origen recibe 403.
El navegador solo autoriza los métodos y encabezados anunciados por el filtro.
El registro y el procesamiento previo al recurso siguen la
[especificación de Jakarta REST 4.0](https://jakarta.ee/specifications/restful-ws/4.0/jakarta-restful-ws-spec-4.0.html#filters).

Se permiten GET, HEAD, POST, PUT, DELETE y OPTIONS, y los encabezados Accept,
Content-Type, Authorization, If-Match e If-None-Match. JavaScript puede leer
ETag y Location. El permiso del preflight se almacena hasta 600 segundos.
Se conserva `Vary` y se añade `Origin` para separar las respuestas en cachés.
No se habilitan cookies ni `credentials: 'include'`. El frontend no necesita
enviar Authorization ni credenciales BASIC. Permitir ese encabezado en CORS
no obliga a utilizarlo.

## Seguridad actual

`web.xml` ya no declara restricciones de autenticación ni transporte para
`/api/*`, ni configura BASIC. Se retiró la página de error que añadía el desafío
`WWW-Authenticate: Basic` a las respuestas 401.

Las operaciones de `GenericResource` y los listados específicos de
`PersonaResource`, `PersonaRolResource` y `ConsultaResource` usan `@PermitAll`.
Esto incluye los métodos CRUD heredados por los catálogos y los filtros por
documento y persona utilizados por el flujo de login. La validación del número
de documento corresponde a la lógica de negocio; el contenedor permite acceso
anónimo a estas operaciones.

`server.xml` ya expone HTTP en 9080. La API no exige redirigir a HTTPS ni validar
el certificado local. La configuración TLS y el registro de usuarios de Liberty
siguen disponibles, pero no imponen autenticación a la API.

## Pruebas

```powershell
mvn '-Dtest=CorsFilterTest,CorsSecurityConfigTest' test
mvn verify
```

`CorsFilterTest` cubre el origen permitido, respuestas REST y preflight.
`CorsSecurityConfigTest` verifica el puerto HTTP 9080, la ausencia de BASIC y
restricciones de transporte, y el acceso anónimo a métodos heredados y listados
específicos. Si Windows bloquea la conexión dinámica de Mockito, puede cargarse
su JAR como agente al iniciar la JVM de pruebas.

Con Liberty iniciado y la aplicación redesplegada, comprobar el preflight sin
credenciales desde PowerShell:

```powershell
curl.exe -i -X OPTIONS "http://localhost:9080/galenoSV/api/v1/clinicas" -H "Origin: http://localhost:3000" -H "Access-Control-Request-Method: PUT" -H "Access-Control-Request-Headers: content-type,if-match"
```

La respuesta esperada es 204, sin redirección ni desafío BASIC, e incluye
`Access-Control-Allow-Origin: http://localhost:3000`, los métodos y encabezados
permitidos y `Access-Control-Max-Age: 600`. Para comprobar el rechazo, repetir
con `Origin: https://example.com`: se espera 403 sin autorización CORS.

Comprobar también una petición real sin Authorization:

```powershell
curl.exe -i "http://localhost:9080/galenoSV/api/v1/clinicas?first=0&max=20" -H "Origin: http://localhost:3000"
```

Con la base de datos disponible se espera 200 y el listado JSON, sin redirección
a HTTPS ni desafío BASIC. La respuesta incluye el origen permitido.

## Siguiente paso

Abrir la carpeta del frontend y revisar `default_service.js`, la configuración
de URL base y un consumidor del servicio. El contrato actual de listado usa
`first`, `max` y `q`; el cuerpo tiene `items`, `total`, `first` y `max`.
Esta etapa todavía no modifica el cliente.

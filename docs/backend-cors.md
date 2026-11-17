# Tarea 2.2: configuración CORS del backend

La URL base confirmada por `server.xml` y `RestApplication` es
`http://localhost:9080/galenoSV/api/v1`. Todas las respuestas REST incluyen
`Access-Control-Allow-Origin: *`, por lo que se admiten peticiones desde cualquier
origen, incluidos otros puertos de desarrollo y `127.0.0.1`.

`CorsFilter` se registra automáticamente con `@Provider`. Implementa
`ContainerResponseFilter` para añadir los encabezados a las respuestas REST y
`ContainerRequestFilter` con `@PreMatching` y prioridad anterior a
`Priorities.AUTHENTICATION` para responder a cualquier petición `OPTIONS` con
`200 OK`, sin cuerpo y antes de seleccionar un recurso. No exige Origin,
Access-Control-Request-Method, tokens ni credenciales para responder a OPTIONS.
El navegador solo autoriza los métodos y encabezados anunciados por el filtro.
El registro y el procesamiento previo al recurso siguen la
[especificación de Jakarta REST 4.0](https://jakarta.ee/specifications/restful-ws/4.0/jakarta-restful-ws-spec-4.0.html#filters).

Todas las respuestas incluyen los métodos GET, POST, PUT, DELETE, OPTIONS, HEAD
y PATCH, y los encabezados origin, content-type, accept, authorization, if-match
e if-none-match. PATCH e If-Match mantienen la actualización de formularios
médicos. JavaScript puede leer `total-registros`, ETag y Location.
Para respuestas `PageResponse`, el filtro añade `total-registros` con el total
de elementos del conjunto, incluso cuando la página está vacía. El cuerpo JSON
de paginación se conserva. El permiso del preflight se almacena hasta 600 segundos.
Los encabezados `Vary` existentes se conservan.
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

`CorsFilterTest` cubre cualquier origen, respuestas REST y de error, OPTIONS sin
tokens, la exposición del total paginado y el registro previo a autenticación.
`CorsSecurityConfigTest` verifica el puerto HTTP 9080, la ausencia de BASIC y
restricciones de transporte, y el acceso anónimo a métodos heredados y listados
específicos. Si Windows bloquea la conexión dinámica de Mockito, puede cargarse
su JAR como agente al iniciar la JVM de pruebas.

Con Liberty iniciado y la aplicación redesplegada, comprobar el preflight sin
credenciales desde PowerShell:

```powershell
curl.exe -i -X OPTIONS "http://localhost:9080/galenoSV/api/v1/clinicas" -H "Origin: http://localhost:3000" -H "Access-Control-Request-Method: PUT" -H "Access-Control-Request-Headers: content-type,if-match"
```

La respuesta esperada es 200, sin redirección ni desafío BASIC, e incluye
`Access-Control-Allow-Origin: *`, los métodos y encabezados permitidos,
`Access-Control-Expose-Headers: total-registros, ETag, Location` y
`Access-Control-Max-Age: 600`. Con `Origin: https://example.com` también se espera
200. OPTIONS sin encabezados de preflight también responde 200.

Comprobar también una petición real sin Authorization:

```powershell
curl.exe -i "http://localhost:9080/galenoSV/api/v1/clinicas?first=0&max=20" -H "Origin: http://localhost:3000"
```

Con la base de datos disponible se espera 200 y el listado JSON, sin redirección
a HTTPS ni desafío BASIC. La respuesta incluye el origen `*` y el encabezado
`total-registros`, cuyo valor coincide con `total` en el cuerpo JSON.

## Siguiente paso

Abrir la carpeta del frontend y revisar `default_service.js`, la configuración
de URL base y un consumidor del servicio. El contrato actual de listado usa
`first`, `max` y `q`; el cuerpo tiene `items`, `total`, `first` y `max`.
Esta etapa todavía no modifica el cliente.

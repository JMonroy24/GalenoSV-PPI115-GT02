# Fase 0: CORS del backend

La URL base confirmada por `server.xml` y `RestApplication` es
`https://localhost:9443/galenoSV/api/v1`. El origen permitido del frontend es
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
No se habilitan cookies ni `credentials: 'include'`; la autenticación actual
usa el encabezado Authorization.

## Seguridad actual

En `web.xml`, únicamente OPTIONS queda exento de BASIC y del requisito de
transporte HTTPS. Las operaciones conservan sus roles y `CONFIDENTIAL`.
No se han cambiado usuarios, autenticación, esquema ni dependencias.
La separación de métodos usa las reglas de
[restricciones de seguridad de Jakarta Servlet](https://jakarta.ee/specifications/servlet/6.1/jakarta-servlet-spec-6.1.html#uncovered-http-protocol-methods).

Las operaciones HTTP en el puerto 9080 se redirigen a HTTPS. El futuro cliente
debe usar directamente el puerto 9443 y el navegador debe confiar en el
certificado local de Liberty. CORS no sustituye la autenticación: todavía se
requieren las credenciales BASIC actuales hasta implementar el token en Fase 1.

Un `ContainerResponseFilter` solo procesa respuestas que pasan por Jakarta REST.
Los rechazos de autenticación o transporte producidos previamente por el
contenedor pueden no incluir encabezados CORS. No se debe interpretar ese caso
como un fallo del preflight ni desactivar la seguridad para resolverlo.

## Pruebas

```powershell
mvn '-Dtest=CorsFilterTest,CorsSecurityConfigTest' test
mvn verify
```

Validación realizada: 28 pruebas nuevas correctas; suite completa de 865 pruebas,
sin fallos ni errores, con 12 omitidas. JaCoCo cubre el 100% de las líneas y ramas
de `CorsFilter`; la cobertura global de líneas es 78.03%. El WAR se generó y
superó el umbral existente de 65%; subirlo a 80% sigue pendiente para Fase 1.
En este entorno aislado se cargó Mockito como agente al iniciar la JVM para
evitar el bloqueo de Windows al adjuntarlo dinámicamente. No se modificó el POM.

Con Liberty iniciado y la aplicación redesplegada, comprobar el preflight sin
credenciales desde PowerShell:

```powershell
curl.exe -i -X OPTIONS "http://localhost:9080/galenoSV/api/v1/clinicas" -H "Origin: http://localhost:3000" -H "Access-Control-Request-Method: PUT" -H "Access-Control-Request-Headers: authorization,content-type,if-match"
```

La respuesta esperada es 204, sin redirección ni desafío BASIC, e incluye
`Access-Control-Allow-Origin: http://localhost:3000`, los métodos y encabezados
permitidos y `Access-Control-Max-Age: 600`. Para comprobar el rechazo, repetir
con `Origin: https://example.com`: se espera 403 sin autorización CORS.

## Siguiente paso

Abrir la carpeta del frontend y revisar `default_service.js`, la configuración
de URL base y un consumidor del servicio. El contrato actual de listado usa
`first`, `max` y `q`; el cuerpo tiene `items`, `total`, `first` y `max`.
Esta etapa todavía no modifica el cliente.

<div align="center">

<img src="src/main/webapp/resources/images/galenosv-logo.png" alt="Logo de GalenoSV" width="160"/>

# GalenoSV

**Sistema de Gestión Clínica**

Proyecto del curso **PPI-115 · Ciclo 2026** · Grupo de Teoría **GT02**
Facultad Multidisciplinaria de Occidente · Universidad de El Salvador

![Java](https://img.shields.io/badge/Java-21-orange)
![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-11-blue)
![PrimeFaces](https://img.shields.io/badge/PrimeFaces-15-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-336791)
![Open Liberty](https://img.shields.io/badge/Open%20Liberty-runtime-purple)

</div>

---

## Tabla de contenido

1. [Descripción](#descripción)
2. [Integrantes](#integrantes)
3. [Funcionalidades](#funcionalidades)
4. [Tecnologías](#tecnologías)
5. [Arquitectura](#arquitectura)
6. [Estructura del proyecto](#estructura-del-proyecto)
7. [Requisitos previos](#requisitos-previos)
8. [Instalación y ejecución](#instalación-y-ejecución)
9. [Base de datos](#base-de-datos)
10. [Pruebas y calidad](#pruebas-y-calidad)
11. [Integración continua](#integración-continua)
12. [Licencia y créditos](#licencia-y-créditos)

---

## Descripción

**GalenoSV** es una aplicación web para la gestión de la atención clínica. Permite administrar
catálogos médicos, registrar personas y sus roles, y dar seguimiento al ciclo completo de una
consulta: procedimientos, pasos ejecutados, órdenes de examen y resultados.

El sistema está construido sobre la plataforma **Jakarta EE 11** con interfaz **Jakarta Faces +
PrimeFaces**, persistencia con **JPA (EclipseLink)** sobre **PostgreSQL**, y se despliega en
**Open Liberty**.

## Integrantes

| Nombre |
| --- |
| Melvin Monroy |
| Josue Escobar |
| Benito Palma |

## Funcionalidades

**Catálogos**
- Roles, tipos de documento, tipos de medio de contacto.
- Tipos de examen y exámenes (con asignación de tipos a cada examen).
- Procedimientos y sus pasos, incluyendo secuencias entre pasos y exámenes asociados.
- Clínicas.

**Personas**
- Gestión de personas, documentos de identificación y medios de contacto.
- Asignación de roles a cada persona.

**Atención clínica**
- Registro de consultas, procedimientos de la consulta y pasos del procedimiento.
- Órdenes de examen y captura de resultados.

**Transversales**
- Selección de clínica y cambio de rol durante la sesión.
- Interfaz bilingüe (español / inglés).
- Paginación diferida (*lazy loading*) en las tablas de datos.
- Control de concurrencia optimista mediante columna de versión.
- Validaciones de negocio e integridad (rangos de fechas, expresiones regulares, secuencias sin ciclos).

## Tecnologías

| Capa | Tecnología |
| --- | --- |
| Lenguaje | Java 21 |
| Plataforma | Jakarta EE 11 (CDI, Faces, JPA, Bean Validation, JTA) |
| Interfaz | Jakarta Faces (Facelets) + PrimeFaces 15 |
| Persistencia | EclipseLink 5.0 · PostgreSQL (driver 42.7.2) |
| Servidor | Open Liberty (`liberty-maven-plugin` 3.12.2) |
| Construcción | Maven (empaquetado WAR) |
| Pruebas | JUnit 5 · Mockito 5 · Testcontainers · JaCoCo |
| CI | GitHub Actions |

## Arquitectura

El proyecto sigue una organización por capas, con el paquete base
`sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv`:

| Paquete | Responsabilidad |
| --- | --- |
| `entities` | Entidades JPA, validadores personalizados y clases base (`EntidadVersionada`, `Auditable`). |
| `control` | DAOs genéricos y específicos, servicios de negocio (`AsignacionService`) y validaciones comunes. |
| `boundary.jsf` | *Backing beans* de Faces (`*Model`), convertidores, `GenericLazyDataModel` y `SesionBean`. |
| `persistence` | Personalizaciones de EclipseLink para el manejo de `UUID` en PostgreSQL. |

Las vistas (`.xhtml`) reutilizan componentes de composición para el CRUD de catálogos y de
entidades transaccionales (`resources/crud/`).

## Estructura del proyecto

```text
GalenoSV/
├── .github/workflows/validacion.yml   # Pipeline de CI
├── scripts/auditar-datos.sql          # Auditoría de datos previa a migrar
├── compose.yaml                       # PostgreSQL opcional para desarrollo
├── pom.xml
└── src/
    ├── main/
    │   ├── java/.../galenosv/         # entities · control · boundary · persistence
    │   ├── liberty/config/server.xml  # Configuración de Open Liberty
    │   ├── resources/                 # persistence.xml, mensajes i18n, migración V1
    │   ├── db/migration/              # Migraciones V2 y V3
    │   └── webapp/                    # Vistas XHTML, plantillas, CSS e imágenes
    └── test/
        ├── java/.../galenosv/         # Pruebas unitarias e integración
        └── resources/db/              # Esquema base de referencia
```

## Requisitos previos

- **JDK 21**
- **Maven 3.9+**
- **PostgreSQL** accesible, con el esquema de `clinica_ppi115_2026_08_20.sql`
- **Open Liberty** compatible con Jakarta EE 11 (`jakartaee-11.0`). Si ya cuenta con una instalación,
  defina la variable de entorno `WLP_INSTALL_DIR` apuntando a ella.
- **Docker** (opcional): para la base de desarrollo de `compose.yaml` y para las pruebas con Testcontainers.

## Instalación y ejecución

**1. Clonar el repositorio**

```bash
git clone <url-del-repositorio>
cd GalenoSV-PPI115-GT02
```

**2. Configurar la conexión a la base de datos**

La fuente de datos `jdbc/galenoSV` se define en
[`src/main/liberty/config/server.xml`](src/main/liberty/config/server.xml). Valores por defecto:


Los usuarios de demostración son `admin` (ADMINISTRADOR) y `clinico`
(PERSONAL_CLINICO). Las variables de entorno reemplazan estos valores.
Para otra instalación, configure las credenciales que **ya tiene** su base;
no es necesario modificar sus usuarios, tablas o datos.
Puede usar opcionalmente `src/main/liberty/config/server.env`, excluido de Git.
`compose.yaml` es una alternativa opcional para una base propia de desarrollo.

**Configurar HTTPS y las claves de autenticación de Liberty**

En PowerShell 7, ejecute una vez desde la raíz del repositorio:

```powershell
./scripts/configurar-seguridad-local.ps1
```

El script genera `GALENOSV_KEYSTORE_PASSWORD` y `GALENOSV_LTPA_PASSWORD` en
`src/main/liberty/config/server.env`, excluido de Git. Conserva los valores
existentes y las credenciales de PostgreSQL y de los usuarios de la aplicación.
En otros entornos, defina ambas variables antes de arrancar el servidor.
Mantenga estas claves entre reinicios; no las regenere mientras reutiliza los
archivos de seguridad existentes.

`server.xml` configura el [almacén TLS de Liberty](https://openliberty.io/docs/latest/reference/config/keyStore.html)
y las [claves LTPA](https://openliberty.io/docs/latest/reference/config/ltpa.html)
con esas variables. Su ausencia provoca los errores `CWWKS9582E` y `CWWKS4118E`.
Liberty genera el certificado local para HTTPS; en desarrollo, el navegador
puede advertir que es un certificado autofirmado.

Si utiliza la base existente del contenedor `GalenoSV-db`, inicie Docker Desktop
y el contenedor antes de Liberty:

```powershell
docker desktop start
docker start GalenoSV-db
```

`GALENOSV_DB_PORT` en `server.env` debe coincidir con el puerto publicado por
el contenedor (por ejemplo, `5433` para `5433:5432`). Un contenedor detenido
provoca `Connection refused` y respuestas `503` al consultar la API.

## API REST

La API se publica bajo la base `http://localhost:9080/galenoSV/api/v1`, sin
autenticación BASIC ni redirección obligatoria a HTTPS. Los endpoints REST usan
`@PermitAll`, incluidas las operaciones heredadas de los catálogos. La validación
del número de documento corresponde a la lógica de negocio. El frontend de
`http://localhost:3000` tiene acceso CORS y no debe enviar credenciales BASIC
ni usar `credentials: 'include'`.

Los errores usan este formato JSON:

```json
{
  "timestamp": "2026-11-07T12:00:00Z",
  "status_code": 404,
  "error_code": "NOT_FOUND",
  "message": "No existe el recurso solicitado.",
  "details": {}
}
```

La paginación se controla con `first` (desplazamiento, por defecto `0`), `max`
(tamaño de página, por defecto `20`, máximo `100`) y el filtro opcional `q`. La
respuesta tiene la forma:

```json
{
  "items": [],
  "total": 0,
  "first": 0,
  "max": 20
}
```

Ejemplos con `/clinicas`:

```bash
# GET: listado paginado
curl \
  'http://localhost:9080/galenoSV/api/v1/clinicas?first=0&max=20'

# GET: un recurso por id (la respuesta incluye el encabezado ETag)
curl -i \
  'http://localhost:9080/galenoSV/api/v1/clinicas/00000000-0000-0000-0000-000000000000'

# POST: crear un recurso
curl -X POST \
  -H 'Content-Type: application/json' \
  -d '{"nombre":"Clínica Central","activo":true,"tipo":"PRIVADA","comentarios":"Sede principal"}' \
  'http://localhost:9080/galenoSV/api/v1/clinicas'

# PUT: actualizar usando el ETag obtenido con GET
curl -X PUT \
  -H 'Content-Type: application/json' \
  -H 'If-Match: W/"<etag-obtenido-en-el-GET>"' \
  -d '{"nombre":"Clínica Central Actualizada","activo":true,"tipo":"PRIVADA","comentarios":"Sede principal"}' \
  'http://localhost:9080/galenoSV/api/v1/clinicas/00000000-0000-0000-0000-000000000000'

# DELETE: elimina el recurso (borrado lógico en clínicas)
curl -i -X DELETE \
  'http://localhost:9080/galenoSV/api/v1/clinicas/00000000-0000-0000-0000-000000000000'
```

`PUT` exige el encabezado `If-Match`;
```bash
export GALENOSV_DB_PASSWORD=<su-contraseña>
docker compose up -d
```

**3. Compilar y ejecutar**

```bash
mvn clean package
mvn liberty:dev
```

**4. Abrir la aplicación**

| Protocolo | URL |
| --- | --- |
| HTTP | <http://localhost:9080/galenoSV/> |
| HTTPS | <https://localhost:9443/galenoSV/> |

El artefacto generado es `target/GalenoSV-1.0-SNAPSHOT.war`.

## Base de datos

- La unidad de persistencia es `GalenoSV_PU` y usa la fuente JTA `jdbc/galenoSV`.
- JPA está configurado con `schema-generation.database.action=none`: **la aplicación no crea ni
  modifica el esquema**.
- El esquema base se encuentra en `src/test/resources/db/clinica_ppi115_2026_08_20.sql`.

Scripts de evolución del esquema, que deben ejecutarse **manualmente y en orden**:

| Script | Propósito |
| --- | --- |
| `src/main/resources/db/migration/V1__esquema_inicial.sql` | Esquema inicial (20 tablas). |
| `src/main/db/migration/V2__validaciones_integridad_y_version.sql` | Columna `version`, restricciones `NOT NULL` y validaciones de integridad (secuencias de pasos sin ciclos ni cruces entre procedimientos). |
| `src/main/db/migration/V3__fecha_nacimiento_sin_hora.sql` | `persona.fecha_nacimiento` pasa a tipo `date`. |

> **Antes de migrar:** haga un respaldo, detenga la aplicación y ejecute
> `scripts/auditar-datos.sql`, que es de solo lectura y reporta conteos de registros
> que incumplirían las nuevas restricciones.

## Pruebas y calidad

```bash
mvn test            # Pruebas unitarias
mvn clean verify    # Pruebas + empaquetado + reporte de cobertura
```

La suite cubre las tres capas del sistema:

- **Entidades:** validaciones, versionado, fechas de creación y reflexión sobre el modelo.
- **Control:** DAOs y servicios de asignación.
- **Boundary:** modelos Faces, convertidores, modelo de datos diferido y sesión.
- **Integración:** pruebas contra PostgreSQL con Testcontainers (requieren Docker).

El reporte de cobertura de **JaCoCo** se genera en `target/site/jacoco/`.

## Integración continua

El flujo `.github/workflows/validacion.yml` se ejecuta en cada *push* y *pull request*:
configura Temurin JDK 21, ejecuta la verificación con Maven y publica como artefactos los
informes de pruebas y de cobertura.

## Licencia y créditos

Proyecto académico desarrollado para el curso **PPI-115 (2026)** de la
Facultad Multidisciplinaria de Occidente, Universidad de El Salvador.

Desarrollado por **Melvin Monroy**, **Josue Escobar** y **Benito Palma**.

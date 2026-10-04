# GalenoSV-PPI115-GT02

Sistema de gestión clínica con Jakarta EE 11, Faces, PrimeFaces, EclipseLink,
PostgreSQL y Open Liberty.

## Requisitos

- Java 21 y Maven.
- Una base PostgreSQL con el esquema original `clinica_ppi115_2026_08_20.sql`.
- Open Liberty compatible con Jakarta EE 11. El plugin Maven administra una
  instalación en `target/liberty/wlp`; si ya tiene Liberty, configure `WLP_INSTALL_DIR`.

**No ejecute migraciones sobre la base del profesor.** JPA tiene
`schema-generation.database.action=none`. El WAR no contiene Flyway ni migraciones.
Los SQL antiguos se conservan únicamente como referencia en `docs/historico-migraciones/`.

## Ejecución local

```bash
mvn clean verify
mvn -DskipTests liberty:dev
```

El WAR es `target/GalenoSV.war`. Las pantallas protegidas están en
`https://localhost:9443/galenoSV/`; HTTP redirige a HTTPS según `web.xml`.
No se necesita Docker ni un archivo `.env` o `server.env` para el arranque local
con los valores de demostración. La base existente debe aceptar estos datos de conexión:

| Variable | Valor local predeterminado |
| --- | --- |
| `GALENOSV_DB_HOST` | `localhost` |
| `GALENOSV_DB_PORT` | `5432` |
| `GALENOSV_DB_NAME` | `galenoSV` |
| `GALENOSV_DB_USER` | `admin` |
| `GALENOSV_DB_PASSWORD` | `admin` |
| `GALENO_ADMIN_PASSWORD` | `admin` |
| `GALENO_CLINICO_PASSWORD` | `clinico` |
| `keystore_password` | `galeno-local-keystore` |
| `GALENOSV_WAR` | `GalenoSV.war` |

Los usuarios de demostración son `admin` (ADMINISTRADOR) y `clinico`
(PERSONAL_CLINICO). Las variables de entorno reemplazan estos valores.
Para otra instalación, configure las credenciales que **ya tiene** su base;
no es necesario modificar sus usuarios, tablas o datos.
Puede usar opcionalmente `src/main/liberty/config/server.env`, excluido de Git.
`compose.yaml` es una alternativa opcional para una base propia de desarrollo.

## Pruebas

```bash
mvn -DskipTests compile
mvn test
mvn clean verify
```

`verify` empaqueta el WAR y exige cobertura de líneas del **65 %**.
Las pruebas unitarias no se conectan a la base de trabajo.

La integración con Docker crea un PostgreSQL temporal, importa una copia literal
del SQL del profesor y ejecuta JPA con generación de esquema desactivada:

```bash
mvn -Pintegration verify
```

Docker solo es necesario para este perfil por defecto. También puede ejecutar el
perfil contra una instancia temporal propia en loopback, con una base **nueva**
llamada `galenosv_test_<numero>` y el usuario de pruebas `galenosv_test`:

```bash
mvn -Pintegration verify -Dgalenosv.test.jdbc.url=jdbc:postgresql://127.0.0.1:55432/galenosv_test_20261002
```

El perfil carga el SQL automáticamente y falla si el esquema ya existe. Use una
base de pruebas nueva en cada ejecución. La URL de integración local rechaza los
nombres normales de bases de trabajo. Puede configurar `galenosv.test.jdbc.user`
y `galenosv.test.jdbc.password` para esa instancia temporal.

Consulte [las correcciones y el guion manual](docs/analisis-y-correcciones.md)
y [el detalle de archivos](docs/archivos-corregidos.md).

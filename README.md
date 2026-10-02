# GalenoSV-PPI115-GT02

Sistema de gestión clínica desarrollado con Jakarta EE, Faces, JPA, EclipseLink, PostgreSQL y Open Liberty.

## Requisitos

- Java 21.
- Maven.
- Docker.
- PostgreSQL 15 o compatible.
- Open Liberty 26.0.0.8 o compatible con Jakarta EE 11.

## Base de datos local

El proyecto utiliza la base de datos:

```text
Base de datos: galenoSV
Usuario: admin
Puerto: 5432
```

## Variables de entorno

Las credenciales no se guardan en Git. Debe existir localmente el archivo:

```text
src/main/liberty/config/server.env
```

Debe contener variables con esta estructura:

```text
GALENO_ADMIN_PASSWORD=contraseña_local
GALENO_CLINICO_PASSWORD=contraseña_local
GALENOSV_DB_PASSWORD=contraseña_de_postgresql
keystore_password=contraseña_del_keystore
```

El archivo debe tener permisos restringidos:

```bash
chmod 600 src/main/liberty/config/server.env
```

## Ejecución local

Copie `.env.example` a `.env`, asigne una contraseña local y cree PostgreSQL:

```bash
docker compose up -d --wait db
```

Para una base nueva, configure `FLYWAY_URL` (por ejemplo
`jdbc:postgresql://localhost:5432/galenoSV`), `FLYWAY_USER` y `FLYWAY_PASSWORD`
en su entorno y aplique el esquema:

```bash
mvn flyway:migrate
```

Para una base existente, consulte primero [la guía de correcciones](docs/analisis-y-correcciones.md).
El esquema inferido debe contrastarse con su DDL; no habilite un baseline automático.
Antes de iniciar la aplicación debe estar aplicada la versión 3 del esquema,
que incluye las columnas de concurrencia y la fecha de nacimiento sin hora.

Después, desde la raíz del proyecto, iniciar Open Liberty:

```bash
WLP_INSTALL_DIR=/opt/openliberty-26.0.0.8/wlp mvn -DskipTests liberty:dev
```

En PowerShell, asigne `$env:WLP_INSTALL_DIR` a su instalación de Liberty y ejecute
`mvn -DskipTests liberty:dev`. Los datos de conexión admiten
`GALENOSV_DB_HOST`, `GALENOSV_DB_PORT`, `GALENOSV_DB_NAME` y `GALENOSV_DB_USER`
en `server.env`; use la misma base y contraseña que en Compose.
El WAR generado es `target/GalenoSV.war`.

La aplicación queda disponible en:

```text
http://localhost:9080/galenoSV/
https://localhost:9443/galenoSV/
```

Las páginas ubicadas bajo `/paginas/*` requieren autenticación. Los usuarios locales configurados son:

```text
admin    → rol ADMINISTRADOR
clinico  → rol PERSONAL_CLINICO
```

Las contraseñas se obtienen únicamente del archivo local `server.env`.

## Seguridad

- Las credenciales se leen mediante variables de entorno.
- `server.env` está excluido de Git.
- La aplicación utiliza `PROJECT_STAGE=Production`.
- Las páginas protegidas requieren autenticación BASIC.
- El acceso protegido exige HTTPS.
- Los mensajes genéricos no exponen detalles internos de PostgreSQL.
- El registro de EclipseLink no muestra parámetros ni SQL sensible.

## Verificación

Compilar el proyecto:

```bash
mvn -DskipTests compile
```

Ejecutar las pruebas:

```bash
mvn -Djacoco.skip=true test
```

Verificación completa del WAR y umbral mínimo de cobertura de líneas (65 %):

```bash
mvn verify
```

Integración con PostgreSQL 15 real en un contenedor temporal independiente
(requiere Docker en ejecución):

```bash
mvn -Pintegration verify
```

Este perfil falla si Docker no está disponible; no omite silenciosamente las pruebas.
La integración continua ejecuta ese perfil y conserva los informes de pruebas y cobertura.

Validar el estado del repositorio:

```bash
git diff --check
git status --short
```

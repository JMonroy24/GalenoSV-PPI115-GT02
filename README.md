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

Primero debe estar iniciado el contenedor de PostgreSQL:

```bash
docker start galenosv-grupo-db
```

Después, desde la raíz del proyecto, iniciar Open Liberty:

```bash
WLP_INSTALL_DIR=/opt/openliberty-26.0.0.8/wlp mvn -DskipTests liberty:dev
```

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

Validar el estado del repositorio:

```bash
git diff --check
git status --short
```

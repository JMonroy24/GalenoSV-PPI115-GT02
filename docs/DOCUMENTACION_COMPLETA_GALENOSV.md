# DOCUMENTACIÓN TÉCNICA Y ARQUITECTÓNICA DEL SISTEMA GALENOSV

**Proyecto:** GalenoSV — Sistema de Gestión Clínica  
**Institución:** Universidad de El Salvador (UES) — Facultad Multidisciplinaria de Occidente  
**Asignatura:** Programación de Plataformas de Información (PPI-115)  
**Versión:** 1.0-SNAPSHOT  
**Fecha de generación:** Octubre 2026  

---

## TABLA DE CONTENIDOS

1. [Resumen Ejecutivo y Propósito del Sistema](#1-resumen-ejecutivo-y-propósito-del-sistema)
2. [Arquitectura General y Stack Tecnológico](#2-arquitectura-general-y-stack-tecnológico)
   - 2.1 Patrón Arquitectónico Boundary-Control-Entity (BCE)
   - 2.2 Tecnologías y Versiones Utilizadas
   - 2.3 Flujo Integral de Información
3. [Estructura del Proyecto y Organización de Paquetes](#3-estructura-del-proyecto-y-organización-de-paquetes)
4. [Módulo de Persistencia y Base de Datos PostgreSQL](#4-módulo-de-persistencia-y-base-de-datos-postgresql)
   - 4.1 Esquema Relacional de la Base de Datos
   - 4.2 Soporte Nativo de UUID en EclipseLink y PostgreSQL
   - 4.3 Control de Concurrencia y Auditoría
5. [Capa de Entidades JPA (Modelo de Dominio)](#5-capa-de-entidades-jpa-modelo-de-dominio)
   - 5.1 Catálogos y Entidades Maestras
   - 5.2 Personas, Identificaciones y Medios de Contacto
   - 5.3 Procedimientos Clínicos y Grafos de Secuencia
   - 5.4 Módulo Transaccional: Consultas, Pasos, Órdenes y Resultados
6. [Capa de Control y Lógica de Negocio (Control)](#6-capa-de-control-y-lógica-de-negocio-control)
   - 6.1 Contrato Genérico: `DAOInterface`
   - 6.2 Motor de Persistencia Dinámico: `DefaultDAO`
   - 6.3 Servicio Transaccional de Dominio: `AsignacionService`
   - 6.4 Validaciones Centralizadas: `ValidadorComun` y Bean Validation
   - 6.5 Catálogo Completo de DAOs Especializados
7. [Capa de Frontera / Presentación (Boundary JSF)](#7-capa-de-frontera--presentación-boundary-jsf)
   - 7.1 Framework de Backing Beans: `Model` y `ModelTransaccional`
   - 7.2 Paginación Lazy Universal: `GenericLazyDataModel`
   - 7.3 Gestión del Contexto de Sesión: `SesionBean`
   - 7.4 Internacionalización: `LocaleBean`
   - 7.5 Convertidor Genérico de Entidades: `EntityConverter`
   - 7.6 Detalle de Backing Beans Específicos
8. [Capa de Vistas y Componentes Compuestos Facelets](#8-capa-de-vistas-y-componentes-compuestos-facelets)
   - 8.1 Plantilla Maestra y Menú
   - 8.2 Componentes Compuestos CRUD
   - 8.3 Páginas de Usuario y Flujos de Navegación
9. [Reglas de Negocio Críticas y Máquinas de Estados](#9-reglas-de-negocio-críticas-y-máquinas-de-estados)
10. [Estrategia de Pruebas y Aseguramiento de Calidad](#10-estrategia-de-pruebas-y-aseguramiento-de-calidad)
11. [Guía de Configuración, Compilación y Despliegue](#11-guía-de-configuración-compilación-y-despliegue)

---

## 1. RESUMEN EJECUTIVO Y PROPÓSITO DEL SISTEMA

**GalenoSV** es un sistema empresarial de gestión y atención clínica diseñado para resolver las necesidades operativas de policlínicas y centros asistenciales de salud. El sistema centraliza la administración de:

- **Infraestructura y Catálogos:** Clínicas de atención, roles institucionales, catálogo de exámenes de laboratorio/diagnóstico y procedimientos médicos parametrizables.
- **Expediente de Personas:** Registro demográfico de pacientes y personal de salud, junto con sus documentos legales (validados sintácticamente mediante expresiones regulares) y canales de contacto.
- **Workflow de Procedimientos Médicos:** Modelado de procedimientos mediante **Grafos Dirigidos Acíclicos (DAG)** donde cada paso clínico posee roles responsables asignados, exámenes requeridos y transiciones secuenciales validadas algorítmicamente para evitar ciclos o bucles infinitos.
- **Atención Transaccional:** Registro de consultas por paciente en la clínica activa de trabajo, ejecución de procedimientos clínicos paso a paso con máquina de estados finitos (`PENDIENTE` -> `EN_CURSO` -> `COMPLETADO`), prescripción de órdenes de exámenes médicos y registro de resultados con diagnósticos y atestados digitales.

---

## 2. ARQUITECTURA GENERAL Y STACK TECNOLÓGICO

### 2.1 Patrón Arquitectónico Boundary-Control-Entity (BCE)

La arquitectura de GalenoSV implementa estrictamente el patrón **Boundary-Control-Entity (BCE)** de Ivar Jacobson (estándar en aplicaciones empresariales Java EE / Jakarta EE):

```
+-----------------------------------------------------------------------------------+
|                            BOUNDARY (Presentación)                                |
|  - Vistas Facelets (.xhtml) + Componentes Compuestos CRUD                         |
|  - PrimeFaces 15 (Tablas, Árboles, DatePickers, AutoComplete, Toolbars)            |
|  - Backing Beans JSF: @Named, @ViewScoped, @SessionScoped                         |
|  - Model<T, ID> & ModelTransaccional<T, ID>                                       |
+----------------------------------------+------------------------------------------+
                                         | Inyección CDI (@Inject)
                                         v
+-----------------------------------------------------------------------------------+
|                             CONTROL (Lógica de Negocio)                           |
|  - AsignacionService (@ApplicationScoped, @Transactional)                         |
|  - DAOs Específicos (ConsultaDAO, ProcedimientoPasoDAO, PersonaRolDAO, etc.)      |
|  - DefaultDAO<T, ID> (Criteria API dinámica, ordenamiento, filtrado, paginación)  |
|  - ValidadorComun & Bean Validation (@RangoFechasValido, @RegexValido)            |
+----------------------------------------+------------------------------------------+
                                         | JPA EntityManager / CriteriaBuilder
                                         v
+-----------------------------------------------------------------------------------+
|                             ENTITY (Datos y Persistencia)                         |
|  - Entidades JPA 3.2 (@Entity, @Table, @ManyToOne, @OneToMany)                   |
|  - Contratos: IdentificableEntity, Auditable, EntidadVersionada                   |
|  - Adaptadores EclipseLink: PostgresUuidPlatform, UuidFieldSessionCustomizer       |
|  - Base de Datos: PostgreSQL 15+ nativa (UUIDs, Foreign Keys, Triggers)           |
+-----------------------------------------------------------------------------------+
```

### 2.2 Tecnologías y Versiones Utilizadas

| Componente | Tecnología | Versión | Propósito |
|---|---|---|---|
| **Lenguaje** | Java SE | OpenJDK 21 LTS | Código fuente moderno, Switch Expressions, Records, Sequenced Collections. |
| **Estándar Empresarial** | Jakarta EE | 11.0.0-M1 | Especificaciones estándar de Faces, CDI, JPA, Transactions, Bean Validation. |
| **Servidor de Aplicaciones** | Open Liberty | 24.x+ | Runtime de alto rendimiento para microservicios y aplicaciones Jakarta EE 11. |
| **Capa Web / UI** | Jakarta Faces & Facelets | 4.1.2 | Motor de componentes de servidor y ciclo de vida HTTP/JSF. |
| **Librería de Componentes** | PrimeFaces (Jakarta) | 15.0.17 | Componentes ricos: `p:dataTable`, `p:treeTable`, `p:autoComplete`, `p:datePicker`. |
| **ORM / Persistencia** | EclipseLink | 5.0.0 | Implementación JPA de referencia en Jakarta EE. |
| **Driver de Base de Datos** | PostgreSQL JDBC Driver | 42.7.2 | Conectividad con la base relacional PostgreSQL. |
| **Base de Datos** | PostgreSQL | 15.x / 16.x | Almacenamiento relacional con tipos nativos `uuid` y marcas temporales. |
| **Validación de Datos** | Hibernate Validator | 9.0.1.Final | Motor de Bean Validation 3.1. |
| **Pruebas Unitarias** | JUnit Jupiter & Mockito | 5.10.2 / 5.23.0 | Framework de pruebas y mocks para CDI y JPA. |
| **Pruebas de Integración** | Testcontainers PostgreSQL | 1.21.3 | Contenedores efímeros Docker para probar consultas reales en PostgreSQL. |
| **Métricas de Calidad** | JaCoCo Maven Plugin | 0.8.11 | Medición estricta de cobertura de código (mínimo exigido 65%). |

### 2.3 Flujo Integral de Información

1. **Petición del Usuario:** El usuario interactúa con la vista XHTML enviando un evento Ajax mediante PrimeFaces.
2. **Conversión y Validación JSF:** `EntityConverter` traduce los parámetros de selección (`UUID`) a objetos JPA validados contra el metamodelo. Las anotaciones de Bean Validation (`@NotNull`, `@Size`, `@RangoFechasValido`) ejecutan validaciones sintácticas.
3. **Invocación del Backing Bean (Boundary):** El método del `Model` invocado ejecuta validaciones contextuales de negocio (`validarNegocio`).
4. **Capa de Control / Transaccionalidad:** Si la operación involucra dependencias complejas (ej. asignaciones de pasos, tipos o consultas), se delega a `AsignacionService` o al `DAO` correspondiente con `@Transactional`.
5. **Capa de Persistencia (DefaultDAO):** Construye la consulta mediante JPA Criteria API asegurando paginación estricta en SQL (`setFirstResult`, `setMaxResults`), orden determinista con ID como desempate y prevención de SQL Injection.
6. **Manejo de Respuestas y Excepciones:** Si la base de datos o el negocio rechazan la operación, `Model.clasificarError` traduce los códigos `SQLState` de PostgreSQL a mensajes claros en español para la UI sin exponer detalles de infraestructura ni datos sensibles.

---

## 3. ESTRUCTURA DEL PROYECTO Y ORGANIZACIÓN DE PAQUETES

```
GalenoSV/
├── pom.xml                                      # Configuración Maven, dependencias y plugins
├── README.md                                    # Guía general de instalación y ejecución
├── docs/                                        # Documentación de análisis y técnica
│   ├── analisis-y-correcciones.md               # Bitácora de incidencias resueltas
│   └── DOCUMENTACION_COMPLETA_GALENOSV.md       # Este documento técnico exhaustivo
├── src/
│   ├── main/
│   │   ├── java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/
│   │   │   ├── boundary/jsf/                    # Controladores de vista JSF (Backing Beans)
│   │   │   │   ├── Model.java                   # Template Method CRUD base
│   │   │   │   ├── ModelTransaccional.java      # Extensión para paginación lazy
│   │   │   │   ├── GenericLazyDataModel.java    # Adaptador PrimeFaces LazyDataModel
│   │   │   │   ├── EntityConverter.java         # Convertidor universal Clase:UUID
│   │   │   │   ├── Estado.java                  # Enum: NINGUNO, CREAR, MODIFICAR, ELIMINAR
│   │   │   │   ├── EstadoPaso.java              # FSM: PENDIENTE, EN_CURSO, COMPLETADO
│   │   │   │   ├── SesionBean.java              # Sesión del usuario: clínica y rol activos
│   │   │   │   ├── LocaleBean.java              # Bean de internacionalización (i18n)
│   │   │   │   └── *Model.java                  # 18 backing beans especializados
│   │   │   ├── control/                         # Servicios de negocio y DAOs
│   │   │   │   ├── DAOInterface.java            # Contrato genérico CRUD y filtros
│   │   │   │   ├── DefaultDAO.java              # Implementación base con Criteria API
│   │   │   │   ├── AsignacionService.java       # Servicio transaccional y grafos (DFS)
│   │   │   │   ├── ValidadorComun.java          # Reglas reutilizables de validación
│   │   │   │   ├── ValidacionNegocioException.java # Excepción controlada de negocio
│   │   │   │   ├── FiltroConsulta.java          # Record para filtros de columna
│   │   │   │   ├── OrdenConsulta.java           # Record para ordenamiento de columnas
│   │   │   │   └── *DAO.java                    # 20 DAOs especializados
│   │   │   ├── entities/                        # Entidades JPA
│   │   │   │   ├── IdentificableEntity.java     # Contrato para idKey (rowKey PrimeFaces)
│   │   │   │   ├── Auditable.java               # Interfaz para auditoría de creación/edición
│   │   │   │   ├── EntidadVersionada.java       # Superclase mapeada con @Version
│   │   │   │   ├── UUIDConverter.java           # Convertidor JPA para UUID
│   │   │   │   ├── validation/                  # Validadores de Bean Validation personalizados
│   │   │   │   │   ├── PeriodoFechas.java       # Interfaz de rango de fechas
│   │   │   │   │   ├── RangoFechasValido.java   # Anotación @RangoFechasValido
│   │   │   │   │   ├── RangoFechasValidoValidator.java # Validador fechaFin >= fechaInicio
│   │   │   │   │   ├── RegexValido.java         # Anotación @RegexValido
│   │   │   │   │   └── RegexValidoValidator.java # Validador de expresiones regulares
│   │   │   │   └── *.java                       # 19 entidades JPA del modelo relacional
│   │   │   └── persistence/                     # Adaptadores de infraestructura EclipseLink
│   │   │       ├── PostgresUuidPlatform.java    # Plataforma PostgreSQL con tipos nativos
│   │   │       └── UuidFieldSessionCustomizer.java # Customizer para enlaces de UUID nulos
│   │   ├── resources/
│   │   │   ├── META-INF/persistence.xml         # Unidad de persistencia GalenoSV_PU
│   │   │   ├── messages.properties              # Textos e internacionalización en español
│   │   │   ├── messages_en.properties           # Textos en inglés
│   │   │   ├── ValidationMessages.properties   # Mensajes de Bean Validation en español
│   │   │   └── db/migration/V1__esquema_inicial.sql # DDL de referencia PostgreSQL
│   │   └── webapp/                              # Capa de presentación Facelets
│   │       ├── WEB-INF/
│   │       │   ├── web.xml                      # Configuración de servlet y parámetros JSF
│   │       │   ├── faces-config.xml             # Recursos de Faces e i18n
│   │       │   ├── beans.xml                    # Habilitación de CDI 4.1
│   │       │   └── templates/
│   │       │       ├── template.xhtml           # Plantilla base (Header, Menú, Growl, Footer)
│   │       │       └── menu.xhtml               # Barra de navegación principal
│   │       ├── resources/
│   │       │   ├── crud/                        # Componentes compuestos Facelets
│   │       │   │   ├── botonesTop.xhtml         # Botones superiores de toolbar
│   │       │   │   ├── botonesFormulario.xhtml  # Botones Guardar/Cancelar
│   │       │   │   ├── crudCatalogo.xhtml       # Componente CRUD para listas en memoria
│   │       │   │   ├── crudTransaccional.xhtml  # Componente CRUD con Lazy Data Model
│   │       │   │   └── pasosProcedimiento.xhtml # Componente árbol de pasos de procedimiento
│   │       │   ├── css/styles.css               # Estilos globales y diseño responsivo
│   │       │   └── images/galenosv-logo.png     # Logotipo del sistema
│   │       ├── index.xhtml                      # Pantalla de bienvenida
│   │       └── paginas/                         # Vistas de la aplicación
│   │           ├── cambiarRol.xhtml             # Selector de contexto de sesión
│   │           ├── catalogo/                    # Vistas de catálogos
│   │           ├── persona/                     # Vistas de personas, documentos, medios
│   │           └── clinica/                     # Vistas transaccionales de atención
│   └── test/                                    # Pruebas unitarias e integración (JUnit 5)
```

---

## 4. MÓDULO DE PERSISTENCIA Y BASE DE DATOS POSTGRESQL

### 4.1 Esquema Relacional de la Base de Datos

La base de datos utiliza identificadores de clave primaria del tipo **UUID nativo** de PostgreSQL en todas las tablas para permitir generación distribuida de identificadores sin colisiones.

```
       +------------------+             +--------------------+
       |     clinica      |             |        rol         |
       +------------------+             +--------------------+
       | PK id_clinica    |             | PK id_rol          |
       |    nombre        |             |    nombre          |
       |    activo        |             |    activo          |
       +--------+---------+             +---------+----------+
                |                                 |
                +----------------+  +-------------+
                                 |  |
                                 v  v
                       +----------------------+
                       |     persona_rol      |
                       +----------------------+
                       | PK id_persona_rol    |
                       | FK id_clinica        |
                       | FK id_rol            |
                       | FK id_persona        |
                       +----------+-----------+
                                  |
            +---------------------+---------------------+
            |                                           |
            v                                           v
+-----------------------+                   +-----------------------+
|        persona        |                   |       consulta        |
+-----------------------+                   +-----------------------+
| PK id_persona         |                   | PK id_consulta        |
|    nombres            |                   | FK id_persona_rol (pac)|
|    apellidos          |                   |    fecha_inicio       |
|    fecha_nacimiento   |                   |    fecha_fin          |
+-----+-----------+-----+                   |    referencia_externa |
      |           |                         +-----------+-----------+
      v           v                                     |
+-----------+ +---------------+                         v
| documento | | medio_contacto|             +-----------------------+
+-----------+ +---------------+             | consulta_procedimiento|
| FK tipo   | | FK tipo_medio |             +-----------------------+
|    valor  | |    valor      |             | PK id_c_procedimiento |
+-----------+ +---------------+             | FK id_consulta        |
                                            | FK id_procedimiento   |
                                            +-----------+-----------+
                                                        |
                                                        v
                                            +---------------------------+
                                            | consulta_procedimiento_paso|
                                            +---------------------------+
                                            | PK id_c_proc_paso         |
                                            | FK id_c_procedimiento     |
                                            | FK id_persona_rol (resp)  |
                                            |    estado (FSM)           |
                                            +-------------+-------------+
                                                          |
                                                          v
                                            +---------------------------+
                                            |       orden_examen        |
                                            +---------------------------+
                                            | PK id_orden_examen        |
                                            | FK id_c_proc_paso         |
                                            |    indicaciones           |
                                            +-------------+-------------+
                                                          |
                                                          v
                                            +---------------------------+
                                            |      examen_resultado     |
                                            +---------------------------+
                                            | PK id_examen_resultado    |
                                            | FK id_orden_examen        |
                                            |    resultado              |
                                            |    interpretacion         |
                                            |    ruta_atestado          |
                                            +---------------------------+
```

### 4.2 Soporte Nativo de UUID en EclipseLink y PostgreSQL

#### El Desafío Técnico:
En JPA con EclipseLink sobre PostgreSQL, cuando un campo `UUID` tiene valor `null` en un INSERT o UPDATE (por ejemplo en una clave foránea opcional), el driver JDBC envía `Types.VARCHAR` por defecto. El motor PostgreSQL rechaza este enlace con el error `PSQLException: ERROR: column is of type uuid but expression is of type character varying` (EclipseLink Bug #2717).

#### La Solución Implementada:
El proyecto implementa una solución de infraestructura en dos niveles:

1. **`PostgresUuidPlatform` (`persistence/PostgresUuidPlatform.java`):**
   Sobrescribe la plataforma estándar `PostgreSQLPlatform` de EclipseLink:
   ```java
   @Override
   public int getJDBCTypeForSetNull(DatabaseField field) {
       if (isUuidField(field)) return Types.OTHER;
       return super.getJDBCTypeForSetNull(field);
   }
   @Override
   public int getJDBCType(Class<?> javaType) {
       if (UUID.class.equals(javaType)) return Types.OTHER;
       return super.getJDBCType(javaType);
   }
   ```
2. **`UuidFieldSessionCustomizer` (`persistence/UuidFieldSessionCustomizer.java`):**
   Intercepta la inicialización de la sesión de EclipseLink y escanea todos los descriptores JPA y mappings (`ObjectReferenceMapping`, `OneToManyMapping`, `OneToOneMapping`). Identifica cada campo cuyo tipo Java sea `java.util.UUID` o contenga "uuid" y le asigna explícitamente `field.setSqlType(Types.OTHER)` y `field.setColumnDefinition("uuid")`.

### 4.3 Control de Concurrencia y Auditoría

- **`EntidadVersionada` (`entities/EntidadVersionada.java`):**
  MappedSuperclass que define la columna `@Version private long version`. Permite que JPA active el bloqueo optimista (*Optimistic Locking*), arrojando `OptimisticLockException` si dos usuarios intentan actualizar el mismo registro concurrentemente.
- **`Auditable` (`entities/Auditable.java`):**
  Interfaz que define métodos para rastrear quién y cuándo modificó los datos (`usuarioCreacion`, `usuarioModificacion`, `fechaCreacion`).
- **`@PrePersist` en Entidades:** Asigna automáticamente la fecha de creación del servidor (`new Date()`) si no fue provista externamente, respetando datos históricos si ya venían definidos.

---

## 5. CAPA DE ENTIDADES JPA (MODELO DE DOMINIO)

Todas las entidades implementan `IdentificableEntity`, lo que garantiza que expongan el método `getIdKey()` que retorna la clave primaria en formato String, garantizando un `rowKey` determinista y estable en las tablas de PrimeFaces.

### 5.1 Catálogos y Entidades Maestras

#### `Clinica`
- **Tabla:** `clinica`
- **Atributos:** `idClinica` (UUID, PK), `nombre` (varchar 255), `tipo` (varchar 20), `activo` (boolean), `comentarios` (varchar 2000).
- **Propósito:** Representa los centros asistenciales o sedes. Al crear una clínica, `activo` se inicializa en `true`.

#### `Rol`
- **Tabla:** `rol`
- **Atributos:** `idRol` (UUID, PK), `nombre` (varchar 155), `activo` (boolean), `observaciones` (varchar 2000).
- **Propósito:** Define los roles en el sistema (ej. "Paciente", "Doctor", "Enfermero"). El nombre debe ser único.

#### `TipoDocumento`
- **Tabla:** `tipo_documento`
- **Atributos:** `idTipoDocumento` (UUID, PK), `nombre` (varchar 155), `expresionRegular` (varchar 500), `indicaciones` (varchar 2000), `activo` (boolean).
- **Propósito:** Parametriza documentos de identidad (ej. DUI, Pasaporte, Carnet de Minoridad). Permite que la validación de formato sea configurable dinámicamente mediante Regex.

#### `TipoMedioContacto`
- **Tabla:** `tipo_medio_contacto`
- **Atributos:** `idTipoMedioContacto` (UUID, PK), `nombre` (varchar 155), `expresionRegular` (varchar 500), `indicaciones` (varchar 2000), `activo` (boolean).
- **Propósito:** Parametriza vías de contacto (ej. "Teléfono Móvil", "Correo Electrónico", "WhatsApp") con expresiones regulares de formato.

#### `TipoExamen`
- **Tabla:** `tipo_examen`
- **Atributos:** `idTipoExamen` (UUID, PK), `nombre` (varchar 255), `activo` (boolean), `observaciones` (varchar 2000).
- **Propósito:** Categorías o especialidades de exámenes (ej. "Hematología", "Bioquímica", "Radiología").

#### `Examen`
- **Tabla:** `examen`
- **Atributos:** `idExamen` (UUID, PK), `nombre` (varchar 255), `activo` (boolean), `observaciones` (varchar 2000).
- **Relaciones:** `examenTipoExamenList` (`@OneToMany`).
- **Propósito:** Exámenes médicos específicos (ej. "Hemograma Completo", "Glucosa en Sangre").

#### `ExamenTipoExamen`
- **Tabla:** `examen_tipo_examen`
- **Atributos:** `idExamenTipoExamen` (UUID, PK), `fechaCreacion` (Date), `observaciones` (varchar 2000).
- **Relaciones:** `idExamen` (`@ManyToOne` -> `Examen`), `idTipoExamen` (`@ManyToOne` -> `TipoExamen`).
- **Propósito:** Tabla de asociación que clasifica un examen dentro de uno o varios tipos de examen.

---

### 5.2 Personas, Identificaciones y Medios de Contacto

#### `Persona`
- **Tabla:** `persona`
- **Atributos:** `idPersona` (UUID, PK), `nombres` (varchar 255), `apellidos` (varchar 255), `fechaNacimiento` (Date), `fechaCreacion` (Date).
- **Relaciones:** `documentoList` (`@OneToMany`), `medioContactoList` (`@OneToMany`), `personaRolList` (`@OneToMany`).
- **Propósito:** Registro maestro de individuos físicos en el sistema.

#### `PersonaRol`
- **Tabla:** `persona_rol`
- **Atributos:** `idPersonaRol` (UUID, PK), `fechaCreacion` (Date).
- **Relaciones:** `idPersona` (`@ManyToOne`), `idRol` (`@ManyToOne`), `idClinica` (`@ManyToOne`).
- **Propósito:** Asocia a una Persona con un Rol específico en una Clínica determinada. Una persona puede ser "Paciente" en una clínica y "Médico" en otra.

#### `Documento`
- **Tabla:** `documento`
- **Atributos:** `idDocumento` (UUID, PK), `valor` (varchar 50), `rutaFisica` (varchar 500).
- **Relaciones:** `idPersona` (`@ManyToOne`), `idTipoDocumento` (`@ManyToOne`).
- **Propósito:** Almacena el documento formal de la persona (ej. DUI "01234567-8"). Su valor se valida contra la expresión regular del `TipoDocumento`.

#### `MedioContacto`
- **Tabla:** `medio_contacto`
- **Atributos:** `idMedioContacto` (UUID, PK), `valor` (varchar 255), `fechaCreacion` (Date).
- **Relaciones:** `idPersona` (`@ManyToOne`), `idTipoMedioContacto` (`@ManyToOne`).
- **Propósito:** Canales de contacto personal. El formato de `valor` se valida dinámicamente con la regex del `TipoMedioContacto`.

---

### 5.3 Procedimientos Clínicos y Grafos de Secuencia

#### `Procedimiento`
- **Tabla:** `procedimiento`
- **Atributos:** `idProcedimiento` (UUID, PK), `nombre` (varchar 155), `activo` (boolean), `observaciones` (varchar 2000).
- **Relaciones:** `procedimientoPasoList` (`@OneToMany`).
- **Propósito:** Catálogo de intervenciones o protocolos estandarizados (ej. "Control Prenatal", "Curación de Heridas").

#### `ProcedimientoPaso`
- **Tabla:** `procedimiento_paso`
- **Atributos:** `idProcedimientoPaso` (UUID, PK), `nombre` (varchar 155), `indicaFin` (boolean).
- **Relaciones:** `idProcedimiento` (`@ManyToOne`), `idRol` (`@ManyToOne`), `procedimientoPasoExamenList` (`@OneToMany`).
- **Propósito:** Nodo dentro del flujo del procedimiento. Especifica qué rol debe ejecutarlo y si este paso concluye el protocolo (`indicaFin = true`).

#### `ProcedimientoPasoSecuencia`
- **Tabla:** `procedimiento_paso_secuencia`
- **Atributos:** `idProcedimientoPasoSecuencia` (UUID, PK), `idProcedimientoPasoReferencia` (UUID), `tipoSecuencia` (varchar 20, ej. "SIGUIENTE").
- **Relaciones:** `idProcedimientoPaso` (`@ManyToOne` -> Paso Origen).
- **Propósito:** Arista dirigida en el grafo que une un paso de origen con un paso de referencia (destino).

#### `ProcedimientoPasoExamen`
- **Tabla:** `procedimiento_paso_examen`
- **Atributos:** `idProcedimientoPasoExamen` (UUID, PK), `fechaCreacion` (Date), `activo` (boolean), `observaciones` (varchar 2000).
- **Relaciones:** `idProcedimientoPaso` (`@ManyToOne`), `idExamen` (`@ManyToOne`).
- **Propósito:** Exámenes médicos requeridos o sugeridos durante la ejecución de ese paso.

---

### 5.4 Módulo Transaccional: Consultas, Pasos, Órdenes y Resultados

#### `Consulta`
- **Tabla:** `consulta`
- **Atributos:** `idConsulta` (UUID, PK), `fechaInicio` (Date), `fechaFin` (Date), `referenciaExterna` (varchar 255), `observaciones` (varchar 2000).
- **Relaciones:** `idPersonaRol` (`@ManyToOne` -> Paciente), `consultaProcedimientoList` (`@OneToMany`).
- **Validaciones:** `fechaFin` debe ser posterior o igual a `fechaInicio` (`@RangoFechasValido`). El paciente debe pertenecer a la clínica de la sesión actual.

#### `ConsultaProcedimiento`
- **Tabla:** `consulta_procedimiento`
- **Atributos:** `idConsultaProcedimiento` (UUID, PK), `fechaInicio` (Date), `fechaFin` (Date), `observaciones` (varchar 2000).
- **Relaciones:** `idConsulta` (`@ManyToOne`), `idProcedimiento` (`@ManyToOne`), `consultaProcedimientoPasoList` (`@OneToMany`).
- **Propósito:** Instancia de ejecución de un procedimiento durante una consulta.

#### `ConsultaProcedimientoPaso`
- **Tabla:** `consulta_procedimiento_paso`
- **Atributos:** `idConsultaProcedimientoPaso` (UUID, PK), `fechaInicio` (Date), `fechaFin` (Date), `estado` (varchar 20).
- **Relaciones:** `idConsultaProcedimiento` (`@ManyToOne`), `idPersonaRol` (`@ManyToOne` -> Profesional responsable).
- **Máquina de Estados (`EstadoPaso`):**
  - `PENDIENTE`: Paso creado, en espera.
  - `EN_CURSO`: Paso en atención activa.
  - `COMPLETADO`: Paso finalizado.

#### `OrdenExamen`
- **Tabla:** `orden_examen`
- **Atributos:** `idOrdenExamen` (UUID, PK), `fechaCreacion` (Date), `indicaciones` (varchar 2000).
- **Relaciones:** `idConsultaProcedimientoPaso` (`@ManyToOne`), `examenResultadoList` (`@OneToMany`).
- **Propósito:** Prescripción médica emitida dentro de un paso clínico.

#### `ExamenResultado`
- **Tabla:** `examen_resultado`
- **Atributos:** `idExamenResultado` (UUID, PK), `fechaCreacion` (Date), `resultado` (varchar 4000), `interpretacion` (varchar 4000), `rutaAtestado` (varchar 500).
- **Relaciones:** `idOrdenExamen` (`@ManyToOne`).
- **Propósito:** Registro del informe de laboratorio/diagnóstico, dictamen clínico y referencia física/digital al atestado.

---

## 6. CAPA DE CONTROL Y LÓGICA DE NEGOCIO (CONTROL)

### 6.1 Contrato Genérico: `DAOInterface<T, ID>`

Declara las operaciones estándar para desacoplar el acceso a datos:
- `create(T entity)`, `update(T entity)`, `delete(T entity)`, `findById(ID id)`, `findAll()`
- `findRange(int first, int max)`: Paginación simple.
- `count()`: Conteo total.
- `findRange(int first, int max, String filtroGlobal, List<OrdenConsulta> orden, List<FiltroConsulta> filtros)`: Paginación avanzada con ordenamiento y filtros múltiples.
- `count(String filtroGlobal, List<FiltroConsulta> filtros)`: Conteo coincidente con los mismos filtros aplicados en el listado.

### 6.2 Motor de Persistencia Dinámico: `DefaultDAO<T, ID>`

Clase base abstracta de 326 líneas que concentra el comportamiento de persistencia optimizada:

1. **Resolución Automática de ID:** Mediante reflexión sobre la jerarquía de clases localiza el campo anotado con `@Id`.
2. **Paginación Determinista y Desempate:**
   En paginación SQL, si una columna posee valores idénticos (ej. varias personas con el apellido "Hernández"), el orden entre páginas puede variar según el plan de ejecución del motor. `DefaultDAO` agrega automáticamente `cb.asc(root.get(campoId))` como criterio de desempate final si el ID no fue incluido en el orden primario.
3. **Carga Anticipada Segura de Relaciones (`cargarRelaciones`):**
   Para evitar el problema de rendimiento N+1 (*N+1 query problem*), ejecuta `root.fetch(ruta, JoinType.LEFT)`. Solo permite relaciones a uno (`@ManyToOne`, `@OneToOne`) para no desvirtuar el recuento de filas en la paginación de SQL.
4. **Búsqueda Global Literal y Segura:**
   El método `patronBusqueda` normaliza el texto (hasta 100 caracteres) y escapa los metacaracteres de SQL `LIKE` (`\`, `%`, `_`) mediante `escaparLike`:
   ```java
   public static String escaparLike(String valor) {
       return valor.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
   }
   ```
5. **Filtrado Multidimensional de Columnas:**
   Soporta 13 modos de comparación: `CONTAINS`, `NOT_CONTAINS`, `STARTS_WITH`, `ENDS_WITH`, `EXACT`, `EQUALS`, `NOT_EQUALS`, `LESS_THAN`, `LESS_THAN_EQUALS`, `GREATER_THAN`, `GREATER_THAN_EQUALS`, `IN`, `BETWEEN`.
6. **Verificación de Unicidad Universal (`existePorCampo`):**
   Verifica mediante Criteria API si ya existe un valor de texto (insensible a mayúsculas/minúsculas) en la tabla, admitiendo excluir el ID del registro en edición:
   ```java
   public boolean existePorCampo(String campo, String valor, ID excluirId);
   ```

---

### 6.3 Servicio Transaccional de Dominio: `AsignacionService`

Componente `@ApplicationScoped` que encapsula operaciones que involucran consistencia entre múltiples entidades, bloqueos de concurrencia y validaciones de teoría de grafos:

#### A. Detección de Ciclos en Procedimientos (`validarSinCiclo`):
Implementa un algoritmo de **Búsqueda en Profundidad (DFS iterativo)** con estructuras `ArrayDeque` y `Set<UUID>`.
- **Lógica:** Al intentar enlazar el paso $A \rightarrow B$, el algoritmo toma el nodo destino $B$ y explora todas sus ramas dirigidas salientes. Si en algún momento alcanza el nodo origen $A$, significa que el enlace crearía un ciclo ($A \rightarrow B \rightarrow \dots \rightarrow A$). En ese caso arroja inmediatamente `ValidacionNegocioException("La secuencia produciría un ciclo entre los pasos.")`.
- **Bloqueo Pesimista:** Antes de evaluar el grafo, ejecuta un bloqueo pesimista de escritura (`LockModeType.PESSIMISTIC_WRITE`) sobre el `Procedimiento` padre para evitar que dos usuarios concurrentes aprueben simultáneamente aristas opuestas ($A \rightarrow B$ y $B \rightarrow A$).

#### B. Creación Atómica de Procedimiento y Paso Inicial (`crearProcedimientoConPaso`):
Garantiza que al agregar un procedimiento a una consulta, tanto el `ConsultaProcedimiento` como su primer `ConsultaProcedimientoPaso` (en estado `PENDIENTE`) se persistan en la misma transacción (`em.flush()`). Si uno falla, ambos sufren rollback.

#### C. Creación Atómica de Examen y Tipo Inicial (`crearExamenConTipo`):
Persiste simultáneamente el nuevo `Examen` y su registro asociativo `ExamenTipoExamen`.

#### D. Sincronización de Pasos y Exámenes (`guardarPaso`):
Al crear un nuevo paso subordinado a un padre, genera automáticamente la relación `ProcedimientoPasoSecuencia` con `tipoSecuencia = "SIGUIENTE"`. Además sincroniza la lista de exámenes asociados, eliminando los que fueron removidos por el usuario en memoria y actualizando los vigentes.

#### E. Eliminación Segura de Pasos (`eliminarPaso`):
Valida que el paso no tenga pasos hijos subordinados con `tipoSecuencia = "SIGUIENTE"`. Si tiene hijos, prohíbe la eliminación. Si no tiene hijos, elimina en cascada sus secuencias entrantes/salientes, sus asignaciones de exámenes y finalmente el paso.

---

### 6.4 Validaciones Centralizadas: `ValidadorComun` y Bean Validation

`ValidadorComun` provee funciones estáticas independientes del framework web:
- `textoObligatorio(String valor, String campo)`: Verifica que no sea nulo ni en blanco tras `trim()`.
- `activo(Boolean activo, String campo)`: Lanza excepción si el valor no es `Boolean.TRUE`.
- `rangoFechas(Date inicio, Date fin)`: Comprueba que la fecha de fin no sea anterior a la de inicio.
- `dentroDelPeriodo(Date inicio, Date fin, Date padreInicio, Date padreFin)`: Asegura que un evento subordinado no inicie antes ni termine después de su consulta padre.
- `expresionRegular(String regex)`: Valida la sintaxis de una expresión regular mediante `Pattern.compile(regex)`.
- `formato(String valor, String regex, String indicaciones)`: Evalúa que el valor coincida con el patrón mediante `Pattern.matches()`.

---

### 6.5 Catálogo Completo de DAOs Especializados

| DAO | Entidad | Métodos Clave y Consultas |
|---|---|---|
| `ClinicaDAO` | `Clinica` | `findAllActivos()`: Retorna clínicas activas ordenadas por nombre. Búsqueda en campos `nombre`, `tipo`, `comentarios`. |
| `RolDAO` | `Rol` | `findAllActivos()`: Retorna roles activos ordenados por nombre. Búsqueda en `nombre`. |
| `TipoDocumentoDAO` | `TipoDocumento` | `findAllActivos()`: Tipos de documento activos. Búsqueda en `nombre`. |
| `TipoMedioContactoDAO` | `TipoMedioContacto` | `findAllActivos()`: Tipos de contacto activos. Búsqueda en `nombre`. |
| `TipoExamenDAO` | `TipoExamen` | `findAllActivos()`: Tipos de examen activos. Búsqueda en `nombre`. |
| `ExamenDAO` | `Examen` | `findAllActivos()`: Exámenes activos para combos de selección. |
| `ExamenTipoExamenDAO` | `ExamenTipoExamen` | `findByExamen(UUID idExamen)`: Tipos asociados a un examen. `existeExamenTipo(...)`: Validación de duplicados. |
| `PersonaDAO` | `Persona` | `buscarParaAutocompletar(...)`: Búsqueda por nombres y apellidos. |
| `PersonaRolDAO` | `PersonaRol` | `findByClinicaAndRol(...)`: Personas con rol asignado en clínica activa. `buscarPacientes(...)`: Búsqueda de pacientes de la clínica actual. `findResponsable(...)`: Primer funcionario disponible para un rol en la clínica. |
| `DocumentoDAO` | `Documento` | `findByPersona(UUID idPersona)`: Documentos de una persona. `existePersonaTipoValor(...)`: Evita duplicar el mismo documento en la misma persona. |
| `MedioContactoDAO` | `MedioContacto` | `findByPersona(UUID idPersona)`: Medios de contacto de una persona. `existePersonaTipoValor(...)`: Evita duplicados. |
| `ProcedimientoDAO` | `Procedimiento` | `findAllActivos()`: Procedimientos activos para asignación en consultas. |
| `ProcedimientoPasoDAO` | `ProcedimientoPaso` | `findByProcedimiento(UUID id)`: Pasos con rol y exámenes precargados. `tieneFin(...)`: Valida si existe un paso con `indicaFin = true`. `findPasoInicial(...)`: Encuentra el paso raíz (nodo sin dependencias entrantes). |
| `ProcedimientoPasoSecuenciaDAO` | `ProcedimientoPasoSecuencia` | `findByProcedimiento(...)`: Consulta todas las aristas de secuencia. `existeSecuencia(...)`: Validación de relación existente. |
| `ProcedimientoPasoExamenDAO` | `ProcedimientoPasoExamen` | `findByPaso(...)`: Exámenes asignados a un paso. `existePasoExamen(...)`: Validación de duplicados. |
| `ConsultaDAO` | `Consulta` | `findRangeFiltrado(...)` y `countFiltrado(...)`: Paginación filtrada por clínica de sesión, rango de fechas y texto libre. `buscarParaAutocompletar(...)`: Consultas por paciente o referencia. |
| `ConsultaProcedimientoDAO` | `ConsultaProcedimiento` | `findByConsulta(UUID idConsulta)`: Procedimientos de una consulta. |
| `ConsultaProcedimientoPasoDAO` | `ConsultaProcedimientoPaso` | `findByConsultaProcedimiento(UUID idCP)`: Pasos ejecutados con su responsable precargado. |
| `OrdenExamenDAO` | `OrdenExamen` | `buscarParaAutocompletar(...)`: Búsqueda por indicaciones o nombre del procedimiento. |
| `ExamenResultadoDAO` | `ExamenResultado` | `findByOrdenExamen(UUID idOrden)`: Resultados e informes de una orden. |

---

## 7. CAPA DE FRONTERA / PRESENTACIÓN (BOUNDARY JSF)

### 7.1 Framework de Backing Beans: `Model` y `ModelTransaccional`

#### `Model<T, ID>` (Template Method Pattern):
Controlador abstracto que define el ciclo de vida CRUD para vistas JSF:
- **Estados de Operación (`Estado`):** `NINGUNO`, `CREAR`, `MODIFICAR`, `ELIMINAR`.
- **Métodos de Ciclo de Vida:**
  - `cargarDatos()`: Carga la lista completa en memoria (usado en catálogos pequeños).
  - `prepararNuevo()`: Instancia una entidad limpia mediante `crearNuevoRegistro()` y pasa a estado `CREAR`.
  - `seleccionar(T registro)`: Asigna el registro en edición y pasa a estado `MODIFICAR`.
  - `cancelar()`: Restablece el formulario a estado `NINGUNO`.
  - `guardar()`: Valida mediante `validarNegocio(T)`. Invoca `persistirNuevo(T)` o `persistirCambios(T)`. Captura y clasifica excepciones.
  - `eliminar(T registro)`: Elimina la entidad y actualiza la vista.
- **Clasificación Inteligente de Errores (`clasificarError`):**
  Inspecciona la cadena de causas (`getCause()` y `getNextException()`) detectando códigos estándar `SQLState` de PostgreSQL:
  - `23505`: "Ya existe un registro con estos datos. Verifique los campos que deben ser únicos."
  - `23503`: "No se puede completar la operación: este registro está relacionado con otros datos."
  - `23502`: "Faltan campos obligatorios. Complete todos los datos requeridos."
  - `23514`: "Los datos no cumplen las reglas requeridas. Revise las fechas y los valores ingresados."
  - `22001`: "Uno de los campos excede la longitud máxima permitida."
  - `40001`, `40P01`, `55P03`: "Otro usuario está modificando estos datos. Recargue el registro e intente nuevamente."

#### `ModelTransaccional<T, ID>`:
Extiende `Model` para entidades de alto volumen transaccional (ej. `Consulta`, `Persona`, `OrdenExamen`, `Clinica`, `Rol`).
- **Anulación de Carga Completa:** Sobrescribe `cargarDatos()` como un método vacío (*no-op*), impidiendo terminantemente la ejecución de `findAll()` que saturaría la memoria RAM del servidor.
- **Integración con Lazy Loading:** Inicializa y expone `GenericLazyDataModel<T>`.

---

### 7.2 Paginación Lazy Universal: `GenericLazyDataModel<T>`

Extiende `org.primefaces.model.LazyDataModel<T>` de PrimeFaces:
1. **`count(Map<String, FilterMeta> filterBy)`:** Extrae los filtros de columna y el filtro global, delegando al método `dao.count(...)` de la base de datos.
2. **`load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy)`:** Transforma la estructura de orden de PrimeFaces en una lista inmutable de `OrdenConsulta` y los filtros en `FiltroConsulta`. Invoca `dao.findRange(...)` retornando únicamente la página visible (ej. 15 registros).
3. **`getRowKey(T object)`:** Retorna `entity.getIdKey()`.
4. **`getRowData(String rowKey)`:** Recupera el objeto correspondiente desde la lista cargada en el wrapped data.

---

### 7.3 Gestión del Contexto de Sesión: `SesionBean`

Componente `@SessionScoped` que gobierna la seguridad y contexto operativo:
- **Variables de Sesión:** `clinicaActual` (`Clinica`), `rolActual` (`Rol`), `personaActual` (`Persona`).
- **Reglas de Selección:**
  - Solo permite seleccionar clínicas con `activo = true`.
  - Solo permite seleccionar roles con `activo = true`.
  - Valida mediante `PersonaRolDAO.findByClinicaAndRol` que la persona elegida tenga efectivamente ese rol asignado y activo en esa clínica de trabajo.
- **Propósito:** Al registrar una consulta médica, el sistema garantiza que la consulta se asigne a la clínica activa de la sesión y que el personal actuante tenga permisos en dicha sede.
- **Cierre de Sesión:** Limpia las variables de contexto y redirige a la página inicial (`/index.jsf`).

---

### 7.4 Internacionalización: `LocaleBean`

Componente `@SessionScoped`:
- Detecta el idioma del navegador del cliente al iniciar la sesión. Si no es inglés o español, establece español (`es`) por defecto.
- Permite alternar dinámicamente entre Español (`es`) e Inglés (`en`) en tiempo de ejecución, actualizando el `UIViewRoot` de Faces y recargando los paquetes de recursos `messages.properties` y `messages_en.properties`.

---

### 7.5 Convertidor Genérico de Entidades: `EntityConverter`

Componente `@RequestScoped` registrado bajo el nombre `entityConverter`:
- **`getAsString`:** Recibe una entidad JPA que implementa `IdentificableEntity` y genera un identificador seguro con la sintaxis:
  `sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona:e3b0c442-98fc-1c14-9afb-4c72fe0a7e80`
- **`getAsObject`:** Descompone la cadena en el nombre de la clase y el UUID. Verifica contra el `Metamodel` de JPA que la clase pertenezca al dominio, valida el formato del UUID y busca la entidad mediante `em.find(entityClass, id)`. Si no existe o es inválida, lanza `ConverterException` impidiendo inyecciones o manipulaciones de parámetros en selects y autocompletes.

---

### 7.6 Detalle de Backing Beans Específicos

#### `ConsultaModel` (`boundary/jsf/ConsultaModel.java`)
- Administra las consultas médicas y, de forma embebida, sus procedimientos ejecutados (`ConsultaProcedimiento`) y pasos (`ConsultaProcedimientoPaso`).
- **Lógica de Paciente:** `validarPaciente` exige que la persona asociada tenga rol con nombre que incluya "paciente", activo, y asignado a la clínica actual de `SesionBean`.
- **Filtro Temporal:** Filtra consultas por rango de fechas (`fechaDesde` y `fechaHasta`). Calcula el inicio del día (00:00:00) y fin del día (23:59:59.999) en la zona horaria `America/El_Salvador`.
- **Adición de Procedimientos:** Al agregar un procedimiento a la consulta:
  1. Verifica que el procedimiento esté activo.
  2. Valida que sus fechas estén dentro del período de la consulta.
  3. Localiza el paso inicial del procedimiento mediante `procedimientoPasoDAO.findPasoInicial`.
  4. Localiza al profesional responsable con ese rol en la clínica activa (`personaRolDAO.findResponsable`).
  5. Invoca `asignacionService.crearProcedimientoConPaso` creando el paso en estado `PENDIENTE`.

#### `ProcedimientoModel` y `ProcedimientoPasoModel`
- **Visualización en Árbol:** `ProcedimientoModel.getArbolPasos` construye un bosque jerárquico (`TreeNode<ProcedimientoPaso>`) analizando las aristas `SIGUIENTE` de `ProcedimientoPasoSecuencia`. Resuelve huérfanos y nodos raíz para visualizarlos en `p:treeTable`.
- **Alerta de Finalización:** `advertirSinFin` despliega una advertencia si el procedimiento no posee ningún paso con `indicaFin = true`.
- **Editor de Pasos (`ProcedimientoPasoModel`):**
  - Al crear el primer paso, no admite dependencia.
  - Al crear pasos subsecuentes, exige seleccionar el paso padre del que depende.
  - No permite que un paso con `indicaFin = true` tenga pasos hijos.
  - Detecta ciclos en memoria antes de persistir.
  - Permite asociar exámenes al paso y observaciones clínicas.

#### `PersonaModel` (`boundary/jsf/PersonaModel.java`)
- Administra el registro de personas y sus tres colecciones embebidas:
  1. **Documentos:** Valida la expresión regular del tipo de documento seleccionado (`ValidadorComun.formato`) y descarta duplicados en la persona.
  2. **Medios de Contacto:** Valida la expresión regular del tipo de contacto y descarta duplicados.
  3. **Roles en Clínicas:** Asigna roles asegurando que la clínica y el rol estén activos y no hayan sido asignados previamente a la misma persona.

#### `ExamenModel` (`boundary/jsf/ExamenModel.java`)
- Administra el catálogo de exámenes.
- Permite crear el examen y su primera clasificación de tipo en una sola transacción (`persistirNuevo` -> `asignacionService.crearExamenConTipo`).
- Gestiona la adición y remoción de tipos de examen (`ExamenTipoExamen`).

#### `OrdenExamenModel` (`boundary/jsf/OrdenExamenModel.java`)
- Gestiona las órdenes de examen emitidas para un paso de procedimiento de consulta.
- Administra embebidos los resultados clínicos (`ExamenResultado`), con interpretación y ruta al atestado físico o digital.

---

## 8. CAPA DE VISTAS Y COMPONENTES COMPUESTOS FACELETS

### 8.1 Plantilla Maestra y Menú

- **`template.xhtml`:**
  - Encabezado con insignia de la clínica activa de trabajo y nombre del usuario en sesión (`sesionBean.displayUsuario`).
  - Botón de acceso rápido para cambio de contexto/rol (`/paginas/cambiarRol.xhtml`).
  - Selector de idioma con menú desplegable (Español / English).
  - Componente `<p:growl id="growlGlobal" showDetail="true" life="4000"><p:autoUpdate/></p:growl>` para notificaciones uniformes en toda la aplicación.
- **`menu.xhtml`:**
  - Estructurado lógicamente en tres submenús:
    1. **Catálogos:** Roles, Tipos de Documento, Tipos de Medio de Contacto, Tipos de Examen, Exámenes, Procedimientos, Clínicas.
    2. **Personas:** Personas, Documentos, Medios de Contacto, Roles de Persona.
    3. **Atención Clínica:** Consultas, Órdenes de Examen, Resultados de Examen.

### 8.2 Componentes Compuestos CRUD

Ubicados en `src/main/webapp/resources/crud/`:

#### `crudCatalogo.xhtml`
Componente para entidades de bajo volumen que se gestionan completamente en memoria.
- Recibe el atributo `model` (subclase de `Model`) y `titulo`.
- Encapsula `p:dataTable` con paginación local y selección de filas.
- Alterna automáticamente entre vista de tabla (cuando `registroActual == null`) y formulario de edición/creación (cuando `registroActual != null`).
- Incluye diálogo global de confirmación para eliminaciones (`p:confirmDialog`).

#### `crudTransaccional.xhtml`
Componente de alto rendimiento para entidades transaccionales.
- Recibe `model` y `lazyModel` (`GenericLazyDataModel`).
- Posee barra de herramientas superior con búsqueda global (`filtroGlobal`), botón de búsqueda y botón de limpieza.
- La tabla opera en modo `lazy="true"`, consultando exclusivamente los registros de la página activa.
- Provee *facets* para `formulario`, `relaciones` (renderizadas en pestañas adicionales) y `accionesExtra`.

#### `pasosProcedimiento.xhtml`
Componente especializado para modelar flujos de trabajo clínicos.
- Muestra los pasos en un árbol interactivo `p:treeTable`.
- Etiquetas visuales (`p:tag`) para identificar el paso "Inicial" y el paso de "Fin".
- Panel integrado para asociar exámenes requeridos a cada paso seleccionado.

---

## 9. REGLAS DE NEGOCIO CRÍTICAS Y MÁQUINAS DE ESTADOS

### 9.1 Máquina de Estados del Paso Clínico (`EstadoPaso`)

```
   [Creación de Paso]
           |
           v
     +-----------+
     | PENDIENTE |  (Paso creado y en espera de atención)
     +-----+-----+
           | Transición válida
           v
     +-----------+
     | EN_CURSO  |  (Atención médica activa)
     +-----+-----+
           | Transición válida
           v
    +-------------+
    | COMPLETADO  |  (Paso finalizado - Estado terminal)
    +-------------+
```

- Un paso nuevo únicamente puede nacer en estado `PENDIENTE`.
- Solo se permite avanzar en orden estricto: `PENDIENTE -> EN_CURSO -> COMPLETADO`.
- Se prohíben retrocesos o saltos directos (ej. de `PENDIENTE` a `COMPLETADO`).
- Solo se pueden emitir órdenes de examen cuando el paso está `EN_CURSO` o `COMPLETADO`.

### 9.2 Reglas de la Consulta Médica y Pacientes
- **Clínica Obligatoria:** No se pueden consultar ni crear registros sin haber seleccionado previamente una clínica activa en `SesionBean`.
- **Validación de Paciente:** La persona a la que se le abre una consulta debe tener asignado el rol "Paciente" (o que contenga dicha denominación) en esa misma clínica.
- **Intervalo de Consulta:** La fecha de fin de la consulta no puede ser anterior a la fecha de inicio.
- **Subordinación Temporal:** Los procedimientos y pasos clínicos no pueden iniciar antes del inicio de la consulta ni finalizar después de que la consulta haya concluido.

### 9.3 Reglas de Grafos en Procedimientos
- **Paso Inicial:** Es aquel paso que no posee ninguna secuencia entrante (*in-degree 0*).
- **Paso de Fin:** Todo procedimiento debe tener al menos un paso marcado con `indicaFin = true`.
- **Sin Hijos tras el Fin:** Un paso con `indicaFin = true` no puede tener pasos subordinados (secuencias salientes).
- **Aciclicidad Estricta:** Cualquier intento de crear una secuencia que conecte directa o indirectamente un paso con sus antecesores es abortado por el algoritmo DFS.

---

## 10. ESTRATEGIA DE PRUEBAS Y ASEGURAMIENTO DE CALIDAD

El proyecto cuenta con una batería de más de **460 pruebas unitarias y de integración**, garantizando una cobertura de código superior al umbral mínimo del **65%** exigido por la configuración de JaCoCo.

### 10.1 Categorías de Pruebas

1. **Pruebas de Regresión de Rúbrica (`RubricaRegressionTest`):**
   - Verifica que todos los catálogos nuevos nazcan con `activo = true`.
   - Comprueba que la sesión clínica rechace clínicas inactivas y recuerde la sede activa.
   - Valida que los documentos y contactos fallen ante regex inválidas o tipos inactivos.
   - Prueba que las consultas médicas fallen si no hay clínica activa o si el paciente pertenece a otra sede.
   - Verifica la creación atómica de procedimientos con su paso inicial `PENDIENTE`.
   - Comprueba el cálculo de fechas de inicio y fin de día en la zona horaria de El Salvador.
2. **Pruebas Unitarias de DAOs (`*DAOTest`):**
   - Utilizan mocks de `EntityManager` y `CriteriaBuilder` para verificar la generación de sentencias JPQL y Criteria.
3. **Pruebas Unitarias de Modelos (`*ModelTest`):**
   - Prueban los métodos del ciclo de vida CRUD, selección de filas, limpieza de borradores y manejo de mensajes en `FacesContext`.
4. **Pruebas de Algoritmos y Validación (`AsignacionServiceTest`):**
   - Simula grafos complejos (lineales, ramificados, con ciclos) comprobando la efectividad del algoritmo DFS.
5. **Pruebas de Integración con Base Real (`PostgreSQLIT`):**
   - Utiliza Testcontainers para levantar una instancia real de PostgreSQL 15, cargando el esquema SQL del profesor (`clinica_ppi115_2026_08_20.sql`) para validar tipos UUID, claves foráneas y triggers.

---

## 11. GUÍA DE CONFIGURACIÓN, COMPILACIÓN Y DESPLIEGUE

### 11.1 Requisitos del Entorno
- **JDK 21** (Eclipse Temurin, OpenJDK u Oracle JDK).
- **Apache Maven 3.9+**.
- **PostgreSQL 15+** en ejecución con el esquema `clinica_ppi115_2026_08_20.sql`.
- **Open Liberty** (administrado automáticamente por el plugin maven en `target/liberty/wlp`).

### 11.2 Parámetros de Conexión (Variables de Entorno)

| Variable | Valor Predeterminado | Descripción |
|---|---|---|
| `GALENOSV_DB_HOST` | `localhost` | Host del servidor PostgreSQL |
| `GALENOSV_DB_PORT` | `5432` | Puerto del servidor PostgreSQL |
| `GALENOSV_DB_NAME` | `galenoSV` | Nombre de la base de datos clínica |
| `GALENOSV_DB_USER` | `admin` | Usuario con permisos en la base de datos |
| `GALENOSV_DB_PASSWORD` | `admin` | Contraseña de la base de datos |
| `GALENO_ADMIN_PASSWORD` | `admin` | Contraseña del usuario administrador de la app |
| `GALENO_CLINICO_PASSWORD` | `clinico` | Contraseña del personal clínico |

### 11.3 Comandos de Compilación y Ejecución

```bash
# 1. Compilar y ejecutar pruebas unitarias con reporte de cobertura JaCoCo
mvn clean verify

# 2. Iniciar el servidor Open Liberty en modo desarrollo (hot-reload habilitado)
mvn -DskipTests liberty:dev

# 3. Ejecutar pruebas de integración completas contra Docker Testcontainers
mvn -Pintegration verify
```

### 11.4 Acceso a la Aplicación
Una vez iniciado Open Liberty, la aplicación estará disponible en:
- **URL Segura (HTTPS):** `https://localhost:9443/galenoSV/`
- **Credenciales Demo:**
  - Administrador: usuario `admin`, clave `admin`
  - Personal Clínico: usuario `clinico`, clave `clinico`

---

## CONCLUSIÓN

El sistema **GalenoSV** representa una arquitectura de software empresarial robusta, desacoplada y orientada a la mantenibilidad. Combina la potencia del estándar **Jakarta EE 11** con un control exhaustivo de persistencia sobre **PostgreSQL**, garantizando alta integridad transaccional, fluidez en la interfaz de usuario con **PrimeFaces 15**, y un cumplimiento estricto de las reglas y protocolos clínicos del modelo de salud.

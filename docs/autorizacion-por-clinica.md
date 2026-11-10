# Autorización por clínica: análisis del modelo actual

Documento de análisis. No incluye cambios de código.

Archivos revisados:

- `src/main/java/.../entities/PersonaRol.java`
- `src/main/java/.../entities/Consulta.java`
- `src/main/java/.../entities/Clinica.java`
- `src/main/java/.../control/PersonaRolService.java`
- `src/main/java/.../boundary/jsf/SesionBean.java`

Archivos de contexto consultados: `server.xml`, `web.xml`, `DefaultService`, `DefaultDAO`, `FiltroConsulta`, `GenericResource`, `GenericLazyDataModel`, `ConsultaModel`, `ConsultaDAO`, `PersonaRolDAO`, migraciones SQL y el resto de entidades JPA.

## Resumen ejecutivo

En el estado actual **no existe una ruta de datos desde el principal de Liberty (`admin`, `clinico`) hasta una `Persona` o una `Clinica`**. Liberty solo aporta un nombre de usuario y grupos/roles de aplicación. La relación Persona-Clínica existe en el dominio mediante `PersonaRol`, pero nada indica qué `Persona` corresponde al usuario autenticado.

Por tanto:

- `admin` y `clinico` son usuarios de `basicRegistry`, no filas de la base de datos.
- `Persona` es un registro demográfico; no tiene usuario, login ni correo obligatorio.
- `PersonaRol` es la tabla puente `Persona` - `Rol` - `Clinica`; una persona puede tener varias asignaciones y varias clínicas.
- `SesionBean` es la única relación “usuario -> clínica” en tiempo de ejecución, pero es una selección manual de sesión, no una autorización derivada del principal.
- La seguridad REST actual solo valida roles (`ADMINISTRADOR`, `PERSONAL_CLINICO`), no el alcance por clínica. Un `clinico` autenticado puede listar y consultar por ID datos de cualquier clínica.

## 1. Relación usuario autenticado -> Persona -> Clínica

### 1.1 Principal de Liberty

`src/main/liberty/config/server.xml` define:

- Usuario `admin`, grupo `ADMINISTRADOR`.
- Usuario `clinico`, grupo `PERSONAL_CLINICO`.
- Ambos grupos se mapean a los roles de aplicación del mismo nombre.

`web.xml` protege `/api/*` con autenticación BASIC y exige uno de esos dos roles. `GenericResource` permite lectura a ambos roles y escritura solo a `ADMINISTRADOR`.

El principal disponible en JAX-RS es únicamente:

- `SecurityContext.getUserPrincipal().getName()`: `admin` o `clinico`.
- Pertenencia a `ADMINISTRADOR` o `PERSONAL_CLINICO`.

No hay atributos de usuario adicionales, ni un registro de usuarios propio de la aplicación.

### 1.2 Persona y PersonaRol

`Persona` contiene nombres, apellidos, fecha de nacimiento y fecha de creación. No contiene:

- nombre de usuario,
- login,
- correo electrónico obligatorio,
- `id_clinica`,
- referencia al principal de Liberty.

`PersonaRol` sí contiene la relación con clínica:

- `idPersonaRol` (PK),
- `idPersona` (`@ManyToOne`),
- `idRol` (`@ManyToOne`),
- `idClinica` (`@ManyToOne`),
- `fechaCreacion`.

Es decir:

```text
Principal (admin / clinico)
    |
    +-- no existe FK ni tabla de usuario --> Persona

Persona 1 --- N PersonaRol N --- 1 Clinica
                     |
                     N
                     |
                     1 Rol
```

Una misma `Persona` puede tener varias filas `PersonaRol`, por ejemplo:

- paciente en una clínica,
- doctor en otra clínica,
- enfermero en la misma clínica con otro rol.

La tabla `persona_rol.id_clinica` es nullable en el esquema inicial (`V1`) y la migración `V2` no la declara `NOT NULL`. Por tanto, existen asignaciones sin clínica que no deberían contar para autorización por clínica.

`PersonaRolService` ya ofrece consultas útiles para resolver clínicas:

- `findByPersona(UUID idPersona)`: todas las asignaciones de una persona, con rol y clínica.
- `findByClinicaAndRol(UUID idClinica, UUID idRol)`: personas con un rol en una clínica.
- `buscarPacientes(String filtro, UUID idClinica, int max)`: pacientes de una clínica.
- `findResponsable(UUID idClinica, UUID idRol)`: responsable de un rol en una clínica.

Ninguna de estas consultas recibe un nombre de usuario.

### 1.3 Qué hace SesionBean

`SesionBean` es `@SessionScoped` y guarda:

- `clinicaActual`,
- `rolActual`,
- `personaActual`,
- variables temporales para los formularios.

Su funcionamiento actual:

- `getClinicasActivas()` devuelve **todas** las clínicas activas.
- `getRolesActivos()` devuelve **todos** los roles activos.
- `aplicarClinica()` acepta cualquier clínica activa.
- `aplicarCambios()` valida que la persona seleccionada tenga el rol seleccionado en la clínica seleccionada, usando `PersonaRolService.findByClinicaAndRol`.
- `cerrarSesion()` limpia la selección.

Lo importante: `SesionBean` **no consulta el principal**. No comprueba que `clinico` sea la persona seleccionada ni que tenga permiso sobre esa clínica. La selección de clínica es una decisión manual del usuario en la sesión.

Además, `web.xml` solo declara una restricción de seguridad para `/api/*`; no hay una restricción equivalente declarada para las páginas JSF. En cualquier caso, un `ContainerRequestFilter` de JAX-RS no se ejecuta para JSF.

### 1.4 Consecuencia actual

No es posible responder con el esquema actual:

> ¿Cuál es la `Persona` del usuario `clinico`?

Ni tampoco:

> ¿A qué clínicas puede acceder `clinico`?

La única respuesta disponible es la selección manual de `SesionBean`, que no está vinculada al principal y que, en REST, ni siquiera existe porque no hay sesión JSF.

## 2. Entidades que se pueden filtrar por clínica

### 2.1 Rutas de relación

La siguiente tabla indica las rutas que existen en el modelo y si pueden expresarse como `FiltroConsulta` para `DefaultDAO`.

| Entidad | Ruta hacia clínica | ¿FiltroConsulta? | Observación |
|---|---|---|---|
| `Clinica` | `idClinica` | Sí, `EQUALS` o `IN` | Para un usuario restringido, filtrar por `IN` con sus clínicas asignadas. |
| `PersonaRol` | `idClinica.idClinica` | Sí, `EQUALS` o `IN` | Asignación directa. Ignorar filas con `idClinica` nulo. |
| `Consulta` | `idPersonaRol.idClinica.idClinica` | Sí, `EQUALS` | La consulta pertenece a la clínica del paciente asignado. |
| `ConsultaProcedimiento` | `idConsulta.idPersonaRol.idClinica.idClinica` | Sí, `EQUALS` | La clínica se hereda de la consulta. |
| `ConsultaProcedimientoPaso` | `idConsultaProcedimiento.idConsulta.idPersonaRol.idClinica.idClinica` | Sí, `EQUALS` | Ruta por la consulta; es la recomendada para saber a qué clínica pertenece el paso. |
| `ConsultaProcedimientoPaso` | `idPersonaRol.idClinica.idClinica` | Sí, `EQUALS` | Ruta por el responsable del paso. Puede ser distinta de la clínica de la consulta. |
| `OrdenExamen` | `idConsultaProcedimientoPaso.idConsultaProcedimiento.idConsulta.idPersonaRol.idClinica.idClinica` | Sí, `EQUALS` | Ruta por la consulta del paso. |
| `ExamenResultado` | `idOrdenExamen.idConsultaProcedimientoPaso.idConsultaProcedimiento.idConsulta.idPersonaRol.idClinica.idClinica` | Sí, `EQUALS` | Ruta por la orden y la consulta. |
| `Persona` | `personaRolList.idClinica.idClinica` | No | `personaRolList` es `@OneToMany`; `DefaultDAO` no atraviesa colecciones. Además, una persona puede pertenecer a varias clínicas. |
| `Documento` | `idPersona.personaRolList.idClinica.idClinica` | No | Misma limitación de colección. El documento está ligado a `Persona`, no a `PersonaRol` ni a `Clinica`. |
| `MedioContacto` | `idPersona.personaRolList.idClinica.idClinica` | No | Igual que `Documento`. |
| `Rol` | — | No | Catálogo global; no tiene clínica. |
| `TipoDocumento` | — | No | Catálogo global. |
| `TipoMedioContacto` | — | No | Catálogo global. |
| `TipoExamen` | — | No | Catálogo global. |
| `Examen` | — | No | Catálogo global. |
| `ExamenTipoExamen` | — | No | Asociación entre catálogos globales. |
| `Procedimiento` | — | No | Catálogo global. |
| `ProcedimientoPaso` | — | No | Pertenece a un procedimiento global; tiene `idRol`, pero no clínica. |
| `ProcedimientoPasoExamen` | — | No | Asociación entre paso y examen; sin clínica. |
| `ProcedimientoPasoSecuencia` | — | No | Secuencia entre pasos; sin clínica. |

Rutas adicionales que sí funcionan, aunque no sirven para autorizar por clínica:

- `Documento.idPersona.idPersona` y `MedioContacto.idPersona.idPersona`: filtran por persona, no por clínica.
- `Consulta.idPersonaRol.idPersona.idPersona`: filtra consultas de una persona.
- `PersonaRol.idPersona.idPersona` y `PersonaRol.idRol.idRol`: filtran por persona o rol.

### 2.2 Limitaciones de DefaultDAO/FiltroConsulta

`DefaultDAO.resolverRuta` solo permite:

- atributos persistentes escalares en el último segmento,
- relaciones `@ManyToOne` o `@OneToOne` en los segmentos intermedios,
- ninguna colección `@OneToMany` ni `@ManyToMany`.

Por eso las rutas de `Persona`, `Documento` y `MedioContacto` no son válidas como `FiltroConsulta`. Para esas entidades haría falta:

- una consulta JPQL/Criteria específica con `EXISTS` o subconsulta, o
- una relación directa nueva, por ejemplo `Documento.idPersonaRol` o `Documento.idClinica`, si se pudiera cambiar el esquema.

`DefaultDAO.aplicarFiltros` combina todos los `FiltroConsulta` con `AND`. Esto es útil para exigir, por ejemplo, la clínica de la consulta y la clínica del responsable del paso a la vez.

Los modos relevantes ya existen:

- `EQUALS`: comparación directa; funciona con `UUID`.
- `IN`: comparación contra una colección; funciona para listar varias clínicas permitidas.

No hay soporte de `OR` ni de subconsultas en `FiltroConsulta`.

### 2.3 Semántica de pertenencia

Hay dos clínicas posibles en la cadena transaccional:

1. **Clínica de la consulta**: se obtiene del paciente (`Consulta.idPersonaRol.idClinica`).
2. **Clínica del responsable del paso**: se obtiene del profesional asignado (`ConsultaProcedimientoPaso.idPersonaRol.idClinica`).

En JSF, `ConsultaModel` pasa la clínica de trabajo a `ConsultaService` al crear o actualizar. El servicio comprueba el rol activo de paciente tanto para JSF como para REST. La clínica de trabajo se comprueba cuando el llamador proporciona su UUID. REST aún requiere un vínculo con el principal para resolver ese alcance. También `agregarProcedimiento` busca el responsable con `findResponsable(idClinicaActual, rol)`. La intención del diseño es que ambas clínicas coincidan, pero el modelo no impone esa igualdad a nivel de base de datos.

Antes de filtrar por clínica hay que decidir si:

- se confía en la clínica de la consulta como dueña del expediente, o
- se exige que la clínica del responsable también coincida, o
- se permite un responsable de otra clínica y se define qué datos puede ver.

### 2.4 Parche de `consulta_procedimiento_paso`

El SQL compartido agrega `id_procedimiento_paso` y `valor` como columnas anulables. La referencia conecta el paso ejecutado con la definición en el catálogo `procedimiento_paso`. La clínica del expediente sigue resolviéndose a través de `consulta_procedimiento` y la consulta. El catálogo de pasos es compartido entre clínicas.

La FK con `ON DELETE RESTRICT` impide borrar una definición cuando existen pasos ejecutados que la referencian. La cláusula `NOT VALID` pospone la revisión de las filas anteriores, mientras que la FK comprueba las referencias nuevas o modificadas. El parche tampoco exige que la definición pertenezca al mismo procedimiento que el registro de consulta. Si esa igualdad se requiere, debe comprobarse al asignar una definición.

La entidad JPA y el DTO actuales de `ConsultaProcedimientoPaso` carecen de esos campos. Antes de exponer `valor`, hay que decidir su contenido, quién puede leerlo y cómo auditar su acceso. Si contiene datos clínicos, aplica el alcance de la consulta.

## 3. Propuesta mínima

### 3.1 Componentes

La propuesta mínima para REST sería:

1. **Resolver de identidad**
   - Un componente `ClinicaUsuarioResolver` que recibe el nombre del principal y devuelve la persona y/o las clínicas permitidas.
   - Sin cambio de esquema, el resolver puede apoyarse en una configuración externa `usuario -> idPersona`.
   - Con el `idPersona`, se reutiliza `PersonaRolService.findByPersona` para obtener las clínicas activas.
   - Si se prefiere no tocar configuración, se puede mapear directamente `usuario -> idClinica`, pero se pierde la validación contra `PersonaRol`.

2. **Contexto de petición**
   - Un bean CDI `@RequestScoped` `ClinicaContexto` con:
     - `boolean restringido`,
     - `UUID clinicaActiva`,
     - `Set<UUID> clinicasPermitidas`.
   - Es preferible a `ThreadLocal`: JAX-RS puede reutilizar hilos y el contexto de petición CDI expresa mejor el alcance.
   - El filtro escribe el contexto y los recursos y servicios lo leen durante la misma petición.
   - Para los recursos sujetos a clínica, un contexto ausente o una membresía vacía debe impedir el acceso. Omitir el filtro en ese caso devolvería todos los registros.

3. **Filtro JAX-RS**
   - `@Provider` que implemente `ContainerRequestFilter`.
   - Anotado con `@Priority(Priorities.AUTHORIZATION)` para ejecutarse después de la autenticación y con `SecurityContext` disponible.
   - Debe aplicar solo a `/api/*` o a los recursos que correspondan.
   - Flujo:
     1. Obtener el principal. Si no hay principal, abortar con 401.
     2. Si el usuario es `ADMINISTRADOR`, decidir si se marca como no restringido o si también se le exige clínica.
     3. Si es `PERSONAL_CLINICO`, resolver persona y clínicas activas.
     4. Si no hay clínica, abortar con 403.
     5. Si hay una sola clínica, usarla como clínica activa.
     6. Si hay varias, exigir una selección explícita, por ejemplo `X-Clinica-Id`, y validar que pertenezca al conjunto permitido.
     7. Guardar el resultado en `ClinicaContexto`.

4. **Aplicación en listados**
   - `GenericResource.listar` debe dejar de usar solo la sobrecarga de tres argumentos.
   - Debe construir una lista de filtros e invocar las sobrecargas con filtros:
     - `findRange(first, max, filtroGlobal, orden, filtros)`,
     - `count(filtroGlobal, filtros)`.
   - Para consultas, el servidor construye un `FiltroConsulta` con ruta `idPersonaRol.idClinica.idClinica`, modo `EQUALS` y el UUID validado. La misma lista de filtros se usa para los resultados y su total.
   - Cada recurso concreto declara su ruta de clínica, por ejemplo:
     - `Clinica`: `idClinica` con `IN` de clínicas permitidas.
     - `PersonaRol`: `idClinica.idClinica`.
     - `Consulta`: `idPersonaRol.idClinica.idClinica`.
     - `ConsultaProcedimiento`: `idConsulta.idPersonaRol.idClinica.idClinica`.
     - `ConsultaProcedimientoPaso`: `idConsultaProcedimiento.idConsulta.idPersonaRol.idClinica.idClinica`.
     - `OrdenExamen`: ruta por el paso y la consulta.
     - `ExamenResultado`: ruta por la orden.
   - Si el contexto no está restringido, no se añade el filtro.
   - Si el recurso no tiene ruta de clínica, hay que decidir si es catálogo global o si queda restringido a administradores.

   Ejemplo conceptual del filtro que se agregaría a la lista:

   - `FiltroConsulta("idPersonaRol.idClinica.idClinica", idClinica, "EQUALS")`
   - `FiltroConsulta("idClinica", clinicasPermitidas, "IN")` para el recurso `Clinica`

   El filtro de clínica debe agregarse siempre al final y no debe poder ser eliminado por parámetros del cliente. Al combinarse con `AND`, un cliente no puede ampliar el alcance enviando otro filtro.

### 3.2 Cobertura más allá del listado

El filtro de clínica en `findRange` solo protege los listados. Para una autorización correcta también hay que cubrir:

- `GET /api/recurso/{id}`: verificar que la entidad pertenece a la clínica activa antes de devolverla.
- `PUT /api/recurso/{id}`: verificar la entidad actual y el destino del cambio.
- `DELETE /api/recurso/{id}`: verificar la entidad antes de eliminar.
- `POST /api/recurso`: validar que la clínica del cuerpo coincida con la clínica activa o que el usuario sea administrador global.

Sin esta cobertura, un usuario restringido podría seguir accediendo por ID a un recurso de otra clínica aunque el listado esté filtrado.

### 3.3 JSF no queda cubierto por el ContainerRequestFilter

`ContainerRequestFilter` es de JAX-RS. Las páginas JSF usan `SesionBean` y los modelos JSF, no pasan por `/api/*`. Por tanto, el filtro propuesto no protege JSF.

Para JSF habría que:

- restringir `SesionBean.getClinicasActivas()` al conjunto resuelto para el principal,
- revalidar la clínica en `aplicarClinica` y `aplicarCambios`,
- y aplicar la misma restricción en los modelos que usan `GenericLazyDataModel` o consultas propias.

`ConsultaModel` ya filtra consultas por `SesionBean.getClinicaActual()`, pero el resto de modelos genéricos no lo hace automáticamente.

### 3.4 Opción sin cambiar el esquema

Si el esquema no se puede modificar, la opción más limpia es:

1. Mantener una configuración externa `usuario -> idPersona`.
2. Resolver las clínicas con `PersonaRolService.findByPersona`.
3. Exigir `X-Clinica-Id` cuando la persona tenga más de una clínica.
4. Validar que la clínica solicitada esté entre las asignaciones activas de la persona.
5. Tratar a `admin` como usuario global o también mapearlo a una persona, según la decisión de alcance.

Ventaja: usa la tabla `PersonaRol` existente y no inventa una relación usuario-persona en la base de datos.

Limitación: la configuración externa hay que mantenerla y no queda auditada en la base de datos. Tampoco resuelve por sí sola el caso de `Persona`, `Documento` y `MedioContacto`, que siguen siendo entidades centradas en la persona y no en la clínica.

## 4. Decisiones pendientes

| Decisión | Opciones | Impacto |
|---|---|---|
| ¿De dónde sale el vínculo usuario-persona? | Nueva tabla `usuario` o columna en `Persona`; configuración externa `usuario -> idPersona`; convención usuario = documento/correo; grupo de Liberty por clínica | Sin cambio de esquema, la configuración externa es la opción más directa. La tabla `usuario` es la solución limpia a medio plazo. |
| ¿Un usuario puede tener una o varias clínicas? | Una clínica fija; varias con selección por `X-Clinica-Id`; varias con clínica por defecto | Si hay varias, hace falta un mecanismo de selección y validación en cada petición. |
| ¿Qué alcance tiene `ADMINISTRADOR`? | Acceso global; también restringido a una clínica; lectura global y escritura restringida | El comportamiento actual es global para escritura. Hay que decidir si eso se mantiene. |
| ¿Qué roles de dominio autorizan acceso? | Cualquier `PersonaRol` activo; solo roles clínicos; excluir explícitamente `paciente` | El rol Liberty `PERSONAL_CLINICO` no es lo mismo que el `Rol` de dominio. Hay que definir la regla. |
| ¿Qué clínica manda en pasos, órdenes y resultados? | La de la consulta; la del responsable del paso; ambas deben coincidir | La ruta por la consulta es la más estable para el expediente. La ruta por el responsable puede ser más restrictiva. |
| ¿Persona, Documento y MedioContacto son globales o por clínica? | Globales a la persona; visibles solo en clínicas donde la persona tiene asignación; replicados por clínica | Si se limitan por clínica sin cambiar el esquema, hará falta una consulta con subconsulta. |
| ¿Los catálogos son globales? | `Rol`, `Examen`, `Procedimiento`, tipos, etc. compartidos; catálogos por clínica | Si deben ser por clínica, el esquema actual no lo soporta y habría que añadir relaciones. |
| ¿Qué pasa si no se resuelve clínica? | 401, 403, 409 o redirección a selección de clínica | Para REST, 403 es lo más claro. Para JSF, se podría obligar a elegir clínica. |
| ¿Qué pasa si hay varias clínicas y no se envía selección? | 400/409; usar una por defecto; usar la última de sesión | Sin una regla explícita, el filtro no puede elegir de forma segura. |
| ¿Se protege también JSF con el mismo criterio? | Solo REST; REST y JSF; solo JSF | El `ContainerRequestFilter` no cubre JSF. Si la aplicación se usa por JSF, hay que decidir si se acepta esa brecha. |
| ¿Cómo se crean los datos de demostración? | `clinico` mapeado a una `Persona` con `PersonaRol` en una clínica; `admin` global; ambos mapeados | Sin datos semilla, el filtro rechazará al usuario clínico o no tendrá clínica que resolver. |
| ¿Se requiere auditoría de acceso por clínica? | No; log de accesos; tabla de auditoría | No es imprescindible para el MVP, pero conviene decidirlo antes de producción. |

## Recomendación para el MVP

Sin cambiar el esquema:

1. Mapear `clinico` a un `idPersona` mediante configuración externa.
2. Resolver sus clínicas con `PersonaRolService.findByPersona`, exigiendo `Clinica.activo = true` y `Rol.activo = true`.
3. Si tiene una sola clínica, usarla automáticamente; si tiene varias, exigir `X-Clinica-Id` validado contra sus asignaciones.
4. Tratar a `admin` como global, salvo que se decida lo contrario.
5. Aplicar `FiltroConsulta` en los listados REST mediante las sobrecargas con filtros de `DefaultService.findRange` y `count`.
6. Extender la verificación a `GET/PUT/DELETE` por ID y a `POST`.
7. Restringir también `SesionBean.getClinicasActivas()` y las consultas JSF para que la selección manual no pueda salirse del conjunto permitido.

A medio plazo, si se puede cambiar el esquema, la solución más mantenible es una tabla `usuario` con `username` único y FK a `Persona`, más una regla explícita para la clínica activa. Eso elimina la configuración manual y permite auditar el vínculo usuario-persona.

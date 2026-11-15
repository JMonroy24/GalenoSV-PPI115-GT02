# Refactorización BCE por archivo

Los cambios están aplicados en el código del repositorio. La capa Boundary presenta y valida el contexto de interacción; Control conserva las operaciones y consultas; Entity declara las consultas nombradas.

## Decisiones sobre los nombres del repositorio

- La observación «completarConsulta» se aplica a `ConsultaProcedimientoModel.completeConsulta`, según la aclaración del usuario. Se conserva ese nombre público utilizado por Facelets.
- `Examen` no tiene un atributo de expresión regular. Ese flujo corresponde a `tipoDocumento.xhtml` y `tipoMedioContacto.xhtml`; se han ajustado ambas vistas sin alterar el esquema del examen.
- `examen.xhtml` ya no contenía una lista de procedimientos. Se comprobó que no consulta ni muestra esos procedimientos; el editor mantiene únicamente los tipos asignados.
- Las tablas principales requieren filtro antes de contar o cargar. Las relaciones se consultan por el padre seleccionado; sus borradores ya cargados requieren texto al superar 20 elementos.

## Java: entidades

| Archivo | Cambio específico |
| --- | --- |
| [entities/Clinica.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/entities/Clinica.java) | Añade `Clinica.findAllActivos`. |
| [entities/Consulta.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/entities/Consulta.java) | Añade `Consulta.findRangeFiltrado`, `Consulta.countFiltrado`, `Consulta.autocompletarPorClinica`. |
| [entities/Documento.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/entities/Documento.java) | Añade `Documento.existeTipoValor`, `Documento.existeTipoValorExcluyendo`, `Documento.existePersonaTipoValor`, `Documento.existePersonaTipoValorExcluyendo`. |
| [entities/Examen.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/entities/Examen.java) | Añade `Examen.findAllActivos`. |
| [entities/ExamenTipoExamen.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/entities/ExamenTipoExamen.java) | Añade `ExamenTipoExamen.existeExamenTipo`, `ExamenTipoExamen.existeExamenTipoExcluyendo`. |
| [entities/MedioContacto.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/entities/MedioContacto.java) | Añade `MedioContacto.existePersonaTipoValor`, `MedioContacto.existePersonaTipoValorExcluyendo`. |
| [entities/PersonaRol.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/entities/PersonaRol.java) | Añade `PersonaRol.findByClinicaAndRol`, `PersonaRol.findResponsable`, `PersonaRol.existeAsignacionSinClinica`, `PersonaRol.existeAsignacionSinClinicaExcluyendo`, `PersonaRol.existeAsignacionConClinica`, `PersonaRol.existeAsignacionConClinicaExcluyendo`. |
| [entities/Procedimiento.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/entities/Procedimiento.java) | Añade `Procedimiento.findAllActivos`. |
| [entities/ProcedimientoPaso.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/entities/ProcedimientoPaso.java) | Añade `ProcedimientoPaso.findByProcedimiento`, `ProcedimientoPaso.findPasoInicial`, `ProcedimientoPaso.tieneFin`. |
| [entities/ProcedimientoPasoExamen.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/entities/ProcedimientoPasoExamen.java) | Añade `ProcedimientoPasoExamen.existePasoExamen`, `ProcedimientoPasoExamen.existePasoExamenExcluyendo`. |
| [entities/ProcedimientoPasoSecuencia.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/entities/ProcedimientoPasoSecuencia.java) | Añade `ProcedimientoPasoSecuencia.findByProcedimiento`, `ProcedimientoPasoSecuencia.existeSecuencia`, `ProcedimientoPasoSecuencia.existeSecuenciaExcluyendo`. |
| [entities/Rol.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/entities/Rol.java) | Añade `Rol.findAllActivos`. |
| [entities/TipoDocumento.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/entities/TipoDocumento.java) | Añade `TipoDocumento.findAllActivos`. |
| [entities/TipoExamen.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/entities/TipoExamen.java) | Añade `TipoExamen.findAllActivos`. |
| [entities/TipoMedioContacto.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/entities/TipoMedioContacto.java) | Añade `TipoMedioContacto.findAllActivos`. |

## Java: Control y persistencia

| Archivo | Cambio específico |
| --- | --- |
| [control/ClinicaDAO.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/ClinicaDAO.java) | Reemplaza JPQL repetido por createNamedQuery con consultas declaradas en la entidad; conserva parámetros, orden y exclusiones. |
| [control/ConsultaDAO.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/ConsultaDAO.java) | Reemplaza JPQL repetido por createNamedQuery con consultas declaradas en la entidad; conserva parámetros, orden y exclusiones. |
| [control/ConsultaProcedimientoPasoService.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/ConsultaProcedimientoPasoService.java) | Elimina la sobrescritura redundante de buscarParaAutocompletar para heredar la defensa común de DefaultService. |
| [control/ConsultaProcedimientoService.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/ConsultaProcedimientoService.java) | Elimina la sobrescritura redundante de buscarParaAutocompletar para heredar la defensa común de DefaultService. |
| [control/ConsultaService.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/ConsultaService.java) | Hereda la defensa común y añade autocompletado con clínica obligatoria y longitud mínima de dos caracteres. |
| [control/DefaultService.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/DefaultService.java) | Retorna List.of() inmediatamente para filtros nulos, vacíos o blancos. Normaliza los filtros válidos y conserva la propagación de excepciones del DAO. |
| [control/DocumentoDAO.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/DocumentoDAO.java) | Reemplaza JPQL repetido por createNamedQuery con consultas declaradas en la entidad; conserva parámetros, orden y exclusiones. |
| [control/ExamenDAO.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/ExamenDAO.java) | Reemplaza JPQL repetido por createNamedQuery con consultas declaradas en la entidad; conserva parámetros, orden y exclusiones. |
| [control/ExamenTipoExamenDAO.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/ExamenTipoExamenDAO.java) | Reemplaza JPQL repetido por createNamedQuery con consultas declaradas en la entidad; conserva parámetros, orden y exclusiones. |
| [control/MedioContactoDAO.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/MedioContactoDAO.java) | Reemplaza JPQL repetido por createNamedQuery con consultas declaradas en la entidad; conserva parámetros, orden y exclusiones. |
| [control/OrdenExamenService.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/OrdenExamenService.java) | Elimina la sobrescritura redundante de buscarParaAutocompletar para heredar la defensa común de DefaultService. |
| [control/PersonaRolDAO.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/PersonaRolDAO.java) | Reemplaza JPQL repetido por createNamedQuery con consultas declaradas en la entidad; conserva parámetros, orden y exclusiones. |
| [control/PersonaRolService.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/PersonaRolService.java) | Elimina la sobrescritura redundante de buscarParaAutocompletar para heredar la defensa común de DefaultService. |
| [control/PersonaService.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/PersonaService.java) | Elimina la sobrescritura redundante de buscarParaAutocompletar para heredar la defensa común de DefaultService. |
| [control/ProcedimientoDAO.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/ProcedimientoDAO.java) | Reemplaza JPQL repetido por createNamedQuery con consultas declaradas en la entidad; conserva parámetros, orden y exclusiones. |
| [control/ProcedimientoPasoDAO.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/ProcedimientoPasoDAO.java) | Reemplaza JPQL repetido por createNamedQuery con consultas declaradas en la entidad; conserva parámetros, orden y exclusiones. Declara campos de búsqueda para aplicar realmente el filtro global. |
| [control/ProcedimientoPasoExamenDAO.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/ProcedimientoPasoExamenDAO.java) | Reemplaza JPQL repetido por createNamedQuery con consultas declaradas en la entidad; conserva parámetros, orden y exclusiones. |
| [control/ProcedimientoPasoSecuenciaDAO.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/ProcedimientoPasoSecuenciaDAO.java) | Reemplaza JPQL repetido por createNamedQuery con consultas declaradas en la entidad; conserva parámetros, orden y exclusiones. Declara campos de búsqueda para aplicar realmente el filtro global. |
| [control/RolDAO.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/RolDAO.java) | Reemplaza JPQL repetido por createNamedQuery con consultas declaradas en la entidad; conserva parámetros, orden y exclusiones. |
| [control/TipoDocumentoDAO.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/TipoDocumentoDAO.java) | Reemplaza JPQL repetido por createNamedQuery con consultas declaradas en la entidad; conserva parámetros, orden y exclusiones. |
| [control/TipoExamenDAO.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/TipoExamenDAO.java) | Reemplaza JPQL repetido por createNamedQuery con consultas declaradas en la entidad; conserva parámetros, orden y exclusiones. |
| [control/TipoMedioContactoDAO.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/TipoMedioContactoDAO.java) | Reemplaza JPQL repetido por createNamedQuery con consultas declaradas en la entidad; conserva parámetros, orden y exclusiones. |
| [persistence/UuidFieldSessionCustomizer.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/persistence/UuidFieldSessionCustomizer.java) | Elimina las capturas que ignoraban fallos; aborta la configuración inválida mediante PersistenceException con la causa original. |

## Java: Boundary JSF

| Archivo | Cambio específico |
| --- | --- |
| [boundary/jsf/ConsultaModel.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/boundary/jsf/ConsultaModel.java) | Exige texto o fechas antes del listado; protege las lecturas durante el renderizado. No conserva fallos en la caché de documentos ni muestra «Finalizado» cuando falla la lectura del estado. |
| [boundary/jsf/ConsultaProcedimientoModel.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/boundary/jsf/ConsultaProcedimientoModel.java) | Valida completeConsulta: filtro normalizado de al menos dos caracteres y clínica activa con identificador. Busca únicamente en esa clínica y avisa ante una referencia URL inválida. |
| [boundary/jsf/ExamenModel.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/boundary/jsf/ExamenModel.java) | Marca validationFailed al rechazar una asignación, para conservar abierto el diálogo y mostrar la validación. |
| [boundary/jsf/Model.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/boundary/jsf/Model.java) | Centraliza los errores de lectura: notifica y registra la primera causa de persistencia, bloquea lecturas posteriores en la misma petición Faces y permite reintentar en la siguiente. Las listas de relaciones con más de 20 elementos requieren texto. |
| [boundary/jsf/ModelTransaccional.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/boundary/jsf/ModelTransaccional.java) | Activa por defecto el filtro obligatorio: count/load no acceden al servicio antes de introducir un criterio. |
| [boundary/jsf/ProcedimientoPasoModel.java](../src/main/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/boundary/jsf/ProcedimientoPasoModel.java) | Filtra los exámenes por el paso actualmente editado antes de aplicar el texto; no muestra asociaciones de otros pasos. |

## Java: pruebas

| Archivo | Cambio específico |
| --- | --- |
| [boundary/jsf/ConsultaProcedimientoModelTest.java](../src/test/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/boundary/jsf/ConsultaProcedimientoModelTest.java) | Cubre filtros insuficientes, normalización, ausencia de coincidencias, fallo de conexión y clínica ausente o inactiva. |
| [boundary/jsf/GenericLazyDataModelTest.java](../src/test/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/boundary/jsf/GenericLazyDataModelTest.java) | Verifica count/load sin interacción con DAO para filtros nulos, vacíos y blancos, y la activación de búsquedas tras normalizar el texto. |
| [boundary/jsf/GuardarCatalogoTest.java](../src/test/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/boundary/jsf/GuardarCatalogoTest.java) | Sustituye pruebas CRUD repetidas por un contrato parametrizado para cuatro modelos reales en CREAR/MODIFICAR, sin sobrescribir guardar. |
| [boundary/jsf/ModelTest.java](../src/test/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/boundary/jsf/ModelTest.java) | Amplía guardar para estados no permitidos y verifica que cien lecturas posteriores al fallo no consultan la base ni repiten el aviso; la siguiente petición sí reintenta. |
| [boundary/jsf/ProcedimientoModelTest.java](../src/test/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/boundary/jsf/ProcedimientoModelTest.java) | Actualiza el contrato del listado: no carga ni cuenta hasta recibir filtro. |
| [boundary/jsf/ProcedimientoPasoModelTest.java](../src/test/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/boundary/jsf/ProcedimientoPasoModelTest.java) | Comprueba el umbral de 20 exámenes y que el filtro nunca incluya una asociación perteneciente a otro paso. |
| [boundary/jsf/RolModelTest.java](../src/test/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/boundary/jsf/RolModelTest.java) | Retira los casos repetidos de guardar/crear y guardar/modificar, cubiertos ahora por `GuardarCatalogoTest`; conserva las pruebas particulares del modelo. |
| [boundary/jsf/TipoDocumentoModelTest.java](../src/test/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/boundary/jsf/TipoDocumentoModelTest.java) | Retira los casos repetidos de guardar/crear y guardar/modificar, cubiertos ahora por `GuardarCatalogoTest`; conserva las pruebas particulares del modelo. |
| [boundary/jsf/TipoExamenModelTest.java](../src/test/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/boundary/jsf/TipoExamenModelTest.java) | Retira los casos repetidos de guardar/crear y guardar/modificar, cubiertos ahora por `GuardarCatalogoTest`; conserva las pruebas particulares del modelo. |
| [boundary/jsf/TipoMedioContactoModelTest.java](../src/test/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/boundary/jsf/TipoMedioContactoModelTest.java) | Retira los casos repetidos de guardar/crear y guardar/modificar, cubiertos ahora por `GuardarCatalogoTest`; conserva las pruebas particulares del modelo. |
| [control/AutocompletadoDAOTest.java](../src/test/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/AutocompletadoDAOTest.java) | Contrato parametrizado para diez DAO: filtros nulos/blancos sin EntityManager, escape literal LIKE, límite de resultados y propagación de PersistenceException. |
| [control/AutocompletadoServiceTest.java](../src/test/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/AutocompletadoServiceTest.java) | Contrato parametrizado para diez servicios: filtros nulos/blancos sin interacción con DAO, normalización del caso válido y propagación de la misma excepción. |
| [control/ClinicaDAOTest.java](../src/test/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/ClinicaDAOTest.java) | Adapta los mocks y verificaciones a `createNamedQuery`, conservando los contratos de parámetros y resultados. |
| [control/ConsultaServiceTest.java](../src/test/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/ConsultaServiceTest.java) | Cubre el autocompletado por clínica con filtros nulos, vacíos, blancos/cortos y clínica ausente. |
| [control/ConsultasClinicaDAOTest.java](../src/test/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/ConsultasClinicaDAOTest.java) | Comprueba NamedQuery, parámetros de fecha siempre tipados, variantes de conteo/listado y búsquedas literales de pacientes y responsables. |
| [control/DocumentoDAOTest.java](../src/test/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/DocumentoDAOTest.java) | Adapta los mocks y verificaciones a `createNamedQuery`, conservando los contratos de parámetros y resultados. |
| [control/ExamenDAOTest.java](../src/test/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/ExamenDAOTest.java) | Adapta los mocks y verificaciones a `createNamedQuery`, conservando los contratos de parámetros y resultados. |
| [control/ExamenTipoExamenDAOTest.java](../src/test/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/ExamenTipoExamenDAOTest.java) | Adapta los mocks y verificaciones a `createNamedQuery`, conservando los contratos de parámetros y resultados. |
| [control/MedioContactoDAOTest.java](../src/test/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/MedioContactoDAOTest.java) | Adapta los mocks y verificaciones a `createNamedQuery`, conservando los contratos de parámetros y resultados. |
| [control/PersonaRolDAOTest.java](../src/test/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/PersonaRolDAOTest.java) | Adapta los mocks y verificaciones a `createNamedQuery`, conservando los contratos de parámetros y resultados. |
| [control/PostgreSQLIT.java](../src/test/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/PostgreSQLIT.java) | Amplía la integración real: autocompletado por clínica, exclusión del registro editado, filtros de pasos/secuencias y asociaciones de exámenes por paso. |
| [control/ProcedimientoDAOTest.java](../src/test/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/ProcedimientoDAOTest.java) | Adapta los mocks y verificaciones a `createNamedQuery`, conservando los contratos de parámetros y resultados. |
| [control/ProcedimientoPasoDAOTest.java](../src/test/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/ProcedimientoPasoDAOTest.java) | Adapta los mocks y verificaciones a `createNamedQuery`, conservando los contratos de parámetros y resultados. |
| [control/RolDAOTest.java](../src/test/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/RolDAOTest.java) | Adapta los mocks y verificaciones a `createNamedQuery`, conservando los contratos de parámetros y resultados. |
| [control/TipoDocumentoDAOTest.java](../src/test/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/TipoDocumentoDAOTest.java) | Adapta los mocks y verificaciones a `createNamedQuery`, conservando los contratos de parámetros y resultados. |
| [control/TipoExamenDAOTest.java](../src/test/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/TipoExamenDAOTest.java) | Adapta los mocks y verificaciones a `createNamedQuery`, conservando los contratos de parámetros y resultados. |
| [control/TipoMedioContactoDAOTest.java](../src/test/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/control/TipoMedioContactoDAOTest.java) | Adapta los mocks y verificaciones a `createNamedQuery`, conservando los contratos de parámetros y resultados. |
| [persistence/UuidFieldSessionCustomizerTest.java](../src/test/java/sv/edu/ues/occ/ingenieria/ppi115_2026/clinica/galenosv/persistence/UuidFieldSessionCustomizerTest.java) | Verifica que un descriptor inválido detiene la configuración y conserva la causa del fallo. |

## Facelets: vistas y componentes

| Archivo | Cambio específico |
| --- | --- |
| [paginas/catalogo/examen.xhtml](../src/main/webapp/paginas/catalogo/examen.xhtml) | Formulario vertical con datos esenciales separados. Tipo inicial y asignaciones adicionales en diálogos modales responsivos; botones para volver al examen. La vista permanece limitada al examen y sus tipos. |
| [paginas/catalogo/procedimiento.xhtml](../src/main/webapp/paginas/catalogo/procedimiento.xhtml) | Mensaje de filtro obligatorio en el listado; acciones de navegación/edición en azul. |
| [paginas/catalogo/tipoDocumento.xhtml](../src/main/webapp/paginas/catalogo/tipoDocumento.xhtml) | Explica la secuencia nombre → expresión regular → volver a la lista; tipografía monoespaciada para el patrón. |
| [paginas/catalogo/tipoMedioContacto.xhtml](../src/main/webapp/paginas/catalogo/tipoMedioContacto.xhtml) | Explica la secuencia nombre → expresión regular → volver a la lista; tipografía monoespaciada para el patrón. |
| [resources/crud/botonesFormulario.xhtml](../src/main/webapp/resources/crud/botonesFormulario.xhtml) | La acción de cancelar/navegar usa azul sin cambiar su procesamiento. |
| [resources/crud/botonesTop.xhtml](../src/main/webapp/resources/crud/botonesTop.xhtml) | Nuevo y abrir edición usan azul; eliminar conserva rojo. |
| [resources/crud/crudCatalogo.xhtml](../src/main/webapp/resources/crud/crudCatalogo.xhtml) | Confirmación responsiva y navegación azul. |
| [resources/crud/crudTransaccional.xhtml](../src/main/webapp/resources/crud/crudTransaccional.xhtml) | Instrucción visible antes de buscar y mensaje de lista vacía acorde al filtro. Volver a la lista sin procesar campos. Diálogo de confirmación adaptable. |
| [resources/crud/pasosProcedimiento.xhtml](../src/main/webapp/resources/crud/pasosProcedimiento.xhtml) | Comunica el filtro requerido para más de 20 exámenes asignados y utiliza azul para acciones de preparación y navegación. |

## CSS

| Archivo | Cambio específico |
| --- | --- |
| [resources/css/styles.css](../src/main/webapp/resources/css/styles.css) | Formulario de examen en una columna; diálogos limitados al viewport, contenido desplazable y botones adaptados a móvil; estilo del campo regex y ayuda de búsqueda. |

## Manejo de fallos

Un fallo de persistencia se propaga desde el DAO y el servicio hasta Boundary. Boundary registra tipos/SQLState y presenta un mensaje sin credenciales ni valores clínicos. Un indicador en `FacesContext.getAttributes()` evita continuar con más lecturas en esa petición; no bloquea futuras peticiones. Un descriptor UUID inválido detiene el inicio del proveedor en lugar de dejar mapeos parcialmente configurados.

Las transacciones conservan las reglas de [PersistenceException de Jakarta Persistence 3.2](https://jakarta.ee/specifications/persistence/3.2/apidocs/jakarta.persistence/jakarta/persistence/persistenceexception); el código no intenta reusar una transacción fallida.

## Validación

**Resultado final:** 926 casos, cero fallos y cero errores; 12 pruebas preexistentes omitidas. Incluye las ocho pruebas de PostgreSQL. WAR generado y umbral JaCoCo superado.

Validación final y resultados: ver el registro `target/refactor-verify-complete.log`. La compilación se verifica en `target/refactor-verification` para aislarla de las clases generadas por el editor. PostgreSQL 18 utiliza únicamente una base temporal local de pruebas; la instancia se detiene al terminar.

- Suite unitaria y contrato de cobertura JaCoCo: `verify`.
- Integración PostgreSQL: `PostgreSQLIT`, ocho casos sobre el esquema original.
- XML de las 26 vistas Facelets analizado correctamente.
- No se ha realizado validación visual con la aplicación desplegada en un servidor Jakarta EE.

Consultas nombradas añadidas: **32**.

Cobertura total de líneas en la verificación: **79.94 %**.

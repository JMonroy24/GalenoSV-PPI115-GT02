# Análisis y correcciones de GalenoSV

Se contrastaron los dos documentos aportados con el código del repositorio. Al iniciar
esta revisión ya existían cambios locales extensos; se conservaron, se verificaron y
se completaron. Esta guía describe el conjunto resultante y sus límites.

## Resultado por problema

| Hallazgo | Corrección en el proyecto |
| --- | --- |
| Páginas inestables y orden ignorado | `DefaultDAO` ordena por el ID como desempate, admite orden y filtros por columna; `GenericLazyDataModel` los transmite y convierte conteos con `Math.toIntExact`. |
| Autocompletados sin filtro | Implementación reutilizable con búsqueda literal, mínimo de dos caracteres y límite de 50 resultados. Los patrones escapan `%`, `_` y barra inversa. |
| Consultas N+1 | Carga anticipada de relaciones a uno en DAOs de las tablas; se excluyen colecciones para conservar la paginación SQL. El número de consultas en Liberty sigue requiriendo medición con datos representativos. |
| Nombre del paso de referencia | Consulta por ID en `ProcedimientoPasoModel`, sin depender de la lista nula del modelo transaccional. |
| Campos sin validar | Restricciones Bean Validation, mensajes en español, validación de expresiones regulares y rangos de fechas; reglas cruzadas mediante `validarNegocio`. |
| Formato y duplicados | Documento y contacto usan las expresiones configuradas por tipo; catálogos y relaciones consultan duplicados. |
| Fechas de creación nulas | `@PrePersist` conserva fechas importadas y asigna las ausentes. |
| Asignaciones no atómicas y ciclos | `AsignacionService` valida y persiste en la misma transacción; bloquea el padre y detecta ciclos con DFS. Los flujos de asociaciones usan el servicio. |
| Errores y diálogos | Clasificación por SQLState, mensajes sin rutas de campos ni valores clínicos, conservación del formulario al fallar y restauración del estado de eliminación. |
| Selecciones inválidas | `EntityConverter` rechaza UUID inválidos, clases ajenas al metamodelo, registros inexistentes y entidades sin ID. |
| Metadata UUID de EclipseLink | El customizer conserva el nombre de clase Java y asigna el tipo SQL mediante `sqlType`/`columnDefinition`; evita escribir `uuid` como nombre de clase. Pruebas específicas cubren PK, FK y campos de texto. |
| Configuración y reproducibilidad | Java 21 con `release`, Jakarta EE final, conexión configurable, WAR estable. |
| Pruebas débiles | Pruebas de validaciones reales y regresiones de infraestructura, sin `catch` vacíos en los tests; perfil de integración y umbral de cobertura de líneas del 65 %. |
| Limpieza | Autocompletados e imports redundantes corregidos, NamedQueries sin uso eliminadas y serialVersionUID en los modelos. Los archivos compartidos de NetBeans se conservan para el equipo. |

## Decisiones que requieren información del negocio

Los documentos presentan los valores de estado, tipo de clínica y tipo de secuencia
como propuestas, no como listas autorizadas. Se conservan sus textos y se valida
presencia/longitud cuando corresponda. No se inventa un enum que rechace datos históricos.
Definir esos valores permite completar los selectores y las reglas de fecha de cierre
por estado.

Fecha de nacimiento, clínica de una asignación y rol de un paso mantienen su
opcionalidad actual: los propios documentos piden confirmar esos requisitos.
Tampoco se prohíben secuencias salientes por `indicaFin` sin confirmar su semántica.
Las fechas de eventos clínicos permanecen en `Date`; requieren una política común
de zona horaria antes de migrar su significado a `java.time`.

Los límites de longitud y el esquema inicial son propuestas basadas en los mapeos.
Deben contrastarse con el DDL y los datos reales antes del despliegue.

## Aplicar a una base existente

1. Detener la aplicación y respaldar la base de datos.
2. Ejecutar `scripts/auditar-datos.sql` si existe.
   El script devuelve conteos de datos inválidos, duplicados, referencias y ciclos.
   Revisar también fechas de nacimiento históricas y las longitudes propuestas.
3. Resolver los datos incompatibles con criterio del negocio. No sustituir fechas
   desconocidas por la fecha actual ni borrar duplicados automáticamente.
4. Iniciar Liberty y verificar creación, edición, errores de formulario, selectores
   y paginación con datos representativos.

## Evidencia de verificación

Verificación local final: **464 pruebas unitarias, cero fallos y cero errores**;
WAR generado y umbral JaCoCo cumplido. XML/XHTML parseados y `git diff --check`
sin errores de espacios.

`mvn verify` compila, ejecuta las pruebas unitarias, genera el WAR y comprueba el
umbral de cobertura. Los informes quedan en `target/surefire-reports` y
`target/site/jacoco/index.html`.

`mvn -Pintegration verify` ejecuta además `PostgreSQLIT`, con JDBC y
EclipseLink reales: unicidad normalizada, campos vacíos y nulos, FK de referencia,
fechas inconsistentes, búsquedas literales, paginación y bloqueo optimista.
Este equipo no dispone de Docker accesible; esas pruebas se compilaron pero su
ejecución requiere Docker o el job de CI. No se presenta la integración como aprobada.

Quedan pendientes la ejecución de integración, la comprobación visual en Liberty
y la auditoría del DDL/datos del entorno de despliegue.

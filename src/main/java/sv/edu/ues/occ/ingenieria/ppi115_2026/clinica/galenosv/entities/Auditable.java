package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities;

import java.util.Date;

/**
 * Contrato de auditoría para entidades JPA que deben registrar
 * quién y cuándo creó o modificó un registro.
 *
 * <p>Las entidades que implementen esta interfaz serán detectadas
 * automáticamente por {@code Model.guardar()} para estampar el
 * usuario autenticado en cada operación de persistencia, sin necesidad
 * de modificar el backing bean específico de cada entidad.
 *
 * <h3>Columnas esperadas en la tabla:</h3>
 * <pre>
 *   usuario_creacion    VARCHAR(100)
 *   usuario_modificacion VARCHAR(100)
 * </pre>
 *
 * <h3>Cómo implementar en una entidad existente:</h3>
 * <pre>
 *   {@literal @}Column(name = "usuario_creacion", length = 100)
 *   private String usuarioCreacion;
 *
 *   {@literal @}Column(name = "usuario_modificacion", length = 100)
 *   private String usuarioModificacion;
 *
 *   // ... implementar getters/setters de Auditable
 * </pre>
 */
public interface Auditable {

    String getUsuarioCreacion();
    void setUsuarioCreacion(String usuarioCreacion);

    String getUsuarioModificacion();
    void setUsuarioModificacion(String usuarioModificacion);

    Date getFechaCreacion();
    void setFechaCreacion(Date fechaCreacion);
}

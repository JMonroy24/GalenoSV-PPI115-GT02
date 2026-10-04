package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities;

/**
 * Contrato para que todas las entidades JPA del proyecto expongan
 * una clave estable como {@code String} usada en {@code rowKey} de PrimeFaces.
 *
 * <p>El uso de {@code hashCode()} como {@code rowKey} es inestable porque
 * puede colisionar y cambia entre recargas. Implementar este método
 * retornando el UUID de la PK garantiza unicidad y estabilidad.
 */
public interface IdentificableEntity extends java.io.Serializable {

    /**
     * Retorna el identificador único de la entidad como cadena de texto,
     * apto para usarse como {@code rowKey="#{registro.idKey}"} en PrimeFaces.
     *
     * @return representación String del ID primario; nunca {@code null}
     *         en un registro persistido.
     */
    String getIdKey();
}

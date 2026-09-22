package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import java.io.Serializable;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;

/**
 * Backing bean abstracto para entidades transaccionales (alto volumen).
 * Extiende Model para integrar GenericLazyDataModel y anular la carga 
 * completa en memoria (findAll).
 *
 * @param <T>  tipo de la entidad JPA
 * @param <ID> tipo de la llave primaria; debe ser Serializable
 */
public abstract class ModelTransaccional<T, ID extends Serializable> extends Model<T, ID> {

    private static final long serialVersionUID = 1L;

    /**
     * Modelo lazy para PrimeFaces; solo carga la página visible.
     */
    private GenericLazyDataModel<T> lazyModel;

    /**
     * Inicializa el GenericLazyDataModel a partir del DAO concreto.
     * Debe invocarse en el @PostConstruct de la subclase.
     * Reemplaza la llamada a cargarDatos() que usan los modelos de catálogo.
     */
    protected void inicializarLazyModel() {
        this.lazyModel = new GenericLazyDataModel<>(getDAO());
    }

    /**
     * No-op intencionalmente.
     * Sobreescrito para garantizar que nunca se ejecute un findAll()
     * sobre entidades transaccionales de alto volumen.
     */
    @Override
    public void cargarDatos() {
        // No-op: la paginación la controla GenericLazyDataModel
    }

    /**
     * @return el modelo lazy conectado al DAO
     */
    public GenericLazyDataModel<T> getLazyModel() {
        return lazyModel;
    }
}

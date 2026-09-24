package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import java.util.List;
import java.util.Map;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;

/**
 * DataModel genérico para paginación server-side y búsqueda con PrimeFaces.
 * Delega al DAO para cargar solo la página visible y procesar el filtro global.
 *
 * @param <T> tipo de la entidad JPA
 */
public class GenericLazyDataModel<T> extends LazyDataModel<T> {

    private static final long serialVersionUID = 1L;

    private final DAOInterface<T, ?> dao;

    /**
     * Texto de búsqueda global, enlazado desde el p:inputText
     * del composite crudTransaccional.xhtml.
     * Cuando es null o vacío, no se aplica filtro.
     */
    private String filtroGlobal;

    /**
     * @param dao el DAO que provee findRange() y count();
     *            no debe ser null
     */
    public GenericLazyDataModel(DAOInterface<T, ?> dao) {
        if (dao == null) {
            throw new IllegalArgumentException("El DAO no puede ser null");
        }
        this.dao = dao;
    }

    /**
     * Retorna la cantidad total de registros que coinciden con el filtro global.
     * PrimeFaces usa este valor para calcular el número de páginas del paginador.
     */
    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        return (int) dao.count(filtroGlobal);
    }

    /**
     * Carga únicamente la página solicitada, aplicando el filtro global
     * si está definido.
     * 
     * Los parámetros sortBy y filterBy de PrimeFaces se
     * reciben pero no se delegan al DAO (requeriría un findRange con
     * más parámetros). Se pueden implementar progresivamente.
     * 
     */
    @Override
    public List<T> load(int first, int pageSize,
                        Map<String, SortMeta> sortBy,
                        Map<String, FilterMeta> filterBy) {
        return dao.findRange(first, pageSize, filtroGlobal);
    }

    /**
     * Clave de fila para PrimeFaces (selection + lazy).
     * Debe coincidir con rowKey="#{registro.hashCode()}" en los composites CRUD.
     */
    @Override
    public String getRowKey(T object) {
        if (object == null) {
            return null;
        }
        return String.valueOf(object.hashCode());
    }

    /**
     * Resuelve la entidad a partir de la clave de fila generada por getRowKey.
     * Busca en la página actualmente cargada (wrapped data).
     */
    @Override
    public T getRowData(String rowKey) {
        if (rowKey == null || rowKey.isEmpty()) {
            return null;
        }
        List<T> data = getWrappedData();
        if (data != null) {
            for (T item : data) {
                if (item != null && rowKey.equals(String.valueOf(item.hashCode()))) {
                    return item;
                }
            }
        }
        return null;
    }

    // ─── Filtro global ───────────────────────────────────────────────

    public String getFiltroGlobal() {
        return filtroGlobal;
    }

    public void setFiltroGlobal(String filtroGlobal) {
        this.filtroGlobal = filtroGlobal;
    }
}

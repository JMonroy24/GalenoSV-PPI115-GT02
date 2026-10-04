package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.FiltroConsulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.OrdenConsulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.IdentificableEntity;

/** Paginación, orden y filtros de PrimeFaces procesados en la base de datos. */
public class GenericLazyDataModel<T> extends LazyDataModel<T> {

    private static final long serialVersionUID = 1L;
    private final DAOInterface<T, ?> dao;
    private String filtroGlobal;

    public GenericLazyDataModel(DAOInterface<T, ?> dao) {
        if (dao == null) {
            throw new IllegalArgumentException("El DAO no puede ser null");
        }
        this.dao = dao;
    }

    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        return Math.toIntExact(dao.count(filtroGlobal, filtros(filterBy)));
    }

    @Override
    public List<T> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        List<OrdenConsulta> orden = sortBy == null ? List.of() : sortBy.values().stream()
                .filter(meta -> meta.getField() != null && meta.getOrder() != SortOrder.UNSORTED)
                .sorted(Comparator.comparingInt(SortMeta::getPriority))
                .map(meta -> new OrdenConsulta(meta.getField(), meta.getOrder() == SortOrder.ASCENDING))
                .toList();
        return dao.findRange(first, pageSize, filtroGlobal, orden, filtros(filterBy));
    }

    private List<FiltroConsulta> filtros(Map<String, FilterMeta> filterBy) {
        if (filterBy == null) {
            return List.of();
        }
        return filterBy.values().stream()
                .filter(meta -> meta.getField() != null && meta.getFilterValue() != null)
                .map(meta -> new FiltroConsulta(meta.getField(), meta.getFilterValue(),
                        meta.getMatchMode() == null ? "CONTAINS" : meta.getMatchMode().name()))
                .toList();
    }

    @Override
    public String getRowKey(T object) {
        return object instanceof IdentificableEntity entity ? entity.getIdKey() : null;
    }

    @Override
    public T getRowData(String rowKey) {
        if (rowKey == null || rowKey.isEmpty()) {
            return null;
        }
        List<T> data = getWrappedData();
        if (data != null) {
            for (T item : data) {
                if (item instanceof IdentificableEntity entity && rowKey.equals(entity.getIdKey())) {
                    return item;
                }
            }
        }
        return null;
    }

    public String getFiltroGlobal() {
        return filtroGlobal;
    }

    public void setFiltroGlobal(String filtroGlobal) {
        this.filtroGlobal = DefaultDAO.normalizarFiltro(filtroGlobal);
    }
}

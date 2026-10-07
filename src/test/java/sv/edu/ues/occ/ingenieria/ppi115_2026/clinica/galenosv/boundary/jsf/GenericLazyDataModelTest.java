package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.MatchMode;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.FiltroConsulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.OrdenConsulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GenericLazyDataModelTest {
    @SuppressWarnings("unchecked")
    private final DAOInterface<Persona, UUID> dao = mock(DAOInterface.class);
    private final GenericLazyDataModel<Persona> model = new GenericLazyDataModel<>(dao);

    @Test
    void cargaTraduceOrdenYFiltrosDeColumna() {
        SortMeta orden = mock(SortMeta.class);
        when(orden.getField()).thenReturn("nombres");
        when(orden.getOrder()).thenReturn(SortOrder.DESCENDING);
        FilterMeta filtro = mock(FilterMeta.class);
        when(filtro.getField()).thenReturn("apellidos");
        when(filtro.getFilterValue()).thenReturn("Pérez");
        when(filtro.getMatchMode()).thenReturn(MatchMode.STARTS_WITH);
        model.setFiltroGlobal(" Ana ");
        model.load(10, 5, Map.of("nombres", orden), Map.of("apellidos", filtro));
        model.count(Map.of("apellidos", filtro));
        List<FiltroConsulta> esperado = List.of(new FiltroConsulta("apellidos", "Pérez", "STARTS_WITH"));
        verify(dao).findRange(10, 5, "Ana", List.of(new OrdenConsulta("nombres", false)), esperado);
        verify(dao).count("Ana", esperado);
    }

    @Test
    void conteoGrandeNoSeConvierteEnNegativo() {
        when(dao.count(null, List.of())).thenReturn((long) Integer.MAX_VALUE + 1);
        assertThrows(ArithmeticException.class, () -> model.count(null));
    }

    @Test
    void filtroVacioSeConvierteANullYTextoSeLimita() {
        model.setFiltroGlobal("  ");
        assertNull(model.getFiltroGlobal());
        model.setFiltroGlobal("x".repeat(120));
        assertEquals(100, model.getFiltroGlobal().length());
    }

    @Test
    void identificaFilaConUuidPersistente() {
        Persona persona = new Persona(UUID.randomUUID());
        model.setWrappedData(List.of(persona));
        assertEquals(persona.getIdPersona().toString(), model.getRowKey(persona));
        assertSame(persona, model.getRowData(persona.getIdKey()));
        assertNull(model.getRowData(UUID.randomUUID().toString()));
        assertNull(model.getRowData(null));
        assertNull(model.getRowKey(null));
    }

    @Test
    void requiereDao() {
        assertThrows(IllegalArgumentException.class, () -> new GenericLazyDataModel<>(null));
    }
}

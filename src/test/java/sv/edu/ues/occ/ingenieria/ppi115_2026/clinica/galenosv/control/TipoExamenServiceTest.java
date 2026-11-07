package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoExamenServiceTest {

    @Mock
    private TipoExamenDAO tipoExamenDAO;

    @InjectMocks
    private TipoExamenService tipoExamenService;

    @BeforeEach
    void setUp() {
        tipoExamenService.setTipoExamenDAO(tipoExamenDAO);
    }

    @Test
    void getDAORetornaTipoExamenDAO() {
        assertSame(tipoExamenDAO, tipoExamenService.getDAO());
        assertSame(tipoExamenDAO, tipoExamenService.getTipoExamenDAO());
    }

    @Test
    void crearValidaYPersiste() {
        TipoExamen te = new TipoExamen(UUID.randomUUID());
        te.setNombre("  Hemograma  ");
        when(tipoExamenDAO.existePorCampo("nombre", "Hemograma", te.getIdTipoExamen())).thenReturn(false);

        tipoExamenService.crear(te);

        assertEquals("Hemograma", te.getNombre());
        verify(tipoExamenDAO).create(te);
    }

    @Test
    void crearRechazaNombreVacio() {
        TipoExamen te = new TipoExamen(UUID.randomUUID());
        te.setNombre("   ");

        assertThrows(ValidacionNegocioException.class, () -> tipoExamenService.crear(te));
        verify(tipoExamenDAO, never()).create(any());
    }

    @Test
    void crearRechazaNombreDuplicado() {
        TipoExamen te = new TipoExamen(UUID.randomUUID());
        te.setNombre("Hemograma");
        when(tipoExamenDAO.existePorCampo("nombre", "Hemograma", te.getIdTipoExamen())).thenReturn(true);

        assertThrows(ValidacionNegocioException.class, () -> tipoExamenService.crear(te));
        verify(tipoExamenDAO, never()).create(any());
    }

    @Test
    void actualizarValidaYActualiza() {
        TipoExamen te = new TipoExamen(UUID.randomUUID());
        te.setNombre("Hemograma");
        when(tipoExamenDAO.existePorCampo("nombre", "Hemograma", te.getIdTipoExamen())).thenReturn(false);

        tipoExamenService.actualizar(te);

        verify(tipoExamenDAO).update(te);
    }

    @Test
    void eliminarDelegaEnDAO() {
        TipoExamen te = new TipoExamen(UUID.randomUUID());
        tipoExamenService.eliminar(te);

        verify(tipoExamenDAO).delete(te);
    }

    @Test
    void getTiposActivosYFindAllActivosDeleganEnDAO() {
        List<TipoExamen> lista = List.of(new TipoExamen(UUID.randomUUID()));
        when(tipoExamenDAO.findAllActivos()).thenReturn(lista);

        assertSame(lista, tipoExamenService.getTiposActivos());
        assertSame(lista, tipoExamenService.findAllActivos());
        verify(tipoExamenDAO, times(2)).findAllActivos();
    }
}

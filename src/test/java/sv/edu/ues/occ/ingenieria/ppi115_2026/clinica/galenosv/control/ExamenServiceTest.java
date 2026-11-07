package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Examen;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenTipoExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceTest {

    @Mock
    private ExamenDAO examenDAO;

    @Mock
    private ExamenTipoExamenDAO examenTipoExamenDAO;

    @Mock
    private AsignacionService asignacionService;

    @InjectMocks
    private ExamenService examenService;

    @BeforeEach
    void setUp() {
        examenService.setExamenDAO(examenDAO);
        examenService.setExamenTipoExamenDAO(examenTipoExamenDAO);
        examenService.setAsignacionService(asignacionService);
    }

    @Test
    void getDAORetornaExamenDAO() {
        assertSame(examenDAO, examenService.getDAO());
        assertSame(examenDAO, examenService.getExamenDAO());
        assertSame(examenTipoExamenDAO, examenService.getExamenTipoExamenDAO());
        assertSame(asignacionService, examenService.getAsignacionService());
    }

    @Test
    void crearValidaYPersiste() {
        Examen e = new Examen(UUID.randomUUID());
        e.setNombre("  Glucosa  ");
        when(examenDAO.existePorCampo("nombre", "Glucosa", e.getIdExamen())).thenReturn(false);

        examenService.crear(e);

        assertEquals("Glucosa", e.getNombre());
        verify(examenDAO).create(e);
    }

    @Test
    void crearRechazaNombreVacio() {
        Examen e = new Examen(UUID.randomUUID());
        e.setNombre("   ");

        assertThrows(ValidacionNegocioException.class, () -> examenService.crear(e));
        verify(examenDAO, never()).create(any());
    }

    @Test
    void crearRechazaNombreDuplicado() {
        Examen e = new Examen(UUID.randomUUID());
        e.setNombre("Glucosa");
        when(examenDAO.existePorCampo("nombre", "Glucosa", e.getIdExamen())).thenReturn(true);

        assertThrows(ValidacionNegocioException.class, () -> examenService.crear(e));
        verify(examenDAO, never()).create(any());
    }

    @Test
    void actualizarValidaYActualiza() {
        Examen e = new Examen(UUID.randomUUID());
        e.setNombre("Glucosa");
        when(examenDAO.existePorCampo("nombre", "Glucosa", e.getIdExamen())).thenReturn(false);

        examenService.actualizar(e);

        verify(examenDAO).update(e);
    }

    @Test
    void eliminarDelegaEnDAO() {
        Examen e = new Examen(UUID.randomUUID());
        examenService.eliminar(e);

        verify(examenDAO).delete(e);
    }

    @Test
    void getActivosYFindAllActivosDeleganEnDAO() {
        List<Examen> lista = List.of(new Examen(UUID.randomUUID()));
        when(examenDAO.findAllActivos()).thenReturn(lista);

        assertSame(lista, examenService.getActivos());
        assertSame(lista, examenService.findAllActivos());
        verify(examenDAO, times(2)).findAllActivos();
    }

    @Test
    void findTiposByExamenDelegaEnDAO() {
        UUID id = UUID.randomUUID();
        List<ExamenTipoExamen> lista = List.of(new ExamenTipoExamen(UUID.randomUUID()));
        when(examenTipoExamenDAO.findByExamen(id)).thenReturn(lista);

        assertSame(lista, examenService.findTiposByExamen(id));
        assertTrue(examenService.findTiposByExamen(null).isEmpty());
    }

    @Test
    void agregarTipoDelegaEnAsignacionService() {
        ExamenTipoExamen ete = new ExamenTipoExamen(UUID.randomUUID());
        examenService.agregarTipo(ete);

        verify(asignacionService).guardarTipo(ete, true);
    }

    @Test
    void quitarTipoDelegaEnDAO() {
        ExamenTipoExamen ete = new ExamenTipoExamen(UUID.randomUUID());
        examenService.quitarTipo(ete);

        verify(examenTipoExamenDAO).delete(ete);
    }

    @Test
    void crearExamenConTipoValidaYDelega() {
        Examen e = new Examen(UUID.randomUUID());
        e.setNombre("Glucosa");
        ExamenTipoExamen ete = new ExamenTipoExamen(UUID.randomUUID());
        when(examenDAO.existePorCampo("nombre", "Glucosa", e.getIdExamen())).thenReturn(false);

        examenService.crearExamenConTipo(e, ete);

        verify(asignacionService).crearExamenConTipo(e, ete);
    }
}

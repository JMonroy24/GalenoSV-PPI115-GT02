package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenTipoExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenTipoExamenServiceTest {

    @Mock
    private ExamenTipoExamenDAO examenTipoExamenDAO;

    @Mock
    private AsignacionService asignacionService;

    @InjectMocks
    private ExamenTipoExamenService examenTipoExamenService;

    @BeforeEach
    void setUp() {
        examenTipoExamenService.setExamenTipoExamenDAO(examenTipoExamenDAO);
        examenTipoExamenService.setAsignacionService(asignacionService);
    }

    @Test
    void getDAORetornaExamenTipoExamenDAO() {
        assertSame(examenTipoExamenDAO, examenTipoExamenService.getDAO());
        assertSame(examenTipoExamenDAO, examenTipoExamenService.getExamenTipoExamenDAO());
        assertSame(asignacionService, examenTipoExamenService.getAsignacionService());
    }

    @Test
    void crearDelegaEnAsignacionService() {
        ExamenTipoExamen ete = new ExamenTipoExamen(UUID.randomUUID());
        examenTipoExamenService.crear(ete);

        verify(asignacionService).guardarTipo(ete, true);
    }

    @Test
    void actualizarDelegaEnAsignacionService() {
        ExamenTipoExamen ete = new ExamenTipoExamen(UUID.randomUUID());
        examenTipoExamenService.actualizar(ete);

        verify(asignacionService).guardarTipo(ete, false);
    }

    @Test
    void crearAsignaFechaDeCreacionCuandoNoViene() {
        ExamenTipoExamen ete = new ExamenTipoExamen(UUID.randomUUID());

        examenTipoExamenService.crear(ete);

        assertNotNull(ete.getFechaCreacion());
        verify(asignacionService).guardarTipo(ete, true);
    }

    @Test
    void actualizarNoModificaFechaDeCreacion() {
        Date fechaOriginal = new Date(1600000000000L);
        ExamenTipoExamen ete = new ExamenTipoExamen(UUID.randomUUID());
        ete.setFechaCreacion(fechaOriginal);

        examenTipoExamenService.actualizar(ete);

        assertEquals(fechaOriginal, ete.getFechaCreacion());
        verify(asignacionService).guardarTipo(ete, false);
    }

    @Test
    void findByExamenDelegaEnDAO() {
        UUID id = UUID.randomUUID();
        List<ExamenTipoExamen> lista = List.of(new ExamenTipoExamen(UUID.randomUUID()));
        when(examenTipoExamenDAO.findByExamen(id)).thenReturn(lista);

        assertSame(lista, examenTipoExamenService.findByExamen(id));
        verify(examenTipoExamenDAO).findByExamen(id);
    }
}

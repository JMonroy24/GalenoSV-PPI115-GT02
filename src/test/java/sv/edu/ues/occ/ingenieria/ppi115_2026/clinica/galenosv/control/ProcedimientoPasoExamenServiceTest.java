package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcedimientoPasoExamenServiceTest {

    @Mock
    private ProcedimientoPasoExamenDAO procedimientoPasoExamenDAO;

    @Mock
    private AsignacionService asignacionService;

    @InjectMocks
    private ProcedimientoPasoExamenService procedimientoPasoExamenService;

    @BeforeEach
    void setUp() {
        procedimientoPasoExamenService.setProcedimientoPasoExamenDAO(procedimientoPasoExamenDAO);
        procedimientoPasoExamenService.setAsignacionService(asignacionService);
    }

    @Test
    void crearAsignaFechaDeCreacionCuandoNoViene() {
        ProcedimientoPasoExamen nuevo = new ProcedimientoPasoExamen(UUID.randomUUID());

        procedimientoPasoExamenService.crear(nuevo);

        assertNotNull(nuevo.getFechaCreacion());
        verify(asignacionService).guardarExamen(nuevo, true);
    }

    @Test
    void actualizarNoModificaFechaDeCreacion() {
        Date fechaOriginal = new Date(1600000000000L);
        ProcedimientoPasoExamen existente = new ProcedimientoPasoExamen(UUID.randomUUID());
        existente.setFechaCreacion(fechaOriginal);

        procedimientoPasoExamenService.actualizar(existente);

        assertEquals(fechaOriginal, existente.getFechaCreacion());
        verify(asignacionService).guardarExamen(existente, false);
    }
}

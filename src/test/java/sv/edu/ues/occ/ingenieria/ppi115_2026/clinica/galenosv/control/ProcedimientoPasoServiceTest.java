package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPaso;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcedimientoPasoServiceTest {

    @Mock
    private ProcedimientoPasoDAO procedimientoPasoDAO;

    @Mock
    private AsignacionService asignacionService;

    private ProcedimientoPasoService service;

    @BeforeEach
    void setUp() {
        service = new ProcedimientoPasoService(procedimientoPasoDAO, asignacionService);
    }

    @Test
    void eliminarDelegaEnAsignacionServiceEliminarPasoYNuncaLlamaDaoDelete() {
        ProcedimientoPaso paso = new ProcedimientoPaso(UUID.randomUUID());
        service.eliminar(paso);
        verify(asignacionService).eliminarPaso(paso);
        verify(procedimientoPasoDAO, never()).delete(any());
    }

    @Test
    void crearConNombreEnBlancoLanzaValidacionNegocioException() {
        ProcedimientoPaso paso = new ProcedimientoPaso(UUID.randomUUID());
        paso.setNombre("   ");
        assertThrows(ValidacionNegocioException.class, () -> service.crear(paso));
        verify(procedimientoPasoDAO, never()).create(any());
    }

    @Test
    void actualizarConNombreConEspaciosLoRecortaYLlamaDaoUpdate() {
        ProcedimientoPaso paso = new ProcedimientoPaso(UUID.randomUUID());
        paso.setNombre(" X ");
        service.actualizar(paso);
        assertEquals("X", paso.getNombre());
        verify(procedimientoPasoDAO).update(paso);
    }
}

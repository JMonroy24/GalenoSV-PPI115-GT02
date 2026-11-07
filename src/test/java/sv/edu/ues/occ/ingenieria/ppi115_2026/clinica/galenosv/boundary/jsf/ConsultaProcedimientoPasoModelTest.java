package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimientoPaso;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
public class ConsultaProcedimientoPasoModelTest {

    @Mock
    private ConsultaProcedimientoPasoService consultaProcedimientoPasoService;

    @InjectMocks
    private ConsultaProcedimientoPasoModel consultaProcedimientoPasoModel;

    @BeforeEach
    public void setUp() {
        consultaProcedimientoPasoModel.setConsultaProcedimientoPasoService(consultaProcedimientoPasoService);
    }

    @Test
    public void testInitYCargarDatos() {
        consultaProcedimientoPasoModel.init();
        assertNotNull(consultaProcedimientoPasoModel.getLazyModel());
        assertNull(consultaProcedimientoPasoModel.getRegistros());

    }

    @Test
    public void testPrepararNuevo() {
        consultaProcedimientoPasoModel.prepararNuevo();

        assertNotNull(consultaProcedimientoPasoModel.getRegistroActual());
        assertEquals(Estado.CREAR, consultaProcedimientoPasoModel.getEstado());
        assertTrue(consultaProcedimientoPasoModel.isEstadoCrear());
    }

    @Test
    public void testSeleccionar() {
        ConsultaProcedimientoPaso cpp = new ConsultaProcedimientoPaso(UUID.randomUUID());
        consultaProcedimientoPasoModel.seleccionar(cpp);

        assertEquals(cpp, consultaProcedimientoPasoModel.getRegistroActual());
        assertEquals(Estado.MODIFICAR, consultaProcedimientoPasoModel.getEstado());
        assertTrue(consultaProcedimientoPasoModel.isEstadoModificar());
    }

    @Test
    public void testCancelar() {
        consultaProcedimientoPasoModel.prepararNuevo();
        consultaProcedimientoPasoModel.cancelar();

        assertNull(consultaProcedimientoPasoModel.getRegistroActual());
        assertEquals(Estado.NINGUNO, consultaProcedimientoPasoModel.getEstado());
        assertTrue(consultaProcedimientoPasoModel.isEstadoNinguno());
    }

    @Test
    public void testGuardarCrear() {
        consultaProcedimientoPasoModel.prepararNuevo();
        consultaProcedimientoPasoModel.getRegistroActual().setEstado("COMPLETADO");

        consultaProcedimientoPasoModel.getRegistroActual().setEstado("Registrado");
        consultaProcedimientoPasoModel.guardar();

        verify(consultaProcedimientoPasoService).create(any(ConsultaProcedimientoPaso.class));
        assertEquals(Estado.NINGUNO, consultaProcedimientoPasoModel.getEstado());
        assertNull(consultaProcedimientoPasoModel.getRegistroActual());
    }

    @Test
    public void testGuardarModificar() {
        ConsultaProcedimientoPaso cpp = new ConsultaProcedimientoPaso(UUID.randomUUID());
        consultaProcedimientoPasoModel.seleccionar(cpp);

        consultaProcedimientoPasoModel.getRegistroActual().setEstado("Registrado");
        consultaProcedimientoPasoModel.guardar();

        verify(consultaProcedimientoPasoService).update(cpp);
        assertEquals(Estado.NINGUNO, consultaProcedimientoPasoModel.getEstado());
    }

    @Test
    public void testEliminar() {
        ConsultaProcedimientoPaso cpp = new ConsultaProcedimientoPaso(UUID.randomUUID());
        consultaProcedimientoPasoModel.eliminar(cpp);

        verify(consultaProcedimientoPasoService).delete(cpp);
    }

    @Test
    public void testGettersAndSetters() {
        consultaProcedimientoPasoModel.init();
        assertEquals(consultaProcedimientoPasoService, consultaProcedimientoPasoModel.getDAO());
        assertEquals(consultaProcedimientoPasoService, consultaProcedimientoPasoModel.getConsultaProcedimientoPasoService());
        assertNotNull(consultaProcedimientoPasoModel.crearNuevoRegistro());
        assertNotNull(consultaProcedimientoPasoModel.getLazyModel());
    }
}

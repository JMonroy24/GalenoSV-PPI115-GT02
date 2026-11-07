package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimiento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
public class ConsultaProcedimientoModelTest {

    @Mock
    private ConsultaProcedimientoService consultaProcedimientoService;

    @InjectMocks
    private ConsultaProcedimientoModel consultaProcedimientoModel;

    private org.mockito.MockedStatic<jakarta.faces.context.FacesContext> faces;

    @org.junit.jupiter.api.AfterEach
    void closeFaces() { faces.close(); }

    @BeforeEach
    public void setUp() {
        faces = mockStatic(jakarta.faces.context.FacesContext.class);
        consultaProcedimientoModel.setConsultaProcedimientoService(consultaProcedimientoService);
    }

    @Test
    public void testInitYCargarDatos() {
        consultaProcedimientoModel.init();
        assertNotNull(consultaProcedimientoModel.getLazyModel());
        assertNull(consultaProcedimientoModel.getRegistros());

    }

    @Test
    public void testPrepararNuevo() {
        consultaProcedimientoModel.prepararNuevo();

        assertNotNull(consultaProcedimientoModel.getRegistroActual());
        assertEquals(Estado.CREAR, consultaProcedimientoModel.getEstado());
        assertTrue(consultaProcedimientoModel.isEstadoCrear());
    }

    @Test
    public void testSeleccionar() {
        ConsultaProcedimiento cp = new ConsultaProcedimiento(UUID.randomUUID());
        consultaProcedimientoModel.seleccionar(cp);

        assertEquals(cp, consultaProcedimientoModel.getRegistroActual());
        assertEquals(Estado.MODIFICAR, consultaProcedimientoModel.getEstado());
        assertTrue(consultaProcedimientoModel.isEstadoModificar());
    }

    @Test
    public void testCancelar() {
        consultaProcedimientoModel.prepararNuevo();
        consultaProcedimientoModel.cancelar();

        assertNull(consultaProcedimientoModel.getRegistroActual());
        assertEquals(Estado.NINGUNO, consultaProcedimientoModel.getEstado());
        assertTrue(consultaProcedimientoModel.isEstadoNinguno());
    }

    @Test
    public void testGuardarCrear() {
        consultaProcedimientoModel.prepararNuevo();
        consultaProcedimientoModel.getRegistroActual().setObservaciones("Procedimiento de prueba");

        consultaProcedimientoModel.guardar();

        verify(consultaProcedimientoService).create(any(ConsultaProcedimiento.class));
        assertEquals(Estado.NINGUNO, consultaProcedimientoModel.getEstado());
        assertNull(consultaProcedimientoModel.getRegistroActual());
    }

    @Test
    public void testGuardarModificar() {
        ConsultaProcedimiento cp = new ConsultaProcedimiento(UUID.randomUUID());
        consultaProcedimientoModel.seleccionar(cp);

        consultaProcedimientoModel.guardar();

        verify(consultaProcedimientoService).update(cp);
        assertEquals(Estado.NINGUNO, consultaProcedimientoModel.getEstado());
    }

    @Test
    public void testEliminar() {
        ConsultaProcedimiento cp = new ConsultaProcedimiento(UUID.randomUUID());
        consultaProcedimientoModel.eliminar(cp);

        verify(consultaProcedimientoService).delete(cp);
    }

    @Test
    public void testGettersAndSetters() {
        consultaProcedimientoModel.init();
        assertEquals(consultaProcedimientoService, consultaProcedimientoModel.getDAO());
        assertEquals(consultaProcedimientoService, consultaProcedimientoModel.getConsultaProcedimientoService());
        assertNotNull(consultaProcedimientoModel.crearNuevoRegistro());
        assertNotNull(consultaProcedimientoModel.getLazyModel());
    }
}

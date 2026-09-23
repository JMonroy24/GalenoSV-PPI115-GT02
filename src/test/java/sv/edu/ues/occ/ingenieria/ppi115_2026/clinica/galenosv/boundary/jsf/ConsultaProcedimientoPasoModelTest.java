package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoPasoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimientoPaso;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConsultaProcedimientoPasoModelTest {

    @Mock
    private ConsultaProcedimientoPasoDAO consultaProcedimientoPasoDAO;

    @InjectMocks
    private ConsultaProcedimientoPasoModel consultaProcedimientoPasoModel;

    @BeforeEach
    public void setUp() {
        consultaProcedimientoPasoModel.setConsultaProcedimientoPasoDAO(consultaProcedimientoPasoDAO);
    }

    @Test
    public void testInitYCargarDatos() {
        consultaProcedimientoPasoModel.init();

        // ModelTransaccional inicializa un LazyDataModel en lugar de cargar
        // todos los registros en memoria con findAll(); verificamos el lazy model.
        assertNotNull(consultaProcedimientoPasoModel.getLazyModel());
        verifyNoInteractions(consultaProcedimientoPasoDAO);
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

        consultaProcedimientoPasoModel.guardar();

        verify(consultaProcedimientoPasoDAO).create(any(ConsultaProcedimientoPaso.class));
        assertEquals(Estado.NINGUNO, consultaProcedimientoPasoModel.getEstado());
        assertNull(consultaProcedimientoPasoModel.getRegistroActual());
    }

    @Test
    public void testGuardarModificar() {
        ConsultaProcedimientoPaso cpp = new ConsultaProcedimientoPaso(UUID.randomUUID());
        consultaProcedimientoPasoModel.seleccionar(cpp);

        consultaProcedimientoPasoModel.guardar();

        verify(consultaProcedimientoPasoDAO).update(cpp);
        assertEquals(Estado.NINGUNO, consultaProcedimientoPasoModel.getEstado());
    }

    @Test
    public void testEliminar() {
        ConsultaProcedimientoPaso cpp = new ConsultaProcedimientoPaso(UUID.randomUUID());
        consultaProcedimientoPasoModel.eliminar(cpp);

        verify(consultaProcedimientoPasoDAO).delete(cpp);
    }

    @Test
    public void testGettersAndSetters() {
        consultaProcedimientoPasoModel.init();
        assertEquals(consultaProcedimientoPasoDAO, consultaProcedimientoPasoModel.getDAO());
        assertEquals(consultaProcedimientoPasoDAO, consultaProcedimientoPasoModel.getConsultaProcedimientoPasoDAO());
        assertNotNull(consultaProcedimientoPasoModel.crearNuevoRegistro());
        assertNotNull(consultaProcedimientoPasoModel.getLazyModel());
    }
}

package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimiento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConsultaProcedimientoModelTest {

    @Mock
    private ConsultaProcedimientoDAO consultaProcedimientoDAO;

    @InjectMocks
    private ConsultaProcedimientoModel consultaProcedimientoModel;

    @BeforeEach
    public void setUp() {
        consultaProcedimientoModel.setConsultaProcedimientoDAO(consultaProcedimientoDAO);
    }

    @Test
    public void testInitYCargarDatos() {
        ConsultaProcedimiento cp = new ConsultaProcedimiento(UUID.randomUUID());
        when(consultaProcedimientoDAO.findAll()).thenReturn(List.of(cp));

        consultaProcedimientoModel.init();

        assertNotNull(consultaProcedimientoModel.getRegistros());
        assertEquals(1, consultaProcedimientoModel.getRegistros().size());
        verify(consultaProcedimientoDAO).findAll();
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
        when(consultaProcedimientoDAO.findAll()).thenReturn(Collections.emptyList());

        consultaProcedimientoModel.prepararNuevo();
        consultaProcedimientoModel.getRegistroActual().setObservaciones("Procedimiento de prueba");

        consultaProcedimientoModel.guardar();

        verify(consultaProcedimientoDAO).create(any(ConsultaProcedimiento.class));
        assertEquals(Estado.NINGUNO, consultaProcedimientoModel.getEstado());
        assertNull(consultaProcedimientoModel.getRegistroActual());
    }

    @Test
    public void testGuardarModificar() {
        when(consultaProcedimientoDAO.findAll()).thenReturn(Collections.emptyList());

        ConsultaProcedimiento cp = new ConsultaProcedimiento(UUID.randomUUID());
        consultaProcedimientoModel.seleccionar(cp);

        consultaProcedimientoModel.guardar();

        verify(consultaProcedimientoDAO).update(cp);
        assertEquals(Estado.NINGUNO, consultaProcedimientoModel.getEstado());
    }

    @Test
    public void testEliminar() {
        when(consultaProcedimientoDAO.findAll()).thenReturn(Collections.emptyList());

        ConsultaProcedimiento cp = new ConsultaProcedimiento(UUID.randomUUID());
        consultaProcedimientoModel.eliminar(cp);

        verify(consultaProcedimientoDAO).delete(cp);
    }

    @Test
    public void testGettersAndSetters() {
        assertEquals(consultaProcedimientoDAO, consultaProcedimientoModel.getDAO());
        assertEquals(consultaProcedimientoDAO, consultaProcedimientoModel.getConsultaProcedimientoDAO());
        assertNotNull(consultaProcedimientoModel.crearNuevoRegistro());
    }
}

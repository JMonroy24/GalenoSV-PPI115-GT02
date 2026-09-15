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
        ConsultaProcedimientoPaso cpp = new ConsultaProcedimientoPaso(UUID.randomUUID());
        when(consultaProcedimientoPasoDAO.findAll()).thenReturn(List.of(cpp));

        consultaProcedimientoPasoModel.init();

        assertNotNull(consultaProcedimientoPasoModel.getRegistros());
        assertEquals(1, consultaProcedimientoPasoModel.getRegistros().size());
        verify(consultaProcedimientoPasoDAO).findAll();
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
        when(consultaProcedimientoPasoDAO.findAll()).thenReturn(Collections.emptyList());

        consultaProcedimientoPasoModel.prepararNuevo();
        consultaProcedimientoPasoModel.getRegistroActual().setEstado("COMPLETADO");

        consultaProcedimientoPasoModel.guardar();

        verify(consultaProcedimientoPasoDAO).create(any(ConsultaProcedimientoPaso.class));
        assertEquals(Estado.NINGUNO, consultaProcedimientoPasoModel.getEstado());
        assertNull(consultaProcedimientoPasoModel.getRegistroActual());
    }

    @Test
    public void testGuardarModificar() {
        when(consultaProcedimientoPasoDAO.findAll()).thenReturn(Collections.emptyList());

        ConsultaProcedimientoPaso cpp = new ConsultaProcedimientoPaso(UUID.randomUUID());
        consultaProcedimientoPasoModel.seleccionar(cpp);

        consultaProcedimientoPasoModel.guardar();

        verify(consultaProcedimientoPasoDAO).update(cpp);
        assertEquals(Estado.NINGUNO, consultaProcedimientoPasoModel.getEstado());
    }

    @Test
    public void testEliminar() {
        when(consultaProcedimientoPasoDAO.findAll()).thenReturn(Collections.emptyList());

        ConsultaProcedimientoPaso cpp = new ConsultaProcedimientoPaso(UUID.randomUUID());
        consultaProcedimientoPasoModel.eliminar(cpp);

        verify(consultaProcedimientoPasoDAO).delete(cpp);
    }

    @Test
    public void testGettersAndSetters() {
        assertEquals(consultaProcedimientoPasoDAO, consultaProcedimientoPasoModel.getDAO());
        assertEquals(consultaProcedimientoPasoDAO, consultaProcedimientoPasoModel.getConsultaProcedimientoPasoDAO());
        assertNotNull(consultaProcedimientoPasoModel.crearNuevoRegistro());
    }
}

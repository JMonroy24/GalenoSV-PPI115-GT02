package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.MedioContactoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.MedioContacto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MedioContactoModelTest {

    @Mock
    private MedioContactoDAO medioContactoDAO;

    @InjectMocks
    private MedioContactoModel medioContactoModel;

    @BeforeEach
    public void setUp() {
        medioContactoModel.setMedioContactoDAO(medioContactoDAO);
    }

    @Test
    public void testInitYCargarDatos() {
        medioContactoModel.init();

        // ModelTransaccional inicializa un LazyDataModel en lugar de cargar
        // todos los registros en memoria con findAll(); verificamos el lazy model.
        assertNotNull(medioContactoModel.getLazyModel());
        verifyNoInteractions(medioContactoDAO);
    }

    @Test
    public void testPrepararNuevo() {
        medioContactoModel.prepararNuevo();

        assertNotNull(medioContactoModel.getRegistroActual());
        assertEquals(Estado.CREAR, medioContactoModel.getEstado());
        assertTrue(medioContactoModel.isEstadoCrear());
    }

    @Test
    public void testSeleccionar() {
        MedioContacto mc = new MedioContacto(UUID.randomUUID());
        medioContactoModel.seleccionar(mc);

        assertEquals(mc, medioContactoModel.getRegistroActual());
        assertEquals(Estado.MODIFICAR, medioContactoModel.getEstado());
        assertTrue(medioContactoModel.isEstadoModificar());
    }

    @Test
    public void testCancelar() {
        medioContactoModel.prepararNuevo();
        medioContactoModel.cancelar();

        assertNull(medioContactoModel.getRegistroActual());
        assertEquals(Estado.NINGUNO, medioContactoModel.getEstado());
        assertTrue(medioContactoModel.isEstadoNinguno());
    }

    @Test
    public void testGuardarCrear() {
        medioContactoModel.prepararNuevo();
        medioContactoModel.getRegistroActual().setValor("test@email.com");

        medioContactoModel.guardar();

        verify(medioContactoDAO).create(any(MedioContacto.class));
        assertEquals(Estado.NINGUNO, medioContactoModel.getEstado());
        assertNull(medioContactoModel.getRegistroActual());
    }

    @Test
    public void testGuardarModificar() {
        MedioContacto mc = new MedioContacto(UUID.randomUUID());
        medioContactoModel.seleccionar(mc);

        medioContactoModel.guardar();

        verify(medioContactoDAO).update(mc);
        assertEquals(Estado.NINGUNO, medioContactoModel.getEstado());
    }

    @Test
    public void testEliminar() {
        MedioContacto mc = new MedioContacto(UUID.randomUUID());
        medioContactoModel.eliminar(mc);

        verify(medioContactoDAO).delete(mc);
    }

    @Test
    public void testGettersAndSetters() {
        medioContactoModel.init();
        assertEquals(medioContactoDAO, medioContactoModel.getDAO());
        assertEquals(medioContactoDAO, medioContactoModel.getMedioContactoDAO());
        assertNotNull(medioContactoModel.crearNuevoRegistro());
        assertNotNull(medioContactoModel.getLazyModel());
    }
}

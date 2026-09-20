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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.RolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Rol;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RolModelTest {

    @Mock
    private RolDAO rolDAO;

    @InjectMocks
    private RolModel rolModel;

    @BeforeEach
    public void setUp() {
        rolModel.setRolDAO(rolDAO);
    }

    @Test
    public void testInitYCargarDatos() {
        Rol r = new Rol(UUID.randomUUID());
        when(rolDAO.findAll()).thenReturn(List.of(r));

        rolModel.init();

        assertNotNull(rolModel.getRegistros());
        assertEquals(1, rolModel.getRegistros().size());
        verify(rolDAO).findAll();
    }

    @Test
    public void testPrepararNuevo() {
        rolModel.prepararNuevo();

        assertNotNull(rolModel.getRegistroActual());
        assertEquals(Estado.CREAR, rolModel.getEstado());
        assertTrue(rolModel.isEstadoCrear());
    }

    @Test
    public void testSeleccionar() {
        Rol r = new Rol(UUID.randomUUID());
        rolModel.seleccionar(r);

        assertEquals(r, rolModel.getRegistroActual());
        assertEquals(Estado.MODIFICAR, rolModel.getEstado());
        assertTrue(rolModel.isEstadoModificar());
    }

    @Test
    public void testCancelar() {
        rolModel.prepararNuevo();
        rolModel.cancelar();

        assertNull(rolModel.getRegistroActual());
        assertEquals(Estado.NINGUNO, rolModel.getEstado());
        assertTrue(rolModel.isEstadoNinguno());
    }

    @Test
    public void testGuardarCrear() {
        when(rolDAO.findAll()).thenReturn(Collections.emptyList());

        rolModel.prepararNuevo();
        rolModel.getRegistroActual().setNombre("Medico");

        rolModel.guardar();

        verify(rolDAO).create(any(Rol.class));
        assertEquals(Estado.NINGUNO, rolModel.getEstado());
        assertNull(rolModel.getRegistroActual());
    }

    @Test
    public void testGuardarModificar() {
        when(rolDAO.findAll()).thenReturn(Collections.emptyList());

        Rol r = new Rol(UUID.randomUUID());
        rolModel.seleccionar(r);

        rolModel.guardar();

        verify(rolDAO).update(r);
        assertEquals(Estado.NINGUNO, rolModel.getEstado());
    }

    @Test
    public void testEliminar() {
        when(rolDAO.findAll()).thenReturn(Collections.emptyList());

        Rol r = new Rol(UUID.randomUUID());
        rolModel.eliminar(r);

        verify(rolDAO).delete(r);
    }

    @Test
    public void testGettersAndSetters() {
        assertEquals(rolDAO, rolModel.getDAO());
        assertEquals(rolDAO, rolModel.getRolDAO());
        assertNotNull(rolModel.crearNuevoRegistro());
    }
}

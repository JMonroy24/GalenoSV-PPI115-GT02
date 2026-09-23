package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaRolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonaRolModelTest {

    @Mock
    private PersonaRolDAO personaRolDAO;

    @InjectMocks
    private PersonaRolModel personaRolModel;

    @BeforeEach
    public void setUp() {
        personaRolModel.setPersonaRolDAO(personaRolDAO);
    }

    @Test
    public void testInitYCargarDatos() {
        personaRolModel.init();

        // ModelTransaccional inicializa un LazyDataModel en lugar de cargar
        // todos los registros en memoria con findAll(); verificamos el lazy model.
        assertNotNull(personaRolModel.getLazyModel());
        verifyNoInteractions(personaRolDAO);
    }

    @Test
    public void testPrepararNuevo() {
        personaRolModel.prepararNuevo();

        assertNotNull(personaRolModel.getRegistroActual());
        assertEquals(Estado.CREAR, personaRolModel.getEstado());
        assertTrue(personaRolModel.isEstadoCrear());
    }

    @Test
    public void testSeleccionar() {
        PersonaRol pr = new PersonaRol(UUID.randomUUID());
        personaRolModel.seleccionar(pr);

        assertEquals(pr, personaRolModel.getRegistroActual());
        assertEquals(Estado.MODIFICAR, personaRolModel.getEstado());
        assertTrue(personaRolModel.isEstadoModificar());
    }

    @Test
    public void testCancelar() {
        personaRolModel.prepararNuevo();
        personaRolModel.cancelar();

        assertNull(personaRolModel.getRegistroActual());
        assertEquals(Estado.NINGUNO, personaRolModel.getEstado());
        assertTrue(personaRolModel.isEstadoNinguno());
    }

    @Test
    public void testGuardarCrear() {
        personaRolModel.prepararNuevo();

        personaRolModel.guardar();

        verify(personaRolDAO).create(any(PersonaRol.class));
        assertEquals(Estado.NINGUNO, personaRolModel.getEstado());
        assertNull(personaRolModel.getRegistroActual());
    }

    @Test
    public void testGuardarModificar() {
        PersonaRol pr = new PersonaRol(UUID.randomUUID());
        personaRolModel.seleccionar(pr);

        personaRolModel.guardar();

        verify(personaRolDAO).update(pr);
        assertEquals(Estado.NINGUNO, personaRolModel.getEstado());
    }

    @Test
    public void testEliminar() {
        PersonaRol pr = new PersonaRol(UUID.randomUUID());
        personaRolModel.eliminar(pr);

        verify(personaRolDAO).delete(pr);
    }

    @Test
    public void testGettersAndSetters() {
        personaRolModel.init();
        assertEquals(personaRolDAO, personaRolModel.getDAO());
        assertEquals(personaRolDAO, personaRolModel.getPersonaRolDAO());
        assertNotNull(personaRolModel.crearNuevoRegistro());
        assertNotNull(personaRolModel.getLazyModel());
    }
}

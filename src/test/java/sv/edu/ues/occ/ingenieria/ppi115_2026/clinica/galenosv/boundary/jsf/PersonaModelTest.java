package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonaModelTest {

    @Mock
    private PersonaDAO personaDAO;

    @InjectMocks
    private PersonaModel personaModel;

    @BeforeEach
    public void setUp() {
        personaModel.setPersonaDAO(personaDAO);
    }

    @Test
    public void testInitYCargarDatos() {
        personaModel.init();

        // ModelTransaccional inicializa un LazyDataModel en lugar de cargar
        // todos los registros en memoria con findAll(); verificamos el lazy model.
        assertNotNull(personaModel.getLazyModel());
        verifyNoInteractions(personaDAO);
    }

    @Test
    public void testPrepararNuevo() {
        personaModel.prepararNuevo();

        assertNotNull(personaModel.getRegistroActual());
        assertEquals(Estado.CREAR, personaModel.getEstado());
        assertTrue(personaModel.isEstadoCrear());
    }

    @Test
    public void testSeleccionar() {
        Persona p = new Persona(UUID.randomUUID());
        personaModel.seleccionar(p);

        assertEquals(p, personaModel.getRegistroActual());
        assertEquals(Estado.MODIFICAR, personaModel.getEstado());
        assertTrue(personaModel.isEstadoModificar());
    }

    @Test
    public void testCancelar() {
        personaModel.prepararNuevo();
        personaModel.cancelar();

        assertNull(personaModel.getRegistroActual());
        assertEquals(Estado.NINGUNO, personaModel.getEstado());
        assertTrue(personaModel.isEstadoNinguno());
    }

    @Test
    public void testGuardarCrear() {
        personaModel.prepararNuevo();
        personaModel.getRegistroActual().setNombres("Juan");

        personaModel.guardar();

        verify(personaDAO).create(any(Persona.class));
        assertEquals(Estado.NINGUNO, personaModel.getEstado());
        assertNull(personaModel.getRegistroActual());
    }

    @Test
    public void testGuardarModificar() {
        Persona p = new Persona(UUID.randomUUID());
        personaModel.seleccionar(p);

        personaModel.guardar();

        verify(personaDAO).update(p);
        assertEquals(Estado.NINGUNO, personaModel.getEstado());
    }

    @Test
    public void testEliminar() {
        Persona p = new Persona(UUID.randomUUID());
        personaModel.eliminar(p);

        verify(personaDAO).delete(p);
    }

    @Test
    public void testGettersAndSetters() {
        personaModel.init();
        assertEquals(personaDAO, personaModel.getDAO());
        assertEquals(personaDAO, personaModel.getPersonaDAO());
        assertNotNull(personaModel.crearNuevoRegistro());
        assertNotNull(personaModel.getLazyModel());
    }
}

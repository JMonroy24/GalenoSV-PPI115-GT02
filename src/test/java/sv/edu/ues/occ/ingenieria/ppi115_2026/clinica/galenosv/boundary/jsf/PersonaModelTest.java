package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DocumentoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.MedioContactoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaRolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
public class PersonaModelTest {

    @Mock
    private PersonaService personaService;

    @Mock
    private DocumentoService documentoService;

    @Mock
    private MedioContactoService medioContactoService;

    @Mock
    private PersonaRolService personaRolService;

    @InjectMocks
    private PersonaModel personaModel;

    @BeforeEach
    public void setUp() {
        personaModel.setPersonaService(personaService);
    }

    @Test
    public void testInitYCargarDatos() {
        personaModel.init();
        assertNotNull(personaModel.getLazyModel());
        assertNull(personaModel.getRegistros());

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

        personaModel.getRegistroActual().setNombres("Ana");
        personaModel.getRegistroActual().setApellidos("Pérez");
        personaModel.guardar();

        verify(personaService).create(any(Persona.class));
        assertEquals(Estado.NINGUNO, personaModel.getEstado());
        assertNull(personaModel.getRegistroActual());
    }

    @Test
    public void testGuardarModificar() {
        Persona p = new Persona(UUID.randomUUID());
        personaModel.seleccionar(p);

        personaModel.getRegistroActual().setNombres("Ana");
        personaModel.getRegistroActual().setApellidos("Pérez");
        personaModel.guardar();

        verify(personaService).update(p);
        assertEquals(Estado.NINGUNO, personaModel.getEstado());
    }

    @Test
    public void testEliminar() {
        Persona p = new Persona(UUID.randomUUID());
        personaModel.eliminar(p);

        verify(personaService).delete(p);
    }

    @Test
    public void testGettersAndSetters() {
        personaModel.init();
        assertEquals(personaService, personaModel.getDAO());
        assertEquals(personaService, personaModel.getPersonaService());
        assertNotNull(personaModel.crearNuevoRegistro());
        assertNotNull(personaModel.getLazyModel());
    }
}

package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.RolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Rol;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
public class RolModelTest {

    @Mock
    private RolService rolService;

    @InjectMocks
    private RolModel rolModel;

    @BeforeEach
    public void setUp() {
        rolModel.setRolService(rolService);
    }

    @Test
    public void testInitYCargarDatos() {
        rolModel.init();
        assertNotNull(rolModel.getLazyModel());
        assertNull(rolModel.getRegistros());
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
        rolModel.prepararNuevo();
        rolModel.getRegistroActual().setNombre("Registro válido");
        rolModel.guardar();

        verify(rolService).create(any(Rol.class));
        assertEquals(Estado.NINGUNO, rolModel.getEstado());
        assertNull(rolModel.getRegistroActual());
    }

    @Test
    public void testGuardarModificar() {
        Rol r = new Rol(UUID.randomUUID());
        rolModel.seleccionar(r);

        rolModel.getRegistroActual().setNombre("Registro válido");
        rolModel.guardar();

        verify(rolService).update(r);
        assertEquals(Estado.NINGUNO, rolModel.getEstado());
    }

    @Test
    public void testEliminar() {
        Rol r = new Rol(UUID.randomUUID());
        rolModel.eliminar(r);

        verify(rolService).delete(r);
    }

    @Test
    public void testGetRolesActivos() {
        List<Rol> lista = List.of(new Rol(UUID.randomUUID()));
        when(rolService.getRolesActivos()).thenReturn(lista);

        assertSame(lista, rolModel.getRolesActivos());
        verify(rolService).getRolesActivos();
    }

    @Test
    public void testGettersAndSetters() {
        assertEquals(rolService, rolModel.getDAO());
        assertEquals(rolService, rolModel.getRolService());
        assertNotNull(rolModel.crearNuevoRegistro());
    }

    @Test
    void listadoNoConsultaHastaIngresarUnFiltro() {
        rolModel.init();
        var lista = rolModel.getLazyModel();

        for (String filtro : new String[]{null, "   ", " "}) {
            lista.setFiltroGlobal(filtro);
            assertTrue(lista.isEsperaFiltro());
            assertEquals(0, lista.count(null));
            assertTrue(lista.load(0, 10, null, null).isEmpty());
        }
        verifyNoInteractions(rolService);

        lista.setFiltroGlobal("  M  ");
        assertFalse(lista.isEsperaFiltro());
        lista.count(null);
        lista.load(0, 10, null, null);
        verify(rolService).count("M", java.util.List.of());
        verify(rolService).findRange(0, 10, "M", java.util.List.of(), java.util.List.of());
    }

    @Test
    void sugiereRolesConUnCaracterPeroIgnoraElFiltroVacio() {
        assertTrue(rolModel.completeRol(" ").isEmpty());
        verifyNoInteractions(rolService);

        var rol = new Rol(UUID.randomUUID());
        rol.setNombre("M");
        when(rolService.buscarParaAutocompletar("M", 20)).thenReturn(java.util.List.of(rol));

        assertEquals(java.util.List.of(rol), rolModel.completeRol(" M "));
    }
}

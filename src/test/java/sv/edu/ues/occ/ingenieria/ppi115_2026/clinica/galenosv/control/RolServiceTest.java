package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Rol;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolServiceTest {

    @Mock
    private RolDAO rolDAO;

    @InjectMocks
    private RolService rolService;

    @BeforeEach
    void setUp() {
        rolService.setRolDAO(rolDAO);
    }

    @Test
    void getDAORetornaRolDAO() {
        assertSame(rolDAO, rolService.getDAO());
        assertSame(rolDAO, rolService.getRolDAO());
    }

    @Test
    void crearValidaYPersiste() {
        Rol r = new Rol(UUID.randomUUID());
        r.setNombre("  Médico  ");
        when(rolDAO.existePorCampo("nombre", "Médico", r.getIdRol())).thenReturn(false);

        rolService.crear(r);

        assertEquals("Médico", r.getNombre());
        verify(rolDAO).create(r);
    }

    @Test
    void crearRechazaNombreVacio() {
        Rol r = new Rol(UUID.randomUUID());
        r.setNombre("   ");

        assertThrows(ValidacionNegocioException.class, () -> rolService.crear(r));
        verify(rolDAO, never()).create(any());
    }

    @Test
    void crearRechazaNombreDuplicado() {
        Rol r = new Rol(UUID.randomUUID());
        r.setNombre("Médico");
        when(rolDAO.existePorCampo("nombre", "Médico", r.getIdRol())).thenReturn(true);

        assertThrows(ValidacionNegocioException.class, () -> rolService.crear(r));
        verify(rolDAO, never()).create(any());
    }

    @Test
    void actualizarValidaYActualiza() {
        Rol r = new Rol(UUID.randomUUID());
        r.setNombre("Médico");
        when(rolDAO.existePorCampo("nombre", "Médico", r.getIdRol())).thenReturn(false);

        rolService.actualizar(r);

        verify(rolDAO).update(r);
    }

    @Test
    void eliminarDelegaEnDAO() {
        Rol r = new Rol(UUID.randomUUID());
        rolService.eliminar(r);

        verify(rolDAO).delete(r);
    }

    @Test
    void getRolesActivosYFindAllActivosDeleganEnDAO() {
        List<Rol> lista = List.of(new Rol(UUID.randomUUID()));
        when(rolDAO.findAllActivos()).thenReturn(lista);

        assertSame(lista, rolService.getRolesActivos());
        assertSame(lista, rolService.findAllActivos());
        verify(rolDAO, times(2)).findAllActivos();
    }
}

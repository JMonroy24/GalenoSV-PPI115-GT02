package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Clinica;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClinicaServiceTest {

    @Mock
    private ClinicaDAO clinicaDAO;

    @InjectMocks
    private ClinicaService clinicaService;

    @BeforeEach
    void setUp() {
        clinicaService.setClinicaDAO(clinicaDAO);
    }

    @Test
    void getDAORetornaClinicaDAO() {
        assertSame(clinicaDAO, clinicaService.getDAO());
        assertSame(clinicaDAO, clinicaService.getClinicaDAO());
    }

    @Test
    void crearValidaYPersiste() {
        Clinica c = new Clinica(UUID.randomUUID());
        c.setNombre("  Clínica San Rafael  ");
        c.setTipo("  GENERAL  ");
        when(clinicaDAO.existePorCampo("nombre", "Clínica San Rafael", c.getIdClinica())).thenReturn(false);

        clinicaService.crear(c);

        assertEquals("Clínica San Rafael", c.getNombre());
        assertEquals("GENERAL", c.getTipo());
        verify(clinicaDAO).create(c);
    }

    @Test
    void crearRechazaNombreVacio() {
        Clinica c = new Clinica(UUID.randomUUID());
        c.setNombre("   ");
        c.setTipo("GENERAL");

        assertThrows(ValidacionNegocioException.class, () -> clinicaService.crear(c));
        verify(clinicaDAO, never()).create(any());
    }

    @Test
    void crearRechazaTipoVacio() {
        Clinica c = new Clinica(UUID.randomUUID());
        c.setNombre("Clínica San Rafael");
        c.setTipo("   ");

        assertThrows(ValidacionNegocioException.class, () -> clinicaService.crear(c));
        verify(clinicaDAO, never()).create(any());
    }

    @Test
    void crearRechazaNombreDuplicado() {
        Clinica c = new Clinica(UUID.randomUUID());
        c.setNombre("Clínica San Rafael");
        c.setTipo("GENERAL");
        when(clinicaDAO.existePorCampo("nombre", "Clínica San Rafael", c.getIdClinica())).thenReturn(true);

        assertThrows(ValidacionNegocioException.class, () -> clinicaService.crear(c));
        verify(clinicaDAO, never()).create(any());
    }

    @Test
    void actualizarValidaYActualiza() {
        Clinica c = new Clinica(UUID.randomUUID());
        c.setNombre("Clínica San Rafael");
        c.setTipo("GENERAL");
        when(clinicaDAO.existePorCampo("nombre", "Clínica San Rafael", c.getIdClinica())).thenReturn(false);

        clinicaService.actualizar(c);

        verify(clinicaDAO).update(c);
    }

    @Test
    void eliminarDelegaEnDAO() {
        Clinica c = new Clinica(UUID.randomUUID());
        clinicaService.eliminar(c);

        verify(clinicaDAO).delete(c);
    }

    @Test
    void getActivosYFindAllActivosDeleganEnDAO() {
        List<Clinica> lista = List.of(new Clinica(UUID.randomUUID()));
        when(clinicaDAO.findAllActivos()).thenReturn(lista);

        assertSame(lista, clinicaService.getActivos());
        assertSame(lista, clinicaService.findAllActivos());
        verify(clinicaDAO, times(2)).findAllActivos();
    }
}

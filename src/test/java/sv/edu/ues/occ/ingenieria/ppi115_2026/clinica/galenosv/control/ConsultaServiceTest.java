package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Clinica;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Rol;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConsultaServiceTest {

    private final ConsultaDAO dao = mock(ConsultaDAO.class);
    private final ConsultaService service = new ConsultaService(dao);
    private final Consulta consulta = new Consulta(UUID.randomUUID());
    private final Clinica clinica = new Clinica(UUID.randomUUID());
    private final Rol rol = new Rol(UUID.randomUUID());

    @BeforeEach
    void prepararPaciente() {
        rol.setNombre("PACIENTE ambulatorio");
        rol.setActivo(true);
        clinica.setActivo(true);
        var paciente = new PersonaRol(UUID.randomUUID());
        paciente.setIdRol(rol);
        paciente.setIdClinica(clinica);
        consulta.setIdPersonaRol(paciente);
        consulta.setFechaInicio(new Date(1000));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"Doctor", "   "})
    void rechazaRolQueNoIdentificaPaciente(String nombre) {
        rol.setNombre(nombre);
        assertPacienteInvalido();
    }

    @Test
    void rechazaPacienteInactivoOAsignacionAusente() {
        rol.setActivo(false);
        assertPacienteInvalido();
        consulta.setIdPersonaRol(null);
        assertPacienteInvalido();
    }

    @Test
    void rechazaAltaYEdicionEnOtraClinica() {
        UUID otraClinica = UUID.randomUUID();
        String motivo = "La persona debe ser paciente de la clínica de trabajo.";
        assertEquals(motivo, assertThrows(ValidacionNegocioException.class,
                () -> service.crear(consulta, otraClinica)).getMessage());
        assertEquals(motivo, assertThrows(ValidacionNegocioException.class,
                () -> service.actualizar(consulta, otraClinica)).getMessage());
        verifyNoInteractions(dao);
    }

    @Test
    void rechazaClinicaDeTrabajoAusente() {
        assertEquals("Seleccione una clínica de trabajo antes de crear consultas.",
                assertThrows(ValidacionNegocioException.class, () -> service.crear(consulta, null)).getMessage());
        verifyNoInteractions(dao);
    }

    @Test
    void rechazaFinAnteriorAlInicio() {
        consulta.setFechaFin(new Date(999));
        assertThrows(ValidacionNegocioException.class, () -> service.crear(consulta));
        verifyNoInteractions(dao);
    }

    @Test
    void permitePacienteActivoDeLaClinica() {
        service.crear(consulta, clinica.getIdClinica());
        service.actualizar(consulta, clinica.getIdClinica());
        verify(dao).create(consulta);
        verify(dao).update(consulta);
    }

    private void assertPacienteInvalido() {
        assertEquals("La persona debe ser paciente.",
                assertThrows(ValidacionNegocioException.class, () -> service.crear(consulta)).getMessage());
        verifyNoInteractions(dao);
    }
}

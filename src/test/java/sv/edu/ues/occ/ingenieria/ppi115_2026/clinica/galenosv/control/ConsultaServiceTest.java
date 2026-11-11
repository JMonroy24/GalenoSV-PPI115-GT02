package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Rol;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConsultaServiceTest {

    @Mock
    private ConsultaDAO consultaDAO;

    private ConsultaService service;

    @BeforeEach
    void setUp() {
        service = new ConsultaService(consultaDAO);
    }

    private Consulta consultaConRol(String nombreRol, Boolean activo) {
        Rol rol = new Rol(UUID.randomUUID());
        rol.setNombre(nombreRol);
        rol.setActivo(activo);
        PersonaRol personaRol = new PersonaRol(UUID.randomUUID());
        personaRol.setIdRol(rol);
        Consulta c = new Consulta(UUID.randomUUID());
        c.setIdPersonaRol(personaRol);
        return c;
    }

    @Test
    public void testValidarPersonaEsPaciente() {
        // sin personaRol falla
        Consulta sinRol = new Consulta(UUID.randomUUID());
        sinRol.setIdPersonaRol(null);
        assertThrows(ValidacionNegocioException.class, () -> service.crear(sinRol));

        // rol null falla
        PersonaRol prSinRol = new PersonaRol(UUID.randomUUID());
        prSinRol.setIdRol(null);
        Consulta sinTipoRol = new Consulta(UUID.randomUUID());
        sinTipoRol.setIdPersonaRol(prSinRol);
        assertThrows(ValidacionNegocioException.class, () -> service.crear(sinTipoRol));

        // rol inactivo falla
        assertThrows(ValidacionNegocioException.class,
                () -> service.crear(consultaConRol("Paciente", Boolean.FALSE)));

        // rol "Médico" (no contiene "paciente") falla
        assertThrows(ValidacionNegocioException.class,
                () -> service.crear(consultaConRol("Médico", Boolean.TRUE)));

        // rol "Paciente" activo pasa y llama a dao.create
        Consulta valida = consultaConRol("Paciente", Boolean.TRUE);
        service.crear(valida);
        verify(consultaDAO).create(valida);
    }

    @Test
    public void testValidarClinicaExiste() {
        // idClinica null falla
        Consulta c = consultaConRol("Paciente", Boolean.TRUE);
        assertThrows(ValidacionNegocioException.class, () -> service.crear(c, null));
        verify(consultaDAO, never()).create(any());
    }

    @Test
    public void testValidarClinicaActiva() {
        // persona de otra clinica falla
        Consulta c = consultaConRol("Paciente", Boolean.TRUE);
        UUID otraClinica = UUID.randomUUID();
        var clinicaPersona = new sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Clinica(UUID.randomUUID());
        c.getIdPersonaRol().setIdClinica(clinicaPersona);
        assertThrows(ValidacionNegocioException.class, () -> service.crear(c, otraClinica));
        verify(consultaDAO, never()).create(any());

        // clinica correcta pasa y llama a dao.create
        UUID idClinicaCorrecta = clinicaPersona.getIdClinica();
        service.crear(c, idClinicaCorrecta);
        verify(consultaDAO).create(c);

        // actualizar con clinica correcta llama a dao.update
        service.actualizar(c, idClinicaCorrecta);
        verify(consultaDAO).update(c);
    }
}
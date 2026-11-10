package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Clinica;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Rol;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonaRolServiceTest {

    @Mock
    private PersonaRolDAO personaRolDAO;

    @InjectMocks
    private PersonaRolService personaRolService;

    @BeforeEach
    void setUp() {
        personaRolService.setPersonaRolDAO(personaRolDAO);
    }

    @Test
    void crearAsignaFechaDeCreacionCuandoNoViene() {
        PersonaRol nueva = nuevaAsignacion();
        when(personaRolDAO.existeAsignacion(any(), any(), any(), any())).thenReturn(false);

        personaRolService.crear(nueva);

        assertNotNull(nueva.getFechaCreacion());
        verify(personaRolDAO).create(nueva);
    }

    @Test
    void actualizarNoModificaFechaDeCreacion() {
        Date fechaOriginal = new Date(1600000000000L);
        PersonaRol existente = nuevaAsignacion();
        existente.setFechaCreacion(fechaOriginal);
        when(personaRolDAO.existeAsignacion(any(), any(), any(), any())).thenReturn(false);

        personaRolService.actualizar(existente);

        assertEquals(fechaOriginal, existente.getFechaCreacion());
        verify(personaRolDAO).update(existente);
    }

    private PersonaRol nuevaAsignacion() {
        Persona persona = new Persona(UUID.randomUUID());
        Rol rol = new Rol(UUID.randomUUID());
        rol.setActivo(Boolean.TRUE);
        Clinica clinica = new Clinica(UUID.randomUUID());
        clinica.setActivo(Boolean.TRUE);
        PersonaRol asignacion = new PersonaRol(UUID.randomUUID());
        asignacion.setIdPersona(persona);
        asignacion.setIdRol(rol);
        asignacion.setIdClinica(clinica);
        return asignacion;
    }

    @Test
    void crearConPersonaNulaLanzaValidacionNegocioException() {
        PersonaRol pr = nuevaAsignacion();
        pr.setIdPersona(null);
        assertThrows(ValidacionNegocioException.class, () -> personaRolService.crear(pr));
        verify(personaRolDAO, never()).create(any());
    }

    @Test
    void crearConRolNuloLanzaValidacionNegocioException() {
        PersonaRol pr = nuevaAsignacion();
        pr.setIdRol(null);
        assertThrows(ValidacionNegocioException.class, () -> personaRolService.crear(pr));
        verify(personaRolDAO, never()).create(any());
    }

    @Test
    void crearConRolInactivoLanzaValidacionNegocioException() {
        PersonaRol pr = nuevaAsignacion();
        pr.getIdRol().setActivo(Boolean.FALSE);
        assertThrows(ValidacionNegocioException.class, () -> personaRolService.crear(pr));
        verify(personaRolDAO, never()).create(any());
    }

    @Test
    void crearConClinicaNulaLanzaValidacionNegocioException() {
        PersonaRol pr = nuevaAsignacion();
        pr.setIdClinica(null);
        assertThrows(ValidacionNegocioException.class, () -> personaRolService.crear(pr));
        verify(personaRolDAO, never()).create(any());
    }

    @Test
    void crearConClinicaInactivaLanzaValidacionNegocioException() {
        PersonaRol pr = nuevaAsignacion();
        pr.getIdClinica().setActivo(Boolean.FALSE);
        assertThrows(ValidacionNegocioException.class, () -> personaRolService.crear(pr));
        verify(personaRolDAO, never()).create(any());
    }

    @Test
    void crearConAsignacionDuplicadaLanzaValidacionNegocioException() {
        PersonaRol pr = nuevaAsignacion();
        when(personaRolDAO.existeAsignacion(any(), any(), any(), any())).thenReturn(true);
        assertThrows(ValidacionNegocioException.class, () -> personaRolService.crear(pr));
        verify(personaRolDAO, never()).create(any());
    }

    @Test
    void crearValidoLlamaDAOCreate() {
        PersonaRol pr = nuevaAsignacion();
        when(personaRolDAO.existeAsignacion(any(), any(), any(), any())).thenReturn(false);
        personaRolService.crear(pr);
        verify(personaRolDAO).create(pr);
    }
}


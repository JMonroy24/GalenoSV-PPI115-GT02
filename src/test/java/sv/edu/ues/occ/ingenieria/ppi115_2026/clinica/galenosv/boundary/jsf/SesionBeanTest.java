package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SesionBeanTest {
    private SesionBean sesion() {
        var s = new SesionBean();
        s.clinicaDAO = mock(ClinicaDAO.class); s.rolDAO = mock(RolDAO.class);
        s.personaRolDAO = mock(PersonaRolDAO.class); return s;
    }
    private Clinica clinica() {
        var c = new Clinica(UUID.randomUUID()); c.setActivo(true); return c;
    }
    @Test void puedeAsignarSoloClinicaSinExigirPersona() {
        var s = sesion(); var c = clinica(); s.setClinicaTemp(c);
        when(s.clinicaDAO.findById(c.getIdClinica())).thenReturn(c);
        s.aplicarClinica(); assertSame(c, s.getClinicaActual());
        assertNull(s.getPersonaActual()); assertNull(s.getRolActual());
    }
    @Test void cambiarClinicaLimpiaElRolYLaPersonaAnteriores() {
        var s = sesion(); s.setClinicaActual(clinica());
        s.setRolActual(new Rol(UUID.randomUUID())); s.setPersonaActual(new Persona(UUID.randomUUID()));
        var c = clinica(); s.setClinicaTemp(c); when(s.clinicaDAO.findById(c.getIdClinica())).thenReturn(c);
        s.aplicarClinica(); assertSame(c, s.getClinicaActual());
        assertNull(s.getRolActual()); assertNull(s.getPersonaActual());
    }
    @Test void rechazaClinicaDesactivadaDespuesDeAbrirFormulario() {
        var s = sesion(); var anterior = clinica(); s.setClinicaActual(anterior);
        var c = clinica(); s.setClinicaTemp(c); c.setActivo(false);
        when(s.clinicaDAO.findById(c.getIdClinica())).thenReturn(c);
        s.aplicarClinica(); assertSame(anterior, s.getClinicaActual());
    }
    @Test void rolSoloSeAplicaSiLaPersonaEstaAsignadaEnLaClinica() {
        var s = sesion(); var c = clinica(); var r = new Rol(UUID.randomUUID()); r.setActivo(true);
        var persona = new Persona(UUID.randomUUID());
        s.setClinicaTemp(c); s.setRolTemp(r); s.setPersonaTemp(persona);
        when(s.clinicaDAO.findById(c.getIdClinica())).thenReturn(c);
        when(s.rolDAO.findById(r.getIdRol())).thenReturn(r);
        s.aplicarCambios(); assertNull(s.getRolActual());
        var asignacion = new PersonaRol(UUID.randomUUID()); asignacion.setIdPersona(persona);
        when(s.personaRolDAO.findByClinicaAndRol(c.getIdClinica(), r.getIdRol())).thenReturn(List.of(asignacion));
        s.aplicarCambios(); assertSame(persona, s.getPersonaActual()); assertSame(r, s.getRolActual());
    }
    @Test void cerrarSesionLimpiaRolPersonaYClinicaIncluidosBorradores() {
        var s = sesion(); var c = clinica(); var r = new Rol(UUID.randomUUID()); var p = new Persona(UUID.randomUUID());
        s.setClinicaActual(c); s.setRolActual(r); s.setPersonaActual(p);
        s.setClinicaTemp(c); s.setRolTemp(r); s.setPersonaTemp(p);
        assertEquals("/index?faces-redirect=true", s.cerrarSesion());
        assertFalse(s.isClinicaSeleccionada()); assertNull(s.getClinicaActual());
        assertNull(s.getRolActual()); assertNull(s.getPersonaActual());
        assertNull(s.getClinicaTemp()); assertNull(s.getRolTemp()); assertNull(s.getPersonaTemp());
    }
}

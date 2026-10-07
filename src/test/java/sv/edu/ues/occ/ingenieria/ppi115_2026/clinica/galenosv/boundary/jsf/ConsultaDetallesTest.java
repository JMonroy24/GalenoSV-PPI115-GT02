package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConsultaDetallesTest {
    private ConsultaModel modelo() {
        var m = new ConsultaModel(); m.consultaDAO = mock(ConsultaDAO.class);
        m.documentoDAO = mock(DocumentoDAO.class); m.procedimientoPasoDAO = mock(ProcedimientoPasoDAO.class);
        m.personaRolDAO = mock(PersonaRolDAO.class); m.consultaProcedimientoPasoDAO = mock(ConsultaProcedimientoPasoDAO.class);
        m.sesionBean = new SesionBean(); m.init(); m.prepararNuevo(); return m;
    }
    @Test void informacionIncluyePersonaNacimientoYDocumentos() {
        var m = modelo(); var persona = new Persona(UUID.randomUUID());
        persona.setNombres("Paciente"); persona.setApellidos("Prueba");
        persona.setFechaNacimiento(Date.from(java.time.Instant.parse("2000-01-15T12:00:00Z")));
        var pr = new PersonaRol(UUID.randomUUID()); pr.setIdPersona(persona);
        var tipo = new TipoDocumento(UUID.randomUUID()); tipo.setNombre("Documento de prueba");
        var doc = new Documento(UUID.randomUUID()); doc.setIdTipoDocumento(tipo); doc.setValor("DOC-001");
        var pasaporte = new TipoDocumento(UUID.randomUUID()); pasaporte.setNombre("Pasaporte");
        var doc2 = new Documento(UUID.randomUUID()); doc2.setIdTipoDocumento(pasaporte); doc2.setValor("PAS-002");
        when(m.documentoDAO.findByPersona(persona.getIdPersona())).thenReturn(List.of(doc, doc2));
        String detalle = m.getInformacionPaciente(pr);
        assertTrue(detalle.contains("Paciente Prueba")); assertTrue(detalle.contains("15/01/2000"));
        assertTrue(detalle.contains("Documento de prueba: DOC-001"));
        assertTrue(detalle.contains("Pasaporte: PAS-002"));
        m.getInformacionPaciente(pr); verify(m.documentoDAO).findByPersona(persona.getIdPersona());
        assertEquals("Seleccione un paciente.", m.getInformacionPaciente(null));
    }
    @Test void pacientesConElMismoNombreConservanSusPropiosDocumentos() {
        var m = modelo();
        var persona1 = new Persona(UUID.randomUUID());
        var persona2 = new Persona(UUID.randomUUID());
        persona1.setNombres("Paciente"); persona1.setApellidos("Prueba");
        persona2.setNombres("Paciente"); persona2.setApellidos("Prueba");
        var pr1 = new PersonaRol(UUID.randomUUID()); pr1.setIdPersona(persona1);
        var pr2 = new PersonaRol(UUID.randomUUID()); pr2.setIdPersona(persona2);
        var doc1 = new Documento(UUID.randomUUID()); doc1.setValor("DOC-001");
        var doc2 = new Documento(UUID.randomUUID()); doc2.setValor("DOC-002");
        when(m.documentoDAO.findByPersona(persona1.getIdPersona())).thenReturn(List.of(doc1));
        when(m.documentoDAO.findByPersona(persona2.getIdPersona())).thenReturn(List.of(doc2));
        assertTrue(m.getInformacionPaciente(pr1).contains("Documento: DOC-001"));
        assertFalse(m.getInformacionPaciente(pr1).contains("DOC-002"));
        assertTrue(m.getInformacionPaciente(pr2).contains("Documento: DOC-002"));
        assertFalse(m.getInformacionPaciente(pr2).contains("DOC-001"));
        verify(m.documentoDAO).findByPersona(persona1.getIdPersona());
        verify(m.documentoDAO).findByPersona(persona2.getIdPersona());
    }
    @Test void seleccionDeProcedimientoMuestraInicioYResponsableYLimpiaAlCambiar() {
        var m = modelo(); var clinica = new Clinica(UUID.randomUUID()); clinica.setActivo(true); m.sesionBean.setClinicaActual(clinica);
        var proc = new Procedimiento(UUID.randomUUID()); m.getProcedimientoNuevo().setIdProcedimiento(proc);
        var paso = new ProcedimientoPaso(UUID.randomUUID()); paso.setNombre("Recepción");
        var rol = new Rol(UUID.randomUUID()); paso.setIdRol(rol);
        var responsable = new PersonaRol(UUID.randomUUID()); var persona = new Persona(UUID.randomUUID());
        persona.setNombres("Responsable"); persona.setApellidos("Prueba"); responsable.setIdPersona(persona); responsable.setIdRol(rol);
        when(m.procedimientoPasoDAO.findPasoInicial(proc.getIdProcedimiento())).thenReturn(paso);
        when(m.personaRolDAO.findResponsable(clinica.getIdClinica(), rol.getIdRol())).thenReturn(responsable);
        m.actualizarInicioPrevisto(); assertSame(paso, m.getPasoInicialPrevisto());
        assertSame(responsable, m.getResponsableInicialPrevisto()); assertEquals("Responsable Prueba", m.getNombreResponsableInicial());
        var cp = new ConsultaProcedimiento(UUID.randomUUID()); cp.setIdProcedimiento(proc);
        var asignado = new ConsultaProcedimientoPaso(UUID.randomUUID()); asignado.setIdPersonaRol(responsable); asignado.setFechaInicio(new Date());
        when(m.consultaProcedimientoPasoDAO.findByConsultaProcedimiento(cp.getIdConsultaProcedimiento())).thenReturn(List.of(asignado));
        assertTrue(m.getDetallePrimerPaso(cp).contains("Recepción")); assertTrue(m.getDetallePrimerPaso(cp).contains("Responsable Prueba"));
        m.getProcedimientoNuevo().setIdProcedimiento(new Procedimiento(UUID.randomUUID()));
        m.actualizarInicioPrevisto(); assertNull(m.getPasoInicialPrevisto()); assertNull(m.getResponsableInicialPrevisto());
    }
    @Test void cerrarSesionImpideCrearConsultaYBuscarPacientes() {
        var m = modelo(); m.sesionBean.cerrarSesion();
        assertTrue(m.completePersonaRol("Paciente").isEmpty()); verifyNoInteractions(m.personaRolDAO);
        assertThrows(ValidacionNegocioException.class, () -> m.validarNegocio(m.getRegistroActual()));
    }
    @Test void personaNuevaTieneFechaDeCreacion() {
        Date antes = new Date(); var persona = new PersonaModel().crearNuevoRegistro();
        assertNotNull(persona.getFechaCreacion()); assertFalse(persona.getFechaCreacion().before(antes));
    }
}

package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Regresiones de los flujos integrados que antes omitían las reglas de la rúbrica. */
class RubricaRegressionTest {
    private Clinica clinica() {
        Clinica c = new Clinica(UUID.randomUUID()); c.setNombre("Central"); c.setActivo(true); return c;
    }

    private Rol rol(String nombre) {
        Rol r = new Rol(UUID.randomUUID()); r.setNombre(nombre); r.setActivo(true); return r;
    }

    private PersonaRol personaRol(Clinica c, Rol r) {
        PersonaRol pr = new PersonaRol(UUID.randomUUID()); pr.setIdClinica(c); pr.setIdRol(r);
        pr.setIdPersona(new Persona(UUID.randomUUID())); return pr;
    }

    private ConsultaModel consultaModel() {
        ConsultaModel m = new ConsultaModel(); m.consultaDAO = mock(ConsultaDAO.class);
        m.personaRolDAO = mock(PersonaRolDAO.class); m.procedimientoPasoDAO = mock(ProcedimientoPasoDAO.class);
        m.consultaProcedimientoDAO = mock(ConsultaProcedimientoDAO.class);
        m.consultaProcedimientoPasoDAO = mock(ConsultaProcedimientoPasoDAO.class);
        m.asignacionService = mock(AsignacionService.class); m.sesionBean = new SesionBean();
        m.sesionBean.setClinicaActual(clinica()); m.init(); m.prepararNuevo();
        m.getRegistroActual().setIdPersonaRol(personaRol(m.sesionBean.getClinicaActual(), rol("Paciente")));
        return m;
    }

    private PersonaModel personaModel() {
        PersonaModel m = new PersonaModel(); m.personaDAO = mock(PersonaDAO.class);
        m.documentoDAO = mock(DocumentoDAO.class); m.medioContactoDAO = mock(MedioContactoDAO.class);
        m.personaRolDAO = mock(PersonaRolDAO.class); m.tipoDocumentoDAO = mock(TipoDocumentoDAO.class);
        m.tipoMedioContactoDAO = mock(TipoMedioContactoDAO.class); m.rolDAO = mock(RolDAO.class);
        m.clinicaDAO = mock(ClinicaDAO.class); m.init(); m.seleccionar(new Persona(UUID.randomUUID()));
        clearInvocations(m.personaRolDAO); return m;
    }

    @Test
    void catalogosNuevosSonActivosYPasoNuevoNoFinaliza() {
        assertTrue(new ClinicaModel().crearNuevoRegistro().getActivo());
        assertTrue(new RolModel().crearNuevoRegistro().getActivo());
        assertTrue(new TipoDocumentoModel().crearNuevoRegistro().getActivo());
        assertTrue(new TipoMedioContactoModel().crearNuevoRegistro().getActivo());
        assertTrue(new TipoExamenModel().crearNuevoRegistro().getActivo());
        assertTrue(new ProcedimientoModel().crearNuevoRegistro().getActivo());
        assertTrue(new ExamenModel().crearNuevoRegistro().getActivo());
        assertFalse(new ProcedimientoPasoModel().crearNuevoRegistro().getIndicaFin());
    }

    @Test
    void clinicaDeTrabajoEsPorSesionYSoloAceptaActivas() {
        SesionBean primera = new SesionBean(), segunda = new SesionBean();
        primera.clinicaDAO = mock(ClinicaDAO.class); Clinica c = clinica();
        when(primera.clinicaDAO.findAllActivos()).thenReturn(List.of(c));
        assertFalse(primera.isClinicaSeleccionada()); assertEquals("Sin clínica seleccionada", primera.getNombreClinica());
        assertEquals(List.of(c), primera.getClinicasActivas()); primera.setClinicaActual(c);
        assertTrue(primera.isClinicaSeleccionada()); assertEquals("Central", primera.getNombreClinica());
        assertNull(segunda.getClinicaActual()); Clinica inactiva = clinica(); inactiva.setActivo(false);
        primera.setClinicaActual(inactiva); assertSame(c, primera.getClinicaActual());
        primera.setClinicaActual(null); assertFalse(primera.isClinicaSeleccionada());
    }

    @Test
    void pasosExigenRolActivoYDistintoAlCrear() {
        ProcedimientoPasoModel m = new ProcedimientoPasoModel(); m.procedimientoPasoDAO = mock(ProcedimientoPasoDAO.class);
        m.prepararNuevo(); ProcedimientoPaso paso = m.getRegistroActual(); paso.setNombre("Inicio");
        Procedimiento p = new Procedimiento(UUID.randomUUID()); p.setActivo(false); paso.setIdProcedimiento(p);
        assertThrows(ValidacionNegocioException.class, () -> m.validarNegocio(paso)); p.setActivo(true);
        assertThrows(ValidacionNegocioException.class, () -> m.validarNegocio(paso));
        Rol rol = rol("Doctor"); rol.setActivo(false); paso.setIdRol(rol);
        assertThrows(ValidacionNegocioException.class, () -> m.validarNegocio(paso)); rol.setActivo(true);
        ProcedimientoPaso existente = new ProcedimientoPaso(UUID.randomUUID()); existente.setIdRol(rol); existente.setIdProcedimiento(p);
        when(m.procedimientoPasoDAO.findByProcedimiento(p.getIdProcedimiento(), null)).thenReturn(List.of(existente));
        assertTrue(assertThrows(ValidacionNegocioException.class, () -> m.validarNegocio(paso)).getMessage().contains("rol distinto"));
        paso.setIdRol(rol("Enfermera")); m.setPasoDependeDe(existente); assertDoesNotThrow(() -> m.validarNegocio(paso));
        m.setEstado(Estado.MODIFICAR); paso.setIdRol(rol); assertDoesNotThrow(() -> m.validarNegocio(paso));
    }

    @ParameterizedTest
    @ValueSource(strings = {"incorrecto", "[", "inactivo"})
    void documentosIntegradosRechazanFormatoRegexInvalidaYTipoInactivo(String caso) {
        PersonaModel m = personaModel(); TipoDocumento tipo = new TipoDocumento(UUID.randomUUID()); tipo.setNombre("DUI");
        tipo.setActivo(!caso.equals("inactivo")); tipo.setExpresionRegular(caso.equals("[") ? "[" : "[0-9]{8}-[0-9]");
        tipo.setIndicaciones("Use el formato DUI."); m.getDocumentoNuevo().setIdTipoDocumento(tipo);
        m.getDocumentoNuevo().setValor(caso.equals("incorrecto") ? "ABC" : "12345678-9");
        m.agregarDocumento(); verify(m.documentoDAO, never()).create(any()); assertNotNull(m.getDocumentoNuevo().getIdTipoDocumento());
        tipo.setActivo(true); tipo.setExpresionRegular("[0-9]{8}-[0-9]"); m.getDocumentoNuevo().setValor(" 12345678-9 ");
        ArgumentCaptor<Documento> captor = ArgumentCaptor.forClass(Documento.class); m.agregarDocumento();
        verify(m.documentoDAO).create(captor.capture()); assertEquals("12345678-9", captor.getValue().getValor());
        assertSame(m.getRegistroActual(), captor.getValue().getIdPersona()); assertNull(m.getDocumentoNuevo().getIdTipoDocumento());
    }

    @ParameterizedTest
    @ValueSource(strings = {"incorrecto", "[", "inactivo"})
    void contactosIntegradosCompartenLasValidaciones(String caso) {
        PersonaModel m = personaModel(); TipoMedioContacto tipo = new TipoMedioContacto(UUID.randomUUID());
        tipo.setActivo(!caso.equals("inactivo")); tipo.setExpresionRegular(caso.equals("[") ? "[" : "[0-9]{8}");
        m.getMedioNuevo().setIdTipoMedioContacto(tipo); m.getMedioNuevo().setValor(caso.equals("incorrecto") ? "ABC" : "12345678");
        m.agregarMedioContacto(); verify(m.medioContactoDAO, never()).create(any());
        tipo.setActivo(true); tipo.setExpresionRegular("[0-9]{8}"); m.getMedioNuevo().setValor(" 12345678 "); m.agregarMedioContacto();
        ArgumentCaptor<MedioContacto> captor = ArgumentCaptor.forClass(MedioContacto.class);
        verify(m.medioContactoDAO).create(captor.capture()); assertEquals("12345678", captor.getValue().getValor());
        assertNotNull(captor.getValue().getFechaCreacion());
    }

    @Test
    void rolesIntegradosRequierenRolYClinicaActivos() {
        PersonaModel m = personaModel(); m.agregarRol(); verifyNoInteractions(m.personaRolDAO);
        Rol r = rol("Doctor"); r.setActivo(false); m.getRolNuevo().setIdRol(r); m.agregarRol();
        verifyNoInteractions(m.personaRolDAO); r.setActivo(true); m.agregarRol(); verifyNoInteractions(m.personaRolDAO);
        Clinica c = clinica(); c.setActivo(false); m.getRolNuevo().setIdClinica(c); m.agregarRol(); verifyNoInteractions(m.personaRolDAO);
        c.setActivo(true); m.agregarRol(); verify(m.personaRolDAO).create(any(PersonaRol.class));
    }

    @Test
    void personaSinRolesSeGuardaYCatalogosSoloDevuelvenActivos() {
        PersonaModel m = personaModel(); m.prepararNuevo(); m.getRegistroActual().setNombres("Ana"); m.getRegistroActual().setApellidos("Pérez");
        m.guardar(); verify(m.personaDAO).create(any()); verify(m.personaRolDAO, never()).create(any());
        m.getTiposDocumento(); m.getTiposMedioContacto(); m.getRolesActivos(); m.getClinicas();
        verify(m.tipoDocumentoDAO).findAllActivos(); verify(m.tipoMedioContactoDAO).findAllActivos();
        verify(m.rolDAO).findAllActivos(); verify(m.clinicaDAO).findAllActivos();
    }

    @Test
    void consultaRechazaAusenciaDeClinicaRolIncorrectoYOtraClinica() {
        ConsultaModel m = consultaModel(); Consulta c = m.getRegistroActual();
        m.sesionBean.setClinicaActual(null); m.guardar(); verify(m.consultaDAO, never()).create(any());
        assertTrue(m.isEstadoCrear()); assertTrue(m.completePersonaRol("Ana").isEmpty()); verifyNoInteractions(m.personaRolDAO);
        m.sesionBean.setClinicaActual(c.getIdPersonaRol().getIdClinica()); c.getIdPersonaRol().setIdRol(rol("Doctor"));
        m.guardar(); verify(m.consultaDAO, never()).create(any()); c.getIdPersonaRol().setIdRol(rol("Paciente"));
        m.sesionBean.setClinicaActual(clinica()); m.guardar(); verify(m.consultaDAO, never()).create(any());
        m.sesionBean.setClinicaActual(c.getIdPersonaRol().getIdClinica()); c.getIdPersonaRol().getIdRol().setActivo(false);
        m.guardar(); verify(m.consultaDAO, never()).create(any()); c.getIdPersonaRol().getIdRol().setActivo(true);
        m.guardar(); verify(m.consultaDAO).create(c); assertTrue(m.isEstadoNinguno());
    }

    @Test
    void autocompletadoUsaLaClinicaActualYLimitaResultados() {
        ConsultaModel m = consultaModel(); assertTrue(m.completePersonaRol("a").isEmpty());
        assertTrue(m.completePersonaRol(null).isEmpty()); verifyNoInteractions(m.personaRolDAO);
        m.completePersonaRol("Ana"); verify(m.personaRolDAO).buscarPacientes("Ana", m.sesionBean.getClinicaActual().getIdClinica(), 20);
    }

    @Test
    void procedimientoNoSeCreaSinInicioRolOResponsable() {
        ConsultaModel m = consultaModel(); m.seleccionar(m.getRegistroActual());
        Procedimiento p = new Procedimiento(UUID.randomUUID()); p.setActivo(false); m.getProcedimientoNuevo().setIdProcedimiento(p);
        m.getProcedimientoNuevo().setFechaInicio(m.getRegistroActual().getFechaInicio()); m.agregarProcedimiento();
        verifyNoInteractions(m.asignacionService); p.setActivo(true); m.agregarProcedimiento(); verifyNoInteractions(m.asignacionService);
        ProcedimientoPaso inicio = new ProcedimientoPaso(UUID.randomUUID()); when(m.procedimientoPasoDAO.findPasoInicial(p.getIdProcedimiento())).thenReturn(inicio);
        m.agregarProcedimiento(); verifyNoInteractions(m.asignacionService); Rol r = rol("Doctor"); inicio.setIdRol(r);
        r.setActivo(false); m.agregarProcedimiento(); verifyNoInteractions(m.asignacionService); r.setActivo(true);
        m.agregarProcedimiento(); verifyNoInteractions(m.asignacionService);
        PersonaRol responsable = personaRol(m.sesionBean.getClinicaActual(), r);
        when(m.personaRolDAO.findResponsable(m.sesionBean.getClinicaActual().getIdClinica(), r.getIdRol())).thenReturn(responsable);
        ConsultaProcedimiento borrador = m.getProcedimientoNuevo(); m.agregarProcedimiento();
        ArgumentCaptor<ConsultaProcedimientoPaso> captor = ArgumentCaptor.forClass(ConsultaProcedimientoPaso.class);
        verify(m.asignacionService).crearProcedimientoConPaso(eq(borrador), captor.capture());
        assertEquals("PENDIENTE", captor.getValue().getEstado()); assertSame(responsable, captor.getValue().getIdPersonaRol());
        assertEquals(borrador.getFechaInicio(), captor.getValue().getFechaInicio()); assertSame(borrador, m.getProcedimientoActivo());
        verify(m.consultaProcedimientoDAO, never()).create(any()); verify(m.consultaProcedimientoPasoDAO, never()).create(any());
        verify(m.consultaProcedimientoPasoDAO).findByConsultaProcedimiento(borrador.getIdConsultaProcedimiento());
    }

    @Test
    void filtroIncluyeElDiaCompletoYNoCargaFechasInvertidas() {
        ConsultaModel m = consultaModel(); UUID c = m.sesionBean.getClinicaActual().getIdClinica();
        Date dia = Date.from(Instant.parse("2026-10-02T12:00:00Z")); m.setFechaDesde(dia); m.setFechaHasta(dia);
        m.getLazyModel().setFiltroGlobal("Referencia"); m.filtrar(); m.getLazyModel().count(Map.of());
        m.getLazyModel().load(0, 15, Map.of(), Map.of());
        Date inicio = Date.from(Instant.parse("2026-10-02T06:00:00Z")), fin = Date.from(Instant.parse("2026-10-03T05:59:59.999Z"));
        verify(m.consultaDAO).countFiltrado(c, inicio, fin, "Referencia");
        verify(m.consultaDAO).findRangeFiltrado(0, 15, c, inicio, fin, "Referencia"); clearInvocations(m.consultaDAO);
        m.setFechaHasta(Date.from(Instant.parse("2026-10-01T12:00:00Z"))); m.filtrar();
        assertEquals(0, m.getLazyModel().count(Map.of())); assertTrue(m.getLazyModel().load(0, 15, Map.of(), Map.of()).isEmpty());
        verifyNoInteractions(m.consultaDAO); m.limpiarFiltro(); assertNull(m.getFechaDesde()); assertNull(m.getFechaHasta());
    }

    @Test
    void examenGuardaTipoInicialYObservacionesEnUnaSolaOperacion() {
        ExamenModel m = new ExamenModel(); m.examenDAO = mock(ExamenDAO.class); m.asignacionService = mock(AsignacionService.class);
        m.prepararNuevo(); Examen examen = m.getRegistroActual(); examen.setNombre("Hemograma"); TipoExamen tipo = new TipoExamen(UUID.randomUUID());
        tipo.setActivo(false); m.setTipoSeleccionado(tipo); m.guardar(); verifyNoInteractions(m.asignacionService); assertTrue(m.isEstadoCrear());
        tipo.setActivo(true); m.setObservacionesTipo("Muestra sanguínea"); m.guardar();
        ArgumentCaptor<ExamenTipoExamen> captor = ArgumentCaptor.forClass(ExamenTipoExamen.class);
        verify(m.asignacionService).crearExamenConTipo(eq(examen), captor.capture());
        assertSame(tipo, captor.getValue().getIdTipoExamen()); assertEquals("Muestra sanguínea", captor.getValue().getObservaciones());
        verify(m.examenDAO, never()).create(any()); assertTrue(m.isEstadoNinguno());
    }

    @Test
    void errorAlCrearExamenConTipoConservaElFormulario() {
        ExamenModel m = new ExamenModel(); m.examenDAO = mock(ExamenDAO.class); m.asignacionService = mock(AsignacionService.class);
        m.prepararNuevo(); m.getRegistroActual().setNombre("Hemograma"); TipoExamen tipo = new TipoExamen(UUID.randomUUID()); tipo.setActivo(true);
        m.setTipoSeleccionado(tipo); doThrow(new IllegalStateException("fallo")).when(m.asignacionService).crearExamenConTipo(any(), any());
        m.guardar(); assertTrue(m.isEstadoCrear()); assertNotNull(m.getRegistroActual()); assertSame(tipo, m.getTipoSeleccionado());
    }
}

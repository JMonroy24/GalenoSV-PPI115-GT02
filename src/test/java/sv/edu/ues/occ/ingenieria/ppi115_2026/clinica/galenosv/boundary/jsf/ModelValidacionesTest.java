package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ModelValidacionesTest {

    @Test
    void validaNombreUnicoDeClinica() {
        var model = new ClinicaModel();
        var dao = mock(ClinicaDAO.class);
        model.setClinicaDAO(dao);
        var registro = new Clinica(UUID.randomUUID());
        registro.setNombre("   ");
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(registro));
        registro.setNombre("  Nombre  ");
        registro.setTipo("GENERAL");
        assertDoesNotThrow(() -> model.validarNegocio(registro));
        assertEquals("Nombre", registro.getNombre());
        when(dao.existePorCampo("nombre", "Nombre", registro.getIdClinica())).thenReturn(true);
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(registro));
    }

    @Test
    void validaNombreUnicoDeRol() {
        var model = new RolModel();
        var dao = mock(RolDAO.class);
        model.setRolDAO(dao);
        var registro = new Rol(UUID.randomUUID());
        registro.setNombre("   ");
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(registro));
        registro.setNombre("  Nombre  ");
        assertDoesNotThrow(() -> model.validarNegocio(registro));
        assertEquals("Nombre", registro.getNombre());
        when(dao.existePorCampo("nombre", "Nombre", registro.getIdRol())).thenReturn(true);
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(registro));
    }

    @Test
    void validaNombreUnicoDeProcedimiento() {
        var model = new ProcedimientoModel();
        var dao = mock(ProcedimientoDAO.class);
        model.setProcedimientoDAO(dao);
        var registro = new Procedimiento(UUID.randomUUID());
        registro.setNombre("   ");
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(registro));
        registro.setNombre("  Nombre  ");
        assertDoesNotThrow(() -> model.validarNegocio(registro));
        assertEquals("Nombre", registro.getNombre());
        when(dao.existePorCampo("nombre", "Nombre", registro.getIdProcedimiento())).thenReturn(true);
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(registro));
    }

    @Test
    void validaNombreUnicoDeExamen() {
        var model = new ExamenModel();
        var dao = mock(ExamenDAO.class);
        model.setExamenDAO(dao);
        var registro = new Examen(UUID.randomUUID());
        registro.setNombre("   ");
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(registro));
        registro.setNombre("  Nombre  ");
        assertDoesNotThrow(() -> model.validarNegocio(registro));
        assertEquals("Nombre", registro.getNombre());
        when(dao.existePorCampo("nombre", "Nombre", registro.getIdExamen())).thenReturn(true);
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(registro));
    }

    @Test
    void validaNombreUnicoDeTipoExamen() {
        var model = new TipoExamenModel();
        var dao = mock(TipoExamenDAO.class);
        model.setTipoExamenDAO(dao);
        var registro = new TipoExamen(UUID.randomUUID());
        registro.setNombre("   ");
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(registro));
        registro.setNombre("  Nombre  ");
        assertDoesNotThrow(() -> model.validarNegocio(registro));
        assertEquals("Nombre", registro.getNombre());
        when(dao.existePorCampo("nombre", "Nombre", registro.getIdTipoExamen())).thenReturn(true);
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(registro));
    }

    @Test
    void validaNombreUnicoDeTipoDocumento() {
        var model = new TipoDocumentoModel();
        var dao = mock(TipoDocumentoDAO.class);
        model.setTipoDocumentoDAO(dao);
        var registro = new TipoDocumento(UUID.randomUUID());
        registro.setNombre("   ");
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(registro));
        registro.setNombre("  Nombre  ");
        assertDoesNotThrow(() -> model.validarNegocio(registro));
        assertEquals("Nombre", registro.getNombre());
        when(dao.existePorCampo("nombre", "Nombre", registro.getIdTipoDocumento())).thenReturn(true);
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(registro));
    }

    @Test
    void validaNombreUnicoDeTipoMedioContacto() {
        var model = new TipoMedioContactoModel();
        var dao = mock(TipoMedioContactoDAO.class);
        model.setTipoMedioContactoDAO(dao);
        var registro = new TipoMedioContacto(UUID.randomUUID());
        registro.setNombre("   ");
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(registro));
        registro.setNombre("  Nombre  ");
        assertDoesNotThrow(() -> model.validarNegocio(registro));
        assertEquals("Nombre", registro.getNombre());
        when(dao.existePorCampo("nombre", "Nombre", registro.getIdTipoMedioContacto())).thenReturn(true);
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(registro));
    }

    @Test
    void personaRechazaVaciosYFechaFutura() {
        var model = new PersonaModel();
        var persona = new Persona(UUID.randomUUID());
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(persona));
        persona.setNombres(" Ana "); persona.setApellidos(" Pérez ");
        persona.setFechaNacimiento(new Date(model.getHoy().getTime() + 86400000L));
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(persona));
        persona.setFechaNacimiento(java.util.Date.from(java.time.LocalDate.of(1970, 1, 1).atStartOfDay(java.time.ZoneId.of("America/El_Salvador")).toInstant()));
        assertDoesNotThrow(() -> model.validarNegocio(persona));
        assertEquals("Ana", persona.getNombres()); assertEquals("Pérez", persona.getApellidos());
        assertNotNull(model.getHoy());
    }

    @Test
    void documentoValidaFormatoActividadYDuplicado() {
        var model = new DocumentoModel(); var dao = mock(DocumentoDAO.class); model.setDocumentoDAO(dao);
        var registro = new Documento(UUID.randomUUID());
        registro.setIdPersona(new Persona(UUID.randomUUID()));
        var tipo = new TipoDocumento(UUID.randomUUID());
        tipo.setActivo(true); tipo.setExpresionRegular("[0-9]{8}-[0-9]"); tipo.setIndicaciones("Use ocho dígitos, guion y un dígito.");
        registro.setIdTipoDocumento(tipo); registro.setValor("incorrecto");
        var error = assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(registro));
        assertTrue(error.getMessage().contains(tipo.getIndicaciones()));
        registro.setValor(" 12345678-9 ");
        assertDoesNotThrow(() -> model.validarNegocio(registro));
        assertEquals("12345678-9", registro.getValor());
        tipo.setActivo(false);
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(registro));
        tipo.setActivo(true);
        when(dao.existePersonaTipoValor(registro.getIdPersona().getIdPersona(), tipo.getIdTipoDocumento(), "12345678-9", registro.getIdDocumento())).thenReturn(true);
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(registro));
        assertTrue(model.completePersona(" a ").isEmpty());
    }

    @Test
    void contactoValidaFormatoActividadYDuplicadoPorPersona() {
        var model = new MedioContactoModel(); var dao = mock(MedioContactoDAO.class); model.setMedioContactoDAO(dao);
        var registro = new MedioContacto(UUID.randomUUID());
        var persona = new Persona(UUID.randomUUID()); registro.setIdPersona(persona);
        var tipo = new TipoMedioContacto(UUID.randomUUID()); tipo.setActivo(true);
        tipo.setExpresionRegular("[^@ ]+@[^@ ]+[.][^@ ]+"); registro.setIdTipoMedioContacto(tipo);
        registro.setValor("correo inválido");
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(registro));
        registro.setValor(" ana@example.org ");
        assertDoesNotThrow(() -> model.validarNegocio(registro));
        assertEquals("ana@example.org", registro.getValor());
        tipo.setActivo(false);
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(registro));
        tipo.setActivo(true);
        when(dao.existePersonaTipoValor(persona.getIdPersona(), tipo.getIdTipoMedioContacto(),
                "ana@example.org", registro.getIdMedioContacto())).thenReturn(true);
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(registro));
        assertTrue(model.completePersona(null).isEmpty());
    }

    @Test
    void personaRolExigeClinicaYRechazaDuplicadoEInactivos() {
        var model = new PersonaRolModel(); var dao = mock(PersonaRolDAO.class); model.setPersonaRolDAO(dao);
        var registro = new PersonaRol(UUID.randomUUID());
        var persona = new Persona(UUID.randomUUID()); registro.setIdPersona(persona);
        var rol = new Rol(UUID.randomUUID()); rol.setActivo(true); registro.setIdRol(rol);
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(registro));
        var asignada = new Clinica(UUID.randomUUID()); asignada.setActivo(true); registro.setIdClinica(asignada);
        assertDoesNotThrow(() -> model.validarNegocio(registro));
        when(dao.existeAsignacion(persona.getIdPersona(), rol.getIdRol(), asignada.getIdClinica(), registro.getIdPersonaRol())).thenReturn(true);
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(registro));
        rol.setActivo(false);
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(registro));
        rol.setActivo(true);
        var clinica = new Clinica(UUID.randomUUID()); clinica.setActivo(false); registro.setIdClinica(clinica);
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(registro));
        clinica.setActivo(true);
        assertDoesNotThrow(() -> model.validarNegocio(registro));
    }

    @Test
    void losTresModelosClinicosRechazanFechasInvertidas() {
        var consulta = new Consulta(UUID.randomUUID()); consulta.setFechaInicio(new Date(100)); consulta.setFechaFin(new Date(1));
        var modelConsulta = new ConsultaModel(); modelConsulta.sesionBean = new SesionBean();
        var clinica = new Clinica(UUID.randomUUID()); clinica.setActivo(true); modelConsulta.sesionBean.setClinicaActual(clinica);
        var rol = new Rol(UUID.randomUUID()); rol.setActivo(true); rol.setNombre("Paciente");
        var paciente = new PersonaRol(UUID.randomUUID()); paciente.setIdClinica(clinica); paciente.setIdRol(rol); consulta.setIdPersonaRol(paciente);
        var cp = new ConsultaProcedimiento(UUID.randomUUID()); cp.setFechaInicio(new Date(100)); cp.setFechaFin(new Date(1));
        var paso = new ConsultaProcedimientoPaso(UUID.randomUUID()); paso.setFechaInicio(new Date(100)); paso.setFechaFin(new Date(1)); paso.setEstado("Registrado");
        assertThrows(ValidacionNegocioException.class, () -> modelConsulta.validarNegocio(consulta));
        assertThrows(ValidacionNegocioException.class, () -> new ConsultaProcedimientoModel().validarNegocio(cp));
        assertThrows(ValidacionNegocioException.class, () -> new ConsultaProcedimientoPasoModel().validarNegocio(paso));
        consulta.setFechaFin(new Date(100)); cp.setFechaFin(null); paso.setFechaFin(new Date(200));
        assertDoesNotThrow(() -> modelConsulta.validarNegocio(consulta));
        assertDoesNotThrow(() -> new ConsultaProcedimientoModel().validarNegocio(cp));
        assertDoesNotThrow(() -> new ConsultaProcedimientoPasoModel().validarNegocio(paso));
    }

    @Test
    void procedimientoDebeCaberEnConsultaYTenerCatalogoActivo() {
        var model = new ConsultaProcedimientoModel(); var registro = new ConsultaProcedimiento(UUID.randomUUID());
        var consulta = new Consulta(UUID.randomUUID()); consulta.setFechaInicio(new Date(100)); consulta.setFechaFin(new Date(300));
        registro.setIdConsulta(consulta); registro.setFechaInicio(new Date(1));
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(registro));
        registro.setFechaInicio(new Date(200)); registro.setFechaFin(new Date(400));
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(registro));
        registro.setFechaFin(new Date(300));
        var procedimiento = new Procedimiento(UUID.randomUUID()); procedimiento.setActivo(false); registro.setIdProcedimiento(procedimiento);
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(registro));
        procedimiento.setActivo(true);
        assertDoesNotThrow(() -> model.validarNegocio(registro));
    }

    @Test
    void ordenYResultadoExigenContenidoYPadre() {
        var orden = new OrdenExamen(UUID.randomUUID()); var model = new OrdenExamenModel();
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(orden));
        orden.setIndicaciones(" Tomar muestra ");
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(orden));
        orden.setIdConsultaProcedimientoPaso(new ConsultaProcedimientoPaso(UUID.randomUUID()));
        assertDoesNotThrow(() -> model.validarNegocio(orden));
        assertEquals("Tomar muestra", orden.getIndicaciones());
        var resultado = new ExamenResultado(UUID.randomUUID()); var modeloResultado = new ExamenResultadoModel();
        assertThrows(ValidacionNegocioException.class, () -> modeloResultado.validarNegocio(resultado));
        resultado.setResultado(" Normal "); resultado.setInterpretacion(" Sin alteraciones "); resultado.setIdOrdenExamen(orden);
        assertDoesNotThrow(() -> modeloResultado.validarNegocio(resultado));
        assertEquals("Normal", resultado.getResultado());
    }

    @Test
    void pasoValidaNombreDentroDeProcedimientoYResuelveReferenciaSinLista() {
        var model = new ProcedimientoPasoModel(); var dao = mock(ProcedimientoPasoDAO.class); model.setProcedimientoPasoDAO(dao);
        var paso = new ProcedimientoPaso(UUID.randomUUID()); paso.setNombre(" Paso ");
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(paso));
        var procedimiento = new Procedimiento(UUID.randomUUID()); procedimiento.setActivo(true); paso.setIdProcedimiento(procedimiento);
        var rol = new Rol(UUID.randomUUID()); rol.setActivo(true); paso.setIdRol(rol);
        assertDoesNotThrow(() -> model.validarNegocio(paso));
        when(dao.existeNombreEnProcedimiento(procedimiento.getIdProcedimiento(), "Paso", paso.getIdProcedimientoPaso())).thenReturn(true);
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(paso));
        assertNull(model.getRegistros());
        when(dao.findById(paso.getIdProcedimientoPaso())).thenReturn(paso);
        assertEquals("Paso", model.nombrePasoReferencia(paso.getIdProcedimientoPaso()));
        assertEquals("", model.nombrePasoReferencia(null));
        UUID perdido = UUID.randomUUID();
        assertEquals(perdido.toString(), model.nombrePasoReferencia(perdido));
        assertDoesNotThrow(() -> model.seleccionar(null));
        assertDoesNotThrow(() -> new ExamenModel().seleccionar(null));
    }

    @Test
    void catalogosDeFormatoRechazanRegexInvalida() {
        var documento = new TipoDocumento(UUID.randomUUID()); documento.setNombre("DUI"); documento.setExpresionRegular("[");
        assertThrows(ValidacionNegocioException.class, () -> new TipoDocumentoModel().validarNegocio(documento));
        var contacto = new TipoMedioContacto(UUID.randomUUID()); contacto.setNombre("Correo"); contacto.setExpresionRegular("(");
        assertThrows(ValidacionNegocioException.class, () -> new TipoMedioContactoModel().validarNegocio(contacto));
    }
}

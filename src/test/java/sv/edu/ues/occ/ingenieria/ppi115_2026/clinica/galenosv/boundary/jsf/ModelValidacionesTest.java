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
    void documentoDelegaValidacionAlServicio() {
        var model = new DocumentoModel(); var service = mock(DocumentoService.class); model.setDocumentoService(service);
        model.prepararNuevo();
        doThrow(new ValidacionNegocioException("regla de servicio")).when(service).create(any(Documento.class));
        model.guardar();
        verify(service).create(any(Documento.class));
        assertEquals(Estado.CREAR, model.getEstado());
        assertNotNull(model.getRegistroActual());
    }

    @Test
    void medioContactoDelegaValidacionAlServicio() {
        var model = new MedioContactoModel(); var service = mock(MedioContactoService.class); model.setMedioContactoService(service);
        model.prepararNuevo();
        doThrow(new ValidacionNegocioException("regla de servicio")).when(service).create(any(MedioContacto.class));
        model.guardar();
        verify(service).create(any(MedioContacto.class));
        assertEquals(Estado.CREAR, model.getEstado());
        assertNotNull(model.getRegistroActual());
    }

    @Test
    void personaRolExigeClinicaYRechazaDuplicadoEInactivos() {
        var model = new PersonaRolModel(); var dao = mock(PersonaRolService.class); model.setPersonaRolService(dao);
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

}

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
        var service = new PersonaService(mock(PersonaDAO.class));
        var persona = new Persona(UUID.randomUUID());
        assertThrows(ValidacionNegocioException.class, () -> service.crear(persona));
        persona.setNombres(" Ana "); persona.setApellidos(" Pérez ");
        persona.setFechaNacimiento(new Date(model.getHoy().getTime() + 86400000L));
        assertThrows(ValidacionNegocioException.class, () -> service.crear(persona));
        persona.setFechaNacimiento(java.util.Date.from(java.time.LocalDate.of(1970, 1, 1).atStartOfDay(java.time.ZoneId.of("America/El_Salvador")).toInstant()));
        assertDoesNotThrow(() -> service.crear(persona));
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
        var personaRolDAO = mock(PersonaRolDAO.class);
        var service = new PersonaRolService(personaRolDAO);
        var registro = new PersonaRol(UUID.randomUUID());
        var persona = new Persona(UUID.randomUUID()); registro.setIdPersona(persona);
        var rol = new Rol(UUID.randomUUID()); rol.setActivo(true); registro.setIdRol(rol);
        assertThrows(ValidacionNegocioException.class, () -> service.crear(registro));
        var asignada = new Clinica(UUID.randomUUID()); asignada.setActivo(true); registro.setIdClinica(asignada);
        assertDoesNotThrow(() -> service.crear(registro));
        when(personaRolDAO.existeAsignacion(persona.getIdPersona(), rol.getIdRol(), asignada.getIdClinica(), registro.getIdPersonaRol())).thenReturn(true);
        assertThrows(ValidacionNegocioException.class, () -> service.crear(registro));
        rol.setActivo(false);
        assertThrows(ValidacionNegocioException.class, () -> service.crear(registro));
        rol.setActivo(true);
        var clinica = new Clinica(UUID.randomUUID()); clinica.setActivo(false); registro.setIdClinica(clinica);
        assertThrows(ValidacionNegocioException.class, () -> service.crear(registro));
        clinica.setActivo(true);
        assertDoesNotThrow(() -> service.crear(registro));
    }

    @Test
    void losTresModelosClinicosRechazanFechasInvertidas() {
        var consulta = new Consulta(UUID.randomUUID()); consulta.setFechaInicio(new Date(100)); consulta.setFechaFin(new Date(1));
        var clinica = new Clinica(UUID.randomUUID()); clinica.setActivo(true);
        var rol = new Rol(UUID.randomUUID()); rol.setActivo(true); rol.setNombre("Paciente");
        var paciente = new PersonaRol(UUID.randomUUID()); paciente.setIdClinica(clinica); paciente.setIdRol(rol); consulta.setIdPersonaRol(paciente);
        var cp = new ConsultaProcedimiento(UUID.randomUUID()); cp.setFechaInicio(new Date(100)); cp.setFechaFin(new Date(1));
        var paso = new ConsultaProcedimientoPaso(UUID.randomUUID()); paso.setFechaInicio(new Date(100)); paso.setFechaFin(new Date(1)); paso.setEstado("Registrado");
        var cs = new ConsultaService(mock(ConsultaDAO.class));
        var cps = new ConsultaProcedimientoService(mock(ConsultaProcedimientoDAO.class));
        var cpps = new ConsultaProcedimientoPasoService(mock(ConsultaProcedimientoPasoDAO.class));
        assertThrows(ValidacionNegocioException.class, () -> cs.crear(consulta));
        assertThrows(ValidacionNegocioException.class, () -> cps.crear(cp));
        assertThrows(ValidacionNegocioException.class, () -> cpps.crear(paso));
        consulta.setFechaFin(new Date(100)); cp.setFechaFin(null); paso.setFechaFin(new Date(200));
        assertDoesNotThrow(() -> cs.crear(consulta));
        assertDoesNotThrow(() -> cps.crear(cp));
        assertDoesNotThrow(() -> cpps.crear(paso));
    }

    @Test
    void procedimientoDebeCaberEnConsultaYTenerCatalogoActivo() {
        var service = new ConsultaProcedimientoService(mock(ConsultaProcedimientoDAO.class));
        var registro = new ConsultaProcedimiento(UUID.randomUUID());
        var consulta = new Consulta(UUID.randomUUID()); consulta.setFechaInicio(new Date(100)); consulta.setFechaFin(new Date(300));
        registro.setIdConsulta(consulta); registro.setFechaInicio(new Date(1));
        assertThrows(ValidacionNegocioException.class, () -> service.crear(registro));
        registro.setFechaInicio(new Date(200)); registro.setFechaFin(new Date(400));
        assertThrows(ValidacionNegocioException.class, () -> service.crear(registro));
        registro.setFechaFin(new Date(300));
        var procedimiento = new Procedimiento(UUID.randomUUID()); procedimiento.setActivo(false); registro.setIdProcedimiento(procedimiento);
        assertThrows(ValidacionNegocioException.class, () -> service.crear(registro));
        procedimiento.setActivo(true);
        assertDoesNotThrow(() -> service.crear(registro));
    }

    @Test
    void ordenYResultadoExigenContenidoYPadre() {
        var orden = new OrdenExamen(UUID.randomUUID());
        var ordenService = new OrdenExamenService(mock(OrdenExamenDAO.class));
        assertThrows(ValidacionNegocioException.class, () -> ordenService.crear(orden));
        orden.setIndicaciones(" Tomar muestra ");
        assertThrows(ValidacionNegocioException.class, () -> ordenService.crear(orden));
        orden.setIdConsultaProcedimientoPaso(new ConsultaProcedimientoPaso(UUID.randomUUID()));
        assertDoesNotThrow(() -> ordenService.crear(orden));
        assertEquals("Tomar muestra", orden.getIndicaciones());
        var resultado = new ExamenResultado(UUID.randomUUID());
        var resultadoService = new ExamenResultadoService(mock(ExamenResultadoDAO.class));
        assertThrows(ValidacionNegocioException.class, () -> resultadoService.crear(resultado));
        resultado.setResultado(" Normal "); resultado.setInterpretacion(" Sin alteraciones "); resultado.setIdOrdenExamen(orden);
        assertDoesNotThrow(() -> resultadoService.crear(resultado));
        assertEquals("Normal", resultado.getResultado());
    }

}

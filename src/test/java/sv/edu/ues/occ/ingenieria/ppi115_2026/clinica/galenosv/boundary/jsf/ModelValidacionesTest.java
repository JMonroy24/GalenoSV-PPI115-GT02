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
    void modelosClinicosDeleganAlServicioInclusoConDatosInvalidos() {
        var procedimientos = new ConsultaProcedimientoModel();
        var servicioProcedimientos = mock(ConsultaProcedimientoService.class);
        procedimientos.setConsultaProcedimientoService(servicioProcedimientos);
        procedimientos.prepararNuevo();
        var procedimiento = procedimientos.getRegistroActual();
        procedimiento.setFechaInicio(new Date(200));
        procedimiento.setFechaFin(new Date(100));
        doThrow(new ValidacionNegocioException("Fechas inválidas.")).when(servicioProcedimientos).create(procedimiento);
        procedimientos.guardar();
        verify(servicioProcedimientos).create(procedimiento);
        assertTrue(procedimientos.isEstadoCrear());

        var pasos = new ConsultaProcedimientoPasoModel();
        var servicioPasos = mock(ConsultaProcedimientoPasoService.class);
        pasos.setConsultaProcedimientoPasoService(servicioPasos);
        pasos.prepararNuevo();
        var paso = pasos.getRegistroActual();
        paso.setEstado(" ");
        doThrow(new ValidacionNegocioException("Estado obligatorio.")).when(servicioPasos).create(paso);
        pasos.guardar();
        verify(servicioPasos).create(paso);
        assertTrue(pasos.isEstadoCrear());

        var resultados = new ExamenResultadoModel();
        var servicioResultados = mock(ExamenResultadoService.class);
        resultados.setExamenResultadoService(servicioResultados);
        resultados.prepararNuevo();
        var resultado = resultados.getRegistroActual();
        doThrow(new ValidacionNegocioException("Resultado obligatorio.")).when(servicioResultados).create(resultado);
        resultados.guardar();
        verify(servicioResultados).create(resultado);
        assertTrue(resultados.isEstadoCrear());
    }

    @Test
    void ordenExigeContenidoYPadre() {
        var orden = new OrdenExamen(UUID.randomUUID()); var model = new OrdenExamenModel();
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(orden));
        orden.setIndicaciones(" Tomar muestra ");
        assertThrows(ValidacionNegocioException.class, () -> model.validarNegocio(orden));
        orden.setIdConsultaProcedimientoPaso(new ConsultaProcedimientoPaso(UUID.randomUUID()));
        assertDoesNotThrow(() -> model.validarNegocio(orden));
        assertEquals("Tomar muestra", orden.getIndicaciones());
    }

}

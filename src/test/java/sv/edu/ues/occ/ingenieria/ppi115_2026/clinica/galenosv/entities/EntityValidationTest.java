package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.validation.PeriodoFechas;

/** Ejecuta Bean Validation real, sin simular el proveedor ni los constraints. */
class EntityValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void crearValidador() {
        factory = Validation.byDefaultProvider().configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .clockProvider(() -> Clock.fixed(Instant.parse("2026-09-29T12:00:00Z"), ZoneOffset.UTC))
                .buildValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void cerrarValidador() {
        factory.close();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t\n"})
    void nombresYApellidosRechazanBlancos(String blanco) {
        Persona persona = personaValida();
        persona.setNombres(blanco);
        persona.setApellidos(blanco);

        assertEquals(Set.of("nombres", "apellidos"), propiedadesInvalidas(persona));
        assertTrue(validator.validate(persona).stream()
                .allMatch(v -> !v.getMessage().contains("{") && v.getMessage().contains("obligatorios")));
    }

    @Test
    void nacimientoEsOpcionalPeroDebeSerPasadoSiSeProporciona() {
        Persona persona = personaValida();
        assertTrue(validator.validate(persona).isEmpty());

        persona.setFechaNacimiento(java.util.Date.from(java.time.LocalDate.of(2026, 9, 30).atStartOfDay(java.time.ZoneId.of("America/El_Salvador")).toInstant()));
        assertEquals(Set.of("fechaNacimiento"), propiedadesInvalidas(persona));

        persona.setFechaNacimiento(java.util.Date.from(java.time.LocalDate.of(1990, 1, 1).atStartOfDay(java.time.ZoneId.of("America/El_Salvador")).toInstant()));
        assertTrue(validator.validate(persona).isEmpty());
    }

    @Test
    void documentoExigePersonaTipoYValorYRespetaLimites() {
        Documento documento = new Documento(UUID.randomUUID());
        documento.setValor("   ");
        assertEquals(Set.of("idPersona", "idTipoDocumento", "valor"), propiedadesInvalidas(documento));

        documento.setIdPersona(personaValida());
        documento.setIdTipoDocumento(new TipoDocumento(UUID.randomUUID()));
        documento.setValor("1".repeat(50));
        documento.setRutaFisica("x".repeat(500));
        assertTrue(validator.validate(documento).isEmpty());

        documento.setValor("1".repeat(51));
        documento.setRutaFisica("x".repeat(501));
        assertEquals(Set.of("valor", "rutaFisica"), propiedadesInvalidas(documento));
    }

    @Test
    void medioContactoNoPuedePersistirseSinDatosObligatorios() {
        MedioContacto contacto = new MedioContacto(UUID.randomUUID());
        assertEquals(Set.of("idPersona", "idTipoMedioContacto", "valor"), propiedadesInvalidas(contacto));
        contacto.setIdPersona(personaValida());
        contacto.setIdTipoMedioContacto(new TipoMedioContacto(UUID.randomUUID()));
        contacto.setValor("persona@example.org");
        assertTrue(validator.validate(contacto).isEmpty());
        contacto.setValor("x".repeat(256));
        assertEquals(Set.of("valor"), propiedadesInvalidas(contacto));
    }

    @Test
    void catalogoExigeNombreNoBlancoYActivoNoNulo() {
        Clinica clinica = new Clinica(UUID.randomUUID());
        clinica.setNombre(" ");
        assertEquals(Set.of("nombre", "activo"), propiedadesInvalidas(clinica));
        clinica.setNombre("Clínica central");
        clinica.setActivo(false);
        assertTrue(validator.validate(clinica).isEmpty());
        clinica.setComentarios("x".repeat(2001));
        assertEquals(Set.of("comentarios"), propiedadesInvalidas(clinica));
    }

    @ParameterizedTest
    @MethodSource("tiposConExpresion")
    void tiposRechazanRegexInvalidaYAdmitenFormatoOpcional(Object tipo, Consumer<String> asignar) {
        asignar.accept("[");
        assertEquals(Set.of("expresionRegular"), propiedadesInvalidas(tipo));
        assertEquals("La expresión regular no es válida.", validator.validate(tipo).iterator().next().getMessage());
        asignar.accept("[0-9]{8}-[0-9]");
        assertTrue(validator.validate(tipo).isEmpty());
        asignar.accept(null);
        assertTrue(validator.validate(tipo).isEmpty());
        asignar.accept(" ");
        assertTrue(validator.validate(tipo).isEmpty());
        asignar.accept("a".repeat(501));
        assertEquals(Set.of("expresionRegular"), propiedadesInvalidas(tipo));
    }

    static Stream<Arguments> tiposConExpresion() {
        TipoDocumento documento = new TipoDocumento(UUID.randomUUID());
        documento.setNombre("DUI");
        documento.setActivo(true);
        TipoMedioContacto contacto = new TipoMedioContacto(UUID.randomUUID());
        contacto.setNombre("Correo");
        contacto.setActivo(true);
        return Stream.of(Arguments.of(documento, (Consumer<String>) documento::setExpresionRegular),
                Arguments.of(contacto, (Consumer<String>) contacto::setExpresionRegular));
    }

    @ParameterizedTest
    @MethodSource("periodosClinicos")
    void rangoEsAbiertoOIgualOPosteriorNuncaInvertido(PeriodoFechas periodo, Consumer<Date> asignarFin) {
        assertTrue(validator.validate(periodo).isEmpty());
        asignarFin.accept(new Date(periodo.getFechaInicio().getTime()));
        assertTrue(validator.validate(periodo).isEmpty());
        asignarFin.accept(new Date(periodo.getFechaInicio().getTime() + 1000));
        assertTrue(validator.validate(periodo).isEmpty());
        asignarFin.accept(new Date(periodo.getFechaInicio().getTime() - 1));
        assertEquals(Set.of("fechaFin"), propiedadesInvalidas(periodo));
    }

    static Stream<Arguments> periodosClinicos() {
        Consulta consulta = new Consulta(UUID.randomUUID());
        consulta.setIdPersonaRol(new PersonaRol(UUID.randomUUID()));
        consulta.setFechaInicio(new Date(10000));
        ConsultaProcedimiento procedimiento = new ConsultaProcedimiento(UUID.randomUUID());
        procedimiento.setIdConsulta(consulta);
        procedimiento.setIdProcedimiento(new Procedimiento(UUID.randomUUID()));
        procedimiento.setFechaInicio(new Date(10000));
        ConsultaProcedimientoPaso paso = new ConsultaProcedimientoPaso(UUID.randomUUID());
        paso.setIdConsultaProcedimiento(procedimiento);
        paso.setIdPersonaRol(new PersonaRol(UUID.randomUUID()));
        paso.setFechaInicio(new Date(10000));
        paso.setEstado("ESTADO_EXISTENTE");
        return Stream.of(Arguments.of(consulta, (Consumer<Date>) consulta::setFechaFin),
                Arguments.of(procedimiento, (Consumer<Date>) procedimiento::setFechaFin),
                Arguments.of(paso, (Consumer<Date>) paso::setFechaFin));
    }

    @Test
    void atencionesRequierenInicioYAsociaciones() {
        assertEquals(Set.of("fechaInicio", "idPersonaRol"),
                propiedadesInvalidas(new Consulta(UUID.randomUUID())));
        assertEquals(Set.of("fechaInicio", "idConsulta", "idProcedimiento"),
                propiedadesInvalidas(new ConsultaProcedimiento(UUID.randomUUID())));
        assertEquals(Set.of("fechaInicio", "idConsultaProcedimiento", "idPersonaRol", "estado"),
                propiedadesInvalidas(new ConsultaProcedimientoPaso(UUID.randomUUID())));
    }

    @Test
    void ordenYResultadoRechazanTextosEnBlanco() {
        OrdenExamen orden = new OrdenExamen(UUID.randomUUID());
        orden.setIndicaciones(" ");
        assertEquals(Set.of("idConsultaProcedimientoPaso", "indicaciones"), propiedadesInvalidas(orden));
        ExamenResultado resultado = new ExamenResultado(UUID.randomUUID());
        resultado.setResultado(" ");
        resultado.setInterpretacion("\n");
        assertEquals(Set.of("idOrdenExamen", "resultado", "interpretacion"), propiedadesInvalidas(resultado));
        resultado.setIdOrdenExamen(orden);
        resultado.setResultado("x".repeat(4000));
        resultado.setInterpretacion("Sin alteraciones");
        assertTrue(validator.validate(resultado).isEmpty());
        resultado.setResultado("x".repeat(4001));
        assertEquals(Set.of("resultado"), propiedadesInvalidas(resultado));
    }

    @Test
    void clinicaYRolDePasoSiguenSiendoOpcionalesComoEnElFormulario() {
        PersonaRol asignacion = new PersonaRol(UUID.randomUUID());
        asignacion.setIdPersona(personaValida());
        asignacion.setIdRol(new Rol(UUID.randomUUID()));
        assertTrue(validator.validate(asignacion).isEmpty());

        ProcedimientoPaso paso = new ProcedimientoPaso(UUID.randomUUID());
        paso.setNombre("Admisión");
        paso.setIdProcedimiento(new Procedimiento(UUID.randomUUID()));
        assertEquals(Boolean.FALSE, paso.getIndicaFin());
        assertTrue(validator.validate(paso).isEmpty());
        paso.setIndicaFin(null);
        assertEquals(Set.of("indicaFin"), propiedadesInvalidas(paso));
    }

    @Test
    void relacionesExigenReferenciasYEstadoExplicito() {
        assertEquals(Set.of("idExamen", "idTipoExamen"),
                propiedadesInvalidas(new ExamenTipoExamen(UUID.randomUUID())));
        assertEquals(Set.of("idExamen", "idProcedimientoPaso", "activo"),
                propiedadesInvalidas(new ProcedimientoPasoExamen(UUID.randomUUID())));
        assertEquals(Set.of("idProcedimientoPaso", "idProcedimientoPasoReferencia", "tipoSecuencia"),
                propiedadesInvalidas(new ProcedimientoPasoSecuencia(UUID.randomUUID())));
    }

    private static Persona personaValida() {
        Persona persona = new Persona(UUID.randomUUID());
        persona.setNombres("María José");
        persona.setApellidos("De León");
        return persona;
    }

    private static Set<String> propiedadesInvalidas(Object entidad) {
        return validator.validate(entidad).stream().map(ConstraintViolation::getPropertyPath)
                .map(Object::toString).collect(java.util.stream.Collectors.toSet());
    }
}

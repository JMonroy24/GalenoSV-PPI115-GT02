package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Size;
import java.util.stream.Stream;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LongitudDTOTest {

    private static final ValidatorFactory FACTORY = Validation.byDefaultProvider().configure()
            .messageInterpolator(new ParameterMessageInterpolator()).buildValidatorFactory();

    @AfterAll
    static void cerrarValidador() {
        FACTORY.close();
    }

    @ParameterizedTest(name = "{0}.{1} admite hasta {2} caracteres")
    @MethodSource("limitesAuditados")
    void compruebaLimiteExactoYExceso(Class<?> dto, String campo, int max) {
        var validator = FACTORY.getValidator();
        assertTrue(validator.validateValue(dto, campo, "x".repeat(max)).isEmpty());
        var violaciones = validator.validateValue(dto, campo, "x".repeat(max + 1));
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(violacion ->
                violacion.getConstraintDescriptor().getAnnotation() instanceof Size));
    }

    static Stream<Arguments> limitesAuditados() {
        return Stream.of(
                Arguments.of(ClinicaDTO.class, "nombre", 255),
                Arguments.of(ClinicaDTO.class, "tipo", 20),
                Arguments.of(ClinicaDTO.class, "comentarios", 2000),
                Arguments.of(ConsultaDTO.class, "referenciaExterna", 255),
                Arguments.of(ConsultaDTO.class, "observaciones", 2000),
                Arguments.of(ConsultaProcedimientoDTO.class, "observaciones", 2000),
                Arguments.of(ConsultaProcedimientoPasoDTO.class, "estado", 20),
                Arguments.of(DocumentoDTO.class, "valor", 50),
                Arguments.of(ExamenDTO.class, "nombre", 255),
                Arguments.of(ExamenDTO.class, "observaciones", 2000),
                Arguments.of(ExamenResultadoDTO.class, "resultado", 4000),
                Arguments.of(ExamenResultadoDTO.class, "interpretacion", 4000),
                Arguments.of(ExamenResultadoDTO.class, "rutaAtestado", 500),
                Arguments.of(ExamenTipoExamenDTO.class, "observaciones", 2000),
                Arguments.of(MedioContactoDTO.class, "valor", 255),
                Arguments.of(OrdenExamenDTO.class, "indicaciones", 2000),
                Arguments.of(PersonaDTO.class, "nombres", 255),
                Arguments.of(PersonaDTO.class, "apellidos", 255),
                Arguments.of(ProcedimientoDTO.class, "nombre", 155),
                Arguments.of(ProcedimientoDTO.class, "observaciones", 2000),
                Arguments.of(ProcedimientoPasoDTO.class, "nombre", 155),
                Arguments.of(ProcedimientoPasoExamenDTO.class, "observaciones", 2000),
                Arguments.of(ProcedimientoPasoSecuenciaDTO.class, "tipoSecuencia", 20),
                Arguments.of(RolDTO.class, "nombre", 155),
                Arguments.of(RolDTO.class, "observaciones", 2000),
                Arguments.of(TipoDocumentoDTO.class, "nombre", 155),
                Arguments.of(TipoDocumentoDTO.class, "indicaciones", 2000),
                Arguments.of(TipoDocumentoDTO.class, "expresionRegular", 500),
                Arguments.of(TipoExamenDTO.class, "nombre", 255),
                Arguments.of(TipoExamenDTO.class, "observaciones", 2000),
                Arguments.of(TipoMedioContactoDTO.class, "nombre", 155),
                Arguments.of(TipoMedioContactoDTO.class, "indicaciones", 2000),
                Arguments.of(TipoMedioContactoDTO.class, "expresionRegular", 500));
    }
}

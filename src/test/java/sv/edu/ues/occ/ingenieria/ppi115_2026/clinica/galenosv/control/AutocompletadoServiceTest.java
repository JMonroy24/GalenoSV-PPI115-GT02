package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.persistence.PersistenceException;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Contrato común: las entradas vacías nunca llegan al DAO. */
class AutocompletadoServiceTest {
    static Stream<Arguments> servicios() {
        return Stream.of(ConsultaService.class, ConsultaProcedimientoService.class,
                ConsultaProcedimientoPasoService.class, OrdenExamenService.class,
                PersonaService.class, PersonaRolService.class, ExamenService.class,
                ProcedimientoService.class, RolService.class, TipoExamenService.class)
                .map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("servicios")
    @SuppressWarnings({"rawtypes", "unchecked"})
    void filtrosVaciosNoInvocanServiciosSubyacentes(Class<? extends DefaultService> tipo) throws Exception {
        DefaultService servicio = tipo.getConstructor().newInstance();
        var campo = Stream.of(tipo.getDeclaredFields())
                .filter(f -> DefaultDAO.class.isAssignableFrom(f.getType())).findFirst().orElseThrow();
        campo.setAccessible(true);
        DefaultDAO dao = mock((Class<? extends DefaultDAO>) campo.getType());
        campo.set(servicio, dao);
        for (String filtro : new String[]{null, "", "  ", "\t\n", "\u2003"}) {
            assertTrue(servicio.buscarParaAutocompletar(filtro, 20).isEmpty(), tipo.getSimpleName());
        }
        verifyNoInteractions(dao);
    }

    @ParameterizedTest
    @MethodSource("servicios")
    @SuppressWarnings({"rawtypes", "unchecked"})
    void busquedaValidaNormalizaYPropagaLaMismaCausa(Class<? extends DefaultService> tipo) throws Exception {
        DefaultService servicio = tipo.getConstructor().newInstance();
        var campo = Stream.of(tipo.getDeclaredFields())
                .filter(f -> DefaultDAO.class.isAssignableFrom(f.getType())).findFirst().orElseThrow();
        campo.setAccessible(true);
        DefaultDAO dao = mock((Class<? extends DefaultDAO>) campo.getType());
        campo.set(servicio, dao);
        List resultado = List.of(new Object());
        when(dao.buscarParaAutocompletar("Ana", 20)).thenReturn(resultado);
        assertSame(resultado, servicio.buscarParaAutocompletar("  Ana  ", 20));
        var error = new PersistenceException("Fallo de conexión");
        when(dao.buscarParaAutocompletar("Ana", 20)).thenThrow(error);
        assertSame(error, assertThrows(PersistenceException.class,
                () -> servicio.buscarParaAutocompletar("Ana", 20)));
    }
}

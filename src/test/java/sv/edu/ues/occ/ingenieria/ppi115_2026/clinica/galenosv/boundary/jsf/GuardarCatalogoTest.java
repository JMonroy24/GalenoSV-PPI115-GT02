package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** El mismo contrato se ejecuta sobre los modelos reales, sin sobrescribir guardar. */
class GuardarCatalogoTest {
    static Stream<Arguments> catalogos() {
        return Stream.of(RolModel.class, TipoExamenModel.class,
                TipoDocumentoModel.class, TipoMedioContactoModel.class)
                .flatMap(tipo -> Stream.of(Estado.CREAR, Estado.MODIFICAR)
                        .map(estado -> Arguments.of(tipo, estado)));
    }

    @ParameterizedTest
    @MethodSource("catalogos")
    @SuppressWarnings({"rawtypes", "unchecked"})
    void guardarDelegaUnaSolaOperacionYLiberaFormulario(Class<? extends Model> tipo, Estado estado) throws Exception {
        Model modelo = tipo.getConstructor().newInstance();
        var campo = Stream.of(tipo.getDeclaredFields())
                .filter(f -> DefaultService.class.isAssignableFrom(f.getType())).findFirst().orElseThrow();
        campo.setAccessible(true);
        DefaultService servicio = mock((Class<? extends DefaultService>) campo.getType());
        campo.set(modelo, servicio);
        modelo.prepararNuevo();
        Object registro = modelo.getRegistroActual();
        modelo.setEstado(estado);

        modelo.guardar();

        if (estado == Estado.CREAR) verify(servicio).create(registro);
        else verify(servicio).update(registro);
        verifyNoMoreInteractions(servicio);
        assertEquals(Estado.NINGUNO, modelo.getEstado());
        assertNull(modelo.getRegistroActual());
    }
}

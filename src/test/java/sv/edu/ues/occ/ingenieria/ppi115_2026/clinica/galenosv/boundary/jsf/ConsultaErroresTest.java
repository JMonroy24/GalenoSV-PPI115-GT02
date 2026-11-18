package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.persistence.PersistenceException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConsultaErroresTest {
    static Stream<Model<?, ?>> modelos() {
        return Stream.of(new ConsultaModel(), new ConsultaProcedimientoModel());
    }

    @AfterEach
    void liberar() { Contexto.instalar(null); }

    @ParameterizedTest
    @MethodSource("modelos")
    void muestraCausaSqlAnidadaEImprimeStacktraceCompleto(Model<?, ?> modelo) {
        var contexto = mock(FacesContext.class);
        when(contexto.getAttributes()).thenReturn(new HashMap<>());
        Contexto.instalar(contexto);
        var error = spy(new PersistenceException("Transacción fallida",
                new SQLException("La columna valor no existe", "42703")));

        modelo.notificarErrorEscritura("guardar registro", error);

        verify(error).printStackTrace();
        var mensaje = ArgumentCaptor.forClass(FacesMessage.class);
        verify(contexto).addMessage(isNull(), mensaje.capture());
        assertEquals(FacesMessage.SEVERITY_ERROR, mensaje.getValue().getSeverity());
        assertEquals("La columna valor no existe", mensaje.getValue().getDetail());
    }

    @ParameterizedTest
    @MethodSource("modelos")
    void excepcionSinMensajeUsaToString(Model<?, ?> modelo) {
        var contexto = mock(FacesContext.class);
        Contexto.instalar(contexto);
        modelo.notificarErrorEscritura("guardar registro", new IllegalStateException());
        var mensaje = ArgumentCaptor.forClass(FacesMessage.class);
        verify(contexto).addMessage(isNull(), mensaje.capture());
        assertEquals("java.lang.IllegalStateException", mensaje.getValue().getDetail());
    }

    private abstract static class Contexto extends FacesContext {
        static void instalar(FacesContext contexto) { setCurrentInstance(contexto); }
    }
}

package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ValidacionNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;

@ExtendWith(MockitoExtension.class)
class ModelTest {

    @Mock
    private DAOInterface<Persona, UUID> dao;
    @Mock
    private FacesContext context;
    private ModeloPrueba model;

    @BeforeEach
    void prepararContexto() {
        ContextoAccesible.instalar(context);
        model = new ModeloPrueba(dao);
    }

    @AfterEach
    void liberarContexto() {
        ContextoAccesible.instalar(null);
    }

    @ParameterizedTest
    @EnumSource(value = Estado.class, names = {"CREAR", "MODIFICAR"})
    void guardarConReglaInvalidaConservaFormularioYMarcaValidacion(Estado estado) {
        model.prepararNuevo();
        model.setEstado(estado);
        Persona editada = model.getRegistroActual();
        editada.setNombres("Datos todavía editables");
        model.errorValidacion = new ValidacionNegocioException("La fecha de fin debe ser posterior al inicio.");

        model.guardar();

        verifyNoInteractions(dao);
        assertSame(editada, model.getRegistroActual());
        assertEquals(estado, model.getEstado());
        assertEquals("Datos todavía editables", editada.getNombres());
        verify(context).validationFailed();
        assertEquals("La fecha de fin debe ser posterior al inicio.", ultimoMensaje().getDetail());
    }

    @Test
    void errorPersistenciaTambienConservaEstadoYActivaValidationFailed() {
        model.prepararNuevo();
        Persona editada = model.getRegistroActual();
        doThrow(new PersistenceException(new SQLException("mensaje traducido", "23505"))).when(dao).create(editada);

        model.guardar();

        assertSame(editada, model.getRegistroActual());
        assertEquals(Estado.CREAR, model.getEstado());
        verify(context).validationFailed();
        verify(dao, never()).findAll();
        assertTrue(ultimoMensaje().getDetail().contains("Ya existe"));
    }

    @Test
    void guardarSinOperacionNoEscribeNiCierraSilenciosamenteElDialogo() {
        model.guardar();
        verifyNoInteractions(dao);
        verify(context).validationFailed();
        assertEquals(Estado.NINGUNO, model.getEstado());
    }

    @ParameterizedTest
    @EnumSource(value = Estado.class, names = {"CREAR", "MODIFICAR"})
    void guardadoValidoPersisteYLiberaFormulario(Estado estado) {
        model.prepararNuevo();
        model.setEstado(estado);
        Persona editada = model.getRegistroActual();
        when(dao.findAll()).thenReturn(List.of(editada));

        model.guardar();

        assertSame(editada, model.registroValidado);
        if (estado == Estado.CREAR) verify(dao).create(editada);
        else verify(dao).update(editada);
        assertEquals(List.of(editada), model.getRegistros());
        assertNull(model.getRegistroActual());
        assertEquals(Estado.NINGUNO, model.getEstado());
        verify(context, never()).validationFailed();
    }

    @Test
    void eliminarNullNoTocaOperacionEnCurso() {
        model.prepararNuevo();
        Persona editada = model.getRegistroActual();
        assertDoesNotThrow(() -> model.eliminar(null));
        verifyNoInteractions(dao);
        assertSame(editada, model.getRegistroActual());
        assertEquals(Estado.CREAR, model.getEstado());
        assertEquals(FacesMessage.SEVERITY_WARN, ultimoMensaje().getSeverity());
    }

    @ParameterizedTest
    @EnumSource(value = Estado.class, names = {"NINGUNO", "CREAR", "MODIFICAR"})
    void borrarFallidoRestauraEstadoAnteriorYSeleccion(Estado estado) {
        Persona persona = new Persona(UUID.randomUUID());
        model.setEstado(estado);
        model.setRegistroActual(persona);
        model.setSeleccion(persona);
        doThrow(new PersistenceException(new SQLException("FK", "23503"))).when(dao).delete(persona);

        model.eliminar(persona);

        assertEquals(estado, model.getEstado());
        assertSame(persona, model.getRegistroActual());
        assertSame(persona, model.getSeleccion());
        verify(dao, never()).findAll();
        assertTrue(ultimoMensaje().getDetail().contains("relacionado"));
    }

    @Test
    void borrarOtraFilaConservaEdicionActual() {
        Persona editada = new Persona(UUID.randomUUID());
        Persona borrada = new Persona(UUID.randomUUID());
        model.seleccionar(editada);
        when(dao.findAll()).thenReturn(List.of(editada));

        model.eliminar(borrada);

        assertEquals(Estado.MODIFICAR, model.getEstado());
        assertSame(editada, model.getRegistroActual());
    }

    @Test
    void borrarFilaEditadaLimpiaSeleccionYFormulario() {
        Persona editada = new Persona(UUID.randomUUID());
        model.seleccionar(editada);
        model.setSeleccion(editada);
        when(dao.findAll()).thenReturn(List.of());
        model.eliminar(editada);
        assertEquals(Estado.NINGUNO, model.getEstado());
        assertNull(model.getRegistroActual());
        assertNull(model.getSeleccion());
    }

    @ParameterizedTest
    @CsvSource({"23505,Ya existe", "23503,relacionado", "23502,obligatorios", "22001,longitud",
            "23514,reglas", "40001,Otro usuario", "40P01,Otro usuario", "55P03,Otro usuario"})
    void clasificaSqlStateSinDependerDelIdioma(String state, String fragmento) {
        String mensaje = model.clasificarError(new PersistenceException(
                new SQLException("contenido de la base no apto para mostrar", state)));
        assertTrue(mensaje.contains(fragmento), mensaje);
        assertFalse(mensaje.contains("contenido de la base"));
    }

    @Test
    void detectaSqlStateEnNextExceptionDeBatch() {
        SQLException batch = new SQLException("batch fallido", "HY000");
        batch.setNextException(new SQLException("duplicado", "23505"));
        assertTrue(model.clasificarError(new PersistenceException(batch)).contains("Ya existe"));
    }

    @Test
    void errorDePersistenciaNoExponeDetalleInterno() {
        String mensaje = model.clasificarError(new PersistenceException("detalle interno"));
        assertTrue(mensaje.contains("No fue posible"));
        assertFalse(mensaje.contains("detalle interno"));
    }

    @Test
    void validacionesMuestranMensajesSinRutasNiValoresClinicos() {
        @SuppressWarnings("unchecked") ConstraintViolation<Object> primera = mock(ConstraintViolation.class);
        @SuppressWarnings("unchecked") ConstraintViolation<Object> segunda = mock(ConstraintViolation.class);
        when(primera.getMessage()).thenReturn("Seleccione una persona.");
        when(segunda.getMessage()).thenReturn("La fecha de inicio es obligatoria.");
        ConstraintViolationException error = new ConstraintViolationException("Datos inválidos", Set.of(primera, segunda));
        clearInvocations(primera, segunda);
        String mensaje = model.clasificarError(error);
        assertEquals("La fecha de inicio es obligatoria.; Seleccione una persona.", mensaje);
        verify(primera, never()).getInvalidValue();
        verify(primera, never()).getPropertyPath();
    }

    @Test
    void errorDesconocidoNoFiltraInformacionDelServidor() {
        assertEquals("No fue posible completar la operación. Intente nuevamente o contacte al administrador.",
                model.clasificarError(new RuntimeException("valor sensible unique llave duplicada")));
    }

    @Test
    void causasCiclicasNoBloqueanClasificacion() {
        RuntimeException primera = new RuntimeException("primera");
        RuntimeException segunda = new RuntimeException("segunda", primera);
        primera.initCause(segunda);
        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
            assertTrue(model.clasificarError(primera).startsWith("No fue posible"));
        });
    }

    private FacesMessage ultimoMensaje() {
        ArgumentCaptor<FacesMessage> captor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(context, atLeastOnce()).addMessage(isNull(), captor.capture());
        return captor.getValue();
    }

    private static class ModeloPrueba extends Model<Persona, UUID> {
        private final DAOInterface<Persona, UUID> dao;
        private RuntimeException errorValidacion;
        private Persona registroValidado;

        ModeloPrueba(DAOInterface<Persona, UUID> dao) { this.dao = dao; }
        @Override protected DAOInterface<Persona, UUID> getDAO() { return dao; }
        @Override protected Persona crearNuevoRegistro() { return new Persona(UUID.randomUUID()); }
        @Override protected void validarNegocio(Persona registro) {
            registroValidado = registro;
            if (errorValidacion != null) throw errorValidacion;
        }
    }

    private abstract static class ContextoAccesible extends FacesContext {
        static void instalar(FacesContext context) { setCurrentInstance(context); }
    }
}

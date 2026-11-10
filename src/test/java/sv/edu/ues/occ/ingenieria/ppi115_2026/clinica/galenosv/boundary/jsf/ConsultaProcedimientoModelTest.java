package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.persistence.PersistenceException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimiento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
public class ConsultaProcedimientoModelTest {

    @Mock
    private ConsultaProcedimientoService consultaProcedimientoService;

    @Mock
    private ConsultaService consultaService;

    @InjectMocks
    private ConsultaProcedimientoModel consultaProcedimientoModel;

    private org.mockito.MockedStatic<jakarta.faces.context.FacesContext> faces;

    @org.junit.jupiter.api.AfterEach
    void closeFaces() { faces.close(); }

    @BeforeEach
    public void setUp() {
        faces = mockStatic(jakarta.faces.context.FacesContext.class);
        consultaProcedimientoModel.setConsultaProcedimientoService(consultaProcedimientoService);
    }

    @Test
    public void testInitYCargarDatos() {
        consultaProcedimientoModel.init();
        assertNotNull(consultaProcedimientoModel.getLazyModel());
        assertNull(consultaProcedimientoModel.getRegistros());

    }

    @Test
    public void testPrepararNuevo() {
        consultaProcedimientoModel.prepararNuevo();

        assertNotNull(consultaProcedimientoModel.getRegistroActual());
        assertEquals(Estado.CREAR, consultaProcedimientoModel.getEstado());
        assertTrue(consultaProcedimientoModel.isEstadoCrear());
    }

    @Test
    public void testSeleccionar() {
        ConsultaProcedimiento cp = new ConsultaProcedimiento(UUID.randomUUID());
        consultaProcedimientoModel.seleccionar(cp);

        assertEquals(cp, consultaProcedimientoModel.getRegistroActual());
        assertEquals(Estado.MODIFICAR, consultaProcedimientoModel.getEstado());
        assertTrue(consultaProcedimientoModel.isEstadoModificar());
    }

    @Test
    public void testCancelar() {
        consultaProcedimientoModel.prepararNuevo();
        consultaProcedimientoModel.cancelar();

        assertNull(consultaProcedimientoModel.getRegistroActual());
        assertEquals(Estado.NINGUNO, consultaProcedimientoModel.getEstado());
        assertTrue(consultaProcedimientoModel.isEstadoNinguno());
    }

    @Test
    public void testGuardarCrear() {
        consultaProcedimientoModel.prepararNuevo();
        consultaProcedimientoModel.getRegistroActual().setObservaciones("Procedimiento de prueba");

        consultaProcedimientoModel.guardar();

        verify(consultaProcedimientoService).create(any(ConsultaProcedimiento.class));
        assertEquals(Estado.NINGUNO, consultaProcedimientoModel.getEstado());
        assertNull(consultaProcedimientoModel.getRegistroActual());
    }

    @Test
    public void testGuardarModificar() {
        ConsultaProcedimiento cp = new ConsultaProcedimiento(UUID.randomUUID());
        consultaProcedimientoModel.seleccionar(cp);

        consultaProcedimientoModel.guardar();

        verify(consultaProcedimientoService).update(cp);
        assertEquals(Estado.NINGUNO, consultaProcedimientoModel.getEstado());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", " a ", "\u2003"})
    void completarConsultaConFiltroInsuficienteNoInvocaElServicio(String filtro) {
        assertTrue(consultaProcedimientoModel.completeConsulta(filtro).isEmpty());
        verifyNoInteractions(consultaService);
    }

    @Test
    void completarConsultaDevuelveCoincidenciasDelFiltroNormalizado() {
        List<Consulta> esperadas = List.of(new Consulta(UUID.randomUUID()));
        when(consultaService.buscarParaAutocompletar("Ana Pérez", 20)).thenReturn(esperadas);

        assertEquals(esperadas, consultaProcedimientoModel.completeConsulta("  Ana Pérez  "));
        verify(consultaService).buscarParaAutocompletar("Ana Pérez", 20);
    }

    @Test
    void completarConsultaSinCoincidenciasNoAgregaUnMensajeDeError() {
        FacesContext contexto = mock(FacesContext.class);
        faces.when(FacesContext::getCurrentInstance).thenReturn(contexto);
        when(consultaService.buscarParaAutocompletar("zz", 20)).thenReturn(List.of());

        assertTrue(consultaProcedimientoModel.completeConsulta("zz").isEmpty());
        verify(contexto, never()).addMessage(any(), any());
    }

    @Test
    void completarConsultaNotificaUnFalloDeConexionSinExponerDetalles() {
        FacesContext contexto = mock(FacesContext.class);
        faces.when(FacesContext::getCurrentInstance).thenReturn(contexto);
        var fallo = new PersistenceException(new SQLException("credencial privada", "28P01"));
        when(consultaService.buscarParaAutocompletar("Ana", 20)).thenThrow(fallo);

        assertTrue(consultaProcedimientoModel.completeConsulta("Ana").isEmpty());
        var mensaje = ArgumentCaptor.forClass(FacesMessage.class);
        verify(contexto).addMessage(isNull(), mensaje.capture());
        assertEquals(FacesMessage.SEVERITY_ERROR, mensaje.getValue().getSeverity());
        assertTrue(mensaje.getValue().getDetail().contains("conectar a la base de datos"));
        assertFalse(mensaje.getValue().getDetail().contains("credencial privada"));
    }

    @Test
    public void testEliminar() {
        ConsultaProcedimiento cp = new ConsultaProcedimiento(UUID.randomUUID());
        consultaProcedimientoModel.eliminar(cp);

        verify(consultaProcedimientoService).delete(cp);
    }

    @Test
    public void testGettersAndSetters() {
        consultaProcedimientoModel.init();
        assertEquals(consultaProcedimientoService, consultaProcedimientoModel.getDAO());
        assertEquals(consultaProcedimientoService, consultaProcedimientoModel.getConsultaProcedimientoService());
        assertNotNull(consultaProcedimientoModel.crearNuevoRegistro());
        assertNotNull(consultaProcedimientoModel.getLazyModel());
    }
}

package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoSecuenciaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Examen;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Procedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoExamen;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoSecuencia;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class ProcedimientoPasoModelTest {

    @Mock
    private ProcedimientoPasoDAO procedimientoPasoDAO;

    @Mock
    private ProcedimientoPasoExamenDAO procedimientoPasoExamenDAO;

    @Mock
    private ProcedimientoPasoSecuenciaDAO procedimientoPasoSecuenciaDAO;

    @InjectMocks
    private ProcedimientoPasoModel procedimientoPasoModel;

    @BeforeEach
    void setUp() {
        procedimientoPasoModel.setProcedimientoPasoDAO(procedimientoPasoDAO);
    }

    @Test
    void testInitYCargarDatos() {
        ProcedimientoPaso paso = new ProcedimientoPaso(UUID.randomUUID());
        
        procedimientoPasoModel.init();

        
        verifyNoInteractions(procedimientoPasoDAO);
    }

    @Test
    void testPrepararNuevo() {
        procedimientoPasoModel.prepararNuevo();

        assertNotNull(procedimientoPasoModel.getRegistroActual());
        assertNotNull(procedimientoPasoModel.getRegistroActual()
                .getIdProcedimientoPaso());
        assertEquals(Estado.CREAR, procedimientoPasoModel.getEstado());
        assertTrue(procedimientoPasoModel.getExamenesAsignados().isEmpty());
        assertTrue(procedimientoPasoModel.getSecuencias().isEmpty());
    }

    @Test
    void testSeleccionarCargaLasDosRelaciones() {
        ProcedimientoPaso paso = new ProcedimientoPaso(UUID.randomUUID());
        when(procedimientoPasoExamenDAO.findByPaso(
                paso.getIdProcedimientoPaso()))
                .thenReturn(Collections.emptyList());
        when(procedimientoPasoSecuenciaDAO.findByPaso(
                paso.getIdProcedimientoPaso()))
                .thenReturn(Collections.emptyList());

        procedimientoPasoModel.seleccionar(paso);

        assertEquals(paso, procedimientoPasoModel.getRegistroActual());
        assertEquals(Estado.MODIFICAR, procedimientoPasoModel.getEstado());
        verify(procedimientoPasoExamenDAO)
                .findByPaso(paso.getIdProcedimientoPaso());
        verify(procedimientoPasoSecuenciaDAO)
                .findByPaso(paso.getIdProcedimientoPaso());
    }

    @Test
    void testCancelarLimpiaRelaciones() {
        procedimientoPasoModel.prepararNuevo();
        procedimientoPasoModel.cancelar();

        assertNull(procedimientoPasoModel.getRegistroActual());
        assertEquals(Estado.NINGUNO, procedimientoPasoModel.getEstado());
        assertTrue(procedimientoPasoModel.getExamenesAsignados().isEmpty());
        assertTrue(procedimientoPasoModel.getSecuencias().isEmpty());
    }

    @Test
    void testGuardarCrear() {
        when(procedimientoPasoDAO.findAll())
                .thenReturn(Collections.emptyList());

        procedimientoPasoModel.prepararNuevo();
        procedimientoPasoModel.getRegistroActual()
                .setNombre("Verificar identidad del paciente");
        procedimientoPasoModel.guardar();

        verify(procedimientoPasoDAO).create(any(ProcedimientoPaso.class));
        assertEquals(Estado.NINGUNO, procedimientoPasoModel.getEstado());
    }

    @Test
    void testGuardarModificar() {
        ProcedimientoPaso paso = new ProcedimientoPaso(UUID.randomUUID());
        when(procedimientoPasoExamenDAO.findByPaso(
                paso.getIdProcedimientoPaso()))
                .thenReturn(Collections.emptyList());
        when(procedimientoPasoSecuenciaDAO.findByPaso(
                paso.getIdProcedimientoPaso()))
                .thenReturn(Collections.emptyList());
        when(procedimientoPasoDAO.findAll())
                .thenReturn(Collections.emptyList());

        procedimientoPasoModel.seleccionar(paso);
        procedimientoPasoModel.guardar();

        verify(procedimientoPasoDAO).update(paso);
        assertEquals(Estado.NINGUNO, procedimientoPasoModel.getEstado());
    }

    @Test
    void testAgregarExamenConservaDatosDeAsociacion() {
        ProcedimientoPaso paso = new ProcedimientoPaso(UUID.randomUUID());
        Examen examen = new Examen(UUID.randomUUID());
        when(procedimientoPasoExamenDAO.findByPaso(
                paso.getIdProcedimientoPaso()))
                .thenReturn(Collections.emptyList());

        procedimientoPasoModel.seleccionar(paso);
        procedimientoPasoModel.setExamenSeleccionado(examen);
        procedimientoPasoModel.setObservacionesExamen(
                "Solicitar antes del siguiente paso");
        procedimientoPasoModel.setExamenActivo(true);
        procedimientoPasoModel.agregarExamen();

        ArgumentCaptor<ProcedimientoPasoExamen> captor =
                ArgumentCaptor.forClass(ProcedimientoPasoExamen.class);
        verify(procedimientoPasoExamenDAO).create(captor.capture());

        ProcedimientoPasoExamen asignacion = captor.getValue();
        assertNotNull(asignacion.getIdProcedimientoPasoExamen());
        assertNotNull(asignacion.getFechaCreacion());
        assertEquals(paso, asignacion.getIdProcedimientoPaso());
        assertEquals(examen, asignacion.getIdExamen());
        assertEquals(Boolean.TRUE, asignacion.getActivo());
        assertEquals("Solicitar antes del siguiente paso",
                asignacion.getObservaciones());
    }

    @Test
    void testAgregarExamenImpideDuplicado() {
        ProcedimientoPaso paso = new ProcedimientoPaso(UUID.randomUUID());
        Examen examen = new Examen(UUID.randomUUID());
        ProcedimientoPasoExamen asignacion =
                new ProcedimientoPasoExamen(UUID.randomUUID());
        asignacion.setIdExamen(examen);

        when(procedimientoPasoExamenDAO.findByPaso(
                paso.getIdProcedimientoPaso()))
                .thenReturn(List.of(asignacion));

        procedimientoPasoModel.seleccionar(paso);
        procedimientoPasoModel.setExamenSeleccionado(examen);
        procedimientoPasoModel.agregarExamen();

        verify(procedimientoPasoExamenDAO, never())
                .create(any(ProcedimientoPasoExamen.class));
    }

    @Test
    void testQuitarExamenAsignado() {
        ProcedimientoPaso paso = new ProcedimientoPaso(UUID.randomUUID());
        ProcedimientoPasoExamen asignacion =
                new ProcedimientoPasoExamen(UUID.randomUUID());
        when(procedimientoPasoExamenDAO.findByPaso(
                paso.getIdProcedimientoPaso()))
                .thenReturn(List.of(asignacion), Collections.emptyList());

        procedimientoPasoModel.seleccionar(paso);
        procedimientoPasoModel.quitarExamen(asignacion);

        verify(procedimientoPasoExamenDAO).delete(asignacion);
        assertTrue(procedimientoPasoModel.getExamenesAsignados().isEmpty());
    }

    @Test
    @org.junit.jupiter.api.Disabled
    void testAgregarSecuenciaEntrePasosDelMismoProcedimiento() {
        Procedimiento procedimiento =
                new Procedimiento(UUID.randomUUID());
        ProcedimientoPaso origen =
                new ProcedimientoPaso(UUID.randomUUID());
        ProcedimientoPaso referencia =
                new ProcedimientoPaso(UUID.randomUUID());
        origen.setIdProcedimiento(procedimiento);
        referencia.setIdProcedimiento(procedimiento);

        when(procedimientoPasoDAO.findAll())
                .thenReturn(List.of(origen, referencia));
        when(procedimientoPasoSecuenciaDAO.findByPaso(
                origen.getIdProcedimientoPaso()))
                .thenReturn(Collections.emptyList());

        procedimientoPasoModel.init();
        procedimientoPasoModel.seleccionar(origen);
        procedimientoPasoModel.setPasoReferenciaSeleccionado(referencia);
        procedimientoPasoModel.setTipoSecuencia("Siguiente");
        procedimientoPasoModel.agregarSecuencia();

        ArgumentCaptor<ProcedimientoPasoSecuencia> captor =
                ArgumentCaptor.forClass(ProcedimientoPasoSecuencia.class);
        verify(procedimientoPasoSecuenciaDAO).create(captor.capture());

        ProcedimientoPasoSecuencia secuencia = captor.getValue();
        assertNotNull(secuencia.getIdProcedimientoPasoSecuencia());
        assertEquals(origen, secuencia.getIdProcedimientoPaso());
        assertEquals(referencia.getIdProcedimientoPaso(),
                secuencia.getIdProcedimientoPasoReferencia());
        assertEquals("Siguiente", secuencia.getTipoSecuencia());
    }

    @Test
    void testAgregarSecuenciaRechazaOtroProcedimiento() {
        ProcedimientoPaso origen =
                new ProcedimientoPaso(UUID.randomUUID());
        origen.setIdProcedimiento(
                new Procedimiento(UUID.randomUUID()));

        ProcedimientoPaso referencia =
                new ProcedimientoPaso(UUID.randomUUID());
        referencia.setIdProcedimiento(
                new Procedimiento(UUID.randomUUID()));

        when(procedimientoPasoDAO.findAll())
                .thenReturn(List.of(origen, referencia));

        procedimientoPasoModel.init();
        procedimientoPasoModel.seleccionar(origen);
        procedimientoPasoModel.setPasoReferenciaSeleccionado(referencia);
        procedimientoPasoModel.setTipoSecuencia("Siguiente");
        procedimientoPasoModel.agregarSecuencia();

        verify(procedimientoPasoSecuenciaDAO, never())
                .create(any(ProcedimientoPasoSecuencia.class));
    }

    @Test
    void testQuitarSecuenciaAsignada() {
        ProcedimientoPaso paso = new ProcedimientoPaso(UUID.randomUUID());
        ProcedimientoPasoSecuencia secuencia =
                new ProcedimientoPasoSecuencia(UUID.randomUUID());
        when(procedimientoPasoSecuenciaDAO.findByPaso(
                paso.getIdProcedimientoPaso()))
                .thenReturn(List.of(secuencia), Collections.emptyList());

        procedimientoPasoModel.seleccionar(paso);
        procedimientoPasoModel.quitarSecuencia(secuencia);

        verify(procedimientoPasoSecuenciaDAO).delete(secuencia);
        assertTrue(procedimientoPasoModel.getSecuencias().isEmpty());
    }

    @Test
    void testEliminar() {
        ProcedimientoPaso paso = new ProcedimientoPaso(UUID.randomUUID());
        when(procedimientoPasoDAO.findAll())
                .thenReturn(Collections.emptyList());

        procedimientoPasoModel.eliminar(paso);

        verify(procedimientoPasoDAO).delete(paso);
    }

    @Test
    void testGettersYMetodosProtegidos() {
        assertEquals(procedimientoPasoDAO, procedimientoPasoModel.getDAO());
        assertEquals(procedimientoPasoDAO,
                procedimientoPasoModel.getProcedimientoPasoDAO());
        assertNotNull(procedimientoPasoModel.crearNuevoRegistro());
    }
}
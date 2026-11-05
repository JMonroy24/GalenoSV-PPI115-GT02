package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ValidacionNegocioException;
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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Examen;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenTipoExamen;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class ExamenModelTest {

    @Mock
    private ExamenService examenService;

    @InjectMocks
    private ExamenModel examenModel;

    @BeforeEach
    void setUp() {
        examenModel.setExamenService(examenService);
    }

    @Test
    void testInitYCargarDatos() {
        examenModel.init();
        assertNotNull(examenModel.getLazyModel());
        verifyNoInteractions(examenService);
    }

    @Test
    void testPrepararNuevo() {
        examenModel.prepararNuevo();

        assertNotNull(examenModel.getRegistroActual());
        assertNotNull(examenModel.getRegistroActual().getIdExamen());
        assertEquals(Estado.CREAR, examenModel.getEstado());
        assertTrue(examenModel.getTiposAsignados().isEmpty());
    }

    @Test
    void testSeleccionarCargaTiposAsignados() {
        Examen examen = new Examen(UUID.randomUUID());
        when(examenService.findTiposByExamen(examen.getIdExamen()))
                .thenReturn(Collections.emptyList());

        examenModel.seleccionar(examen);

        assertEquals(examen, examenModel.getRegistroActual());
        assertEquals(Estado.MODIFICAR, examenModel.getEstado());
        assertTrue(examenModel.getTiposAsignados().isEmpty());
        verify(examenService).findTiposByExamen(examen.getIdExamen());
    }

    @Test
    void testCancelar() {
        examenModel.prepararNuevo();

        examenModel.cancelar();

        assertNull(examenModel.getRegistroActual());
        assertEquals(Estado.NINGUNO, examenModel.getEstado());
        assertTrue(examenModel.getTiposAsignados().isEmpty());
    }

    @Test
    void testGuardarCrear() {
        examenModel.prepararNuevo();
        examenModel.getRegistroActual().setNombre("Registro válido");
        examenModel.guardar();

        verify(examenService).create(any(Examen.class));
        assertEquals(Estado.NINGUNO, examenModel.getEstado());
        assertNull(examenModel.getRegistroActual());
    }

    @Test
    void testGuardarCrearConTipo() {
        examenModel.prepararNuevo();
        examenModel.getRegistroActual().setNombre("Registro válido");
        TipoExamen tipo = new TipoExamen(UUID.randomUUID());
        tipo.setActivo(true);
        examenModel.setTipoSeleccionado(tipo);
        examenModel.setObservacionesTipo("Obs");
        examenModel.guardar();

        verify(examenService).crearExamenConTipo(any(Examen.class), any(ExamenTipoExamen.class));
        assertEquals(Estado.NINGUNO, examenModel.getEstado());
        assertNull(examenModel.getRegistroActual());
    }

    @Test
    void testGuardarModificar() {
        Examen examen = new Examen(UUID.randomUUID());
        when(examenService.findTiposByExamen(examen.getIdExamen()))
                .thenReturn(Collections.emptyList());

        examenModel.seleccionar(examen);
        examenModel.getRegistroActual().setNombre("Registro válido");
        examenModel.guardar();

        verify(examenService).update(examen);
        assertEquals(Estado.NINGUNO, examenModel.getEstado());
    }

    @Test
    void testAgregarTipoGuardaAsociacion() {
        Examen examen = new Examen(UUID.randomUUID());
        TipoExamen tipo = new TipoExamen(UUID.randomUUID());
        tipo.setActivo(true);
        when(examenService.findTiposByExamen(examen.getIdExamen()))
                .thenReturn(Collections.emptyList());

        examenModel.seleccionar(examen);
        examenModel.setTipoSeleccionado(tipo);
        examenModel.setObservacionesTipo("Requiere preparación");
        examenModel.agregarTipo();

        ArgumentCaptor<ExamenTipoExamen> captor =
                ArgumentCaptor.forClass(ExamenTipoExamen.class);
        verify(examenService).agregarTipo(captor.capture());

        ExamenTipoExamen asignacion = captor.getValue();
        assertNotNull(asignacion.getIdExamenTipoExamen());
        assertNotNull(asignacion.getFechaCreacion());
        assertEquals(examen, asignacion.getIdExamen());
        assertEquals(tipo, asignacion.getIdTipoExamen());
        assertEquals("Requiere preparación", asignacion.getObservaciones());
        assertNull(examenModel.getTipoSeleccionado());
        assertNull(examenModel.getObservacionesTipo());
    }

    @Test
    void testAgregarTipoImpideDuplicado() {
        Examen examen = new Examen(UUID.randomUUID());
        TipoExamen tipo = new TipoExamen(UUID.randomUUID());
        tipo.setActivo(true);
        ExamenTipoExamen asignacion =
                new ExamenTipoExamen(UUID.randomUUID());
        asignacion.setIdTipoExamen(tipo);

        when(examenService.findTiposByExamen(examen.getIdExamen()))
                .thenReturn(List.of(asignacion));

        examenModel.seleccionar(examen);
        examenModel.setTipoSeleccionado(tipo);
        doThrow(new ValidacionNegocioException("Duplicado"))
                .when(examenService).agregarTipo(any());
        examenModel.agregarTipo();

        assertEquals(tipo, examenModel.getTipoSeleccionado());
        assertEquals(Estado.MODIFICAR, examenModel.getEstado());
    }

    @Test
    void testQuitarTipoEliminaAsociacionDelExamen() {
        Examen examen = new Examen(UUID.randomUUID());
        ExamenTipoExamen asignacion =
                new ExamenTipoExamen(UUID.randomUUID());

        when(examenService.findTiposByExamen(examen.getIdExamen()))
                .thenReturn(List.of(asignacion), Collections.emptyList());

        examenModel.seleccionar(examen);
        examenModel.quitarTipo(asignacion);

        verify(examenService).quitarTipo(asignacion);
        assertTrue(examenModel.getTiposAsignados().isEmpty());
    }

    @Test
    void testQuitarTipoRechazaAsociacionAjena() {
        Examen examen = new Examen(UUID.randomUUID());
        ExamenTipoExamen asignacionAjena =
                new ExamenTipoExamen(UUID.randomUUID());

        when(examenService.findTiposByExamen(examen.getIdExamen()))
                .thenReturn(Collections.emptyList());

        examenModel.seleccionar(examen);
        examenModel.quitarTipo(asignacionAjena);

        verify(examenService, never())
                .quitarTipo(any(ExamenTipoExamen.class));
    }

    @Test
    void testEliminar() {
        Examen examen = new Examen(UUID.randomUUID());

        examenModel.eliminar(examen);

        verify(examenService).delete(examen);
    }

    @Test
    void testGetActivos() {
        List<Examen> lista = List.of(new Examen(UUID.randomUUID()));
        when(examenService.getActivos()).thenReturn(lista);

        assertSame(lista, examenModel.getExamenesActivos());
        verify(examenService).getActivos();
    }

    @Test
    void testGettersYMetodosProtegidos() {
        assertEquals(examenService, examenModel.getDAO());
        assertEquals(examenService, examenModel.getExamenService());
        assertNotNull(examenModel.crearNuevoRegistro());
    }
}

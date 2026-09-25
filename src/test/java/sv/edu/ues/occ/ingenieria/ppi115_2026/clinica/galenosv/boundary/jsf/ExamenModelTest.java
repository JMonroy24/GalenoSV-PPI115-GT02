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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenTipoExamenDAO;
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
    private ExamenDAO examenDAO;

    @Mock
    private ExamenTipoExamenDAO examenTipoExamenDAO;

    @InjectMocks
    private ExamenModel examenModel;

    @BeforeEach
    void setUp() {
        examenModel.setExamenDAO(examenDAO);
    }

    @Test
    void testInitYCargarDatos() {
        Examen examen = new Examen(UUID.randomUUID());
        
        examenModel.init();

        
        verifyNoInteractions(examenDAO);
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
        when(examenTipoExamenDAO.findByExamen(examen.getIdExamen()))
                .thenReturn(Collections.emptyList());

        examenModel.seleccionar(examen);

        assertEquals(examen, examenModel.getRegistroActual());
        assertEquals(Estado.MODIFICAR, examenModel.getEstado());
        assertTrue(examenModel.getTiposAsignados().isEmpty());
        verify(examenTipoExamenDAO).findByExamen(examen.getIdExamen());
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
        examenModel.getRegistroActual().setNombre("Hemograma");
        examenModel.guardar();

        verify(examenDAO).create(any(Examen.class));
        assertEquals(Estado.NINGUNO, examenModel.getEstado());
        assertNull(examenModel.getRegistroActual());
    }

    @Test
    void testGuardarModificar() {
        Examen examen = new Examen(UUID.randomUUID());
        when(examenTipoExamenDAO.findByExamen(examen.getIdExamen()))
                .thenReturn(Collections.emptyList());
        
        examenModel.seleccionar(examen);
        examenModel.guardar();

        verify(examenDAO).update(examen);
        assertEquals(Estado.NINGUNO, examenModel.getEstado());
    }

    @Test
    void testAgregarTipoGuardaAsociacion() {
        Examen examen = new Examen(UUID.randomUUID());
        TipoExamen tipo = new TipoExamen(UUID.randomUUID());
        when(examenTipoExamenDAO.findByExamen(examen.getIdExamen()))
                .thenReturn(Collections.emptyList());

        examenModel.seleccionar(examen);
        examenModel.setTipoSeleccionado(tipo);
        examenModel.setObservacionesTipo("Requiere preparación");
        examenModel.agregarTipo();

        ArgumentCaptor<ExamenTipoExamen> captor =
                ArgumentCaptor.forClass(ExamenTipoExamen.class);
        verify(examenTipoExamenDAO).create(captor.capture());

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
        ExamenTipoExamen asignacion =
                new ExamenTipoExamen(UUID.randomUUID());
        asignacion.setIdTipoExamen(tipo);

        when(examenTipoExamenDAO.findByExamen(examen.getIdExamen()))
                .thenReturn(List.of(asignacion));

        examenModel.seleccionar(examen);
        examenModel.setTipoSeleccionado(tipo);
        examenModel.agregarTipo();

        verify(examenTipoExamenDAO, never())
                .create(any(ExamenTipoExamen.class));
    }

    @Test
    void testQuitarTipoEliminaAsociacionDelExamen() {
        Examen examen = new Examen(UUID.randomUUID());
        ExamenTipoExamen asignacion =
                new ExamenTipoExamen(UUID.randomUUID());

        when(examenTipoExamenDAO.findByExamen(examen.getIdExamen()))
                .thenReturn(List.of(asignacion), Collections.emptyList());

        examenModel.seleccionar(examen);
        examenModel.quitarTipo(asignacion);

        verify(examenTipoExamenDAO).delete(asignacion);
        assertTrue(examenModel.getTiposAsignados().isEmpty());
    }

    @Test
    void testQuitarTipoRechazaAsociacionAjena() {
        Examen examen = new Examen(UUID.randomUUID());
        ExamenTipoExamen asignacionAjena =
                new ExamenTipoExamen(UUID.randomUUID());

        when(examenTipoExamenDAO.findByExamen(examen.getIdExamen()))
                .thenReturn(Collections.emptyList());

        examenModel.seleccionar(examen);
        examenModel.quitarTipo(asignacionAjena);

        verify(examenTipoExamenDAO, never())
                .delete(any(ExamenTipoExamen.class));
    }

    @Test
    void testEliminar() {
        Examen examen = new Examen(UUID.randomUUID());
        
        examenModel.eliminar(examen);

        verify(examenDAO).delete(examen);
    }

    @Test
    void testGettersYMetodosProtegidos() {
        assertEquals(examenDAO, examenModel.getDAO());
        assertEquals(examenDAO, examenModel.getExamenDAO());
        assertNotNull(examenModel.crearNuevoRegistro());
    }
}
package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Examen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenModelTest {

    @Mock
    private ExamenDAO examenDAO;

    @InjectMocks
    private ExamenModel examenModel;

    @BeforeEach
    void setUp() {
        examenModel.setExamenDAO(examenDAO);
    }

    @Test
    void testInitYCargarDatos() {
        Examen examen = new Examen(UUID.randomUUID());
        when(examenDAO.findAll()).thenReturn(List.of(examen));

        examenModel.init();

        assertNotNull(examenModel.getRegistros());
        assertEquals(1, examenModel.getRegistros().size());
        verify(examenDAO).findAll();
    }

    @Test
    void testPrepararNuevo() {
        examenModel.prepararNuevo();

        assertNotNull(examenModel.getRegistroActual());
        assertEquals(Estado.CREAR, examenModel.getEstado());
        assertTrue(examenModel.isEstadoCrear());
    }

    @Test
    void testSeleccionar() {
        Examen examen = new Examen(UUID.randomUUID());

        examenModel.seleccionar(examen);

        assertEquals(examen, examenModel.getRegistroActual());
        assertEquals(Estado.MODIFICAR, examenModel.getEstado());
        assertTrue(examenModel.isEstadoModificar());
    }

    @Test
    void testCancelar() {
        examenModel.prepararNuevo();

        examenModel.cancelar();

        assertNull(examenModel.getRegistroActual());
        assertEquals(Estado.NINGUNO, examenModel.getEstado());
        assertTrue(examenModel.isEstadoNinguno());
    }

    @Test
    void testGuardarCrear() {
        when(examenDAO.findAll()).thenReturn(Collections.emptyList());

        examenModel.prepararNuevo();
        examenModel.getRegistroActual().setNombre("Hemograma");

        examenModel.guardar();

        verify(examenDAO).create(any(Examen.class));
        assertEquals(Estado.NINGUNO, examenModel.getEstado());
        assertNull(examenModel.getRegistroActual());
    }

    @Test
    void testGuardarModificar() {
        when(examenDAO.findAll()).thenReturn(Collections.emptyList());
        Examen examen = new Examen(UUID.randomUUID());

        examenModel.seleccionar(examen);
        examenModel.guardar();

        verify(examenDAO).update(examen);
        assertEquals(Estado.NINGUNO, examenModel.getEstado());
    }

    @Test
    void testEliminar() {
        when(examenDAO.findAll()).thenReturn(Collections.emptyList());
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
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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenTipoExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenTipoExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class ExamenTipoExamenModelTest {

    @Mock
    private ExamenTipoExamenDAO examenTipoexamenDAO;

    @InjectMocks
    private ExamenTipoExamenModel examenTipoexamenModel;

    @BeforeEach
    void setUp() {
        examenTipoexamenModel.setExamenTipoExamenDAO(examenTipoexamenDAO);
    }

    @Test
    void testInitYCargarDatos() {
        ExamenTipoExamen examenTipoexamen = new ExamenTipoExamen(UUID.randomUUID());
        
        examenTipoexamenModel.init();

        
        assertEquals(1, examenTipoexamenModel.getLazyModel() != null ? 1 : 0);
        verifyNoInteractions(examenTipoexamenDAO);
    }

    @Test
    void testPrepararNuevo() {
        examenTipoexamenModel.prepararNuevo();

        assertNotNull(examenTipoexamenModel.getRegistroActual());
        assertEquals(Estado.CREAR, examenTipoexamenModel.getEstado());
        assertTrue(examenTipoexamenModel.isEstadoCrear());
    }

    @Test
    void testSeleccionar() {
        ExamenTipoExamen examenTipoexamen = new ExamenTipoExamen(UUID.randomUUID());

        examenTipoexamenModel.seleccionar(examenTipoexamen);

        assertEquals(examenTipoexamen, examenTipoexamenModel.getRegistroActual());
        assertEquals(Estado.MODIFICAR, examenTipoexamenModel.getEstado());
        assertTrue(examenTipoexamenModel.isEstadoModificar());
    }

    @Test
    void testCancelar() {
        examenTipoexamenModel.prepararNuevo();

        examenTipoexamenModel.cancelar();

        assertNull(examenTipoexamenModel.getRegistroActual());
        assertEquals(Estado.NINGUNO, examenTipoexamenModel.getEstado());
        assertTrue(examenTipoexamenModel.isEstadoNinguno());
    }

    @Test
    void testGuardarCrear() {
        
        examenTipoexamenModel.prepararNuevo();
        examenTipoexamenModel.getRegistroActual().setObservaciones("Asociación entre examen y tipo de examen");

        examenTipoexamenModel.guardar();

        verify(examenTipoexamenDAO).create(any(ExamenTipoExamen.class));
        assertEquals(Estado.NINGUNO, examenTipoexamenModel.getEstado());
        assertNull(examenTipoexamenModel.getRegistroActual());
    }

    @Test
    void testGuardarModificar() {
                ExamenTipoExamen examenTipoexamen = new ExamenTipoExamen(UUID.randomUUID());

        examenTipoexamenModel.seleccionar(examenTipoexamen);
        examenTipoexamenModel.guardar();

        verify(examenTipoexamenDAO).update(examenTipoexamen);
        assertEquals(Estado.NINGUNO, examenTipoexamenModel.getEstado());
    }

    @Test
    void testEliminar() {
                ExamenTipoExamen examenTipoexamen = new ExamenTipoExamen(UUID.randomUUID());

        examenTipoexamenModel.eliminar(examenTipoexamen);

        verify(examenTipoexamenDAO).delete(examenTipoexamen);
    }

    @Test
    void testGettersYMetodosProtegidos() {
        assertEquals(examenTipoexamenDAO, examenTipoexamenModel.getDAO());
        assertEquals(examenTipoexamenDAO, examenTipoexamenModel.getExamenTipoExamenDAO());
        assertNotNull(examenTipoexamenModel.crearNuevoRegistro());
    }
}
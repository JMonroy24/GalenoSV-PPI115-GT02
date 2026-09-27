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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Procedimiento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class ProcedimientoModelTest {

    @Mock
    private ProcedimientoDAO procedimientoDAO;

    @InjectMocks
    private ProcedimientoModel procedimientoModel;

    @BeforeEach
    void setUp() {
        procedimientoModel.setProcedimientoDAO(procedimientoDAO);
    }

    @Test
    void testInitYCargarDatos() {
        Procedimiento procedimiento = new Procedimiento(UUID.randomUUID());
        
        procedimientoModel.init();

        
        
        verifyNoInteractions(procedimientoDAO);
    }

    @Test
    void testPrepararNuevo() {
        procedimientoModel.prepararNuevo();

        assertNotNull(procedimientoModel.getRegistroActual());
        assertEquals(Estado.CREAR, procedimientoModel.getEstado());
        assertTrue(procedimientoModel.isEstadoCrear());
    }

    @Test
    void testSeleccionar() {
        Procedimiento procedimiento = new Procedimiento(UUID.randomUUID());

        procedimientoModel.seleccionar(procedimiento);

        assertEquals(procedimiento, procedimientoModel.getRegistroActual());
        assertEquals(Estado.MODIFICAR, procedimientoModel.getEstado());
        assertTrue(procedimientoModel.isEstadoModificar());
    }

    @Test
    void testCancelar() {
        procedimientoModel.prepararNuevo();

        procedimientoModel.cancelar();

        assertNull(procedimientoModel.getRegistroActual());
        assertEquals(Estado.NINGUNO, procedimientoModel.getEstado());
        assertTrue(procedimientoModel.isEstadoNinguno());
    }

    @Test
    void testGuardarCrear() {
        
        procedimientoModel.prepararNuevo();
        procedimientoModel.getRegistroActual().setNombre("Toma de muestra sanguínea");

        procedimientoModel.guardar();

        verify(procedimientoDAO).create(any(Procedimiento.class));
        assertEquals(Estado.NINGUNO, procedimientoModel.getEstado());
        assertNull(procedimientoModel.getRegistroActual());
    }

    @Test
    void testGuardarModificar() {
                Procedimiento procedimiento = new Procedimiento(UUID.randomUUID());

        procedimientoModel.seleccionar(procedimiento);
        procedimientoModel.guardar();

        verify(procedimientoDAO).update(procedimiento);
        assertEquals(Estado.NINGUNO, procedimientoModel.getEstado());
    }

    @Test
    void testEliminar() {
                Procedimiento procedimiento = new Procedimiento(UUID.randomUUID());

        procedimientoModel.eliminar(procedimiento);

        verify(procedimientoDAO).delete(procedimiento);
    }

    @Test
    void testGettersYMetodosProtegidos() {
        assertEquals(procedimientoDAO, procedimientoModel.getDAO());
        assertEquals(procedimientoDAO, procedimientoModel.getProcedimientoDAO());
        assertNotNull(procedimientoModel.crearNuevoRegistro());
    }
}
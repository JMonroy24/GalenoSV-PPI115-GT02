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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPaso;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcedimientoPasoModelTest {

    @Mock
    private ProcedimientoPasoDAO procedimientoPasoDAO;

    @InjectMocks
    private ProcedimientoPasoModel procedimientoPasoModel;

    @BeforeEach
    void setUp() {
        procedimientoPasoModel.setProcedimientoPasoDAO(procedimientoPasoDAO);
    }

    @Test
    void testInitYCargarDatos() {
        ProcedimientoPaso procedimientoPaso = new ProcedimientoPaso(UUID.randomUUID());
        when(procedimientoPasoDAO.findAll()).thenReturn(List.of(procedimientoPaso));

        procedimientoPasoModel.init();

        assertNotNull(procedimientoPasoModel.getRegistros());
        assertEquals(1, procedimientoPasoModel.getRegistros().size());
        verify(procedimientoPasoDAO).findAll();
    }

    @Test
    void testPrepararNuevo() {
        procedimientoPasoModel.prepararNuevo();

        assertNotNull(procedimientoPasoModel.getRegistroActual());
        assertEquals(Estado.CREAR, procedimientoPasoModel.getEstado());
        assertTrue(procedimientoPasoModel.isEstadoCrear());
    }

    @Test
    void testSeleccionar() {
        ProcedimientoPaso procedimientoPaso = new ProcedimientoPaso(UUID.randomUUID());

        procedimientoPasoModel.seleccionar(procedimientoPaso);

        assertEquals(procedimientoPaso, procedimientoPasoModel.getRegistroActual());
        assertEquals(Estado.MODIFICAR, procedimientoPasoModel.getEstado());
        assertTrue(procedimientoPasoModel.isEstadoModificar());
    }

    @Test
    void testCancelar() {
        procedimientoPasoModel.prepararNuevo();

        procedimientoPasoModel.cancelar();

        assertNull(procedimientoPasoModel.getRegistroActual());
        assertEquals(Estado.NINGUNO, procedimientoPasoModel.getEstado());
        assertTrue(procedimientoPasoModel.isEstadoNinguno());
    }

    @Test
    void testGuardarCrear() {
        when(procedimientoPasoDAO.findAll()).thenReturn(Collections.emptyList());

        procedimientoPasoModel.prepararNuevo();
        procedimientoPasoModel.getRegistroActual().setNombre("Verificar identidad del paciente");

        procedimientoPasoModel.guardar();

        verify(procedimientoPasoDAO).create(any(ProcedimientoPaso.class));
        assertEquals(Estado.NINGUNO, procedimientoPasoModel.getEstado());
        assertNull(procedimientoPasoModel.getRegistroActual());
    }

    @Test
    void testGuardarModificar() {
        when(procedimientoPasoDAO.findAll()).thenReturn(Collections.emptyList());
        ProcedimientoPaso procedimientoPaso = new ProcedimientoPaso(UUID.randomUUID());

        procedimientoPasoModel.seleccionar(procedimientoPaso);
        procedimientoPasoModel.guardar();

        verify(procedimientoPasoDAO).update(procedimientoPaso);
        assertEquals(Estado.NINGUNO, procedimientoPasoModel.getEstado());
    }

    @Test
    void testEliminar() {
        when(procedimientoPasoDAO.findAll()).thenReturn(Collections.emptyList());
        ProcedimientoPaso procedimientoPaso = new ProcedimientoPaso(UUID.randomUUID());

        procedimientoPasoModel.eliminar(procedimientoPaso);

        verify(procedimientoPasoDAO).delete(procedimientoPaso);
    }

    @Test
    void testGettersYMetodosProtegidos() {
        assertEquals(procedimientoPasoDAO, procedimientoPasoModel.getDAO());
        assertEquals(procedimientoPasoDAO, procedimientoPasoModel.getProcedimientoPasoDAO());
        assertNotNull(procedimientoPasoModel.crearNuevoRegistro());
    }
}
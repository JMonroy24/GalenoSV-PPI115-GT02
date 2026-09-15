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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenResultadoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenResultado;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExamenResultadoModelTest {

    @Mock
    private ExamenResultadoDAO examenResultadoDAO;

    @InjectMocks
    private ExamenResultadoModel examenResultadoModel;

    @BeforeEach
    public void setUp() {
        examenResultadoModel.setExamenResultadoDAO(examenResultadoDAO);
    }

    @Test
    public void testInitYCargarDatos() {
        ExamenResultado er = new ExamenResultado(UUID.randomUUID());
        when(examenResultadoDAO.findAll()).thenReturn(List.of(er));

        examenResultadoModel.init();

        assertNotNull(examenResultadoModel.getRegistros());
        assertEquals(1, examenResultadoModel.getRegistros().size());
        verify(examenResultadoDAO).findAll();
    }

    @Test
    public void testPrepararNuevo() {
        examenResultadoModel.prepararNuevo();

        assertNotNull(examenResultadoModel.getRegistroActual());
        assertEquals(Estado.CREAR, examenResultadoModel.getEstado());
        assertTrue(examenResultadoModel.isEstadoCrear());
    }

    @Test
    public void testSeleccionar() {
        ExamenResultado er = new ExamenResultado(UUID.randomUUID());
        examenResultadoModel.seleccionar(er);

        assertEquals(er, examenResultadoModel.getRegistroActual());
        assertEquals(Estado.MODIFICAR, examenResultadoModel.getEstado());
        assertTrue(examenResultadoModel.isEstadoModificar());
    }

    @Test
    public void testCancelar() {
        examenResultadoModel.prepararNuevo();
        examenResultadoModel.cancelar();

        assertNull(examenResultadoModel.getRegistroActual());
        assertEquals(Estado.NINGUNO, examenResultadoModel.getEstado());
        assertTrue(examenResultadoModel.isEstadoNinguno());
    }

    @Test
    public void testGuardarCrear() {
        when(examenResultadoDAO.findAll()).thenReturn(Collections.emptyList());

        examenResultadoModel.prepararNuevo();
        examenResultadoModel.getRegistroActual().setResultado("Normal");

        examenResultadoModel.guardar();

        verify(examenResultadoDAO).create(any(ExamenResultado.class));
        assertEquals(Estado.NINGUNO, examenResultadoModel.getEstado());
        assertNull(examenResultadoModel.getRegistroActual());
    }

    @Test
    public void testGuardarModificar() {
        when(examenResultadoDAO.findAll()).thenReturn(Collections.emptyList());

        ExamenResultado er = new ExamenResultado(UUID.randomUUID());
        examenResultadoModel.seleccionar(er);

        examenResultadoModel.guardar();

        verify(examenResultadoDAO).update(er);
        assertEquals(Estado.NINGUNO, examenResultadoModel.getEstado());
    }

    @Test
    public void testEliminar() {
        when(examenResultadoDAO.findAll()).thenReturn(Collections.emptyList());

        ExamenResultado er = new ExamenResultado(UUID.randomUUID());
        examenResultadoModel.eliminar(er);

        verify(examenResultadoDAO).delete(er);
    }

    @Test
    public void testGettersAndSetters() {
        assertEquals(examenResultadoDAO, examenResultadoModel.getDAO());
        assertEquals(examenResultadoDAO, examenResultadoModel.getExamenResultadoDAO());
        assertNotNull(examenResultadoModel.crearNuevoRegistro());
    }
}

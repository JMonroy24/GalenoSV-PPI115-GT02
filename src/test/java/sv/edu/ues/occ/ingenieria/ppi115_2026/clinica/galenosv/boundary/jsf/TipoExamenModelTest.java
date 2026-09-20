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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.TipoExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TipoExamenModelTest {

    @Mock
    private TipoExamenDAO tipoExamenDAO;

    @InjectMocks
    private TipoExamenModel tipoExamenModel;

    @BeforeEach
    public void setUp() {
        tipoExamenModel.setTipoExamenDAO(tipoExamenDAO);
    }

    @Test
    public void testInitYCargarDatos() {
        TipoExamen te = new TipoExamen(UUID.randomUUID());
        when(tipoExamenDAO.findAll()).thenReturn(List.of(te));

        tipoExamenModel.init();

        assertNotNull(tipoExamenModel.getRegistros());
        assertEquals(1, tipoExamenModel.getRegistros().size());
        verify(tipoExamenDAO).findAll();
    }

    @Test
    public void testPrepararNuevo() {
        tipoExamenModel.prepararNuevo();

        assertNotNull(tipoExamenModel.getRegistroActual());
        assertEquals(Estado.CREAR, tipoExamenModel.getEstado());
        assertTrue(tipoExamenModel.isEstadoCrear());
    }

    @Test
    public void testSeleccionar() {
        TipoExamen te = new TipoExamen(UUID.randomUUID());
        tipoExamenModel.seleccionar(te);

        assertEquals(te, tipoExamenModel.getRegistroActual());
        assertEquals(Estado.MODIFICAR, tipoExamenModel.getEstado());
        assertTrue(tipoExamenModel.isEstadoModificar());
    }

    @Test
    public void testCancelar() {
        tipoExamenModel.prepararNuevo();
        tipoExamenModel.cancelar();

        assertNull(tipoExamenModel.getRegistroActual());
        assertEquals(Estado.NINGUNO, tipoExamenModel.getEstado());
        assertTrue(tipoExamenModel.isEstadoNinguno());
    }

    @Test
    public void testGuardarCrear() {
        when(tipoExamenDAO.findAll()).thenReturn(Collections.emptyList());

        tipoExamenModel.prepararNuevo();
        tipoExamenModel.getRegistroActual().setNombre("Hemograma");

        tipoExamenModel.guardar();

        verify(tipoExamenDAO).create(any(TipoExamen.class));
        assertEquals(Estado.NINGUNO, tipoExamenModel.getEstado());
        assertNull(tipoExamenModel.getRegistroActual());
    }

    @Test
    public void testGuardarModificar() {
        when(tipoExamenDAO.findAll()).thenReturn(Collections.emptyList());

        TipoExamen te = new TipoExamen(UUID.randomUUID());
        tipoExamenModel.seleccionar(te);

        tipoExamenModel.guardar();

        verify(tipoExamenDAO).update(te);
        assertEquals(Estado.NINGUNO, tipoExamenModel.getEstado());
    }

    @Test
    public void testEliminar() {
        when(tipoExamenDAO.findAll()).thenReturn(Collections.emptyList());

        TipoExamen te = new TipoExamen(UUID.randomUUID());
        tipoExamenModel.eliminar(te);

        verify(tipoExamenDAO).delete(te);
    }

    @Test
    public void testGettersAndSetters() {
        assertEquals(tipoExamenDAO, tipoExamenModel.getDAO());
        assertEquals(tipoExamenDAO, tipoExamenModel.getTipoExamenDAO());
        assertNotNull(tipoExamenModel.crearNuevoRegistro());
    }
}

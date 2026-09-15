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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.OrdenExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.OrdenExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrdenExamenModelTest {

    @Mock
    private OrdenExamenDAO ordenExamenDAO;

    @InjectMocks
    private OrdenExamenModel ordenExamenModel;

    @BeforeEach
    public void setUp() {
        ordenExamenModel.setOrdenExamenDAO(ordenExamenDAO);
    }

    @Test
    public void testInitYCargarDatos() {
        OrdenExamen oe = new OrdenExamen(UUID.randomUUID());
        when(ordenExamenDAO.findAll()).thenReturn(List.of(oe));

        ordenExamenModel.init();

        assertNotNull(ordenExamenModel.getRegistros());
        assertEquals(1, ordenExamenModel.getRegistros().size());
        verify(ordenExamenDAO).findAll();
    }

    @Test
    public void testPrepararNuevo() {
        ordenExamenModel.prepararNuevo();

        assertNotNull(ordenExamenModel.getRegistroActual());
        assertEquals(Estado.CREAR, ordenExamenModel.getEstado());
        assertTrue(ordenExamenModel.isEstadoCrear());
    }

    @Test
    public void testSeleccionar() {
        OrdenExamen oe = new OrdenExamen(UUID.randomUUID());
        ordenExamenModel.seleccionar(oe);

        assertEquals(oe, ordenExamenModel.getRegistroActual());
        assertEquals(Estado.MODIFICAR, ordenExamenModel.getEstado());
        assertTrue(ordenExamenModel.isEstadoModificar());
    }

    @Test
    public void testCancelar() {
        ordenExamenModel.prepararNuevo();
        ordenExamenModel.cancelar();

        assertNull(ordenExamenModel.getRegistroActual());
        assertEquals(Estado.NINGUNO, ordenExamenModel.getEstado());
        assertTrue(ordenExamenModel.isEstadoNinguno());
    }

    @Test
    public void testGuardarCrear() {
        when(ordenExamenDAO.findAll()).thenReturn(Collections.emptyList());

        ordenExamenModel.prepararNuevo();
        ordenExamenModel.getRegistroActual().setIndicaciones("Realizar examen de sangre");

        ordenExamenModel.guardar();

        verify(ordenExamenDAO).create(any(OrdenExamen.class));
        assertEquals(Estado.NINGUNO, ordenExamenModel.getEstado());
        assertNull(ordenExamenModel.getRegistroActual());
    }

    @Test
    public void testGuardarModificar() {
        when(ordenExamenDAO.findAll()).thenReturn(Collections.emptyList());

        OrdenExamen oe = new OrdenExamen(UUID.randomUUID());
        ordenExamenModel.seleccionar(oe);

        ordenExamenModel.guardar();

        verify(ordenExamenDAO).update(oe);
        assertEquals(Estado.NINGUNO, ordenExamenModel.getEstado());
    }

    @Test
    public void testEliminar() {
        when(ordenExamenDAO.findAll()).thenReturn(Collections.emptyList());

        OrdenExamen oe = new OrdenExamen(UUID.randomUUID());
        ordenExamenModel.eliminar(oe);

        verify(ordenExamenDAO).delete(oe);
    }

    @Test
    public void testGettersAndSetters() {
        assertEquals(ordenExamenDAO, ordenExamenModel.getDAO());
        assertEquals(ordenExamenDAO, ordenExamenModel.getOrdenExamenDAO());
        assertNotNull(ordenExamenModel.crearNuevoRegistro());
    }
}

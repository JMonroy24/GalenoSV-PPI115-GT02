package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenResultadoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.OrdenExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.OrdenExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
public class OrdenExamenModelTest {

    @Mock
    private OrdenExamenService ordenExamenService;

    @Mock
    private ExamenResultadoService examenResultadoService;

    @InjectMocks
    private OrdenExamenModel ordenExamenModel;

    @BeforeEach
    public void setUp() {
        ordenExamenModel.setOrdenExamenService(ordenExamenService);
    }

    @Test
    public void testInitYCargarDatos() {
        ordenExamenModel.init();
        assertNotNull(ordenExamenModel.getLazyModel());
        assertNull(ordenExamenModel.getRegistros());

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
        ordenExamenModel.prepararNuevo();
        ordenExamenModel.getRegistroActual().setIndicaciones("Realizar examen de sangre");

        ordenExamenModel.getRegistroActual().setIndicaciones("Tomar muestra");
        ordenExamenModel.getRegistroActual().setIdConsultaProcedimientoPaso(new sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimientoPaso(UUID.randomUUID()));
        ordenExamenModel.guardar();

        verify(ordenExamenService).create(any(OrdenExamen.class));
        assertEquals(Estado.NINGUNO, ordenExamenModel.getEstado());
        assertNull(ordenExamenModel.getRegistroActual());
    }

    @Test
    public void testGuardarModificar() {
        OrdenExamen oe = new OrdenExamen(UUID.randomUUID());
        ordenExamenModel.seleccionar(oe);

        ordenExamenModel.getRegistroActual().setIndicaciones("Tomar muestra");
        ordenExamenModel.getRegistroActual().setIdConsultaProcedimientoPaso(new sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimientoPaso(UUID.randomUUID()));
        ordenExamenModel.guardar();

        verify(ordenExamenService).update(oe);
        assertEquals(Estado.NINGUNO, ordenExamenModel.getEstado());
    }

    @Test
    public void testEliminar() {
        OrdenExamen oe = new OrdenExamen(UUID.randomUUID());
        ordenExamenModel.eliminar(oe);

        verify(ordenExamenService).delete(oe);
    }

    @Test
    public void testGettersAndSetters() {
        ordenExamenModel.init();
        assertEquals(ordenExamenService, ordenExamenModel.getDAO());
        assertEquals(ordenExamenService, ordenExamenModel.getOrdenExamenService());
        assertNotNull(ordenExamenModel.crearNuevoRegistro());
        assertNotNull(ordenExamenModel.getLazyModel());
    }
}

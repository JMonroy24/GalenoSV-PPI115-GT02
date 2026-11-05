package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.TipoExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
public class TipoExamenModelTest {

    @Mock
    private TipoExamenService tipoExamenService;

    @InjectMocks
    private TipoExamenModel tipoExamenModel;

    @BeforeEach
    public void setUp() {
        tipoExamenModel.setTipoExamenService(tipoExamenService);
    }

    @Test
    public void testInitYCargarDatos() {
        tipoExamenModel.init();
        assertNotNull(tipoExamenModel.getLazyModel());
        assertNull(tipoExamenModel.getRegistros());
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
        tipoExamenModel.prepararNuevo();
        tipoExamenModel.getRegistroActual().setNombre("Registro válido");
        tipoExamenModel.guardar();

        verify(tipoExamenService).create(any(TipoExamen.class));
        assertEquals(Estado.NINGUNO, tipoExamenModel.getEstado());
        assertNull(tipoExamenModel.getRegistroActual());
    }

    @Test
    public void testGuardarModificar() {
        TipoExamen te = new TipoExamen(UUID.randomUUID());
        tipoExamenModel.seleccionar(te);

        tipoExamenModel.getRegistroActual().setNombre("Registro válido");
        tipoExamenModel.guardar();

        verify(tipoExamenService).update(te);
        assertEquals(Estado.NINGUNO, tipoExamenModel.getEstado());
    }

    @Test
    public void testEliminar() {
        TipoExamen te = new TipoExamen(UUID.randomUUID());
        tipoExamenModel.eliminar(te);

        verify(tipoExamenService).delete(te);
    }

    @Test
    public void testGetTiposActivos() {
        List<TipoExamen> lista = List.of(new TipoExamen(UUID.randomUUID()));
        when(tipoExamenService.getTiposActivos()).thenReturn(lista);

        assertSame(lista, tipoExamenModel.getTiposActivos());
        verify(tipoExamenService).getTiposActivos();
    }

    @Test
    public void testGettersAndSetters() {
        assertEquals(tipoExamenService, tipoExamenModel.getDAO());
        assertEquals(tipoExamenService, tipoExamenModel.getTipoExamenService());
        assertNotNull(tipoExamenModel.crearNuevoRegistro());
    }
}

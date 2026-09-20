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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.TipoMedioContactoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoMedioContacto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TipoMedioContactoModelTest {

    @Mock
    private TipoMedioContactoDAO tipoMedioContactoDAO;

    @InjectMocks
    private TipoMedioContactoModel tipoMedioContactoModel;

    @BeforeEach
    public void setUp() {
        tipoMedioContactoModel.setTipoMedioContactoDAO(tipoMedioContactoDAO);
    }

    @Test
    public void testInitYCargarDatos() {
        TipoMedioContacto tmc = new TipoMedioContacto(UUID.randomUUID());
        when(tipoMedioContactoDAO.findAll()).thenReturn(List.of(tmc));

        tipoMedioContactoModel.init();

        assertNotNull(tipoMedioContactoModel.getRegistros());
        assertEquals(1, tipoMedioContactoModel.getRegistros().size());
        verify(tipoMedioContactoDAO).findAll();
    }

    @Test
    public void testPrepararNuevo() {
        tipoMedioContactoModel.prepararNuevo();

        assertNotNull(tipoMedioContactoModel.getRegistroActual());
        assertEquals(Estado.CREAR, tipoMedioContactoModel.getEstado());
        assertTrue(tipoMedioContactoModel.isEstadoCrear());
    }

    @Test
    public void testSeleccionar() {
        TipoMedioContacto tmc = new TipoMedioContacto(UUID.randomUUID());
        tipoMedioContactoModel.seleccionar(tmc);

        assertEquals(tmc, tipoMedioContactoModel.getRegistroActual());
        assertEquals(Estado.MODIFICAR, tipoMedioContactoModel.getEstado());
        assertTrue(tipoMedioContactoModel.isEstadoModificar());
    }

    @Test
    public void testCancelar() {
        tipoMedioContactoModel.prepararNuevo();
        tipoMedioContactoModel.cancelar();

        assertNull(tipoMedioContactoModel.getRegistroActual());
        assertEquals(Estado.NINGUNO, tipoMedioContactoModel.getEstado());
        assertTrue(tipoMedioContactoModel.isEstadoNinguno());
    }

    @Test
    public void testGuardarCrear() {
        when(tipoMedioContactoDAO.findAll()).thenReturn(Collections.emptyList());

        tipoMedioContactoModel.prepararNuevo();
        tipoMedioContactoModel.getRegistroActual().setNombre("Telefono");

        tipoMedioContactoModel.guardar();

        verify(tipoMedioContactoDAO).create(any(TipoMedioContacto.class));
        assertEquals(Estado.NINGUNO, tipoMedioContactoModel.getEstado());
        assertNull(tipoMedioContactoModel.getRegistroActual());
    }

    @Test
    public void testGuardarModificar() {
        when(tipoMedioContactoDAO.findAll()).thenReturn(Collections.emptyList());

        TipoMedioContacto tmc = new TipoMedioContacto(UUID.randomUUID());
        tipoMedioContactoModel.seleccionar(tmc);

        tipoMedioContactoModel.guardar();

        verify(tipoMedioContactoDAO).update(tmc);
        assertEquals(Estado.NINGUNO, tipoMedioContactoModel.getEstado());
    }

    @Test
    public void testEliminar() {
        when(tipoMedioContactoDAO.findAll()).thenReturn(Collections.emptyList());

        TipoMedioContacto tmc = new TipoMedioContacto(UUID.randomUUID());
        tipoMedioContactoModel.eliminar(tmc);

        verify(tipoMedioContactoDAO).delete(tmc);
    }

    @Test
    public void testGettersAndSetters() {
        assertEquals(tipoMedioContactoDAO, tipoMedioContactoModel.getDAO());
        assertEquals(tipoMedioContactoDAO, tipoMedioContactoModel.getTipoMedioContactoDAO());
        assertNotNull(tipoMedioContactoModel.crearNuevoRegistro());
    }
}

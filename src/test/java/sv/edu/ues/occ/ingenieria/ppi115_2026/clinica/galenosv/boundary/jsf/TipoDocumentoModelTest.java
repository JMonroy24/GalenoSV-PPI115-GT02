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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.TipoDocumentoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoDocumento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
public class TipoDocumentoModelTest {

    @Mock
    private TipoDocumentoDAO tipoDocumentoDAO;

    @InjectMocks
    private TipoDocumentoModel tipoDocumentoModel;

    @BeforeEach
    public void setUp() {
        tipoDocumentoModel.setTipoDocumentoDAO(tipoDocumentoDAO);
    }

    @Test
    public void testInitYCargarDatos() {
        tipoDocumentoModel.init();
        assertNotNull(tipoDocumentoModel.getLazyModel());
        assertNull(tipoDocumentoModel.getRegistros());

    }

    @Test
    public void testPrepararNuevo() {
        tipoDocumentoModel.prepararNuevo();

        assertNotNull(tipoDocumentoModel.getRegistroActual());
        assertEquals(Estado.CREAR, tipoDocumentoModel.getEstado());
        assertTrue(tipoDocumentoModel.isEstadoCrear());
    }

    @Test
    public void testSeleccionar() {
        TipoDocumento td = new TipoDocumento(UUID.randomUUID());
        tipoDocumentoModel.seleccionar(td);

        assertEquals(td, tipoDocumentoModel.getRegistroActual());
        assertEquals(Estado.MODIFICAR, tipoDocumentoModel.getEstado());
        assertTrue(tipoDocumentoModel.isEstadoModificar());
    }

    @Test
    public void testCancelar() {
        tipoDocumentoModel.prepararNuevo();
        tipoDocumentoModel.cancelar();

        assertNull(tipoDocumentoModel.getRegistroActual());
        assertEquals(Estado.NINGUNO, tipoDocumentoModel.getEstado());
        assertTrue(tipoDocumentoModel.isEstadoNinguno());
    }

    @Test
    public void testGuardarCrear() {
        
        tipoDocumentoModel.prepararNuevo();
        tipoDocumentoModel.getRegistroActual().setNombre("DUI");

        tipoDocumentoModel.getRegistroActual().setNombre("Registro válido");
        tipoDocumentoModel.guardar();

        verify(tipoDocumentoDAO).create(any(TipoDocumento.class));
        assertEquals(Estado.NINGUNO, tipoDocumentoModel.getEstado());
        assertNull(tipoDocumentoModel.getRegistroActual());
    }

    @Test
    public void testGuardarModificar() {
        
        TipoDocumento td = new TipoDocumento(UUID.randomUUID());
        tipoDocumentoModel.seleccionar(td);

        tipoDocumentoModel.getRegistroActual().setNombre("Registro válido");
        tipoDocumentoModel.guardar();

        verify(tipoDocumentoDAO).update(td);
        assertEquals(Estado.NINGUNO, tipoDocumentoModel.getEstado());
    }

    @Test
    public void testEliminar() {
        
        TipoDocumento td = new TipoDocumento(UUID.randomUUID());
        tipoDocumentoModel.eliminar(td);

        verify(tipoDocumentoDAO).delete(td);
    }

    @Test
    public void testGettersAndSetters() {
        assertEquals(tipoDocumentoDAO, tipoDocumentoModel.getDAO());
        assertEquals(tipoDocumentoDAO, tipoDocumentoModel.getTipoDocumentoDAO());
        assertNotNull(tipoDocumentoModel.crearNuevoRegistro());
    }
}

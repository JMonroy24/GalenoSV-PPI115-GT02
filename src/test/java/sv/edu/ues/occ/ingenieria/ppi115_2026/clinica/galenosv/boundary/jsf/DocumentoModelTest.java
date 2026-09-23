package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DocumentoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Documento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DocumentoModelTest {

    @Mock
    private DocumentoDAO documentoDAO;

    @InjectMocks
    private DocumentoModel documentoModel;

    @BeforeEach
    public void setUp() {
        documentoModel.setDocumentoDAO(documentoDAO);
    }

    @Test
    public void testInitYCargarDatos() {
        documentoModel.init();

        // ModelTransaccional inicializa un LazyDataModel en lugar de cargar
        // todos los registros en memoria con findAll(); verificamos el lazy model.
        assertNotNull(documentoModel.getLazyModel());
        verifyNoInteractions(documentoDAO);
    }

    @Test
    public void testPrepararNuevo() {
        documentoModel.prepararNuevo();

        assertNotNull(documentoModel.getRegistroActual());
        assertEquals(Estado.CREAR, documentoModel.getEstado());
        assertTrue(documentoModel.isEstadoCrear());
    }

    @Test
    public void testSeleccionar() {
        Documento d = new Documento(UUID.randomUUID());
        documentoModel.seleccionar(d);

        assertEquals(d, documentoModel.getRegistroActual());
        assertEquals(Estado.MODIFICAR, documentoModel.getEstado());
        assertTrue(documentoModel.isEstadoModificar());
    }

    @Test
    public void testCancelar() {
        documentoModel.prepararNuevo();
        documentoModel.cancelar();

        assertNull(documentoModel.getRegistroActual());
        assertEquals(Estado.NINGUNO, documentoModel.getEstado());
        assertTrue(documentoModel.isEstadoNinguno());
    }

    @Test
    public void testGuardarCrear() {
        documentoModel.prepararNuevo();

        documentoModel.guardar();

        verify(documentoDAO).create(any(Documento.class));
        assertEquals(Estado.NINGUNO, documentoModel.getEstado());
        assertNull(documentoModel.getRegistroActual());
    }

    @Test
    public void testGuardarModificar() {
        Documento d = new Documento(UUID.randomUUID());
        documentoModel.seleccionar(d);

        documentoModel.guardar();

        verify(documentoDAO).update(d);
        assertEquals(Estado.NINGUNO, documentoModel.getEstado());
    }

    @Test
    public void testEliminar() {
        Documento d = new Documento(UUID.randomUUID());
        documentoModel.eliminar(d);

        verify(documentoDAO).delete(d);
    }

    @Test
    public void testGettersAndSetters() {
        documentoModel.init();
        assertEquals(documentoDAO, documentoModel.getDAO());
        assertEquals(documentoDAO, documentoModel.getDocumentoDAO());
        assertNotNull(documentoModel.crearNuevoRegistro());
        assertNotNull(documentoModel.getLazyModel());
    }
}

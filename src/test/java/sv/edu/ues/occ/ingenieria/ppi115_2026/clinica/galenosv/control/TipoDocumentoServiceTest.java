package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoDocumento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoDocumentoServiceTest {

    @Mock
    private TipoDocumentoDAO tipoDocumentoDAO;

    @InjectMocks
    private TipoDocumentoService tipoDocumentoService;

    @BeforeEach
    void setUp() {
        tipoDocumentoService.setTipoDocumentoDAO(tipoDocumentoDAO);
    }

    @Test
    void getDAORetornaTipoDocumentoDAO() {
        assertSame(tipoDocumentoDAO, tipoDocumentoService.getDAO());
        assertSame(tipoDocumentoDAO, tipoDocumentoService.getTipoDocumentoDAO());
    }

    @Test
    void crearValidaYPersiste() {
        TipoDocumento td = new TipoDocumento(UUID.randomUUID());
        td.setNombre("  DUI  ");
        td.setExpresionRegular("^[0-9]{8}-[0-9]$");
        when(tipoDocumentoDAO.existePorCampo("nombre", "DUI", td.getIdTipoDocumento())).thenReturn(false);

        tipoDocumentoService.crear(td);

        assertEquals("DUI", td.getNombre());
        verify(tipoDocumentoDAO).create(td);
    }

    @Test
    void crearRechazaNombreVacio() {
        TipoDocumento td = new TipoDocumento(UUID.randomUUID());
        td.setNombre("   ");

        assertThrows(ValidacionNegocioException.class, () -> tipoDocumentoService.crear(td));
        verify(tipoDocumentoDAO, never()).create(any());
    }

    @Test
    void crearRechazaNombreDuplicado() {
        TipoDocumento td = new TipoDocumento(UUID.randomUUID());
        td.setNombre("DUI");
        when(tipoDocumentoDAO.existePorCampo("nombre", "DUI", td.getIdTipoDocumento())).thenReturn(true);

        assertThrows(ValidacionNegocioException.class, () -> tipoDocumentoService.crear(td));
        verify(tipoDocumentoDAO, never()).create(any());
    }

    @Test
    void crearRechazaExpresionRegularInvalida() {
        TipoDocumento td = new TipoDocumento(UUID.randomUUID());
        td.setNombre("DUI");
        td.setExpresionRegular("[");

        assertThrows(ValidacionNegocioException.class, () -> tipoDocumentoService.crear(td));
        verify(tipoDocumentoDAO, never()).create(any());
    }

    @Test
    void actualizarValidaYActualiza() {
        TipoDocumento td = new TipoDocumento(UUID.randomUUID());
        td.setNombre("DUI");
        when(tipoDocumentoDAO.existePorCampo("nombre", "DUI", td.getIdTipoDocumento())).thenReturn(false);

        tipoDocumentoService.actualizar(td);

        verify(tipoDocumentoDAO).update(td);
    }

    @Test
    void eliminarDelegaEnDAO() {
        TipoDocumento td = new TipoDocumento(UUID.randomUUID());
        tipoDocumentoService.eliminar(td);

        verify(tipoDocumentoDAO).delete(td);
    }

    @Test
    void getActivosYFindAllActivosDeleganEnDAO() {
        List<TipoDocumento> lista = List.of(new TipoDocumento(UUID.randomUUID()));
        when(tipoDocumentoDAO.findAllActivos()).thenReturn(lista);

        assertSame(lista, tipoDocumentoService.getActivos());
        assertSame(lista, tipoDocumentoService.findAllActivos());
        verify(tipoDocumentoDAO, times(2)).findAllActivos();
    }
}

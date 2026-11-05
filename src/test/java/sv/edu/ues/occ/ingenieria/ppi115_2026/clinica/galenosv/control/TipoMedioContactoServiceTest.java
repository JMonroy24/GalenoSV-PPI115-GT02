package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoMedioContacto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoMedioContactoServiceTest {

    @Mock
    private TipoMedioContactoDAO tipoMedioContactoDAO;

    @InjectMocks
    private TipoMedioContactoService tipoMedioContactoService;

    @BeforeEach
    void setUp() {
        tipoMedioContactoService.setTipoMedioContactoDAO(tipoMedioContactoDAO);
    }

    @Test
    void getDAORetornaTipoMedioContactoDAO() {
        assertSame(tipoMedioContactoDAO, tipoMedioContactoService.getDAO());
        assertSame(tipoMedioContactoDAO, tipoMedioContactoService.getTipoMedioContactoDAO());
    }

    @Test
    void crearValidaYPersiste() {
        TipoMedioContacto tmc = new TipoMedioContacto(UUID.randomUUID());
        tmc.setNombre("  Teléfono  ");
        tmc.setExpresionRegular("^[0-9]{8}$");
        when(tipoMedioContactoDAO.existePorCampo("nombre", "Teléfono", tmc.getIdTipoMedioContacto())).thenReturn(false);

        tipoMedioContactoService.crear(tmc);

        assertEquals("Teléfono", tmc.getNombre());
        verify(tipoMedioContactoDAO).create(tmc);
    }

    @Test
    void crearRechazaNombreVacio() {
        TipoMedioContacto tmc = new TipoMedioContacto(UUID.randomUUID());
        tmc.setNombre("   ");

        assertThrows(ValidacionNegocioException.class, () -> tipoMedioContactoService.crear(tmc));
        verify(tipoMedioContactoDAO, never()).create(any());
    }

    @Test
    void crearRechazaNombreDuplicado() {
        TipoMedioContacto tmc = new TipoMedioContacto(UUID.randomUUID());
        tmc.setNombre("Teléfono");
        when(tipoMedioContactoDAO.existePorCampo("nombre", "Teléfono", tmc.getIdTipoMedioContacto())).thenReturn(true);

        assertThrows(ValidacionNegocioException.class, () -> tipoMedioContactoService.crear(tmc));
        verify(tipoMedioContactoDAO, never()).create(any());
    }

    @Test
    void crearRechazaExpresionRegularInvalida() {
        TipoMedioContacto tmc = new TipoMedioContacto(UUID.randomUUID());
        tmc.setNombre("Teléfono");
        tmc.setExpresionRegular("(");

        assertThrows(ValidacionNegocioException.class, () -> tipoMedioContactoService.crear(tmc));
        verify(tipoMedioContactoDAO, never()).create(any());
    }

    @Test
    void actualizarValidaYActualiza() {
        TipoMedioContacto tmc = new TipoMedioContacto(UUID.randomUUID());
        tmc.setNombre("Teléfono");
        when(tipoMedioContactoDAO.existePorCampo("nombre", "Teléfono", tmc.getIdTipoMedioContacto())).thenReturn(false);

        tipoMedioContactoService.actualizar(tmc);

        verify(tipoMedioContactoDAO).update(tmc);
    }

    @Test
    void eliminarDelegaEnDAO() {
        TipoMedioContacto tmc = new TipoMedioContacto(UUID.randomUUID());
        tipoMedioContactoService.eliminar(tmc);

        verify(tipoMedioContactoDAO).delete(tmc);
    }

    @Test
    void getActivosYFindAllActivosDeleganEnDAO() {
        List<TipoMedioContacto> lista = List.of(new TipoMedioContacto(UUID.randomUUID()));
        when(tipoMedioContactoDAO.findAllActivos()).thenReturn(lista);

        assertSame(lista, tipoMedioContactoService.getActivos());
        assertSame(lista, tipoMedioContactoService.findAllActivos());
        verify(tipoMedioContactoDAO, times(2)).findAllActivos();
    }
}

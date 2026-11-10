package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.ws.rs.NotFoundException;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error.PreconditionFailedException;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Clinica;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultServiceTest {

    @Mock
    private ClinicaDAO clinicaDAO;

    private ServicioPrueba service;

    private static class ServicioPrueba extends DefaultService<Clinica, UUID> {
        private final ClinicaDAO dao;
        boolean validarInvocado;
        boolean nuevoParametro;

        ServicioPrueba(ClinicaDAO dao) {
            this.dao = dao;
        }

        @Override
        protected DefaultDAO<Clinica, UUID> getDAO() {
            return dao;
        }

        @Override
        protected void validar(Clinica entidad, boolean nuevo) {
            this.validarInvocado = true;
            this.nuevoParametro = nuevo;
            super.validar(entidad, nuevo);
        }
    }

    @BeforeEach
    void setUp() {
        service = new ServicioPrueba(clinicaDAO);
    }

    @Test
    void crearInvocaValidarYCreate() {
        Clinica c = new Clinica(UUID.randomUUID());
        service.crear(c);

        assertTrue(service.validarInvocado);
        assertTrue(service.nuevoParametro);
        verify(clinicaDAO).create(c);
    }

    @Test
    void createDelegaEnCrear() {
        Clinica c = new Clinica(UUID.randomUUID());
        service.create(c);

        assertTrue(service.validarInvocado);
        assertTrue(service.nuevoParametro);
        verify(clinicaDAO).create(c);
    }

    @Test
    void actualizarInvocaValidarYUpdate() {
        Clinica c = new Clinica(UUID.randomUUID());
        service.actualizar(c);

        assertTrue(service.validarInvocado);
        assertFalse(service.nuevoParametro);
        verify(clinicaDAO).update(c);
    }

    @Test
    void updateDelegaEnActualizar() {
        Clinica c = new Clinica(UUID.randomUUID());
        service.update(c);

        assertTrue(service.validarInvocado);
        assertFalse(service.nuevoParametro);
        verify(clinicaDAO).update(c);
    }

    @Test
    void actualizarSiAplicaCambiosValidaYActualiza() {
        UUID id = UUID.randomUUID();
        Clinica c = new Clinica(id);
        when(clinicaDAO.findByIdParaActualizar(id)).thenReturn(c);

        Clinica resultado = service.actualizarSi(id, e -> true, e -> e.setNombre("Nuevo"));

        assertSame(c, resultado);
        assertEquals("Nuevo", c.getNombre());
        assertTrue(service.validarInvocado);
        assertFalse(service.nuevoParametro);
        verify(clinicaDAO).update(c);
    }

    @Test
    void actualizarSiInexistenteLanzaNotFound() {
        UUID id = UUID.randomUUID();
        when(clinicaDAO.findByIdParaActualizar(id)).thenReturn(null);

        assertThrows(NotFoundException.class,
                () -> service.actualizarSi(id, e -> true, e -> e.setNombre("Nuevo")));
        verify(clinicaDAO, never()).update(any());
    }

    @Test
    void actualizarSiConPrecondicionFallaNoAplicaCambiosNiActualiza() {
        UUID id = UUID.randomUUID();
        Clinica c = new Clinica(id);
        c.setNombre("Original");
        when(clinicaDAO.findByIdParaActualizar(id)).thenReturn(c);

        assertThrows(PreconditionFailedException.class,
                () -> service.actualizarSi(id, e -> false, e -> e.setNombre("Nuevo")));
        assertEquals("Original", c.getNombre());
        assertFalse(service.validarInvocado);
        verify(clinicaDAO, never()).update(any());
    }

    @Test
    void eliminarInvocaDelete() {
        Clinica c = new Clinica(UUID.randomUUID());
        service.eliminar(c);

        verify(clinicaDAO).delete(c);
    }

    @Test
    void deleteDelegaEnEliminar() {
        Clinica c = new Clinica(UUID.randomUUID());
        service.delete(c);

        verify(clinicaDAO).delete(c);
    }

    @Test
    void findByIdDelegaEnDAO() {
        UUID id = UUID.randomUUID();
        Clinica c = new Clinica(id);
        when(clinicaDAO.findById(id)).thenReturn(c);

        assertSame(c, service.findById(id));
        verify(clinicaDAO).findById(id);
    }

    @Test
    void findAllDelegaEnDAO() {
        List<Clinica> lista = List.of(new Clinica(UUID.randomUUID()));
        when(clinicaDAO.findAll()).thenReturn(lista);

        assertSame(lista, service.findAll());
        verify(clinicaDAO).findAll();
    }

    @Test
    void findRangeDelegaEnDAO() {
        List<Clinica> lista = List.of(new Clinica(UUID.randomUUID()));
        when(clinicaDAO.findRange(0, 10)).thenReturn(lista);

        assertSame(lista, service.findRange(0, 10));
        verify(clinicaDAO).findRange(0, 10);
    }

    @Test
    void countDelegaEnDAO() {
        when(clinicaDAO.count()).thenReturn(5L);

        assertEquals(5L, service.count());
        verify(clinicaDAO).count();
    }

    @Test
    void findRangeConFiltroGlobalDelegaEnDAO() {
        List<Clinica> lista = List.of(new Clinica(UUID.randomUUID()));
        when(clinicaDAO.findRange(0, 10, "filtro")).thenReturn(lista);

        assertSame(lista, service.findRange(0, 10, "filtro"));
        verify(clinicaDAO).findRange(0, 10, "filtro");
    }

    @Test
    void countConFiltroGlobalDelegaEnDAO() {
        when(clinicaDAO.count("filtro")).thenReturn(3L);

        assertEquals(3L, service.count("filtro"));
        verify(clinicaDAO).count("filtro");
    }

    @Test
    void findRangeConOrdenYFiltrosDelegaEnDAO() {
        List<Clinica> lista = List.of(new Clinica(UUID.randomUUID()));
        List<OrdenConsulta> orden = List.of();
        List<FiltroConsulta> filtros = List.of();
        when(clinicaDAO.findRange(0, 10, "filtro", orden, filtros)).thenReturn(lista);

        assertSame(lista, service.findRange(0, 10, "filtro", orden, filtros));
        verify(clinicaDAO).findRange(0, 10, "filtro", orden, filtros);
    }

    @Test
    void countConFiltrosDelegaEnDAO() {
        List<FiltroConsulta> filtros = List.of();
        when(clinicaDAO.count("filtro", filtros)).thenReturn(2L);

        assertEquals(2L, service.count("filtro", filtros));
        verify(clinicaDAO).count("filtro", filtros);
    }

    @Test
    void normalizarFiltroDelegaEnDefaultDAO() {
        assertEquals("test", DefaultService.normalizarFiltro("  test  "));
        assertNull(DefaultService.normalizarFiltro("   "));
    }
}

package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPaso;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcedimientoPasoDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private CriteriaQuery<ProcedimientoPaso> cq;

    @Mock
    private CriteriaQuery<Long> cqLong;

    @Mock
    private Root<ProcedimientoPaso> root;

    @Mock
    private TypedQuery<ProcedimientoPaso> query;

    @Mock
    private TypedQuery<Long> queryLong;

    @InjectMocks
    private ProcedimientoPasoDAO dao;

    @BeforeEach
    void setUp() {
        dao = new ProcedimientoPasoDAO(em);
    }

    @Test
    void testConstructorsAndGetEntityManager() {
        ProcedimientoPasoDAO defaultDao = new ProcedimientoPasoDAO();

        assertNull(defaultDao.getEntityManager());
        assertEquals(em, dao.getEntityManager());
    }

    @Test
    void testCreate() {
        ProcedimientoPaso entity = new ProcedimientoPaso(UUID.randomUUID());

        dao.create(entity);

        verify(em, times(1)).persist(entity);
    }

    @Test
    void testUpdate() {
        ProcedimientoPaso entity = new ProcedimientoPaso(UUID.randomUUID());

        dao.update(entity);

        verify(em, times(1)).merge(entity);
    }

    @Test
    void testDelete() {
        ProcedimientoPaso entity = new ProcedimientoPaso(UUID.randomUUID());
        when(em.merge(entity)).thenReturn(entity);

        dao.delete(entity);

        verify(em, times(1)).merge(entity);
        verify(em, times(1)).remove(entity);
    }

    @Test
    void testFindById() {
        UUID id = UUID.randomUUID();
        ProcedimientoPaso expected = new ProcedimientoPaso(id);
        when(em.find(ProcedimientoPaso.class, id)).thenReturn(expected);

        ProcedimientoPaso actual = dao.findById(id);

        assertNotNull(actual);
        assertEquals(id, actual.getIdProcedimientoPaso());
        verify(em, times(1)).find(ProcedimientoPaso.class, id);
    }

    @Test
    void testFindAll() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(ProcedimientoPaso.class)).thenReturn(cq);
        when(cq.from(ProcedimientoPaso.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.getResultList())
                .thenReturn(List.of(new ProcedimientoPaso(UUID.randomUUID())));

        List<ProcedimientoPaso> result = dao.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query, times(1)).getResultList();
    }

    @Test
    void testFindRange() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(ProcedimientoPaso.class)).thenReturn(cq);
        when(cq.from(ProcedimientoPaso.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList())
                .thenReturn(List.of(new ProcedimientoPaso(UUID.randomUUID())));

        List<ProcedimientoPaso> result = dao.findRange(0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
    }

    @Test
    void testCount() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Long.class)).thenReturn(cqLong);
        when(cqLong.from(ProcedimientoPaso.class)).thenReturn(root);
        when(cb.count(root)).thenReturn(null);
        when(em.createQuery(cqLong)).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(1L);

        long result = dao.count();

        assertEquals(1L, result);
        verify(queryLong).getSingleResult();
    }

    @Test
    void testFindByProcedimiento() {
        UUID id = UUID.randomUUID();
        when(em.createQuery(anyString(), eq(ProcedimientoPaso.class))).thenReturn(query);
        when(query.setParameter(eq("idProc"), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new ProcedimientoPaso(UUID.randomUUID())));

        List<ProcedimientoPaso> result = dao.findByProcedimiento(id, null);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testFindByProcedimientoNull() {
        List<ProcedimientoPaso> result = dao.findByProcedimiento(null, null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testFindByProcedimientoWithExclude() {
        UUID id = UUID.randomUUID();
        UUID ex = UUID.randomUUID();
        when(em.createQuery(anyString(), eq(ProcedimientoPaso.class))).thenReturn(query);
        when(query.setParameter(eq("idProc"), any())).thenReturn(query);
        when(query.setParameter(eq("excluir"), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new ProcedimientoPaso(UUID.randomUUID())));

        List<ProcedimientoPaso> result = dao.findByProcedimiento(id, ex);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testExisteNombreEnProcedimiento() {
        UUID id = UUID.randomUUID();
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(queryLong);
        when(queryLong.setParameter(eq("procedimiento"), any())).thenReturn(queryLong);
        when(queryLong.setParameter(eq("nombre"), anyString())).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(1L);

        boolean result = dao.existeNombreEnProcedimiento(id, "nombre", null);

        assertTrue(result);
    }
    
    @Test
    void testExisteNombreEnProcedimientoNulls() {
        assertFalse(dao.existeNombreEnProcedimiento(null, "n", null));
        assertFalse(dao.existeNombreEnProcedimiento(UUID.randomUUID(), null, null));
        assertFalse(dao.existeNombreEnProcedimiento(UUID.randomUUID(), "  ", null));
    }
    
    @Test
    void testExisteNombreEnProcedimientoWithExclude() {
        UUID id = UUID.randomUUID();
        UUID ex = UUID.randomUUID();
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(queryLong);
        when(queryLong.setParameter(eq("procedimiento"), any())).thenReturn(queryLong);
        when(queryLong.setParameter(eq("nombre"), anyString())).thenReturn(queryLong);
        when(queryLong.setParameter(eq("excluir"), any())).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(0L);

        boolean result = dao.existeNombreEnProcedimiento(id, "nombre", ex);

        assertFalse(result);
    }

    @Test
    void testTieneFin() {
        UUID id = UUID.randomUUID();
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(queryLong);
        when(queryLong.setParameter(eq("idProc"), any())).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(1L);

        boolean result = dao.tieneFin(id);

        assertTrue(result);
    }

    @Test
    void testTieneFinNull() {
        assertFalse(dao.tieneFin(null));
    }

    @Test
    void testFindPasoInicial() {
        UUID id = UUID.randomUUID();
        when(em.createQuery(anyString(), eq(ProcedimientoPaso.class))).thenReturn(query);
        when(query.setParameter(eq("idProc"), any())).thenReturn(query);
        when(query.setMaxResults(1)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new ProcedimientoPaso(UUID.randomUUID())));

        ProcedimientoPaso result = dao.findPasoInicial(id);

        assertNotNull(result);
    }
    
    @Test
    void testFindPasoInicialNull() {
        assertNull(dao.findPasoInicial(null));
    }
}
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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcedimientoPasoExamenDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private CriteriaQuery<ProcedimientoPasoExamen> cq;

    @Mock
    private CriteriaQuery<Long> cqLong;

    @Mock
    private Root<ProcedimientoPasoExamen> root;

    @Mock
    private TypedQuery<ProcedimientoPasoExamen> query;

    @Mock
    private TypedQuery<Long> queryLong;

    @InjectMocks
    private ProcedimientoPasoExamenDAO dao;

    @BeforeEach
    void setUp() {
        dao = new ProcedimientoPasoExamenDAO(em);
    }

    @Test
    void testConstructorsAndGetEntityManager() {
        ProcedimientoPasoExamenDAO defaultDao =
                new ProcedimientoPasoExamenDAO();

        assertNull(defaultDao.getEntityManager());
        assertEquals(em, dao.getEntityManager());
    }

    @Test
    void testCreate() {
        ProcedimientoPasoExamen entity =
                new ProcedimientoPasoExamen(UUID.randomUUID());

        dao.create(entity);

        verify(em, times(1)).persist(entity);
    }

    @Test
    void testUpdate() {
        ProcedimientoPasoExamen entity =
                new ProcedimientoPasoExamen(UUID.randomUUID());

        dao.update(entity);

        verify(em, times(1)).merge(entity);
    }

    @Test
    void testDelete() {
        ProcedimientoPasoExamen entity =
                new ProcedimientoPasoExamen(UUID.randomUUID());

        when(em.merge(entity)).thenReturn(entity);

        dao.delete(entity);

        verify(em, times(1)).merge(entity);
        verify(em, times(1)).remove(entity);
    }

    @Test
    void testFindById() {
        UUID id = UUID.randomUUID();
        ProcedimientoPasoExamen expected =
                new ProcedimientoPasoExamen(id);

        when(em.find(ProcedimientoPasoExamen.class, id))
                .thenReturn(expected);

        ProcedimientoPasoExamen actual = dao.findById(id);

        assertNotNull(actual);
        assertEquals(id, actual.getIdProcedimientoPasoExamen());
        verify(em, times(1))
                .find(ProcedimientoPasoExamen.class, id);
    }

    @Test
    void testFindAll() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(ProcedimientoPasoExamen.class))
                .thenReturn(cq);
        when(cq.from(ProcedimientoPasoExamen.class))
                .thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.getResultList()).thenReturn(
                List.of(new ProcedimientoPasoExamen(UUID.randomUUID()))
        );

        List<ProcedimientoPasoExamen> result = dao.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query, times(1)).getResultList();
    }

    @Test
    void testFindRange() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(ProcedimientoPasoExamen.class))
                .thenReturn(cq);
        when(cq.from(ProcedimientoPasoExamen.class))
                .thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(
                List.of(new ProcedimientoPasoExamen(UUID.randomUUID()))
        );

        List<ProcedimientoPasoExamen> result =
                dao.findRange(0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query, times(1)).setFirstResult(0);
        verify(query, times(1)).setMaxResults(10);
    }

    @Test
    void testCount() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Long.class)).thenReturn(cqLong);
        when(cqLong.from(ProcedimientoPasoExamen.class))
                .thenReturn(root);
        when(cb.count(root)).thenReturn(null);
        when(em.createQuery(cqLong)).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(4L);

        long count = dao.count();

        assertEquals(4L, count);
        verify(queryLong, times(1)).getSingleResult();
    }
}
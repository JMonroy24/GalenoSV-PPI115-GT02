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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Procedimiento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcedimientoDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private CriteriaQuery<Procedimiento> cq;

    @Mock
    private CriteriaQuery<Long> cqLong;

    @Mock
    private Root<Procedimiento> root;

    @Mock
    private TypedQuery<Procedimiento> query;

    @Mock
    private TypedQuery<Long> queryLong;

    @InjectMocks
    private ProcedimientoDAO dao;

    @BeforeEach
    void setUp() {
        dao = new ProcedimientoDAO(em);
    }

    @Test
    void testConstructorsAndGetEntityManager() {
        ProcedimientoDAO defaultDao = new ProcedimientoDAO();

        assertNull(defaultDao.getEntityManager());
        assertEquals(em, dao.getEntityManager());
    }

    @Test
    void testCreate() {
        Procedimiento entity = new Procedimiento(UUID.randomUUID());

        dao.create(entity);

        verify(em, times(1)).persist(entity);
    }

    @Test
    void testUpdate() {
        Procedimiento entity = new Procedimiento(UUID.randomUUID());

        dao.update(entity);

        verify(em, times(1)).merge(entity);
    }

    @Test
    void testDelete() {
        Procedimiento entity = new Procedimiento(UUID.randomUUID());
        when(em.merge(entity)).thenReturn(entity);

        dao.delete(entity);

        verify(em, times(1)).merge(entity);
        verify(em, times(1)).remove(entity);
    }

    @Test
    void testFindById() {
        UUID id = UUID.randomUUID();
        Procedimiento expected = new Procedimiento(id);
        when(em.find(Procedimiento.class, id)).thenReturn(expected);

        Procedimiento actual = dao.findById(id);

        assertNotNull(actual);
        assertEquals(id, actual.getIdProcedimiento());
        verify(em, times(1)).find(Procedimiento.class, id);
    }

    @Test
    void testFindAll() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Procedimiento.class)).thenReturn(cq);
        when(cq.from(Procedimiento.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.getResultList())
                .thenReturn(List.of(new Procedimiento(UUID.randomUUID())));

        List<Procedimiento> result = dao.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query, times(1)).getResultList();
    }

    @Test
    void testFindRange() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Procedimiento.class)).thenReturn(cq);
        when(cq.from(Procedimiento.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList())
                .thenReturn(List.of(new Procedimiento(UUID.randomUUID())));

        List<Procedimiento> result = dao.findRange(0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
    }

    @Test
    void testCount() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Long.class)).thenReturn(cqLong);
        when(cqLong.from(Procedimiento.class)).thenReturn(root);
        when(cb.count(root)).thenReturn(null);
        when(em.createQuery(cqLong)).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(1L);

        long result = dao.count();

        assertEquals(1L, result);
        verify(queryLong).getSingleResult();
    }
}
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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.OrdenExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdenExamenDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private CriteriaQuery<OrdenExamen> cq;

    @Mock
    private CriteriaQuery<Long> cqLong;

    @Mock
    private Root<OrdenExamen> root;

    @Mock
    private TypedQuery<OrdenExamen> query;

    @Mock
    private TypedQuery<Long> queryLong;

    @InjectMocks
    private OrdenExamenDAO dao;

    @BeforeEach
    void setUp() {
        dao = new OrdenExamenDAO(em);
    }

    @Test
    void testConstructorsAndGetEntityManager() {
        OrdenExamenDAO defaultDao = new OrdenExamenDAO();
        assertNull(defaultDao.getEntityManager());

        assertEquals(em, dao.getEntityManager());
    }

    @Test
    void testCreate() {
        OrdenExamen entity = new OrdenExamen(UUID.randomUUID());
        dao.create(entity);
        verify(em, times(1)).persist(entity);
    }

    @Test
    void testUpdate() {
        OrdenExamen entity = new OrdenExamen(UUID.randomUUID());
        dao.update(entity);
        verify(em, times(1)).merge(entity);
    }

    @Test
    void testDelete() {
        OrdenExamen entity = new OrdenExamen(UUID.randomUUID());
        when(em.merge(entity)).thenReturn(entity);

        dao.delete(entity);

        verify(em, times(1)).merge(entity);
        verify(em, times(1)).remove(entity);
    }

    @Test
    void testFindById() {
        UUID id = UUID.randomUUID();
        OrdenExamen expected = new OrdenExamen(id);
        when(em.find(OrdenExamen.class, id)).thenReturn(expected);

        OrdenExamen actual = dao.findById(id);

        assertNotNull(actual);
        assertEquals(id, actual.getIdOrdenExamen());
        verify(em, times(1)).find(OrdenExamen.class, id);
    }

    @Test
    void testFindAll() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(OrdenExamen.class)).thenReturn(cq);
        when(cq.from(OrdenExamen.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new OrdenExamen(UUID.randomUUID())));

        List<OrdenExamen> result = dao.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query, times(1)).getResultList();
    }

    @Test
    void testFindRange() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(OrdenExamen.class)).thenReturn(cq);
        when(cq.from(OrdenExamen.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new OrdenExamen(UUID.randomUUID())));

        List<OrdenExamen> result = dao.findRange(0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query, times(1)).setFirstResult(0);
        verify(query, times(1)).setMaxResults(10);
    }

    @Test
    void testCount() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Long.class)).thenReturn(cqLong);
        when(cqLong.from(OrdenExamen.class)).thenReturn(root);
        when(cb.count(root)).thenReturn(null);
        when(em.createQuery(cqLong)).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(5L);

        long count = dao.count();

        assertEquals(5L, count);
        verify(queryLong, times(1)).getSingleResult();
    }
}

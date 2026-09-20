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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Examen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private CriteriaQuery<Examen> cq;

    @Mock
    private CriteriaQuery<Long> cqLong;

    @Mock
    private Root<Examen> root;

    @Mock
    private TypedQuery<Examen> query;

    @Mock
    private TypedQuery<Long> queryLong;

    @InjectMocks
    private ExamenDAO dao;

    @BeforeEach
    void setUp() {
        dao = new ExamenDAO(em);
    }

    @Test
    void testConstructorsAndGetEntityManager() {
        ExamenDAO defaultDao = new ExamenDAO();

        assertNull(defaultDao.getEntityManager());
        assertEquals(em, dao.getEntityManager());
    }

    @Test
    void testCreate() {
        Examen entity = new Examen(UUID.randomUUID());

        dao.create(entity);

        verify(em, times(1)).persist(entity);
    }

    @Test
    void testUpdate() {
        Examen entity = new Examen(UUID.randomUUID());

        dao.update(entity);

        verify(em, times(1)).merge(entity);
    }

    @Test
    void testDelete() {
        Examen entity = new Examen(UUID.randomUUID());
        when(em.merge(entity)).thenReturn(entity);

        dao.delete(entity);

        verify(em, times(1)).merge(entity);
        verify(em, times(1)).remove(entity);
    }

    @Test
    void testFindById() {
        UUID id = UUID.randomUUID();
        Examen expected = new Examen(id);
        when(em.find(Examen.class, id)).thenReturn(expected);

        Examen actual = dao.findById(id);

        assertNotNull(actual);
        assertEquals(id, actual.getIdExamen());
        verify(em, times(1)).find(Examen.class, id);
    }

    @Test
    void testFindAll() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Examen.class)).thenReturn(cq);
        when(cq.from(Examen.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.getResultList())
                .thenReturn(List.of(new Examen(UUID.randomUUID())));

        List<Examen> result = dao.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query, times(1)).getResultList();
    }

    @Test
    void testFindRange() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Examen.class)).thenReturn(cq);
        when(cq.from(Examen.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList())
                .thenReturn(List.of(new Examen(UUID.randomUUID())));

        List<Examen> result = dao.findRange(0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
    }

    @Test
    void testCount() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Long.class)).thenReturn(cqLong);
        when(cqLong.from(Examen.class)).thenReturn(root);
        when(cb.count(root)).thenReturn(null);
        when(em.createQuery(cqLong)).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(1L);

        long result = dao.count();

        assertEquals(1L, result);
        verify(queryLong).getSingleResult();
    }
}
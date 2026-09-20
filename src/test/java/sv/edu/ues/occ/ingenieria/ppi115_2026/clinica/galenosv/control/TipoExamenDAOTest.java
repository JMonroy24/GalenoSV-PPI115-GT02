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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoExamenDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private CriteriaQuery<TipoExamen> cq;

    @Mock
    private CriteriaQuery<Long> cqLong;

    @Mock
    private Root<TipoExamen> root;

    @Mock
    private TypedQuery<TipoExamen> query;

    @Mock
    private TypedQuery<Long> queryLong;

    @InjectMocks
    private TipoExamenDAO dao;

    @BeforeEach
    void setUp() {
        dao = new TipoExamenDAO();
        dao.setEntityManager(em);
    }

    @Test
    void testConstructorsAndGetEntityManager() {
        TipoExamenDAO defaultDao = new TipoExamenDAO();
        assertNull(defaultDao.getEntityManager());

        defaultDao.setEntityManager(em);
        assertEquals(em, defaultDao.getEntityManager());
    }

    @Test
    void testSetEntityManager() {
        TipoExamenDAO localDao = new TipoExamenDAO();
        localDao.setEntityManager(em);
        assertEquals(em, localDao.getEntityManager());
    }

    @Test
    void testCreate() {
        TipoExamen tipoExamen = new TipoExamen(UUID.randomUUID());
        dao.create(tipoExamen);
        verify(em, times(1)).persist(tipoExamen);
    }

    @Test
    void testUpdate() {
        TipoExamen tipoExamen = new TipoExamen(UUID.randomUUID());
        dao.update(tipoExamen);
        verify(em, times(1)).merge(tipoExamen);
    }

    @Test
    void testDelete() {
        TipoExamen tipoExamen = new TipoExamen(UUID.randomUUID());
        when(em.merge(tipoExamen)).thenReturn(tipoExamen);

        dao.delete(tipoExamen);

        verify(em, times(1)).merge(tipoExamen);
        verify(em, times(1)).remove(tipoExamen);
    }

    @Test
    void testFindById() {
        UUID id = UUID.randomUUID();
        TipoExamen expected = new TipoExamen(id);
        when(em.find(TipoExamen.class, id)).thenReturn(expected);

        TipoExamen actual = dao.findById(id);

        assertNotNull(actual);
        assertEquals(id, actual.getIdTipoExamen());
        verify(em, times(1)).find(TipoExamen.class, id);
    }

    @Test
    void testFindAll() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(TipoExamen.class)).thenReturn(cq);
        when(cq.from(TipoExamen.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new TipoExamen(UUID.randomUUID())));

        List<TipoExamen> result = dao.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query, times(1)).getResultList();
    }

    @Test
    void testFindRange() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(TipoExamen.class)).thenReturn(cq);
        when(cq.from(TipoExamen.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new TipoExamen(UUID.randomUUID())));

        List<TipoExamen> result = dao.findRange(0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query, times(1)).setFirstResult(0);
        verify(query, times(1)).setMaxResults(10);
    }

    @Test
    void testCount() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Long.class)).thenReturn(cqLong);
        when(cqLong.from(TipoExamen.class)).thenReturn(root);
        when(cb.count(root)).thenReturn(null);
        when(em.createQuery(cqLong)).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(7L);

        long count = dao.count();

        assertEquals(7L, count);
        verify(queryLong, times(1)).getSingleResult();
    }
}

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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimiento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaProcedimientoDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private CriteriaQuery<ConsultaProcedimiento> cq;

    @Mock
    private CriteriaQuery<Long> cqLong;

    @Mock
    private Root<ConsultaProcedimiento> root;

    @Mock
    private TypedQuery<ConsultaProcedimiento> query;

    @Mock
    private TypedQuery<Long> queryLong;

    @InjectMocks
    private ConsultaProcedimientoDAO dao;

    @BeforeEach
    void setUp() {
        dao = new ConsultaProcedimientoDAO(em);
    }

    @Test
    void testConstructorsAndGetEntityManager() {
        ConsultaProcedimientoDAO defaultDao = new ConsultaProcedimientoDAO();
        assertNull(defaultDao.getEntityManager());

        assertEquals(em, dao.getEntityManager());
    }

    @Test
    void testCreate() {
        ConsultaProcedimiento entity = new ConsultaProcedimiento(UUID.randomUUID());
        dao.create(entity);
        verify(em, times(1)).persist(entity);
    }

    @Test
    void testUpdate() {
        ConsultaProcedimiento entity = new ConsultaProcedimiento(UUID.randomUUID());
        dao.update(entity);
        verify(em, times(1)).merge(entity);
    }

    @Test
    void testDelete() {
        ConsultaProcedimiento entity = new ConsultaProcedimiento(UUID.randomUUID());
        when(em.merge(entity)).thenReturn(entity);

        dao.delete(entity);

        verify(em, times(1)).merge(entity);
        verify(em, times(1)).remove(entity);
    }

    @Test
    void testFindById() {
        UUID id = UUID.randomUUID();
        ConsultaProcedimiento expected = new ConsultaProcedimiento(id);
        when(em.find(ConsultaProcedimiento.class, id)).thenReturn(expected);

        ConsultaProcedimiento actual = dao.findById(id);

        assertNotNull(actual);
        assertEquals(id, actual.getIdConsultaProcedimiento());
        verify(em, times(1)).find(ConsultaProcedimiento.class, id);
    }

    @Test
    void testFindAll() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(ConsultaProcedimiento.class)).thenReturn(cq);
        when(cq.from(ConsultaProcedimiento.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new ConsultaProcedimiento(UUID.randomUUID())));

        List<ConsultaProcedimiento> result = dao.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query, times(1)).getResultList();
    }

    @Test
    void testFindRange() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(ConsultaProcedimiento.class)).thenReturn(cq);
        when(cq.from(ConsultaProcedimiento.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new ConsultaProcedimiento(UUID.randomUUID())));

        List<ConsultaProcedimiento> result = dao.findRange(0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query, times(1)).setFirstResult(0);
        verify(query, times(1)).setMaxResults(10);
    }

    @Test
    void testCount() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Long.class)).thenReturn(cqLong);
        when(cqLong.from(ConsultaProcedimiento.class)).thenReturn(root);
        when(cb.count(root)).thenReturn(null);
        when(em.createQuery(cqLong)).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(2L);

        long count = dao.count();

        assertEquals(2L, count);
        verify(queryLong, times(1)).getSingleResult();
    }
}

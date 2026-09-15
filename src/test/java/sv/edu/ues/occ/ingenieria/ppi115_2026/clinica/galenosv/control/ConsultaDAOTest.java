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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Consulta;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private CriteriaQuery<Consulta> cq;

    @Mock
    private CriteriaQuery<Long> cqLong;

    @Mock
    private Root<Consulta> root;

    @Mock
    private TypedQuery<Consulta> query;

    @Mock
    private TypedQuery<Long> queryLong;

    @InjectMocks
    private ConsultaDAO dao;

    @BeforeEach
    void setUp() {
        dao = new ConsultaDAO(em);
    }

    @Test
    void testConstructorsAndGetEntityManager() {
        ConsultaDAO defaultDao = new ConsultaDAO();
        assertNull(defaultDao.getEntityManager());

        assertEquals(em, dao.getEntityManager());
    }

    @Test
    void testCreate() {
        Consulta consulta = new Consulta(UUID.randomUUID());
        dao.create(consulta);
        verify(em, times(1)).persist(consulta);
    }

    @Test
    void testUpdate() {
        Consulta consulta = new Consulta(UUID.randomUUID());
        dao.update(consulta);
        verify(em, times(1)).merge(consulta);
    }

    @Test
    void testDelete() {
        Consulta consulta = new Consulta(UUID.randomUUID());
        when(em.merge(consulta)).thenReturn(consulta);

        dao.delete(consulta);

        verify(em, times(1)).merge(consulta);
        verify(em, times(1)).remove(consulta);
    }

    @Test
    void testFindById() {
        UUID id = UUID.randomUUID();
        Consulta expected = new Consulta(id);
        when(em.find(Consulta.class, id)).thenReturn(expected);

        Consulta actual = dao.findById(id);

        assertNotNull(actual);
        assertEquals(id, actual.getIdConsulta());
        verify(em, times(1)).find(Consulta.class, id);
    }

    @Test
    void testFindAll() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Consulta.class)).thenReturn(cq);
        when(cq.from(Consulta.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new Consulta(UUID.randomUUID())));

        List<Consulta> result = dao.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query, times(1)).getResultList();
    }

    @Test
    void testFindRange() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Consulta.class)).thenReturn(cq);
        when(cq.from(Consulta.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new Consulta(UUID.randomUUID())));

        List<Consulta> result = dao.findRange(0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query, times(1)).setFirstResult(0);
        verify(query, times(1)).setMaxResults(10);
    }

    @Test
    void testCount() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Long.class)).thenReturn(cqLong);
        when(cqLong.from(Consulta.class)).thenReturn(root);
        when(cb.count(root)).thenReturn(null);
        when(em.createQuery(cqLong)).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(3L);

        long count = dao.count();

        assertEquals(3L, count);
        verify(queryLong, times(1)).getSingleResult();
    }
}

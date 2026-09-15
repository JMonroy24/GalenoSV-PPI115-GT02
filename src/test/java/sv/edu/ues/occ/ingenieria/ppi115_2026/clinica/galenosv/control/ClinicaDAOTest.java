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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Clinica;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClinicaDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private CriteriaQuery<Clinica> cq;

    @Mock
    private CriteriaQuery<Long> cqLong;

    @Mock
    private Root<Clinica> root;

    @Mock
    private TypedQuery<Clinica> query;

    @Mock
    private TypedQuery<Long> queryLong;

    @InjectMocks
    private ClinicaDAO dao;

    @BeforeEach
    void setUp() {
        dao = new ClinicaDAO(em);
    }

    @Test
    void testConstructorsAndGetEntityManager() {
        ClinicaDAO defaultDao = new ClinicaDAO();
        assertNull(defaultDao.getEntityManager());

        assertEquals(em, dao.getEntityManager());
    }

    @Test
    void testCreate() {
        Clinica clinica = new Clinica(UUID.randomUUID(), "Clinica Central");
        dao.create(clinica);
        verify(em, times(1)).persist(clinica);
    }

    @Test
    void testUpdate() {
        Clinica clinica = new Clinica(UUID.randomUUID(), "Clinica Norte");
        dao.update(clinica);
        verify(em, times(1)).merge(clinica);
    }

    @Test
    void testDelete() {
        Clinica clinica = new Clinica(UUID.randomUUID(), "Clinica Sur");
        when(em.merge(clinica)).thenReturn(clinica);

        dao.delete(clinica);

        verify(em, times(1)).merge(clinica);
        verify(em, times(1)).remove(clinica);
    }

    @Test
    void testFindById() {
        UUID id = UUID.randomUUID();
        Clinica expected = new Clinica(id, "Clinica Poniente");
        when(em.find(Clinica.class, id)).thenReturn(expected);

        Clinica actual = dao.findById(id);

        assertNotNull(actual);
        assertEquals(expected.getNombre(), actual.getNombre());
        verify(em, times(1)).find(Clinica.class, id);
    }

    @Test
    void testFindAll() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Clinica.class)).thenReturn(cq);
        when(cq.from(Clinica.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new Clinica(UUID.randomUUID(), "Clinica 1")));

        List<Clinica> result = dao.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query, times(1)).getResultList();
    }

    @Test
    void testFindRange() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Clinica.class)).thenReturn(cq);
        when(cq.from(Clinica.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new Clinica(UUID.randomUUID(), "Clinica 1")));

        List<Clinica> result = dao.findRange(0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query, times(1)).setFirstResult(0);
        verify(query, times(1)).setMaxResults(10);
    }

    @Test
    void testCount() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Long.class)).thenReturn(cqLong);
        when(cqLong.from(Clinica.class)).thenReturn(root);
        when(cb.count(root)).thenReturn(null);
        when(em.createQuery(cqLong)).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(5L);

        long count = dao.count();

        assertEquals(5L, count);
        verify(queryLong, times(1)).getSingleResult();
    }
}

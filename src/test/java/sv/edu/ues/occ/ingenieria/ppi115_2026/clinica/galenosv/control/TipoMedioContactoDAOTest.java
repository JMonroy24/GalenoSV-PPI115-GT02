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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoMedioContacto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoMedioContactoDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private CriteriaQuery<TipoMedioContacto> cq;

    @Mock
    private CriteriaQuery<Long> cqLong;

    @Mock
    private Root<TipoMedioContacto> root;

    @Mock
    private TypedQuery<TipoMedioContacto> query;

    @Mock
    private TypedQuery<Long> queryLong;

    @InjectMocks
    private TipoMedioContactoDAO dao;

    @BeforeEach
    void setUp() {
        dao = new TipoMedioContactoDAO(em);
    }

    @Test
    void testConstructorsAndGetEntityManager() {
        TipoMedioContactoDAO defaultDao = new TipoMedioContactoDAO();
        assertNull(defaultDao.getEntityManager());

        assertEquals(em, dao.getEntityManager());
    }

    @Test
    void testCreate() {
        TipoMedioContacto tipoMedioContacto = new TipoMedioContacto(UUID.randomUUID());
        dao.create(tipoMedioContacto);
        verify(em, times(1)).persist(tipoMedioContacto);
    }

    @Test
    void testUpdate() {
        TipoMedioContacto tipoMedioContacto = new TipoMedioContacto(UUID.randomUUID());
        dao.update(tipoMedioContacto);
        verify(em, times(1)).merge(tipoMedioContacto);
    }

    @Test
    void testDelete() {
        TipoMedioContacto tipoMedioContacto = new TipoMedioContacto(UUID.randomUUID());
        when(em.merge(tipoMedioContacto)).thenReturn(tipoMedioContacto);

        dao.delete(tipoMedioContacto);

        verify(em, times(1)).merge(tipoMedioContacto);
        verify(em, times(1)).remove(tipoMedioContacto);
    }

    @Test
    void testFindById() {
        UUID id = UUID.randomUUID();
        TipoMedioContacto expected = new TipoMedioContacto(id);
        when(em.find(TipoMedioContacto.class, id)).thenReturn(expected);

        TipoMedioContacto actual = dao.findById(id);

        assertNotNull(actual);
        assertEquals(id, actual.getIdTipoMedioContacto());
        verify(em, times(1)).find(TipoMedioContacto.class, id);
    }

    @Test
    void testFindAll() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(TipoMedioContacto.class)).thenReturn(cq);
        when(cq.from(TipoMedioContacto.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new TipoMedioContacto(UUID.randomUUID())));

        List<TipoMedioContacto> result = dao.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query, times(1)).getResultList();
    }

    @Test
    void testFindRange() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(TipoMedioContacto.class)).thenReturn(cq);
        when(cq.from(TipoMedioContacto.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new TipoMedioContacto(UUID.randomUUID())));

        List<TipoMedioContacto> result = dao.findRange(0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query, times(1)).setFirstResult(0);
        verify(query, times(1)).setMaxResults(10);
    }

    @Test
    void testCount() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Long.class)).thenReturn(cqLong);
        when(cqLong.from(TipoMedioContacto.class)).thenReturn(root);
        when(cb.count(root)).thenReturn(null);
        when(em.createQuery(cqLong)).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(7L);

        long count = dao.count();

        assertEquals(7L, count);
        verify(queryLong, times(1)).getSingleResult();
    }
}

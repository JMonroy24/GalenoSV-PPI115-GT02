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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoDocumento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoDocumentoDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private CriteriaQuery<TipoDocumento> cq;

    @Mock
    private CriteriaQuery<Long> cqLong;

    @Mock
    private Root<TipoDocumento> root;

    @Mock
    private TypedQuery<TipoDocumento> query;

    @Mock
    private TypedQuery<Long> queryLong;

    @InjectMocks
    private TipoDocumentoDAO dao;

    @BeforeEach
    void setUp() {
        dao = new TipoDocumentoDAO(em);
    }

    @Test
    void testConstructorsAndGetEntityManager() {
        TipoDocumentoDAO defaultDao = new TipoDocumentoDAO();
        assertNull(defaultDao.getEntityManager());

        assertEquals(em, dao.getEntityManager());
    }

    @Test
    void testCreate() {
        TipoDocumento tipoDocumento = new TipoDocumento(UUID.randomUUID());
        dao.create(tipoDocumento);
        verify(em, times(1)).persist(tipoDocumento);
    }

    @Test
    void testUpdate() {
        TipoDocumento tipoDocumento = new TipoDocumento(UUID.randomUUID());
        dao.update(tipoDocumento);
        verify(em, times(1)).merge(tipoDocumento);
    }

    @Test
    void testDelete() {
        TipoDocumento tipoDocumento = new TipoDocumento(UUID.randomUUID());
        when(em.merge(tipoDocumento)).thenReturn(tipoDocumento);

        dao.delete(tipoDocumento);

        verify(em, times(1)).merge(tipoDocumento);
        verify(em, times(1)).remove(tipoDocumento);
    }

    @Test
    void testFindById() {
        UUID id = UUID.randomUUID();
        TipoDocumento expected = new TipoDocumento(id);
        when(em.find(TipoDocumento.class, id)).thenReturn(expected);

        TipoDocumento actual = dao.findById(id);

        assertNotNull(actual);
        assertEquals(id, actual.getIdTipoDocumento());
        verify(em, times(1)).find(TipoDocumento.class, id);
    }

    @Test
    void testFindAll() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(TipoDocumento.class)).thenReturn(cq);
        when(cq.from(TipoDocumento.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new TipoDocumento(UUID.randomUUID())));

        List<TipoDocumento> result = dao.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query, times(1)).getResultList();
    }

    @Test
    void testFindRange() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(TipoDocumento.class)).thenReturn(cq);
        when(cq.from(TipoDocumento.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new TipoDocumento(UUID.randomUUID())));

        List<TipoDocumento> result = dao.findRange(0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query, times(1)).setFirstResult(0);
        verify(query, times(1)).setMaxResults(10);
    }

    @Test
    void testCount() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Long.class)).thenReturn(cqLong);
        when(cqLong.from(TipoDocumento.class)).thenReturn(root);
        when(cb.count(root)).thenReturn(null);
        when(em.createQuery(cqLong)).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(7L);

        long count = dao.count();

        assertEquals(7L, count);
        verify(queryLong, times(1)).getSingleResult();
    }
}

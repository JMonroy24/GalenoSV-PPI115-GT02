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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Documento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentoDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private CriteriaQuery<Documento> cq;

    @Mock
    private CriteriaQuery<Long> cqLong;

    @Mock
    private Root<Documento> root;

    @Mock
    private TypedQuery<Documento> query;

    @Mock
    private TypedQuery<Long> queryLong;

    @InjectMocks
    private DocumentoDAO dao;

    @BeforeEach
    void setUp() {
        dao = new DocumentoDAO(em);
    }

    @Test
    void testConstructorsAndGetEntityManager() {
        DocumentoDAO defaultDao = new DocumentoDAO();
        assertNull(defaultDao.getEntityManager());

        assertEquals(em, dao.getEntityManager());
    }

    @Test
    void testCreate() {
        Documento documento = new Documento(UUID.randomUUID());
        dao.create(documento);
        verify(em, times(1)).persist(documento);
    }

    @Test
    void testUpdate() {
        Documento documento = new Documento(UUID.randomUUID());
        dao.update(documento);
        verify(em, times(1)).merge(documento);
    }

    @Test
    void testDelete() {
        Documento documento = new Documento(UUID.randomUUID());
        when(em.merge(documento)).thenReturn(documento);

        dao.delete(documento);

        verify(em, times(1)).merge(documento);
        verify(em, times(1)).remove(documento);
    }

    @Test
    void testFindById() {
        UUID id = UUID.randomUUID();
        Documento expected = new Documento(id);
        when(em.find(Documento.class, id)).thenReturn(expected);

        Documento actual = dao.findById(id);

        assertNotNull(actual);
        assertEquals(id, actual.getIdDocumento());
        verify(em, times(1)).find(Documento.class, id);
    }

    @Test
    void testFindAll() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Documento.class)).thenReturn(cq);
        when(cq.from(Documento.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new Documento(UUID.randomUUID())));

        List<Documento> result = dao.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query, times(1)).getResultList();
    }

    @Test
    void testFindRange() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Documento.class)).thenReturn(cq);
        when(cq.from(Documento.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new Documento(UUID.randomUUID())));

        List<Documento> result = dao.findRange(0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query, times(1)).setFirstResult(0);
        verify(query, times(1)).setMaxResults(10);
    }

    @Test
    void testCount() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Long.class)).thenReturn(cqLong);
        when(cqLong.from(Documento.class)).thenReturn(root);
        when(cb.count(root)).thenReturn(null);
        when(em.createQuery(cqLong)).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(7L);

        long count = dao.count();

        assertEquals(7L, count);
        verify(queryLong, times(1)).getSingleResult();
    }

    @Test
    void testExisteTipoValor() {
        UUID tipo = UUID.randomUUID();
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(queryLong);
        when(queryLong.setParameter(eq("tipo"), any())).thenReturn(queryLong);
        when(queryLong.setParameter(eq("valor"), anyString())).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(1L);

        boolean result = dao.existeTipoValor(tipo, "valor", null);

        assertTrue(result);
    }

    @Test
    void testExisteTipoValorNulls() {
        assertFalse(dao.existeTipoValor(null, "v", null));
        assertFalse(dao.existeTipoValor(UUID.randomUUID(), null, null));
        assertFalse(dao.existeTipoValor(UUID.randomUUID(), " ", null));
    }

    @Test
    void testExisteTipoValorWithExclude() {
        UUID tipo = UUID.randomUUID();
        UUID ex = UUID.randomUUID();
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(queryLong);
        when(queryLong.setParameter(eq("tipo"), any())).thenReturn(queryLong);
        when(queryLong.setParameter(eq("valor"), anyString())).thenReturn(queryLong);
        when(queryLong.setParameter(eq("excluir"), any())).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(0L);

        boolean result = dao.existeTipoValor(tipo, "valor", ex);

        assertFalse(result);
    }

    @Test
    void testExistePersonaTipoValor() {
        UUID per = UUID.randomUUID();
        UUID tipo = UUID.randomUUID();
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(queryLong);
        when(queryLong.setParameter(eq("persona"), any())).thenReturn(queryLong);
        when(queryLong.setParameter(eq("tipo"), any())).thenReturn(queryLong);
        when(queryLong.setParameter(eq("valor"), anyString())).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(1L);

        boolean result = dao.existePersonaTipoValor(per, tipo, "valor", null);

        assertTrue(result);
    }

    @Test
    void testExistePersonaTipoValorNulls() {
        assertFalse(dao.existePersonaTipoValor(null, UUID.randomUUID(), "v", null));
        assertFalse(dao.existePersonaTipoValor(UUID.randomUUID(), null, "v", null));
        assertFalse(dao.existePersonaTipoValor(UUID.randomUUID(), UUID.randomUUID(), null, null));
        assertFalse(dao.existePersonaTipoValor(UUID.randomUUID(), UUID.randomUUID(), " ", null));
    }

    @Test
    void testExistePersonaTipoValorWithExclude() {
        UUID per = UUID.randomUUID();
        UUID tipo = UUID.randomUUID();
        UUID ex = UUID.randomUUID();
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(queryLong);
        when(queryLong.setParameter(eq("persona"), any())).thenReturn(queryLong);
        when(queryLong.setParameter(eq("tipo"), any())).thenReturn(queryLong);
        when(queryLong.setParameter(eq("valor"), anyString())).thenReturn(queryLong);
        when(queryLong.setParameter(eq("excluir"), any())).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(0L);

        boolean result = dao.existePersonaTipoValor(per, tipo, "valor", ex);

        assertFalse(result);
    }

    @Test
    void testFindByPersona() {
        UUID id = UUID.randomUUID();
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Documento.class)).thenReturn(cq);
        
        @SuppressWarnings("unchecked")
        Root<Documento> rootMock = mock(Root.class, RETURNS_DEEP_STUBS);
        when(cq.from(Documento.class)).thenReturn(rootMock);
        when(cq.select(rootMock)).thenReturn(cq);
        when(cq.where((jakarta.persistence.criteria.Predicate) any())).thenReturn(cq);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new Documento(UUID.randomUUID())));

        List<Documento> result = dao.findByPersona(id);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}

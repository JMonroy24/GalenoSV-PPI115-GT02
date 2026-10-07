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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.MedioContacto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedioContactoDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private CriteriaQuery<MedioContacto> cq;

    @Mock
    private CriteriaQuery<Long> cqLong;

    @Mock
    private Root<MedioContacto> root;

    @Mock
    private TypedQuery<MedioContacto> query;

    @Mock
    private TypedQuery<Long> queryLong;

    @InjectMocks
    private MedioContactoDAO dao;

    @BeforeEach
    void setUp() {
        dao = new MedioContactoDAO(em);
    }

    @Test
    void testConstructorsAndGetEntityManager() {
        MedioContactoDAO defaultDao = new MedioContactoDAO();
        assertNull(defaultDao.getEntityManager());

        assertEquals(em, dao.getEntityManager());
    }

    @Test
    void testCreate() {
        MedioContacto entity = new MedioContacto(UUID.randomUUID());
        dao.create(entity);
        verify(em, times(1)).persist(entity);
    }

    @Test
    void testUpdate() {
        MedioContacto entity = new MedioContacto(UUID.randomUUID());
        dao.update(entity);
        verify(em, times(1)).merge(entity);
    }

    @Test
    void testDelete() {
        MedioContacto entity = new MedioContacto(UUID.randomUUID());
        when(em.merge(entity)).thenReturn(entity);

        dao.delete(entity);

        verify(em, times(1)).merge(entity);
        verify(em, times(1)).remove(entity);
    }

    @Test
    void testFindById() {
        UUID id = UUID.randomUUID();
        MedioContacto expected = new MedioContacto(id);
        when(em.find(MedioContacto.class, id)).thenReturn(expected);

        MedioContacto actual = dao.findById(id);

        assertNotNull(actual);
        assertEquals(id, actual.getIdMedioContacto());
        verify(em, times(1)).find(MedioContacto.class, id);
    }

    @Test
    void testFindAll() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(MedioContacto.class)).thenReturn(cq);
        when(cq.from(MedioContacto.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new MedioContacto(UUID.randomUUID())));

        List<MedioContacto> result = dao.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query, times(1)).getResultList();
    }

    @Test
    void testFindRange() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(MedioContacto.class)).thenReturn(cq);
        when(cq.from(MedioContacto.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new MedioContacto(UUID.randomUUID())));

        List<MedioContacto> result = dao.findRange(0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query, times(1)).setFirstResult(0);
        verify(query, times(1)).setMaxResults(10);
    }

    @Test
    void testCount() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Long.class)).thenReturn(cqLong);
        when(cqLong.from(MedioContacto.class)).thenReturn(root);
        when(cb.count(root)).thenReturn(null);
        when(em.createQuery(cqLong)).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(3L);

        long count = dao.count();

        assertEquals(3L, count);
        verify(queryLong, times(1)).getSingleResult();
    }

    @Test
    void testExistePersonaTipoValor() {
        UUID idPersona = UUID.randomUUID();
        UUID idTipo = UUID.randomUUID();
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(queryLong);
        when(queryLong.setParameter(eq("persona"), any())).thenReturn(queryLong);
        when(queryLong.setParameter(eq("tipo"), any())).thenReturn(queryLong);
        when(queryLong.setParameter(eq("valor"), anyString())).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(1L);

        boolean result = dao.existePersonaTipoValor(idPersona, idTipo, "valor1", null);

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
        UUID idPersona = UUID.randomUUID();
        UUID idTipo = UUID.randomUUID();
        UUID idExclude = UUID.randomUUID();
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(queryLong);
        when(queryLong.setParameter(eq("persona"), any())).thenReturn(queryLong);
        when(queryLong.setParameter(eq("tipo"), any())).thenReturn(queryLong);
        when(queryLong.setParameter(eq("valor"), anyString())).thenReturn(queryLong);
        when(queryLong.setParameter(eq("excluir"), any())).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(0L);

        boolean result = dao.existePersonaTipoValor(idPersona, idTipo, "valor1", idExclude);

        assertFalse(result);
    }

    @Test
    void testFindByPersona() {
        UUID idPersona = UUID.randomUUID();
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(MedioContacto.class)).thenReturn(cq);
        
        @SuppressWarnings("unchecked")
        Root<MedioContacto> rootMock = mock(Root.class, RETURNS_DEEP_STUBS);
        when(cq.from(MedioContacto.class)).thenReturn(rootMock);
        when(cq.select(rootMock)).thenReturn(cq);
        when(cq.where((jakarta.persistence.criteria.Predicate) any())).thenReturn(cq);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new MedioContacto(UUID.randomUUID())));

        List<MedioContacto> result = dao.findByPersona(idPersona);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}

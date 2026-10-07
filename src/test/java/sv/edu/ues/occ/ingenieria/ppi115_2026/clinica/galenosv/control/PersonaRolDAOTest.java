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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonaRolDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private CriteriaQuery<PersonaRol> cq;

    @Mock
    private CriteriaQuery<Long> cqLong;

    @Mock
    private Root<PersonaRol> root;

    @Mock
    private TypedQuery<PersonaRol> query;

    @Mock
    private TypedQuery<Long> queryLong;

    @InjectMocks
    private PersonaRolDAO dao;

    @BeforeEach
    void setUp() {
        dao = new PersonaRolDAO(em);
    }

    @Test
    void testConstructorsAndGetEntityManager() {
        PersonaRolDAO defaultDao = new PersonaRolDAO();
        assertNull(defaultDao.getEntityManager());

        assertEquals(em, dao.getEntityManager());
    }

    @Test
    void testCreate() {
        PersonaRol entity = new PersonaRol(UUID.randomUUID());
        dao.create(entity);
        verify(em, times(1)).persist(entity);
    }

    @Test
    void testUpdate() {
        PersonaRol entity = new PersonaRol(UUID.randomUUID());
        dao.update(entity);
        verify(em, times(1)).merge(entity);
    }

    @Test
    void testDelete() {
        PersonaRol entity = new PersonaRol(UUID.randomUUID());
        when(em.merge(entity)).thenReturn(entity);

        dao.delete(entity);

        verify(em, times(1)).merge(entity);
        verify(em, times(1)).remove(entity);
    }

    @Test
    void testFindById() {
        UUID id = UUID.randomUUID();
        PersonaRol expected = new PersonaRol(id);
        when(em.find(PersonaRol.class, id)).thenReturn(expected);

        PersonaRol actual = dao.findById(id);

        assertNotNull(actual);
        assertEquals(id, actual.getIdPersonaRol());
        verify(em, times(1)).find(PersonaRol.class, id);
    }

    @Test
    void testFindAll() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(PersonaRol.class)).thenReturn(cq);
        when(cq.from(PersonaRol.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new PersonaRol(UUID.randomUUID())));

        List<PersonaRol> result = dao.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query, times(1)).getResultList();
    }

    @Test
    void testFindRange() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(PersonaRol.class)).thenReturn(cq);
        when(cq.from(PersonaRol.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new PersonaRol(UUID.randomUUID())));

        List<PersonaRol> result = dao.findRange(0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query, times(1)).setFirstResult(0);
        verify(query, times(1)).setMaxResults(10);
    }

    @Test
    void testCount() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Long.class)).thenReturn(cqLong);
        when(cqLong.from(PersonaRol.class)).thenReturn(root);
        when(cb.count(root)).thenReturn(null);
        when(em.createQuery(cqLong)).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(3L);

        long count = dao.count();

        assertEquals(3L, count);
        verify(queryLong, times(1)).getSingleResult();
    }

    @Test
    void testBuscarParaAutocompletar() {
        when(em.createQuery(anyString(), eq(PersonaRol.class))).thenReturn(query);
        when(query.setParameter(eq("patron"), anyString())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new PersonaRol(UUID.randomUUID())));

        List<PersonaRol> result = dao.buscarParaAutocompletar("test", 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
    
    @Test
    void testBuscarParaAutocompletar_Null() {
        assertNotNull(dao.buscarParaAutocompletar(null, 10));
        assertTrue(dao.buscarParaAutocompletar("t", 10).isEmpty());
    }

    @Test
    void testFindByClinicaAndRol() {
        UUID clinica = UUID.randomUUID();
        UUID rol = UUID.randomUUID();
        when(em.createQuery(anyString(), eq(PersonaRol.class))).thenReturn(query);
        when(query.setParameter(eq("clinica"), any())).thenReturn(query);
        when(query.setParameter(eq("rol"), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new PersonaRol(UUID.randomUUID())));

        List<PersonaRol> result = dao.findByClinicaAndRol(clinica, rol);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
    
    @Test
    void testFindByClinicaAndRolNull() {
        assertTrue(dao.findByClinicaAndRol(null, UUID.randomUUID()).isEmpty());
        assertTrue(dao.findByClinicaAndRol(UUID.randomUUID(), null).isEmpty());
    }

    @Test
    void testExisteAsignacion() {
        UUID persona = UUID.randomUUID();
        UUID rol = UUID.randomUUID();
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(queryLong);
        when(queryLong.setParameter(eq("persona"), any())).thenReturn(queryLong);
        when(queryLong.setParameter(eq("rol"), any())).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(1L);

        boolean result = dao.existeAsignacion(persona, rol, null, null);

        assertTrue(result);
    }
    
    @Test
    void testExisteAsignacionWithClinicaAndExclude() {
        UUID persona = UUID.randomUUID();
        UUID rol = UUID.randomUUID();
        UUID clinica = UUID.randomUUID();
        UUID excluir = UUID.randomUUID();
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(queryLong);
        when(queryLong.setParameter(eq("persona"), any())).thenReturn(queryLong);
        when(queryLong.setParameter(eq("rol"), any())).thenReturn(queryLong);
        when(queryLong.setParameter(eq("clinica"), any())).thenReturn(queryLong);
        when(queryLong.setParameter(eq("excluir"), any())).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(0L);

        boolean result = dao.existeAsignacion(persona, rol, clinica, excluir);

        assertFalse(result);
    }

    @Test
    void testExisteAsignacionNulls() {
        assertFalse(dao.existeAsignacion(null, UUID.randomUUID(), null, null));
        assertFalse(dao.existeAsignacion(UUID.randomUUID(), null, null, null));
    }

    @Test
    void testFindByPersona() {
        UUID idPersona = UUID.randomUUID();
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(PersonaRol.class)).thenReturn(cq);
        
        @SuppressWarnings("unchecked")
        Root<PersonaRol> rootMock = mock(Root.class, RETURNS_DEEP_STUBS);
        when(cq.from(PersonaRol.class)).thenReturn(rootMock);
        when(cq.select(rootMock)).thenReturn(cq);
        when(cq.where((jakarta.persistence.criteria.Predicate) any())).thenReturn(cq);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new PersonaRol(UUID.randomUUID())));

        List<PersonaRol> result = dao.findByPersona(idPersona);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testBuscarPacientes() {
        UUID idClinica = UUID.randomUUID();
        when(em.createQuery(anyString(), eq(PersonaRol.class))).thenReturn(query);
        when(query.setParameter(eq("clinica"), any())).thenReturn(query);
        when(query.setParameter(eq("rolPaciente"), anyString())).thenReturn(query);
        when(query.setParameter(eq("patron"), anyString())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new PersonaRol(UUID.randomUUID())));

        List<PersonaRol> result = dao.buscarPacientes("test", idClinica, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
    
    @Test
    void testBuscarPacientes_Null() {
        assertTrue(dao.buscarPacientes(null, UUID.randomUUID(), 10).isEmpty());
        assertTrue(dao.buscarPacientes("t", UUID.randomUUID(), 10).isEmpty());
        assertTrue(dao.buscarPacientes("test", null, 10).isEmpty());
    }

    @Test
    void testFindResponsable() {
        UUID idClinica = UUID.randomUUID();
        UUID idRol = UUID.randomUUID();
        when(em.createQuery(anyString(), eq(PersonaRol.class))).thenReturn(query);
        when(query.setParameter(eq("clinica"), any())).thenReturn(query);
        when(query.setParameter(eq("rol"), any())).thenReturn(query);
        when(query.setMaxResults(1)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new PersonaRol(UUID.randomUUID())));

        PersonaRol result = dao.findResponsable(idClinica, idRol);

        assertNotNull(result);
    }
    
    @Test
    void testFindResponsableNulls() {
        assertNull(dao.findResponsable(null, UUID.randomUUID()));
        assertNull(dao.findResponsable(UUID.randomUUID(), null));
    }
}

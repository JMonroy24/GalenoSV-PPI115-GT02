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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoSecuencia;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del componente de acceso a datos encargado de las
 * relaciones de secuencia entre los pasos de un procedimiento.
 *
 * Se utiliza un EntityManager simulado para verificar que el DAO delegue
 * correctamente las operaciones CRUD y las consultas heredadas de DefaultDAO,
 * sin acceder a una base de datos real.
 */
@ExtendWith(MockitoExtension.class)
class ProcedimientoPasoSecuenciaDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private CriteriaQuery<ProcedimientoPasoSecuencia> cq;

    @Mock
    private CriteriaQuery<Long> cqLong;

    @Mock
    private Root<ProcedimientoPasoSecuencia> root;

    @Mock
    private TypedQuery<ProcedimientoPasoSecuencia> query;

    @Mock
    private TypedQuery<Long> queryLong;

    @InjectMocks
    private ProcedimientoPasoSecuenciaDAO dao;

    /**
     * Crea el DAO con el EntityManager simulado antes de cada prueba.
     */
    @BeforeEach
    void setUp() {
        dao = new ProcedimientoPasoSecuenciaDAO(em);
    }

    /**
     * Verifica el constructor requerido por CDI y el constructor utilizado
     * para proporcionar manualmente un EntityManager.
     */
    @Test
    void testConstructorsAndGetEntityManager() {
        ProcedimientoPasoSecuenciaDAO defaultDao =
                new ProcedimientoPasoSecuenciaDAO();

        assertNull(defaultDao.getEntityManager());
        assertEquals(em, dao.getEntityManager());
    }

    /**
     * Verifica que crear una relación de secuencia invoque persist exactamente
     * una vez sobre el EntityManager.
     */
    @Test
    void testCreate() {
        ProcedimientoPasoSecuencia entity =
                new ProcedimientoPasoSecuencia(UUID.randomUUID());

        dao.create(entity);

        verify(em, times(1)).persist(entity);
    }

    /**
     * Verifica que modificar una relación de secuencia invoque merge sobre el
     * EntityManager.
     */
    @Test
    void testUpdate() {
        ProcedimientoPasoSecuencia entity =
                new ProcedimientoPasoSecuencia(UUID.randomUUID());

        dao.update(entity);

        verify(em, times(1)).merge(entity);
    }

    /**
     * Verifica que eliminar una relación primero adjunte la entidad mediante
     * merge y después invoque remove.
     */
    @Test
    void testDelete() {
        ProcedimientoPasoSecuencia entity =
                new ProcedimientoPasoSecuencia(UUID.randomUUID());

        when(em.merge(entity)).thenReturn(entity);

        dao.delete(entity);

        verify(em, times(1)).merge(entity);
        verify(em, times(1)).remove(entity);
    }

    /**
     * Verifica la búsqueda de una relación de secuencia mediante su
     * identificador UUID.
     */
    @Test
    void testFindById() {
        UUID id = UUID.randomUUID();
        ProcedimientoPasoSecuencia expected =
                new ProcedimientoPasoSecuencia(id);

        when(em.find(ProcedimientoPasoSecuencia.class, id))
                .thenReturn(expected);

        ProcedimientoPasoSecuencia actual = dao.findById(id);

        assertNotNull(actual);
        assertEquals(
                id,
                actual.getIdProcedimientoPasoSecuencia()
        );
        verify(em, times(1))
                .find(ProcedimientoPasoSecuencia.class, id);
    }

    /**
     * Verifica que la consulta general devuelva todas las relaciones de
     * secuencia proporcionadas por el TypedQuery.
     */
    @Test
    void testFindAll() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(ProcedimientoPasoSecuencia.class))
                .thenReturn(cq);
        when(cq.from(ProcedimientoPasoSecuencia.class))
                .thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.getResultList()).thenReturn(
                List.of(
                        new ProcedimientoPasoSecuencia(UUID.randomUUID())
                )
        );

        List<ProcedimientoPasoSecuencia> result = dao.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query, times(1)).getResultList();
    }

    /**
     * Verifica que la consulta paginada aplique correctamente la posición
     * inicial y la cantidad máxima de resultados.
     */
    @Test
    void testFindRange() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(ProcedimientoPasoSecuencia.class))
                .thenReturn(cq);
        when(cq.from(ProcedimientoPasoSecuencia.class))
                .thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(
                List.of(
                        new ProcedimientoPasoSecuencia(UUID.randomUUID())
                )
        );

        List<ProcedimientoPasoSecuencia> result =
                dao.findRange(0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query, times(1)).setFirstResult(0);
        verify(query, times(1)).setMaxResults(10);
    }

    /**
     * Verifica que el conteo retorne la cantidad de relaciones de secuencia
     * proporcionada por la consulta Criteria.
     */
    @Test
    void testCount() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Long.class)).thenReturn(cqLong);
        when(cqLong.from(ProcedimientoPasoSecuencia.class))
                .thenReturn(root);
        when(cb.count(root)).thenReturn(null);
        when(em.createQuery(cqLong)).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(4L);

        long count = dao.count();

        assertEquals(4L, count);
        verify(queryLong, times(1)).getSingleResult();
    }
}
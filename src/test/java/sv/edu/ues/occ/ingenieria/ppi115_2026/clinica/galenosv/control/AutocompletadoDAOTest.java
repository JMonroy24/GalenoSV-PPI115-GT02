package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AutocompletadoDAOTest {
    static Stream<Arguments> consultas() {
        return Stream.of(ConsultaDAO.class, ConsultaProcedimientoDAO.class,
                ConsultaProcedimientoPasoDAO.class, OrdenExamenDAO.class, PersonaDAO.class,
                PersonaRolDAO.class, ExamenDAO.class, ProcedimientoDAO.class,
                RolDAO.class, TipoExamenDAO.class).map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("consultas")
    void filtroVacioNoAccedeAlEntityManager(Class<? extends DefaultDAO<?, ?>> tipo) throws Exception {
        EntityManager em = mock(EntityManager.class);
        DefaultDAO<?, ?> dao = tipo.getConstructor(EntityManager.class).newInstance(em);
        for (String filtro : new String[]{null, "", "  ", "\t\n", "\u2003"}) {
            assertTrue(dao.buscarParaAutocompletar(filtro, 20).isEmpty());
        }
        verifyNoInteractions(em);
    }

    @ParameterizedTest
    @MethodSource("consultas")
    @SuppressWarnings({"rawtypes", "unchecked"})
    void consultaLiteralAcotadaPropagaErrores(Class<? extends DefaultDAO<?, ?>> tipo) throws Exception {
        EntityManager em = mock(EntityManager.class);
        TypedQuery query = mock(TypedQuery.class, RETURNS_SELF);
        when(em.createNamedQuery(anyString(), any(Class.class))).thenReturn(query);
        DefaultDAO<?, ?> dao = tipo.getConstructor(EntityManager.class).newInstance(em);
        dao.buscarParaAutocompletar("  50%_  ", 20);
        verify(query).setParameter("patron", "%50\\%\\_%");
        verify(query).setMaxResults(20);
        var error = new PersistenceException("Conexión no disponible");
        when(query.getResultList()).thenThrow(error);
        assertSame(error, assertThrows(PersistenceException.class,
                () -> dao.buscarParaAutocompletar("Ana", 20)));
    }
}

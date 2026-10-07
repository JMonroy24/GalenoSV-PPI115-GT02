package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultDAOTest {
    private final EntityManager em = mock(EntityManager.class);
    private final PersonaDAO dao = new PersonaDAO(em);
    private final CriteriaBuilder cb = mock(CriteriaBuilder.class, RETURNS_DEEP_STUBS);
    @SuppressWarnings("unchecked")
    private final CriteriaQuery<Persona> cq = mock(CriteriaQuery.class, RETURNS_SELF);
    @SuppressWarnings("unchecked")
    private final Root<Persona> root = mock(Root.class, RETURNS_DEEP_STUBS);
    @SuppressWarnings("unchecked")
    private final TypedQuery<Persona> query = mock(TypedQuery.class, RETURNS_SELF);

    private void configurarPagina() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Persona.class)).thenReturn(cq);
        when(cq.from(Persona.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
    }

    @Test
    void rechazaEntidadesNulasSinPersistir() {
        assertThrows(NullPointerException.class, () -> dao.create(null));
        assertThrows(NullPointerException.class, () -> dao.update(null));
        assertThrows(NullPointerException.class, () -> dao.delete(null));
        assertThrows(NullPointerException.class, () -> dao.findById(null));
        verifyNoInteractions(em);
    }

    @Test
    void validaLimitesAntesDeConsultar() {
        assertThrows(IllegalArgumentException.class, () -> dao.findRange(-1, 10));
        assertThrows(IllegalArgumentException.class, () -> dao.findRange(0, 0, "ana"));
        assertThrows(IllegalArgumentException.class, () -> dao.findRange(0, -10));
        verifyNoInteractions(em);
    }

    @Test
    void normalizaYEscapaBusquedaLiteral() {
        assertNull(DefaultDAO.normalizarFiltro("   "));
        assertEquals("Ana", DefaultDAO.normalizarFiltro(" Ana "));
        assertEquals(100, DefaultDAO.normalizarFiltro("x".repeat(120)).length());
        assertEquals("50\\%\\_\\\\", DefaultDAO.escaparLike("50%_\\"));
        assertEquals("%á\\%\\_%", DefaultDAO.patronBusqueda(" Á%_ "));
    }

    @Test
    void ordenPredeterminadoUsaIdentificadorUnico() {
        configurarPagina();
        Order porId = cb.asc(root.get("idPersona"));
        dao.findRange(20, 10);
        verify(cq).orderBy(List.of(porId));
        verify(query).setFirstResult(20);
        verify(query).setMaxResults(10);
    }

    @Test
    void ordenSolicitadoConservaDesempatePorId() {
        configurarPagina();
        Order nombre = cb.desc(root.get("nombres"));
        Order porId = cb.asc(root.get("idPersona"));
        dao.findRange(0, 10, null, List.of(new OrdenConsulta("nombres", false)), List.of());
        verify(cq).orderBy(List.of(nombre, porId));
    }

    @Test
    void ordenPorIdNoSeDuplica() {
        configurarPagina();
        Order porId = cb.desc(root.get("idPersona"));
        dao.findRange(0, 10, null, List.of(new OrdenConsulta("idPersona", false)), List.of());
        verify(cq).orderBy(List.of(porId));
    }

    @Test
    void rechazaRutaAjenaAtributosPersistentes() {
        configurarPagina();
        assertThrows(IllegalArgumentException.class, () -> dao.findRange(0, 10, null,
                List.of(new OrdenConsulta("nombres desc; DROP TABLE persona", false)), List.of()));
        assertThrows(IllegalArgumentException.class, () -> dao.findRange(0, 10, null,
                List.of(new OrdenConsulta("serialVersionUID", false)), List.of()));
        assertThrows(IllegalArgumentException.class, () -> dao.findRange(0, 10, null,
                List.of(new OrdenConsulta("documentoList.valor", false)), List.of()));
        verify(query, never()).getResultList();
    }

    @Test
    void filtroGlobalEscapaPorcentajeEnCadaCampo() {
        configurarPagina();
        dao.findRange(0, 10, " 50%_ ");
        verify(cb).like(cb.lower(root.get("nombres").as(String.class)), "%50\\%\\_%", '\\');
        verify(cb).like(cb.lower(root.get("apellidos").as(String.class)), "%50\\%\\_%", '\\');
    }

    @Test
    void autocompletarUsaFiltroYLimitaCantidad() {
        PersonaDAO espia = spy(dao);
        doReturn(List.of()).when(espia).findRange(anyInt(), anyInt(), anyString());
        assertTrue(espia.buscarParaAutocompletar("a", 10).isEmpty());
        espia.buscarParaAutocompletar(" Ana ", 500);
        espia.buscarParaAutocompletar(" Ana ", 0);
        verify(espia).findRange(0, 50, "Ana");
        verify(espia).findRange(0, 1, "Ana");
        verifyNoInteractions(em);
    }

    @Test
    @SuppressWarnings("unchecked")
    void unicidadNormalizaYExcluyeRegistroEnEdicion() {
        CriteriaQuery<Long> total = mock(CriteriaQuery.class, RETURNS_SELF);
        TypedQuery<Long> countQuery = mock(TypedQuery.class);
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Long.class)).thenReturn(total);
        when(total.from(Persona.class)).thenReturn(root);
        when(em.createQuery(total)).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(1L);
        UUID excluir = UUID.randomUUID();
        assertTrue(dao.existePorCampo("nombres", " ANA ", excluir));
        verify(cb).equal(cb.lower(root.get("nombres")), "ana");
        verify(cb).notEqual(root.get("idPersona"), excluir);
        assertFalse(dao.existePorCampo("nombres", " ", null));
    }

    @Test
    void filtroColumnaRechazaModoDesconocido() {
        configurarPagina();
        assertThrows(IllegalArgumentException.class, () -> dao.findRange(0, 10, null, List.of(),
                List.of(new FiltroConsulta("nombres", "Ana", "CUSTOM"))));
    }
}

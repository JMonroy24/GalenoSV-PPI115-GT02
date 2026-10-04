package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConsultasClinicaDAOTest {
    @Test
    void sinClinicaNoSeConsultaLaBase() {
        EntityManager em = mock(EntityManager.class);
        assertTrue(new ConsultaDAO(em).findRangeFiltrado(0, 15, null, null, null).isEmpty());
        assertEquals(0, new ConsultaDAO(em).countFiltrado(null, null, null));
        assertTrue(new PersonaRolDAO(em).buscarPacientes("Ana", null, 20).isEmpty());
        assertNull(new PersonaRolDAO(em).findResponsable(null, UUID.randomUUID()));
        assertNull(new PersonaRolDAO(em).findResponsable(UUID.randomUUID(), null));
        assertNull(new ProcedimientoPasoDAO(em).findPasoInicial(null)); verifyNoInteractions(em);
    }

    @Test
    @SuppressWarnings("unchecked")
    void documentosSoloCompruebanDuplicadosDeLaMismaPersona() {
        EntityManager em = mock(EntityManager.class); TypedQuery<Long> query = mock(TypedQuery.class, RETURNS_SELF);
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(query); when(query.getSingleResult()).thenReturn(1L);
        DocumentoDAO dao = new DocumentoDAO(em); UUID persona = UUID.randomUUID(), tipo = UUID.randomUUID(), excluir = UUID.randomUUID();
        assertFalse(dao.existePersonaTipoValor(null, tipo, "123", null)); verifyNoInteractions(em);
        assertTrue(dao.existePersonaTipoValor(persona, tipo, " ABC ", excluir));
        ArgumentCaptor<String> jpql = ArgumentCaptor.forClass(String.class); verify(em).createQuery(jpql.capture(), eq(Long.class));
        assertTrue(jpql.getValue().contains("d.idPersona.idPersona = :persona"));
        verify(query).setParameter("persona", persona); verify(query).setParameter("valor", "abc"); verify(query).setParameter("excluir", excluir);
    }

    @Test
    @SuppressWarnings("unchecked")
    void listadoYConteoSoloEnlazanLosParametrosPresentes() {
        EntityManager em = mock(EntityManager.class);
        TypedQuery<Consulta> lista = mock(TypedQuery.class, RETURNS_SELF);
        TypedQuery<Long> conteo = mock(TypedQuery.class, RETURNS_SELF);
        when(em.createQuery(anyString(), eq(Consulta.class))).thenReturn(lista);
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(conteo);
        when(conteo.getSingleResult()).thenReturn(2L);
        ConsultaDAO dao = new ConsultaDAO(em); UUID clinica = UUID.randomUUID(); Date desde = new Date(100), hasta = new Date(200);
        dao.findRangeFiltrado(15, 10, clinica, desde, null);
        ArgumentCaptor<String> jpql = ArgumentCaptor.forClass(String.class);
        verify(em).createQuery(jpql.capture(), eq(Consulta.class));
        assertTrue(jpql.getValue().contains("c.fechaInicio >= :desde")); assertFalse(jpql.getValue().contains(":hasta"));
        assertTrue(jpql.getValue().contains("ORDER BY c.fechaInicio DESC, c.idConsulta"));
        verify(lista).setParameter("clinica", clinica); verify(lista).setParameter("desde", desde);
        verify(lista, never()).setParameter(eq("hasta"), any()); verify(lista).setFirstResult(15); verify(lista).setMaxResults(10);
        assertEquals(2, dao.countFiltrado(clinica, null, hasta));
        verify(em).createQuery(jpql.capture(), eq(Long.class));
        assertFalse(jpql.getValue().contains("FETCH")); assertFalse(jpql.getValue().contains(":desde")); assertTrue(jpql.getValue().contains(":hasta"));
        verify(conteo).setParameter("hasta", hasta); verify(conteo, never()).setParameter(eq("desde"), any());
        assertThrows(IllegalArgumentException.class, () -> dao.findRangeFiltrado(-1, 10, clinica, null, null));
        assertThrows(IllegalArgumentException.class, () -> dao.findRangeFiltrado(0, 0, clinica, null, null));
    }

    @Test
    @SuppressWarnings("unchecked")
    void pacientesAgrupanElTextoYFiltranClinicaYRolActivo() {
        EntityManager em = mock(EntityManager.class); TypedQuery<PersonaRol> query = mock(TypedQuery.class, RETURNS_SELF);
        when(em.createQuery(anyString(), eq(PersonaRol.class))).thenReturn(query);
        UUID clinica = UUID.randomUUID(); PersonaRolDAO dao = new PersonaRolDAO(em);
        assertTrue(dao.buscarPacientes("a", clinica, 20).isEmpty()); assertTrue(dao.buscarPacientes(null, clinica, 20).isEmpty());
        verifyNoInteractions(em); dao.buscarPacientes("50%_", clinica, 20);
        ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class); verify(em).createQuery(sql.capture(), eq(PersonaRol.class));
        assertTrue(sql.getValue().contains("c.idClinica = :clinica")); assertTrue(sql.getValue().contains("r.activo = true"));
        assertTrue(sql.getValue().contains("LOWER(r.nombre) LIKE :rolPaciente")); assertTrue(sql.getValue().contains(" AND (LOWER(p.nombres)"));
        verify(query).setParameter("clinica", clinica); verify(query).setParameter("rolPaciente", "%paciente%"); verify(query).setParameter("patron", "%50\\%\\_%"); verify(query).setMaxResults(20);
    }

    @Test
    @SuppressWarnings("unchecked")
    void pasoInicialNoEsDestinoYResponsablePerteneceAlRolYClinica() {
        EntityManager em = mock(EntityManager.class); TypedQuery<ProcedimientoPaso> pasos = mock(TypedQuery.class, RETURNS_SELF);
        TypedQuery<PersonaRol> personas = mock(TypedQuery.class, RETURNS_SELF);
        when(em.createQuery(anyString(), eq(ProcedimientoPaso.class))).thenReturn(pasos);
        when(em.createQuery(anyString(), eq(PersonaRol.class))).thenReturn(personas);
        UUID procedimiento = UUID.randomUUID(), clinica = UUID.randomUUID(), rol = UUID.randomUUID();
        ProcedimientoPaso inicio = new ProcedimientoPaso(UUID.randomUUID()); PersonaRol responsable = new PersonaRol(UUID.randomUUID());
        when(pasos.getResultList()).thenReturn(List.of(inicio)); when(personas.getResultList()).thenReturn(List.of(responsable));
        assertSame(inicio, new ProcedimientoPasoDAO(em).findPasoInicial(procedimiento));
        ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class); verify(em).createQuery(sql.capture(), eq(ProcedimientoPaso.class));
        assertTrue(sql.getValue().contains("NOT EXISTS")); assertTrue(sql.getValue().contains("s.idProcedimientoPasoReferencia = pp.idProcedimientoPaso"));
        verify(pasos).setParameter("idProc", procedimiento); verify(pasos).setMaxResults(1);
        assertSame(responsable, new PersonaRolDAO(em).findResponsable(clinica, rol));
        verify(personas).setParameter("clinica", clinica); verify(personas).setParameter("rol", rol); verify(personas).setMaxResults(1);
        when(pasos.getResultList()).thenReturn(List.of()); when(personas.getResultList()).thenReturn(List.of());
        assertNull(new ProcedimientoPasoDAO(em).findPasoInicial(procedimiento)); assertNull(new PersonaRolDAO(em).findResponsable(clinica, rol));
    }

    @Test
    void creacionesCompuestasPersistenElPadreAntesDelHijoEnLaMismaTransaccion() throws Exception {
        AsignacionService servicio = new AsignacionService(); servicio.em = mock(EntityManager.class);
        ConsultaProcedimiento cp = new ConsultaProcedimiento(UUID.randomUUID()); ConsultaProcedimientoPaso paso = new ConsultaProcedimientoPaso(UUID.randomUUID());
        servicio.crearProcedimientoConPaso(cp, paso); assertSame(cp, paso.getIdConsultaProcedimiento());
        var orden = inOrder(servicio.em); orden.verify(servicio.em).persist(cp); orden.verify(servicio.em).persist(paso); orden.verify(servicio.em).flush();
        Examen examen = new Examen(UUID.randomUUID()); ExamenTipoExamen tipo = new ExamenTipoExamen(UUID.randomUUID());
        servicio.crearExamenConTipo(examen, tipo); assertSame(examen, tipo.getIdExamen());
        orden.verify(servicio.em).persist(examen); orden.verify(servicio.em).persist(tipo); orden.verify(servicio.em).flush();
        assertNotNull(AsignacionService.class.getMethod("crearProcedimientoConPaso", ConsultaProcedimiento.class, ConsultaProcedimientoPaso.class)
                .getAnnotation(jakarta.transaction.Transactional.class));
        doThrow(new IllegalStateException("fallo del hijo")).when(servicio.em).persist(paso);
        assertThrows(IllegalStateException.class, () -> servicio.crearProcedimientoConPaso(cp, paso));
    }
}

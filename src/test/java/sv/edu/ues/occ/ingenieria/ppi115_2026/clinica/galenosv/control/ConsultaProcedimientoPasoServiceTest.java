package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error.PreconditionFailedException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaProcedimientoPasoServiceTest {
    @Mock ConsultaProcedimientoPasoDAO dao;
    @Mock EntityManager em;
    ConsultaProcedimientoPasoService service;

    @BeforeEach void iniciar() { service = new ConsultaProcedimientoPasoService(dao); }

    @SuppressWarnings("unchecked")
    private ConsultaProcedimientoPaso preparar() {
        var paso = FixturesClinicos.pasoValido();
        var query = mock(TypedQuery.class, RETURNS_SELF);
        lenient().when(dao.getEntityManager()).thenReturn(em);
        lenient().when(em.createNamedQuery("ProcedimientoPasoSecuencia.findByProcedimiento", ProcedimientoPasoSecuencia.class)).thenReturn(query);
        lenient().when(query.getResultList()).thenReturn(List.of());
        lenient().when(dao.findByIdParaFlujo(paso.getIdConsultaProcedimientoPaso())).thenReturn(paso);
        return paso;
    }

    @Test void crearConFechaFinAnteriorAInicioRechaza() {
        var paso = FixturesClinicos.pasoValido(); paso.setFechaFin(new Date(1));
        assertThrows(ValidacionNegocioException.class, () -> service.crear(paso));
        verify(dao, never()).create(any());
    }
    @Test void crearRechazaEstadoNuloVacioODesconocido() {
        var paso = FixturesClinicos.pasoValido();
        for (String estado : new String[]{null, "  ", "INVALIDO"}) {
            paso.setEstado(estado);
            assertThrows(ValidacionNegocioException.class, () -> service.crear(paso));
        }
        verify(dao, never()).create(any());
    }
    @Test void crearValidoNormalizaEstadoYConservaDefinicion() {
        var paso = preparar(); paso.setEstado(" en_curso ");
        service.crear(paso);
        assertEquals("EN_CURSO", paso.getEstado()); assertNotNull(paso.getIdProcedimientoPaso());
        verify(dao).bloquearConsulta(paso); verify(dao).create(paso);
    }
    @Test void crearRechazaDefinicionNulaODeOtroProcedimiento() {
        var paso = FixturesClinicos.pasoValido(); var definicion = paso.getIdProcedimientoPaso();
        paso.setIdProcedimientoPaso(null);
        assertThrows(ValidacionNegocioException.class, () -> service.crear(paso));
        paso.setIdProcedimientoPaso(definicion); definicion.setIdProcedimiento(new Procedimiento(UUID.randomUUID()));
        assertThrows(ValidacionNegocioException.class, () -> service.crear(paso)); verifyNoInteractions(dao);
    }
    @Test void crearRechazaRolYClinicaInactivosOAsignacionIncompatible() {
        var paso = FixturesClinicos.pasoValido(); var rol = paso.getIdPersonaRol().getIdRol(); var clinica = paso.getIdPersonaRol().getIdClinica();
        rol.setActivo(false); assertThrows(ValidacionNegocioException.class, () -> service.crear(paso));
        rol.setActivo(true); clinica.setActivo(false); assertThrows(ValidacionNegocioException.class, () -> service.crear(paso));
        clinica.setActivo(true); paso.getIdPersonaRol().setIdRol(new Rol(UUID.randomUUID()));
        assertThrows(ValidacionNegocioException.class, () -> service.crear(paso)); verifyNoInteractions(dao);
    }
    @Test void crearNoDuplicaDefinicionYaRegistrada() {
        var paso = preparar();
        when(dao.findRegistro(paso.getIdConsultaProcedimiento().getIdConsultaProcedimiento(), paso.getIdProcedimientoPaso().getIdProcedimientoPaso())).thenReturn(paso);
        assertThrows(ValidacionNegocioException.class, () -> service.crear(paso)); verify(dao, never()).create(any());
    }
    @Test void completarGuardaFechaYFinalizaConsultaConservandoFormulario() {
        var paso = preparar(); var inicio = new Date();
        String valor = "{\"detalle\":\"" + "a".repeat(6000) + "\"}";
        assertSame(paso, service.actualizarFormulario(paso.getIdConsultaProcedimientoPaso(), p -> true, " COMPLETADO ", valor));
        assertEquals("COMPLETADO", paso.getEstado()); assertEquals(valor, paso.getValor());
        assertFalse(paso.getFechaFin().before(inicio));
        assertEquals(paso.getFechaFin(), paso.getIdConsultaProcedimiento().getFechaFin());
        assertEquals(paso.getFechaFin(), paso.getIdConsultaProcedimiento().getIdConsulta().getFechaFin());
        verify(dao).update(paso);
    }
    @Test void guardarCompletadoConservaFechaFinInclusoEnPut() {
        var paso = preparar(); var fin = new Date(12345); paso.setEstado("COMPLETADO"); paso.setFechaFin(fin);
        service.actualizarSi(paso.getIdConsultaProcedimientoPaso(), p -> true, p -> { p.setFechaFin(new Date()); p.setValor("nuevo"); });
        assertEquals(fin, paso.getFechaFin()); assertEquals("nuevo", paso.getValor());
    }
    @Test void etagObsoletoNoModificaElPaso() {
        var paso = preparar(); paso.setValor("original");
        assertThrows(PreconditionFailedException.class, () -> service.actualizarFormulario(paso.getIdConsultaProcedimientoPaso(), p -> false, "COMPLETADO", "nuevo"));
        assertEquals("PENDIENTE", paso.getEstado()); assertEquals("original", paso.getValor());
        assertNull(paso.getFechaFin()); verify(dao, never()).update(any());
    }
    @Test void rechazaReabrirCompletadoSinAccionDeRetroceso() {
        var paso = preparar(); paso.setEstado("COMPLETADO"); paso.setFechaFin(new Date(500));
        assertThrows(ValidacionNegocioException.class, () -> service.actualizarFormulario(paso.getIdConsultaProcedimientoPaso(), p -> true, "EN_CURSO", ""));
        verify(dao, never()).update(any());
    }
    @Test void retrocesoPrimerPasoSeRechaza() {
        var paso = preparar();
        assertThrows(ValidacionNegocioException.class, () -> service.retroceder(paso.getIdConsultaProcedimientoPaso(), p -> true, ""));
    }
    @Test void rechazaOrdenesConExamenInactivoAlOperarPaso() {
        var paso = FixturesClinicos.pasoValido(); var examen = new Examen(UUID.randomUUID()); examen.setActivo(false);
        var orden = new OrdenExamen(UUID.randomUUID()); orden.setIdExamen(examen); paso.setOrdenExamenList(List.of(orden));
        assertThrows(ValidacionNegocioException.class, () -> service.crear(paso)); verifyNoInteractions(dao);
    }
}

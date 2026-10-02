package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AsignacionServiceTest {
    private AsignacionService servicio;

    @BeforeEach
    void preparar() {
        servicio = new AsignacionService();
        servicio.em = mock(EntityManager.class);
        servicio.examenTipoExamenDAO = mock(ExamenTipoExamenDAO.class);
        servicio.procedimientoPasoExamenDAO = mock(ProcedimientoPasoExamenDAO.class);
        servicio.procedimientoPasoSecuenciaDAO = mock(ProcedimientoPasoSecuenciaDAO.class);
    }

    @Test
    void detectaAutorreferenciaCicloDirectoYTransitivo() {
        UUID a = UUID.randomUUID(), b = UUID.randomUUID(), c = UUID.randomUUID();
        assertThrows(ValidacionNegocioException.class,
                () -> AsignacionService.validarSinCiclo(a, a, null, List.of()));
        assertThrows(ValidacionNegocioException.class,
                () -> AsignacionService.validarSinCiclo(a, b, null, List.of(arista(b, a))));
        assertThrows(ValidacionNegocioException.class,
                () -> AsignacionService.validarSinCiclo(c, a, null, List.of(arista(a, b), arista(b, c))));
    }

    @Test
    void permiteGrafoAciclicoYExcluyeAristaEditada() {
        UUID a = UUID.randomUUID(), b = UUID.randomUUID(), c = UUID.randomUUID();
        var anterior = arista(a, b);
        assertDoesNotThrow(() -> AsignacionService.validarSinCiclo(a, c, null, List.of(arista(a, b), arista(b, c))));
        assertDoesNotThrow(() -> AsignacionService.validarSinCiclo(b, a,
                anterior.getIdProcedimientoPasoSecuencia(), List.of(anterior)));
        // Una rama histórica cíclica ajena no provoca un bucle infinito en la validación.
        assertDoesNotThrow(() -> AsignacionService.validarSinCiclo(c, a, null, List.of(arista(a, b), arista(b, a))));
    }

    @Test
    void rechazaTipoDuplicadoDespuesDeBloquearElExamen() {
        Examen examen = new Examen(UUID.randomUUID());
        examen.setActivo(true);
        TipoExamen tipo = new TipoExamen(UUID.randomUUID());
        tipo.setActivo(true);
        ExamenTipoExamen registro = new ExamenTipoExamen(UUID.randomUUID());
        registro.setIdExamen(examen);
        registro.setIdTipoExamen(tipo);
        when(servicio.em.find(Examen.class, examen.getIdExamen(), LockModeType.PESSIMISTIC_WRITE)).thenReturn(examen);
        when(servicio.em.find(TipoExamen.class, tipo.getIdTipoExamen())).thenReturn(tipo);
        when(servicio.examenTipoExamenDAO.existeExamenTipo(examen.getIdExamen(), tipo.getIdTipoExamen(), null)).thenReturn(true);

        assertThrows(ValidacionNegocioException.class, () -> servicio.guardarTipo(registro, true));

        var orden = inOrder(servicio.em, servicio.examenTipoExamenDAO);
        orden.verify(servicio.em).find(Examen.class, examen.getIdExamen(), LockModeType.PESSIMISTIC_WRITE);
        orden.verify(servicio.examenTipoExamenDAO).existeExamenTipo(examen.getIdExamen(), tipo.getIdTipoExamen(), null);
        verify(servicio.examenTipoExamenDAO, never()).create(any());
    }

    @Test
    void altaYEdicionDeTipoCompartenValidacionYExclusion() {
        Examen examen = new Examen(UUID.randomUUID()); examen.setActivo(true);
        TipoExamen tipo = new TipoExamen(UUID.randomUUID()); tipo.setActivo(true);
        ExamenTipoExamen registro = new ExamenTipoExamen(UUID.randomUUID());
        registro.setIdExamen(examen); registro.setIdTipoExamen(tipo);
        when(servicio.em.find(Examen.class, examen.getIdExamen(), LockModeType.PESSIMISTIC_WRITE)).thenReturn(examen);
        when(servicio.em.find(TipoExamen.class, tipo.getIdTipoExamen())).thenReturn(tipo);
        servicio.guardarTipo(registro, false);
        verify(servicio.examenTipoExamenDAO).existeExamenTipo(examen.getIdExamen(), tipo.getIdTipoExamen(), registro.getIdExamenTipoExamen());
        verify(servicio.examenTipoExamenDAO).update(registro);
        verify(servicio.em).flush();
        tipo.setActivo(false);
        assertThrows(ValidacionNegocioException.class, () -> servicio.guardarTipo(registro, true));
        verify(servicio.examenTipoExamenDAO, never()).create(any());
    }

    @Test
    void examenInactivoODuplicadoNoSeAsigna() {
        ProcedimientoPaso paso = new ProcedimientoPaso(UUID.randomUUID());
        Examen examen = new Examen(UUID.randomUUID()); examen.setActivo(false);
        ProcedimientoPasoExamen registro = new ProcedimientoPasoExamen(UUID.randomUUID());
        registro.setIdProcedimientoPaso(paso); registro.setIdExamen(examen);
        when(servicio.em.find(ProcedimientoPaso.class, paso.getIdProcedimientoPaso(), LockModeType.PESSIMISTIC_WRITE)).thenReturn(paso);
        when(servicio.em.find(Examen.class, examen.getIdExamen())).thenReturn(examen);
        assertThrows(ValidacionNegocioException.class, () -> servicio.guardarExamen(registro, true));
        examen.setActivo(true);
        when(servicio.procedimientoPasoExamenDAO.existePasoExamen(paso.getIdProcedimientoPaso(), examen.getIdExamen(), null)).thenReturn(true);
        assertThrows(ValidacionNegocioException.class, () -> servicio.guardarExamen(registro, true));
        verify(servicio.procedimientoPasoExamenDAO, never()).create(any());
    }

    @Test
    void servicioSecuenciaBloqueaProcedimientoYRechazaCicloDeBD() {
        Procedimiento procedimiento = new Procedimiento(UUID.randomUUID());
        ProcedimientoPaso a = new ProcedimientoPaso(UUID.randomUUID()), b = new ProcedimientoPaso(UUID.randomUUID());
        a.setIdProcedimiento(procedimiento); b.setIdProcedimiento(procedimiento);
        var registro = arista(a.getIdProcedimientoPaso(), b.getIdProcedimientoPaso());
        when(servicio.em.find(ProcedimientoPaso.class, a.getIdProcedimientoPaso())).thenReturn(a);
        when(servicio.em.find(ProcedimientoPaso.class, b.getIdProcedimientoPaso())).thenReturn(b);
        when(servicio.em.find(Procedimiento.class, procedimiento.getIdProcedimiento(), LockModeType.PESSIMISTIC_WRITE)).thenReturn(procedimiento);
        when(servicio.procedimientoPasoSecuenciaDAO.findByProcedimiento(procedimiento.getIdProcedimiento()))
                .thenReturn(List.of(arista(b.getIdProcedimientoPaso(), a.getIdProcedimientoPaso())));

        assertThrows(ValidacionNegocioException.class, () -> servicio.guardarSecuencia(registro, true));
        verify(servicio.procedimientoPasoSecuenciaDAO, never()).create(any());
        var orden = inOrder(servicio.em, servicio.procedimientoPasoSecuenciaDAO);
        orden.verify(servicio.em).find(Procedimiento.class, procedimiento.getIdProcedimiento(), LockModeType.PESSIMISTIC_WRITE);
        orden.verify(servicio.procedimientoPasoSecuenciaDAO).findByProcedimiento(procedimiento.getIdProcedimiento());

        b.setIdProcedimiento(new Procedimiento(UUID.randomUUID()));
        assertThrows(ValidacionNegocioException.class, () -> servicio.guardarSecuencia(registro, false));
        verify(servicio.procedimientoPasoSecuenciaDAO, never()).update(any());
    }

    private static ProcedimientoPasoSecuencia arista(UUID a, UUID b) {
        var arista = new ProcedimientoPasoSecuencia(UUID.randomUUID());
        arista.setIdProcedimientoPaso(new ProcedimientoPaso(a));
        arista.setIdProcedimientoPasoReferencia(b);
        arista.setTipoSecuencia("Siguiente");
        return arista;
    }
}

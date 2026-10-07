package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcedimientoPasoModelTest {
    @Mock ProcedimientoPasoDAO procedimientoPasoDAO;
    @Mock ProcedimientoPasoExamenDAO procedimientoPasoExamenDAO;
    @Mock ProcedimientoPasoSecuenciaDAO procedimientoPasoSecuenciaDAO;
    @Mock AsignacionService asignacionService;
    @Mock RolDAO rolDAO;
    @Mock ExamenDAO examenDAO;
    @InjectMocks ProcedimientoPasoModel model;
    Procedimiento procedimiento;
    Rol rol;
    @BeforeEach void preparar() {
        procedimiento = new Procedimiento(UUID.randomUUID());
        rol = new Rol(UUID.randomUUID()); rol.setActivo(true);
        model.abrirProcedimiento(procedimiento);
        model.getRegistroActual().setNombre("Recepción");
        model.getRegistroActual().setIdRol(rol);
        lenient().when(rolDAO.findById(rol.getIdRol())).thenReturn(rol);
    }
    ProcedimientoPaso padre() {
        ProcedimientoPaso paso = new ProcedimientoPaso(UUID.randomUUID());
        paso.setIdProcedimiento(procedimiento); paso.setNombre("Padre");
        paso.setIdRol(new Rol(UUID.randomUUID())); paso.setIndicaFin(false);
        return paso;
    }
    ProcedimientoPasoSecuencia relacion(ProcedimientoPaso padre, ProcedimientoPaso hijo) {
        ProcedimientoPasoSecuencia s = new ProcedimientoPasoSecuencia(UUID.randomUUID());
        s.setIdProcedimientoPaso(padre); s.setIdProcedimientoPasoReferencia(hijo.getIdProcedimientoPaso());
        s.setTipoSecuencia("SIGUIENTE"); return s;
    }
    @Test void primerPasoSinDependencia() {
        assertTrue(model.guardarPaso());
        verify(asignacionService).guardarPaso(any(), eq(true), isNull(), eq(List.of()));
        assertEquals(Estado.CREAR, model.getEstado());
        assertEquals(procedimiento, model.getRegistroActual().getIdProcedimiento());
    }
    @Test void primerPasoRechazaDependencia() {
        model.setDependeDe(padre()); assertFalse(model.guardarPaso());
        verifyNoInteractions(asignacionService);
    }
    @Test void siguientesExigenPadreDelMismoProcedimiento() {
        ProcedimientoPaso padre = padre();
        when(procedimientoPasoDAO.findByProcedimiento(procedimiento.getIdProcedimiento())).thenReturn(List.of(padre));
        assertFalse(model.guardarPaso());
        model.setDependeDe(new ProcedimientoPaso(UUID.randomUUID())); assertFalse(model.guardarPaso());
        model.setDependeDe(padre); assertTrue(model.guardarPaso());
        verify(asignacionService).guardarPaso(any(), eq(true), eq(padre), anyList());
    }
    @Test void rechazaRolInactivoPeroPermiteRepetirRolEnPasos() {
        rol.setActivo(false); assertFalse(model.guardarPaso()); rol.setActivo(true);
        ProcedimientoPaso padre = padre(); padre.setIdRol(rol);
        when(procedimientoPasoDAO.findByProcedimiento(procedimiento.getIdProcedimiento())).thenReturn(List.of(padre));
        model.setDependeDe(padre); assertTrue(model.guardarPaso());
        verify(asignacionService).guardarPaso(any(), eq(true), eq(padre), anyList());
    }
    @Test void noAgregarHijoAPasoDeFin() {
        ProcedimientoPaso padre = padre(); padre.setIndicaFin(true);
        when(procedimientoPasoDAO.findByProcedimiento(procedimiento.getIdProcedimiento())).thenReturn(List.of(padre));
        model.setDependeDe(padre); assertFalse(model.guardarPaso());
    }
    @Test void noMarcarFinSiTieneHijos() {
        ProcedimientoPaso paso = model.getRegistroActual(); paso.setIndicaFin(true);
        when(procedimientoPasoSecuenciaDAO.findByProcedimiento(procedimiento.getIdProcedimiento()))
                .thenReturn(List.of(relacion(paso, padre())));
        assertFalse(model.guardarPaso());
    }
    @Test void noCrearCiclo() {
        ProcedimientoPaso padre = padre();
        when(procedimientoPasoDAO.findByProcedimiento(procedimiento.getIdProcedimiento())).thenReturn(List.of(padre));
        when(procedimientoPasoSecuenciaDAO.findByProcedimiento(procedimiento.getIdProcedimiento()))
                .thenReturn(List.of(relacion(model.getRegistroActual(), padre)));
        model.setDependeDe(padre); assertFalse(model.guardarPaso());
    }
    @Test void examenesEnMemoriaRechazanInactivosYDuplicados() {
        Examen examen = new Examen(UUID.randomUUID()); examen.setActivo(false);
        model.setExamenSeleccionado(examen); model.agregarExamen(); assertTrue(model.getExamenesAsignados().isEmpty());
        examen.setActivo(true); model.setObservacionesExamen("Antes del paso"); model.agregarExamen();
        model.setExamenSeleccionado(examen); model.agregarExamen();
        assertEquals(1, model.getExamenesAsignados().size());
        assertEquals("Antes del paso", model.getExamenesAsignados().getFirst().getObservaciones());
        verifyNoInteractions(asignacionService, procedimientoPasoExamenDAO);
        when(examenDAO.findById(examen.getIdExamen())).thenReturn(examen);
        assertTrue(model.guardarPaso());
        verify(asignacionService).guardarPaso(any(), eq(true), isNull(), argThat(l -> l.size() == 1));
    }
    @Test void cancelarDescartaExamenesSinPersistir() {
        Examen examen = new Examen(UUID.randomUUID()); examen.setActivo(true);
        model.setExamenSeleccionado(examen); model.agregarExamen(); model.cancelar();
        assertTrue(model.getExamenesAsignados().isEmpty());
        assertEquals(procedimiento, model.getRegistroActual().getIdProcedimiento());
        verifyNoInteractions(asignacionService, procedimientoPasoExamenDAO);
    }
    @Test void quitarExamenEnEdicionSoloSeGuardaAlConfirmar() {
        ProcedimientoPaso paso = model.getRegistroActual();
        ProcedimientoPasoExamen examen = new ProcedimientoPasoExamen(UUID.randomUUID());
        when(procedimientoPasoExamenDAO.findByPaso(paso.getIdProcedimientoPaso())).thenReturn(List.of(examen));
        model.seleccionar(paso);
        model.setExamenAsignadoSeleccionado(examen); model.quitarExamenSeleccionado();
        verify(procedimientoPasoExamenDAO, never()).delete(any());
        assertTrue(model.guardarPaso());
        verify(asignacionService).guardarPaso(any(), eq(false), isNull(), eq(List.of()));
    }
    @Test void edicionNoCambiaRolNiDependenciaYCancelarNoModificaOriginal() {
        ProcedimientoPaso paso = model.getRegistroActual(); model.seleccionar(paso);
        model.getRegistroActual().setNombre("Otro"); model.cancelar(); assertEquals("Recepción", paso.getNombre());
        model.seleccionar(paso); model.setDependeDe(padre()); assertFalse(model.guardarPaso());
    }
    @Test void noEliminarPasoConHijos() {
        ProcedimientoPaso paso = model.getRegistroActual();
        when(procedimientoPasoSecuenciaDAO.findByProcedimiento(procedimiento.getIdProcedimiento()))
                .thenReturn(List.of(relacion(paso, padre())));
        assertFalse(model.eliminarPaso(paso)); verifyNoInteractions(asignacionService);
    }
    @Test void errorDePersistenciaConservaBorrador() {
        doThrow(new IllegalStateException("fallo")).when(asignacionService).guardarPaso(any(), anyBoolean(), any(), anyList());
        ProcedimientoPaso borrador = model.getRegistroActual();
        assertFalse(model.guardarPaso()); assertSame(borrador, model.getRegistroActual());
    }
    @Test void mismoRolPuedeUsarseEnDosProcedimientosDiferentes() {
        assertTrue(model.guardarPaso());
        Procedimiento otro = new Procedimiento(UUID.randomUUID()); otro.setActivo(true);
        model.abrirProcedimiento(otro);
        model.getRegistroActual().setNombre("Inicio del otro procedimiento");
        model.getRegistroActual().setIdRol(rol);
        assertTrue(model.guardarPaso());
        verify(asignacionService).guardarPaso(argThat(p -> procedimiento.equals(p.getIdProcedimiento())
                && rol.equals(p.getIdRol())), eq(true), isNull(), anyList());
        verify(asignacionService).guardarPaso(argThat(p -> otro.equals(p.getIdProcedimiento())
                && rol.equals(p.getIdRol())), eq(true), isNull(), anyList());
    }
}

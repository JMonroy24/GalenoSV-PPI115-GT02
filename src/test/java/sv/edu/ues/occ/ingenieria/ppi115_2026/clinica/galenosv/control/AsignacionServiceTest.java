package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.persistence.EntityManager;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsignacionServiceTest {
    @Mock ProcedimientoPasoDAO pasoDAO;
    @Mock ProcedimientoPasoExamenDAO examenDAO;
    @Mock ProcedimientoPasoSecuenciaDAO secuenciaDAO;
    @Mock EntityManager em;
    @InjectMocks AsignacionService service;
    @Test void creaPasoConConvencionPadreHijoYExamenes() {
        var padre = new ProcedimientoPaso(UUID.randomUUID());
        var hijo = new ProcedimientoPaso(UUID.randomUUID());
        var examen = new ProcedimientoPasoExamen(UUID.randomUUID());
        service.guardarPaso(hijo, true, padre, List.of(examen));
        var captor = ArgumentCaptor.forClass(ProcedimientoPasoSecuencia.class);
        verify(secuenciaDAO).create(captor.capture());
        assertEquals(padre, captor.getValue().getIdProcedimientoPaso());
        assertEquals(hijo.getIdProcedimientoPaso(), captor.getValue().getIdProcedimientoPasoReferencia());
        assertEquals("SIGUIENTE", captor.getValue().getTipoSecuencia());
        var orden = inOrder(pasoDAO, secuenciaDAO, examenDAO, em);
        orden.verify(pasoDAO).create(hijo); orden.verify(secuenciaDAO).create(any());
        orden.verify(examenDAO).update(examen); orden.verify(em).flush();
    }
    @Test void edicionEliminaSoloExamenesRetiradosSinCambiarDependencia() {
        var paso = new ProcedimientoPaso(UUID.randomUUID());
        var conservado = new ProcedimientoPasoExamen(UUID.randomUUID());
        var eliminado = new ProcedimientoPasoExamen(UUID.randomUUID());
        when(examenDAO.findByPaso(paso.getIdProcedimientoPaso())).thenReturn(List.of(conservado, eliminado));
        service.guardarPaso(paso, false, null, List.of(conservado));
        verify(examenDAO).delete(eliminado); verify(examenDAO, never()).delete(conservado);
        verify(examenDAO).update(conservado); verifyNoInteractions(secuenciaDAO);
    }
    @Test void borrarHojaRetiraDependenciaYExamenesAntesDelPaso() {
        var procedimiento = new Procedimiento(UUID.randomUUID());
        var padre = new ProcedimientoPaso(UUID.randomUUID());
        var hoja = new ProcedimientoPaso(UUID.randomUUID()); hoja.setIdProcedimiento(procedimiento);
        var relacion = new ProcedimientoPasoSecuencia(UUID.randomUUID());
        relacion.setIdProcedimientoPaso(padre); relacion.setIdProcedimientoPasoReferencia(hoja.getIdProcedimientoPaso());
        relacion.setTipoSecuencia("SIGUIENTE");
        var examen = new ProcedimientoPasoExamen(UUID.randomUUID());
        when(secuenciaDAO.findByProcedimiento(procedimiento.getIdProcedimiento())).thenReturn(List.of(relacion));
        when(examenDAO.findByPaso(hoja.getIdProcedimientoPaso())).thenReturn(List.of(examen));
        service.eliminarPaso(hoja);
        var orden = inOrder(secuenciaDAO, examenDAO, pasoDAO);
        orden.verify(secuenciaDAO).delete(relacion); orden.verify(examenDAO).delete(examen);
        orden.verify(pasoDAO).delete(hoja);
    }
    @Test void falloSePropagaParaRevertirLaTransaccion() {
        var paso = new ProcedimientoPaso(UUID.randomUUID());
        doThrow(new IllegalStateException("fallo al confirmar")).when(em).flush();
        assertThrows(IllegalStateException.class, () -> service.guardarPaso(paso, true, null, List.of()));
    }
}

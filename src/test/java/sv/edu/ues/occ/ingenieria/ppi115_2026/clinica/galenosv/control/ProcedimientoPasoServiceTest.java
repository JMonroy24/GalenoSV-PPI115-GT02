package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPaso;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProcedimientoPasoServiceTest {

    @Test
    void delegaCreacionActualizacionCondicionalYEliminacionSinListaDeExamenes() {
        var dao = mock(ProcedimientoPasoDAO.class);
        var asignaciones = mock(AsignacionService.class);
        var servicio = new ProcedimientoPasoService(dao, asignaciones);
        var paso = new ProcedimientoPaso(UUID.randomUUID());
        servicio.crear(paso);
        verify(asignaciones).guardarPaso(paso, true, null);
        when(dao.findByIdParaActualizar(paso.getIdProcedimientoPaso())).thenReturn(paso);
        assertSame(paso, servicio.actualizarSi(paso.getIdProcedimientoPaso(),
                actual -> true, actual -> actual.setNombre("Paso editado")));
        assertEquals("Paso editado", paso.getNombre());
        verify(asignaciones).guardarPaso(paso, false, null);

        doThrow(new ValidacionNegocioException("No se puede eliminar un paso con hijos."))
                .when(asignaciones).eliminarPaso(paso);
        assertThrows(ValidacionNegocioException.class, () -> servicio.eliminar(paso));
        verify(dao, never()).update(any());
        verify(dao, never()).delete(any());
    }
}

package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimientoPaso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ConsultaProcedimientoPasoServiceTest {

    @Test
    void rechazaFechasInvertidasYEstadoVacioAntesDeActualizar() {
        var dao = mock(ConsultaProcedimientoPasoDAO.class);
        var servicio = new ConsultaProcedimientoPasoService(dao);
        var paso = new ConsultaProcedimientoPaso(UUID.randomUUID());
        paso.setFechaInicio(new Date(100));
        paso.setFechaFin(new Date(99));
        paso.setEstado("Pendiente");
        assertThrows(ValidacionNegocioException.class, () -> servicio.actualizar(paso));
        paso.setFechaFin(new Date(100));
        paso.setEstado(" ");
        assertThrows(ValidacionNegocioException.class, () -> servicio.actualizar(paso));
        verifyNoInteractions(dao);

        paso.setEstado(" Pendiente ");
        servicio.actualizar(paso);
        assertEquals("Pendiente", paso.getEstado());
        verify(dao).update(paso);
    }
}

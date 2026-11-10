package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimiento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaProcedimientoServiceTest {

    @Mock
    private ConsultaProcedimientoDAO consultaProcedimientoDAO;

    private ConsultaProcedimientoService service;

    @BeforeEach
    void setUp() {
        service = new ConsultaProcedimientoService(consultaProcedimientoDAO);
    }

    private Date hace(int dias) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, dias);
        return cal.getTime();
    }

    @Test
    void crearConFechaFinAnteriorAFechaInicioLanzaValidacionNegocioException() {
        ConsultaProcedimiento cp = new ConsultaProcedimiento(UUID.randomUUID());
        cp.setFechaInicio(hace(0));
        cp.setFechaFin(hace(-1)); // fin antes que inicio
        // cp.setEstado("EN_CURSO"); // removed - no estado field

        assertThrows(ValidacionNegocioException.class, () -> service.crear(cp));
        verify(consultaProcedimientoDAO, never()).create(any());
    }

    @Test
    void crearConPeriodoFueraDelRangoDeConsultaLanzaValidacionNegocioException() {
        // Simular consulta dentro de entidad cp (need a mock Consulta)
        // Here we just set idConsulta with dates that cause fuera del periodo
        // Assuming ConsultaProcedimientoService.validar will call dentroDelPeriodo when idConsulta != null.
        // We'll create a dummy Consulta with start/end dates.
        var consulta = mock(sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Consulta.class);
        Date consultaInicio = hace(-10);
        Date consultaFin = hace(10);
        when(consulta.getFechaInicio()).thenReturn(consultaInicio);
        when(consulta.getFechaFin()).thenReturn(consultaFin);
        ConsultaProcedimiento cp = new ConsultaProcedimiento(UUID.randomUUID());
        cp.setFechaInicio(hace(5)); // dentro de rango de consulta
        cp.setFechaFin(hace(15)); // fin after consultaFin => fuera del periodo
        cp.setIdConsulta(consulta);
        // cp.setEstado("EN_CURSO"); // removed - no estado field

        assertThrows(ValidacionNegocioException.class, () -> service.crear(cp));
        verify(consultaProcedimientoDAO, never()).create(any());
    }

    @Test
    void crearConProcedimientoInactivoLanzaValidacionNegocioException() {
        var procedimiento = mock(sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Procedimiento.class);
        when(procedimiento.getActivo()).thenReturn(false);
        ConsultaProcedimiento cp = new ConsultaProcedimiento(UUID.randomUUID());
        cp.setFechaInicio(hace(0));
        // cp.setEstado("EN_CURSO"); // removed - no estado field
        cp.setIdProcedimiento(procedimiento);

        assertThrows(ValidacionNegocioException.class, () -> service.crear(cp));
        verify(consultaProcedimientoDAO, never()).create(any());
    }

    @Test
    void crearValidoLlamaDAOCreate() {
        ConsultaProcedimiento cp = new ConsultaProcedimiento(UUID.randomUUID());
        cp.setFechaInicio(hace(0));
        // cp.setEstado(" EN_CURSO "); // removed - no estado field
        // No consulta ni procedimiento, valid passes
        service.crear(cp);
        verify(consultaProcedimientoDAO).create(cp);
    }
}

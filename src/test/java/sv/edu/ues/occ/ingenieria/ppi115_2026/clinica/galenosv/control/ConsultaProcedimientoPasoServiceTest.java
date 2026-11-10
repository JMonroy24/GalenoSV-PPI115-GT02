package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimientoPaso;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaProcedimientoPasoServiceTest {

    @Mock
    private ConsultaProcedimientoPasoDAO consultaProcedimientoPasoDAO;

    private ConsultaProcedimientoPasoService service;

    @BeforeEach
    void setUp() {
        service = new ConsultaProcedimientoPasoService(consultaProcedimientoPasoDAO);
    }

    private Date hace(int dias) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, dias);
        return cal.getTime();
    }

    @Test
    void crearConFechaFinAnteriorAFechaInicioLanzaValidacionNegocioException() {
        ConsultaProcedimientoPaso cpp = new ConsultaProcedimientoPaso(UUID.randomUUID());
        cpp.setFechaInicio(hace(0));
        cpp.setFechaFin(hace(-1)); // fin antes que inicio
        cpp.setEstado("EN_CURSO");

        assertThrows(ValidacionNegocioException.class, () -> service.crear(cpp));
        verify(consultaProcedimientoPasoDAO, never()).create(any());
    }

    @Test
    void crearConEstadoNullLanzaValidacionNegocioException() {
        ConsultaProcedimientoPaso cpp = new ConsultaProcedimientoPaso(UUID.randomUUID());
        cpp.setFechaInicio(hace(0));
        cpp.setEstado(null);

        assertThrows(ValidacionNegocioException.class, () -> service.crear(cpp));
        verify(consultaProcedimientoPasoDAO, never()).create(any());
    }

    @Test
    void crearConEstadoEnBlancoLanzaValidacionNegocioException() {
        ConsultaProcedimientoPaso cpp = new ConsultaProcedimientoPaso(UUID.randomUUID());
        cpp.setFechaInicio(hace(0));
        cpp.setEstado("   ");

        assertThrows(ValidacionNegocioException.class, () -> service.crear(cpp));
        verify(consultaProcedimientoPasoDAO, never()).create(any());
    }

    @Test
    void crearValidoLlamaDAOCreateYRecortaEstado() {
        ConsultaProcedimientoPaso cpp = new ConsultaProcedimientoPaso(UUID.randomUUID());
        cpp.setFechaInicio(hace(0));
        cpp.setEstado(" EN_CURSO ");

        service.crear(cpp);

        assertEquals("EN_CURSO", cpp.getEstado());
        verify(consultaProcedimientoPasoDAO).create(cpp);
    }
}

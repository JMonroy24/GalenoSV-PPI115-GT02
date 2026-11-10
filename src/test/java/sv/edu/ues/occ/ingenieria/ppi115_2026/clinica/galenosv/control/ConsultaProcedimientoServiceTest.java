package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Procedimiento;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ConsultaProcedimientoServiceTest {

    @Test
    void rechazaFechasInvertidasFueraDeConsultaYProcedimientoInactivo() {
        var dao = mock(ConsultaProcedimientoDAO.class);
        var servicio = new ConsultaProcedimientoService(dao);
        var consulta = new Consulta(UUID.randomUUID());
        consulta.setFechaInicio(new Date(100));
        consulta.setFechaFin(new Date(300));
        var registro = new ConsultaProcedimiento(UUID.randomUUID());
        registro.setIdConsulta(consulta);
        registro.setFechaInicio(new Date(200));
        registro.setFechaFin(new Date(199));
        assertThrows(ValidacionNegocioException.class, () -> servicio.crear(registro));
        registro.setFechaFin(new Date(301));
        assertThrows(ValidacionNegocioException.class, () -> servicio.crear(registro));
        registro.setFechaFin(new Date(300));
        var procedimiento = new Procedimiento(UUID.randomUUID());
        procedimiento.setActivo(false);
        registro.setIdProcedimiento(procedimiento);
        assertThrows(ValidacionNegocioException.class, () -> servicio.crear(registro));
        verifyNoInteractions(dao);

        procedimiento.setActivo(true);
        servicio.crear(registro);
        verify(dao).create(registro);
    }
}

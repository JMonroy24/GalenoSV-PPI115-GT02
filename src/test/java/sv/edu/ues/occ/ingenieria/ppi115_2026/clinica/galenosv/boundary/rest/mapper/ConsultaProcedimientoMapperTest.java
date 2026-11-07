package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.ws.rs.NotFoundException;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ConsultaProcedimientoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Procedimiento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaProcedimientoMapperTest {

    @Mock
    ConsultaService consultaService;

    @Mock
    ProcedimientoService procedimientoService;

    @InjectMocks
    ConsultaProcedimientoMapper mapper;

    @Test
    void idaYVueltaConservaCamposYResuelveRelaciones() {
        UUID id = UUID.randomUUID();
        Date inicio = new Date(1700000000000L);
        Date fin = new Date(1700003600000L);
        Consulta consulta = new Consulta(UUID.randomUUID());
        Procedimiento procedimiento = new Procedimiento(UUID.randomUUID());
        ConsultaProcedimiento entidad = new ConsultaProcedimiento(id);
        entidad.setFechaInicio(inicio);
        entidad.setFechaFin(fin);
        entidad.setObservaciones("Procedimiento aplicado");
        entidad.setIdConsulta(consulta);
        entidad.setIdProcedimiento(procedimiento);

        ConsultaProcedimientoDTO dto = mapper.toDTO(entidad);
        assertEquals(id, dto.id());
        assertEquals(inicio, dto.fechaInicio());
        assertEquals(fin, dto.fechaFin());
        assertEquals("Procedimiento aplicado", dto.observaciones());
        assertEquals(consulta.getIdConsulta(), dto.idConsulta());
        assertEquals(procedimiento.getIdProcedimiento(), dto.idProcedimiento());

        when(consultaService.findById(consulta.getIdConsulta())).thenReturn(consulta);
        when(procedimientoService.findById(procedimiento.getIdProcedimiento())).thenReturn(procedimiento);
        ConsultaProcedimiento vuelta = mapper.toEntity(dto);

        assertEquals(id, vuelta.getIdConsultaProcedimiento());
        assertEquals(dto.fechaInicio(), vuelta.getFechaInicio());
        assertEquals(dto.fechaFin(), vuelta.getFechaFin());
        assertEquals(dto.observaciones(), vuelta.getObservaciones());
        assertSame(consulta, vuelta.getIdConsulta());
        assertSame(procedimiento, vuelta.getIdProcedimiento());
    }

    @Test
    void copiarNoCambiaElIdDelDestino() {
        UUID idDestino = UUID.randomUUID();
        ConsultaProcedimiento destino = new ConsultaProcedimiento(idDestino);
        Consulta consulta = new Consulta(UUID.randomUUID());
        Procedimiento procedimiento = new Procedimiento(UUID.randomUUID());
        ConsultaProcedimientoDTO dto = new ConsultaProcedimientoDTO(UUID.randomUUID(), null, null,
                "Nuevas observaciones", consulta.getIdConsulta(), procedimiento.getIdProcedimiento());
        when(consultaService.findById(consulta.getIdConsulta())).thenReturn(consulta);
        when(procedimientoService.findById(procedimiento.getIdProcedimiento())).thenReturn(procedimiento);

        mapper.copiar(dto, destino);

        assertEquals(idDestino, destino.getIdConsultaProcedimiento());
        assertEquals("Nuevas observaciones", destino.getObservaciones());
        assertSame(consulta, destino.getIdConsulta());
        assertSame(procedimiento, destino.getIdProcedimiento());
    }

    @Test
    void relacionInexistenteLanzaNotFound() {
        Consulta consulta = new Consulta(UUID.randomUUID());
        UUID idProcedimiento = UUID.randomUUID();
        ConsultaProcedimientoDTO dto = new ConsultaProcedimientoDTO(null, null, null, null,
                consulta.getIdConsulta(), idProcedimiento);
        when(consultaService.findById(consulta.getIdConsulta())).thenReturn(consulta);
        when(procedimientoService.findById(idProcedimiento)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> mapper.toEntity(dto));
    }
}

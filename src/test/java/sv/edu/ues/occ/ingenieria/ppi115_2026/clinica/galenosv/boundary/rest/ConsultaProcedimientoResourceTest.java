package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ConsultaProcedimientoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ConsultaProcedimientoMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimiento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaProcedimientoResourceTest {

    @Mock
    ConsultaProcedimientoService consultaProcedimientoService;

    @Mock
    ConsultaProcedimientoMapper consultaProcedimientoMapper;

    @InjectMocks
    ConsultaProcedimientoResource consultaProcedimientoResource;

    @Test
    void getListaDevuelveTotalYLimitaMaxA100() {
        ConsultaProcedimiento entidad = new ConsultaProcedimiento(UUID.randomUUID());
        ConsultaProcedimientoDTO dto = new ConsultaProcedimientoDTO(entidad.getIdConsultaProcedimiento(),
                null, null, null, UUID.randomUUID(), UUID.randomUUID());
        when(consultaProcedimientoService.findRange(0, 100, null)).thenReturn(List.of(entidad));
        when(consultaProcedimientoService.count(null)).thenReturn(1L);
        when(consultaProcedimientoMapper.toDTO(entidad)).thenReturn(dto);

        PageResponse<ConsultaProcedimientoDTO> respuesta = consultaProcedimientoResource.listar(0, 1000, null);

        assertEquals(1, respuesta.items().size());
        assertEquals(1L, respuesta.total());
        assertEquals(100, respuesta.max());
        verify(consultaProcedimientoService).findRange(0, 100, null);
        verify(consultaProcedimientoService).count(null);
    }

    @Test
    void getPorIdInexistenteLanzaNotFound() {
        UUID id = UUID.randomUUID();
        when(consultaProcedimientoService.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> consultaProcedimientoResource.obtener(id.toString()));
    }

    @Test
    void postDevuelve201YHeaderLocation() {
        ConsultaProcedimientoDTO dto = new ConsultaProcedimientoDTO(null, null, null, null,
                UUID.randomUUID(), UUID.randomUUID());
        ConsultaProcedimiento entidad = new ConsultaProcedimiento();
        ConsultaProcedimientoDTO creada = new ConsultaProcedimientoDTO(UUID.randomUUID(),
                dto.fechaInicio(), dto.fechaFin(), dto.observaciones(), dto.idConsulta(), dto.idProcedimiento());
        when(consultaProcedimientoMapper.toEntity(dto)).thenReturn(entidad);
        when(consultaProcedimientoMapper.toDTO(entidad)).thenReturn(creada);

        Response respuesta = consultaProcedimientoResource.crear(dto);

        assertEquals(201, respuesta.getStatus());
        assertNotNull(respuesta.getHeaderString("Location"));
        assertTrue(respuesta.getHeaderString("Location").contains(creada.id().toString()));
        verify(consultaProcedimientoService).crear(entidad);
    }

    @Test
    void putExistenteCopiaYActualiza() {
        UUID id = UUID.randomUUID();
        ConsultaProcedimiento entidad = new ConsultaProcedimiento(id);
        ConsultaProcedimientoDTO dto = new ConsultaProcedimientoDTO(null, null, null, null,
                UUID.randomUUID(), UUID.randomUUID());
        ConsultaProcedimientoDTO esperada = new ConsultaProcedimientoDTO(id,
                dto.fechaInicio(), dto.fechaFin(), dto.observaciones(), dto.idConsulta(), dto.idProcedimiento());
        when(consultaProcedimientoService.findById(id)).thenReturn(entidad);
        when(consultaProcedimientoMapper.toDTO(entidad)).thenReturn(esperada);

        ConsultaProcedimientoDTO respuesta = consultaProcedimientoResource.actualizar(id.toString(), sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcular(esperada), dto);

        assertEquals(esperada, respuesta);
        verify(consultaProcedimientoMapper).copiar(dto, entidad);
        verify(consultaProcedimientoService).actualizar(entidad);
    }

    @Test
    void deleteDevuelve204() {
        UUID id = UUID.randomUUID();
        ConsultaProcedimiento entidad = new ConsultaProcedimiento(id);
        when(consultaProcedimientoService.findById(id)).thenReturn(entidad);

        Response respuesta = consultaProcedimientoResource.eliminar(id.toString());

        assertEquals(204, respuesta.getStatus());
        verify(consultaProcedimientoService).eliminar(entidad);
    }
}

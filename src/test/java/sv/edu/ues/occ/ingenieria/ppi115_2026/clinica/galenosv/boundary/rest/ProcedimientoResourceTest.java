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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ProcedimientoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ProcedimientoMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Procedimiento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcedimientoResourceTest {

    @Mock
    ProcedimientoService procedimientoService;

    @Mock
    ProcedimientoMapper procedimientoMapper;

    @InjectMocks
    ProcedimientoResource procedimientoResource;

    @Test
    void getListaDevuelveTotalYLimitaMaxA100() {
        Procedimiento entidad = new Procedimiento(UUID.randomUUID());
        ProcedimientoDTO dto = new ProcedimientoDTO(entidad.getIdProcedimiento(), "Consulta", Boolean.TRUE, null);
        when(procedimientoService.findRange(0, 100, null)).thenReturn(List.of(entidad));
        when(procedimientoService.count(null)).thenReturn(1L);
        when(procedimientoMapper.toDTO(entidad)).thenReturn(dto);

        PageResponse<ProcedimientoDTO> respuesta = procedimientoResource.listar(0, 1000, null);

        assertEquals(1, respuesta.items().size());
        assertEquals(1L, respuesta.total());
        assertEquals(100, respuesta.max());
        verify(procedimientoService).findRange(0, 100, null);
        verify(procedimientoService).count(null);
    }

    @Test
    void getPorIdInexistenteLanzaNotFound() {
        UUID id = UUID.randomUUID();
        when(procedimientoService.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> procedimientoResource.obtener(id.toString()));
    }

    @Test
    void postDevuelve201YHeaderLocation() {
        ProcedimientoDTO dto = new ProcedimientoDTO(null, "Nuevo", Boolean.TRUE, null);
        Procedimiento entidad = new Procedimiento();
        ProcedimientoDTO creada = new ProcedimientoDTO(UUID.randomUUID(), "Nuevo", Boolean.TRUE, null);
        when(procedimientoMapper.toEntity(dto)).thenReturn(entidad);
        when(procedimientoMapper.toDTO(entidad)).thenReturn(creada);

        Response respuesta = procedimientoResource.crear(dto);

        assertEquals(201, respuesta.getStatus());
        assertNotNull(respuesta.getHeaderString("Location"));
        assertTrue(respuesta.getHeaderString("Location").contains(creada.id().toString()));
        verify(procedimientoService).crear(entidad);
    }

    @Test
    void putExistenteCopiaYActualiza() {
        UUID id = UUID.randomUUID();
        Procedimiento entidad = new Procedimiento(id);
        ProcedimientoDTO dto = new ProcedimientoDTO(null, "Actualizado", Boolean.TRUE, null);
        ProcedimientoDTO esperada = new ProcedimientoDTO(id, "Actualizado", Boolean.TRUE, null);
        when(procedimientoMapper.toDTO(entidad)).thenReturn(esperada);
        when(procedimientoService.actualizarSi(any(), any(), any())).thenAnswer(inv -> {
            java.util.function.Predicate<Object> precondicion = inv.getArgument(1);
            java.util.function.Consumer<Object> cambios = inv.getArgument(2);
            if (!precondicion.test(entidad)) {
                throw new sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error.PreconditionFailedException("El ETag no coincide.");
            }
            cambios.accept(entidad);
            return entidad;
        });

        Response respuesta = procedimientoResource.actualizar(id.toString(), sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcularFuerte(esperada), dto);

        assertEquals(esperada, respuesta.getEntity());
        assertEquals(sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcularFuerte(esperada), respuesta.getHeaderString("ETag"));
        verify(procedimientoMapper).copiar(dto, entidad);
        verify(procedimientoService).actualizarSi(any(), any(), any());
    }

    @Test
    void deleteDevuelve204() {
        UUID id = UUID.randomUUID();
        Procedimiento entidad = new Procedimiento(id);
        when(procedimientoService.findById(id)).thenReturn(entidad);

        Response respuesta = procedimientoResource.eliminar(id.toString());

        assertEquals(204, respuesta.getStatus());
        assertFalse(entidad.getActivo());
        verify(procedimientoService).actualizar(entidad);
    }
}

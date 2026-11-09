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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ProcedimientoPasoSecuenciaDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ProcedimientoPasoSecuenciaMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoSecuenciaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoSecuencia;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcedimientoPasoSecuenciaResourceTest {

    @Mock
    ProcedimientoPasoSecuenciaService procedimientoPasoSecuenciaService;

    @Mock
    ProcedimientoPasoSecuenciaMapper procedimientoPasoSecuenciaMapper;

    @InjectMocks
    ProcedimientoPasoSecuenciaResource procedimientoPasoSecuenciaResource;

    @Test
    void getListaDevuelveTotalYLimitaMaxA100() {
        ProcedimientoPasoSecuencia entidad = new ProcedimientoPasoSecuencia(UUID.randomUUID());
        ProcedimientoPasoSecuenciaDTO dto = new ProcedimientoPasoSecuenciaDTO(entidad.getIdProcedimientoPasoSecuencia(),
                UUID.randomUUID(), "SIGUIENTE", UUID.randomUUID());
        when(procedimientoPasoSecuenciaService.findRange(0, 100, null)).thenReturn(List.of(entidad));
        when(procedimientoPasoSecuenciaService.count(null)).thenReturn(1L);
        when(procedimientoPasoSecuenciaMapper.toDTO(entidad)).thenReturn(dto);

        PageResponse<ProcedimientoPasoSecuenciaDTO> respuesta = procedimientoPasoSecuenciaResource.listar(0, 1000, null);

        assertEquals(1, respuesta.items().size());
        assertEquals(1L, respuesta.total());
        assertEquals(100, respuesta.max());
        verify(procedimientoPasoSecuenciaService).findRange(0, 100, null);
        verify(procedimientoPasoSecuenciaService).count(null);
    }

    @Test
    void getPorIdInexistenteLanzaNotFound() {
        UUID id = UUID.randomUUID();
        when(procedimientoPasoSecuenciaService.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> procedimientoPasoSecuenciaResource.obtener(id.toString()));
    }

    @Test
    void postDevuelve201YHeaderLocation() {
        ProcedimientoPasoSecuenciaDTO dto = new ProcedimientoPasoSecuenciaDTO(null,
                UUID.randomUUID(), "SIGUIENTE", UUID.randomUUID());
        ProcedimientoPasoSecuencia entidad = new ProcedimientoPasoSecuencia();
        ProcedimientoPasoSecuenciaDTO creada = new ProcedimientoPasoSecuenciaDTO(UUID.randomUUID(),
                dto.idProcedimientoPasoReferencia(), dto.tipoSecuencia(), dto.idProcedimientoPaso());
        when(procedimientoPasoSecuenciaMapper.toEntity(dto)).thenReturn(entidad);
        when(procedimientoPasoSecuenciaMapper.toDTO(entidad)).thenReturn(creada);

        Response respuesta = procedimientoPasoSecuenciaResource.crear(dto);

        assertEquals(201, respuesta.getStatus());
        assertNotNull(respuesta.getHeaderString("Location"));
        assertTrue(respuesta.getHeaderString("Location").contains(creada.id().toString()));
        verify(procedimientoPasoSecuenciaService).crear(entidad);
    }

    @Test
    void putExistenteCopiaYActualiza() {
        UUID id = UUID.randomUUID();
        ProcedimientoPasoSecuencia entidad = new ProcedimientoPasoSecuencia(id);
        ProcedimientoPasoSecuenciaDTO dto = new ProcedimientoPasoSecuenciaDTO(null,
                UUID.randomUUID(), "SIGUIENTE", UUID.randomUUID());
        ProcedimientoPasoSecuenciaDTO esperada = new ProcedimientoPasoSecuenciaDTO(id,
                dto.idProcedimientoPasoReferencia(), dto.tipoSecuencia(), dto.idProcedimientoPaso());
        when(procedimientoPasoSecuenciaMapper.toDTO(entidad)).thenReturn(esperada);
        when(procedimientoPasoSecuenciaService.actualizarSi(any(), any(), any())).thenAnswer(inv -> {
            java.util.function.Predicate<Object> precondicion = inv.getArgument(1);
            java.util.function.Consumer<Object> cambios = inv.getArgument(2);
            if (!precondicion.test(entidad)) {
                throw new sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error.PreconditionFailedException("El ETag no coincide.");
            }
            cambios.accept(entidad);
            return entidad;
        });

        Response respuesta = procedimientoPasoSecuenciaResource.actualizar(id.toString(), sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcularFuerte(esperada), dto);

        assertEquals(esperada, respuesta.getEntity());
        assertEquals(sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcularFuerte(esperada), respuesta.getHeaderString("ETag"));
        verify(procedimientoPasoSecuenciaMapper).copiar(dto, entidad);
        verify(procedimientoPasoSecuenciaService).actualizarSi(any(), any(), any());
    }

    @Test
    void deleteDevuelve204() {
        UUID id = UUID.randomUUID();
        ProcedimientoPasoSecuencia entidad = new ProcedimientoPasoSecuencia(id);
        when(procedimientoPasoSecuenciaService.findById(id)).thenReturn(entidad);

        Response respuesta = procedimientoPasoSecuenciaResource.eliminar(id.toString());

        assertEquals(204, respuesta.getStatus());
        verify(procedimientoPasoSecuenciaService).eliminar(entidad);
    }
}

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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ExamenResultadoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ExamenResultadoMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenResultadoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenResultado;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenResultadoResourceTest {

    @Mock
    ExamenResultadoService examenResultadoService;

    @Mock
    ExamenResultadoMapper examenResultadoMapper;

    @InjectMocks
    ExamenResultadoResource examenResultadoResource;

    @Test
    void getListaDevuelveTotalYLimitaMaxA100() {
        ExamenResultado entidad = new ExamenResultado(UUID.randomUUID());
        ExamenResultadoDTO dto = new ExamenResultadoDTO(entidad.getIdExamenResultado(),
                null, "r", "i", null, UUID.randomUUID());
        when(examenResultadoService.findRange(0, 100, null)).thenReturn(List.of(entidad));
        when(examenResultadoService.count(null)).thenReturn(1L);
        when(examenResultadoMapper.toDTO(entidad)).thenReturn(dto);

        PageResponse<ExamenResultadoDTO> respuesta = examenResultadoResource.listar(0, 1000, null);

        assertEquals(1, respuesta.items().size());
        assertEquals(1L, respuesta.total());
        assertEquals(100, respuesta.max());
        verify(examenResultadoService).findRange(0, 100, null);
        verify(examenResultadoService).count(null);
    }

    @Test
    void getPorIdInexistenteLanzaNotFound() {
        UUID id = UUID.randomUUID();
        when(examenResultadoService.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> examenResultadoResource.obtener(id.toString()));
    }

    @Test
    void postDevuelve201YHeaderLocation() {
        ExamenResultadoDTO dto = new ExamenResultadoDTO(null, null, "r", "i", null, UUID.randomUUID());
        ExamenResultado entidad = new ExamenResultado();
        ExamenResultadoDTO creada = new ExamenResultadoDTO(UUID.randomUUID(),
                dto.fechaCreacion(), dto.resultado(), dto.interpretacion(), dto.rutaAtestado(), dto.idOrdenExamen());
        when(examenResultadoMapper.toEntity(dto)).thenReturn(entidad);
        when(examenResultadoMapper.toDTO(entidad)).thenReturn(creada);

        Response respuesta = examenResultadoResource.crear(dto);

        assertEquals(201, respuesta.getStatus());
        assertNotNull(respuesta.getHeaderString("Location"));
        assertTrue(respuesta.getHeaderString("Location").contains(creada.id().toString()));
        verify(examenResultadoService).crear(entidad);
    }

    @Test
    void putExistenteCopiaYActualiza() {
        UUID id = UUID.randomUUID();
        ExamenResultado entidad = new ExamenResultado(id);
        ExamenResultadoDTO dto = new ExamenResultadoDTO(null, null, "r", "i", null, UUID.randomUUID());
        ExamenResultadoDTO esperada = new ExamenResultadoDTO(id,
                dto.fechaCreacion(), dto.resultado(), dto.interpretacion(), dto.rutaAtestado(), dto.idOrdenExamen());
        when(examenResultadoMapper.toDTO(entidad)).thenReturn(esperada);
        when(examenResultadoService.actualizarSi(any(), any(), any())).thenAnswer(inv -> {
            java.util.function.Predicate<Object> precondicion = inv.getArgument(1);
            java.util.function.Consumer<Object> cambios = inv.getArgument(2);
            if (!precondicion.test(entidad)) {
                throw new sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error.PreconditionFailedException("El ETag no coincide.");
            }
            cambios.accept(entidad);
            return entidad;
        });

        Response respuesta = examenResultadoResource.actualizar(id.toString(), sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcularFuerte(esperada), dto);

        assertEquals(esperada, respuesta.getEntity());
        assertEquals(sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcularFuerte(esperada), respuesta.getHeaderString("ETag"));
        verify(examenResultadoMapper).copiar(dto, entidad);
        verify(examenResultadoService).actualizarSi(any(), any(), any());
    }

    @Test
    void deleteDevuelve204() {
        UUID id = UUID.randomUUID();
        ExamenResultado entidad = new ExamenResultado(id);
        when(examenResultadoService.findById(id)).thenReturn(entidad);

        Response respuesta = examenResultadoResource.eliminar(id.toString());

        assertEquals(204, respuesta.getStatus());
        verify(examenResultadoService).eliminar(entidad);
    }
}

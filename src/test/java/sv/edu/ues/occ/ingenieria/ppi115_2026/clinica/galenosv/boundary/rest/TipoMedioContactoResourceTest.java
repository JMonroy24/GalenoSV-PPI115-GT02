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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.TipoMedioContactoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.TipoMedioContactoMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.TipoMedioContactoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoMedioContacto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoMedioContactoResourceTest {

    @Mock
    TipoMedioContactoService tipoMedioContactoService;

    @Mock
    TipoMedioContactoMapper tipoMedioContactoMapper;

    @InjectMocks
    TipoMedioContactoResource tipoMedioContactoResource;

    @Test
    void getListaDevuelveTotalYLimitaMaxA100() {
        TipoMedioContacto entidad = new TipoMedioContacto(UUID.randomUUID());
        TipoMedioContactoDTO dto = new TipoMedioContactoDTO(entidad.getIdTipoMedioContacto(), "Correo", null, null, Boolean.TRUE);
        when(tipoMedioContactoService.findRange(0, 100, null)).thenReturn(List.of(entidad));
        when(tipoMedioContactoService.count(null)).thenReturn(1L);
        when(tipoMedioContactoMapper.toDTO(entidad)).thenReturn(dto);

        PageResponse<TipoMedioContactoDTO> respuesta = tipoMedioContactoResource.listar(0, 1000, null);

        assertEquals(1, respuesta.items().size());
        assertEquals(1L, respuesta.total());
        assertEquals(100, respuesta.max());
        verify(tipoMedioContactoService).findRange(0, 100, null);
        verify(tipoMedioContactoService).count(null);
    }

    @Test
    void getPorIdInexistenteLanzaNotFound() {
        UUID id = UUID.randomUUID();
        when(tipoMedioContactoService.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> tipoMedioContactoResource.obtener(id.toString()));
    }

    @Test
    void postDevuelve201YHeaderLocation() {
        TipoMedioContactoDTO dto = new TipoMedioContactoDTO(null, "Correo", null, null, Boolean.TRUE);
        TipoMedioContacto entidad = new TipoMedioContacto();
        TipoMedioContactoDTO creada = new TipoMedioContactoDTO(UUID.randomUUID(), "Correo", null, null, Boolean.TRUE);
        when(tipoMedioContactoMapper.toEntity(dto)).thenReturn(entidad);
        when(tipoMedioContactoMapper.toDTO(entidad)).thenReturn(creada);

        Response respuesta = tipoMedioContactoResource.crear(dto);

        assertEquals(201, respuesta.getStatus());
        assertNotNull(respuesta.getHeaderString("Location"));
        assertTrue(respuesta.getHeaderString("Location").contains(creada.id().toString()));
        verify(tipoMedioContactoService).crear(entidad);
    }

    @Test
    void putExistenteCopiaYActualiza() {
        UUID id = UUID.randomUUID();
        TipoMedioContacto entidad = new TipoMedioContacto(id);
        TipoMedioContactoDTO dto = new TipoMedioContactoDTO(null, "Teléfono", null, null, Boolean.TRUE);
        TipoMedioContactoDTO esperada = new TipoMedioContactoDTO(id, "Teléfono", null, null, Boolean.TRUE);
        when(tipoMedioContactoMapper.toDTO(entidad)).thenReturn(esperada);
        when(tipoMedioContactoService.actualizarSi(any(), any(), any())).thenAnswer(inv -> {
            java.util.function.Predicate<Object> precondicion = inv.getArgument(1);
            java.util.function.Consumer<Object> cambios = inv.getArgument(2);
            if (!precondicion.test(entidad)) {
                throw new sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error.PreconditionFailedException("El ETag no coincide.");
            }
            cambios.accept(entidad);
            return entidad;
        });

        Response respuesta = tipoMedioContactoResource.actualizar(id.toString(), sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcularFuerte(esperada), dto);

        assertEquals(esperada, respuesta.getEntity());
        assertEquals(sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcularFuerte(esperada), respuesta.getHeaderString("ETag"));
        verify(tipoMedioContactoMapper).copiar(dto, entidad);
        verify(tipoMedioContactoService).actualizarSi(any(), any(), any());
    }

    @Test
    void deleteDevuelve204() {
        UUID id = UUID.randomUUID();
        TipoMedioContacto entidad = new TipoMedioContacto(id);
        when(tipoMedioContactoService.findById(id)).thenReturn(entidad);

        Response respuesta = tipoMedioContactoResource.eliminar(id.toString());

        assertEquals(204, respuesta.getStatus());
        assertFalse(entidad.getActivo());
        verify(tipoMedioContactoService).actualizar(entidad);
    }
}

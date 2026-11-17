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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ConsultaDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ConsultaMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Consulta;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaResourceTest {

    @Mock
    ConsultaService consultaService;

    @Mock
    ConsultaMapper consultaMapper;

    @InjectMocks
    ConsultaResource consultaResource;

    @Test
    void getListaDevuelveTotalYLimitaMaxA100() {
        Consulta entidad = new Consulta(UUID.randomUUID());
        ConsultaDTO dto = new ConsultaDTO(entidad.getIdConsulta(), null, null,
                "REF-001", null, UUID.randomUUID());
        when(consultaService.findRange(0, 100, null)).thenReturn(List.of(entidad));
        when(consultaService.count(null)).thenReturn(1L);
        when(consultaMapper.toDTO(entidad)).thenReturn(dto);

        PageResponse<ConsultaDTO> respuesta = consultaResource.listar(0, 1000, null);

        assertEquals(1, respuesta.items().size());
        assertEquals(1L, respuesta.total());
        assertEquals(100, respuesta.max());
        verify(consultaService).findRange(0, 100, null);
        verify(consultaService).count(null);
    }

    @Test
    void getPorIdInexistenteLanzaNotFound() {
        UUID id = UUID.randomUUID();
        when(consultaService.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> consultaResource.obtener(id.toString()));
    }

    @Test
    void listarFiltraRelacionYTotalConservandoBusquedaYPaginacion() {
        UUID id = UUID.randomUUID();
        consultaResource.pacienteId = id.toString();
        Consulta entidad = new Consulta(UUID.randomUUID());
        when(consultaService.findRange(5, 100, "texto", id)).thenReturn(List.of(entidad));
        when(consultaService.count("texto", id)).thenReturn(7L);

        var respuesta = consultaResource.listar(5, 1000, " texto ");

        assertEquals(1, respuesta.items().size());
        assertEquals(7L, respuesta.total());
        assertEquals(5, respuesta.first());
        assertEquals(100, respuesta.max());
        verify(consultaService).findRange(5, 100, "texto", id);
        verify(consultaService).count("texto", id);
        verify(consultaService, never()).findRange(anyInt(), anyInt(), any());
    }

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(strings = {"", " ", "invalido", "1-1-1-1-1"})
    void listarRechazaUuidInvalidoSinConsultarServicio(String id) {
        consultaResource.pacienteId = id;

        var error = assertThrows(jakarta.ws.rs.BadRequestException.class,
                () -> consultaResource.listar(0, 20, null));

        assertEquals(400, error.getResponse().getStatus());
        verifyNoInteractions(consultaService);
    }

    @Test
    void listarConRelacionRechazaPaginacionInvalida() {
        consultaResource.pacienteId = UUID.randomUUID().toString();
        assertThrows(jakarta.ws.rs.BadRequestException.class,
                () -> consultaResource.listar(-1, 20, null));
        assertThrows(jakarta.ws.rs.BadRequestException.class,
                () -> consultaResource.listar(0, 0, null));
        verifyNoInteractions(consultaService);
    }

    @Test
    void postDevuelve201YHeaderLocation() {
        ConsultaDTO dto = new ConsultaDTO(null, null, null, "REF-001", null, UUID.randomUUID());
        Consulta entidad = new Consulta();
        ConsultaDTO creada = new ConsultaDTO(UUID.randomUUID(),
                dto.fechaInicio(), dto.fechaFin(), dto.referenciaExterna(), dto.observaciones(), dto.idPersonaRol());
        when(consultaMapper.toEntity(dto)).thenReturn(entidad);
        when(consultaMapper.toDTO(entidad)).thenReturn(creada);

        Response respuesta = consultaResource.crear(dto);

        assertEquals(201, respuesta.getStatus());
        assertNotNull(respuesta.getHeaderString("Location"));
        assertTrue(respuesta.getHeaderString("Location").contains(creada.id().toString()));
        verify(consultaService).crear(entidad);
    }

    @Test
    void putExistenteCopiaYActualiza() {
        UUID id = UUID.randomUUID();
        Consulta entidad = new Consulta(id);
        ConsultaDTO dto = new ConsultaDTO(null, null, null, "REF-001", null, UUID.randomUUID());
        ConsultaDTO esperada = new ConsultaDTO(id,
                dto.fechaInicio(), dto.fechaFin(), dto.referenciaExterna(), dto.observaciones(), dto.idPersonaRol());
        when(consultaMapper.toDTO(entidad)).thenReturn(esperada);
        when(consultaService.actualizarSi(any(), any(), any())).thenAnswer(inv -> {
            java.util.function.Predicate<Object> precondicion = inv.getArgument(1);
            java.util.function.Consumer<Object> cambios = inv.getArgument(2);
            if (!precondicion.test(entidad)) {
                throw new sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error.PreconditionFailedException("El ETag no coincide.");
            }
            cambios.accept(entidad);
            return entidad;
        });

        Response respuesta = consultaResource.actualizar(id.toString(), sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcularFuerte(esperada), dto);

        assertEquals(esperada, respuesta.getEntity());
        assertEquals(sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcularFuerte(esperada), respuesta.getHeaderString("ETag"));
        verify(consultaMapper).copiar(dto, entidad);
        verify(consultaService).actualizarSi(any(), any(), any());
    }

    @Test
    void deleteDevuelve204() {
        UUID id = UUID.randomUUID();
        Consulta entidad = new Consulta(id);
        when(consultaService.findById(id)).thenReturn(entidad);

        Response respuesta = consultaResource.eliminar(id.toString());

        assertEquals(204, respuesta.getStatus());
        verify(consultaService).eliminar(entidad);
    }
}

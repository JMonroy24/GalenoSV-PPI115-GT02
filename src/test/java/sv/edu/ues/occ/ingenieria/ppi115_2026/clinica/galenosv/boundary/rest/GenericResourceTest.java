package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.ws.rs.core.Response;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error.PreconditionFailedException;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error.PreconditionRequiredException;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GenericResourceTest {

    private static final UUID ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    static class Entidad {

        UUID id;
        String nombre;

        Entidad(UUID id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }
    }

    record DTO(UUID id, String nombre) {
    }

    static class TestResource extends GenericResource<Entidad, DTO, UUID> {

        private final DefaultService<Entidad, UUID> service;
        private final EntityMapper<Entidad, DTO> mapper;

        TestResource(DefaultService<Entidad, UUID> service, EntityMapper<Entidad, DTO> mapper) {
            this.service = service;
            this.mapper = mapper;
        }

        @Override
        protected DefaultService<Entidad, UUID> getService() {
            return service;
        }

        @Override
        protected EntityMapper<Entidad, DTO> getMapper() {
            return mapper;
        }

        @Override
        protected UUID getId(DTO dto) {
            return dto == null ? null : dto.id();
        }

        @Override
        protected void setId(Entidad entidad, UUID id) {
            entidad.id = id;
        }

        @Override
        protected void asignarNuevoId(Entidad entidad) {
            entidad.id = UUID.randomUUID();
        }

        @Override
        protected UUID parseId(String id) {
            return UUID.fromString(id);
        }
    }

    private DefaultService<Entidad, UUID> serviceMock() {
        @SuppressWarnings("unchecked")
        DefaultService<Entidad, UUID> service = mock(DefaultService.class);
        return service;
    }

    @SuppressWarnings("unchecked")
    private EntityMapper<Entidad, DTO> mapperMock() {
        return mock(EntityMapper.class);
    }

    @Test
    void getDevuelveEtagDebilDelDtoSerializado() {
        DefaultService<Entidad, UUID> service = serviceMock();
        EntityMapper<Entidad, DTO> mapper = mapperMock();
        Entidad entidad = new Entidad(ID, "Original");
        DTO dto = new DTO(ID, "Original");
        when(service.findById(ID)).thenReturn(entidad);
        when(mapper.toDTO(entidad)).thenReturn(dto);

        Response respuesta = new TestResource(service, mapper).obtener(ID.toString());

        assertEquals(200, respuesta.getStatus());
        assertSame(dto, respuesta.getEntity());
        assertEquals(ETagUtils.calcular(dto), respuesta.getHeaderString("ETag"));
        assertTrue(respuesta.getHeaderString("ETag").startsWith("W/\""));
    }

    @Test
    void putSinIfMatchLanzaPreconditionRequired() {
        DefaultService<Entidad, UUID> service = serviceMock();
        EntityMapper<Entidad, DTO> mapper = mapperMock();
        Entidad entidad = new Entidad(ID, "Original");
        when(service.findById(ID)).thenReturn(entidad);

        TestResource resource = new TestResource(service, mapper);

        assertThrows(PreconditionRequiredException.class,
                () -> resource.actualizar(ID.toString(), null, new DTO(null, "Nuevo")));
        verify(mapper, never()).copiar(any(), any());
        verify(service, never()).actualizar(any());
    }

    @Test
    void putConIfMatchIncorrectoLanzaPreconditionFailed() {
        DefaultService<Entidad, UUID> service = serviceMock();
        EntityMapper<Entidad, DTO> mapper = mapperMock();
        Entidad entidad = new Entidad(ID, "Original");
        DTO actual = new DTO(ID, "Original");
        when(service.findById(ID)).thenReturn(entidad);
        when(mapper.toDTO(entidad)).thenReturn(actual);

        TestResource resource = new TestResource(service, mapper);
        String etagAjeno = ETagUtils.calcular(new DTO(ID, "Otro estado"));

        assertThrows(PreconditionFailedException.class,
                () -> resource.actualizar(ID.toString(), etagAjeno, new DTO(null, "Nuevo")));
        verify(mapper, never()).copiar(any(), any());
        verify(service, never()).actualizar(any());
    }

    @Test
    void putConIfMatchCorrectoActualiza() {
        DefaultService<Entidad, UUID> service = serviceMock();
        EntityMapper<Entidad, DTO> mapper = mapperMock();
        Entidad entidad = new Entidad(ID, "Original");
        DTO actual = new DTO(ID, "Original");
        DTO cambios = new DTO(null, "Nuevo");
        when(service.findById(ID)).thenReturn(entidad);
        when(mapper.toDTO(entidad)).thenReturn(actual);

        TestResource resource = new TestResource(service, mapper);
        DTO respuesta = resource.actualizar(ID.toString(), ETagUtils.calcular(actual), cambios);

        assertSame(actual, respuesta);
        verify(mapper).copiar(cambios, entidad);
        verify(service).actualizar(entidad);
    }
}

package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error.PreconditionFailedException;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error.PreconditionRequiredException;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultDAO;
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

    static class TestService extends DefaultService<Entidad, UUID> {

        private final DefaultDAO<Entidad, UUID> dao;

        TestService(DefaultDAO<Entidad, UUID> dao) {
            this.dao = dao;
        }

        @Override
        protected DefaultDAO<Entidad, UUID> getDAO() {
            return dao;
        }
    }

    private DefaultService<Entidad, UUID> serviceMock() {
        @SuppressWarnings("unchecked")
        DefaultService<Entidad, UUID> service = mock(DefaultService.class);
        return service;
    }

    private DefaultDAO<Entidad, UUID> daoMock() {
        @SuppressWarnings("unchecked")
        DefaultDAO<Entidad, UUID> dao = mock(DefaultDAO.class);
        return dao;
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

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void putSinIfMatchLanzaPreconditionRequiredSinConsultarBD(String ifMatch) {
        DefaultDAO<Entidad, UUID> dao = daoMock();
        DefaultService<Entidad, UUID> service = new TestService(dao);
        EntityMapper<Entidad, DTO> mapper = mapperMock();
        TestResource resource = new TestResource(service, mapper);

        assertThrows(PreconditionRequiredException.class,
                () -> resource.actualizar(ID.toString(), ifMatch, new DTO(null, "Nuevo")));
        verifyNoInteractions(mapper, dao);
    }

    @Test
    void putConIfMatchIncorrectoLanzaPreconditionFailed() {
        DefaultDAO<Entidad, UUID> dao = daoMock();
        DefaultService<Entidad, UUID> service = new TestService(dao);
        EntityMapper<Entidad, DTO> mapper = mapperMock();
        Entidad entidad = new Entidad(ID, "Original");
        DTO actual = new DTO(ID, "Original");
        when(dao.findByIdParaActualizar(ID)).thenReturn(entidad);
        when(mapper.toDTO(entidad)).thenReturn(actual);

        TestResource resource = new TestResource(service, mapper);
        String etagAjeno = ETagUtils.calcularFuerte(new DTO(ID, "Otro estado"));

        assertThrows(PreconditionFailedException.class,
                () -> resource.actualizar(ID.toString(), etagAjeno, new DTO(null, "Nuevo")));
        verify(mapper, never()).copiar(any(), any());
        verify(dao, never()).update(any());
    }

    @Test
    void putConComodinSobreIdInexistenteDevuelve404() {
        DefaultDAO<Entidad, UUID> dao = daoMock();
        EntityMapper<Entidad, DTO> mapper = mapperMock();
        TestResource resource = new TestResource(new TestService(dao), mapper);

        assertThrows(NotFoundException.class,
                () -> resource.actualizar(ID.toString(), "*", new DTO(null, "Nuevo")));
        verify(dao).findByIdParaActualizar(ID);
        verifyNoMoreInteractions(dao);
        verifyNoInteractions(mapper);
    }

    @Test
    void putConIfMatchCorrectoDevuelveEstadoNuevoYRechazaReusoDelEtagAnterior() {
        DefaultDAO<Entidad, UUID> dao = daoMock();
        DefaultService<Entidad, UUID> service = new TestService(dao);
        EntityMapper<Entidad, DTO> mapper = mapperMock();
        Entidad entidad = new Entidad(ID, "Original");
        DTO actual = new DTO(ID, "Original");
        DTO cambios = new DTO(null, "Nuevo");
        when(dao.findByIdParaActualizar(ID)).thenReturn(entidad);
        when(mapper.toDTO(entidad)).thenAnswer(inv -> new DTO(entidad.id, entidad.nombre));
        doAnswer(inv -> {
            entidad.nombre = cambios.nombre();
            return null;
        }).when(mapper).copiar(cambios, entidad);
        TestResource resource = new TestResource(service, mapper);

        assertThrows(PreconditionFailedException.class,
                () -> resource.actualizar(ID.toString(), ETagUtils.calcular(actual), cambios));
        String etagAnterior = ETagUtils.calcularFuerte(actual);
        Response respuesta = resource.actualizar(ID.toString(), etagAnterior, cambios);

        DTO actualizada = new DTO(ID, "Nuevo");
        assertEquals(200, respuesta.getStatus());
        assertEquals(actualizada, respuesta.getEntity());
        assertEquals(ETagUtils.calcularFuerte(actualizada), respuesta.getHeaderString("ETag"));
        assertNotEquals(etagAnterior, respuesta.getHeaderString("ETag"));
        assertThrows(PreconditionFailedException.class,
                () -> resource.actualizar(ID.toString(), etagAnterior, new DTO(null, "Obsoleto")));
        verify(mapper).copiar(cambios, entidad);
        verify(dao).update(entidad);
    }
}

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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ConsultaDetalleDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ConsultaPasoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ConsultaDetalleMapper;
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

    @Mock
    sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaTicketService consultaTicketService;

    @Mock
    ConsultaDetalleMapper consultaDetalleMapper;

    @InjectMocks
    ConsultaResource consultaResource;

    @Test
    void ticketDevuelvePdfInlineSinCacheYUsaElFrontendIndicado() {
        UUID id = UUID.randomUUID();
        byte[] pdf = "%PDF-1.7".getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        when(consultaTicketService.generar(id, "https://pares.example/galeno/#consulta/" + id)).thenReturn(pdf);
        try (var respuesta = consultaResource.descargarTicket(id.toString(), "https://pares.example/galeno/", "http://localhost:3000")) {
            assertEquals(200, respuesta.getStatus());
            assertEquals("application/pdf", respuesta.getMediaType().toString());
            assertSame(pdf, respuesta.getEntity());
            assertEquals("inline; filename=\"ticket-consulta.pdf\"",
                    respuesta.getHeaderString("Content-Disposition"));
            assertEquals("no-store", respuesta.getHeaderString("Cache-Control"));
        }
        verify(consultaTicketService).generar(id, "https://pares.example/galeno/#consulta/" + id);
    }

    @Test
    void ticketRechazaUuidInvalidoYPropagaConsultaInexistente() {
        assertThrows(jakarta.ws.rs.BadRequestException.class, () -> consultaResource.descargarTicket("1-1-1-1-1", null, null));
        verifyNoInteractions(consultaTicketService);
        UUID id = UUID.randomUUID();
        when(consultaTicketService.generar(eq(id), anyString())).thenThrow(new NotFoundException());
        assertThrows(NotFoundException.class, () -> consultaResource.descargarTicket(id.toString(), null, null));
    }

    @Test
    void ticketUsaOriginYRechazaFrontendInseguroAntesDeGenerar() {
        UUID id = UUID.randomUUID();
        assertThrows(jakarta.ws.rs.BadRequestException.class,
                () -> consultaResource.descargarTicket(id.toString(), "javascript:alert(1)", "http://localhost:3000"));
        verifyNoInteractions(consultaTicketService);
        consultaResource.descargarTicket(id.toString(), null, "https://pares.example").close();
        verify(consultaTicketService).generar(id, "https://pares.example/#consulta/" + id);
    }

    @Test
    void ticketDelRecursoContienePdfValidoYQrEscaneable() throws Exception {
        UUID id = UUID.randomUUID();
        var dao = mock(sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaDAO.class);
        var procedimientos = mock(sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoDAO.class);
        when(dao.findById(id)).thenReturn(new Consulta(id));
        consultaResource.consultaTicketService = new sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaTicketService() {
            { consultaDAO = dao; procedimientoDAO = procedimientos; }
        };
        try (var respuesta = consultaResource.descargarTicket(id.toString(), "http://localhost:3000", null);
             var pdf = org.apache.pdfbox.Loader.loadPDF((byte[]) respuesta.getEntity())) {
            assertEquals(200, respuesta.getStatus());
            assertEquals("application/pdf", respuesta.getMediaType().toString());
            assertEquals("inline; filename=\"ticket-consulta.pdf\"", respuesta.getHeaderString("Content-Disposition"));
            assertEquals(1, pdf.getNumberOfPages());
            assertEquals("http://localhost:3000/#consulta/" + id,
                    sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.TicketPdfAssertions.leerQr(pdf));
        }
    }

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
    void getPorIdIncluyePasosYETag() {
        UUID id = UUID.randomUUID();
        UUID idPaso = UUID.randomUUID();
        Consulta entidad = new Consulta(id);
        ConsultaDetalleDTO dto = new ConsultaDetalleDTO(id, null, null, "REF-002", null, null,
                List.of(new ConsultaPasoDTO(UUID.randomUUID(), "Toma de muestra", 1, "EJECUTADO", "7.5", idPaso)));
        when(consultaService.findById(id)).thenReturn(entidad);
        when(consultaDetalleMapper.toDTO(entidad)).thenReturn(dto);

        Response respuesta = consultaResource.obtener(id.toString());

        assertEquals(dto, respuesta.getEntity());
        assertEquals(ETagUtils.calcular(dto), respuesta.getHeaderString("ETag"));
        assertEquals(1, ((ConsultaDetalleDTO) respuesta.getEntity()).pasos().size());
        verify(consultaDetalleMapper).toDTO(entidad);
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
    @Test void filtrosClinicosSeCombinanConPacienteYConteo() {
        UUID clinica = UUID.randomUUID(), rol = UUID.randomUUID(), paciente = UUID.randomUUID();
        consultaResource.idClinica = clinica.toString(); consultaResource.idRol = rol.toString();
        consultaResource.pacienteId = paciente.toString(); consultaResource.desde = "2026-11-01"; consultaResource.hasta = "2026-11-24";
        when(consultaService.findRangeRest(eq(2), eq(10), any())).thenReturn(List.of());
        when(consultaService.countRest(any())).thenReturn(8L);
        var respuesta = consultaResource.listar(2, 10, "ref");
        var captor = org.mockito.ArgumentCaptor.forClass(sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.FiltroConsultasRest.class);
        verify(consultaService).findRangeRest(eq(2), eq(10), captor.capture());
        var filtro = captor.getValue(); verify(consultaService).countRest(filtro);
        assertEquals(clinica, filtro.idClinica()); assertEquals(rol, filtro.idRol()); assertEquals(paciente, filtro.pacienteId());
        assertEquals(java.time.Instant.parse("2026-11-01T06:00:00Z"), filtro.desde().toInstant());
        assertEquals(java.time.Instant.parse("2026-11-25T05:59:59.999Z"), filtro.hasta().toInstant());
        assertEquals(8L, respuesta.total()); assertEquals(2, respuesta.first());
    }

    @Test void filtrosRechazanFechasRangosYUuidInvalidos() {
        consultaResource.desde = "2026-02-30";
        assertThrows(jakarta.ws.rs.BadRequestException.class, () -> consultaResource.listar(0, 20, null));
        consultaResource.desde = "2026-11-24"; consultaResource.hasta = "2026-11-01";
        assertThrows(jakarta.ws.rs.BadRequestException.class, () -> consultaResource.listar(0, 20, null));
        consultaResource.desde = null; consultaResource.hasta = null; consultaResource.idRol = "invalido";
        assertThrows(jakarta.ws.rs.BadRequestException.class, () -> consultaResource.listar(0, 20, null));
        verifyNoInteractions(consultaService);
    }

}

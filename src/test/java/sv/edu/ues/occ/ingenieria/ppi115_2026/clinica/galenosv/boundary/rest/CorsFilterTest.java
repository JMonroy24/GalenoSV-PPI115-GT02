package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CorsFilterTest {

    private final CorsFilter filter = new CorsFilter();
    private final ContainerRequestContext request = mock(ContainerRequestContext.class);
    private final ContainerResponseContext response = mock(ContainerResponseContext.class);
    private final MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();

    @BeforeEach
    void preparar() {
        when(response.getHeaders()).thenReturn(headers);
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 201, 204, 400, 401, 403, 404, 500})
    void agregaCorsEnRespuestasRestSinCambiarEstadoNiContenido(int status) {
        when(request.getHeaderString("Origin")).thenReturn("http://localhost:3000");
        when(response.getStatus()).thenReturn(status);
        headers.putSingle("ETag", "\"version-1\"");
        headers.putSingle("Location", "/galenoSV/api/v1/clinicas/1");

        filter.filter(request, response);

        assertEquals("*", headers.getFirst("Access-Control-Allow-Origin"));
        assertEquals(List.of("total-registros", "ETag", "Location", "Content-Disposition"), tokens("Access-Control-Expose-Headers"));
        assertEquals(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"),
                tokens("Access-Control-Allow-Methods"));
        assertEquals(List.of("origin", "content-type", "accept", "authorization", "if-match", "if-none-match"),
                tokens("Access-Control-Allow-Headers"));
        assertEquals("\"version-1\"", headers.getFirst("ETag"));
        assertEquals("/galenoSV/api/v1/clinicas/1", headers.getFirst("Location"));
        assertNull(headers.getFirst("Access-Control-Allow-Credentials"));
        assertNull(headers.getFirst("total-registros"));
        assertEquals(status, response.getStatus());
        verify(response, never()).setStatus(anyInt());
        verify(response, never()).setEntity(any());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "null", "https://example.com", "http://localhost:3001",
        "http://localhost:3000.evil.example", "http://127.0.0.1:3000"})
    void permiteCorsDesdeCualquierOrigenOSinOrigin(String origin) {
        when(request.getHeaderString("Origin")).thenReturn(origin);

        filter.filter(request, response);

        assertEquals("*", headers.getFirst("Access-Control-Allow-Origin"));
    }

    @Test
    void conservaVaryExistenteYNoDuplicaOrigin() {
        headers.add("Vary", "Accept-Encoding, origin");

        filter.filter(request, response);
        filter.filter(request, response);

        assertEquals(List.of("Accept-Encoding, origin"), headers.get("Vary"));
    }

    @Test
    void conservaOtrasVariantesDeCache() {
        headers.add("Vary", "Accept-Encoding");

        filter.filter(request, response);

        assertEquals(List.of("Accept-Encoding"), headers.get("Vary"));
    }

    @Test
    void conservaVaryComodin() {
        headers.add("Vary", "*");

        filter.filter(request, response);

        assertEquals(List.of("*"), headers.get("Vary"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST", "PUT", "DELETE", "HEAD", "PATCH"})
    void respondePreflightCon200YEncabezadosSinRequerirToken(String method) {
        when(request.getMethod()).thenReturn("OPTIONS");
        when(request.getHeaderString("Origin")).thenReturn("http://localhost:3000");
        when(request.getHeaderString("Access-Control-Request-Method")).thenReturn(method);

        filter.filter(request);
        filter.filter(request, response);

        ArgumentCaptor<Response> captured = ArgumentCaptor.forClass(Response.class);
        verify(request).abortWith(captured.capture());
        assertEquals(200, captured.getValue().getStatus());
        assertFalse(captured.getValue().hasEntity());
        assertTrue(tokens("Access-Control-Allow-Methods").contains(method));
        assertEquals("*", headers.getFirst("Access-Control-Allow-Origin"));
        assertTrue(tokens("Access-Control-Allow-Headers").containsAll(
                List.of("origin", "content-type", "accept", "authorization", "if-match")));
        assertEquals("600", headers.getFirst("Access-Control-Max-Age"));
    }

    @Test
    void respondePreflightDeOtroOrigenCon200() {
        when(request.getMethod()).thenReturn("OPTIONS");
        when(request.getHeaderString("Origin")).thenReturn("https://example.com");
        when(request.getHeaderString("Access-Control-Request-Method")).thenReturn("GET");

        filter.filter(request);
        filter.filter(request, response);

        ArgumentCaptor<Response> captured = ArgumentCaptor.forClass(Response.class);
        verify(request).abortWith(captured.capture());
        assertEquals(200, captured.getValue().getStatus());
        assertEquals("*", headers.getFirst("Access-Control-Allow-Origin"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST", "PUT", "DELETE", "HEAD", "PATCH"})
    void noInterrumpePeticionesNormales(String method) {
        when(request.getMethod()).thenReturn(method);
        when(request.getHeaderString("Origin")).thenReturn("http://localhost:3000");

        filter.filter(request);

        verify(request, never()).abortWith(any());
    }

    @Test
    void respondeOptionsSinEncabezadosDePreflightCon200() {
        when(request.getMethod()).thenReturn("OPTIONS");
        when(request.getHeaderString("Origin")).thenReturn("http://localhost:3000");

        filter.filter(request);

        ArgumentCaptor<Response> captured = ArgumentCaptor.forClass(Response.class);
        verify(request).abortWith(captured.capture());
        assertEquals(200, captured.getValue().getStatus());
    }

    @Test
    void respondeOptionsSinOriginCon200() {
        when(request.getMethod()).thenReturn("OPTIONS");
        when(request.getHeaderString("Access-Control-Request-Method")).thenReturn("GET");

        filter.filter(request);

        ArgumentCaptor<Response> captured = ArgumentCaptor.forClass(Response.class);
        verify(request).abortWith(captured.capture());
        assertEquals(200, captured.getValue().getStatus());
    }

    @ParameterizedTest
    @CsvSource({"0, 0", "42, 20", "3000000000, 40"})
    void publicaTotalDeRegistrosDeLaPaginaSinModificarElCuerpo(long total, int first) {
        var pagina = new PageResponse<>(List.of(), total, first, 20);
        when(response.getEntity()).thenReturn(pagina);

        filter.filter(request, response);

        assertEquals(Long.toString(total), headers.getFirst("total-registros"));
        assertTrue(tokens("Access-Control-Expose-Headers").contains("total-registros"));
        verify(response, never()).setEntity(any());
    }

    @Test
    void registraAmbosFiltrosAntesDeLaAutenticacionYDelRecurso() {
        assertTrue(CorsFilter.class.isAnnotationPresent(Provider.class));
        assertTrue(CorsFilter.class.isAnnotationPresent(PreMatching.class));
        assertTrue(ContainerRequestFilter.class.isAssignableFrom(CorsFilter.class));
        assertTrue(ContainerResponseFilter.class.isAssignableFrom(CorsFilter.class));
        assertTrue(CorsFilter.class.getAnnotation(Priority.class).value() < Priorities.AUTHENTICATION);
    }

    @Test
    void pdfInlineConservaContenidoYCabecerasYExponeLaDisposicion() {
        byte[] pdf = "%PDF-1.7".getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        when(request.getMethod()).thenReturn("GET");
        when(response.getEntity()).thenReturn(pdf);
        headers.putSingle("Content-Type", "application/pdf");
        headers.putSingle("Content-Disposition", "inline; filename=\"ticket-consulta.pdf\"");
        filter.filter(request, response);
        assertEquals("*", headers.getFirst("Access-Control-Allow-Origin"));
        assertTrue(tokens("Access-Control-Expose-Headers").contains("Content-Disposition"));
        assertEquals("application/pdf", headers.getFirst("Content-Type"));
        assertEquals("inline; filename=\"ticket-consulta.pdf\"", headers.getFirst("Content-Disposition"));
        verify(response, never()).setEntity(any());
    }

    private List<String> tokens(String name) {
        return Arrays.stream(headers.getFirst(name).toString().split(",\\s*")).toList();
    }
}

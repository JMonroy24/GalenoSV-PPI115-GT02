package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
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
    void permiteOrigenLocalEnRespuestasRestSinCambiarEstadoNiContenido(int status) {
        when(request.getHeaderString("Origin")).thenReturn("http://localhost:3000");
        when(response.getStatus()).thenReturn(status);
        headers.putSingle("ETag", "\"version-1\"");
        headers.putSingle("Location", "/galenoSV/api/v1/clinicas/1");

        filter.filter(request, response);

        assertEquals("http://localhost:3000", headers.getFirst("Access-Control-Allow-Origin"));
        assertEquals(List.of("ETag", "Location"), tokens("Access-Control-Expose-Headers"));
        assertEquals("\"version-1\"", headers.getFirst("ETag"));
        assertEquals("/galenoSV/api/v1/clinicas/1", headers.getFirst("Location"));
        assertNull(headers.getFirst("Access-Control-Allow-Credentials"));
        assertEquals(status, response.getStatus());
        verify(response, never()).setStatus(anyInt());
        verify(response, never()).setEntity(any());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "null", "https://example.com", "http://localhost:3001",
        "http://localhost:3000.evil.example", "http://127.0.0.1:3000"})
    void noConcedeCorsAOrigenAusenteONoAutorizado(String origin) {
        when(request.getHeaderString("Origin")).thenReturn(origin);

        filter.filter(request, response);

        assertTrue(headers.keySet().stream().noneMatch(name -> name.startsWith("Access-Control-")));
        assertEquals("Origin", headers.getFirst("Vary"));
    }

    @Test
    void conservaVaryExistenteYNoDuplicaOrigin() {
        headers.add("Vary", "Accept-Encoding, origin");

        filter.filter(request, response);
        filter.filter(request, response);

        assertEquals(List.of("Accept-Encoding, origin"), headers.get("Vary"));
    }

    @Test
    void agregaOriginSinPerderOtrasVariantes() {
        headers.add("Vary", "Accept-Encoding");

        filter.filter(request, response);

        assertEquals(List.of("Accept-Encoding", "Origin"), headers.get("Vary"));
    }

    @Test
    void conservaVaryComodin() {
        headers.add("Vary", "*");

        filter.filter(request, response);

        assertEquals(List.of("*"), headers.get("Vary"));
    }

    @Test
    void respondePreflightCon204YEncabezadosParaTokenJsonYControlDeVersion() {
        when(request.getMethod()).thenReturn("OPTIONS");
        when(request.getHeaderString("Origin")).thenReturn("http://localhost:3000");
        when(request.getHeaderString("Access-Control-Request-Method")).thenReturn("PUT");

        filter.filter(request);
        filter.filter(request, response);

        ArgumentCaptor<Response> captured = ArgumentCaptor.forClass(Response.class);
        verify(request).abortWith(captured.capture());
        assertEquals(204, captured.getValue().getStatus());
        assertFalse(captured.getValue().hasEntity());
        assertEquals(List.of("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS"),
                tokens("Access-Control-Allow-Methods"));
        assertEquals(List.of("Accept", "Content-Type", "Authorization", "If-Match", "If-None-Match"),
                tokens("Access-Control-Allow-Headers"));
        assertEquals("600", headers.getFirst("Access-Control-Max-Age"));
    }

    @Test
    void rechazaPreflightDeOrigenNoAutorizado() {
        when(request.getMethod()).thenReturn("OPTIONS");
        when(request.getHeaderString("Origin")).thenReturn("https://example.com");
        when(request.getHeaderString("Access-Control-Request-Method")).thenReturn("GET");

        filter.filter(request);

        ArgumentCaptor<Response> captured = ArgumentCaptor.forClass(Response.class);
        verify(request).abortWith(captured.capture());
        assertEquals(403, captured.getValue().getStatus());
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST", "PUT", "DELETE", "HEAD"})
    void noInterrumpePeticionesNormales(String method) {
        when(request.getMethod()).thenReturn(method);
        when(request.getHeaderString("Origin")).thenReturn("http://localhost:3000");

        filter.filter(request);

        verify(request, never()).abortWith(any());
    }

    @Test
    void noInterrumpeOptionsSinEncabezadosDePreflight() {
        when(request.getMethod()).thenReturn("OPTIONS");
        when(request.getHeaderString("Origin")).thenReturn("http://localhost:3000");

        filter.filter(request);

        verify(request, never()).abortWith(any());
    }

    @Test
    void noInterrumpeOptionsSinOrigin() {
        when(request.getMethod()).thenReturn("OPTIONS");
        when(request.getHeaderString("Access-Control-Request-Method")).thenReturn("GET");

        filter.filter(request);

        verify(request, never()).abortWith(any());
    }

    private List<String> tokens(String name) {
        return Arrays.stream(headers.getFirst(name).toString().split(",\\s*")).toList();
    }
}

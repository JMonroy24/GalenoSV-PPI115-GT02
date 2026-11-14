package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.annotation.Priority;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.util.Arrays;
import java.util.List;

/** CORS para el frontend local; no sustituye la autenticación de la API. */
@Provider
@PreMatching
@Priority(Priorities.AUTHENTICATION - 1)
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String ALLOWED_ORIGIN = "http://localhost:3000";

    @Override
    public void filter(ContainerRequestContext request) {
        if (isPreflight(request)) {
            Response.Status status = ALLOWED_ORIGIN.equals(request.getHeaderString("Origin"))
                    ? Response.Status.NO_CONTENT : Response.Status.FORBIDDEN;
            request.abortWith(Response.status(status).build());
        }
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) {
        MultivaluedMap<String, Object> headers = response.getHeaders();
        // También varía la respuesta cuando no se concede acceso al origen.
        boolean variesByOrigin = headers.getOrDefault("Vary", List.of()).stream()
                .flatMap(value -> Arrays.stream(value.toString().split(",")))
                .map(String::trim)
                .anyMatch(value -> "Origin".equalsIgnoreCase(value) || "*".equals(value));
        if (!variesByOrigin) {
            headers.add("Vary", "Origin");
        }

        if (!ALLOWED_ORIGIN.equals(request.getHeaderString("Origin"))) {
            return;
        }

        headers.putSingle("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
        headers.putSingle("Access-Control-Expose-Headers", "ETag, Location");
        if (isPreflight(request)) {
            headers.putSingle("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, OPTIONS");
            headers.putSingle("Access-Control-Allow-Headers",
                    "Accept, Content-Type, Authorization, If-Match, If-None-Match");
            headers.putSingle("Access-Control-Max-Age", "600");
        }
    }

    private boolean isPreflight(ContainerRequestContext request) {
        return HttpMethod.OPTIONS.equals(request.getMethod())
                && request.getHeaderString("Origin") != null
                && request.getHeaderString("Access-Control-Request-Method") != null;
    }
}

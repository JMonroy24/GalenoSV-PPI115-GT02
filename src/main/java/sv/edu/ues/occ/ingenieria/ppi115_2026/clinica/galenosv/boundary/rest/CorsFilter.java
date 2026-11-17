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

/** CORS para la API REST, con preflight previo a la autenticación. */
@Provider
@PreMatching
@Priority(Priorities.AUTHENTICATION - 1)
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext request) {
        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            request.abortWith(Response.ok().build());
        }
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) {
        MultivaluedMap<String, Object> headers = response.getHeaders();
        headers.putSingle("Access-Control-Allow-Origin", "*");
        // PATCH e If-Match permiten actualizar los formularios médicos existentes.
        headers.putSingle("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH");
        headers.putSingle("Access-Control-Allow-Headers",
                "origin, content-type, accept, authorization, if-match, if-none-match");
        headers.putSingle("Access-Control-Expose-Headers", "total-registros, ETag, Location");
        if (response.getEntity() instanceof PageResponse<?> pagina) {
            headers.putSingle("total-registros", Long.toString(pagina.total()));
        }
        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            headers.putSingle("Access-Control-Max-Age", "600");
        }
    }

}

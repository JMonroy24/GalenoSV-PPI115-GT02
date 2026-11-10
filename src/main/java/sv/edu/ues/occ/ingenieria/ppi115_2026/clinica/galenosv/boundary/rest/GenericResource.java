package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import java.io.Serializable;
import java.net.URI;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error.PreconditionRequiredException;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultService;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public abstract class GenericResource<E, D, ID extends Serializable> {

    protected abstract DefaultService<E, ID> getService();

    protected abstract EntityMapper<E, D> getMapper();

    protected abstract ID getId(D dto);

    protected abstract void setId(E entidad, ID id);

    protected abstract void asignarNuevoId(E entidad);

    protected abstract ID parseId(String id);

    protected boolean borradoLogico() {
        return false;
    }

    protected void marcarInactivo(E entidad) {
        throw new UnsupportedOperationException("El recurso no soporta borrado lógico.");
    }

    @GET
    @RolesAllowed({"ADMINISTRADOR", "PERSONAL_CLINICO"})
    @Operation(summary = "Lista los recursos de forma paginada")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Listado paginado obtenido correctamente."),
        @APIResponse(responseCode = "400", description = "Parámetros de paginación inválidos."),
        @APIResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public PageResponse<D> listar(
            @QueryParam("first") @jakarta.ws.rs.DefaultValue("0") int first,
            @QueryParam("max") @jakarta.ws.rs.DefaultValue("20") int max,
            @QueryParam("q") String q) {
        if (first < 0) {
            throw new BadRequestException("El parámetro first debe ser mayor o igual a 0.");
        }
        if (max < 1) {
            throw new BadRequestException("El parámetro max debe ser mayor a 0.");
        }
        if (max > 100) {
            max = 100;
        }
        String filtro = q == null || q.isBlank() ? null : q.trim();
        List<D> items = getService().findRange(first, max, filtro).stream()
                .map(getMapper()::toDTO)
                .toList();
        long total = getService().count(filtro);
        return new PageResponse<>(items, total, first, max);
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMINISTRADOR", "PERSONAL_CLINICO"})
    @Operation(summary = "Obtiene un recurso por su identificador")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Recurso encontrado; incluye el encabezado ETag."),
        @APIResponse(responseCode = "404", description = "El recurso no existe."),
        @APIResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public Response obtener(@PathParam("id") String id) {
        E entidad = getService().findById(parseId(id));
        if (entidad == null) {
            throw new NotFoundException("No existe el recurso solicitado.");
        }
        D dto = getMapper().toDTO(entidad);
        return Response.ok(dto)
                .header("ETag", ETagUtils.calcular(dto))
                .build();
    }

    @POST
    @RolesAllowed("ADMINISTRADOR")
    @Operation(summary = "Crea un nuevo recurso")
    @APIResponses({
        @APIResponse(responseCode = "201", description = "Recurso creado; devuelve la ubicación en Location."),
        @APIResponse(responseCode = "400", description = "Solicitud inválida o cuerpo mal formado."),
        @APIResponse(responseCode = "409", description = "Conflicto con el estado actual del recurso."),
        @APIResponse(responseCode = "422", description = "Datos de entrada que no cumplen las validaciones."),
        @APIResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public Response crear(@Valid D dto) {
        E entidad = getMapper().toEntity(dto);
        asignarNuevoId(entidad);
        getService().crear(entidad);
        D creada = getMapper().toDTO(entidad);
        URI ubicacion = UriBuilder.fromResource(getClass())
                .path("{id}")
                .build(getId(creada));
        return Response.created(ubicacion).entity(creada).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("ADMINISTRADOR")
    @Operation(summary = "Actualiza un recurso existente usando If-Match")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Recurso actualizado correctamente; incluye el encabezado ETag."),
        @APIResponse(responseCode = "400", description = "Solicitud inválida o cuerpo mal formado."),
        @APIResponse(responseCode = "404", description = "El recurso no existe."),
        @APIResponse(responseCode = "409", description = "Conflicto con el estado actual del recurso."),
        @APIResponse(responseCode = "412", description = "El ETag enviado no coincide con el estado actual."),
        @APIResponse(responseCode = "422", description = "Datos de entrada que no cumplen las validaciones."),
        @APIResponse(responseCode = "428", description = "Falta el encabezado If-Match."),
        @APIResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public Response actualizar(@PathParam("id") String id,
            @HeaderParam("If-Match") String ifMatch,
            @Valid D dto) {
        ID clave = parseId(id);
        E entidad = getService().actualizarSi(clave,
                e -> {
                    if (ifMatch == null || ifMatch.isBlank()) {
                        throw new PreconditionRequiredException(
                                "Debe enviar el encabezado If-Match con el ETag actual del recurso.");
                    }
                    return ETagUtils.coincide(ifMatch, ETagUtils.calcular(getMapper().toDTO(e)));
                },
                e -> {
                    getMapper().copiar(dto, e);
                    setId(e, clave);
                });
        D actualizada = getMapper().toDTO(entidad);
        return Response.ok(actualizada)
                .header("ETag", ETagUtils.calcularFuerte(actualizada))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMINISTRADOR")
    @Operation(summary = "Elimina un recurso de forma física o lógica según el recurso")
    @APIResponses({
        @APIResponse(responseCode = "204", description = "Recurso eliminado correctamente."),
        @APIResponse(responseCode = "404", description = "El recurso no existe."),
        @APIResponse(responseCode = "409", description = "Conflicto al eliminar el recurso."),
        @APIResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public Response eliminar(@PathParam("id") String id) {
        E entidad = getService().findById(parseId(id));
        if (entidad == null) {
            throw new NotFoundException("No existe el recurso solicitado.");
        }
        if (borradoLogico()) {
            marcarInactivo(entidad);
            getService().actualizar(entidad);
        } else {
            getService().eliminar(entidad);
        }
        return Response.noContent().build();
    }
}

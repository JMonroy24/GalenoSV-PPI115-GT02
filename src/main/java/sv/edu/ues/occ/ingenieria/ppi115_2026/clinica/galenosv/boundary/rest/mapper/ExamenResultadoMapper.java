package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.EntityMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ExamenResultadoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.OrdenExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenResultado;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.OrdenExamen;

@ApplicationScoped
public class ExamenResultadoMapper implements EntityMapper<ExamenResultado, ExamenResultadoDTO> {

    @Inject
    OrdenExamenService ordenExamenService;

    @Override
    public ExamenResultadoDTO toDTO(ExamenResultado entidad) {
        if (entidad == null) {
            return null;
        }
        return new ExamenResultadoDTO(
                entidad.getIdExamenResultado(),
                entidad.getFechaCreacion(),
                entidad.getResultado(),
                entidad.getInterpretacion(),
                entidad.getRutaAtestado(),
                entidad.getIdOrdenExamen() == null ? null : entidad.getIdOrdenExamen().getIdOrdenExamen());
    }

    @Override
    public ExamenResultado toEntity(ExamenResultadoDTO dto) {
        if (dto == null) {
            return null;
        }
        ExamenResultado entidad = new ExamenResultado();
        entidad.setIdExamenResultado(dto.id());
        copiar(dto, entidad);
        return entidad;
    }

    @Override
    public void copiar(ExamenResultadoDTO dto, ExamenResultado destino) {
        if (dto == null || destino == null) {
            return;
        }
        destino.setResultado(dto.resultado());
        destino.setInterpretacion(dto.interpretacion());
        destino.setRutaAtestado(dto.rutaAtestado());
        destino.setIdOrdenExamen(resolverOrdenExamen(dto.idOrdenExamen()));
    }

    private OrdenExamen resolverOrdenExamen(UUID id) {
        if (id == null) {
            return null;
        }
        OrdenExamen ordenExamen = ordenExamenService.findById(id);
        if (ordenExamen == null) {
            throw new NotFoundException("No existe la orden de examen solicitada.");
        }
        return ordenExamen;
    }
}

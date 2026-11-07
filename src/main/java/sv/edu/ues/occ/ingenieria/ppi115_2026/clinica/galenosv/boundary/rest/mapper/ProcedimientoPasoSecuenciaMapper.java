package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.EntityMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ProcedimientoPasoSecuenciaDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoSecuencia;

@ApplicationScoped
public class ProcedimientoPasoSecuenciaMapper
        implements EntityMapper<ProcedimientoPasoSecuencia, ProcedimientoPasoSecuenciaDTO> {

    @Inject
    ProcedimientoPasoService procedimientoPasoService;

    @Override
    public ProcedimientoPasoSecuenciaDTO toDTO(ProcedimientoPasoSecuencia entidad) {
        if (entidad == null) {
            return null;
        }
        return new ProcedimientoPasoSecuenciaDTO(
                entidad.getIdProcedimientoPasoSecuencia(),
                entidad.getIdProcedimientoPasoReferencia(),
                entidad.getTipoSecuencia(),
                entidad.getIdProcedimientoPaso() == null
                        ? null
                        : entidad.getIdProcedimientoPaso().getIdProcedimientoPaso());
    }

    @Override
    public ProcedimientoPasoSecuencia toEntity(ProcedimientoPasoSecuenciaDTO dto) {
        if (dto == null) {
            return null;
        }
        ProcedimientoPasoSecuencia entidad = new ProcedimientoPasoSecuencia();
        entidad.setIdProcedimientoPasoSecuencia(dto.id());
        copiar(dto, entidad);
        return entidad;
    }

    @Override
    public void copiar(ProcedimientoPasoSecuenciaDTO dto, ProcedimientoPasoSecuencia destino) {
        if (dto == null || destino == null) {
            return;
        }
        destino.setIdProcedimientoPasoReferencia(dto.idProcedimientoPasoReferencia());
        destino.setTipoSecuencia(dto.tipoSecuencia());
        destino.setIdProcedimientoPaso(resolverPaso(dto.idProcedimientoPaso()));
    }

    private ProcedimientoPaso resolverPaso(UUID id) {
        if (id == null) {
            return null;
        }
        ProcedimientoPaso paso = procedimientoPasoService.findById(id);
        if (paso == null) {
            throw new NotFoundException("No existe el paso de procedimiento solicitado.");
        }
        return paso;
    }
}

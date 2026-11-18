package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.EntityMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.OrdenExamenDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Examen;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.OrdenExamen;

@ApplicationScoped
public class OrdenExamenMapper implements EntityMapper<OrdenExamen, OrdenExamenDTO> {

    @Inject
    ConsultaProcedimientoPasoService consultaProcedimientoPasoService;

    @Inject
    ExamenService examenService;

    @Override
    public OrdenExamenDTO toDTO(OrdenExamen entidad) {
        if (entidad == null) {
            return null;
        }
        return new OrdenExamenDTO(
                entidad.getIdOrdenExamen(),
                entidad.getFechaCreacion(),
                entidad.getIndicaciones(),
                entidad.getIdConsultaProcedimientoPaso() == null
                        ? null
                        : entidad.getIdConsultaProcedimientoPaso().getIdConsultaProcedimientoPaso(),
                entidad.getIdExamen() == null ? null : entidad.getIdExamen().getIdExamen());
    }

    @Override
    public OrdenExamen toEntity(OrdenExamenDTO dto) {
        if (dto == null) {
            return null;
        }
        OrdenExamen entidad = new OrdenExamen();
        entidad.setIdOrdenExamen(dto.id());
        copiar(dto, entidad);
        return entidad;
    }

    @Override
    public void copiar(OrdenExamenDTO dto, OrdenExamen destino) {
        if (dto == null || destino == null) {
            return;
        }
        destino.setIndicaciones(dto.indicaciones());
        destino.setIdConsultaProcedimientoPaso(resolverPaso(dto.idConsultaProcedimientoPaso()));
        destino.setIdExamen(resolverExamen(dto.idExamen()));
    }

    private Examen resolverExamen(UUID id) {
        if (id == null) {
            return null;
        }
        Examen examen = examenService.findById(id);
        if (examen == null) {
            throw new NotFoundException("No existe el examen solicitado.");
        }
        return examen;
    }

    private ConsultaProcedimientoPaso resolverPaso(UUID id) {
        if (id == null) {
            return null;
        }
        ConsultaProcedimientoPaso paso = consultaProcedimientoPasoService.findById(id);
        if (paso == null) {
            throw new NotFoundException("No existe el paso de consulta solicitado.");
        }
        return paso;
    }
}

package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.EntityMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ProcedimientoPasoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.RolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Procedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Rol;

@ApplicationScoped
public class ProcedimientoPasoMapper implements EntityMapper<ProcedimientoPaso, ProcedimientoPasoDTO> {

    @Inject
    ProcedimientoService procedimientoService;

    @Inject
    RolService rolService;

    @Override
    public ProcedimientoPasoDTO toDTO(ProcedimientoPaso entidad) {
        if (entidad == null) {
            return null;
        }
        return new ProcedimientoPasoDTO(
                entidad.getIdProcedimientoPaso(),
                entidad.getNombre(),
                entidad.getIndicaFin(),
                entidad.getIdProcedimiento() == null ? null : entidad.getIdProcedimiento().getIdProcedimiento(),
                entidad.getIdRol() == null ? null : entidad.getIdRol().getIdRol());
    }

    @Override
    public ProcedimientoPaso toEntity(ProcedimientoPasoDTO dto) {
        if (dto == null) {
            return null;
        }
        ProcedimientoPaso entidad = new ProcedimientoPaso();
        entidad.setIdProcedimientoPaso(dto.id());
        copiar(dto, entidad);
        return entidad;
    }

    @Override
    public void copiar(ProcedimientoPasoDTO dto, ProcedimientoPaso destino) {
        if (dto == null || destino == null) {
            return;
        }
        destino.setNombre(dto.nombre());
        destino.setIndicaFin(dto.indicaFin());
        destino.setIdProcedimiento(resolverProcedimiento(dto.idProcedimiento()));
        destino.setIdRol(resolverRol(dto.idRol()));
    }

    private Procedimiento resolverProcedimiento(UUID id) {
        if (id == null) {
            return null;
        }
        Procedimiento procedimiento = procedimientoService.findById(id);
        if (procedimiento == null) {
            throw new NotFoundException("No existe el procedimiento solicitado.");
        }
        return procedimiento;
    }

    private Rol resolverRol(UUID id) {
        if (id == null) {
            return null;
        }
        Rol rol = rolService.findById(id);
        if (rol == null) {
            throw new NotFoundException("No existe el rol solicitado.");
        }
        return rol;
    }
}

package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ConsultaDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ConsultaDetalleDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ConsultaPasoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimientoPaso;

@ApplicationScoped
public class ConsultaDetalleMapper {

    @Inject
    ConsultaMapper consultaMapper;

    @Inject
    ConsultaProcedimientoService consultaProcedimientoService;

    @Inject
    ConsultaProcedimientoPasoService consultaProcedimientoPasoService;

    public ConsultaDetalleDTO toDTO(Consulta entidad) {
        if (entidad == null) {
            return null;
        }
        ConsultaDTO base = consultaMapper.toDTO(entidad);
        List<ConsultaProcedimientoPaso> pasos = new ArrayList<>();
        consultaProcedimientoService.findByConsulta(entidad.getIdConsulta())
                .forEach(procedimiento -> pasos.addAll(
                        consultaProcedimientoPasoService.findByConsultaProcedimiento(
                                procedimiento.getIdConsultaProcedimiento())));
        pasos.sort(Comparator
                .comparing(ConsultaProcedimientoPaso::getFechaInicio,
                        Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(paso -> paso.getIdConsultaProcedimientoPaso() == null
                        ? "" : paso.getIdConsultaProcedimientoPaso().toString()));
        List<ConsultaPasoDTO> detalles = new ArrayList<>();
        for (int i = 0; i < pasos.size(); i++) {
            detalles.add(toPaso(pasos.get(i), i + 1));
        }
        return new ConsultaDetalleDTO(
                base.id(), base.fechaInicio(), base.fechaFin(), base.referenciaExterna(),
                base.observaciones(), base.idPersonaRol(), detalles);
    }

    private ConsultaPasoDTO toPaso(ConsultaProcedimientoPaso paso, int orden) {
        String nombre = null;
        if (paso.getIdProcedimientoPaso() != null) {
            nombre = paso.getIdProcedimientoPaso().getNombre();
        } else if (paso.getIdConsultaProcedimiento() != null
                && paso.getIdConsultaProcedimiento().getIdProcedimiento() != null) {
            nombre = paso.getIdConsultaProcedimiento().getIdProcedimiento().getNombre();
        }
        return new ConsultaPasoDTO(
                paso.getIdConsultaProcedimientoPaso(),
                nombre,
                orden,
                paso.getEstado(),
                paso.getValor(),
                paso.getIdProcedimientoPaso() == null
                        ? null : paso.getIdProcedimientoPaso().getIdProcedimientoPaso());
    }
}

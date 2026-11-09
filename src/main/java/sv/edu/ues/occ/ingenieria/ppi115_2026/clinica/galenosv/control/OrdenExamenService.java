package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.OrdenExamen;

@ApplicationScoped
public class OrdenExamenService extends DefaultService<OrdenExamen, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected OrdenExamenDAO ordenExamenDAO;

    public OrdenExamenService() {
    }

    public OrdenExamenService(OrdenExamenDAO ordenExamenDAO) {
        this.ordenExamenDAO = ordenExamenDAO;
    }

    @Override
    protected DefaultDAO<OrdenExamen, UUID> getDAO() {
        return ordenExamenDAO;
    }

    @Override
    protected void validar(OrdenExamen r, boolean nuevo) {
        if (nuevo && r.getFechaCreacion() == null) {
            r.setFechaCreacion(new Date());
        }
        r.setIndicaciones(ValidadorComun.textoObligatorio(r.getIndicaciones(), "Las indicaciones"));
        ValidadorComun.requerido(r.getIdConsultaProcedimientoPaso(), "Seleccione un paso de la consulta.");
    }

    @Override
    public List<OrdenExamen> buscarParaAutocompletar(String filtro, int max) {
        return ordenExamenDAO.buscarParaAutocompletar(filtro, max);
    }

    public OrdenExamenDAO getOrdenExamenDAO() {
        return ordenExamenDAO;
    }

    public void setOrdenExamenDAO(OrdenExamenDAO ordenExamenDAO) {
        this.ordenExamenDAO = ordenExamenDAO;
    }
}

package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoExamen;

/**
 * Servicio de negocio para la gestión de tipos de examen.
 */
@ApplicationScoped
public class TipoExamenService extends DefaultService<TipoExamen, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected TipoExamenDAO tipoExamenDAO;

    public TipoExamenService() {
    }

    public TipoExamenService(TipoExamenDAO tipoExamenDAO) {
        this.tipoExamenDAO = tipoExamenDAO;
    }

    @Override
    protected DefaultDAO<TipoExamen, UUID> getDAO() {
        return tipoExamenDAO;
    }

    @Override
    protected void validar(TipoExamen r, boolean nuevo) {
        r.setNombre(ValidadorComun.textoObligatorio(r.getNombre(), "El nombre"));
        if (tipoExamenDAO.existePorCampo("nombre", r.getNombre(), r.getIdTipoExamen())) {
            throw new ValidacionNegocioException("Ya existe un registro con este nombre.");
        }
    }

    public List<TipoExamen> getTiposActivos() {
        return findAllActivos();
    }

    public List<TipoExamen> findAllActivos() {
        return tipoExamenDAO.findAllActivos();
    }

    public TipoExamenDAO getTipoExamenDAO() {
        return tipoExamenDAO;
    }

    public void setTipoExamenDAO(TipoExamenDAO tipoExamenDAO) {
        this.tipoExamenDAO = tipoExamenDAO;
    }
}

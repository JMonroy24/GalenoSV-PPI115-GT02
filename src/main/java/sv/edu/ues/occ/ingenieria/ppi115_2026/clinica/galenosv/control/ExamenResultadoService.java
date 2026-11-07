package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenResultado;

@ApplicationScoped
public class ExamenResultadoService extends DefaultService<ExamenResultado, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected ExamenResultadoDAO examenResultadoDAO;

    public ExamenResultadoService() {
    }

    public ExamenResultadoService(ExamenResultadoDAO examenResultadoDAO) {
        this.examenResultadoDAO = examenResultadoDAO;
    }

    @Override
    protected DefaultDAO<ExamenResultado, UUID> getDAO() {
        return examenResultadoDAO;
    }

    @Override
    protected void validar(ExamenResultado r, boolean nuevo) {
        r.setResultado(ValidadorComun.textoObligatorio(r.getResultado(), "El resultado"));
        r.setInterpretacion(ValidadorComun.textoObligatorio(r.getInterpretacion(), "La interpretación"));
        ValidadorComun.requerido(r.getIdOrdenExamen(), "Seleccione una orden de examen.");
    }

    public List<ExamenResultado> findByOrdenExamen(UUID idOrdenExamen) {
        if (idOrdenExamen == null) {
            return List.of();
        }
        return examenResultadoDAO.findByOrdenExamen(idOrdenExamen);
    }

    public ExamenResultadoDAO getExamenResultadoDAO() {
        return examenResultadoDAO;
    }

    public void setExamenResultadoDAO(ExamenResultadoDAO examenResultadoDAO) {
        this.examenResultadoDAO = examenResultadoDAO;
    }
}

package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ValidacionNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ValidadorComun;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenResultadoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.OrdenExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenResultado;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.OrdenExamen;

/**
 * Backing bean JSF para la gestión de la entidad ExamenResultado.
 */
@Named("examenResultadoModel")
@ViewScoped
public class ExamenResultadoModel extends ModelTransaccional<ExamenResultado, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected ExamenResultadoDAO examenResultadoDAO;

    @Inject
    protected OrdenExamenDAO ordenExamenDAO;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
    }

    @Override
    protected DAOInterface<ExamenResultado, UUID> getDAO() {
        return examenResultadoDAO;
    }

    @Override
    protected ExamenResultado crearNuevoRegistro() {
        ExamenResultado er = new ExamenResultado(UUID.randomUUID());
        er.setFechaCreacion(new Date());
        return er;
    }

    /** Método para p:autoComplete. Busca órdenes de examen. */
    public List<OrdenExamen> completeOrdenExamen(String query) {
        if (query == null || query.trim().length() < 2) return java.util.List.of();
        return ordenExamenDAO.buscarParaAutocompletar(query, 20);
    }

    public ExamenResultadoDAO getExamenResultadoDAO() {
        return examenResultadoDAO;
    }

    public void setExamenResultadoDAO(ExamenResultadoDAO examenResultadoDAO) {
        this.examenResultadoDAO = examenResultadoDAO;
    }

    @Override
    protected void validarNegocio(ExamenResultado registro) {
        registro.setResultado(ValidadorComun.textoObligatorio(registro.getResultado(), "El resultado"));
        registro.setInterpretacion(ValidadorComun.textoObligatorio(registro.getInterpretacion(), "La interpretación"));
        ValidadorComun.requerido(registro.getIdOrdenExamen(), "Seleccione una orden de examen.");
    }

}

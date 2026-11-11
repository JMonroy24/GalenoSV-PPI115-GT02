package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenResultadoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.OrdenExamenService;
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
    protected ExamenResultadoService examenResultadoService;

    @Inject
    protected OrdenExamenService ordenExamenService;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
    }

    @Override
    protected DAOInterface<ExamenResultado, UUID> getDAO() {
        return examenResultadoService;
    }

    @Override
    protected ExamenResultado crearNuevoRegistro() {
        ExamenResultado er = new ExamenResultado(UUID.randomUUID());
        er.setFechaCreacion(new Date());
        return er;
    }

    /** Método para p:autoComplete. Busca órdenes de examen. */
    public List<OrdenExamen> completeOrdenExamen(String query) {
        return completar(query, texto -> ordenExamenService.buscarParaAutocompletar(texto, 20));
    }

    public ExamenResultadoService getExamenResultadoService() {
        return examenResultadoService;
    }

    public void setExamenResultadoService(ExamenResultadoService examenResultadoService) {
        this.examenResultadoService = examenResultadoService;
    }


}

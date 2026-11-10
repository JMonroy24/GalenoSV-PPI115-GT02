package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.TipoExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoExamen;

/**
 * Backing bean JSF para la gestión de la entidad TipoExamen.
 */
@Named("tipoExamenModel")
@ViewScoped
public class TipoExamenModel extends ModelTransaccional<TipoExamen, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected TipoExamenService tipoExamenService;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
    }

    @Override
    protected DAOInterface<TipoExamen, UUID> getDAO() {
        return tipoExamenService;
    }

    @Override
    protected TipoExamen crearNuevoRegistro() {
        TipoExamen registro = new TipoExamen(UUID.randomUUID());
        registro.setActivo(true);
        return registro;
    }

    /**
     * Fuente de opciones para selectores de FK en otras vistas.
     * Retorna solo los tipos de examen activos, sin cargar el modelo completo.
     *
     * @return lista de TipoExamen activos ordenados por nombre
     */
    public java.util.List<TipoExamen> getTiposActivos() {
        return tipoExamenService.getTiposActivos();
    }

    public TipoExamenService getTipoExamenService() {
        return tipoExamenService;
    }

    public void setTipoExamenService(TipoExamenService tipoExamenService) {
        this.tipoExamenService = tipoExamenService;
    }

    public List<TipoExamen> completeTipoExamen(String query) {
        return completar(query, 1, texto -> tipoExamenService.buscarParaAutocompletar(texto, 20));
    }
}

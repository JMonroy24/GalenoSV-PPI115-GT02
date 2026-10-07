package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ValidacionNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ValidadorComun;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.TipoExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoExamen;

/**
 * Backing bean JSF para la gestión de la entidad TipoExamen.
 */
@Named("tipoExamenModel")
@ViewScoped
public class TipoExamenModel extends ModelTransaccional<TipoExamen, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected TipoExamenDAO tipoExamenDAO;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
    }

    @Override
    protected DAOInterface<TipoExamen, UUID> getDAO() {
        return tipoExamenDAO;
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
        return tipoExamenDAO.findAllActivos();
    }

    public TipoExamenDAO getTipoExamenDAO() {
        return tipoExamenDAO;
    }

    public void setTipoExamenDAO(TipoExamenDAO tipoExamenDAO) {
        this.tipoExamenDAO = tipoExamenDAO;
    }

    @Override
    protected void validarNegocio(TipoExamen registro) {
        registro.setNombre(ValidadorComun.textoObligatorio(registro.getNombre(), "El nombre"));
        if (tipoExamenDAO.existePorCampo("nombre", registro.getNombre(), registro.getIdTipoExamen())) {
            throw new ValidacionNegocioException("Ya existe un registro con este nombre.");
        }
    }

}

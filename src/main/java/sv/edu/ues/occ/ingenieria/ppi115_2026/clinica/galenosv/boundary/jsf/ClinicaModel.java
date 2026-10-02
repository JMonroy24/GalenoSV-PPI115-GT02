package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ValidacionNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ValidadorComun;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ClinicaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Clinica;

/**
 * Backing bean JSF para la gestión de la entidad Clinica.
 */
@Named("clinicaModel")
@ViewScoped
public class ClinicaModel extends ModelTransaccional<Clinica, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected ClinicaDAO clinicaDAO;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
    }

    @Override
    protected DAOInterface<Clinica, UUID> getDAO() {
        return clinicaDAO;
    }

    @Override
    protected Clinica crearNuevoRegistro() {
        return new Clinica(UUID.randomUUID());
    }

    public ClinicaDAO getClinicaDAO() {
        return clinicaDAO;
    }

    public void setClinicaDAO(ClinicaDAO clinicaDAO) {
        this.clinicaDAO = clinicaDAO;
    }

    @Override
    protected void validarNegocio(Clinica registro) {
        registro.setNombre(ValidadorComun.textoObligatorio(registro.getNombre(), "El nombre"));
        if (clinicaDAO.existePorCampo("nombre", registro.getNombre(), registro.getIdClinica())) {
            throw new ValidacionNegocioException("Ya existe un registro con este nombre.");
        }
    }

    public java.util.List<Clinica> getActivos() {
        return clinicaDAO.findAllActivos();
    }

}

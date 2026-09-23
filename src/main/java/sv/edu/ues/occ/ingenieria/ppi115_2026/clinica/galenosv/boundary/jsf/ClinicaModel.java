package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

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
public class ClinicaModel extends Model<Clinica, UUID> implements Serializable {

    @Inject
    protected ClinicaDAO clinicaDAO;

    @PostConstruct
    public void init() {
        cargarDatos();
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
}

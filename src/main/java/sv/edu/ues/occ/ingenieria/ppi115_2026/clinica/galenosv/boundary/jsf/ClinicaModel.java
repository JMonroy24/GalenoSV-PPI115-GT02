package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ClinicaService;
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
    protected ClinicaService clinicaService;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
    }

    @Override
    protected DAOInterface<Clinica, UUID> getDAO() {
        return clinicaService;
    }

    @Override
    protected Clinica crearNuevoRegistro() {
        Clinica registro = new Clinica(UUID.randomUUID());
        registro.setActivo(true);
        return registro;
    }

    public ClinicaService getClinicaService() {
        return clinicaService;
    }

    public void setClinicaService(ClinicaService clinicaService) {
        this.clinicaService = clinicaService;
    }

    public java.util.List<Clinica> getActivos() {
        return clinicaService.getActivos();
    }
}

package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.MedioContactoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.MedioContacto;

/**
 * Backing bean JSF para la gestión de la entidad MedioContacto.
 */
@Named("medioContactoModel")
@ViewScoped
public class MedioContactoModel extends ModelTransaccional<MedioContacto, UUID> implements Serializable {

    @Inject
    protected MedioContactoDAO medioContactoDAO;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
    }

    @Override
    protected DAOInterface<MedioContacto, UUID> getDAO() {
        return medioContactoDAO;
    }

    @Override
    protected MedioContacto crearNuevoRegistro() {
        return new MedioContacto();
    }

    public MedioContactoDAO getMedioContactoDAO() {
        return medioContactoDAO;
    }

    public void setMedioContactoDAO(MedioContactoDAO medioContactoDAO) {
        this.medioContactoDAO = medioContactoDAO;
    }
}

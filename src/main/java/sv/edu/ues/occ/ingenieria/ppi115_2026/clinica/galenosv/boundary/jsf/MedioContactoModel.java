package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.MedioContactoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.MedioContacto;

/**
 * Backing bean JSF para la gestión de la entidad MedioContacto.
 */
@Named("medioContactoModel")
@ViewScoped
public class MedioContactoModel extends ModelTransaccional<MedioContacto, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected MedioContactoService medioContactoService;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
    }

    @Override
    protected DAOInterface<MedioContacto, UUID> getDAO() {
        return medioContactoService;
    }

    @Override
    protected MedioContacto crearNuevoRegistro() {
        return new MedioContacto(UUID.randomUUID());
    }

    public MedioContactoService getMedioContactoService() {
        return medioContactoService;
    }

    public void setMedioContactoService(MedioContactoService medioContactoService) {
        this.medioContactoService = medioContactoService;
    }

    @Inject
    protected PersonaService personaService;

    public java.util.List<sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona> completePersona(String query) {
        if (query == null || query.trim().length() < 2) return java.util.List.of();
        return personaService.buscarParaAutocompletar(query, 10);
    }

}

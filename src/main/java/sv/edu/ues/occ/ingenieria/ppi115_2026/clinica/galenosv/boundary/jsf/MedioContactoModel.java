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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.MedioContactoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.MedioContacto;

/**
 * Backing bean JSF para la gestión de la entidad MedioContacto.
 */
@Named("medioContactoModel")
@ViewScoped
public class MedioContactoModel extends ModelTransaccional<MedioContacto, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

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
        return new MedioContacto(UUID.randomUUID());
    }

    public MedioContactoDAO getMedioContactoDAO() {
        return medioContactoDAO;
    }

    public void setMedioContactoDAO(MedioContactoDAO medioContactoDAO) {
        this.medioContactoDAO = medioContactoDAO;
    }

    @Inject
    protected sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaDAO personaDAO;

    public java.util.List<sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona> completePersona(String query) {
        if (query == null || query.trim().length() < 2) return java.util.List.of();
        return personaDAO.buscarParaAutocompletar(query, 10);
    }


    @Override
    protected void validarNegocio(MedioContacto registro) {
        registro.setValor(ValidadorComun.textoObligatorio(registro.getValor(), "El contacto"));
        var persona = ValidadorComun.requerido(registro.getIdPersona(), "Seleccione una persona.");
        var tipo = ValidadorComun.requerido(registro.getIdTipoMedioContacto(), "Seleccione un tipo de contacto.");
        ValidadorComun.requerido(persona.getIdPersona(), "Seleccione una persona válida.");
        ValidadorComun.requerido(tipo.getIdTipoMedioContacto(), "Seleccione un tipo de contacto válido.");
        ValidadorComun.activo(tipo.getActivo(), "El tipo de contacto");
        ValidadorComun.formato(registro.getValor(), tipo.getExpresionRegular(), tipo.getIndicaciones());
        if (medioContactoDAO.existePersonaTipoValor(persona.getIdPersona(), tipo.getIdTipoMedioContacto(),
                registro.getValor(), registro.getIdMedioContacto())) {
            throw new ValidacionNegocioException("Este contacto ya está registrado para la persona.");
        }
    }

}

package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.MedioContacto;

@ApplicationScoped
public class MedioContactoService extends DefaultService<MedioContacto, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected MedioContactoDAO medioContactoDAO;

    public MedioContactoService() {
    }

    public MedioContactoService(MedioContactoDAO medioContactoDAO) {
        this.medioContactoDAO = medioContactoDAO;
    }

    @Override
    protected DefaultDAO<MedioContacto, UUID> getDAO() {
        return medioContactoDAO;
    }

    @Override
    protected void validar(MedioContacto r, boolean nuevo) {
        if (nuevo && r.getFechaCreacion() == null) {
            r.setFechaCreacion(new Date());
        }
        r.setValor(ValidadorComun.textoObligatorio(r.getValor(), "El contacto"));
        var persona = ValidadorComun.requerido(r.getIdPersona(), "Seleccione una persona.");
        var tipo = ValidadorComun.requerido(r.getIdTipoMedioContacto(), "Seleccione un tipo de contacto.");
        ValidadorComun.requerido(persona.getIdPersona(), "Seleccione una persona válida.");
        ValidadorComun.requerido(tipo.getIdTipoMedioContacto(), "Seleccione un tipo de contacto válido.");
        ValidadorComun.activo(tipo.getActivo(), "El tipo de contacto");
        ValidadorComun.formato(r.getValor(), tipo.getExpresionRegular(), tipo.getIndicaciones());
        if (medioContactoDAO.existePersonaTipoValor(persona.getIdPersona(), tipo.getIdTipoMedioContacto(),
                r.getValor(), r.getIdMedioContacto())) {
            throw new ValidacionNegocioException("Este contacto ya está registrado para la persona.");
        }
    }

    public boolean existePersonaTipoValor(UUID idPersona, UUID idTipo, String valor, UUID excluirId) {
        return medioContactoDAO.existePersonaTipoValor(idPersona, idTipo, valor, excluirId);
    }

    public List<MedioContacto> findByPersona(UUID idPersona) {
        if (idPersona == null) {
            return List.of();
        }
        return medioContactoDAO.findByPersona(idPersona);
    }

    public MedioContactoDAO getMedioContactoDAO() {
        return medioContactoDAO;
    }

    public void setMedioContactoDAO(MedioContactoDAO medioContactoDAO) {
        this.medioContactoDAO = medioContactoDAO;
    }
}

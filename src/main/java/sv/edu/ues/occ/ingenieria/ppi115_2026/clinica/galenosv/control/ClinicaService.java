package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Clinica;

/**
 * Servicio de negocio para la gestión de clínicas.
 */
@ApplicationScoped
public class ClinicaService extends DefaultService<Clinica, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected ClinicaDAO clinicaDAO;

    public ClinicaService() {
    }

    public ClinicaService(ClinicaDAO clinicaDAO) {
        this.clinicaDAO = clinicaDAO;
    }

    @Override
    protected DefaultDAO<Clinica, UUID> getDAO() {
        return clinicaDAO;
    }

    @Override
    protected void validar(Clinica r, boolean nuevo) {
        r.setNombre(ValidadorComun.textoObligatorio(r.getNombre(), "El nombre"));
        r.setTipo(ValidadorComun.textoObligatorio(r.getTipo(), "El tipo de clínica"));
        if (clinicaDAO.existePorCampo("nombre", r.getNombre(), r.getIdClinica())) {
            throw new ValidacionNegocioException("Ya existe un registro con este nombre.");
        }
    }

    public List<Clinica> getActivos() {
        return findAllActivos();
    }

    public List<Clinica> findAllActivos() {
        return clinicaDAO.findAllActivos();
    }

    public ClinicaDAO getClinicaDAO() {
        return clinicaDAO;
    }

    public void setClinicaDAO(ClinicaDAO clinicaDAO) {
        this.clinicaDAO = clinicaDAO;
    }
}

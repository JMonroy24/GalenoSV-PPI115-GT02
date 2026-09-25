package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaRolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol;

/**
 * Backing bean JSF para la gestión de la entidad Consulta.
 */
@Named("consultaModel")
@ViewScoped
public class ConsultaModel extends ModelTransaccional<Consulta, UUID> implements Serializable {

    @Inject
    protected ConsultaDAO consultaDAO;

    @Inject
    protected PersonaRolDAO personaRolDAO;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
    }

    @Override
    protected DAOInterface<Consulta, UUID> getDAO() {
        return consultaDAO;
    }

    @Override
    protected Consulta crearNuevoRegistro() {
        Consulta consulta = new Consulta(UUID.randomUUID());
        consulta.setFechaInicio(new Date());
        return consulta;
    }

    /** Método para p:autoComplete. Busca asignaciones Persona/Rol. */
    public List<PersonaRol> completePersonaRol(String query) {
        return personaRolDAO.buscarParaAutocompletar(query, 20);
    }

    public ConsultaDAO getConsultaDAO() {
        return consultaDAO;
    }

    public void setConsultaDAO(ConsultaDAO consultaDAO) {
        this.consultaDAO = consultaDAO;
    }
}

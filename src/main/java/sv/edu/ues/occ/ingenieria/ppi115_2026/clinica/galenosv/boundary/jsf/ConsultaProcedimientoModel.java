package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ValidacionNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ValidadorComun;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimiento;

/**
 * Backing bean JSF para la gestión de la entidad ConsultaProcedimiento.
 */
@Named("consultaProcedimientoModel")
@ViewScoped
public class ConsultaProcedimientoModel extends ModelTransaccional<ConsultaProcedimiento, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected ConsultaProcedimientoDAO consultaProcedimientoDAO;

    @Inject
    protected ConsultaDAO consultaDAO;

    /**
     * Consulta recibida por URL (drill-down desde consulta.xhtml, parámetro
     * "idConsulta"). Cuando está presente, los registros nuevos que se
     * creen desde esta pantalla quedan preasignados a esa consulta.
     */
    private Consulta consultaPreseleccionada;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
        leerConsultaDesdeUrl();
    }

    private void leerConsultaDesdeUrl() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc == null) return;
        String parametro = fc.getExternalContext().getRequestParameterMap().get("idConsulta");
        if (parametro == null || parametro.isBlank()) return;
        try {
            consultaPreseleccionada = consultaDAO.findById(UUID.fromString(parametro.trim()));
        } catch (IllegalArgumentException ex) {
            consultaPreseleccionada = null;
        }
    }

    @Override
    protected DAOInterface<ConsultaProcedimiento, UUID> getDAO() {
        return consultaProcedimientoDAO;
    }

    @Override
    protected ConsultaProcedimiento crearNuevoRegistro() {
        ConsultaProcedimiento cp = new ConsultaProcedimiento(UUID.randomUUID());
        cp.setFechaInicio(new Date());
        if (consultaPreseleccionada != null) {
            cp.setIdConsulta(consultaPreseleccionada);
        }
        return cp;
    }

    /** Método para p:autoComplete. Busca consultas por persona o referencia. */
    public List<Consulta> completeConsulta(String query) {
        if (query == null || query.trim().length() < 2) return java.util.List.of();
        return consultaDAO.buscarParaAutocompletar(query, 20);
    }

    public ConsultaProcedimientoDAO getConsultaProcedimientoDAO() {
        return consultaProcedimientoDAO;
    }

    public void setConsultaProcedimientoDAO(ConsultaProcedimientoDAO consultaProcedimientoDAO) {
        this.consultaProcedimientoDAO = consultaProcedimientoDAO;
    }

    public Consulta getConsultaPreseleccionada() {
        return consultaPreseleccionada;
    }

    @Override
    protected void validarNegocio(ConsultaProcedimiento registro) {
        ValidadorComun.rangoFechas(registro.getFechaInicio(), registro.getFechaFin());
        if (registro.getIdConsulta() != null) {
            var consulta = registro.getIdConsulta();
            ValidadorComun.dentroDelPeriodo(registro.getFechaInicio(), registro.getFechaFin(),
                    consulta.getFechaInicio(), consulta.getFechaFin());
        }
        if (registro.getIdProcedimiento() != null) {
            ValidadorComun.activo(registro.getIdProcedimiento().getActivo(), "El procedimiento");
        }
    }

}

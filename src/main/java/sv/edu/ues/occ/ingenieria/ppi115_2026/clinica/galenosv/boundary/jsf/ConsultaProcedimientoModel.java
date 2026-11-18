package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.application.FacesMessage;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultService;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoService;
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

    @Override
    protected boolean mostrarDetalleError() { return true; }

    @Inject
    protected ConsultaProcedimientoService consultaProcedimientoService;

    @Inject
    protected ConsultaService consultaService;

    @Inject protected SesionBean sesionBean;

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
            consultaPreseleccionada = consultaService.findById(UUID.fromString(parametro.trim()));
        } catch (IllegalArgumentException ex) {
            consultaPreseleccionada = null;
            agregarMensaje(FacesMessage.SEVERITY_WARN,
                    "Consulta", "La referencia de consulta recibida no es válida.");
        }
    }

    @Override
    protected DAOInterface<ConsultaProcedimiento, UUID> getDAO() {
        return consultaProcedimientoService;
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
        // Validar antes de acceder incluso a la sesión o al servicio de consultas.
        String texto = DefaultService.normalizarFiltro(query);
        if (texto == null || texto.length() < 2) return List.of();
        var clinica = sesionBean == null ? null : sesionBean.getClinicaActual();
        if (clinica == null || clinica.getIdClinica() == null || !Boolean.TRUE.equals(clinica.getActivo())) {
            agregarMensaje(FacesMessage.SEVERITY_WARN,
                    "Consulta", "Seleccione una clínica activa antes de buscar consultas.");
            return List.of();
        }
        return completar(texto, filtro -> consultaService.buscarParaAutocompletar(filtro, 20, clinica.getIdClinica()));
    }

    public ConsultaProcedimientoService getConsultaProcedimientoService() {
        return consultaProcedimientoService;
    }

    public void setConsultaProcedimientoService(ConsultaProcedimientoService consultaProcedimientoService) {
        this.consultaProcedimientoService = consultaProcedimientoService;
    }

    public Consulta getConsultaPreseleccionada() {
        return consultaPreseleccionada;
    }

}

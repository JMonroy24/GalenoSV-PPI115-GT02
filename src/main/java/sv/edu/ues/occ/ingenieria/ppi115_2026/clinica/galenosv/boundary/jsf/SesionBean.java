package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ClinicaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.RolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaRolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Clinica;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Rol;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol;
import java.util.stream.Collectors;
import java.util.Collections;

@Named("sesionBean")
@SessionScoped
public class SesionBean implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Inject protected ClinicaDAO clinicaDAO;
    @Inject protected RolDAO rolDAO;
    @Inject protected PersonaRolDAO personaRolDAO;
    
    private Clinica clinicaActual;
    private Rol rolActual;
    private Persona personaActual;
    
    // Temporal variables for the form
    private Clinica clinicaTemp;
    private Rol rolTemp;
    private Persona personaTemp;

    public Clinica getClinicaActual() { return clinicaActual; }
    
    public void setClinicaActual(Clinica seleccionada) {
        if (seleccionada != null && !Boolean.TRUE.equals(seleccionada.getActivo())) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            if (contexto != null) {
                contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Clínica", "Seleccione una clínica activa."));
                contexto.validationFailed();
            }
            return;
        }
        this.clinicaActual = seleccionada;
    }


    public Rol getRolActual() { return rolActual; }
    public void setRolActual(Rol rolActual) { this.rolActual = rolActual; }

    public Persona getPersonaActual() { return personaActual; }
    public void setPersonaActual(Persona personaActual) { this.personaActual = personaActual; }

    public Clinica getClinicaTemp() { return clinicaTemp; }
    public void setClinicaTemp(Clinica clinicaTemp) { this.clinicaTemp = clinicaTemp; }

    public Rol getRolTemp() { return rolTemp; }
    public void setRolTemp(Rol rolTemp) { this.rolTemp = rolTemp; }

    public Persona getPersonaTemp() { return personaTemp; }
    public void setPersonaTemp(Persona personaTemp) { this.personaTemp = personaTemp; }

    public List<Clinica> getClinicasActivas() { return clinicaDAO.findAllActivos(); }
    public List<Rol> getRolesActivos() { return rolDAO.findAllActivos(); }
    
    public List<Persona> getPersonasDisponibles() {
        if (clinicaTemp == null || rolTemp == null) return Collections.emptyList();
        return personaRolDAO.findByClinicaAndRol(clinicaTemp.getIdClinica(), rolTemp.getIdRol())
            .stream().map(PersonaRol::getIdPersona).collect(Collectors.toList());
    }

    public void onChangeClinicaRol() {
        personaTemp = null;
    }

    public void aplicarCambios() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (clinicaTemp == null || rolTemp == null || personaTemp == null) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debe seleccionar clínica, rol y persona."));
            return;
        }
        clinicaActual = clinicaTemp;
        rolActual = rolTemp;
        personaActual = personaTemp;
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Rol aplicado correctamente."));
    }

    public boolean isClinicaSeleccionada() { return clinicaActual != null; }
    public String getNombreClinica() {
        return clinicaActual == null ? "Sin clínica seleccionada" : clinicaActual.getNombre();
    }
    
    public String getDisplayUsuario() {
        if (personaActual == null || rolActual == null) return "No autenticado";
        return personaActual.getNombres() + " " + personaActual.getApellidos() + " (" + rolActual.getNombre() + ")";
    }
}

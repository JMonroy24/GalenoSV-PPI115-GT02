package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ValidacionNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ValidadorComun;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ClinicaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DocumentoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.MedioContactoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaRolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.RolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.TipoDocumentoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.TipoMedioContactoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Clinica;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Documento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.MedioContacto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Rol;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoDocumento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoMedioContacto;

/**
 * Backing bean de Persona. Administra embebidos sus documentos,
 * medios de contacto y roles (PersonaRol).
 */
@Named("personaModel")
@ViewScoped
public class PersonaModel extends ModelTransaccional<Persona, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected PersonaDAO personaDAO;
    @Inject
    protected DocumentoDAO documentoDAO;
    @Inject
    protected MedioContactoDAO medioContactoDAO;
    @Inject
    protected PersonaRolDAO personaRolDAO;
    @Inject
    protected TipoDocumentoDAO tipoDocumentoDAO;
    @Inject
    protected TipoMedioContactoDAO tipoMedioContactoDAO;
    @Inject
    protected RolDAO rolDAO;
    @Inject
    protected ClinicaDAO clinicaDAO;

    // Relaciones de la persona en edición
    private List<Documento> documentos = Collections.emptyList();
    private List<MedioContacto> mediosContacto = Collections.emptyList();
    private List<PersonaRol> roles = Collections.emptyList();

    // Borradores enlazados a los formularios de "agregar"
    private Documento documentoNuevo;
    private MedioContacto medioNuevo;
    private PersonaRol rolNuevo;

    // Catálogos para los combos (cargados una sola vez por vista)
    private List<TipoDocumento> tiposDocumento;
    private List<TipoMedioContacto> tiposMedioContacto;
    private List<Rol> rolesActivos;
    private List<Clinica> clinicas;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
        limpiarBorradores();
    }

    @Override
    protected DAOInterface<Persona, UUID> getDAO() {
        return personaDAO;
    }

    @Override
    protected Persona crearNuevoRegistro() {
        return new Persona(UUID.randomUUID());
    }

    @Override
    public void seleccionar(Persona persona) {
        super.seleccionar(persona);
        recargarRelaciones();
        limpiarBorradores();
    }

    @Override
    public void prepararNuevo() {
        super.prepararNuevo();
        vaciarRelaciones();
        limpiarBorradores();
    }

    @Override
    public void cancelar() {
        super.cancelar();
        vaciarRelaciones();
        limpiarBorradores();
    }

    private void recargarRelaciones() {
        UUID id = getRegistroActual().getIdPersona();
        documentos = (List<Documento>) documentoDAO.findByPersona(id);
        mediosContacto = (List<MedioContacto>) medioContactoDAO.findByPersona(id);
        roles = personaRolDAO.findByPersona(id);
    }

    private void vaciarRelaciones() {
        documentos = Collections.emptyList();
        mediosContacto = Collections.emptyList();
        roles = Collections.emptyList();
    }

    private void limpiarBorradores() {
        documentoNuevo = new Documento(UUID.randomUUID());
        medioNuevo = new MedioContacto(UUID.randomUUID());
        rolNuevo = new PersonaRol(UUID.randomUUID());
    }

    private static boolean vacio(String s) {
        return s == null || s.isBlank();
    }


    public void agregarDocumento() {
        if (!relacionesHabilitadas("Documento")) {
            return;
        }
        if (documentoNuevo.getIdTipoDocumento() == null || vacio(documentoNuevo.getValor())) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Documento",
                    "Seleccione el tipo e ingrese el valor del documento.");
            return;
        }
        String valor = documentoNuevo.getValor().trim();
        boolean duplicado = documentos.stream().anyMatch(d
                -> Objects.equals(d.getIdTipoDocumento(), documentoNuevo.getIdTipoDocumento())
                && valor.equalsIgnoreCase(d.getValor()));
        if (duplicado) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Documento",
                    "La persona ya tiene un documento de ese tipo con ese valor.");
            return;
        }
        documentoNuevo.setValor(valor);
        documentoNuevo.setIdPersona(getRegistroActual());
        ejecutarRelacion("Documento", () -> {
            documentoDAO.create(documentoNuevo);
            documentos = (List<Documento>) documentoDAO.findByPersona(getRegistroActual().getIdPersona());
            documentoNuevo = new Documento(UUID.randomUUID());
        }, "Documento agregado correctamente.");
    }

    public void quitarDocumento(Documento documento) {
        if (!relacionesHabilitadas("Documento")) {
            return;
        }
        if (documento == null || !documentos.contains(documento)) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Documento",
                    "Seleccione un documento válido de la persona.");
            return;
        }
        ejecutarRelacion("Documento", () -> {
            documentoDAO.delete(documento);
            documentos = (List<Documento>) documentoDAO.findByPersona(getRegistroActual().getIdPersona());
        }, "Documento quitado correctamente.");
    }


    public void agregarMedioContacto() {
        if (!relacionesHabilitadas("Medio de contacto")) {
            return;
        }
        if (medioNuevo.getIdTipoMedioContacto() == null || vacio(medioNuevo.getValor())) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Medio de contacto",
                    "Seleccione el tipo e ingrese el valor del medio de contacto.");
            return;
        }
        medioNuevo.setValor(medioNuevo.getValor().trim());
        medioNuevo.setIdPersona(getRegistroActual());
        medioNuevo.setFechaCreacion(new Date());
        ejecutarRelacion("Medio de contacto", () -> {
            medioContactoDAO.create(medioNuevo);
            mediosContacto = (List<MedioContacto>) medioContactoDAO.findByPersona(getRegistroActual().getIdPersona());
            medioNuevo = new MedioContacto(UUID.randomUUID());
        }, "Medio de contacto agregado correctamente.");
    }

    public void quitarMedioContacto(MedioContacto medio) {
        if (!relacionesHabilitadas("Medio de contacto")) {
            return;
        }
        if (medio == null || !mediosContacto.contains(medio)) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Medio de contacto",
                    "Seleccione un medio de contacto válido de la persona.");
            return;
        }
        ejecutarRelacion("Medio de contacto", () -> {
            medioContactoDAO.delete(medio);
            mediosContacto = (List<MedioContacto>) medioContactoDAO.findByPersona(getRegistroActual().getIdPersona());
        }, "Medio de contacto quitado correctamente.");
    }


    public void agregarRol() {
        if (!relacionesHabilitadas("Rol")) {
            return;
        }
        if (rolNuevo.getIdRol() == null) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Rol", "Seleccione un rol.");
            return;
        }
        boolean duplicado = roles.stream().anyMatch(r
                -> Objects.equals(r.getIdRol(), rolNuevo.getIdRol())
                && Objects.equals(r.getIdClinica(), rolNuevo.getIdClinica()));
        if (duplicado) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Rol",
                    "La persona ya tiene ese rol en esa clínica.");
            return;
        }
        rolNuevo.setIdPersona(getRegistroActual());
        rolNuevo.setFechaCreacion(new Date());
        ejecutarRelacion("Rol", () -> {
            personaRolDAO.create(rolNuevo);
            roles = personaRolDAO.findByPersona(getRegistroActual().getIdPersona());
            rolNuevo = new PersonaRol(UUID.randomUUID());
        }, "Rol asignado correctamente.");
    }

    public void quitarRol(PersonaRol rol) {
        if (!relacionesHabilitadas("Rol")) {
            return;
        }
        if (rol == null || !roles.contains(rol)) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Rol",
                    "Seleccione un rol válido de la persona.");
            return;
        }
        ejecutarRelacion("Rol", () -> {
            personaRolDAO.delete(rol);
            roles = personaRolDAO.findByPersona(getRegistroActual().getIdPersona());
        }, "Rol quitado correctamente.");
    }


    public List<TipoDocumento> getTiposDocumento() {
        if (tiposDocumento == null) {
            tiposDocumento = tipoDocumentoDAO.findAll();
        }
        return tiposDocumento;
    }

    public List<TipoMedioContacto> getTiposMedioContacto() {
        if (tiposMedioContacto == null) {
            tiposMedioContacto = tipoMedioContactoDAO.findAll();
        }
        return tiposMedioContacto;
    }

    public List<Rol> getRolesActivos() {
        if (rolesActivos == null) {
            rolesActivos = rolDAO.findAllActivos();
        }
        return rolesActivos;
    }

    public List<Clinica> getClinicas() {
        if (clinicas == null) {
            clinicas = clinicaDAO.findAll();
        }
        return clinicas;
    }


    public List<Documento> getDocumentos() { return documentos; }
    public List<MedioContacto> getMediosContacto() { return mediosContacto; }
    public List<PersonaRol> getRoles() { return roles; }

    public Documento getDocumentoNuevo() { return documentoNuevo; }
    public MedioContacto getMedioNuevo() { return medioNuevo; }
    public PersonaRol getRolNuevo() { return rolNuevo; }

    public PersonaDAO getPersonaDAO() {
        return personaDAO;
    }

    public void setPersonaDAO(PersonaDAO personaDAO) {
        this.personaDAO = personaDAO;
    }

    @Override
    protected void validarNegocio(Persona registro) {
        registro.setNombres(ValidadorComun.textoObligatorio(registro.getNombres(), "Los nombres"));
        registro.setApellidos(ValidadorComun.textoObligatorio(registro.getApellidos(), "Los apellidos"));
        if (registro.getFechaNacimiento() != null && !registro.getFechaNacimiento().isBefore(getHoy())) {
            throw new ValidacionNegocioException("La fecha de nacimiento debe estar en el pasado.");
        }
    }

    public java.time.LocalDate getHoy() {
        return java.time.LocalDate.now(java.time.ZoneId.of("America/El_Salvador"));
    }

}

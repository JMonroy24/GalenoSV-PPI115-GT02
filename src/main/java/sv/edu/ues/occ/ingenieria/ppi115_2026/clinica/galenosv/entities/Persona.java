package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;

import java.util.List;
import java.util.UUID;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

@Entity
@Table(name = "persona")
public class Persona implements IdentificableEntity {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "{identificador.obligatorio}")
    @Id
    @Basic(optional = false)
    @Column(name = "id_persona")
    private UUID idPersona;

    @NotBlank(message = "{persona.nombres.obligatorio}")
    @Size(max = 255, message = "{persona.nombres.longitud}")
    @Column(name = "nombres", nullable = false, length = 255)
    private String nombres;

    @NotBlank(message = "{persona.apellidos.obligatorio}")
    @Size(max = 255, message = "{persona.apellidos.longitud}")
    @Column(name = "apellidos", nullable = false, length = 255)
    private String apellidos;

    @Past(message = "{persona.fechaNacimiento.pasada}")
    @Column(name = "fecha_nacimiento")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaNacimiento;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @OneToMany(mappedBy = "idPersona", fetch = FetchType.LAZY)
    private List<MedioContacto> medioContactoList;

    @OneToMany(mappedBy = "idPersona", fetch = FetchType.LAZY)
    private List<Documento> documentoList;

    @OneToMany(mappedBy = "idPersona", fetch = FetchType.LAZY)
    private List<PersonaRol> personaRolList;

    /** Conserva fechas importadas y asigna la creación en todos los flujos JPA. */
    @PrePersist
    protected void asignarFechaCreacion() {
        if (fechaCreacion == null) {
            fechaCreacion = new Date();
        }
    }

    public Persona() {
    }

    public Persona(UUID idPersona) {
        this.idPersona = idPersona;
    }

    public UUID getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(UUID idPersona) {
        this.idPersona = idPersona;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<MedioContacto> getMedioContactoList() {
        return medioContactoList;
    }

    public void setMedioContactoList(List<MedioContacto> medioContactoList) {
        this.medioContactoList = medioContactoList;
    }

    public List<Documento> getDocumentoList() {
        return documentoList;
    }

    public void setDocumentoList(List<Documento> documentoList) {
        this.documentoList = documentoList;
    }

    public List<PersonaRol> getPersonaRolList() {
        return personaRolList;
    }

    public void setPersonaRolList(List<PersonaRol> personaRolList) {
        this.personaRolList = personaRolList;
    }

    @Override
    public String getIdKey() {
        return idPersona != null ? idPersona.toString() : "";
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPersona != null ? idPersona.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Persona)) {
            return false;
        }
        Persona other = (Persona) object;
        if ((this.idPersona == null && other.idPersona != null) || (this.idPersona != null && !this.idPersona.equals(other.idPersona))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Persona[ idPersona=" + idPersona + " ]";
    }
}

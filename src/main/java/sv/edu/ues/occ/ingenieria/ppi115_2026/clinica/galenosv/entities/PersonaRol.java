package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.PrePersist;

@Entity
@Table(name = "persona_rol")
public class PersonaRol extends EntidadVersionada implements IdentificableEntity {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "{identificador.obligatorio}")
    @Id
    @Basic(optional = false)
    @Column(name = "id_persona_rol")
    private UUID idPersonaRol;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @OneToMany(mappedBy = "idPersonaRol", fetch = FetchType.LAZY)
    private List<ConsultaProcedimientoPaso> consultaProcedimientoPasoList;

    @JoinColumn(name = "id_clinica", referencedColumnName = "id_clinica")
    @ManyToOne(fetch = FetchType.LAZY)
    private Clinica idClinica;

    @NotNull(message = "{personaRol.idPersona.obligatorio}")
    @JoinColumn(name = "id_persona", referencedColumnName = "id_persona", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Persona idPersona;

    @NotNull(message = "{personaRol.idRol.obligatorio}")
    @JoinColumn(name = "id_rol", referencedColumnName = "id_rol", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Rol idRol;

    @OneToMany(mappedBy = "idPersonaRol", fetch = FetchType.LAZY)
    private List<Consulta> consultaList;

    /** Conserva fechas importadas y asigna la creación en todos los flujos JPA. */
    @PrePersist
    protected void asignarFechaCreacion() {
        if (fechaCreacion == null) {
            fechaCreacion = new Date();
        }
    }

    public PersonaRol() {
    }

    public PersonaRol(UUID idPersonaRol) {
        this.idPersonaRol = idPersonaRol;
    }

    public UUID getIdPersonaRol() {
        return idPersonaRol;
    }

    public void setIdPersonaRol(UUID idPersonaRol) {
        this.idPersonaRol = idPersonaRol;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<ConsultaProcedimientoPaso> getConsultaProcedimientoPasoList() {
        return consultaProcedimientoPasoList;
    }

    public void setConsultaProcedimientoPasoList(List<ConsultaProcedimientoPaso> consultaProcedimientoPasoList) {
        this.consultaProcedimientoPasoList = consultaProcedimientoPasoList;
    }

    public Clinica getIdClinica() {
        return idClinica;
    }

    public void setIdClinica(Clinica idClinica) {
        this.idClinica = idClinica;
    }

    public Persona getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Persona idPersona) {
        this.idPersona = idPersona;
    }

    public Rol getIdRol() {
        return idRol;
    }

    public void setIdRol(Rol idRol) {
        this.idRol = idRol;
    }

    public List<Consulta> getConsultaList() {
        return consultaList;
    }

    public void setConsultaList(List<Consulta> consultaList) {
        this.consultaList = consultaList;
    }

    @Override
    public String getIdKey() {
        return idPersonaRol != null ? idPersonaRol.toString() : "";
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPersonaRol != null ? idPersonaRol.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PersonaRol)) {
            return false;
        }
        PersonaRol other = (PersonaRol) object;
        if ((this.idPersonaRol == null && other.idPersonaRol != null) || (this.idPersonaRol != null && !this.idPersonaRol.equals(other.idPersonaRol))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.PersonaRol[ idPersonaRol=" + idPersonaRol + " ]";
    }
}

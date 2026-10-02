package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "rol")
public class Rol extends EntidadVersionada implements IdentificableEntity {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "{identificador.obligatorio}")
    @Id
    @Basic(optional = false)
    @Column(name = "id_rol")
    private UUID idRol;

    @NotBlank(message = "{rol.nombre.obligatorio}")
    @Size(max = 155, message = "{rol.nombre.longitud}")
    @Column(name = "nombre", nullable = false, length = 155)
    private String nombre;

    @NotNull(message = "{rol.activo.obligatorio}")
    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Size(max = 2000, message = "{rol.observaciones.longitud}")
    @Column(name = "observaciones", length = 2000)
    private String observaciones;

    @OneToMany(mappedBy = "idRol", fetch = FetchType.LAZY)
    private List<PersonaRol> personaRolList;

    @OneToMany(mappedBy = "idRol", fetch = FetchType.LAZY)
    private List<ProcedimientoPaso> procedimientoPasoList;

    public Rol() {
    }

    public Rol(UUID idRol) {
        this.idRol = idRol;
    }

    public UUID getIdRol() {
        return idRol;
    }

    public void setIdRol(UUID idRol) {
        this.idRol = idRol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<PersonaRol> getPersonaRolList() {
        return personaRolList;
    }

    public void setPersonaRolList(List<PersonaRol> personaRolList) {
        this.personaRolList = personaRolList;
    }

    public List<ProcedimientoPaso> getProcedimientoPasoList() {
        return procedimientoPasoList;
    }

    public void setProcedimientoPasoList(List<ProcedimientoPaso> procedimientoPasoList) {
        this.procedimientoPasoList = procedimientoPasoList;
    }

    @Override
    public String getIdKey() {
        return idRol != null ? idRol.toString() : "";
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idRol != null ? idRol.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Rol)) {
            return false;
        }
        Rol other = (Rol) object;
        if ((this.idRol == null && other.idRol != null) || (this.idRol != null && !this.idRol.equals(other.idRol))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Rol[ idRol=" + idRol + " ]";
    }
}

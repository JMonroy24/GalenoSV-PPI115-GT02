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
@Table(name = "clinica")
public class Clinica extends EntidadVersionada implements IdentificableEntity {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "{identificador.obligatorio}")
    @Id
    @Basic(optional = false)
    @Column(name = "id_clinica")
    private UUID idClinica;

    @NotBlank(message = "{clinica.nombre.obligatorio}")
    @Size(max = 255, message = "{clinica.nombre.longitud}")
    @Basic(optional = false)
    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @NotNull(message = "{clinica.activo.obligatorio}")
    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Size(max = 20, message = "{clinica.tipo.longitud}")
    @Column(name = "tipo", length = 20)
    private String tipo;

    @Size(max = 2000, message = "{clinica.comentarios.longitud}")
    @Column(name = "comentarios", length = 2000)
    private String comentarios;

    @OneToMany(mappedBy = "idClinica", fetch = FetchType.LAZY)
    private List<PersonaRol> personaRolList;

    public Clinica() {
    }

    public Clinica(UUID idClinica) {
        this.idClinica = idClinica;
    }

    public Clinica(UUID idClinica, String nombre) {
        this.idClinica = idClinica;
        this.nombre = nombre;
    }

    public UUID getIdClinica() {
        return idClinica;
    }

    public void setIdClinica(UUID idClinica) {
        this.idClinica = idClinica;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public List<PersonaRol> getPersonaRolList() {
        return personaRolList;
    }

    public void setPersonaRolList(List<PersonaRol> personaRolList) {
        this.personaRolList = personaRolList;
    }

    @Override
    public String getIdKey() {
        return idClinica != null ? idClinica.toString() : "";
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idClinica != null ? idClinica.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Clinica)) {
            return false;
        }
        Clinica other = (Clinica) object;
        if ((this.idClinica == null && other.idClinica != null) || (this.idClinica != null && !this.idClinica.equals(other.idClinica))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Clinica[ idClinica=" + idClinica + " ]";
    }
}

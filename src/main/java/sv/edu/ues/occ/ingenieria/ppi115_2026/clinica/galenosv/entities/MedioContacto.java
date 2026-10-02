package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.UUID;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "medio_contacto")
public class MedioContacto extends EntidadVersionada implements IdentificableEntity {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "{identificador.obligatorio}")
    @Id
    @Basic(optional = false)
    @Column(name = "id_medio_contacto")
    private UUID idMedioContacto;

    @NotBlank(message = "{medioContacto.valor.obligatorio}")
    @Size(max = 255, message = "{medioContacto.valor.longitud}")
    @Column(name = "valor", nullable = false, length = 255)
    private String valor;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @NotNull(message = "{medioContacto.idPersona.obligatorio}")
    @JoinColumn(name = "id_persona", referencedColumnName = "id_persona", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Persona idPersona;

    @NotNull(message = "{medioContacto.idTipoMedioContacto.obligatorio}")
    @JoinColumn(name = "id_tipo_medio_contacto", referencedColumnName = "id_tipo_medio_contacto", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private TipoMedioContacto idTipoMedioContacto;

    /** Conserva fechas importadas y asigna la creación en todos los flujos JPA. */
    @PrePersist
    protected void asignarFechaCreacion() {
        if (fechaCreacion == null) {
            fechaCreacion = new Date();
        }
    }

    public MedioContacto() {
    }

    public MedioContacto(UUID idMedioContacto) {
        this.idMedioContacto = idMedioContacto;
    }

    public UUID getIdMedioContacto() {
        return idMedioContacto;
    }

    public void setIdMedioContacto(UUID idMedioContacto) {
        this.idMedioContacto = idMedioContacto;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Persona getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Persona idPersona) {
        this.idPersona = idPersona;
    }

    public TipoMedioContacto getIdTipoMedioContacto() {
        return idTipoMedioContacto;
    }

    public void setIdTipoMedioContacto(TipoMedioContacto idTipoMedioContacto) {
        this.idTipoMedioContacto = idTipoMedioContacto;
    }

    @Override
    public String getIdKey() {
        return idMedioContacto != null ? idMedioContacto.toString() : "";
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idMedioContacto != null ? idMedioContacto.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MedioContacto)) {
            return false;
        }
        MedioContacto other = (MedioContacto) object;
        if ((this.idMedioContacto == null && other.idMedioContacto != null) || (this.idMedioContacto != null && !this.idMedioContacto.equals(other.idMedioContacto))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.MedioContacto[ idMedioContacto=" + idMedioContacto + " ]";
    }
}

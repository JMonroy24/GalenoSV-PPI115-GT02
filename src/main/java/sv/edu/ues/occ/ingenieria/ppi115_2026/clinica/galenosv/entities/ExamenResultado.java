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
@Table(name = "examen_resultado")
public class ExamenResultado implements IdentificableEntity {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "{identificador.obligatorio}")
    @Id
    @Basic(optional = false)
    @Column(name = "id_examen_resultado")
    private UUID idExamenResultado;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @NotBlank(message = "{examenResultado.resultado.obligatorio}")
    @Size(max = 4000, message = "{examenResultado.resultado.longitud}")
    @Column(name = "resultado", nullable = false, length = 4000)
    private String resultado;

    @NotBlank(message = "{examenResultado.interpretacion.obligatorio}")
    @Size(max = 4000, message = "{examenResultado.interpretacion.longitud}")
    @Column(name = "interpretacion", nullable = false, length = 4000)
    private String interpretacion;

    @Size(max = 500, message = "{examenResultado.rutaAtestado.longitud}")
    @Column(name = "ruta_atestado", length = 500)
    private String rutaAtestado;

    @NotNull(message = "{examenResultado.idOrdenExamen.obligatorio}")
    @JoinColumn(name = "id_orden_examen", referencedColumnName = "id_orden_examen", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private OrdenExamen idOrdenExamen;

    /** Conserva fechas importadas y asigna la creación en todos los flujos JPA. */
    @PrePersist
    protected void asignarFechaCreacion() {
        if (fechaCreacion == null) {
            fechaCreacion = new Date();
        }
    }

    public ExamenResultado() {
    }

    public ExamenResultado(UUID idExamenResultado) {
        this.idExamenResultado = idExamenResultado;
    }

    public UUID getIdExamenResultado() {
        return idExamenResultado;
    }

    public void setIdExamenResultado(UUID idExamenResultado) {
        this.idExamenResultado = idExamenResultado;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getInterpretacion() {
        return interpretacion;
    }

    public void setInterpretacion(String interpretacion) {
        this.interpretacion = interpretacion;
    }

    public String getRutaAtestado() {
        return rutaAtestado;
    }

    public void setRutaAtestado(String rutaAtestado) {
        this.rutaAtestado = rutaAtestado;
    }

    public OrdenExamen getIdOrdenExamen() {
        return idOrdenExamen;
    }

    public void setIdOrdenExamen(OrdenExamen idOrdenExamen) {
        this.idOrdenExamen = idOrdenExamen;
    }

    @Override
    public String getIdKey() {
        return idExamenResultado != null ? idExamenResultado.toString() : "";
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idExamenResultado != null ? idExamenResultado.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ExamenResultado)) {
            return false;
        }
        ExamenResultado other = (ExamenResultado) object;
        if ((this.idExamenResultado == null && other.idExamenResultado != null) || (this.idExamenResultado != null && !this.idExamenResultado.equals(other.idExamenResultado))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ExamenResultado[ idExamenResultado=" + idExamenResultado + " ]";
    }
}

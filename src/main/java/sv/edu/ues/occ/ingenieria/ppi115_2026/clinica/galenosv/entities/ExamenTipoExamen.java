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

@Entity
@Table(name = "examen_tipo_examen")
public class ExamenTipoExamen extends EntidadVersionada implements IdentificableEntity {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "{identificador.obligatorio}")
    @Id
    @Basic(optional = false)
    @Column(name = "id_examen_tipo_examen")
    private UUID idExamenTipoExamen;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Size(max = 2000, message = "{examenTipoExamen.observaciones.longitud}")
    @Column(name = "observaciones", length = 2000)
    private String observaciones;

    @NotNull(message = "{examenTipoExamen.idExamen.obligatorio}")
    @JoinColumn(name = "id_examen", referencedColumnName = "id_examen", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Examen idExamen;

    @NotNull(message = "{examenTipoExamen.idTipoExamen.obligatorio}")
    @JoinColumn(name = "id_tipo_examen", referencedColumnName = "id_tipo_examen", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private TipoExamen idTipoExamen;

    /** Conserva fechas importadas y asigna la creación en todos los flujos JPA. */
    @PrePersist
    protected void asignarFechaCreacion() {
        if (fechaCreacion == null) {
            fechaCreacion = new Date();
        }
    }

    public ExamenTipoExamen() {
    }

    public ExamenTipoExamen(UUID idExamenTipoExamen) {
        this.idExamenTipoExamen = idExamenTipoExamen;
    }

    public UUID getIdExamenTipoExamen() {
        return idExamenTipoExamen;
    }

    public void setIdExamenTipoExamen(UUID idExamenTipoExamen) {
        this.idExamenTipoExamen = idExamenTipoExamen;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Examen getIdExamen() {
        return idExamen;
    }

    public void setIdExamen(Examen idExamen) {
        this.idExamen = idExamen;
    }

    public TipoExamen getIdTipoExamen() {
        return idTipoExamen;
    }

    public void setIdTipoExamen(TipoExamen idTipoExamen) {
        this.idTipoExamen = idTipoExamen;
    }

    @Override
    public String getIdKey() {
        return idExamenTipoExamen != null ? idExamenTipoExamen.toString() : "";
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idExamenTipoExamen != null ? idExamenTipoExamen.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ExamenTipoExamen)) {
            return false;
        }
        ExamenTipoExamen other = (ExamenTipoExamen) object;
        if ((this.idExamenTipoExamen == null && other.idExamenTipoExamen != null) || (this.idExamenTipoExamen != null && !this.idExamenTipoExamen.equals(other.idExamenTipoExamen))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ExamenTipoExamen[ idExamenTipoExamen=" + idExamenTipoExamen + " ]";
    }
}

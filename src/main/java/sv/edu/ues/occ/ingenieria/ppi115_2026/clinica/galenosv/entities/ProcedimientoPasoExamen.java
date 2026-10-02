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
@Table(name = "procedimiento_paso_examen")
public class ProcedimientoPasoExamen extends EntidadVersionada implements IdentificableEntity {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "{identificador.obligatorio}")
    @Id
    @Basic(optional = false)
    @Column(name = "id_procedimiento_paso_examen")
    private UUID idProcedimientoPasoExamen;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @NotNull(message = "{procedimientoPasoExamen.activo.obligatorio}")
    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Size(max = 2000, message = "{procedimientoPasoExamen.observaciones.longitud}")
    @Column(name = "observaciones", length = 2000)
    private String observaciones;

    @NotNull(message = "{procedimientoPasoExamen.idExamen.obligatorio}")
    @JoinColumn(name = "id_examen", referencedColumnName = "id_examen", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Examen idExamen;

    @NotNull(message = "{procedimientoPasoExamen.idProcedimientoPaso.obligatorio}")
    @JoinColumn(name = "id_procedimiento_paso", referencedColumnName = "id_procedimiento_paso", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ProcedimientoPaso idProcedimientoPaso;

    /** Conserva fechas importadas y asigna la creación en todos los flujos JPA. */
    @PrePersist
    protected void asignarFechaCreacion() {
        if (fechaCreacion == null) {
            fechaCreacion = new Date();
        }
    }

    public ProcedimientoPasoExamen() {
    }

    public ProcedimientoPasoExamen(UUID idProcedimientoPasoExamen) {
        this.idProcedimientoPasoExamen = idProcedimientoPasoExamen;
    }

    public UUID getIdProcedimientoPasoExamen() {
        return idProcedimientoPasoExamen;
    }

    public void setIdProcedimientoPasoExamen(UUID idProcedimientoPasoExamen) {
        this.idProcedimientoPasoExamen = idProcedimientoPasoExamen;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
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

    public Examen getIdExamen() {
        return idExamen;
    }

    public void setIdExamen(Examen idExamen) {
        this.idExamen = idExamen;
    }

    public ProcedimientoPaso getIdProcedimientoPaso() {
        return idProcedimientoPaso;
    }

    public void setIdProcedimientoPaso(ProcedimientoPaso idProcedimientoPaso) {
        this.idProcedimientoPaso = idProcedimientoPaso;
    }

    @Override
    public String getIdKey() {
        return idProcedimientoPasoExamen != null ? idProcedimientoPasoExamen.toString() : "";
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idProcedimientoPasoExamen != null ? idProcedimientoPasoExamen.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ProcedimientoPasoExamen)) {
            return false;
        }
        ProcedimientoPasoExamen other = (ProcedimientoPasoExamen) object;
        if ((this.idProcedimientoPasoExamen == null && other.idProcedimientoPasoExamen != null) || (this.idProcedimientoPasoExamen != null && !this.idProcedimientoPasoExamen.equals(other.idProcedimientoPasoExamen))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ProcedimientoPasoExamen[ idProcedimientoPasoExamen=" + idProcedimientoPasoExamen + " ]";
    }
}

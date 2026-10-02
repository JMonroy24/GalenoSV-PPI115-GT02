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
import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "orden_examen")
public class OrdenExamen extends EntidadVersionada implements IdentificableEntity {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "{identificador.obligatorio}")
    @Id
    @Basic(optional = false)
    @Column(name = "id_orden_examen")
    private UUID idOrdenExamen;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @NotBlank(message = "{ordenExamen.indicaciones.obligatorio}")
    @Size(max = 2000, message = "{ordenExamen.indicaciones.longitud}")
    @Column(name = "indicaciones", nullable = false, length = 2000)
    private String indicaciones;

    @OneToMany(mappedBy = "idOrdenExamen", fetch = FetchType.LAZY)
    private List<ExamenResultado> examenResultadoList;

    @NotNull(message = "{ordenExamen.idConsultaProcedimientoPaso.obligatorio}")
    @JoinColumn(name = "id_consulta_procedimiento_paso", referencedColumnName = "id_consulta_procedimiento_paso", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ConsultaProcedimientoPaso idConsultaProcedimientoPaso;

    /** Conserva fechas importadas y asigna la creación en todos los flujos JPA. */
    @PrePersist
    protected void asignarFechaCreacion() {
        if (fechaCreacion == null) {
            fechaCreacion = new Date();
        }
    }

    public OrdenExamen() {
    }

    public OrdenExamen(UUID idOrdenExamen) {
        this.idOrdenExamen = idOrdenExamen;
    }

    public UUID getIdOrdenExamen() {
        return idOrdenExamen;
    }

    public void setIdOrdenExamen(UUID idOrdenExamen) {
        this.idOrdenExamen = idOrdenExamen;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }

    public List<ExamenResultado> getExamenResultadoList() {
        return examenResultadoList;
    }

    public void setExamenResultadoList(List<ExamenResultado> examenResultadoList) {
        this.examenResultadoList = examenResultadoList;
    }

    public ConsultaProcedimientoPaso getIdConsultaProcedimientoPaso() {
        return idConsultaProcedimientoPaso;
    }

    public void setIdConsultaProcedimientoPaso(ConsultaProcedimientoPaso idConsultaProcedimientoPaso) {
        this.idConsultaProcedimientoPaso = idConsultaProcedimientoPaso;
    }

    @Override
    public String getIdKey() {
        return idOrdenExamen != null ? idOrdenExamen.toString() : "";
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idOrdenExamen != null ? idOrdenExamen.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof OrdenExamen)) {
            return false;
        }
        OrdenExamen other = (OrdenExamen) object;
        if ((this.idOrdenExamen == null && other.idOrdenExamen != null) || (this.idOrdenExamen != null && !this.idOrdenExamen.equals(other.idOrdenExamen))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.OrdenExamen[ idOrdenExamen=" + idOrdenExamen + " ]";
    }
}

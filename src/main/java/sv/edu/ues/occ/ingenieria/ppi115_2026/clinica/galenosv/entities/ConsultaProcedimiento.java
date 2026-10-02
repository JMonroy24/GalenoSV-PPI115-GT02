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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.validation.PeriodoFechas;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.validation.RangoFechasValido;

@Entity
@RangoFechasValido
@Table(name = "consulta_procedimiento")
public class ConsultaProcedimiento extends EntidadVersionada implements IdentificableEntity, PeriodoFechas {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "{identificador.obligatorio}")
    @Id
    @Basic(optional = false)
    @Column(name = "id_consulta_procedimiento")
    private UUID idConsultaProcedimiento;

    @NotNull(message = "{consultaProcedimiento.fechaInicio.obligatorio}")
    @Column(name = "fecha_inicio", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInicio;

    @Column(name = "fecha_fin")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaFin;

    @Size(max = 2000, message = "{consultaProcedimiento.observaciones.longitud}")
    @Column(name = "observaciones", length = 2000)
    private String observaciones;

    @OneToMany(mappedBy = "idConsultaProcedimiento", fetch = FetchType.LAZY)
    private List<ConsultaProcedimientoPaso> consultaProcedimientoPasoList;

    @NotNull(message = "{consultaProcedimiento.idConsulta.obligatorio}")
    @JoinColumn(name = "id_consulta", referencedColumnName = "id_consulta", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Consulta idConsulta;

    @NotNull(message = "{consultaProcedimiento.idProcedimiento.obligatorio}")
    @JoinColumn(name = "id_procedimiento", referencedColumnName = "id_procedimiento", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Procedimiento idProcedimiento;

    public ConsultaProcedimiento() {
    }

    public ConsultaProcedimiento(UUID idConsultaProcedimiento) {
        this.idConsultaProcedimiento = idConsultaProcedimiento;
    }

    public UUID getIdConsultaProcedimiento() {
        return idConsultaProcedimiento;
    }

    public void setIdConsultaProcedimiento(UUID idConsultaProcedimiento) {
        this.idConsultaProcedimiento = idConsultaProcedimiento;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<ConsultaProcedimientoPaso> getConsultaProcedimientoPasoList() {
        return consultaProcedimientoPasoList;
    }

    public void setConsultaProcedimientoPasoList(List<ConsultaProcedimientoPaso> consultaProcedimientoPasoList) {
        this.consultaProcedimientoPasoList = consultaProcedimientoPasoList;
    }

    public Consulta getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(Consulta idConsulta) {
        this.idConsulta = idConsulta;
    }

    public Procedimiento getIdProcedimiento() {
        return idProcedimiento;
    }

    public void setIdProcedimiento(Procedimiento idProcedimiento) {
        this.idProcedimiento = idProcedimiento;
    }

    @Override
    public String getIdKey() {
        return idConsultaProcedimiento != null ? idConsultaProcedimiento.toString() : "";
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idConsultaProcedimiento != null ? idConsultaProcedimiento.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ConsultaProcedimiento)) {
            return false;
        }
        ConsultaProcedimiento other = (ConsultaProcedimiento) object;
        if ((this.idConsultaProcedimiento == null && other.idConsultaProcedimiento != null) || (this.idConsultaProcedimiento != null && !this.idConsultaProcedimiento.equals(other.idConsultaProcedimiento))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ConsultaProcedimiento[ idConsultaProcedimiento=" + idConsultaProcedimiento + " ]";
    }
}

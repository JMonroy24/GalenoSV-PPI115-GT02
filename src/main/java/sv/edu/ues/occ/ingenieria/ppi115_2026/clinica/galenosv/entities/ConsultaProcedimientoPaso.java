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
import jakarta.validation.constraints.NotBlank;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.validation.PeriodoFechas;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.validation.RangoFechasValido;

@Entity
@RangoFechasValido
@Table(name = "consulta_procedimiento_paso")
public class ConsultaProcedimientoPaso extends EntidadVersionada implements IdentificableEntity, PeriodoFechas {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "{identificador.obligatorio}")
    @Id
    @Basic(optional = false)
    @Column(name = "id_consulta_procedimiento_paso")
    private UUID idConsultaProcedimientoPaso;

    @NotNull(message = "{consultaProcedimientoPaso.fechaInicio.obligatorio}")
    @Column(name = "fecha_inicio", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInicio;

    @Column(name = "fecha_fin")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaFin;

    @NotBlank(message = "{consultaProcedimientoPaso.estado.obligatorio}")
    @Size(max = 20, message = "{consultaProcedimientoPaso.estado.longitud}")
    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @NotNull(message = "{consultaProcedimientoPaso.idConsultaProcedimiento.obligatorio}")
    @JoinColumn(name = "id_consulta_procedimiento", referencedColumnName = "id_consulta_procedimiento", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ConsultaProcedimiento idConsultaProcedimiento;

    @NotNull(message = "{consultaProcedimientoPaso.idPersonaRol.obligatorio}")
    @JoinColumn(name = "id_persona_rol", referencedColumnName = "id_persona_rol", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private PersonaRol idPersonaRol;

    @OneToMany(mappedBy = "idConsultaProcedimientoPaso", fetch = FetchType.LAZY)
    private List<OrdenExamen> ordenExamenList;

    public ConsultaProcedimientoPaso() {
    }

    public ConsultaProcedimientoPaso(UUID idConsultaProcedimientoPaso) {
        this.idConsultaProcedimientoPaso = idConsultaProcedimientoPaso;
    }

    public UUID getIdConsultaProcedimientoPaso() {
        return idConsultaProcedimientoPaso;
    }

    public void setIdConsultaProcedimientoPaso(UUID idConsultaProcedimientoPaso) {
        this.idConsultaProcedimientoPaso = idConsultaProcedimientoPaso;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public ConsultaProcedimiento getIdConsultaProcedimiento() {
        return idConsultaProcedimiento;
    }

    public void setIdConsultaProcedimiento(ConsultaProcedimiento idConsultaProcedimiento) {
        this.idConsultaProcedimiento = idConsultaProcedimiento;
    }

    public PersonaRol getIdPersonaRol() {
        return idPersonaRol;
    }

    public void setIdPersonaRol(PersonaRol idPersonaRol) {
        this.idPersonaRol = idPersonaRol;
    }

    public List<OrdenExamen> getOrdenExamenList() {
        return ordenExamenList;
    }

    public void setOrdenExamenList(List<OrdenExamen> ordenExamenList) {
        this.ordenExamenList = ordenExamenList;
    }

    @Override
    public String getIdKey() {
        return idConsultaProcedimientoPaso != null ? idConsultaProcedimientoPaso.toString() : "";
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idConsultaProcedimientoPaso != null ? idConsultaProcedimientoPaso.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ConsultaProcedimientoPaso)) {
            return false;
        }
        ConsultaProcedimientoPaso other = (ConsultaProcedimientoPaso) object;
        if ((this.idConsultaProcedimientoPaso == null && other.idConsultaProcedimientoPaso != null) || (this.idConsultaProcedimientoPaso != null && !this.idConsultaProcedimientoPaso.equals(other.idConsultaProcedimientoPaso))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ConsultaProcedimientoPaso[ idConsultaProcedimientoPaso=" + idConsultaProcedimientoPaso + " ]";
    }
}

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
@Table(name = "consulta")
public class Consulta implements IdentificableEntity, PeriodoFechas {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "{identificador.obligatorio}")
    @Id
    @Basic(optional = false)
    @Column(name = "id_consulta")
    private UUID idConsulta;

    @NotNull(message = "{consulta.fechaInicio.obligatorio}")
    @Column(name = "fecha_inicio", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInicio;

    @Column(name = "fecha_fin")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaFin;

    @Size(max = 255, message = "{consulta.referenciaExterna.longitud}")
    @Column(name = "referencia_externa", length = 255)
    private String referenciaExterna;

    @Size(max = 2000, message = "{consulta.observaciones.longitud}")
    @Column(name = "observaciones", length = 2000)
    private String observaciones;

    @NotNull(message = "{consulta.idPersonaRol.obligatorio}")
    @JoinColumn(name = "id_persona_rol", referencedColumnName = "id_persona_rol", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private PersonaRol idPersonaRol;

    @OneToMany(mappedBy = "idConsulta", fetch = FetchType.LAZY)
    private List<ConsultaProcedimiento> consultaProcedimientoList;

    public Consulta() {
    }

    public Consulta(UUID idConsulta) {
        this.idConsulta = idConsulta;
    }

    public UUID getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(UUID idConsulta) {
        this.idConsulta = idConsulta;
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

    public String getReferenciaExterna() {
        return referenciaExterna;
    }

    public void setReferenciaExterna(String referenciaExterna) {
        this.referenciaExterna = referenciaExterna;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public PersonaRol getIdPersonaRol() {
        return idPersonaRol;
    }

    public void setIdPersonaRol(PersonaRol idPersonaRol) {
        this.idPersonaRol = idPersonaRol;
    }

    public List<ConsultaProcedimiento> getConsultaProcedimientoList() {
        return consultaProcedimientoList;
    }

    public void setConsultaProcedimientoList(List<ConsultaProcedimiento> consultaProcedimientoList) {
        this.consultaProcedimientoList = consultaProcedimientoList;
    }

    @Override
    public String getIdKey() {
        return idConsulta != null ? idConsulta.toString() : "";
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idConsulta != null ? idConsulta.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Consulta)) {
            return false;
        }
        Consulta other = (Consulta) object;
        if ((this.idConsulta == null && other.idConsulta != null) || (this.idConsulta != null && !this.idConsulta.equals(other.idConsulta))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Consulta[ idConsulta=" + idConsulta + " ]";
    }
}

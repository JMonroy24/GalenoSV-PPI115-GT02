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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "procedimiento_paso")
public class ProcedimientoPaso implements IdentificableEntity {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "{identificador.obligatorio}")
    @Id
    @Basic(optional = false)
    @Column(name = "id_procedimiento_paso")
    private UUID idProcedimientoPaso;

    @NotBlank(message = "{procedimientoPaso.nombre.obligatorio}")
    @Size(max = 155, message = "{procedimientoPaso.nombre.longitud}")
    @Column(name = "nombre", nullable = false, length = 155)
    private String nombre;

    @NotNull(message = "{procedimientoPaso.indicaFin.obligatorio}")
    @Column(name = "indica_fin", nullable = false)
    private Boolean indicaFin = false;

    @OneToMany(mappedBy = "idProcedimientoPaso", fetch = FetchType.LAZY)
    private List<ProcedimientoPasoSecuencia> procedimientoPasoSecuenciaList;

    @OneToMany(mappedBy = "idProcedimientoPaso", fetch = FetchType.LAZY)
    private List<ProcedimientoPasoExamen> procedimientoPasoExamenList;

    @NotNull(message = "{procedimientoPaso.idProcedimiento.obligatorio}")
    @JoinColumn(name = "id_procedimiento", referencedColumnName = "id_procedimiento", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Procedimiento idProcedimiento;

    @JoinColumn(name = "id_rol", referencedColumnName = "id_rol")
    @ManyToOne(fetch = FetchType.LAZY)
    private Rol idRol;

    public ProcedimientoPaso() {
    }

    public ProcedimientoPaso(UUID idProcedimientoPaso) {
        this.idProcedimientoPaso = idProcedimientoPaso;
    }

    public UUID getIdProcedimientoPaso() {
        return idProcedimientoPaso;
    }

    public void setIdProcedimientoPaso(UUID idProcedimientoPaso) {
        this.idProcedimientoPaso = idProcedimientoPaso;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getIndicaFin() {
        return indicaFin;
    }

    public void setIndicaFin(Boolean indicaFin) {
        this.indicaFin = indicaFin;
    }

    public List<ProcedimientoPasoSecuencia> getProcedimientoPasoSecuenciaList() {
        return procedimientoPasoSecuenciaList;
    }

    public void setProcedimientoPasoSecuenciaList(List<ProcedimientoPasoSecuencia> procedimientoPasoSecuenciaList) {
        this.procedimientoPasoSecuenciaList = procedimientoPasoSecuenciaList;
    }

    public List<ProcedimientoPasoExamen> getProcedimientoPasoExamenList() {
        return procedimientoPasoExamenList;
    }

    public void setProcedimientoPasoExamenList(List<ProcedimientoPasoExamen> procedimientoPasoExamenList) {
        this.procedimientoPasoExamenList = procedimientoPasoExamenList;
    }

    public Procedimiento getIdProcedimiento() {
        return idProcedimiento;
    }

    public void setIdProcedimiento(Procedimiento idProcedimiento) {
        this.idProcedimiento = idProcedimiento;
    }

    public Rol getIdRol() {
        return idRol;
    }

    public void setIdRol(Rol idRol) {
        this.idRol = idRol;
    }

    @Override
    public String getIdKey() {
        return idProcedimientoPaso != null ? idProcedimientoPaso.toString() : "";
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idProcedimientoPaso != null ? idProcedimientoPaso.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ProcedimientoPaso)) {
            return false;
        }
        ProcedimientoPaso other = (ProcedimientoPaso) object;
        if ((this.idProcedimientoPaso == null && other.idProcedimientoPaso != null) || (this.idProcedimientoPaso != null && !this.idProcedimientoPaso.equals(other.idProcedimientoPaso))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ProcedimientoPaso[ idProcedimientoPaso=" + idProcedimientoPaso + " ]";
    }
}

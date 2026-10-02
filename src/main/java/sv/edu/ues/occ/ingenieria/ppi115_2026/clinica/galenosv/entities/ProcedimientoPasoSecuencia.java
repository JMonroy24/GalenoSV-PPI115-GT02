package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "procedimiento_paso_secuencia")
public class ProcedimientoPasoSecuencia extends EntidadVersionada implements IdentificableEntity {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "{identificador.obligatorio}")
    @Id
    @Basic(optional = false)
    @Column(name = "id_procedimiento_paso_secuencia")
    private UUID idProcedimientoPasoSecuencia;

    @NotNull(message = "{procedimientoPasoSecuencia.idProcedimientoPasoReferencia.obligatorio}")
    @Column(name = "id_procedimiento_paso_referencia", nullable = false)
    private UUID idProcedimientoPasoReferencia;

    @NotBlank(message = "{procedimientoPasoSecuencia.tipoSecuencia.obligatorio}")
    @Size(max = 20, message = "{procedimientoPasoSecuencia.tipoSecuencia.longitud}")
    @Column(name = "tipo_secuencia", nullable = false, length = 20)
    private String tipoSecuencia;

    @NotNull(message = "{procedimientoPasoSecuencia.idProcedimientoPaso.obligatorio}")
    @JoinColumn(name = "id_procedimiento_paso", referencedColumnName = "id_procedimiento_paso", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ProcedimientoPaso idProcedimientoPaso;

    public ProcedimientoPasoSecuencia() {
    }

    public ProcedimientoPasoSecuencia(UUID idProcedimientoPasoSecuencia) {
        this.idProcedimientoPasoSecuencia = idProcedimientoPasoSecuencia;
    }

    public UUID getIdProcedimientoPasoSecuencia() {
        return idProcedimientoPasoSecuencia;
    }

    public void setIdProcedimientoPasoSecuencia(UUID idProcedimientoPasoSecuencia) {
        this.idProcedimientoPasoSecuencia = idProcedimientoPasoSecuencia;
    }

    public UUID getIdProcedimientoPasoReferencia() {
        return idProcedimientoPasoReferencia;
    }

    public void setIdProcedimientoPasoReferencia(UUID idProcedimientoPasoReferencia) {
        this.idProcedimientoPasoReferencia = idProcedimientoPasoReferencia;
    }

    public String getTipoSecuencia() {
        return tipoSecuencia;
    }

    public void setTipoSecuencia(String tipoSecuencia) {
        this.tipoSecuencia = tipoSecuencia;
    }

    public ProcedimientoPaso getIdProcedimientoPaso() {
        return idProcedimientoPaso;
    }

    public void setIdProcedimientoPaso(ProcedimientoPaso idProcedimientoPaso) {
        this.idProcedimientoPaso = idProcedimientoPaso;
    }

    @Override
    public String getIdKey() {
        return idProcedimientoPasoSecuencia != null ? idProcedimientoPasoSecuencia.toString() : "";
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idProcedimientoPasoSecuencia != null ? idProcedimientoPasoSecuencia.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ProcedimientoPasoSecuencia)) {
            return false;
        }
        ProcedimientoPasoSecuencia other = (ProcedimientoPasoSecuencia) object;
        if ((this.idProcedimientoPasoSecuencia == null && other.idProcedimientoPasoSecuencia != null) || (this.idProcedimientoPasoSecuencia != null && !this.idProcedimientoPasoSecuencia.equals(other.idProcedimientoPasoSecuencia))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ProcedimientoPasoSecuencia[ idProcedimientoPasoSecuencia=" + idProcedimientoPasoSecuencia + " ]";
    }
}

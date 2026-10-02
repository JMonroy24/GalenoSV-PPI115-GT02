package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "procedimiento")
public class Procedimiento extends EntidadVersionada implements IdentificableEntity {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "{identificador.obligatorio}")
    @Id
    @Basic(optional = false)
    @Column(name = "id_procedimiento")
    private UUID idProcedimiento;

    @NotBlank(message = "{procedimiento.nombre.obligatorio}")
    @Size(max = 155, message = "{procedimiento.nombre.longitud}")
    @Column(name = "nombre", nullable = false, length = 155)
    private String nombre;

    @NotNull(message = "{procedimiento.activo.obligatorio}")
    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Size(max = 2000, message = "{procedimiento.observaciones.longitud}")
    @Column(name = "observaciones", length = 2000)
    private String observaciones;

    @OneToMany(mappedBy = "idProcedimiento", fetch = FetchType.LAZY)
    private List<ProcedimientoPaso> procedimientoPasoList;

    public Procedimiento() {
    }

    public Procedimiento(UUID idProcedimiento) {
        this.idProcedimiento = idProcedimiento;
    }

    public UUID getIdProcedimiento() {
        return idProcedimiento;
    }

    public void setIdProcedimiento(UUID idProcedimiento) {
        this.idProcedimiento = idProcedimiento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public List<ProcedimientoPaso> getProcedimientoPasoList() {
        return procedimientoPasoList;
    }

    public void setProcedimientoPasoList(List<ProcedimientoPaso> procedimientoPasoList) {
        this.procedimientoPasoList = procedimientoPasoList;
    }

    @Override
    public String getIdKey() {
        return idProcedimiento != null ? idProcedimiento.toString() : "";
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idProcedimiento != null ? idProcedimiento.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Procedimiento)) {
            return false;
        }
        Procedimiento other = (Procedimiento) object;
        if ((this.idProcedimiento == null && other.idProcedimiento != null) || (this.idProcedimiento != null && !this.idProcedimiento.equals(other.idProcedimiento))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Procedimiento[ idProcedimiento=" + idProcedimiento + " ]";
    }
}

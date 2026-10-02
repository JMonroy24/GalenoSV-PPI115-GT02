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
@Table(name = "examen")
public class Examen extends EntidadVersionada implements IdentificableEntity {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "{identificador.obligatorio}")
    @Id
    @Basic(optional = false)
    @Column(name = "id_examen")
    private UUID idExamen;

    @NotBlank(message = "{examen.nombre.obligatorio}")
    @Size(max = 255, message = "{examen.nombre.longitud}")
    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @NotNull(message = "{examen.activo.obligatorio}")
    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Size(max = 2000, message = "{examen.observaciones.longitud}")
    @Column(name = "observaciones", length = 2000)
    private String observaciones;

    @OneToMany(mappedBy = "idExamen", fetch = FetchType.LAZY)
    private List<ExamenTipoExamen> examenTipoExamenList;

    @OneToMany(mappedBy = "idExamen", fetch = FetchType.LAZY)
    private List<ProcedimientoPasoExamen> procedimientoPasoExamenList;

    public Examen() {
    }

    public Examen(UUID idExamen) {
        this.idExamen = idExamen;
    }

    public UUID getIdExamen() {
        return idExamen;
    }

    public void setIdExamen(UUID idExamen) {
        this.idExamen = idExamen;
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

    public List<ExamenTipoExamen> getExamenTipoExamenList() {
        return examenTipoExamenList;
    }

    public void setExamenTipoExamenList(List<ExamenTipoExamen> examenTipoExamenList) {
        this.examenTipoExamenList = examenTipoExamenList;
    }

    public List<ProcedimientoPasoExamen> getProcedimientoPasoExamenList() {
        return procedimientoPasoExamenList;
    }

    public void setProcedimientoPasoExamenList(List<ProcedimientoPasoExamen> procedimientoPasoExamenList) {
        this.procedimientoPasoExamenList = procedimientoPasoExamenList;
    }

    @Override
    public String getIdKey() {
        return idExamen != null ? idExamen.toString() : "";
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idExamen != null ? idExamen.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Examen)) {
            return false;
        }
        Examen other = (Examen) object;
        if ((this.idExamen == null && other.idExamen != null) || (this.idExamen != null && !this.idExamen.equals(other.idExamen))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Examen[ idExamen=" + idExamen + " ]";
    }
}

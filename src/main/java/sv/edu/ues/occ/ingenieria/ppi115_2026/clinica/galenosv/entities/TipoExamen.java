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
@Table(name = "tipo_examen")
public class TipoExamen implements IdentificableEntity {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "{identificador.obligatorio}")
    @Id
    @Basic(optional = false)
    @Column(name = "id_tipo_examen")
    private UUID idTipoExamen;

    @NotBlank(message = "{tipoExamen.nombre.obligatorio}")
    @Size(max = 255, message = "{tipoExamen.nombre.longitud}")
    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @NotNull(message = "{tipoExamen.activo.obligatorio}")
    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Size(max = 2000, message = "{tipoExamen.observaciones.longitud}")
    @Column(name = "observaciones", length = 2000)
    private String observaciones;

    @OneToMany(mappedBy = "idTipoExamen", fetch = FetchType.LAZY)
    private List<ExamenTipoExamen> examenTipoExamenList;

    public TipoExamen() {
    }

    public TipoExamen(UUID idTipoExamen) {
        this.idTipoExamen = idTipoExamen;
    }

    public UUID getIdTipoExamen() {
        return idTipoExamen;
    }

    public void setIdTipoExamen(UUID idTipoExamen) {
        this.idTipoExamen = idTipoExamen;
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

    @Override
    public String getIdKey() {
        return idTipoExamen != null ? idTipoExamen.toString() : "";
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idTipoExamen != null ? idTipoExamen.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TipoExamen)) {
            return false;
        }
        TipoExamen other = (TipoExamen) object;
        if ((this.idTipoExamen == null && other.idTipoExamen != null) || (this.idTipoExamen != null && !this.idTipoExamen.equals(other.idTipoExamen))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.TipoExamen[ idTipoExamen=" + idTipoExamen + " ]";
    }
}

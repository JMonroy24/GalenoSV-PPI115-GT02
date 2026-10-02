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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.validation.RegexValido;

@Entity
@Table(name = "tipo_medio_contacto")
public class TipoMedioContacto extends EntidadVersionada implements IdentificableEntity {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "{identificador.obligatorio}")
    @Id
    @Basic(optional = false)
    @Column(name = "id_tipo_medio_contacto")
    private UUID idTipoMedioContacto;

    @NotBlank(message = "{tipoMedioContacto.nombre.obligatorio}")
    @Size(max = 155, message = "{tipoMedioContacto.nombre.longitud}")
    @Column(name = "nombre", nullable = false, length = 155)
    private String nombre;

    @Size(max = 2000, message = "{tipoMedioContacto.indicaciones.longitud}")
    @Column(name = "indicaciones", length = 2000)
    private String indicaciones;

    @Size(max = 500, message = "{tipoMedioContacto.expresionRegular.longitud}")
    @RegexValido
    @Column(name = "expresion_regular", length = 500)
    private String expresionRegular;

    @NotNull(message = "{tipoMedioContacto.activo.obligatorio}")
    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @OneToMany(mappedBy = "idTipoMedioContacto", fetch = FetchType.LAZY)
    private List<MedioContacto> medioContactoList;

    public TipoMedioContacto() {
    }

    public TipoMedioContacto(UUID idTipoMedioContacto) {
        this.idTipoMedioContacto = idTipoMedioContacto;
    }

    public UUID getIdTipoMedioContacto() {
        return idTipoMedioContacto;
    }

    public void setIdTipoMedioContacto(UUID idTipoMedioContacto) {
        this.idTipoMedioContacto = idTipoMedioContacto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }

    public String getExpresionRegular() {
        return expresionRegular;
    }

    public void setExpresionRegular(String expresionRegular) {
        this.expresionRegular = expresionRegular;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public List<MedioContacto> getMedioContactoList() {
        return medioContactoList;
    }

    public void setMedioContactoList(List<MedioContacto> medioContactoList) {
        this.medioContactoList = medioContactoList;
    }

    @Override
    public String getIdKey() {
        return idTipoMedioContacto != null ? idTipoMedioContacto.toString() : "";
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idTipoMedioContacto != null ? idTipoMedioContacto.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TipoMedioContacto)) {
            return false;
        }
        TipoMedioContacto other = (TipoMedioContacto) object;
        if ((this.idTipoMedioContacto == null && other.idTipoMedioContacto != null) || (this.idTipoMedioContacto != null && !this.idTipoMedioContacto.equals(other.idTipoMedioContacto))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.TipoMedioContacto[ idTipoMedioContacto=" + idTipoMedioContacto + " ]";
    }
}

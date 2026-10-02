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
@Table(name = "tipo_documento")
public class TipoDocumento extends EntidadVersionada implements IdentificableEntity {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "{identificador.obligatorio}")
    @Id
    @Basic(optional = false)
    @Column(name = "id_tipo_documento")
    private UUID idTipoDocumento;

    @NotBlank(message = "{tipoDocumento.nombre.obligatorio}")
    @Size(max = 155, message = "{tipoDocumento.nombre.longitud}")
    @Column(name = "nombre", nullable = false, length = 155)
    private String nombre;

    @Size(max = 2000, message = "{tipoDocumento.indicaciones.longitud}")
    @Column(name = "indicaciones", length = 2000)
    private String indicaciones;

    @Size(max = 500, message = "{tipoDocumento.expresionRegular.longitud}")
    @RegexValido
    @Column(name = "expresion_regular", length = 500)
    private String expresionRegular;

    @NotNull(message = "{tipoDocumento.activo.obligatorio}")
    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @OneToMany(mappedBy = "idTipoDocumento", fetch = FetchType.LAZY)
    private List<Documento> documentoList;

    public TipoDocumento() {
    }

    public TipoDocumento(UUID idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }

    public UUID getIdTipoDocumento() {
        return idTipoDocumento;
    }

    public void setIdTipoDocumento(UUID idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
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

    public List<Documento> getDocumentoList() {
        return documentoList;
    }

    public void setDocumentoList(List<Documento> documentoList) {
        this.documentoList = documentoList;
    }

    @Override
    public String getIdKey() {
        return idTipoDocumento != null ? idTipoDocumento.toString() : "";
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idTipoDocumento != null ? idTipoDocumento.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TipoDocumento)) {
            return false;
        }
        TipoDocumento other = (TipoDocumento) object;
        if ((this.idTipoDocumento == null && other.idTipoDocumento != null) || (this.idTipoDocumento != null && !this.idTipoDocumento.equals(other.idTipoDocumento))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.TipoDocumento[ idTipoDocumento=" + idTipoDocumento + " ]";
    }
}

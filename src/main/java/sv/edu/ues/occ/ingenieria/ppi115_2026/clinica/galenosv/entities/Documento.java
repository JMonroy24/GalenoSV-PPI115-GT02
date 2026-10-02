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
@Table(name = "documento")
public class Documento extends EntidadVersionada implements IdentificableEntity {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "{identificador.obligatorio}")
    @Id
    @Basic(optional = false)
    @Column(name = "id_documento")
    private UUID idDocumento;

    @NotBlank(message = "{documento.valor.obligatorio}")
    @Size(max = 50, message = "{documento.valor.longitud}")
    @Column(name = "valor", nullable = false, length = 50)
    private String valor;

    @Size(max = 500, message = "{documento.rutaFisica.longitud}")
    @Column(name = "ruta_fisica", length = 500)
    private String rutaFisica;

    @NotNull(message = "{documento.idPersona.obligatorio}")
    @JoinColumn(name = "id_persona", referencedColumnName = "id_persona", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Persona idPersona;

    @NotNull(message = "{documento.idTipoDocumento.obligatorio}")
    @JoinColumn(name = "id_tipo_documento", referencedColumnName = "id_tipo_documento", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private TipoDocumento idTipoDocumento;

    public Documento() {
    }

    public Documento(UUID idDocumento) {
        this.idDocumento = idDocumento;
    }

    public UUID getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(UUID idDocumento) {
        this.idDocumento = idDocumento;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getRutaFisica() {
        return rutaFisica;
    }

    public void setRutaFisica(String rutaFisica) {
        this.rutaFisica = rutaFisica;
    }

    public Persona getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Persona idPersona) {
        this.idPersona = idPersona;
    }

    public TipoDocumento getIdTipoDocumento() {
        return idTipoDocumento;
    }

    public void setIdTipoDocumento(TipoDocumento idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }

    @Override
    public String getIdKey() {
        return idDocumento != null ? idDocumento.toString() : "";
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idDocumento != null ? idDocumento.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Documento)) {
            return false;
        }
        Documento other = (Documento) object;
        if ((this.idDocumento == null && other.idDocumento != null) || (this.idDocumento != null && !this.idDocumento.equals(other.idDocumento))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Documento[ idDocumento=" + idDocumento + " ]";
    }
}

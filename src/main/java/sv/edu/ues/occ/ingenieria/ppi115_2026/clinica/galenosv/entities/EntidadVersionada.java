package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.io.Serializable;

/** Permite que JPA rechace actualizaciones y borrados de versiones obsoletas. */
@MappedSuperclass
@Access(AccessType.FIELD)
public abstract class EntidadVersionada implements Serializable {

    private static final long serialVersionUID = 1L;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    public long getVersion() {
        return version;
    }
}

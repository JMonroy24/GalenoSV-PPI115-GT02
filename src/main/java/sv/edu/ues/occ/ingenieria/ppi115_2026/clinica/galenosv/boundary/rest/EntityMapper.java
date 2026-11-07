package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

public interface EntityMapper<E, D> {

    D toDTO(E entidad);

    E toEntity(D dto);

    void copiar(D dto, E destino);
}

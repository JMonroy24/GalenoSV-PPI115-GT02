package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.io.Serializable;

/** Orden de un atributo persistente, independiente de la capa de presentación. */
public record OrdenConsulta(String campo, boolean ascendente) implements Serializable { }

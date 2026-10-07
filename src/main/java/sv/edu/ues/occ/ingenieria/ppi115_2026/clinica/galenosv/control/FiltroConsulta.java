package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.io.Serializable;

/** Filtro de columna. El DAO valida tanto el atributo como el modo solicitado. */
public record FiltroConsulta(String campo, Object valor, String modo) implements Serializable { }

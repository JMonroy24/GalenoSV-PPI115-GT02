package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import java.util.List;

public record PageResponse<T>(List<T> items, long total, int first, int max) {
}

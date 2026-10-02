package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.validation;

import java.util.Date;

/** Contrato común para los intervalos de atención clínica. */
public interface PeriodoFechas {

    Date getFechaInicio();

    Date getFechaFin();
}

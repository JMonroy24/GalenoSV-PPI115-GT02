package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.util.Date;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.*;

public final class FixturesClinicos {
    private FixturesClinicos() { }

    public static ConsultaProcedimientoPaso pasoValido() {
        var clinica = new Clinica(UUID.randomUUID()); clinica.setActivo(true); clinica.setNombre("Clínica de prueba");
        var rol = new Rol(UUID.randomUUID()); rol.setActivo(true); rol.setNombre("Doctor");
        var persona = new Persona(UUID.randomUUID()); persona.setNombres("Responsable de prueba");
        var responsable = new PersonaRol(UUID.randomUUID());
        responsable.setIdRol(rol); responsable.setIdClinica(clinica); responsable.setIdPersona(persona);
        var consulta = new Consulta(UUID.randomUUID()); consulta.setIdPersonaRol(responsable);
        consulta.setFechaInicio(new Date(100));
        var procedimiento = new Procedimiento(UUID.randomUUID()); procedimiento.setActivo(true); procedimiento.setNombre("Procedimiento de prueba");
        var definicion = new ProcedimientoPaso(UUID.randomUUID());
        definicion.setIdProcedimiento(procedimiento); definicion.setIdRol(rol); definicion.setNombre("Paso final"); definicion.setIndicaFin(true);
        var asignado = new ConsultaProcedimiento(UUID.randomUUID()); asignado.setIdConsulta(consulta); asignado.setIdProcedimiento(procedimiento);
        var paso = new ConsultaProcedimientoPaso(UUID.randomUUID());
        paso.setIdConsultaProcedimiento(asignado); paso.setIdProcedimientoPaso(definicion); paso.setIdPersonaRol(responsable);
        paso.setFechaInicio(new Date(200)); paso.setEstado("PENDIENTE");
        return paso;
    }
}

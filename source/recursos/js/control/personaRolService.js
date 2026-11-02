import defaultService from "./defaultService.js";

/** Asignaciones de roles y búsqueda de pacientes de una clínica. */
export default class personaRolService extends defaultService {
    constructor(urlBase = "personaRol") {
        super(urlBase);
    }

    /** @returns {Promise<import('../entity/defaultResponse.js').default>} Pacientes y total. */
    buscarPacientes(filtro, idClinica, max = 20) {
        const url = `${this._urlBase}/buscarPacientes?filtro=${encodeURIComponent(filtro)}&idClinica=${encodeURIComponent(idClinica)}&max=${encodeURIComponent(max)}`;
        return this._find(url);
    }
}

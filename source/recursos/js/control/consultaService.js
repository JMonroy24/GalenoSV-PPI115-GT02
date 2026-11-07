import defaultService from "./defaultService.js";

/** Registro de consultas clínicas con CRUD heredado. */
export default class consultaService extends defaultService {
    constructor(urlBase = "consulta") {
        super(urlBase);
    }
}

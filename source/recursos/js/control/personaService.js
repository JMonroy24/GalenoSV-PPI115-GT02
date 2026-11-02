import defaultService from "./defaultService.js";

/** Acceso a personas con CRUD heredado. */
export default class personaService extends defaultService {
    constructor(urlBase = "persona") {
        super(urlBase);
    }
}

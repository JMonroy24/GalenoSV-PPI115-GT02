import defaultService from "./defaultService.js";

/** Catálogo de clínicas con CRUD heredado. */
export default class clinicaService extends defaultService {
    constructor(urlBase = "clinica") {
        super(urlBase);
    }
}

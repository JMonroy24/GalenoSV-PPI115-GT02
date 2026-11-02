import defaultService from "./defaultService.js";

/** Catálogo de exámenes con CRUD heredado. */
export default class examenService extends defaultService {
    constructor(urlBase = "examen") {
        super(urlBase);
    }
}

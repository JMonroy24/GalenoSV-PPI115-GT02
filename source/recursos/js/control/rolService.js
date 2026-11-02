import defaultService from "./defaultService.js";

/** Catálogo de roles con CRUD heredado. */
export default class rolService extends defaultService {
    constructor(urlBase = "rol") {
        super(urlBase);
    }
}

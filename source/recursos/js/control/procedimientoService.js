import defaultService from "./defaultService.js";

/** Catálogo de procedimientos con CRUD heredado. */
export default class procedimientoService extends defaultService {
    constructor(urlBase = "procedimiento") {
        super(urlBase);
    }
}

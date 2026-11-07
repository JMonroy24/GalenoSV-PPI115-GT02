import defaultService from "./defaultService.js";

/** Catálogo de tipos de examen con CRUD heredado. */
export default class tipoExamenService extends defaultService {
    constructor(urlBase = "tipoExamen") {
        super(urlBase);
    }
}

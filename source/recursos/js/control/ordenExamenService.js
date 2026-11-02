import defaultService from "./defaultService.js";

/** Órdenes de examen de una consulta con CRUD heredado. */
export default class ordenExamenService extends defaultService {
    constructor(urlBase = "ordenExamen") {
        super(urlBase);
    }
}

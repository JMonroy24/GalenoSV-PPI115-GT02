import defaultService from "./defaultService.js";

/** Pasos de procedimientos: create() envía JSON y exige 201 Created. */
export default class procedimientoPasoService extends defaultService {
    constructor(urlBase = "procedimientoPaso") {
        super(urlBase);
    }
}

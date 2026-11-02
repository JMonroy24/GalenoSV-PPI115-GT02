import defaultService from "./defaultService.js";

/** Secuencias entre pasos: create() envía JSON y exige 201 Created. */
export default class secuenciaService extends defaultService {
    constructor(urlBase = "procedimientoPasoSecuencia") {
        super(urlBase);
    }
}

import defaultService from "./defaultService.js";

/** Captura y actualización de resultados de examen mediante create() y update(). */
export default class examenResultadoService extends defaultService {
    constructor(urlBase = "examenResultado") {
        super(urlBase);
    }
}

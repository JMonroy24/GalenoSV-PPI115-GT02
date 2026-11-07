import defaultService from "./defaultService.js";

/** Acceso a documentos de personas con CRUD heredado. */
export default class documentoService extends defaultService {
    constructor(urlBase = "documento") {
        super(urlBase);
    }
}

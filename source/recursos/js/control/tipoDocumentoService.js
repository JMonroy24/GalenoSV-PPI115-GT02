import defaultService from "./defaultService.js";

/** Catálogo de tipos de documento con CRUD heredado. */
export default class tipoDocumentoService extends defaultService {
    constructor(urlBase = "tipoDocumento") {
        super(urlBase);
    }
}

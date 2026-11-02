import defaultService from "./defaultService.js";

/** Catálogo de tipos de medio de contacto con CRUD heredado. */
export default class tipoMedioContactoService extends defaultService {
    constructor(urlBase = "tipoMedioContacto") {
        super(urlBase);
    }
}

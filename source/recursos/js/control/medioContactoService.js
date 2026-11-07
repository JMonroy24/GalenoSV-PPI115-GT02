import defaultService from "./defaultService.js";

/** Acceso a medios de contacto con CRUD heredado. */
export default class medioContactoService extends defaultService {
    constructor(urlBase = "medioContacto") {
        super(urlBase);
    }
}

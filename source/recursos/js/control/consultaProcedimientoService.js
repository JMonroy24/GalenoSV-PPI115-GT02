import defaultService from "./defaultService.js";

/** Procedimientos asignados a consultas con CRUD heredado. */
export default class consultaProcedimientoService extends defaultService {
    constructor(urlBase = "consultaProcedimiento") {
        super(urlBase);
    }
}

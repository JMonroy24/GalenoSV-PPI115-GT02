import defaultError from "../entity/defaultError.js";
import defaultResponse from "../entity/defaultResponse.js";

/** Acceso REST sin dependencias. Las rutas se resuelven respecto al documento. */
export default class defaultService {
    constructor(urlBase) {
        this._urlBase = urlBase.replace(/\/+$/, "");
    }

    /** @returns {Promise<defaultResponse>} Colección paginada y total de registros. */
    findRange(first = 0, max = 20) {
        const url = `${this._urlBase}?first=${encodeURIComponent(first)}&max=${encodeURIComponent(max)}`;
        return this._find(url);
    }

    /** @returns {Promise<object>} Entidad identificada por su clave. */
    findById(id) {
        const url = `${this._urlBase}/${encodeURIComponent(id)}`;
        return this._request(url, { method: "GET" }, (response) => {
            if (response.status === 200) {
                return response.json();
            }
            throw this._httpError(response, "No fue posible obtener el registro.");
        });
    }

    /** POST JSON: exige 201 y conserva Location y el cuerpo de la respuesta. */
    create(data) {
        return this._send("POST", data, [201]);
    }

    /** PUT JSON sobre la colección; acepta una respuesta con o sin contenido. */
    update(data) {
        return this._send("PUT", data, [200, 204]);
    }

    /** DELETE JSON sobre la colección, incluyendo la entidad en el cuerpo. */
    delete(data) {
        return this._send("DELETE", data, [200, 204]);
    }

    /** Lectura común para paginación y búsquedas especializadas. */
    _find(url) {
        return this._request(url, { method: "GET" }, (response) => {
            if (response.status === 200) {
                return response.json().then((data) => {
                    return new defaultResponse(data, response.headers.get("total-registros"));
                });
            }
            throw this._httpError(response, "No fue posible obtener los registros.");
        });
    }

    /** @returns {Promise<Response>} Respuesta HTTP validada, sin consumir el cuerpo. */
    _send(method, data, expectedStatuses) {
        // Serializar dentro de la promesa convierte también fallos de JSON en defaultError.
        return Promise.resolve().then(() => {
            return this._request(this._urlBase, {
                method,
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data)
            }, (response) => {
                if (expectedStatuses.includes(response.status)) {
                    return response;
                }
                throw this._httpError(response, "No fue posible guardar los cambios.");
            });
        }).catch((error) => {
            throw this._defaultError(error);
        });
    }

    /** Incluye errores HTTP, de red y de parseo JSON en el mismo contrato de rechazo. */
    _request(url, options, handleResponse) {
        return Promise.resolve().then(() => {
            return fetch(url, {
                ...options,
                headers: { Accept: "application/json", ...options.headers }
            });
        }).then((response) => {
            return handleResponse(response);
        }).catch((error) => {
            throw this._defaultError(error);
        });
    }

    _httpError(response, message) {
        const error = new Error(`HTTP ${response.status} ${response.statusText}`.trim());
        error.status = response.status;
        error.response = response;
        return new defaultError(message, error);
    }

    _defaultError(error) {
        if (error instanceof defaultError) {
            return error;
        }
        return new defaultError("No fue posible completar la solicitud al servidor.", error);
    }
}

/** Error de servicio con un mensaje de usuario y el detalle técnico original. */
export default class defaultError {
    constructor(message, error) {
        this.message = message;
        this.error = error;
    }
}

/** Colección y total numérico recibido en la cabecera total-registros. */
export default class defaultResponse {
    constructor(data = [], total = 1) {
        this.data = data;
        this.total = 1;

        if (total !== null && String(total).trim() !== "") {
            const parsedTotal = Number(total);
            if (Number.isSafeInteger(parsedTotal) && parsedTotal >= 0) {
                this.total = parsedTotal;
            }
        }
    }
}

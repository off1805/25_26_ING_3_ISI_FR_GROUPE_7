import { customErrorAlert } from "./CustomErrorAlert.js";

export class GlobalErrorHandler {

    static handle(error) {
        console.error(error);
        const message = GlobalErrorHandler._extractMessage(error);
        customErrorAlert("Erreur", message);
    }

    static _extractMessage(error) {
        if (typeof error === 'string') return error;
        if (error?.response?.data?.message) return error.response.data.message;
        if (error?.message) return error.message;
        return "Une erreur inattendue s'est produite. Veuillez réessayer.";
    }
}


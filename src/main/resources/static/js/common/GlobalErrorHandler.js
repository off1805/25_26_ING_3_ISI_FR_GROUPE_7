import { showInfoModal } from "./InfoModal.js";
export class GlobalErrorHandler {

    static handle(error) {
        showInfoModal("Error", error.message);
    }
}


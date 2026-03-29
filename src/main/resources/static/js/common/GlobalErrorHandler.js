import { showInfoModal } from "./InfoModal.js";
export class GlobalErrorHandler {

    static handle(error) {
    console.log(error);
        showInfoModal("Error", error);
    }
}


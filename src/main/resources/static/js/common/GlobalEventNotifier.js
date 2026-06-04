import { showInfoModal, showErrorToast } from "./InfoModal.js";

export class GlobalEventNotifier {

    static eventWellDone(message) {
        showInfoModal("Success", message);
    }

    static eventError(message) {
        showErrorToast(message);
    }
}

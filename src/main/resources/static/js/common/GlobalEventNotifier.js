import { showInfoModal } from "./InfoModal.js";
export class GlobalEventNotifier {

    static eventWellDone(message) {
        showInfoModal("Success", message);
    }
}


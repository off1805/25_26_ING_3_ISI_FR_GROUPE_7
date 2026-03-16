import { GlobalErrorHandler } from "../../common/GlobalErrorHandler.js";
import { GlobalEventNotifier } from "../../common/GlobalEventNotifier.js";
import { CreateUeUC } from "../application/CreateUeUC.js";
import { UeApi } from "../infrastructure/UeApi.js";

export class UeController {
  constructor(ueApi, createUeUC) {
    this.ueApi = ueApi;
    this.createUeUC = createUeUC;
    this.init();
  }

  init() {
    const addUeForm = document.getElementById("addUeForm");
    if (addUeForm) {
      addUeForm.addEventListener("submit", (e) => this.handleCreateUe(e));
    }
  }

  async handleCreateUe(event) {
    event.preventDefault();

    const form = event.target;
    const submitBtn = form.querySelector('button[type="submit"]');

    const fd = new FormData(form);
    const ueData = {
      libelle: fd.get("libelle"),
      code: fd.get("code"),
      credit: fd.get("credit"),
      volumeHoraireTotal: fd.get("volumeHoraireTotal"),
      description: fd.get("description"),
      couleur: fd.get("couleur"),
      specialiteId: fd.get("specialiteId"),
    };

    if (submitBtn) {
      submitBtn.disabled = true;
      submitBtn.classList.add("opacity-60", "pointer-events-none");
    }

    try {
      await this.createUeUC.execute(ueData);
      if (window.HSOverlay) {
        window.HSOverlay.close("#hs-modal-add-ue");
      }
      GlobalEventNotifier.eventWellDone("UE créée avec succès !");
      window.location.reload();
    } catch (e) {
      GlobalErrorHandler.handle(e);
    } finally {
      if (submitBtn) {
        submitBtn.disabled = false;
        submitBtn.classList.remove("opacity-60", "pointer-events-none");
      }
    }
  }
}

const ueApi = new UeApi();
const createUeUC = new CreateUeUC(ueApi);
new UeController(ueApi, createUeUC);


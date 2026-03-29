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

    // Charger les enseignants quand la modale s'ouvre
    const modalBtn = document.querySelector('[data-hs-overlay="#hs-modal-add-ue"]');
    if (modalBtn) {
      modalBtn.addEventListener("click", () => this.loadTeachers());
    }
  }

  async loadTeachers() {
    const select = document.getElementById("enseignant-select");
    if (!select) return;

    try {
      const response = await this.ueApi.getTeachers();
      const teachers = response?.content ?? response ?? [];

      // Vider les options existantes
      select.innerHTML = "";

      teachers.forEach((t) => {
        const label = t.nom && t.prenom ? `${t.prenom} ${t.nom}` : t.email;
        const option = document.createElement("option");
        option.value = t.id;
        option.textContent = label;
        select.appendChild(option);
      });

      // Réinitialiser le composant Preline après avoir peuplé les options
      if (window.HSSelect) {
        const hsInstance = window.HSSelect.getInstance(select);
        if (hsInstance) {
          hsInstance.destroy();
        }
        window.HSSelect.autoInit();
      }
    } catch (e) {
      console.error("Erreur chargement enseignants", e);
    }
  }

  async handleCreateUe(event) {
    event.preventDefault();

    const form = event.target;
    const submitBtn = form.querySelector('button[type="submit"]');
    const select = document.getElementById("enseignant-select");

    // Récupérer les ids sélectionnés via l'instance Preline ou le select natif
    let enseignantIds = [];
    if (window.HSSelect) {
      const hsInstance = window.HSSelect.getInstance(select);
      if (hsInstance) {
        enseignantIds = hsInstance.value.map(Number);
      }
    }
    if (enseignantIds.length === 0 && select) {
      enseignantIds = Array.from(select.selectedOptions).map((o) => Number(o.value));
    }

    const fd = new FormData(form);
    const ueData = {
      libelle: fd.get("libelle"),
      code: fd.get("code"),
      credit: fd.get("credit"),
      volumeHoraireTotal: fd.get("volumeHoraireTotal"),
      description: fd.get("description"),
      couleur: fd.get("couleur"),
      specialiteId: fd.get("specialiteId"),
      enseignantIds,
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

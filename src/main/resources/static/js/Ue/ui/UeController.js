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
    const container = document.getElementById("enseignant-select-container");
    if (!container) return;

    try {
      const response = await this.ueApi.getTeachers();
      const teachers = response?.content ?? response ?? [];
      this.renderTeacherSelect(container, teachers);
    } catch (e) {
      console.error("Erreur chargement enseignants", e);
    }
  }

  renderTeacherSelect(container, teachers) {
    container.innerHTML = "";

    // Input de recherche
    const searchInput = document.createElement("input");
    searchInput.type = "text";
    searchInput.placeholder = "Rechercher un enseignant...";
    searchInput.className =
      "py-2 px-3 block w-full bg-layer border border-layer-line rounded-lg text-sm focus:border-primary focus:ring-primary mb-2 text-layer-foreground placeholder:text-muted-foreground-2";
    searchInput.id = "teacher-search-input";

    // Liste des options
    const list = document.createElement("div");
    list.id = "teacher-options-list";
    list.className =
      "max-h-48 overflow-y-auto border border-layer-line rounded-lg divide-y divide-layer-line";

    if (teachers.length === 0) {
      list.innerHTML = `<p class="text-sm text-muted-foreground-2 px-3 py-2">Aucun enseignant disponible.</p>`;
    } else {
      teachers.forEach((t) => {
        const label = t.nom && t.prenom ? `${t.prenom} ${t.nom}` : t.email;
        const item = document.createElement("label");
        item.className =
          "flex items-center gap-x-3 px-3 py-2 cursor-pointer hover:bg-muted/40 transition-colors";
        item.innerHTML = `
          <input type="checkbox" name="enseignantIds" value="${t.id}"
            class="shrink-0 size-4 rounded border-layer-line text-primary focus:ring-primary bg-layer">
          <span class="text-sm text-layer-foreground">${label}</span>
        `;
        list.appendChild(item);
      });
    }

    container.appendChild(searchInput);
    container.appendChild(list);

    // Filtre de recherche
    searchInput.addEventListener("input", () => {
      const q = searchInput.value.toLowerCase();
      list.querySelectorAll("label").forEach((item) => {
        const text = item.querySelector("span").textContent.toLowerCase();
        item.style.display = text.includes(q) ? "" : "none";
      });
    });
  }

  async handleCreateUe(event) {
    event.preventDefault();

    const form = event.target;
    const submitBtn = form.querySelector('button[type="submit"]');

    const fd = new FormData(form);

    // Récupérer tous les enseignantIds cochés
    const enseignantIds = fd.getAll("enseignantIds").map(Number);

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

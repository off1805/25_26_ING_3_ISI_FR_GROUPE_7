import { GlobalErrorHandler } from "../../common/GlobalErrorHandler.js";
import { GlobalEventNotifier } from "../../common/GlobalEventNotifier.js";
import { CreateFiliereUC } from "../application/CreateFiliereUC.js";
import { DeleteFiliereUC } from "../application/DeleteFiliereUC.js";
import { FiliereApi } from "../infrastructure/FiliereApi.js";
import { filiereRowUI } from "./filiereRowUI.js";

const SELECTORS = {
    createForm: 'form[data-form="create-filiere"]',
    createFormFields: {
        code: 'input[name="code"]',
        nom: 'input[name="nom"]',
        description: 'textarea[name="description"]',
        cycleId: 'input[name="cycleId"]',
    },
    createModal: '#hs-modal-add-filiere',
    listContainer: '#filiere-list-container',
    btnDeleteFiliere: '.btn-delete-filiere',
};

export class FiliereController {
    constructor(filiereApi, createFiliereUC, deleteFiliereUC) {
        this.filiereApi = filiereApi;
        this.createFiliereUC = createFiliereUC;
        this.deleteFiliereUC = deleteFiliereUC;
        this.init();
    }

    init() {
        this._bindCreateForm();
        this._bindListActions();
    }

    _bindCreateForm() {
        const form = document.querySelector(SELECTORS.createForm);
        if (!form) return;
        form.addEventListener('submit', (e) => this._handleCreateFiliere(e));
    }

    async _handleCreateFiliere(event) {
        event.preventDefault();
        const form = event.target;

        const filiereData = {
            code: form.querySelector(SELECTORS.createFormFields.code)?.value?.trim(),
            nom: form.querySelector(SELECTORS.createFormFields.nom)?.value?.trim(),
            description: form.querySelector(SELECTORS.createFormFields.description)?.value?.trim(),
            cycleId: parseInt(form.querySelector(SELECTORS.createFormFields.cycleId)?.value, 10),
        };

        try {
            const newFiliere = await this.createFiliereUC.execute(filiereData);
            window.HSOverlay.close(SELECTORS.createModal);
            GlobalEventNotifier.eventWellDone("Filière créée avec succès !");
            this._addFiliereToList(newFiliere);
            form.reset();
        } catch (e) {
            GlobalErrorHandler.handle(e);
        }
    }

    _bindListActions() {
        const container = document.querySelector(SELECTORS.listContainer);
        if (!container) return;

        container.addEventListener('click', (e) => {
            const deleteBtn = e.target.closest(SELECTORS.btnDeleteFiliere);
            if (deleteBtn) {
                this._handleDeleteFiliere(deleteBtn);
            }
        });
    }

    async _handleDeleteFiliere(button) {
        if (!confirm("Voulez-vous vraiment supprimer cette filière ?")) return;

        const id = button.dataset.filiereId;
        try {
            await this.deleteFiliereUC.execute(id);
            GlobalEventNotifier.eventWellDone("Filière supprimée avec succès !");
            this._removeFiliereFromList(id);
        } catch (e) {
            GlobalErrorHandler.handle(e);
        }
    }

    _addFiliereToList(filiere) {
        const container = document.querySelector(SELECTORS.listContainer);
        if (!container) return;

        const item = filiereRowUI(filiere);
        container.appendChild(item);
        window.HSStaticMethods?.autoInit();
    }

    _removeFiliereFromList(id) {
        const container = document.querySelector(SELECTORS.listContainer);
        if (!container) return;

        const item = [...container.children].find(li => li.dataset.filiereId === `${id}`);
        item?.remove();
    }
}

// Initialisation
const filiereApi = new FiliereApi();
const createFiliereUC = new CreateFiliereUC(filiereApi);
const deleteFiliereUC = new DeleteFiliereUC(filiereApi);

new FiliereController(filiereApi, createFiliereUC, deleteFiliereUC);

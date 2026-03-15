import { GlobalErrorHandler } from "../../common/GlobalErrorHandler.js";
import { GlobalEventNotifier } from "../../common/GlobalEventNotifier.js";
import { SpecialiteApi } from "../infrastructure/SpecialiteApi.js";
import { CreateSpecialiteUC } from "../application/CreateSpecialiteUC.js";
import { DeleteSpecialiteUC } from "../application/DeleteSpecialiteUC.js";
import { specialiteRowUI } from "./specialiteRowUI.js";

const SELECTORS = {
    createForm: 'form[data-form="create-specialite"]',
    createModal: '#hs-modal-add-specialite',
    listContainer: '#specialite-list-container',
    btnDeleteSpecialite: '.btn-delete-specialite',
};

class SpecialiteController {
    constructor() {
        this.api = new SpecialiteApi();
        this.createUC = new CreateSpecialiteUC(this.api);
        this.deleteUC = new DeleteSpecialiteUC(this.api);
        this.init();
    }

    init() {
        this._bindCreateForm();
        this._bindListActions();
    }

    _bindCreateForm() {
        const form = document.querySelector(SELECTORS.createForm);
        if (!form) return;
        form.addEventListener('submit', (e) => this._handleCreate(e));
    }

    _bindListActions() {
        const container = document.querySelector(SELECTORS.listContainer);
        if (!container) return;

        container.addEventListener('click', (e) => {
            const deleteBtn = e.target.closest(SELECTORS.btnDeleteSpecialite);
            if (deleteBtn) {
                this._handleDelete(deleteBtn);
            }
        });
    }

    async _handleCreate(e) {
        e.preventDefault();
        const form = e.target;
        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());

        try {
            const newSpecialite = await this.createUC.execute(data);
            window.HSOverlay.close(SELECTORS.createModal);
            GlobalEventNotifier.eventWellDone("Spécialité créée avec succès !");
            this._addSpecialiteToList(newSpecialite);
            form.reset();
        } catch (error) {
            GlobalErrorHandler.handle(error);
        }
    }

    async _handleDelete(button) {
        if (!confirm('Êtes-vous sûr de vouloir supprimer cette spécialité ?')) return;

        const id = button.getAttribute('data-specialite-id');
        try {
            await this.deleteUC.execute(id);
            GlobalEventNotifier.eventWellDone("Spécialité supprimée avec succès !");
            this._removeSpecialiteFromList(id);
        } catch (error) {
            GlobalErrorHandler.handle(error);
        }
    }

    _addSpecialiteToList(specialite) {
        const container = document.querySelector(SELECTORS.listContainer);
        if (container) {
            container.insertAdjacentHTML('beforeend', specialiteRowUI(specialite));
            window.HSStaticMethods?.autoInit();
        }
    }

    _removeSpecialiteFromList(id) {
        const row = document.querySelector(`li[data-specialite-id="${id}"]`);
        if (row) {
            row.remove();
        }
    }
}

new SpecialiteController();

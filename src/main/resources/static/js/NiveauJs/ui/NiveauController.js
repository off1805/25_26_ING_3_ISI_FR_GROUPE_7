import { GlobalErrorHandler } from "../../common/GlobalErrorHandler.js";
import { GlobalEventNotifier } from "../../common/GlobalEventNotifier.js";
import { NiveauApi } from "../infrastructure/NiveauApi.js";
import { CreateNiveauUC } from "../application/CreateNiveauUC.js";
import { DeleteNiveauUC } from "../application/DeleteNiveauUC.js";
import { createTaskRow } from "./niveauRowUI.js";

const SELECTORS = {
    createForm: 'form[data-form="create-niveau"]',
    createModal: '#hs-modal-add-niveau',
    listContainer: '#niveau-list-container',
    btnDeleteNiveau: '.btn-delete-niveau',
};

class NiveauController {
    constructor() {
        this.api = new NiveauApi();
        this.createUC = new CreateNiveauUC(this.api);
        this.deleteUC = new DeleteNiveauUC(this.api);
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
            const deleteBtn = e.target.closest(SELECTORS.btnDeleteNiveau);
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
            const newNiveau = await this.createUC.execute(data);
            window.HSOverlay.close(SELECTORS.createModal);
            GlobalEventNotifier.eventWellDone("Niveau créé avec succès !");
            this._addNiveauToList(newNiveau);
            form.reset();
        } catch (error) {
            GlobalErrorHandler.handle(error);
        }
    }

    async _handleDelete(button) {
        if (!confirm('Êtes-vous sûr de vouloir supprimer ce niveau ?')) return;

        const id = button.getAttribute('data-niveau-id');
        try {
            await this.deleteUC.execute(id);
            GlobalEventNotifier.eventWellDone("Niveau supprimé avec succès !");
            this._removeNiveauFromList(id);
        } catch (error) {
            GlobalErrorHandler.handle(error);
        }
    }

    _addNiveauToList(niveau) {
        const container = document.querySelector(SELECTORS.listContainer);
        if (container) {
            container.insertAdjacentHTML('beforeend', createTaskRow(niveau));
            window.HSStaticMethods?.autoInit();
        }
    }

    _removeNiveauFromList(id) {
        const row = document.querySelector(`li[data-niveau-id="${id}"]`);
        if (row) {
            row.remove();
        }
    }
}

new NiveauController();

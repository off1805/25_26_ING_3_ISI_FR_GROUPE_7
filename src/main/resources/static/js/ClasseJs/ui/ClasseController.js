import { GlobalErrorHandler } from "../../common/GlobalErrorHandler.js";
import { GlobalEventNotifier } from "../../common/GlobalEventNotifier.js";
import { ClasseApi } from "../infrastructure/ClasseApi.js";
import { CreateClasseUC } from "../application/CreateClasseUC.js";
import { DeleteClasseUC } from "../application/DeleteClasseUC.js";
import { classeRowUI } from "./classeRowUI.js";

const SELECTORS = {
    createForm: 'form[data-form="create-classe"]',
    createModal: '#hs-modal-add-classe',
    listContainer: '#classe-list-container',
    btnDeleteClasse: '.btn-delete-classe',
};

class ClasseController {
    constructor() {
        this.api = new ClasseApi();
        this.createUC = new CreateClasseUC(this.api);
        this.deleteUC = new DeleteClasseUC(this.api);
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
            const deleteBtn = e.target.closest(SELECTORS.btnDeleteClasse);
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
            const newClasse = await this.createUC.execute(data);
            window.HSOverlay.close(SELECTORS.createModal);
            GlobalEventNotifier.eventWellDone("Classe créée avec succès !");
            this._addClasseToList(newClasse);
            form.reset();
        } catch (error) {
            GlobalErrorHandler.handle(error);
        }
    }

    async _handleDelete(button) {
        if (!confirm('Êtes-vous sûr de vouloir supprimer cette classe ?')) return;

        const id = button.getAttribute('data-classe-id');
        try {
            await this.deleteUC.execute(id);
            GlobalEventNotifier.eventWellDone("Classe supprimée avec succès !");
            this._removeClasseFromList(id);
        } catch (error) {
            GlobalErrorHandler.handle(error);
        }
    }

    _addClasseToList(classe) {
        const container = document.querySelector(SELECTORS.listContainer);
        if (container) {
            container.insertAdjacentHTML('beforeend', classeRowUI(classe));
            window.HSStaticMethods?.autoInit();
        }
    }

    _removeClasseFromList(id) {
        const row = document.querySelector(`li[data-classe-id="${id}"]`);
        if (row) {
            row.remove();
        }
    }
}

new ClasseController();

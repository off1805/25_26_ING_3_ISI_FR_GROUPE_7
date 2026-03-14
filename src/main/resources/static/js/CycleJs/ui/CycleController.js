import { GlobalErrorHandler } from "../../common/GlobalErrorHandler.js";
import { GlobalEventNotifier } from "../../common/GlobalEventNotifier.js";
import { CreateCycleUC } from "../application/CreateCycleUC.js";
import { DeleteCycleUC } from "../application/DeleteCycleUC.js";
import { ModifyCycleStatusUC } from "../application/ModifyCycleStatusUC.js";
import { UpdateCycleUC } from "../application/UpdateCycleUC.js";
import { CycleApi } from "../infrastructure/CycleApi.js";
import { cycleRowTable } from "./cycleRowTable.js";

// -----------------------------------------------------
const SELECTORS = {
    // Formulaire de création
    createForm: 'form[data-form="create-cycle"]',
    createFormFields: {
        name: 'input[name="name"]',
        code: 'input[name="code"]',
        durationYears: 'input[name="durationYears"]',
        description: 'textarea[name="description"]',
    },
    createModal: '#hs-modal-add-cycle',

    // Formulaire de modification
    updateForm: 'form[data-form="update-cycle"]',
    updateFormFields: {
        name: 'input[name="name"]',
        code: 'input[name="code"]',
        durationYears: 'input[name="durationYears"]',
        description: 'textarea[name="description"]',
    },
    updateModal: '#hs-modal-edit-cycle',

    // Tableau
    tableBody: '#cycle-table-body',

    // Boutons dans le tableau (ajoutés dynamiquement — voir cycleRowTable.js)
    btnDeleteCycle: '.btn-delete-cycle',
    btnToggleCycleStatus: '.btn-toggle-cycle-status',

    // Attributs data portés par les boutons
    dataAttributes: {
        cycleId: 'cycleId',     // → dataset.cycleId
        cycleStatus: 'cycleStatus', // → dataset.cycleStatus
    },
};
// ---------------------------------------------

export class CycleController {
    constructor(cycleApi, createCycleUC, deleteCycleUC, modifyCycleStatusUC, updateCycleUC) {
        this.cycleApi = cycleApi;
        this.createCycleUC = createCycleUC;
        this.deleteCycleUC = deleteCycleUC;
        this.modifyCycleStatusUC = modifyCycleStatusUC;
        this.updateCycleUC = updateCycleUC;
        this.init();
    }

    init() {
        this._bindCreateForm();
        this._bindUpdateForm();
        this._bindTableActions();
    }

    //Formulaire de création

    _bindCreateForm() {
        const form = document.querySelector(SELECTORS.createForm);
        if (!form) return;
        form.addEventListener('submit', (e) => this._handleCreateCycle(e));
    }

    async _handleCreateCycle(event) {
        event.preventDefault();
        const form = event.target;

        const cycleData = {
            name: form.querySelector(SELECTORS.createFormFields.name)?.value?.trim(),
            code: form.querySelector(SELECTORS.createFormFields.code)?.value?.trim(),
            durationYears: parseInt(form.querySelector(SELECTORS.createFormFields.durationYears)?.value, 10),
            description: form.querySelector(SELECTORS.createFormFields.description)?.value?.trim(),
        };

        try {
            const newCycle = await this.createCycleUC.execute(cycleData);
            HSOverlay.close(SELECTORS.createModal);
            GlobalEventNotifier.eventWellDone("Cycle créé avec succès !");
            this._addCycleRow(newCycle);
        } catch (e) {
            GlobalErrorHandler.handle(e);
        }
    }

    // Formulaire de modification

    _bindUpdateForm() {
        const form = document.querySelector(SELECTORS.updateForm);
        if (!form) return;
        form.addEventListener('submit', (e) => this._handleUpdateCycle(e));
    }

    async _handleUpdateCycle(event) {
        event.preventDefault();
        const form = event.target;
        const id = form.dataset.cycleId;

        const cycleData = {
            name: form.querySelector(SELECTORS.updateFormFields.name)?.value?.trim(),
            code: form.querySelector(SELECTORS.updateFormFields.code)?.value?.trim(),
            durationYears: parseInt(form.querySelector(SELECTORS.updateFormFields.durationYears)?.value, 10),
            description: form.querySelector(SELECTORS.updateFormFields.description)?.value?.trim(),
        };

        try {
            const updated = await this.updateCycleUC.execute(id, cycleData);
            HSOverlay.close(SELECTORS.updateModal);
            GlobalEventNotifier.eventWellDone("Cycle mis à jour avec succès !");
            this._refreshCycleRow(updated);
        } catch (e) {
            GlobalErrorHandler.handle(e);
        }
    }

    //  Actions sur le tableau

    _bindTableActions() {
        // Délégation d'événements sur le tbody pour couvrir les lignes ajoutées dynamiquement
        const tableBody = document.querySelector(SELECTORS.tableBody);
        if (!tableBody) return;

        tableBody.addEventListener('click', (e) => {
            const deleteBtn = e.target.closest(SELECTORS.btnDeleteCycle);
            if (deleteBtn) {
                this._handleDeleteCycle(deleteBtn);
                return;
            }

            const toggleBtn = e.target.closest(SELECTORS.btnToggleCycleStatus);
            if (toggleBtn) {
                this._handleToggleStatus(toggleBtn);
            }
        });
    }

    async _handleDeleteCycle(button) {
        if (!confirm("Voulez-vous vraiment supprimer ce cycle ?")) return;

        const id = button.dataset[SELECTORS.dataAttributes.cycleId];
        if (!id) {
            alert("Erreur : ID du cycle introuvable.");
            return;
        }

        try {
            await this.deleteCycleUC.execute(id);
            GlobalEventNotifier.eventWellDone("Cycle supprimé avec succès !");
            this._removeCycleRow(id);
        } catch (e) {
            GlobalErrorHandler.handle(e);
        }
    }

    async _handleToggleStatus(button) {
        const id = button.dataset[SELECTORS.dataAttributes.cycleId];
        const currentStatus = button.dataset[SELECTORS.dataAttributes.cycleStatus];
        const newStatus = currentStatus === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
        const label = newStatus === 'ACTIVE' ? 'activer' : 'désactiver';

        if (!confirm(`Voulez-vous vraiment ${label} ce cycle ?`)) return;

        try {
            await this.modifyCycleStatusUC.execute(id, newStatus);
            GlobalEventNotifier.eventWellDone(`Cycle ${label === 'activer' ? 'activé' : 'désactivé'} avec succès !`);
            window.location.reload();
        } catch (e) {
            GlobalErrorHandler.handle(e);
        }
    }

    // Manipulation du DOM

    _addCycleRow(cycle) {
        const tableBody = document.querySelector(SELECTORS.tableBody);
        if (!tableBody) return;

        const row = cycleRowTable(cycle);
        tableBody.appendChild(row);
        window.HSStaticMethods?.autoInit();
    }

    _removeCycleRow(id) {
        const tableBody = document.querySelector(SELECTORS.tableBody);
        if (!tableBody) return;

        const row = [...tableBody.children].find(r => r.dataset.cycleId === `${id}`);
        row?.remove();
    }

    _refreshCycleRow(cycle) {
        const tableBody = document.querySelector(SELECTORS.tableBody);
        if (!tableBody) return;

        const existing = [...tableBody.children].find(r => r.dataset.cycleId === `${cycle.id}`);
        if (existing) {
            const newRow = cycleRowTable(cycle);
            tableBody.replaceChild(newRow, existing);
            window.HSStaticMethods?.autoInit();
        }
    }
}

// Initialisation
const cycleApi = new CycleApi();
const createCycleUC = new CreateCycleUC(cycleApi);
const deleteCycleUC = new DeleteCycleUC(cycleApi);
const modifyCycleStatusUC = new ModifyCycleStatusUC(cycleApi);
const updateCycleUC = new UpdateCycleUC(cycleApi);

new CycleController(cycleApi, createCycleUC, deleteCycleUC, modifyCycleStatusUC, updateCycleUC);

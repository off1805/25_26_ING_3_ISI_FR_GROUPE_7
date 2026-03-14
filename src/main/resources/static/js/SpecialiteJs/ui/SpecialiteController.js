import { GlobalErrorHandler } from "../../common/GlobalErrorHandler.js";
import { GlobalEventNotifier } from "../../common/GlobalEventNotifier.js";
import { CreateSpecialiteUC } from "../application/CreateSpecialiteUC.js";
import { DeleteSpecialiteUC } from "../application/DeleteSpecialiteUC.js";
import { ToggleSpecialiteStatusUC } from "../application/ToggleSpecialiteStatusUC.js";
import { UpdateSpecialiteUC } from "../application/UpdateSpecialiteUC.js";
import { SpecialiteApi } from "../infrastructure/SpecialiteApi.js";
import { specialiteRowTable } from "./specialiteRowTable.js";

// ─────────────────────────────────────────────────────────────────────────────
// CONFIGURATION DES SÉLECTEURS HTML
// Modifiez ici les IDs/classes utilisés dans vos templates Thymeleaf / HTML
// sans avoir à toucher à la logique métier ci-dessous.
// ─────────────────────────────────────────────────────────────────────────────
const SELECTORS = {
    // Formulaire de création
    createForm: 'form[data-form="create-specialite"]',
    createFormFields: {
        code: 'input[name="code"]',
        libelle: 'input[name="libelle"]',
        description: 'textarea[name="description"]',
        brancheCode: 'input[name="brancheCode"]',
        niveauMinimum: 'input[name="niveauMinimum"]',
    },
    createModal: '#hs-modal-add-specialite',

    // Formulaire de modification
    updateForm: 'form[data-form="update-specialite"]',
    updateFormFields: {
        code: 'input[name="code"]',
        libelle: 'input[name="libelle"]',
        description: 'textarea[name="description"]',
        brancheCode: 'input[name="brancheCode"]',
        niveauMinimum: 'input[name="niveauMinimum"]',
    },
    updateModal: '#hs-modal-edit-specialite',

    // Tableau
    tableBody: '#specialite-table-body',

    // Boutons dans le tableau (ajoutés dynamiquement — voir specialiteRowTable.js)
    btnDeleteSpecialite: '.btn-delete-specialite',
    btnToggleSpecialiteStatus: '.btn-toggle-specialite-status',

    // Attributs data portés par les boutons
    dataAttributes: {
        specialiteId: 'specialiteId',       // → dataset.specialiteId
        specialiteActive: 'specialiteActive', // → dataset.specialiteActive ("true"/"false")
    },
};
// ─────────────────────────────────────────────────────────────────────────────

export class SpecialiteController {
    constructor(specialiteApi, createSpecialiteUC, deleteSpecialiteUC, toggleSpecialiteStatusUC, updateSpecialiteUC) {
        this.specialiteApi = specialiteApi;
        this.createSpecialiteUC = createSpecialiteUC;
        this.deleteSpecialiteUC = deleteSpecialiteUC;
        this.toggleSpecialiteStatusUC = toggleSpecialiteStatusUC;
        this.updateSpecialiteUC = updateSpecialiteUC;
        this.init();
    }

    init() {
        this._bindCreateForm();
        this._bindUpdateForm();
        this._bindTableActions();
    }

    // ── Formulaire de création ────────────────────────────────────────────────

    _bindCreateForm() {
        const form = document.querySelector(SELECTORS.createForm);
        if (!form) return;
        form.addEventListener('submit', (e) => this._handleCreateSpecialite(e));
    }

    async _handleCreateSpecialite(event) {
        event.preventDefault();
        const form = event.target;

        const data = {
            code: form.querySelector(SELECTORS.createFormFields.code)?.value?.trim(),
            libelle: form.querySelector(SELECTORS.createFormFields.libelle)?.value?.trim(),
            description: form.querySelector(SELECTORS.createFormFields.description)?.value?.trim(),
            brancheCode: form.querySelector(SELECTORS.createFormFields.brancheCode)?.value?.trim(),
            niveauMinimum: parseInt(form.querySelector(SELECTORS.createFormFields.niveauMinimum)?.value, 10),
        };

        try {
            const newSpecialite = await this.createSpecialiteUC.execute(data);
            HSOverlay.close(SELECTORS.createModal);
            GlobalEventNotifier.eventWellDone("Spécialité créée avec succès !");
            this._addSpecialiteRow(newSpecialite);
        } catch (e) {
            GlobalErrorHandler.handle(e);
        }
    }

    // ── Formulaire de modification ────────────────────────────────────────────

    _bindUpdateForm() {
        const form = document.querySelector(SELECTORS.updateForm);
        if (!form) return;
        form.addEventListener('submit', (e) => this._handleUpdateSpecialite(e));
    }

    async _handleUpdateSpecialite(event) {
        event.preventDefault();
        const form = event.target;
        const id = form.dataset.specialiteId;

        const data = {
            code: form.querySelector(SELECTORS.updateFormFields.code)?.value?.trim(),
            libelle: form.querySelector(SELECTORS.updateFormFields.libelle)?.value?.trim(),
            description: form.querySelector(SELECTORS.updateFormFields.description)?.value?.trim(),
            brancheCode: form.querySelector(SELECTORS.updateFormFields.brancheCode)?.value?.trim(),
            niveauMinimum: parseInt(form.querySelector(SELECTORS.updateFormFields.niveauMinimum)?.value, 10),
        };

        try {
            const updated = await this.updateSpecialiteUC.execute(id, data);
            HSOverlay.close(SELECTORS.updateModal);
            GlobalEventNotifier.eventWellDone("Spécialité mise à jour avec succès !");
            this._refreshSpecialiteRow(updated);
        } catch (e) {
            GlobalErrorHandler.handle(e);
        }
    }

    // ── Actions sur le tableau ────────────────────────────────────────────────

    _bindTableActions() {
        const tableBody = document.querySelector(SELECTORS.tableBody);
        if (!tableBody) return;

        // Délégation d'événements pour couvrir les lignes ajoutées dynamiquement
        tableBody.addEventListener('click', (e) => {
            const deleteBtn = e.target.closest(SELECTORS.btnDeleteSpecialite);
            if (deleteBtn) {
                this._handleDeleteSpecialite(deleteBtn);
                return;
            }

            const toggleBtn = e.target.closest(SELECTORS.btnToggleSpecialiteStatus);
            if (toggleBtn) {
                this._handleToggleStatus(toggleBtn);
            }
        });
    }

    async _handleDeleteSpecialite(button) {
        if (!confirm("Voulez-vous vraiment supprimer cette spécialité ?")) return;

        const id = button.dataset[SELECTORS.dataAttributes.specialiteId];
        if (!id) {
            alert("Erreur : ID de la spécialité introuvable.");
            return;
        }

        try {
            await this.deleteSpecialiteUC.execute(id);
            GlobalEventNotifier.eventWellDone("Spécialité supprimée avec succès !");
            this._removeSpecialiteRow(id);
        } catch (e) {
            GlobalErrorHandler.handle(e);
        }
    }

    async _handleToggleStatus(button) {
        const id = button.dataset[SELECTORS.dataAttributes.specialiteId];
        const isActive = button.dataset[SELECTORS.dataAttributes.specialiteActive] === 'true';
        const label = isActive ? 'désactiver' : 'activer';

        if (!confirm(`Voulez-vous vraiment ${label} cette spécialité ?`)) return;

        try {
            await this.toggleSpecialiteStatusUC.execute(id, !isActive);
            GlobalEventNotifier.eventWellDone(`Spécialité ${isActive ? 'désactivée' : 'activée'} avec succès !`);
            window.location.reload();
        } catch (e) {
            GlobalErrorHandler.handle(e);
        }
    }

    // ── Manipulation du DOM ───────────────────────────────────────────────────

    _addSpecialiteRow(specialite) {
        const tableBody = document.querySelector(SELECTORS.tableBody);
        if (!tableBody) return;

        const row = specialiteRowTable(specialite);
        tableBody.appendChild(row);
        window.HSStaticMethods?.autoInit();
    }

    _removeSpecialiteRow(id) {
        const tableBody = document.querySelector(SELECTORS.tableBody);
        if (!tableBody) return;

        const row = [...tableBody.children].find(r => r.dataset.specialiteId === `${id}`);
        row?.remove();
    }

    _refreshSpecialiteRow(specialite) {
        const tableBody = document.querySelector(SELECTORS.tableBody);
        if (!tableBody) return;

        const existing = [...tableBody.children].find(r => r.dataset.specialiteId === `${specialite.id}`);
        if (existing) {
            const newRow = specialiteRowTable(specialite);
            tableBody.replaceChild(newRow, existing);
            window.HSStaticMethods?.autoInit();
        }
    }
}

// ── Initialisation ────────────────────────────────────────────────────────────
const specialiteApi = new SpecialiteApi();
const createSpecialiteUC = new CreateSpecialiteUC(specialiteApi);
const deleteSpecialiteUC = new DeleteSpecialiteUC(specialiteApi);
const toggleSpecialiteStatusUC = new ToggleSpecialiteStatusUC(specialiteApi);
const updateSpecialiteUC = new UpdateSpecialiteUC(specialiteApi);

new SpecialiteController(specialiteApi, createSpecialiteUC, deleteSpecialiteUC, toggleSpecialiteStatusUC, updateSpecialiteUC);

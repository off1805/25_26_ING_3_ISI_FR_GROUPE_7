import { GlobalErrorHandler } from "../../common/GlobalErrorHandler.js";
import { CreateEmploiTempsUC } from "../application/CreateEmploiTempsUC.js";
import { DeleteEmploiTempsUC } from "../application/DeleteEmploiTempsUC.js";
import { UpdateEmploiTempsUC } from "../application/UpdateEmploiTempsUC.js";
import { RetrieveEmploiTempsUC } from "../application/RetrieveEmploiTempsUC.js";
import { AddSeanceToEmploiUC } from "../application/AddSeanceToEmploiUC.js";
import { RemoveSeanceFromEmploiUC } from "../application/RemoveSeanceFromEmploiUC.js";
import { EmploiTempsApi } from "../infrastructure/EmploiTempsApi.js";
import { emploiTempsRowTable } from "./emploiTempsRowTable.js";
import { GlobalEventNotifier } from "../../common/GlobalEventNotifier.js";

export class EmploiTempsController {
    constructor(
        emploiTempsApi,
        createEmploiTempsUC,
        deleteEmploiTempsUC,
        updateEmploiTempsUC,
        retrieveEmploiTempsUC,
        addSeanceToEmploiUC,
        removeSeanceFromEmploiUC
    ) {
        this.emploiTempsApi = emploiTempsApi;
        this.createEmploiTempsUC = createEmploiTempsUC;
        this.deleteEmploiTempsUC = deleteEmploiTempsUC;
        this.updateEmploiTempsUC = updateEmploiTempsUC;
        this.retrieveEmploiTempsUC = retrieveEmploiTempsUC;
        this.addSeanceToEmploiUC = addSeanceToEmploiUC;
        this.removeSeanceFromEmploiUC = removeSeanceFromEmploiUC;

        this.currentEmploiId = null;
        this.init();
    }

    init() {

        const addEmploiForm = document.getElementById('addEmploiForm');
        if (addEmploiForm) {
            addEmploiForm.addEventListener('submit', (e) => this.handleCreateEmploi(e));
        }


        const editEmploiForm = document.getElementById('editEmploiForm');
        if (editEmploiForm) {
            editEmploiForm.addEventListener('submit', (e) => this.handleEditEmploi(e));
        }


        const addSeanceForm = document.getElementById('addSeanceToEmploiForm');
        if (addSeanceForm) {
            addSeanceForm.addEventListener('submit', (e) => this.handleAddSeance(e));
        }


        const searchBtn = document.getElementById('searchEmploisBtn');
        if (searchBtn) {
            searchBtn.addEventListener('click', (e) => this.handleSearch(e));
        }


        const activeBtn = document.getElementById('showActiveEmplois');
        if (activeBtn) {
            activeBtn.addEventListener('click', () => this.loadActiveEmplois());
        }

        const deletedBtn = document.getElementById('showDeletedEmplois');
        if (deletedBtn) {
            deletedBtn.addEventListener('click', () => this.loadDeletedEmplois());
        }


        this._bindTableActions(document.getElementById('table-body'));
    }

    _bindTableActions(container) {
        if (!container) return;

        container.querySelectorAll('.btn-delete-emploi').forEach(btn => {
            btn.addEventListener('click', (e) => this.handleDeleteEmploi(e));
        });

        container.querySelectorAll('.btn-edit-emploi').forEach(btn => {
            btn.addEventListener('click', (e) => this.handleOpenEditModal(e));
        });

        container.querySelectorAll('.btn-add-seance-to-emploi').forEach(btn => {
            btn.addEventListener('click', (e) => this.handleOpenAddSeanceModal(e));
        });

        container.querySelectorAll('.btn-view-seances').forEach(btn => {
            btn.addEventListener('click', (e) => this.handleViewSeances(e));
        });
    }


    async handleCreateEmploi(event) {
        event.preventDefault();
        const formData = new FormData(event.target);

        const emploiData = {
            libelle: formData.get('libelle'),
            dateDebut: formData.get('dateDebut'),
            dateFin: formData.get('dateFin'),
            semaine: formData.get('semaine') || null,
            filiereId: formData.get('filiereId'),
            niveauId: formData.get('niveauId')
        };

        try {
            const newEmploi = await this.createEmploiTempsUC.execute(emploiData);
            HSOverlay.close('#hs-modal-add-emploi');
            GlobalEventNotifier.eventWellDone("Emploi du temps créé avec succès !");
            this._addEmploiRow(newEmploi);
        } catch (e) {
            GlobalErrorHandler.handle(e);
        }
    }

    _addEmploiRow(emploi) {
        const tableBody = document.getElementById('table-body');


        const filiereNom = emploi.filiereNom || `Filière #${emploi.filiereId}`;
        const niveauNom = emploi.niveauNom || `Niveau #${emploi.niveauId}`;
        const seancesCount = emploi.seances ? emploi.seances.length : 0;

        const row = emploiTempsRowTable(
            emploi.libelle,
            emploi.dateDebut,
            emploi.dateFin,
            emploi.semaine,
            filiereNom,
            niveauNom,
            emploi.id,
            seancesCount
        );

        row.dataset.emploiId = emploi.id;
        row.dataset.libelle = emploi.libelle;
        row.dataset.dateDebut = emploi.dateDebut;
        row.dataset.dateFin = emploi.dateFin;
        row.dataset.semaine = emploi.semaine || '';

        row.querySelector('.btn-delete-emploi')?.addEventListener('click', (e) => this.handleDeleteEmploi(e));
        row.querySelector('.btn-edit-emploi')?.addEventListener('click', (e) => this.handleOpenEditModal(e));
        row.querySelector('.btn-add-seance-to-emploi')?.addEventListener('click', (e) => this.handleOpenAddSeanceModal(e));
        row.querySelector('.btn-view-seances')?.addEventListener('click', (e) => this.handleViewSeances(e));

        tableBody.appendChild(row);
        this._syncEmptyState();
    }

    _removeEmploiRow(id) {
        const table = document.getElementById("table-body");
        const target = [...table.children].find(tr => tr.dataset.emploiId === `${id}`);
        if (target) target.remove();
        this._syncEmptyState();
    }

    _syncEmptyState() {
        const tbody = document.getElementById('table-body');
        const emptyRow = document.getElementById('empty-state-row');
        if (!tbody || !emptyRow) return;
        const realRows = tbody.querySelectorAll('tr:not(#empty-state-row)').length;
        emptyRow.classList.toggle('hidden', realRows > 0);
    }


    async handleDeleteEmploi(event) {
        if (!confirm("Voulez-vous vraiment supprimer cet emploi du temps ?")) return;

        const button = event.currentTarget;
        const emploiId = button.dataset.emploiId;

        if (!emploiId) {
            alert("Erreur: ID emploi du temps introuvable.");
            return;
        }

        try {
            await this.deleteEmploiTempsUC.execute(emploiId);
            GlobalEventNotifier.eventWellDone("Emploi du temps supprimé avec succès !");
            this._removeEmploiRow(emploiId);
        } catch (e) {
            GlobalErrorHandler.handle(e);
        }
    }


    handleOpenEditModal(event) {
        const button = event.currentTarget;
        const row = button.closest('tr');
        const emploiId = row?.dataset.emploiId || button.dataset.emploiId;
        const libelle = row?.dataset.libelle || '';
        const dateDebut = row?.dataset.dateDebut || '';
        const dateFin = row?.dataset.dateFin || '';
        const semaine = row?.dataset.semaine || '';

        document.getElementById('edit-emploi-id').value = emploiId;
        document.getElementById('edit-emploi-libelle').value = libelle;
        document.getElementById('edit-emploi-dateDebut').value = dateDebut;
        document.getElementById('edit-emploi-dateFin').value = dateFin;
        document.getElementById('edit-emploi-semaine').value = semaine;

        HSOverlay.open('#hs-modal-edit-emploi');
    }


    async handleEditEmploi(event) {
        event.preventDefault();
        const formData = new FormData(event.target);

        const emploiId = formData.get('id');
        const emploiData = {
            libelle: formData.get('libelle'),
            dateDebut: formData.get('dateDebut'),
            dateFin: formData.get('dateFin'),
            semaine: formData.get('semaine') || null,
            filiereId: formData.get('filiereId'),
            niveauId: formData.get('niveauId')
        };

        try {
            const updatedEmploi = await this.updateEmploiTempsUC.execute(emploiId, emploiData);
            HSOverlay.close('#hs-modal-edit-emploi');
            GlobalEventNotifier.eventWellDone("Emploi du temps modifié avec succès !");
            this._updateEmploiRow(updatedEmploi);
        } catch (e) {
            GlobalErrorHandler.handle(e);
        }
    }

    _updateEmploiRow(emploi) {
        const tbody = document.getElementById('table-body');
        const oldRow = [...tbody.children].find(tr => tr.dataset.emploiId === `${emploi.id}`);
        if (!oldRow) return;

        const filiereNom = emploi.filiereNom || `Filière #${emploi.filiereId}`;
        const niveauNom = emploi.niveauNom || `Niveau #${emploi.niveauId}`;
        const seancesCount = emploi.seances ? emploi.seances.length : 0;

        const newRow = emploiTempsRowTable(
            emploi.libelle,
            emploi.dateDebut,
            emploi.dateFin,
            emploi.semaine,
            filiereNom,
            niveauNom,
            emploi.id,
            seancesCount
        );

        newRow.dataset.emploiId = emploi.id;
        newRow.dataset.libelle = emploi.libelle;
        newRow.dataset.dateDebut = emploi.dateDebut;
        newRow.dataset.dateFin = emploi.dateFin;
        newRow.dataset.semaine = emploi.semaine || '';

        newRow.querySelector('.btn-delete-emploi')?.addEventListener('click', (e) => this.handleDeleteEmploi(e));
        newRow.querySelector('.btn-edit-emploi')?.addEventListener('click', (e) => this.handleOpenEditModal(e));
        newRow.querySelector('.btn-add-seance-to-emploi')?.addEventListener('click', (e) => this.handleOpenAddSeanceModal(e));
        newRow.querySelector('.btn-view-seances')?.addEventListener('click', (e) => this.handleViewSeances(e));

        tbody.replaceChild(newRow, oldRow);
    }


    handleOpenAddSeanceModal(event) {
        const button = event.currentTarget;
        const row = button.closest('tr');
        this.currentEmploiId = row?.dataset.emploiId || button.dataset.emploiId;

        document.getElementById('add-seance-emploi-id').value = this.currentEmploiId;
        document.getElementById('modal-emploi-title').textContent =
            `Ajouter une séance à l'emploi du temps : ${row?.dataset.libelle || ''}`;

        HSOverlay.open('#hs-modal-add-seance-to-emploi');
    }


    async handleAddSeance(event) {
        event.preventDefault();
        const formData = new FormData(event.target);

        const emploiTempsId = formData.get('emploiTempsId');
        const seanceId = formData.get('seanceId');

        if (!emploiTempsId || !seanceId) {
            alert("Veuillez sélectionner une séance");
            return;
        }

        try {
            const updatedEmploi = await this.addSeanceToEmploiUC.execute(emploiTempsId, seanceId);
            HSOverlay.close('#hs-modal-add-seance-to-emploi');
            GlobalEventNotifier.eventWellDone("Séance ajoutée avec succès !");
            this._updateEmploiRow(updatedEmploi);
        } catch (e) {
            GlobalErrorHandler.handle(e);
        }
    }


    async handleViewSeances(event) {
        const button = event.currentTarget;
        const emploiId = button.dataset.emploiId;

        try {
            const emploi = await this.retrieveEmploiTempsUC.getById(emploiId);
            this._displaySeancesModal(emploi);
        } catch (e) {
            GlobalErrorHandler.handle(e);
        }
    }

    _displaySeancesModal(emploi) {

        const modalBody = document.getElementById('seances-list-modal-body');
        if (!modalBody) return;

        let html = '';
        if (emploi.seances && emploi.seances.length > 0) {
            emploi.seances.forEach(s => {
                html += `
                    <div class="flex items-center justify-between p-3 border-b">
                        <div>
                            <p class="font-medium">${s.libelle}</p>
                            <p class="text-sm text-gray-500">${s.dateSeance} ${s.heureDebut}-${s.heureFin}</p>
                        </div>
                        <button class="btn-remove-seance text-red-500" data-seance-id="${s.id}">
                            Retirer
                        </button>
                    </div>
                `;
            });
        } else {
            html = '<p class="p-4 text-center text-gray-500">Aucune séance dans cet emploi du temps</p>';
        }

        modalBody.innerHTML = html;

        // Bind des boutons de retrait
        modalBody.querySelectorAll('.btn-remove-seance').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const seanceId = e.target.dataset.seanceId;
                this._handleRemoveSeance(emploi.id, seanceId);
            });
        });

        HSOverlay.open('#hs-modal-view-seances');
    }

    async _handleRemoveSeance(emploiId, seanceId) {
        if (!confirm("Retirer cette séance de l'emploi du temps ?")) return;

        try {
            const updatedEmploi = await this.removeSeanceFromEmploiUC.execute(emploiId, seanceId);
            HSOverlay.close('#hs-modal-view-seances');
            GlobalEventNotifier.eventWellDone("Séance retirée avec succès !");
            this._updateEmploiRow(updatedEmploi);
        } catch (e) {
            GlobalErrorHandler.handle(e);
        }
    }

    // ── RECHERCHE ─────────────────────────────────────────────────
    async handleSearch(event) {
        const filtre = {
            filiereId: document.getElementById('searchFiliere')?.value,
            niveauId: document.getElementById('searchNiveau')?.value,
            semaine: document.getElementById('searchSemaine')?.value,
            date: document.getElementById('searchDate')?.value
        };

        // Enlever les champs vides
        Object.keys(filtre).forEach(key =>
            filtre[key] === '' || filtre[key] === null || filtre[key] === undefined ? delete filtre[key] : {}
        );

        try {
            const emplois = await this.retrieveEmploiTempsUC.execute(filtre);
            this._renderTable(emplois);
        } catch (e) {
            GlobalErrorHandler.handle(e);
        }
    }

    async loadActiveEmplois() {
        try {
            const emplois = await this.retrieveEmploiTempsUC.getActive();
            this._renderTable(emplois);
        } catch (e) {
            GlobalErrorHandler.handle(e);
        }
    }

    async loadDeletedEmplois() {
        try {
            const emplois = await this.retrieveEmploiTempsUC.getDeleted();
            this._renderTable(emplois);
        } catch (e) {
            GlobalErrorHandler.handle(e);
        }
    }

    _renderTable(emplois) {
        const tbody = document.getElementById('table-body');
        tbody.innerHTML = '';
        emplois.forEach(e => this._addEmploiRow(e));
    }
}

// Initialisation
const emploiTempsApi = new EmploiTempsApi();
const createEmploiTempsUC = new CreateEmploiTempsUC(emploiTempsApi);
const deleteEmploiTempsUC = new DeleteEmploiTempsUC(emploiTempsApi);
const updateEmploiTempsUC = new UpdateEmploiTempsUC(emploiTempsApi);
const retrieveEmploiTempsUC = new RetrieveEmploiTempsUC(emploiTempsApi);
const addSeanceToEmploiUC = new AddSeanceToEmploiUC(emploiTempsApi);
const removeSeanceFromEmploiUC = new RemoveSeanceFromEmploiUC(emploiTempsApi);

// Attendre que le DOM soit chargé
document.addEventListener('DOMContentLoaded', () => {
    new EmploiTempsController(
        emploiTempsApi,
        createEmploiTempsUC,
        deleteEmploiTempsUC,
        updateEmploiTempsUC,
        retrieveEmploiTempsUC,
        addSeanceToEmploiUC,
        removeSeanceFromEmploiUC
    );
});
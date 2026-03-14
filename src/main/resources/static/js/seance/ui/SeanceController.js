import { GlobalErrorHandler } from "../../common/GlobalErrorHandler.js";
import { CreateSeanceUC } from "../application/CreateSeanceUC.js";
import { DeleteSeanceUC } from "../application/DeleteSeanceUC.js";
import { UpdateSeanceUC } from "../application/UpdateSeanceUC.js";
import { RetrieveSeanceUC } from "../application/RetrieveSeanceUC.js";
import { SeanceApi } from "../infrastructure/SeanceApi.js";
import { seanceRowTable } from "./seanceRowTable.js";
import { GlobalEventNotifier } from "../../common/GlobalEventNotifier.js";

export class SeanceController {
    constructor(seanceApi, createSeanceUC, deleteSeanceUC, updateSeanceUC, retrieveSeanceUC) {
        this.seanceApi = seanceApi;
        this.createSeanceUC = createSeanceUC;
        this.deleteSeanceUC = deleteSeanceUC;
        this.updateSeanceUC = updateSeanceUC;
        this.retrieveSeanceUC = retrieveSeanceUC;
        this.init();
    }

    init() {

        const addSeanceForm = document.getElementById('addSeanceForm');
        if (addSeanceForm) {
            addSeanceForm.addEventListener('submit', (e) => this.handleCreateSeance(e));
        }


        const editSeanceForm = document.getElementById('editSeanceForm');
        if (editSeanceForm) {
            editSeanceForm.addEventListener('submit', (e) => this.handleEditSeance(e));
        }


        const searchBtn = document.getElementById('searchSeancesBtn');
        if (searchBtn) {
            searchBtn.addEventListener('click', (e) => this.handleSearch(e));
        }


        this._bindTableActions(document.getElementById('table-body'));
    }

    _bindTableActions(container) {
        if (!container) return;

        container.querySelectorAll('.btn-delete-seance').forEach(btn => {
            btn.addEventListener('click', (e) => this.handleDeleteSeance(e));
        });

        container.querySelectorAll('.btn-edit-seance').forEach(btn => {
            btn.addEventListener('click', (e) => this.handleOpenEditModal(e));
        });
    }


    async handleCreateSeance(event) {
        event.preventDefault();
        const formData = new FormData(event.target);

        const seanceData = {
            libelle: formData.get('libelle'),
            dateSeance: formData.get('dateSeance'),
            heureDebut: formData.get('heureDebut'),
            heureFin: formData.get('heureFin'),
            coursId: parseInt(formData.get('coursId')),
            enseignantId: parseInt(formData.get('enseignantId')),
            salle: formData.get('salle')
        };

        try {
            const newSeance = await this.createSeanceUC.execute(seanceData);
            HSOverlay.close('#hs-modal-add-seance');
            GlobalEventNotifier.eventWellDone("Séance créée avec succès !");
            this._addSeanceRow(newSeance);
        } catch (e) {
            GlobalErrorHandler.handle(e);
        }
    }

    _addSeanceRow(seance) {
        const tableBody = document.getElementById('table-body');
        const row = seanceRowTable(
            seance.libelle,
            seance.dateSeance,
            seance.heureDebut,
            seance.heureFin,
            seance.salle,
            seance.id,
            `Enseignant #${seance.enseignantId}`,
            `Cours #${seance.coursId}`
        );

        row.dataset.seanceId = seance.id;


        row.querySelector('.btn-delete-seance')?.addEventListener('click', (e) => this.handleDeleteSeance(e));
        row.querySelector('.btn-edit-seance')?.addEventListener('click', (e) => this.handleOpenEditModal(e));

        tableBody.appendChild(row);
        this._syncEmptyState();
    }

    _removeSeanceRow(id) {
        const table = document.getElementById("table-body");
        const target = [...table.children].find(tr => tr.dataset.seanceId === `${id}`);
        if (target) target.remove();
        this._syncEmptyState();
    }

    _syncEmptyState() {

    }


    async handleDeleteSeance(event) {
        if (!confirm("Voulez-vous vraiment supprimer cette séance ?")) return;

        const button = event.currentTarget;
        const seanceId = button.dataset.seanceId;

        if (!seanceId) {
            alert("Erreur: ID séance introuvable.");
            return;
        }

        try {
            await this.deleteSeanceUC.execute(seanceId);
            GlobalEventNotifier.eventWellDone("Séance supprimée avec succès !");
            this._removeSeanceRow(seanceId);
        } catch (e) {
            GlobalErrorHandler.handle(e);
        }
    }


    handleOpenEditModal(event) {
        const button = event.currentTarget;
        const row = button.closest('tr');
        const seanceId = row?.dataset.seanceId || button.dataset.seanceId;
        const libelle = row?.dataset.libelle || '';
        const dateSeance = row?.dataset.dateSeance || '';
        const heureDebut = row?.dataset.heureDebut || '';
        const heureFin = row?.dataset.heureFin || '';
        const salle = row?.dataset.salle || '';

        document.getElementById('edit-seance-id').value = seanceId;
        document.getElementById('edit-seance-libelle').value = libelle;
        document.getElementById('edit-seance-date').value = dateSeance;
        document.getElementById('edit-seance-debut').value = heureDebut;
        document.getElementById('edit-seance-fin').value = heureFin;
        document.getElementById('edit-seance-salle').value = salle;

        HSOverlay.open('#hs-modal-edit-seance');
    }


    async handleEditSeance(event) {
        event.preventDefault();
        const formData = new FormData(event.target);

        const seanceId = formData.get('id');
        const seanceData = {
            libelle: formData.get('libelle'),
            heureDebut: formData.get('heureDebut'),
            heureFin: formData.get('heureFin'),
            salle: formData.get('salle')
        };

        try {
            const updatedSeance = await this.updateSeanceUC.execute(seanceId, seanceData);
            HSOverlay.close('#hs-modal-edit-seance');
            GlobalEventNotifier.eventWellDone("Séance modifiée avec succès !");
            this._updateSeanceRow(updatedSeance);
        } catch (e) {
            GlobalErrorHandler.handle(e);
        }
    }

    _updateSeanceRow(seance) {

    }


    async handleSearch(event) {
        const filtre = {
            date: document.getElementById('searchDate')?.value,
            enseignantId: document.getElementById('searchEnseignant')?.value,
            coursId: document.getElementById('searchCours')?.value,
            salle: document.getElementById('searchSalle')?.value
        };

        try {
            const seances = await this.retrieveSeanceUC.execute(filtre);
            this._renderTable(seances);
        } catch (e) {
            GlobalErrorHandler.handle(e);
        }
    }

    _renderTable(seances) {
        const tbody = document.getElementById('table-body');
        tbody.innerHTML = '';
        seances.forEach(s => this._addSeanceRow(s));
    }
}


const seanceApi = new SeanceApi();
const createSeanceUC = new CreateSeanceUC(seanceApi);
const deleteSeanceUC = new DeleteSeanceUC(seanceApi);
const updateSeanceUC = new UpdateSeanceUC(seanceApi);
const retrieveSeanceUC = new RetrieveSeanceUC(seanceApi);
new SeanceController(seanceApi, createSeanceUC, deleteSeanceUC, updateSeanceUC, retrieveSeanceUC);
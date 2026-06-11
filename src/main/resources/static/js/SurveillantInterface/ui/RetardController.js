import { RetardApi } from '../infrastructure/RetardApi.js';
import { retardSemaineUI } from './retardSemaineUI.js';
import { retardCellTitle, RETARD_CELL_ON_TIME_CLASSES, RETARD_CELL_LATE_CLASSES } from './retardCellUI.js';

const SELECTORS = {
    rightPanel:   '#retard-right-panel',
    classeRow:    '.retard-classe-row',
    classeSearch: '#classe-search',
    cell:         '.retard-cell',
};

class RetardController {
    constructor(retardApi) {
        this.retardApi = retardApi;
        this.rightPanel = document.querySelector(SELECTORS.rightPanel);
        this.init();
    }

    init() {
        this._bindCells();
        this._bindClasseRows();
        this._bindClasseSearch();
    }

    // ── Sélection d'une classe (appel JSON, sans rendu Thymeleaf) ───

    _bindClasseRows() {
        document.querySelectorAll(SELECTORS.classeRow).forEach((row) => {
            row.addEventListener('click', (event) => {
                event.preventDefault();
                const classeId = row.getAttribute('data-classe-id');
                if (classeId) this._loadClasse(row, classeId);
            });
        });
    }

    async _loadClasse(row, classeId) {
        try {
            const semaine = await this.retardApi.getSemaine(classeId);
            this.rightPanel.innerHTML = retardSemaineUI(semaine);
            this._bindCells(this.rightPanel);
            this._highlightActiveClasse(row);
        } catch (e) {
            console.error('Impossible de charger les retards de la classe :', e);
        }
    }

    _highlightActiveClasse(activeRow) {
        document.querySelectorAll(SELECTORS.classeRow).forEach((row) => {
            row.classList.toggle('bg-primary/10', row === activeRow);
        });
    }

    // ── Recherche de classe ─────────────────────────────────────────

    _bindClasseSearch() {
        const input = document.querySelector(SELECTORS.classeSearch);
        if (!input) return;
        input.addEventListener('input', (event) => {
            const query = event.target.value.toLowerCase().trim();
            document.querySelectorAll(SELECTORS.classeRow).forEach((row) => {
                row.classList.toggle('hidden', query.length > 0 && !(row.dataset.name || '').includes(query));
            });
        });
    }

    // ── Bascule d'une cellule de retard (sauvegarde immédiate) ──────

    _bindCells(container = document) {
        container.querySelectorAll(SELECTORS.cell).forEach((button) => {
            button.addEventListener('click', (event) => this._handleCellClick(event));
        });
    }

    async _handleCellClick(event) {
        const button = event.currentTarget;
        if (button.disabled) return;

        const infoId = button.getAttribute('data-info-id');
        if (!infoId) return;

        button.disabled = true;
        try {
            const result = await this.retardApi.toggleInfoRow(infoId);
            this._applyCellState(button, result.enRetard);
        } catch (e) {
            console.error('Impossible de mettre à jour le retard :', e);
        } finally {
            button.disabled = false;
        }
    }

    _applyCellState(button, enRetard) {
        button.classList.remove(...RETARD_CELL_ON_TIME_CLASSES, ...RETARD_CELL_LATE_CLASSES);
        button.classList.add(...(enRetard ? RETARD_CELL_LATE_CLASSES : RETARD_CELL_ON_TIME_CLASSES));
        button.title = retardCellTitle(enRetard);
    }
}

new RetardController(new RetardApi());

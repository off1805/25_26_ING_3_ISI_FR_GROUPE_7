import { GlobalErrorHandler } from "../../common/GlobalErrorHandler.js";
import { GlobalEventNotifier } from "../../common/GlobalEventNotifier.js";

export class APSubjectsController {
    constructor(specialiteApi, createUeUC, retrieveUeUC) {
        this._specialiteApi = specialiteApi;
        this._createUeUC   = createUeUC;
        this._retrieveUeUC = retrieveUeUC;

        this._allNiveaux          = [];
        this._currentLevelId      = null;
        this._currentSpecialtyId  = null;
        this._subjectsBySpecialty = {};
    }

    init() {
        const data = window.KEMO_DATA || {};
        this._allNiveaux = data.niveaux || [];

        this._initLevelTabs();
        this._initSearchAndFilters();
        this._initFormHandlers();

        document.querySelector("#specialty-tabs")?.addEventListener('click', (event) => {
            const target = event.target.closest(".specialty-tab");
            if (target) {
                this._handleSpecialtyChange(parseInt(target.getAttribute('data-specialty-id')));
            }
        });

        if (this._allNiveaux.length > 0) {
            this._handleLevelChange(this._allNiveaux[0].id);
        }
    }

    // ── Level tabs ────────────────────────────────────────────────────────────

    _initLevelTabs() {
        document.querySelectorAll('.level-filter-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                this._handleLevelChange(parseInt(btn.getAttribute('data-level-id')));
            });
        });
    }

    _handleLevelChange(levelId) {
        this._currentLevelId = levelId;
        document.querySelectorAll('.level-filter-btn').forEach(btn => {
            const isActive = parseInt(btn.getAttribute('data-level-id')) === levelId;
            btn.classList.toggle('active', isActive);
            btn.setAttribute('aria-selected', String(isActive));
        });
        this._renderSpecialtyTabs(levelId);
    }

    // ── Specialty tabs ────────────────────────────────────────────────────────

    async _renderSpecialtyTabs(levelId) {
        const container = document.getElementById('specialty-tabs');
        if (!container) return;

        const specs = Array.from(await this._specialiteApi.getByNiveauId(levelId));

        if (specs.length === 0) {
            container.innerHTML = `<span class="py-4 text-sm text-muted-foreground-2 italic">Aucune spécialité pour ce niveau.</span>`;
            const tbody = document.getElementById('tbody-subjects');
            if (tbody) {
                tbody.innerHTML = `<tr><td colspan="5" class="px-6 py-12 text-center text-muted-foreground-2">
                    <p>Aucune spécialité trouvée pour ce niveau.</p></td></tr>`;
            }
            return;
        }

        container.parentElement?.classList.toggle("hidden", specs.length === 1);

        container.innerHTML = specs.map((spec, i) => `
            <button type="button"
                data-specialty-id="${spec.id}"
                class="specialty-tab px-5 py-1.5 text-xs font-semibold rounded-full hover:bg-layer/60 transition-all
                       ${i === 0 ? 'active border-primary text-primary' : 'border-transparent text-muted-foreground-2 hover:text-primary'}"
                role="tab">
                ${spec.code}
            </button>`).join('');

        this._handleSpecialtyChange(specs[0].id);
    }

    // ── Subject table ─────────────────────────────────────────────────────────

    async _handleSpecialtyChange(specialtyId) {
        this._currentSpecialtyId = specialtyId;

        document.querySelectorAll('.specialty-tab').forEach(tab => {
            const isActive = parseInt(tab.getAttribute('data-specialty-id')) === specialtyId;
            tab.classList.toggle('active', isActive);
            tab.classList.toggle('border-primary', isActive);
            tab.classList.toggle('text-primary', isActive);
            tab.classList.toggle('border-transparent', !isActive);
            tab.classList.toggle('text-muted-foreground-2', !isActive);
        });

        const modalSpecId = document.getElementById('modal-specialite-id');
        if (modalSpecId) modalSpecId.value = specialtyId;

        const tbody = document.getElementById('tbody-subjects');
        if (!tbody) return;

        tbody.innerHTML = `<tr><td colspan="5" class="px-6 py-12 text-center">
            <div class="animate-spin inline-block size-6 border-[3px] border-current border-t-transparent text-primary rounded-full" role="status">
                <span class="sr-only">Chargement...</span>
            </div></td></tr>`;

        try {
            if (!this._subjectsBySpecialty[specialtyId]) {
                this._subjectsBySpecialty[specialtyId] = await this._retrieveUeUC.getBySpecialite(specialtyId);
            }
            this._renderSubjectsTable();
        } catch (error) {
            GlobalErrorHandler.handle(error);
            tbody.innerHTML = `<tr><td colspan="5" class="px-6 py-12 text-center text-red-500">Erreur lors du chargement des matières.</td></tr>`;
        }
    }

    _renderSubjectsTable() {
        const tbody = document.getElementById('tbody-subjects');
        if (!tbody) return;

        const subjects    = this._subjectsBySpecialty[this._currentSpecialtyId] || [];
        const searchTerm  = (document.getElementById('subject-search')?.value || '').toLowerCase();
        const semFilter   = document.getElementById('semester-filter')?.value || 'all';

        const filtered = subjects.filter(s =>
            (s.libelle.toLowerCase().includes(searchTerm) || s.code.toLowerCase().includes(searchTerm)) &&
            (semFilter === 'all' || s.semestre?.toString() === semFilter)
        );

        if (filtered.length === 0) {
            tbody.innerHTML = `<tr><td colspan="5" class="px-6 py-12 text-center text-muted-foreground-2">
                <p>${searchTerm || semFilter !== 'all' ? 'Aucune matière ne correspond aux filtres.' : 'Aucune matière pour cette spécialité.'}</p>
                </td></tr>`;
            return;
        }

        tbody.innerHTML = filtered.map(ue => `
            <tr class="hover:bg-muted/40 transition-colors group">
                <td class="px-6 py-4">
                    <div class="flex items-center gap-3 min-w-0">
                        <div class="size-9 rounded-xl flex items-center justify-center shrink-0"
                             style="background-color:${ue.couleur || '#7c3aed'}1a;color:${ue.couleur || '#7c3aed'};">
                            <i class="bi bi-journal-text text-lg"></i>
                        </div>
                        <div>
                            <p class="text-sm font-bold text-layer-foreground truncate">${ue.libelle}</p>
                            <p class="text-[10px] text-muted-foreground-2 uppercase tracking-tight">${ue.description || 'Pas de description'}</p>
                        </div>
                    </div>
                </td>
                <td class="px-6 py-4">
                    <span class="text-xs inline-flex items-center px-2 py-1 rounded-lg bg-muted text-layer-foreground font-mono font-bold border border-layer-line">
                        ${ue.code}
                    </span>
                </td>
                <td class="px-6 py-4 text-center">
                    <span class="text-sm font-bold text-layer-foreground">${ue.credit}</span>
                </td>
                <td class="px-6 py-4 text-center">
                    <span class="text-sm font-medium text-muted-foreground-2">${ue.volumeHoraireTotal}h</span>
                </td>
                <td class="px-6 py-4 text-right">
                    <div class="flex justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                        <button type="button" class="size-8 flex items-center justify-center rounded-lg text-muted-foreground-2 hover:bg-muted hover:text-primary transition-colors">
                            <i class="bi bi-pencil-square text-sm"></i>
                        </button>
                        <button type="button" class="size-8 flex items-center justify-center rounded-lg text-muted-foreground-2 hover:bg-red-500/10 hover:text-red-500 transition-colors">
                            <i class="bi bi-trash text-sm"></i>
                        </button>
                    </div>
                </td>
            </tr>`).join('');
    }

    // ── Search & filters ──────────────────────────────────────────────────────

    _initSearchAndFilters() {
        document.getElementById('subject-search')?.addEventListener('input', () => this._renderSubjectsTable());
        document.getElementById('semester-filter')?.addEventListener('change', () => this._renderSubjectsTable());
    }

    // ── Create UE form ────────────────────────────────────────────────────────

    _initFormHandlers() {
        const form = document.getElementById('addUeForm');
        if (!form) return;

        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            const submitBtn = document.getElementById('btn-create-ue');
            const fd = new FormData(form);

            const payload = {
                libelle:            fd.get('libelle'),
                code:               fd.get('code'),
                credit:             parseInt(fd.get('credit')),
                volumeHoraireTotal: parseInt(fd.get('volumeHoraireTotal')),
                description:        fd.get('description'),
                couleur:            fd.get('couleur'),
                specialiteId:       parseInt(fd.get('specialiteId')),
                semestre:           parseInt(fd.get('semestre')),
                enseignantIds:      []
            };

            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.innerHTML = `<span class="animate-spin inline-block size-4 border-[2px] border-current border-t-transparent text-white rounded-full mr-2"></span> Création...`;
            }

            try {
                await this._createUeUC.execute(payload);
                window.HSOverlay?.close("#hs-modal-add-ue");
                GlobalEventNotifier.eventWellDone("UE créée avec succès !");
                delete this._subjectsBySpecialty[this._currentSpecialtyId];
                await this._handleSpecialtyChange(this._currentSpecialtyId);
                form.reset();
            } catch (err) {
                GlobalErrorHandler.handle(err);
            } finally {
                if (submitBtn) {
                    submitBtn.disabled = false;
                    submitBtn.innerHTML = "Créer l'UE";
                }
            }
        });
    }
}

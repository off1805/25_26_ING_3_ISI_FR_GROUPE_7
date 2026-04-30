import { GlobalErrorHandler } from "../../common/GlobalErrorHandler.js";

export class APSchedulePageController {
    constructor(retrieveEmploiTempsUC, deleteEmploiTempsUC) {
        this._retrieveUC = retrieveEmploiTempsUC;
        this._deleteUC   = deleteEmploiTempsUC;

        this._allClasses       = [];
        this._classMap         = {};
        this._specialiteMap    = {};
        this._selectedWeekRange = null;
        this._currentLevelId   = 'all';
        this._serverToday      = '';
        this._schedulesByStatus = { ongoing: [], history: null, draft: null };
    }

    // ── Public API (used by APInterface/apSchedule.js wrapper) ────────────────

    init(classes, specialites, today) {
        this._allClasses    = classes || [];
        this._serverToday   = today;

        classes.forEach(c    => (this._classMap[c.id]        = c));
        specialites.forEach(s => (this._specialiteMap[s.id]  = s));

        this._initFilters();
        this._initModal();
        this._initTabListeners();
        this._registerDeleteGlobal();
    }

    setWeekRange(start, end, weekNo) {
        this._selectedWeekRange = { start, end, semaine: weekNo };
    }

    getWeekNumber(d) {
        const date = new Date(d.getTime());
        date.setHours(0, 0, 0, 0);
        date.setDate(date.getDate() + 3 - (date.getDay() + 6) % 7);
        const week1 = new Date(date.getFullYear(), 0, 4);
        return 1 + Math.round(((date.getTime() - week1.getTime()) / 86400000 - 3 + (week1.getDay() + 6) % 7) / 7);
    }

    // ── Filters ───────────────────────────────────────────────────────────────

    _initFilters() {
        document.querySelectorAll('.level-filter-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                const status  = btn.getAttribute('data-filter-status');
                const levelId = btn.getAttribute('data-filter-level');
                this._currentLevelId = levelId;
                this._applyLevelFilter(status, levelId);
            });
        });
    }

    _applyLevelFilter(status, levelId) {
        ['ongoing', 'history', 'draft'].forEach(s => {
            const container = document.getElementById('container-' + s);
            if (!container) return;
            container.querySelectorAll('.level-filter-btn').forEach(btn => {
                btn.classList.toggle('active', btn.getAttribute('data-filter-level') === levelId);
            });
            container.querySelectorAll('.schedule-row').forEach(row => {
                const match = levelId === 'all' || !levelId ||
                              String(row.getAttribute('data-level-id')) === String(levelId);
                row.classList.toggle('hidden', !match);
            });
        });
    }

    _applyCurrentFilter(status) {
        const container = document.getElementById('container-' + status);
        if (!container) return;
        const btn = container.querySelector(`.level-filter-btn[data-filter-level="${this._currentLevelId}"]`);
        if (btn) this._applyLevelFilter(status, this._currentLevelId);
    }

    // ── Tabs ──────────────────────────────────────────────────────────────────

    _initTabListeners() {
        ['ongoing', 'history', 'draft'].forEach(status => {
            document.getElementById(`tab-${status}`)?.addEventListener('click', () => {
                if (status !== 'ongoing' && this._schedulesByStatus[status] === null) {
                    this._loadSchedules(status);
                } else {
                    this._applyCurrentFilter(status);
                }
            });
        });
    }

    // ── Data loading ──────────────────────────────────────────────────────────

    async _loadSchedules(status) {
        const placeholder = document.getElementById(`placeholder-${status}`);
        try {
            const filters = { size: 100 };
            if (status === 'history') filters.endDateBefore  = this._serverToday;
            if (status === 'draft')   filters.startDateAfter = this._serverToday;

            const response  = await this._retrieveUC.execute(filters);
            const classIds  = this._allClasses.map(c => c.id);
            const schedules = (response.content || []).filter(s => classIds.includes(s.classeId));

            this._schedulesByStatus[status] = schedules;
            this._renderRows(status, schedules);
            placeholder?.classList.add('hidden');
            this._applyCurrentFilter(status);
        } catch (error) {
            GlobalErrorHandler.handle(error);
            if (placeholder) {
                const p = placeholder.querySelector('p');
                if (p) p.textContent = "Erreur lors du chargement.";
                placeholder.querySelector('.animate-spin')?.classList.add('hidden');
            }
        }
    }

    // ── Rendering ─────────────────────────────────────────────────────────────

    _renderRows(status, schedules) {
        const tbody = document.getElementById(`tbody-${status}`);
        if (!tbody) return;

        if (schedules.length === 0) {
            tbody.innerHTML = `
                <tr><td colspan="4" class="px-6 py-20 text-center">
                    <div class="flex flex-col items-center gap-3">
                        <div class="size-16 rounded-2xl bg-muted flex items-center justify-center text-muted-foreground-2">
                            <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.25">
                                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                                <line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/>
                            </svg>
                        </div>
                        <p class="text-sm font-bold text-layer-foreground">Aucun emploi du temps</p>
                        <p class="text-xs text-muted-foreground-2">Il n'y a aucun planning dans cette catégorie.</p>
                    </div>
                </td></tr>`;
            return;
        }

        tbody.innerHTML = schedules.map(et => {
            const classe    = this._classMap[et.classeId]             || {};
            const specialite = this._specialiteMap[classe.specialiteId] || {};
            const dateDebut = new Date(et.dateDebut).toLocaleDateString('fr-FR', { day: '2-digit', month: 'short', year: 'numeric' });
            const dateFin   = new Date(et.dateFin).toLocaleDateString('fr-FR',   { day: '2-digit', month: 'short', year: 'numeric' });

            return `
                <tr data-level-id="${specialite.niveauId}" class="schedule-row hover:bg-muted/5 transition-colors group">
                    <td class="px-6 py-4">
                        <div class="flex items-center gap-3">
                            <div class="size-10 rounded-xl bg-primary/10 flex items-center justify-center text-primary font-bold">
                                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
                                    <polyline points="9 22 9 12 15 12 15 22"/>
                                </svg>
                            </div>
                            <div>
                                <p class="text-sm font-bold text-layer-foreground">${classe.code || 'Inconnue'}</p>
                                <p class="text-xs text-muted-foreground-2">${specialite.libelle || ''}</p>
                            </div>
                        </div>
                    </td>
                    <td class="px-6 py-4">
                        <div class="flex items-center gap-2">
                            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="text-muted-foreground-2">
                                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                                <line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/>
                            </svg>
                            <span class="text-sm font-medium text-layer-foreground">${dateDebut} – ${dateFin}</span>
                        </div>
                    </td>
                    <td class="px-6 py-4 text-center">
                        <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-bold bg-muted text-muted-foreground-2 border border-layer-line">
                            S${et.semaine}
                        </span>
                    </td>
                    <td class="px-6 py-4 text-right">
                        <div class="flex justify-end items-center gap-2">
                            <a href="/ap/schedule/edit?id=${et.id}"
                               class="p-2 text-muted-foreground-2 hover:text-primary hover:bg-primary/10 rounded-lg transition-all" title="Modifier">
                                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                                </svg>
                            </a>
                            <button type="button" onclick="deleteSchedule(${et.id})"
                                    class="p-2 text-muted-foreground-2 hover:text-red-500 hover:bg-red-50 rounded-lg transition-all" title="Supprimer">
                                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <polyline points="3 6 5 6 21 6"/>
                                    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                                </svg>
                            </button>
                        </div>
                    </td>
                </tr>`;
        }).join('');
    }

    // ── Modal (créer emploi) ──────────────────────────────────────────────────

    _initModal() {
        document.getElementById('btn-modal-next')?.addEventListener('click', () => {
            const classId = document.getElementById('select-modal-classe')?.value;
            if (!classId) { alert('Veuillez sélectionner une classe.'); return; }

            // Si setWeekRange n'a pas encore été appelé, on lit le texte du bouton calendrier
            if (!this._selectedWeekRange) {
                const rangeText = document.querySelector('#btn-schedule-calendar span')?.textContent?.trim();
                if (!rangeText || !rangeText.includes('—')) {
                    alert('Veuillez sélectionner une semaine dans le calendrier.');
                    return;
                }
                const [startStr, endStr] = rangeText.split('—').map(s => s.trim());
                const toISO = (fr) => { const [d, m, y] = fr.split('/'); return `${y}-${m}-${d}`; };
                const start = toISO(startStr);
                this._selectedWeekRange = {
                    start,
                    end:     toISO(endStr),
                    semaine: this.getWeekNumber(new Date(start))
                };
            }

            const url = new URL('/ap/schedule/edit', window.location.origin);
            url.searchParams.set('classId',   classId);
            url.searchParams.set('dateDebut', this._selectedWeekRange.start);
            url.searchParams.set('dateFin',   this._selectedWeekRange.end);
            url.searchParams.set('semaine',   this._selectedWeekRange.semaine);
            window.location.href = url.toString();
        });
    }

    // ── Delete (global function attendue par les boutons HTML) ────────────────

    _registerDeleteGlobal() {
        window.deleteSchedule = async (id) => {
            if (!confirm('Êtes-vous sûr de vouloir supprimer cet emploi du temps ?')) return;
            try {
                await this._deleteUC.execute(id);
                window.location.reload();
            } catch (error) {
                GlobalErrorHandler.handle(error);
            }
        };
    }
}

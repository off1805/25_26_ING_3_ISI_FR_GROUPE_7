import { Calendar } from '/js/vanilla-calendar.min.js';
import { EmploiTempsApi } from '../emploiTemps/infrastructure/EmploiTempsApi.js';
import { customAlert } from '../common/CustomAlert.js';
import { customErrorAlert } from '../common/CustomErrorAlert.js';
import { GlobalEventNotifier } from '../common/GlobalEventNotifier.js';

const api = new EmploiTempsApi();

let allClasses = [];
let allSpecialites = [];
let classMap = {};
let specialiteMap = {};
let selectedWeekRange = null;
let currentLevelId = 'all';
let _syncingFilter = false;
let serverToday = ''; // YYYY-MM-DD from server

// State to store loaded schedules
let schedulesByStatus = {
    ongoing: [],
    history: null, // null means not loaded yet
    draft: null
};

export const APScheduleController = {
    init: (classes, specialites, today) => {
        allClasses = classes || [];
        allSpecialites = specialites || [];
        serverToday = today;

        // Build maps for quick lookup during dynamic rendering
        classes.forEach(c => classMap[c.id] = c);
        specialites.forEach(s => specialiteMap[s.id] = s);

        // Pre-populate ongoing from DOM (rendered by Thymeleaf)
        captureInitialOngoing();

        initFilters();
        initModal();
        initTabListeners();
    },

    setWeekRange: (start, end, weekNo) => {
        selectedWeekRange = {
            start: start,
            end: end,
            semaine: weekNo
        };
        console.log('Week range updated in controller:', selectedWeekRange);
    },

    getWeekNumber: (d) => {
        const date = new Date(d.getTime());
        date.setHours(0, 0, 0, 0);
        date.setDate(date.getDate() + 3 - (date.getDay() + 6) % 7);
        const week1 = new Date(date.getFullYear(), 0, 4);
        return 1 + Math.round(((date.getTime() - week1.getTime()) / 86400000 - 3 + (week1.getDay() + 6) % 7) / 7);
    }
};

function captureInitialOngoing() {
    // We don't necessarily need the full data objects for ongoing
    // since they are already in DOM, but storing them helps if we re-render
}

function initFilters() {
    document.querySelectorAll('.level-filter-select').forEach(select => {
        select.addEventListener('change', (e) => {
            if (_syncingFilter) return;
            const levelId = e.target.value;
            currentLevelId = levelId;
            syncAllSelectFilters(levelId);
            applyLevelFilter(levelId);
        });
    });
}

function syncAllSelectFilters(levelId) {
    _syncingFilter = true;
    document.querySelectorAll('.level-filter-select').forEach(sel => {
        if (String(sel.value) !== String(levelId)) {
            sel.value = levelId;
            const wrapper = sel.closest('[data-hs-select]');
            if (wrapper && window.HSSelect) {
                const instance = HSSelect.getInstance(wrapper);
                if (instance) instance.setValue(levelId);
            }
        }
    });
    _syncingFilter = false;
}

function initTabListeners() {
    // Listen for tab button clicks
    const tabs = ['ongoing', 'history', 'draft'];
    tabs.forEach(status => {
        const tabBtn = document.getElementById(`tab-${status}`);
        if (tabBtn) {
            tabBtn.addEventListener('click', () => {
                if (status !== 'ongoing' && schedulesByStatus[status] === null) {
                    loadSchedules(status);
                } else {
                    // Sync filter even if already loaded
                    applyCurrentFilterToStatus(status);
                }
            });
        }
    });
}

function applyCurrentFilterToStatus(status) {
    applyLevelFilter(currentLevelId);
}

async function loadSchedules(status) {
    const placeholder = document.getElementById(`placeholder-${status}`);

    try {
        let filters = { size: 100 };
        if (status === 'history') {
            filters.endDateBefore = serverToday;
        } else if (status === 'draft') {
            filters.startDateAfter = serverToday;
        }

        const response = await api.retrieveEmploisTemps(filters);

        const classIds = allClasses.map(c => c.id);
        const schedules = (response.content || []).filter(s => classIds.includes(s.classeId));

        schedulesByStatus[status] = schedules;
        renderScheduleCards(status, schedules);

        if (placeholder) placeholder.classList.add('hidden');

        applyCurrentFilterToStatus(status);

    } catch (error) {
        console.error(`Error loading ${status} schedules:`, error);
        if (placeholder) {
            placeholder.querySelector('p').textContent = "Erreur lors du chargement.";
            const spinner = placeholder.querySelector('.animate-spin');
            if (spinner) spinner.classList.add('hidden');
        }
    }
}

function renderScheduleCards(status, schedules) {
    const grid = document.getElementById(`cards-grid-${status}`);
    if (!grid) return;

    if (schedules.length === 0) {
        grid.innerHTML = `
            <div class="col-span-full py-20 flex flex-col items-center justify-center gap-3 text-muted-foreground-2">
                <div class="size-16 rounded-2xl bg-muted flex items-center justify-center">
                    <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.25">
                        <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                        <line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/>
                        <line x1="3" y1="10" x2="21" y2="10"/>
                    </svg>
                </div>
                <p class="text-sm font-bold text-layer-foreground">Aucun emploi du temps</p>
                <p class="text-xs text-muted-foreground-2">Il n'y a aucun planning dans cette catégorie.</p>
            </div>`;
        return;
    }

    grid.innerHTML = schedules.map(et => {
        const classe = classMap[et.classeId] || {};
        const specialite = specialiteMap[classe.specialiteId] || {};
        const levelId = specialite.niveauId;
        const specialiteLibelle = specialite.libelle
            ? specialite.libelle.charAt(0).toUpperCase() + specialite.libelle.slice(1).toLowerCase()
            : '';
        const dateDebut = new Date(et.dateDebut).toLocaleDateString('fr-FR', { day: '2-digit', month: 'short', year: 'numeric' });
        const dateFin   = new Date(et.dateFin).toLocaleDateString('fr-FR',   { day: '2-digit', month: 'short', year: 'numeric' });

        return `
            <div data-level-id="${levelId}" class="schedule-card relative bg-card border border-card-line rounded-xl p-5 hover:shadow-sm transition-all">
                <div class="absolute top-3 right-3 flex items-center gap-0.5 bg-layer border border-layer-line rounded-lg p-0.5">
                    <a href="/ap/schedule/edit?id=${et.id}"
                        class="size-7 inline-flex items-center justify-center text-muted-foreground-2 hover:text-primary hover:bg-primary/10 rounded-md transition-all" title="Modifier">
                        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                        </svg>
                    </a>
                    <button type="button" onclick="deleteSchedule(${et.id})"
                        class="size-7 inline-flex items-center justify-center text-muted-foreground-2 hover:text-red-500 hover:bg-red-500/10 rounded-md transition-all" title="Supprimer">
                        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <polyline points="3 6 5 6 21 6"/>
                            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                        </svg>
                    </button>
                </div>
                <div class="mb-3">
                    <span class="rounded-full bg-primary/10 px-3 py-1 text-xs font-semibold text-primary">Semaine ${et.semaine}</span>
                </div>
                <h3 class="mb-0.5 text-base font-bold text-layer-foreground">${classe.code || 'Classe inconnue'}</h3>
                <p class="text-xs font-medium text-muted-foreground-1 mb-1.5">${specialiteLibelle}</p>
                <p class="text-sm text-muted-foreground-2">du ${dateDebut} au ${dateFin}</p>
            </div>`;
    }).join('');
}

function applyLevelFilter(levelId) {
    const tabs = ['ongoing', 'history', 'draft'];
    tabs.forEach(s => {
        const container = document.getElementById('container-' + s);
        if (!container) return;
        container.querySelectorAll('.schedule-card').forEach(card => {
            const cardLevelId = card.getAttribute('data-level-id');
            const visible = levelId === 'all' || !levelId || String(cardLevelId) === String(levelId);
            card.classList.toggle('hidden', !visible);
        });
    });
}

function initModal() {
    const nextBtn = document.getElementById('btn-modal-next');
    if (nextBtn) {
        nextBtn.addEventListener('click', () => {
            const classId = document.getElementById('select-modal-classe')?.value;
            if (!classId) {
                GlobalEventNotifier.eventError('Veuillez sélectionner une classe.');
                return;
            }
            if (!selectedWeekRange) {
                GlobalEventNotifier.eventError('Veuillez sélectionner une semaine dans le calendrier.');
                return;
            }

            const url = new URL('/ap/schedule/edit', window.location.origin);
            url.searchParams.set('classId', classId);
            url.searchParams.set('dateDebut', selectedWeekRange.start);
            url.searchParams.set('dateFin', selectedWeekRange.end);
            url.searchParams.set('semaine', selectedWeekRange.semaine);

            window.location.href = url.toString();
        });
    }
}

// Global functions
window.deleteSchedule = async (id) => {
    const confirmed = await customAlert(
        "Supprimer l'emploi du temps",
        "Êtes-vous sûr de vouloir supprimer cet emploi du temps ? Cette action est irréversible.",
        "Supprimer",
        "Annuler"
    );
    if (!confirmed) return;
    try {
        const response = await fetch(`/api/emplois-temps/${id}`, { method: 'DELETE' });
        if (response.ok) {
            window.location.reload();
        } else {
            await customErrorAlert('Erreur', 'La suppression a échoué. Veuillez réessayer.');
        }
    } catch (error) {
        console.error('Delete error:', error);
        await customErrorAlert('Erreur réseau', 'Une erreur réseau s\'est produite. Vérifiez votre connexion et réessayez.');
    }
};

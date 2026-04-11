import { Calendar } from '/js/vanilla-calendar.min.js';
import { EmploiTempsApi } from '../emploiTemps/infrastructure/EmploiTempsApi.js';

const api = new EmploiTempsApi();

let allClasses = [];
let allSpecialites = [];
let classMap = {};
let specialiteMap = {};
let selectedWeekRange = null;
let currentLevelId = 'all';
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
    const filterBtns = document.querySelectorAll('.level-filter-btn');
    filterBtns.forEach(btn => {
        btn.addEventListener('click', (e) => {
            const status = btn.getAttribute('data-filter-status');
            const levelId = btn.getAttribute('data-filter-level');
            currentLevelId = levelId;
            applyLevelFilter(status, levelId, btn);
        });
    });
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
    const container = document.getElementById('container-' + status);
    if (!container) return;
    const activeBtn = container.querySelector(`.level-filter-btn[data-filter-level="${currentLevelId}"]`);
    if (activeBtn) {
        applyLevelFilter(status, currentLevelId, activeBtn);
    }
}

async function loadSchedules(status) {
    const placeholder = document.getElementById(`placeholder-${status}`);
    const tbody = document.getElementById(`tbody-${status}`);

    try {
        let filters = { size: 100 };
        if (status === 'history') {
            filters.endDateBefore = serverToday;
        } else if (status === 'draft') {
            filters.startDateAfter = serverToday;
        }

        const response = await api.retrieveEmploisTemps(filters);
        console.log(response);

        // Filter by our filiere's classes
        const classIds = allClasses.map(c => c.id);
        const schedules = (response.content || []).filter(s => classIds.includes(s.classeId));

        schedulesByStatus[status] = schedules;
        renderScheduleRows(status, schedules);

        if (placeholder) placeholder.classList.add('hidden');

        // Apply current level filter to newly loaded data
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

function renderScheduleRows(status, schedules) {
    const tbody = document.getElementById(`tbody-${status}`);
    if (!tbody) return;

    if (schedules.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="4" class="px-6 py-20 text-center">
                    <div class="flex flex-col items-center gap-3">
                        <div class="size-16 rounded-2xl bg-muted flex items-center justify-center text-muted-foreground-2">
                            <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.25">
                                <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
                                <line x1="16" y1="2" x2="16" y2="6" />
                                <line x1="8" y1="2" x2="8" y2="6" />
                                <line x1="3" y1="10" x2="21" y2="10" />
                            </svg>
                        </div>
                        <p class="text-sm font-bold text-layer-foreground">Aucun emploi du temps</p>
                        <p class="text-xs text-muted-foreground-2">Il n'y a aucun planning dans cette catégorie.</p>
                    </div>
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = schedules.map(et => {
        const classe = classMap[et.classeId] || {};
        const specialite = specialiteMap[classe.specialiteId] || {};
        const levelId = specialite.niveauId;

        const dateDebut = new Date(et.dateDebut).toLocaleDateString('fr-FR', { day: '2-digit', month: 'short', year: 'numeric' });
        const dateFin = new Date(et.dateFin).toLocaleDateString('fr-FR', { day: '2-digit', month: 'short', year: 'numeric' });

        return `
            <tr data-level-id="${levelId}" class="schedule-row hover:bg-muted/5 transition-colors group">
                <td class="px-6 py-4">
                    <div class="flex items-center gap-3">
                        <div class="size-10 rounded-xl bg-primary/10 flex items-center justify-center text-primary font-bold">
                            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
                                <polyline points="9 22 9 12 15 12 15 22" />
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
                            <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
                            <line x1="16" y1="2" x2="16" y2="6" />
                            <line x1="8" y1="2" x2="8" y2="6" />
                            <line x1="3" y1="10" x2="21" y2="10" />
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
                        <a href="/ap/schedule/edit?id=${et.id}" class="p-2 text-muted-foreground-2 hover:text-primary hover:bg-primary/10 rounded-lg transition-all" title="Modifier">
                            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
                                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
                            </svg>
                        </a>
                        <button type="button" onclick="deleteSchedule(${et.id})" class="p-2 text-muted-foreground-2 hover:text-red-500 hover:bg-red-50 rounded-lg transition-all" title="Supprimer">
                            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <polyline points="3 6 5 6 21 6" />
                                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
                            </svg>
                        </button>
                    </div>
                </td>
            </tr>
        `;
    }).join('');
}

function applyLevelFilter(status, levelId, activeBtn) {
    console.log(`Filtering ${status} by level: ${levelId}`);

    // Update buttons in all status containers to keep them in sync
    const tabs = ['ongoing', 'history', 'draft'];
    tabs.forEach(s => {
        const container = document.getElementById('container-' + s);
        if (!container) return;
        container.querySelectorAll('.level-filter-btn').forEach(btn => {
            if (btn.getAttribute('data-filter-level') === levelId) {
               
                btn.classList.add('active');
            } else {
                btn.classList.remove('active');
               
            }
        });

        // Filter rows in this container
        const rows = container.querySelectorAll('.schedule-row');
        rows.forEach(row => {
            const rowLevelId = row.getAttribute('data-level-id');
            if (levelId === 'all' || !levelId || String(rowLevelId) === String(levelId)) {
                row.classList.remove('hidden');
            } else {
                row.classList.add('hidden');
            }
        });
    });
}

function initModal() {
    // Modal Next Action
    const nextBtn = document.getElementById('btn-modal-next');
    if (nextBtn) {
        nextBtn.addEventListener('click', () => {
            const classId = document.getElementById('select-modal-classe')?.value;
            if (!classId) {
                alert('Veuillez sélectionner une classe.');
                return;
            }
            if (!selectedWeekRange) {
                alert('Veuillez sélectionner une semaine dans le calendrier.');
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
    if (!confirm('Êtes-vous sûr de vouloir supprimer cet emploi du temps ?')) return;
    try {
        const response = await fetch(`/api/emplois-temps/${id}`, { method: 'DELETE' });
        if (response.ok) {
            window.location.reload();
        } else {
            alert('Erreur lors de la suppression.');
        }
    } catch (error) {
        console.error('Delete error:', error);
        alert('Erreur réseau.');
    }
};

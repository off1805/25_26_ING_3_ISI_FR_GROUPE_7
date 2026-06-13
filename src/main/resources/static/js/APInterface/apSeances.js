import api from '/js/common/ClientHttp.js'
// ── State ──────────────────────────────────────────────────────────────────────
let weekOffset = 0;
let historiquePage = 0;
const historiquePageSize = 10;
let historiqueClasseId = '';

// ── Init ───────────────────────────────────────────────────────────────────────
async function init() {
    bindControls();
    await Promise.all([loadWeek(), loadClasses()]);
}

// ── Semaine ────────────────────────────────────────────────────────────────────
async function loadWeek() {
    showSection('semaine', 'loading');
    try {
        const data = await api.get(`/api/ap/seances/week?offset=${weekOffset}`);
        renderWeekRange(data.dateDebut, data.dateFin);
        renderSeancesTable('semaine', data.seances ?? [], 'jour');
        updateKpis(data.seances ?? []);
    } catch (e) {
        console.error('Erreur chargement séances de la semaine', e);
        showSection('semaine', 'empty');
    }
}

function renderWeekRange(dateDebut, dateFin) {
    const el = document.getElementById('week-range');
    if (!el) return;
    if (!dateDebut || !dateFin) {
        el.textContent = 'Semaine du — au —';
        return;
    }
    const fmt = d => parseLocalDate(d).toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric' });
    el.textContent = `Semaine du ${fmt(dateDebut)} au ${fmt(dateFin)}`;
    el.title = weekOffset !== 0 ? 'Cliquer pour revenir à la semaine en cours' : '';
    el.classList.toggle('cursor-pointer', weekOffset !== 0);
    el.classList.toggle('hover:underline', weekOffset !== 0);
}

function updateKpis(seances) {
    setText('kpi-total', seances.length);
    setText('kpi-en-cours', seances.filter(s => s.statut === 'EN_COURS').length);

    let totalPresents = 0, totalEffectif = 0;
    seances.forEach(s => {
        if (s.presenceListId != null && s.nbTotal > 0) {
            totalPresents += s.nbPresents ?? 0;
            totalEffectif += s.nbTotal ?? 0;
        }
    });
    setText('kpi-presence', totalEffectif > 0 ? Math.round(totalPresents / totalEffectif * 100) + ' %' : '—');
}

// ── Historique ─────────────────────────────────────────────────────────────────
async function loadHistorique() {
    showSection('historique', 'loading');
    try {
        const params = new URLSearchParams({ page: historiquePage, size: historiquePageSize });
        if (historiqueClasseId) params.set('classeId', historiqueClasseId);
        const page = await api.get(`/api/ap/seances/history?${params.toString()}`);
        renderSeancesTable('historique', page.content ?? [], 'date');
        renderPagination(page);
    } catch (e) {
        console.error("Erreur chargement de l'historique des séances", e);
        showSection('historique', 'empty');
    }
}

function renderPagination(page) {
    const pagination = document.getElementById('historique-pagination');
    const totalElements = page.totalElements ?? 0;
    if (!totalElements) {
        pagination?.classList.add('hidden');
        return;
    }
    pagination?.classList.remove('hidden');
    setText('historique-count', totalElements + ' séance' + (totalElements > 1 ? 's' : ''));
    setText('historique-page-info', `Page ${(page.number ?? 0) + 1} / ${page.totalPages ?? 1}`);

    const prevBtn = document.getElementById('btn-prev-page');
    const nextBtn = document.getElementById('btn-next-page');
    if (prevBtn) prevBtn.disabled = page.first ?? (page.number ?? 0) === 0;
    if (nextBtn) nextBtn.disabled = page.last ?? ((page.number ?? 0) + 1 >= (page.totalPages ?? 1));
}

async function loadClasses() {
    try {
        const classes = await api.get('/api/ap/seances/classes');
        const sel = document.getElementById('filter-classe-historique');
        if (!sel) return;
        classes.forEach(c => {
            const opt = document.createElement('option');
            opt.value = c.classeId;
            opt.textContent = c.code;
            sel.appendChild(opt);
        });
    } catch (e) {
        console.error('Erreur chargement des classes', e);
    }
}

// ── Controls ───────────────────────────────────────────────────────────────────
function bindControls() {
    document.getElementById('btn-prev-week')?.addEventListener('click', () => {
        weekOffset -= 1;
        loadWeek();
    });

    document.getElementById('btn-next-week')?.addEventListener('click', () => {
        weekOffset += 1;
        loadWeek();
    });

    document.getElementById('week-range')?.addEventListener('click', () => {
        if (weekOffset === 0) return;
        weekOffset = 0;
        loadWeek();
    });

    document.getElementById('tab-historique')?.addEventListener('click', () => {
        loadHistorique();
    }, { once: true });

    document.getElementById('filter-classe-historique')?.addEventListener('change', e => {
        historiqueClasseId = e.target.value;
        historiquePage = 0;
        loadHistorique();
    });

    document.getElementById('btn-prev-page')?.addEventListener('click', () => {
        if (historiquePage > 0) {
            historiquePage -= 1;
            loadHistorique();
        }
    });

    document.getElementById('btn-next-page')?.addEventListener('click', () => {
        historiquePage += 1;
        loadHistorique();
    });
}

// ── Rendu tableau ──────────────────────────────────────────────────────────────
function renderSeancesTable(prefix, seances, dateMode) {
    const body = document.getElementById(`${prefix}-table-body`);
    if (!body) return;

    if (!seances.length) {
        showSection(prefix, 'empty');
        return;
    }

    body.innerHTML = seances.map(s => seanceRowHtml(s, dateMode)).join('');
    showSection(prefix, 'table');
}

function seanceRowHtml(s, dateMode) {
    const dateCell = dateMode === 'jour' ? formatJourDate(s.dateSeance) : formatDate(s.dateSeance);
    const horaire = `${formatTime(s.heureDebut)} – ${formatTime(s.heureFin)}`;
    const enseignant = (s.enseignantNom || s.enseignantPrenom)
        ? `${escHtml(s.enseignantPrenom ?? '')} ${escHtml(s.enseignantNom ?? '')}`.trim()
        : '—';

    const presenceCell = (s.presenceListId != null)
        ? `<span class="text-sm font-semibold text-layer-foreground">${s.nbPresents ?? 0}/${s.nbTotal ?? 0}</span>`
        : `<span class="text-sm text-muted-foreground-2">—</span>`;

    const rowAccent = s.statut === 'EN_COURS' ? 'border-l-2 border-l-green-500' : '';

    return `<tr class="bg-card hover:bg-muted/10 transition-colors cursor-pointer ${rowAccent}"
               onclick="window.location.href='/ap/seances/${s.seanceId}/presence'"
               title="Voir la liste de présence">
        <td class="px-4 py-3">
            <p class="text-sm font-semibold text-layer-foreground capitalize">${dateCell}</p>
        </td>
        <td class="px-4 py-3">
            <span class="text-sm text-muted-foreground-2 whitespace-nowrap">${horaire}</span>
        </td>
        <td class="px-4 py-3">
            <p class="text-sm font-semibold text-layer-foreground truncate">${escHtml(s.libelle ?? '—')}</p>
            <p class="text-sm text-muted-foreground-2 sm:hidden">${escHtml(s.classeCode ?? '')}</p>
        </td>
        <td class="px-4 py-3 hidden sm:table-cell">
            <span class="inline-flex items-center px-2.5 py-1 rounded-full bg-muted text-[10px] font-medium text-layer-foreground">${escHtml(s.classeCode ?? '—')}</span>
        </td>
        <td class="px-4 py-3 hidden md:table-cell">
            <span class="text-sm text-muted-foreground-2">${enseignant}</span>
        </td>
        <td class="px-4 py-3 hidden lg:table-cell">
            <span class="text-sm text-muted-foreground-2">${escHtml(s.salle ?? '—')}</span>
        </td>
        <td class="px-4 py-3 text-center text-sm">${presenceCell}</td>
        <td class="px-4 py-3 text-sm">${statutBadge(s.statut)}</td>
    </tr>`;
}

function statutBadge(statut) {
    switch (statut) {
        case 'EN_COURS':
            return `<span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-[10px] font-bold bg-green-500/10 text-green-600">
                <span class="relative flex size-1.5">
                    <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-green-400 opacity-75"></span>
                    <span class="relative inline-flex rounded-full size-1.5 bg-green-500"></span>
                </span>
                En cours
            </span>`;
        case 'A_VENIR':
            return `<span class="inline-flex items-center px-2.5 py-1 rounded-full text-[10px] font-bold bg-yellow-400/10 text-yellow-500">À venir</span>`;
        default:
            return `<span class="inline-flex items-center px-2.5 py-1 rounded-full text-[10px] font-bold bg-muted text-muted-foreground-2">Terminée</span>`;
    }
}

// ── UI helpers ─────────────────────────────────────────────────────────────────
function showSection(prefix, which) {
    const loading = document.getElementById(`${prefix}-loading`);
    const empty = document.getElementById(`${prefix}-empty`);
    const wrap = document.getElementById(`${prefix}-table-wrap`);
    const pagination = document.getElementById(`${prefix}-pagination`);

    loading?.classList.add('hidden');
    empty?.classList.add('hidden');
    empty?.classList.remove('flex', 'flex-col');
    wrap?.classList.add('hidden');
    if (which !== 'table') pagination?.classList.add('hidden');

    if (which === 'loading') loading?.classList.remove('hidden');
    else if (which === 'empty') { empty?.classList.remove('hidden'); empty?.classList.add('flex', 'flex-col'); }
    else if (which === 'table') wrap?.classList.remove('hidden');
}

function setText(id, val) {
    const el = document.getElementById(id);
    if (el) el.textContent = val;
}

function escHtml(str) {
    return String(str ?? '').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}

function parseLocalDate(dateStr) {
    const [y, m, d] = dateStr.split('-').map(Number);
    return new Date(y, m - 1, d);
}

function formatDate(dateStr) {
    if (!dateStr) return '—';
    return parseLocalDate(dateStr).toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric' });
}

function formatJourDate(dateStr) {
    if (!dateStr) return '—';
    return parseLocalDate(dateStr).toLocaleDateString('fr-FR', { weekday: 'long', day: '2-digit', month: '2-digit' });
}

function formatTime(timeStr) {
    if (!timeStr) return '—';
    return timeStr.substring(0, 5);
}

// ── Boot ───────────────────────────────────────────────────────────────────────
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
} else {
    init();
}

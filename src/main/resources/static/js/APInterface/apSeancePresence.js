import api from '/js/common/ClientHttp.js'
import { TokenService } from '/js/common/application/TokenService.js'
import { GlobalEventNotifier } from '/js/common/GlobalEventNotifier.js'

// ── Init ───────────────────────────────────────────────────────────────────────
async function init() {
    const seanceId = document.body.dataset.seanceId;
    if (!seanceId) {
        showSection('empty');
        return;
    }

    document.getElementById('btn-export-excel')?.addEventListener('click', () => exportExcel(seanceId));

    await loadPresence(seanceId);
}

// ── Chargement ─────────────────────────────────────────────────────────────────
async function loadPresence(seanceId) {
    showSection('loading');
    try {
        const detail = await api.get(`/api/ap/seances/${seanceId}/presence`);
        const s = detail.seance;

        setText('seance-titre', s.libelle ?? 'Séance');

        const etudiants = detail.etudiants ?? [];
        const presents = etudiants.filter(e => e.present === true).length;
        const total = etudiants.length;
        const absents = total - presents;
        const taux = total > 0 ? Math.round(presents / total * 100) + ' %' : '—';

        renderInfoList(s, { presents, absents, total, taux });

        if (detail.presenceListId == null || !etudiants.length) {
            showSection('empty');
            return;
        }

        const nbCreneaux = Math.max((detail.creneauxHoraires ?? []).length, 1);
        renderTableHead(nbCreneaux);
        document.getElementById('presence-table-body').innerHTML =
            etudiants.map((e, i) => etudiantRowHtml(e, i + 1, nbCreneaux)).join('');

        showSection('table');
    } catch (e) {
        console.error('Erreur chargement de la liste de présence', e);
        showSection('empty');
    }
}

// ── Informations séance ────────────────────────────────────────────────────────
function renderInfoList(s, stats) {
    const container = document.getElementById('seance-info-list');
    if (!container) return;

    const enseignant = (s.enseignantNom || s.enseignantPrenom)
        ? `${s.enseignantPrenom ?? ''} ${s.enseignantNom ?? ''}`.trim()
        : '—';

    const items = [
        ['Classe', s.classeCode ?? '—'],
        ['Professeur', enseignant],
        ['Date', formatDate(s.dateSeance)],
        ['Horaire', `${formatTime(s.heureDebut)} – ${formatTime(s.heureFin)}`],
        ['Salle', s.salle ?? '—'],
        ['Présents', stats.presents, 'text-green-600'],
        ['Absents', stats.absents, 'text-red-500'],
        ['Total étudiants', stats.total],
        ['Présence', stats.taux]
    ];

    container.innerHTML = items.map(([label, value, colorClass]) => `
        <div>
            <p class="text-[11px] font-medium text-muted-foreground-2 uppercase tracking-wide">${escHtml(label)}</p>
            <p class="text-sm font-bold ${colorClass ?? 'text-layer-foreground'} mt-0.5">${escHtml(value)}</p>
        </div>`).join('');
}

// ── Rendu tableau ──────────────────────────────────────────────────────────────
function renderTableHead(nbCreneaux) {
    const head = document.getElementById('presence-table-head');
    if (!head) return;

    let html = `
        <th class="border border-card-line px-3 py-3 text-xs font-medium text-muted-foreground-2/60 tracking-widest text-center whitespace-nowrap">N°</th>
        <th class="border border-card-line px-3 py-3 text-xs font-medium text-muted-foreground-2/60 tracking-widest text-center whitespace-nowrap">Matricule</th>
        <th class="border border-card-line px-4 py-3 text-sm font-medium text-muted-foreground-2/60 tracking-widest w-full">Nom et Prénom</th>`;

    for (let i = 1; i <= nbCreneaux; i++) {
        html += `<th class="border border-card-line px-2 py-3 text-xs font-medium text-muted-foreground-2/60 tracking-widest text-center whitespace-nowrap">${i}</th>`;
    }
    head.innerHTML = html;
}

function etudiantRowHtml(e, numero, nbCols) {
    const present = e.present === true;
    const label = present ? 'P' : 'A';
    const colorClass = present ? 'text-green-600' : 'text-red-500';

    let cells = '';
    for (let i = 0; i < nbCols; i++) {
        cells += `<td class="border border-card-line px-2 py-2.5 text-center text-sm font-bold ${colorClass}">${label}</td>`;
    }

    return `<tr class="hover:bg-muted/10 transition-colors">
        <td class="border border-card-line px-3 py-2.5 text-center text-sm text-muted-foreground-2">${numero}</td>
        <td class="border border-card-line px-3 py-2.5 text-center text-sm text-muted-foreground-2 whitespace-nowrap">${escHtml(e.matricule ?? '—')}</td>
        <td class="border border-card-line px-4 py-2.5 text-sm font-semibold text-layer-foreground">${escHtml(e.prenom)} ${escHtml(e.nom)}</td>
        ${cells}
    </tr>`;
}

// ── Export Excel ───────────────────────────────────────────────────────────────
async function exportExcel(seanceId) {
    const token = await TokenService.getToken();
    const url = `/api/ap/seances/${seanceId}/presence/export`;

    try {
        const response = await fetch(url, {
            headers: { Authorization: `Bearer ${token}` }
        });

        if (!response.ok) {
            const body = await response.text().catch(() => '');
            throw new Error(`HTTP ${response.status} — ${body.substring(0, 200)}`);
        }

        const blob = await response.blob();
        const blobUrl = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = blobUrl;
        a.download = `presence_seance_${seanceId}.xlsx`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(blobUrl);
    } catch (e) {
        console.error('Erreur export Excel', e);
        GlobalEventNotifier.eventError('Erreur export : ' + e.message);
    }
}

// ── UI helpers ─────────────────────────────────────────────────────────────────
function showSection(which) {
    const loading = document.getElementById('presence-loading');
    const empty = document.getElementById('presence-empty');
    const wrap = document.getElementById('presence-table-wrap');

    loading?.classList.add('hidden');
    empty?.classList.add('hidden');
    empty?.classList.remove('flex', 'flex-col');
    wrap?.classList.add('hidden');

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
    return parseLocalDate(dateStr).toLocaleDateString('fr-FR', { weekday: 'long', day: '2-digit', month: '2-digit', year: 'numeric' });
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

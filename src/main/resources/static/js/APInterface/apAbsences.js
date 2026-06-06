import api from '/js/common/ClientHttp.js';
import { TokenService } from '/js/common/application/TokenService.js';

// ── State ──────────────────────────────────────────────────────────────────────
let allRows      = [];
let searchTerm   = '';
let activeFilter = { classeId: '', ueId: '', dateDebut: '', dateFin: '' };

// ── Init ───────────────────────────────────────────────────────────────────────
async function init() {
    bindControls();
    await Promise.all([loadUes(), loadRows()]);
}

// ── Data loading ───────────────────────────────────────────────────────────────
async function loadRows() {
    showLoading();
    try {
        const params = buildQueryString(activeFilter);
        allRows = await api.get('/api/ap/absences' + params);
        populateClasseFilter(allRows);
        buildExportDropdown(allRows);
        renderAll();
    } catch (e) {
        console.error('Erreur chargement absences', e);
        showEmpty();
    }
}

async function loadUes() {
    try {
        const ues = await api.get('/api/ap/absences/ues');
        const sel = document.getElementById('filter-ue');
        ues.forEach(u => {
            const opt = document.createElement('option');
            opt.value = u.id;
            opt.textContent = u.libelle + (u.code ? ' (' + u.code + ')' : '');
            sel.appendChild(opt);
        });
    } catch (e) {
        console.error('Erreur chargement UEs', e);
    }
}

// ── Controls ───────────────────────────────────────────────────────────────────
function bindControls() {
    document.getElementById('search-input')?.addEventListener('input', e => {
        searchTerm = e.target.value.toLowerCase().trim();
        renderAll();
    });

    document.getElementById('filter-classe')?.addEventListener('change', e => {
        activeFilter.classeId = e.target.value;
        updateFilterBadge();
        loadRows();
    });

    document.getElementById('filter-ue')?.addEventListener('change', e => {
        activeFilter.ueId = e.target.value;
        updateFilterBadge();
        loadRows();
    });

    document.getElementById('filter-date-debut')?.addEventListener('change', e => {
        activeFilter.dateDebut = e.target.value;
        updateFilterBadge();
        loadRows();
    });

    document.getElementById('filter-date-fin')?.addEventListener('change', e => {
        activeFilter.dateFin = e.target.value;
        updateFilterBadge();
        loadRows();
    });

    document.getElementById('btn-reset')?.addEventListener('click', () => {
        activeFilter = { classeId: '', ueId: '', dateDebut: '', dateFin: '' };
        searchTerm = '';
        document.getElementById('search-input').value      = '';
        document.getElementById('filter-classe').value     = '';
        document.getElementById('filter-ue').value         = '';
        document.getElementById('filter-date-debut').value = '';
        document.getElementById('filter-date-fin').value   = '';
        updateFilterBadge();
        loadRows();
    });

    // Le dropdown d'export est peuplé après chaque chargement de données

    // Gestion du backdrop drawer
    const drawerEl = document.getElementById('hs-drawer-absences');
    if (drawerEl) {
        drawerEl.addEventListener('open.hs.overlay', () => {
            document.getElementById('drawer-backdrop')?.classList.remove('hidden');
        });
        drawerEl.addEventListener('close.hs.overlay', () => {
            document.getElementById('drawer-backdrop')?.classList.add('hidden');
        });
    }
}

function populateClasseFilter(rows) {
    const sel = document.getElementById('filter-classe');
    if (!sel) return;
    while (sel.options.length > 1) sel.remove(1);

    const seen = new Set();
    rows.forEach(r => {
        const key = String(r.classeId);
        if (!seen.has(key)) {
            seen.add(key);
            const opt = document.createElement('option');
            opt.value = r.classeId;
            opt.textContent = r.classeCode;
            sel.appendChild(opt);
        }
    });
    // Restaurer la sélection courante après reconstruction
    if (activeFilter.classeId) sel.value = activeFilter.classeId;
}

// ── Filter badge ───────────────────────────────────────────────────────────────
function updateFilterBadge() {
    const count = [activeFilter.classeId, activeFilter.ueId, activeFilter.dateDebut, activeFilter.dateFin]
        .filter(v => v && v !== '').length;
    const badge = document.getElementById('filter-count-badge');
    if (!badge) return;
    if (count > 0) {
        badge.textContent = count;
        badge.classList.remove('hidden');
    } else {
        badge.classList.add('hidden');
    }
}

// ── Render ─────────────────────────────────────────────────────────────────────
function renderAll() {
    const filtered = filterRows(allRows);
    updateKpis(filtered);
    renderTable(filtered);
}

function filterRows(rows) {
    if (!searchTerm) return rows;
    return rows.filter(r => {
        const haystack = `${r.nom} ${r.prenom} ${r.matricule} ${r.classeCode}`.toLowerCase();
        return haystack.includes(searchTerm);
    });
}

function updateKpis(rows) {
    const total    = rows.reduce((s, r) => s + (r.totalAbsences || 0), 0);
    const nonJust  = rows.reduce((s, r) => s + Math.max(0, (r.totalAbsences || 0) - (r.nbJustifiees || 0) - (r.nbEnAttente || 0)), 0);
    const totalSeances = rows.reduce((s, r) => s + (r.totalSeances || 0), 0);
    const taux     = totalSeances > 0 ? Math.round(total / totalSeances * 1000) / 10 : 0;

    setText('kpi-absences', total);
    setText('kpi-non-just', nonJust);
    setText('kpi-taux', taux + ' %');
}

function renderTable(rows) {
    const body = document.getElementById('table-body');
    if (!body) return;

    if (!rows.length) { showEmpty(); return; }

    body.innerHTML = rows.map(rowHtml).join('');
    document.getElementById('table-loading').classList.add('hidden');
    document.getElementById('table-empty').classList.add('hidden');
    document.getElementById('table-empty').classList.remove('flex');
    document.getElementById('table-wrap').classList.remove('hidden');
    document.getElementById('table-footer').classList.remove('hidden');
    setText('table-count', rows.length + ' étudiant' + (rows.length > 1 ? 's' : ''));
}

function rowHtml(r) {
    const abs     = r.totalAbsences || 0;
    const seances = r.totalSeances  || 0;
    const just    = r.nbJustifiees  || 0;
    const attente = r.nbEnAttente   || 0;
    const nonJust = Math.max(0, abs - just - attente);
    const taux    = seances > 0 ? Math.round(abs / seances * 1000) / 10 : 0;

    const initials = (r.prenom?.[0] ?? '') + (r.nom?.[0] ?? '');
    const avatar   = r.photoUrl
        ? `<img src="${r.photoUrl}" alt="" class="size-8 rounded-xl object-cover shrink-0"/>`
        : `<div class="size-8 rounded-xl bg-primary/10 flex items-center justify-center text-xs font-bold text-primary shrink-0">${initials.toUpperCase()}</div>`;

    // Répartition — pills à fond neutre, texte coloré, zéros masqués
    const pills = [];
    if (just > 0)    pills.push(`<span class="px-2 py-0.5 rounded-full bg-muted text-[10px] font-medium text-green-600">${just} just.</span>`);
    if (attente > 0) pills.push(`<span class="px-2 py-0.5 rounded-full bg-muted text-[10px] font-medium text-amber-600">${attente} att.</span>`);
    if (nonJust > 0) pills.push(`<span class="px-2 py-0.5 rounded-full bg-muted text-[10px] font-medium text-red-600">${nonJust} n.j.</span>`);
    const repartitionHtml = pills.length > 0
        ? `<div class="flex items-center gap-1 flex-wrap">${pills.join('')}</div>`
        : `<span class="text-xs text-muted-foreground-2">—</span>`;

    // Indicateur de risque — texte coloré, fond neutre
    let risqueHtml;
    if (taux >= 20) {
        risqueHtml = `<span class="px-2 py-0.5 rounded-full bg-muted text-[10px] font-semibold text-red-600">critique</span>`;
    } else if (taux >= 10) {
        risqueHtml = `<span class="px-2 py-0.5 rounded-full bg-muted text-[10px] font-semibold text-amber-600">élevé</span>`;
    } else {
        risqueHtml = `<span class="text-xs text-muted-foreground-2">—</span>`;
    }

    return `<tr class="bg-card hover:bg-muted/10 transition-colors cursor-pointer"
               onclick="openDetailDrawer(${r.etudiantId})"
               title="Voir le détail séance par séance">
        <td class="px-4 py-3">
            <div class="flex items-center gap-3">
                ${avatar}
                <p class="text-sm font-semibold text-layer-foreground truncate">${escHtml(r.prenom)} ${escHtml(r.nom)}</p>
            </div>
        </td>
        <td class="px-4 py-3 hidden sm:table-cell">
            <span class="text-xs text-muted-foreground-2">${escHtml(r.matricule)}</span>
        </td>
        <td class="px-4 py-3 hidden sm:table-cell">
            <span class="inline-flex items-center px-2.5 py-1 rounded-full bg-muted text-[10px] font-medium text-layer-foreground">${escHtml(r.classeCode)}</span>
        </td>
        <td class="px-4 py-3 text-center">
            <span class="text-sm font-black text-layer-foreground">${abs}</span>
        </td>
        <td class="px-4 py-3 hidden md:table-cell">${repartitionHtml}</td>
        <td class="px-4 py-3 hidden lg:table-cell">
            <div class="flex items-center gap-2">
                <div class="flex-1 h-1.5 rounded-full bg-muted overflow-hidden">
                    <div class="h-full rounded-full bg-primary transition-all" style="width:${Math.min(taux, 100)}%"></div>
                </div>
                <span class="text-[11px] font-semibold text-muted-foreground-2 w-10 text-right">${taux} %</span>
            </div>
        </td>
        <td class="px-4 py-3">${risqueHtml}</td>
    </tr>`;
}

// ── Drawer (Preline) ───────────────────────────────────────────────────────────
window.openDetailDrawer = async function(etudiantId) {
    const row = allRows.find(r => r.etudiantId === etudiantId);
    if (!row) return;

    // Populate header
    document.getElementById('drawer-student-name').textContent = `${row.prenom} ${row.nom}`;
    document.getElementById('drawer-student-meta').textContent = `${row.matricule} · ${row.classeCode}`;

    const abs     = row.totalAbsences || 0;
    const just    = row.nbJustifiees  || 0;
    const attente = row.nbEnAttente   || 0;
    const nonJust = Math.max(0, abs - just - attente);
    document.getElementById('drawer-kpi-abs').textContent     = abs;
    document.getElementById('drawer-kpi-just').textContent    = just;
    document.getElementById('drawer-kpi-nonjust').textContent = nonJust;

    // Open via Preline
    const drawerEl = document.getElementById('hs-drawer-absences');
    if (typeof window.HSOverlay !== 'undefined') {
        window.HSOverlay.open(drawerEl);
    }

    // Load detail
    document.getElementById('drawer-loading').classList.remove('hidden');
    document.getElementById('drawer-empty').classList.add('hidden');
    document.getElementById('drawer-empty').classList.remove('flex');
    document.getElementById('drawer-table-wrap').classList.add('hidden');

    try {
        const lignes = await api.get(`/api/ap/absences/${etudiantId}/detail`);
        document.getElementById('drawer-loading').classList.add('hidden');

        if (!lignes.length) {
            document.getElementById('drawer-empty').classList.remove('hidden');
            document.getElementById('drawer-empty').classList.add('flex', 'flex-col');
            return;
        }

        document.getElementById('drawer-tbody').innerHTML = lignes.map(detailRowHtml).join('');
        document.getElementById('drawer-table-wrap').classList.remove('hidden');
    } catch (e) {
        console.error('Erreur chargement détail', e);
        document.getElementById('drawer-loading').classList.add('hidden');
        document.getElementById('drawer-empty').classList.remove('hidden');
        document.getElementById('drawer-empty').classList.add('flex', 'flex-col');
    }
};

window.closeDrawer = function() {
    const drawerEl = document.getElementById('hs-drawer-absences');
    if (typeof window.HSOverlay !== 'undefined') {
        window.HSOverlay.close(drawerEl);
    }
};

function detailRowHtml(l) {
    const dateStr = l.date ? new Date(l.date).toLocaleDateString('fr-FR', { day: '2-digit', month: 'short', year: 'numeric' }) : '—';
    const horaire = (l.heureDebut && l.heureFin) ? `${l.heureDebut} – ${l.heureFin}` : '—';

    let statutBadge;
    if (l.present) {
        statutBadge = `<span class="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-[10px] font-bold bg-green-100 text-green-700">
            <svg xmlns="http://www.w3.org/2000/svg" width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M20 6 9 17l-5-5"/></svg>
            Présent
        </span>`;
    } else {
        const just = l.justificatifStatut;
        if (just === 'APPROVED') {
            statutBadge = `<span class="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-[10px] font-bold bg-blue-100 text-blue-700">
                <svg xmlns="http://www.w3.org/2000/svg" width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2"/><rect x="9" y="3" width="6" height="4" rx="1.5"/><path d="m9 12 2 2 4-4"/></svg>
                Justifiée
            </span>`;
        } else if (just === 'PENDING') {
            statutBadge = `<span class="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-[10px] font-bold bg-orange-100 text-orange-700">
                <svg xmlns="http://www.w3.org/2000/svg" width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/></svg>
                En attente
            </span>`;
        } else if (just === 'REJECTED') {
            statutBadge = `<span class="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-[10px] font-bold bg-red-100 text-red-600">
                <svg xmlns="http://www.w3.org/2000/svg" width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M18 6 6 18"/><path d="m6 6 12 12"/></svg>
                Rejetée
            </span>`;
        } else {
            statutBadge = `<span class="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-[10px] font-bold bg-red-100 text-red-600">
                <svg xmlns="http://www.w3.org/2000/svg" width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M18 6 6 18"/><path d="m6 6 12 12"/></svg>
                Absent
            </span>`;
        }
    }

    return `<tr class="hover:bg-muted/30 transition-colors">
        <td class="px-4 py-3 text-xs text-layer-foreground font-medium whitespace-nowrap">${dateStr}</td>
        <td class="px-4 py-3 text-xs text-muted-foreground-2 whitespace-nowrap">${horaire}</td>
        <td class="px-4 py-3">
            <p class="text-xs font-semibold text-layer-foreground leading-tight">${escHtml(l.ueLibelle ?? '—')}</p>
            ${l.ueCode ? `<p class="text-[11px] text-muted-foreground-2">${escHtml(l.ueCode)}</p>` : ''}
            ${l.salle  ? `<p class="text-[11px] text-muted-foreground-2">Salle : ${escHtml(l.salle)}</p>` : ''}
        </td>
        <td class="px-4 py-3 text-center">${statutBadge}</td>
    </tr>`;
}

// ── Export Excel (dropdown par classe) ────────────────────────────────────────
function buildExportDropdown(rows) {
    const menu = document.getElementById('export-classe-menu');
    const emptyMsg = document.getElementById('export-menu-empty');
    if (!menu) return;

    // Supprimer les items existants (garder le message vide)
    [...menu.querySelectorAll('[data-export-item]')].forEach(el => el.remove());

    const seen = new Map();
    rows.forEach(r => {
        if (r.classeId && !seen.has(String(r.classeId))) {
            seen.set(String(r.classeId), r.classeCode);
        }
    });

    if (seen.size === 0) {
        if (emptyMsg) emptyMsg.classList.remove('hidden');
        return;
    }

    if (emptyMsg) emptyMsg.classList.add('hidden');

    seen.forEach((classeCode, classeId) => {
        const btn = document.createElement('button');
        btn.type = 'button';
        btn.dataset.exportItem = '1';
        btn.className =
            'w-full flex items-center gap-2.5 px-3 py-2 rounded-lg text-sm text-layer-foreground' +
            ' hover:bg-muted transition-colors text-left';
        btn.innerHTML =
            `<svg xmlns="http://www.w3.org/2000/svg" width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="text-green-600 shrink-0">
                <rect x="3" y="3" width="18" height="18" rx="2"/><path d="M9 9h6M9 12h6M9 15h3"/>
             </svg>
             <span>${escHtml(classeCode)}</span>`;
        btn.addEventListener('click', () => exportExcel(classeId, classeCode));
        menu.appendChild(btn);
    });
}

async function exportExcel(classeId, classeCode) {
    const token = await TokenService.getToken();
    const url = `/api/ap/absences/export?classeId=${encodeURIComponent(classeId)}&classeCode=${encodeURIComponent(classeCode)}`;

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
        a.download = `absences_${classeCode}.xlsx`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(blobUrl);
    } catch (e) {
        console.error('Erreur export Excel', e);
        alert('Erreur export : ' + e.message);
    }
}

// ── UI helpers ─────────────────────────────────────────────────────────────────
function showLoading() {
    document.getElementById('table-loading').classList.remove('hidden');
    document.getElementById('table-empty').classList.add('hidden');
    document.getElementById('table-empty').classList.remove('flex');
    document.getElementById('table-wrap').classList.add('hidden');
    document.getElementById('table-footer').classList.add('hidden');
}

function showEmpty() {
    document.getElementById('table-loading').classList.add('hidden');
    document.getElementById('table-empty').classList.remove('hidden');
    document.getElementById('table-empty').classList.add('flex');
    document.getElementById('table-empty').classList.add('flex-col');
    document.getElementById('table-wrap').classList.add('hidden');
    document.getElementById('table-footer').classList.add('hidden');
    updateKpis([]);
}

function setText(id, val) {
    const el = document.getElementById(id);
    if (el) el.textContent = val;
}

function escHtml(str) {
    return String(str ?? '').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}

function buildQueryString(f) {
    const p = new URLSearchParams();
    if (f.classeId)  p.set('classeId',  f.classeId);
    if (f.ueId)      p.set('ueId',      f.ueId);
    if (f.dateDebut) p.set('dateDebut', f.dateDebut);
    if (f.dateFin)   p.set('dateFin',   f.dateFin);
    const s = p.toString();
    return s ? '?' + s : '';
}

// ── Boot ───────────────────────────────────────────────────────────────────────
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
} else {
    init();
}

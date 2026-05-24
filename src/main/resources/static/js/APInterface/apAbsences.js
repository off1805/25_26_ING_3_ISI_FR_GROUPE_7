import api from '/js/common/ClientHttp.js';

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
        loadRows();
    });

    document.getElementById('filter-ue')?.addEventListener('change', e => {
        activeFilter.ueId = e.target.value;
        loadRows();
    });

    document.getElementById('filter-date-debut')?.addEventListener('change', e => {
        activeFilter.dateDebut = e.target.value;
        loadRows();
    });

    document.getElementById('filter-date-fin')?.addEventListener('change', e => {
        activeFilter.dateFin = e.target.value;
        loadRows();
    });

    document.getElementById('btn-reset')?.addEventListener('click', () => {
        activeFilter = { classeId: '', ueId: '', dateDebut: '', dateFin: '' };
        searchTerm = '';
        document.getElementById('search-input').value     = '';
        document.getElementById('filter-classe').value    = '';
        document.getElementById('filter-ue').value        = '';
        document.getElementById('filter-date-debut').value = '';
        document.getElementById('filter-date-fin').value   = '';
        loadRows();
    });

    document.getElementById('btn-export-csv')?.addEventListener('click', exportCsv);
}

function populateClasseFilter(rows) {
    const sel = document.getElementById('filter-classe');
    if (!sel) return;
    const existing = new Set([...sel.options].map(o => o.value));
    const seen = new Set();
    rows.forEach(r => {
        const key = String(r.classeId);
        if (!seen.has(key) && !existing.has(key)) {
            seen.add(key);
            const opt = document.createElement('option');
            opt.value = r.classeId;
            opt.textContent = r.classeCode;
            sel.appendChild(opt);
        }
        seen.add(key);
    });
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
    const total        = rows.reduce((s, r) => s + (r.totalAbsences || 0), 0);
    const nonJust      = rows.reduce((s, r) => s + Math.max(0, (r.totalAbsences || 0) - (r.nbJustifiees || 0) - (r.nbEnAttente || 0)), 0);
    const totalEnreg   = rows.reduce((s, r) => s + (r.totalEnregistrements || 0), 0);
    const taux         = totalEnreg > 0 ? Math.round(total / totalEnreg * 1000) / 10 : 0;

    setText('kpi-etudiants', rows.length);
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
    const abs      = r.totalAbsences        || 0;
    const enreg    = r.totalEnregistrements || 0;
    const just     = r.nbJustifiees         || 0;
    const attente  = r.nbEnAttente          || 0;
    const nonJust  = Math.max(0, abs - just - attente);
    const taux     = enreg > 0 ? Math.round(abs / enreg * 1000) / 10 : 0;
    const tauxColor = taux >= 20 ? 'bg-red-500' : taux >= 10 ? 'bg-amber-400' : 'bg-primary';

    const initials = (r.prenom?.[0] ?? '') + (r.nom?.[0] ?? '');
    const avatar   = r.photoUrl
        ? `<img src="${r.photoUrl}" alt="" class="size-8 rounded-xl object-cover"/>`
        : `<div class="size-8 rounded-xl bg-primary/10 flex items-center justify-center text-xs font-bold text-primary">${initials.toUpperCase()}</div>`;

    return `<tr class="hover:bg-surface/50 transition-colors">
        <td class="px-4 py-3">
            <div class="flex items-center gap-3">
                ${avatar}
                <div>
                    <p class="text-xs font-semibold text-layer-foreground">${escHtml(r.prenom)} ${escHtml(r.nom)}</p>
                    <p class="text-[11px] text-muted-foreground-2">${escHtml(r.matricule)}</p>
                </div>
            </div>
        </td>
        <td class="px-4 py-3 hidden sm:table-cell">
            <span class="inline-flex items-center px-2 py-0.5 rounded-lg text-[11px] font-semibold bg-muted text-layer-foreground">${escHtml(r.classeCode)}</span>
        </td>
        <td class="px-4 py-3 text-center">
            <span class="text-sm font-black ${abs > 0 ? 'text-layer-foreground' : 'text-muted-foreground-2'}">${abs}</span>
        </td>
        <td class="px-4 py-3 text-center hidden md:table-cell">
            ${just > 0 ? `<span class="inline-flex items-center justify-center px-2 py-0.5 rounded-lg text-[11px] font-bold bg-green-500/10 text-green-500">${just}</span>` : '<span class="text-muted-foreground-2 text-xs">—</span>'}
        </td>
        <td class="px-4 py-3 text-center hidden md:table-cell">
            ${attente > 0 ? `<span class="inline-flex items-center justify-center px-2 py-0.5 rounded-lg text-[11px] font-bold bg-amber-500/10 text-amber-500">${attente}</span>` : '<span class="text-muted-foreground-2 text-xs">—</span>'}
        </td>
        <td class="px-4 py-3 text-center hidden md:table-cell">
            ${nonJust > 0 ? `<span class="inline-flex items-center justify-center px-2 py-0.5 rounded-lg text-[11px] font-bold bg-red-500/10 text-red-500">${nonJust}</span>` : '<span class="text-muted-foreground-2 text-xs">—</span>'}
        </td>
        <td class="px-4 py-3 hidden lg:table-cell">
            <div class="flex items-center gap-2">
                <div class="flex-1 h-1.5 rounded-full bg-muted overflow-hidden">
                    <div class="h-full rounded-full ${tauxColor} transition-all" style="width:${Math.min(taux, 100)}%"></div>
                </div>
                <span class="text-[11px] font-semibold text-muted-foreground-2 w-10 text-right">${taux} %</span>
            </div>
        </td>
    </tr>`;
}

// ── CSV export ─────────────────────────────────────────────────────────────────
function exportCsv() {
    const filtered = filterRows(allRows);
    const headers  = ['Matricule','Prénom','Nom','Classe','Total absences','Justifiées','En attente','Non justifiées','Taux (%)'];
    const lines    = filtered.map(r => {
        const abs     = r.totalAbsences        || 0;
        const enreg   = r.totalEnregistrements || 0;
        const just    = r.nbJustifiees         || 0;
        const attente = r.nbEnAttente          || 0;
        const nonJust = Math.max(0, abs - just - attente);
        const taux    = enreg > 0 ? Math.round(abs / enreg * 1000) / 10 : 0;
        return [r.matricule, r.prenom, r.nom, r.classeCode, abs, just, attente, nonJust, taux].join(';');
    });
    const blob = new Blob(['﻿' + [headers.join(';'), ...lines].join('\n')], { type: 'text/csv;charset=utf-8;' });
    const a    = Object.assign(document.createElement('a'), { href: URL.createObjectURL(blob), download: 'absences.csv' });
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
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

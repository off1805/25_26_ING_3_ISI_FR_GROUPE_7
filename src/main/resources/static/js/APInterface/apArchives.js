import api from '/js/common/ClientHttp.js';

// ── State ──────────────────────────────────────────────────────────────────────
let anneeScolaireId = null;
let activeTab = 'seances';
let justificatifsRows = [];

const seances = { page: 0, size: 10, classeId: '', type: '', dateDebut: '', dateFin: '' };
const absences = { page: 0, size: 10, classeId: '', ueId: '', dateDebut: '', dateFin: '' };
const justificatifs = { page: 0, size: 10, classeId: '', statut: '' };
const histo = { page: 0, size: 10, classeId: '' };

const tabLoaded = { seances: false, absences: false, justificatifs: false, historique: false };

const TABS = {
    seances: { tabId: 'tab-seances', load: loadSeances },
    absences: { tabId: 'tab-absences', load: () => loadUes().then(loadAbsences) },
    justificatifs: { tabId: 'tab-justificatifs', load: loadJustificatifs },
    historique: { tabId: 'tab-historique-classes', load: loadHistorique },
};

// ── Init ───────────────────────────────────────────────────────────────────────
async function init() {
    bindControls();
    await Promise.all([loadAnnees(), loadClasses()]);
    tabLoaded.seances = true;
    await loadSeances();
}

// ── Année scolaire ─────────────────────────────────────────────────────────────
async function loadAnnees() {
    const sel = document.getElementById('filter-annee');
    if (!sel) return;
    try {
        const annees = await api.get('/api/ap/archives/annees');
        const past = (annees ?? []).filter(a => !a.active).sort((a, b) => b.anneeDebut - a.anneeDebut);

        sel.innerHTML = '';
        if (!past.length) {
            sel.innerHTML = '<option value="">Aucune année archivée</option>';
            sel.disabled = true;
            anneeScolaireId = null;
            return;
        }

        past.forEach(a => {
            const opt = document.createElement('option');
            opt.value = a.id;
            opt.textContent = `${a.anneeDebut}/${a.anneeFin}`;
            sel.appendChild(opt);
        });

        anneeScolaireId = past[0].id;
        sel.value = anneeScolaireId;
    } catch (e) {
        console.error('Erreur chargement des années scolaires', e);
    }
}

// ── Classes (filtres) ──────────────────────────────────────────────────────────
async function loadClasses() {
    try {
        const classes = await api.get('/api/ap/seances/classes');
        const options = [{ value: '', label: 'Toutes les classes' }, ...classes.map(c => ({ value: String(c.classeId), label: c.code }))];

        ['filter-seances-classe', 'filter-absences-classe', 'filter-justificatifs-classe', 'filter-histo-classe']
            .forEach(id => populateSelect(id, options));
    } catch (e) {
        console.error('Erreur chargement des classes', e);
    }
}

// ── UEs (filtre absences) ──────────────────────────────────────────────────────
async function loadUes() {
    const options = [{ value: '', label: 'Toutes les matières' }];

    if (anneeScolaireId) {
        try {
            const ues = await api.get(`/api/ap/archives/ues?anneeScolaireId=${anneeScolaireId}`);
            options.push(...ues.map(u => ({ value: String(u.id), label: u.libelle + (u.code ? ` (${u.code})` : '') })));
        } catch (e) {
            console.error('Erreur chargement des matières', e);
        }
    }

    populateSelect('filter-absences-ue', options);
}

// ── Séances ────────────────────────────────────────────────────────────────────
async function loadSeances() {
    if (!anneeScolaireId) { showSection('seances', 'empty'); return; }
    showSection('seances', 'loading');
    try {
        const params = new URLSearchParams({ anneeScolaireId, page: seances.page, size: seances.size });
        if (seances.classeId) params.set('classeId', seances.classeId);
        if (seances.type) params.set('type', seances.type);
        if (seances.dateDebut) params.set('dateDebut', seances.dateDebut);
        if (seances.dateFin) params.set('dateFin', seances.dateFin);

        const page = await api.get(`/api/ap/archives/seances?${params.toString()}`);
        const body = document.getElementById('seances-table-body');
        if (!(page.content ?? []).length) { showSection('seances', 'empty'); return; }
        body.innerHTML = page.content.map(seanceRowHtml).join('');
        showSection('seances', 'table');
        renderPagination('seances', page, 'séance', 'séances');
    } catch (e) {
        console.error('Erreur chargement des séances archivées', e);
        showSection('seances', 'empty');
    }
}

function seanceRowHtml(s) {
    const horaire = `${formatTime(s.heureDebut)} – ${formatTime(s.heureFin)}`;
    const enseignant = (s.enseignantNom || s.enseignantPrenom)
        ? `${escHtml(s.enseignantPrenom ?? '')} ${escHtml(s.enseignantNom ?? '')}`.trim()
        : '—';

    const presenceCell = (s.presenceListId != null)
        ? `<span class="text-sm font-semibold text-layer-foreground">${s.nbPresents ?? 0}/${s.nbTotal ?? 0}</span>`
        : `<span class="text-sm text-muted-foreground-2">—</span>`;

    const typeLabel = s.type === 'EVENEMENT' ? 'Événement' : 'Séance';
    const typeCls = s.type === 'EVENEMENT' ? 'bg-purple-100 text-purple-700' : 'bg-muted text-layer-foreground';

    return `<tr class="bg-card hover:bg-muted/10 transition-colors">
        <td class="px-4 py-3">
            <p class="text-sm font-semibold text-layer-foreground capitalize">${formatDate(s.dateSeance)}</p>
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
        <td class="px-4 py-3 hidden lg:table-cell">
            <span class="inline-flex items-center px-2.5 py-1 rounded-full text-[10px] font-bold ${typeCls}">${typeLabel}</span>
        </td>
        <td class="px-4 py-3 text-center text-sm">${presenceCell}</td>
    </tr>`;
}

// ── Absences ───────────────────────────────────────────────────────────────────
async function loadAbsences() {
    if (!anneeScolaireId) { showSection('absences', 'empty'); return; }
    showSection('absences', 'loading');
    try {
        const params = new URLSearchParams({ anneeScolaireId, page: absences.page, size: absences.size });
        if (absences.classeId) params.set('classeId', absences.classeId);
        if (absences.ueId) params.set('ueId', absences.ueId);
        if (absences.dateDebut) params.set('dateDebut', absences.dateDebut);
        if (absences.dateFin) params.set('dateFin', absences.dateFin);

        const page = await api.get(`/api/ap/archives/absences?${params.toString()}`);
        const body = document.getElementById('absences-table-body');
        if (!(page.content ?? []).length) { showSection('absences', 'empty'); return; }
        body.innerHTML = page.content.map(absenceRowHtml).join('');
        showSection('absences', 'table');
        renderPagination('absences', page, 'étudiant', 'étudiants');
    } catch (e) {
        console.error('Erreur chargement des absences archivées', e);
        showSection('absences', 'empty');
    }
}

function absenceRowHtml(r) {
    const initials = (r.prenom?.[0] ?? '') + (r.nom?.[0] ?? '');
    const avatar = r.photoUrl
        ? `<img src="${r.photoUrl}" alt="" class="size-8 rounded-xl object-cover shrink-0"/>`
        : `<div class="size-8 rounded-xl bg-primary/10 flex items-center justify-center text-xs font-bold text-primary shrink-0">${initials.toUpperCase()}</div>`;

    return `<tr class="bg-card hover:bg-muted/10 transition-colors">
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
            <span class="text-sm text-layer-foreground">${r.totalSeances ?? 0}</span>
        </td>
        <td class="px-4 py-3 text-center">
            <span class="text-sm font-black text-layer-foreground">${r.totalAbsences ?? 0}</span>
        </td>
        <td class="px-4 py-3 text-center hidden md:table-cell">
            <span class="text-sm text-green-600 font-semibold">${r.nbJustifiees ?? 0}</span>
        </td>
        <td class="px-4 py-3 text-center hidden md:table-cell">
            <span class="text-sm text-amber-600 font-semibold">${r.nbEnAttente ?? 0}</span>
        </td>
    </tr>`;
}

// ── Justificatifs ──────────────────────────────────────────────────────────────
async function loadJustificatifs() {
    if (!anneeScolaireId) { showSection('justificatifs', 'empty'); return; }
    showSection('justificatifs', 'loading');
    try {
        const params = new URLSearchParams({ anneeScolaireId, page: justificatifs.page, size: justificatifs.size });
        if (justificatifs.classeId) params.set('classeId', justificatifs.classeId);
        if (justificatifs.statut) params.set('statut', justificatifs.statut);

        const page = await api.get(`/api/ap/archives/justificatifs?${params.toString()}`);
        justificatifsRows = page.content ?? [];
        const body = document.getElementById('justificatifs-table-body');
        if (!justificatifsRows.length) { showSection('justificatifs', 'empty'); return; }
        body.innerHTML = justificatifsRows.map(justificatifRowHtml).join('');
        showSection('justificatifs', 'table');
        renderPagination('justificatifs', page, 'justificatif', 'justificatifs');
    } catch (e) {
        console.error('Erreur chargement des justificatifs archivés', e);
        showSection('justificatifs', 'empty');
    }
}

const STATUT_CFG = {
    PENDING:  { cls: 'bg-orange-100 text-orange-700', label: 'Nouveau'  },
    APPROVED: { cls: 'bg-green-100 text-green-700',   label: 'Accepté'  },
    REJECTED: { cls: 'bg-red-100 text-red-600',       label: 'Rejeté'   },
};

function statutBadge(s) {
    const { cls, label } = STATUT_CFG[s] || { cls: 'bg-muted text-muted-foreground-2', label: '—' };
    return `<span class="inline-flex items-center px-2 py-0.5 rounded-full text-[10px] font-bold ${cls}">${label}</span>`;
}

function justificatifRowHtml(j) {
    return `<tr class="bg-card hover:bg-muted/10 transition-colors">
        <td class="px-4 py-3">
            <p class="text-sm font-semibold text-layer-foreground truncate">${escHtml(j.etudiantPrenom)} ${escHtml(j.etudiantNom)}</p>
            <p class="text-xs text-muted-foreground-2">${escHtml(j.etudiantMatricule ?? '')}</p>
        </td>
        <td class="px-4 py-3">
            <span class="text-sm text-layer-foreground whitespace-nowrap">${fmtDate(j.dateAbsence)}</span>
        </td>
        <td class="px-4 py-3 hidden md:table-cell max-w-xs">
            <p class="text-sm text-muted-foreground-2 truncate">${escHtml(j.motif ?? '—')}</p>
        </td>
        <td class="px-4 py-3">${statutBadge(j.statut)}</td>
        <td class="px-4 py-3 hidden lg:table-cell">
            <span class="text-xs text-muted-foreground-2 whitespace-nowrap">${fmtDatetime(j.createdAt)}</span>
        </td>
        <td class="px-4 py-3 text-right">
            <button type="button" onclick="window.openJustificatifDetail(${j.id})"
                class="px-3 py-1.5 text-xs font-medium rounded-lg bg-layer border border-layer-line text-layer-foreground hover:bg-muted transition-colors">
                Détails
            </button>
        </td>
    </tr>`;
}

window.openJustificatifDetail = function(id) {
    const j = justificatifsRows.find(r => r.id === id);
    if (!j) return;

    setText('jd-student-name', `${j.etudiantPrenom} ${j.etudiantNom}`);
    setText('jd-student-meta', `${j.etudiantMatricule ?? ''} · Absence du ${fmtDate(j.dateAbsence)}`);
    setText('jd-motif', j.motif || '—');

    const seancesEl = document.getElementById('jd-seances');
    seancesEl.innerHTML = (j.seances && j.seances.length > 0)
        ? j.seances.map(s => `
            <div class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-xl border border-card-line bg-layer">
                <svg xmlns="http://www.w3.org/2000/svg" width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="text-muted-foreground-2 shrink-0"><rect x="3" y="4" width="18" height="18" rx="2"/><path d="M16 2v4"/><path d="M8 2v4"/><path d="M3 10h18"/></svg>
                <span class="text-xs font-semibold text-layer-foreground whitespace-nowrap">${fmtDate(s.date)}</span>
                <span class="text-muted-foreground-2 text-xs">·</span>
                <span class="text-xs text-muted-foreground-1 truncate">${escHtml(s.libelle)}</span>
            </div>`).join('')
        : '<p class="text-xs text-muted-foreground-2 italic">Aucune séance renseignée</p>';

    const messageWrap = document.getElementById('jd-message-wrap');
    if (j.message) {
        setText('jd-message', j.message);
        messageWrap.classList.remove('hidden');
    } else {
        messageWrap.classList.add('hidden');
    }

    const fichiersWrap = document.getElementById('jd-fichiers-wrap');
    const fichiersEl = document.getElementById('jd-fichiers');
    if (j.fichiers && j.fichiers.length > 0) {
        fichiersEl.innerHTML = j.fichiers.map((f, idx) => `
            <a href="/api/ap/justificatifs/${j.id}/fichiers/${idx}/download" download
               class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-xl border border-card-line bg-layer hover:bg-muted transition-colors">
                <svg xmlns="http://www.w3.org/2000/svg" width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="text-muted-foreground-2 shrink-0"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
                <span class="text-xs font-medium text-layer-foreground truncate">${escHtml(f.nomOriginal)}</span>
            </a>`).join('');
        fichiersWrap.classList.remove('hidden');
    } else {
        fichiersWrap.classList.add('hidden');
    }

    const commentaireWrap = document.getElementById('jd-commentaire-wrap');
    if (j.commentaireAP) {
        setText('jd-commentaire', j.commentaireAP);
        commentaireWrap.classList.remove('hidden');
    } else {
        commentaireWrap.classList.add('hidden');
    }

    const drawerEl = document.getElementById('hs-drawer-justificatif');
    if (typeof window.HSOverlay !== 'undefined') window.HSOverlay.open(drawerEl);
};

// ── Historique des classes ─────────────────────────────────────────────────────
async function loadHistorique() {
    if (!anneeScolaireId) { showSection('histo', 'empty'); return; }
    showSection('histo', 'loading');
    try {
        const params = new URLSearchParams({ anneeScolaireId, page: histo.page, size: histo.size });
        if (histo.classeId) params.set('classeId', histo.classeId);

        const page = await api.get(`/api/ap/archives/classe-history?${params.toString()}`);
        const body = document.getElementById('histo-table-body');
        if (!(page.content ?? []).length) { showSection('histo', 'empty'); return; }
        body.innerHTML = page.content.map(histoRowHtml).join('');
        showSection('histo', 'table');
        renderPagination('histo', page, 'étudiant', 'étudiants');
    } catch (e) {
        console.error("Erreur chargement de l'historique des classes", e);
        showSection('histo', 'empty');
    }
}

function histoRowHtml(h) {
    return `<tr class="bg-card hover:bg-muted/10 transition-colors">
        <td class="px-4 py-3">
            <p class="text-sm font-semibold text-layer-foreground truncate">${escHtml(h.prenom)} ${escHtml(h.nom)}</p>
        </td>
        <td class="px-4 py-3 hidden sm:table-cell">
            <span class="text-xs text-muted-foreground-2">${escHtml(h.matricule ?? '')}</span>
        </td>
        <td class="px-4 py-3">
            <span class="inline-flex items-center px-2.5 py-1 rounded-full bg-muted text-[10px] font-medium text-layer-foreground">${escHtml(h.classeCode ?? '—')}</span>
        </td>
        <td class="px-4 py-3">
            <span class="text-sm text-layer-foreground">${fmtDate(h.dateDebut)}</span>
        </td>
        <td class="px-4 py-3">
            <span class="text-sm text-layer-foreground">${h.dateFin ? fmtDate(h.dateFin) : '—'}</span>
        </td>
    </tr>`;
}

// ── Controls ───────────────────────────────────────────────────────────────────
function bindControls() {
    // Sélecteur année scolaire
    document.getElementById('filter-annee')?.addEventListener('change', e => {
        anneeScolaireId = e.target.value ? Number(e.target.value) : null;
        seances.page = 0; absences.page = 0; justificatifs.page = 0; histo.page = 0;
        tabLoaded.seances = false; tabLoaded.absences = false; tabLoaded.justificatifs = false; tabLoaded.historique = false;
        tabLoaded[activeTab] = true;
        TABS[activeTab].load();
    });

    // Onglets
    Object.entries(TABS).forEach(([key, cfg]) => {
        document.getElementById(cfg.tabId)?.addEventListener('click', () => {
            activeTab = key;
            if (!tabLoaded[key]) {
                tabLoaded[key] = true;
                cfg.load();
            }
        });
    });

    // Filtres — Séances
    document.getElementById('filter-seances-classe')?.addEventListener('change', e => {
        seances.classeId = e.target.value; seances.page = 0; loadSeances();
    });
    const seancesTypeSelect = document.getElementById('filter-seances-type');
    const handleSeancesTypeChange = () => {
        seances.type = seancesTypeSelect?.value || ''; seances.page = 0; loadSeances();
    };
    seancesTypeSelect?.addEventListener('change', handleSeancesTypeChange);
    seancesTypeSelect?.addEventListener('change.hs.select', handleSeancesTypeChange);
    document.getElementById('filter-seances-date-debut')?.addEventListener('change', e => {
        seances.dateDebut = e.target.value; seances.page = 0; loadSeances();
    });
    document.getElementById('filter-seances-date-fin')?.addEventListener('change', e => {
        seances.dateFin = e.target.value; seances.page = 0; loadSeances();
    });
    document.getElementById('seances-prev-page')?.addEventListener('click', () => {
        if (seances.page > 0) { seances.page -= 1; loadSeances(); }
    });
    document.getElementById('seances-next-page')?.addEventListener('click', () => {
        seances.page += 1; loadSeances();
    });

    // Filtres — Absences
    document.getElementById('filter-absences-classe')?.addEventListener('change', e => {
        absences.classeId = e.target.value; absences.page = 0; loadAbsences();
    });
    document.getElementById('filter-absences-ue')?.addEventListener('change', e => {
        absences.ueId = e.target.value; absences.page = 0; loadAbsences();
    });
    document.getElementById('filter-absences-date-debut')?.addEventListener('change', e => {
        absences.dateDebut = e.target.value; absences.page = 0; loadAbsences();
    });
    document.getElementById('filter-absences-date-fin')?.addEventListener('change', e => {
        absences.dateFin = e.target.value; absences.page = 0; loadAbsences();
    });
    document.getElementById('absences-prev-page')?.addEventListener('click', () => {
        if (absences.page > 0) { absences.page -= 1; loadAbsences(); }
    });
    document.getElementById('absences-next-page')?.addEventListener('click', () => {
        absences.page += 1; loadAbsences();
    });

    // Filtres — Justificatifs
    document.getElementById('filter-justificatifs-classe')?.addEventListener('change', e => {
        justificatifs.classeId = e.target.value; justificatifs.page = 0; loadJustificatifs();
    });
    const justificatifsStatutSelect = document.getElementById('filter-justificatifs-statut');
    const handleJustificatifsStatutChange = () => {
        justificatifs.statut = justificatifsStatutSelect?.value || ''; justificatifs.page = 0; loadJustificatifs();
    };
    justificatifsStatutSelect?.addEventListener('change', handleJustificatifsStatutChange);
    justificatifsStatutSelect?.addEventListener('change.hs.select', handleJustificatifsStatutChange);
    document.getElementById('justificatifs-prev-page')?.addEventListener('click', () => {
        if (justificatifs.page > 0) { justificatifs.page -= 1; loadJustificatifs(); }
    });
    document.getElementById('justificatifs-next-page')?.addEventListener('click', () => {
        justificatifs.page += 1; loadJustificatifs();
    });

    // Filtres — Historique des classes
    document.getElementById('filter-histo-classe')?.addEventListener('change', e => {
        histo.classeId = e.target.value; histo.page = 0; loadHistorique();
    });
    document.getElementById('histo-prev-page')?.addEventListener('click', () => {
        if (histo.page > 0) { histo.page -= 1; loadHistorique(); }
    });
    document.getElementById('histo-next-page')?.addEventListener('click', () => {
        histo.page += 1; loadHistorique();
    });

    // Drawer justificatif — backdrop
    const drawerEl = document.getElementById('hs-drawer-justificatif');
    if (drawerEl) {
        drawerEl.addEventListener('open.hs.overlay', () => {
            document.getElementById('drawer-backdrop-justificatif')?.classList.remove('hidden');
        });
        drawerEl.addEventListener('close.hs.overlay', () => {
            document.getElementById('drawer-backdrop-justificatif')?.classList.add('hidden');
        });
    }
    document.getElementById('drawer-backdrop-justificatif')?.addEventListener('click', () => {
        if (typeof window.HSOverlay !== 'undefined') window.HSOverlay.close(drawerEl);
    });
}

// ── UI helpers ─────────────────────────────────────────────────────────────────
function renderPagination(prefix, page, singular, plural) {
    const pagination = document.getElementById(`${prefix}-pagination`);
    const total = page.totalElements ?? 0;
    if (!total) {
        pagination?.classList.add('hidden');
        return;
    }
    pagination?.classList.remove('hidden');
    setText(`${prefix}-count`, `${total} ${total > 1 ? plural : singular}`);
    setText(`${prefix}-page-info`, `Page ${(page.number ?? 0) + 1} / ${page.totalPages ?? 1}`);

    const prevBtn = document.getElementById(`${prefix}-prev-page`);
    const nextBtn = document.getElementById(`${prefix}-next-page`);
    if (prevBtn) prevBtn.disabled = (page.number ?? 0) === 0;
    if (nextBtn) nextBtn.disabled = (page.number ?? 0) + 1 >= (page.totalPages ?? 1);
}

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

// (Re)populates a Preline select's options, preserving the current selection if still valid.
function populateSelect(id, options) {
    const sel = document.getElementById(id);
    if (!sel) return;

    const currentValue = sel.value;
    sel.innerHTML = options.map(o => {
        const selected = String(o.value) === String(currentValue) ? ' selected' : '';
        return `<option value="${escHtml(o.value)}"${selected}>${escHtml(o.label)}</option>`;
    }).join('');

    if (window.HSSelect) {
        window.HSSelect.getInstance(sel)?.destroy();
        new window.HSSelect(sel);
    }
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

function formatTime(timeStr) {
    if (!timeStr) return '—';
    return timeStr.substring(0, 5);
}

function fmtDate(iso) {
    if (!iso) return '—';
    const [y, m, d] = iso.split('-');
    return `${d}/${m}/${y}`;
}

function fmtDatetime(iso) {
    if (!iso) return '—';
    const d = new Date(iso);
    return d.toLocaleDateString('fr-FR') + ' à ' + d.toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' });
}

// ── Boot ───────────────────────────────────────────────────────────────────────
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
} else {
    init();
}

import { UeApi } from "../Ue/infrastructure/UeApi.js";
import { OffreUeApi } from "../Ue/infrastructure/OffreUeApi.js";
import { CreateUeUC } from "../Ue/application/CreateUeUC.js";
import { GlobalErrorHandler } from "../common/GlobalErrorHandler.js";
import { GlobalEventNotifier } from "../common/GlobalEventNotifier.js";
import { SpecialiteApi } from "../academicStructure/infrastructure/SpecialiteApi.js";
import { ClasseApi } from "../academicStructure/infrastructure/ClasseApi.js";
import { UserApi } from "../User/infrastructure/UserApi.js";

const ueApi = new UeApi();
const offreUeApi = new OffreUeApi();
const userApi = new UserApi();
const classeApi = new ClasseApi();
const createUeUC = new CreateUeUC(ueApi);

let allNiveaux = [];
let currentLevelSpecs = [];
let allLevelSubjects = [];
let currentSpecialtyId = null;
let currentSubjects = [];
let currentPage = 0;
let totalPages = 0;
let totalElements = 0;
const pageSize = 5;
let currentFiltre = { semestre: null, libelle: '' };
let searchDebounceTimer = null;
let currentViewOffre = null;
let cachedEnseignants = null;
let cachedClasses = null;
let cachedClassesSpecId = null;

export const APSubjectsController = {
    init: () => {
        const data = window.KEMO_DATA || {};
        allNiveaux = data.niveaux || [];

        initLevelTabs();
        initSearchAndFilters();
        initCreateFormHandler();
        initEditFormHandler();

        document.getElementById('select-specialty')?.addEventListener('change', (e) => {
            const val = e.target.value;
            handleSpecialtyChange(val === 'all' ? 'all' : parseInt(val));
        });

        if (allNiveaux.length > 0) {
            handleLevelChange(String(allNiveaux[0].id));
        } else {
            handleLevelChange('all');
        }
    }
};

// ── Level tabs ────────────────────────────────────────────────────────────────

function initLevelTabs() {
    const tabs = document.querySelectorAll('#level-tabs .level-filter-btn');
    tabs.forEach(btn => {
        btn.addEventListener('click', () => {
            tabs.forEach(b => {
                b.classList.remove('bg-surface-active', 'text-layer-foreground');
                b.classList.add('bg-transparent', 'text-muted-foreground-1');
            });
            btn.classList.remove('bg-transparent', 'text-muted-foreground-1');
            btn.classList.add('bg-surface-active', 'text-layer-foreground');
            handleLevelChange(btn.dataset.levelId);
        });
    });
}

function handleLevelChange(levelId) {
    renderSpecialtyTabs(levelId);
}

// ── Specialty tabs ────────────────────────────────────────────────────────────

async function renderSpecialtyTabs(levelId) {
    const sel = document.getElementById('select-specialty');
    if (!sel) return;

    const specs = Array.from(await new SpecialiteApi().getByNiveauId(levelId));
    currentLevelSpecs = specs;

    if (window.HSSelect) HSSelect.getInstance(sel)?.destroy();

    if (specs.length === 0) {
        sel.innerHTML = '<option value="" disabled selected>Aucune spécialité</option>';
        sel.disabled = true;
        if (window.HSSelect) new HSSelect(sel);
        const grid = document.getElementById('subjects-grid');
        if (grid) grid.innerHTML = `<div class="col-span-full py-12 text-center text-muted-foreground-2"><p>Aucune spécialité trouvée pour ce niveau.</p></div>`;
        return;
    }

    sel.disabled = false;
    sel.innerHTML = `<option value="all">Toutes les spécialités</option>` +
        specs.map(spec => `<option value="${spec.id}">${spec.code}</option>`).join('');
    if (window.HSSelect) new HSSelect(sel);

    handleSpecialtyChange('all');
}

// ── Subjects table ────────────────────────────────────────────────────────────

async function handleSpecialtyChange(specialtyId) {
    currentSpecialtyId = specialtyId;
    currentPage = 0;
    currentFiltre = { semestre: currentFiltre.semestre, libelle: '' };

    const searchInput = document.getElementById('subject-search');
    if (searchInput) searchInput.value = '';

    const modalSpecId = document.getElementById('modal-specialite-id');
    if (modalSpecId) modalSpecId.value = specialtyId !== 'all' ? specialtyId : '';

    await loadSubjectsPage();
}

async function loadSubjectsPage() {
    const grid = document.getElementById('subjects-grid');
    if (!grid) return;

    grid.innerHTML = `<div class="col-span-full flex justify-center py-12">
        <div class="animate-spin size-6 border-[3px] border-current border-t-transparent text-primary rounded-full"></div>
        </div>`;

    try {
        if (currentSpecialtyId === 'all') {
            // Page 0 = re-fetch depuis l'API (filtre ou niveau a changé)
            if (currentPage === 0) {
                const responses = await Promise.all(
                    currentLevelSpecs.map(s => offreUeApi.getActiveBySpecialite(s.id, 0, 100, currentFiltre))
                );
                allLevelSubjects = responses.flatMap(r => r?.content || []);
            }
            // Pagination client-side
            const start = currentPage * pageSize;
            currentSubjects = allLevelSubjects.slice(start, start + pageSize);
            totalElements = allLevelSubjects.length;
            totalPages = Math.ceil(totalElements / pageSize) || 1;
        } else {
            const response = await offreUeApi.getActiveBySpecialite(
                currentSpecialtyId, currentPage, pageSize, currentFiltre
            );
            currentSubjects = response?.content || [];
            totalPages = response?.totalPages ?? 0;
            totalElements = response?.totalElements ?? 0;
        }
        renderSubjectsGrid();
        renderPaginationControls();
    } catch (error) {
        GlobalErrorHandler.handle(error);
        grid.innerHTML = `<div class="col-span-full py-12 text-center text-red-500">Erreur lors du chargement des matières.</div>`;
    }
}

function renderSubjectsGrid() {
    const grid = document.getElementById('subjects-grid');
    if (!grid) return;

    if (currentSubjects.length === 0) {
        grid.innerHTML = `<div class="col-span-full py-12 text-center text-muted-foreground-2">
            <p>${currentFiltre.libelle || currentFiltre.semestre ? 'Aucune matière ne correspond aux filtres.' : 'Aucune matière pour cette spécialité.'}</p>
            </div>`;
        return;
    }

    grid.innerHTML = currentSubjects.map((offre) => `
        <div class="ue-card flex items-center gap-4 px-5 py-4 bg-card border border-card-line rounded-xl hover:shadow-sm  transition-all cursor-pointer"
            data-offre-id="${offre.id}">
            <div class="flex-shrink-0 w-10 h-10 rounded-lg flex items-center justify-center text-lg"
                style="background-color: ${offre.couleur || '#7c3aed'}19; color: ${offre.couleur || '#7c3aed'};">
                <i class="bi bi-journal-text"></i>
            </div>
            <div class="flex-1 min-w-0">
                <p class="text-sm font-semibold text-layer-foreground truncate">${escapeHtml(offre.libelle)}</p>
                <p class="text-xs text-muted-foreground-2">${offre.volumeHoraireTotal}h &middot; ${offre.credit} crédit(s) &middot; S${offre.semestre || '—'}</p>
            </div>
        </div>`).join('');

    document.querySelectorAll('.ue-card').forEach(card => {
        card.addEventListener('click', () => openViewModal(card.dataset.offreId));
    });
}

function renderPaginationControls() {
    const container = document.getElementById('pagination-container');
    if (!container) return;

    if (totalPages <= 1) {
        container.innerHTML = '';
        return;
    }

    const activeClass = 'bg-foreground border-layer-line text-white';
    const inactiveClass = 'bg-layer border-layer-line text-muted-foreground-1 hover:bg-layer-hover';
    const disabledClass = 'bg-layer border-layer-line text-muted-foreground-2 opacity-40 pointer-events-none';
    const btnBase = 'inline-flex items-center justify-center min-w-[2rem] h-8 px-2 text-xs font-medium rounded-lg border transition-all cursor-pointer';

    let pageNums = [];
    if (totalPages <= 7) {
        pageNums = Array.from({ length: totalPages }, (_, i) => i);
    } else {
        const rangeStart = Math.max(0, currentPage - 1);
        const rangeEnd = Math.min(totalPages - 1, currentPage + 1);
        if (rangeStart > 0) { pageNums.push(0); if (rangeStart > 1) pageNums.push('...'); }
        for (let i = rangeStart; i <= rangeEnd; i++) pageNums.push(i);
        if (rangeEnd < totalPages - 1) { if (rangeEnd < totalPages - 2) pageNums.push('...'); pageNums.push(totalPages - 1); }
    }

    container.innerHTML = `
        <div class="flex flex-col items-center gap-3">
            <div class="flex items-center gap-1" id="pagination-btns">
                <button data-page="${currentPage - 1}" class="${btnBase} ${currentPage === 0 ? disabledClass : inactiveClass}" ${currentPage === 0 ? 'disabled' : ''}>
                    <i class="bi bi-chevron-left text-xs"></i>
                </button>
                ${pageNums.map(p => p === '...'
                    ? `<span class="${btnBase} border-transparent text-muted-foreground-2 cursor-default">…</span>`
                    : `<button data-page="${p}" class="${btnBase} ${p === currentPage ? activeClass : inactiveClass}">${p + 1}</button>`
                ).join('')}
                <button data-page="${currentPage + 1}" class="${btnBase} ${currentPage >= totalPages - 1 ? disabledClass : inactiveClass}" ${currentPage >= totalPages - 1 ? 'disabled' : ''}>
                    <i class="bi bi-chevron-right text-xs"></i>
                </button>
            </div>
            <p class="text-xs text-muted-foreground-2">${totalElements} résultat(s) &middot; Page ${currentPage + 1} sur ${totalPages}</p>
        </div>`;

    container.querySelector('#pagination-btns')?.addEventListener('click', (e) => {
        const btn = e.target.closest('[data-page]');
        if (!btn || btn.disabled || btn.tagName === 'SPAN') return;
        const page = parseInt(btn.dataset.page);
        if (!isNaN(page) && page >= 0 && page < totalPages) {
            currentPage = page;
            loadSubjectsPage();
            document.getElementById('subjects-grid')?.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
        }
    });
}

// ── Edit modal ────────────────────────────────────────────────────────────────

function openEditModal(e) {
    const btn = e.currentTarget;
    document.getElementById('edit-offre-id').value = btn.dataset.offreId;
    document.getElementById('edit-offre-specialite-id').value = btn.dataset.specialiteId;
    document.getElementById('edit-offre-libelle').value = btn.dataset.libelle;
    document.getElementById('edit-offre-code').value = btn.dataset.code;
    document.getElementById('edit-offre-credit').value = btn.dataset.credit;
    document.getElementById('edit-offre-vh').value = btn.dataset.vh;
    document.getElementById('edit-offre-description').value = btn.dataset.description;
    document.getElementById('edit-offre-couleur').value = btn.dataset.couleur || '#7c3aed';
    const semSelect = document.getElementById('edit-offre-semestre');
    if (semSelect) {
        const semVal = btn.dataset.semestre || '1';
        semSelect.value = semVal;
        // Sync l'UI du select Preline
        const hsInstance = window.HSSelect?.getInstance(semSelect);
        if (hsInstance) hsInstance.setValue(semVal);
    }

    if (window.HSOverlay) window.HSOverlay.open("#hs-modal-edit-offre-ue");
}

function initEditFormHandler() {
    const form = document.getElementById('editOffreUeForm');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const submitBtn = document.getElementById('btn-update-offre-ue');
        const fd = new FormData(form);
        const offreId = fd.get('offreId');

        const payload = {
            libelle: fd.get('libelle'),
            code: fd.get('code'),
            credit: parseInt(fd.get('credit')),
            volumeHoraireTotal: parseInt(fd.get('volumeHoraireTotal')),
            description: fd.get('description'),
            couleur: fd.get('couleur'),
            semestre: parseInt(fd.get('semestre')),
            specialiteId: parseInt(fd.get('specialiteId')),
            enseignantIds: []
        };

        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.innerHTML = `<span class="animate-spin inline-block size-4 border-[2px] border-current border-t-transparent text-white rounded-full mr-2"></span> Mise à jour...`;
        }

        try {
            await offreUeApi.updateOffreUe(offreId, payload);
            if (window.HSOverlay) window.HSOverlay.close("#hs-modal-edit-offre-ue");
            GlobalEventNotifier.eventWellDone("UE mise à jour avec succès !");
            currentPage = 0;
            await loadSubjectsPage();
        } catch (err) {
            GlobalErrorHandler.handle(err);
        } finally {
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.innerHTML = "Enregistrer";
            }
        }
    });
}

// ── Search & filters ──────────────────────────────────────────────────────────

function initSearchAndFilters() {
    document.getElementById('subject-search')?.addEventListener('input', (e) => {
        clearTimeout(searchDebounceTimer);
        searchDebounceTimer = setTimeout(() => {
            currentFiltre.libelle = e.target.value.trim();
            currentPage = 0;
            loadSubjectsPage();
        }, 400);
    });

    const semesterSelect = document.getElementById('semester-filter');
    const handleSemesterChange = () => {
        const val = semesterSelect?.value || 'all';
        currentFiltre.semestre = val === 'all' ? null : parseInt(val);
        currentPage = 0;
        loadSubjectsPage();
    };
    semesterSelect?.addEventListener('change', handleSemesterChange);
    semesterSelect?.addEventListener('change.hs.select', handleSemesterChange);
}

// ── Create UE form ────────────────────────────────────────────────────────────

function initCreateFormHandler() {
    const form = document.getElementById('addUeForm');
    if (!form) return;

    // Initialiser les selects avancés du modal (Niveau -> Spécialité)
    initCreateModalSelects();
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const submitBtn = document.getElementById('btn-create-ue');
        const fd = new FormData(form);

        const payload = {
            libelle: fd.get('libelle'),
            code: fd.get('code'),
            credit: parseInt(fd.get('credit')),
            volumeHoraireTotal: parseInt(fd.get('volumeHoraireTotal')),
            description: fd.get('description'),
            couleur: fd.get('couleur'),
            specialiteId: parseInt(fd.get('specialiteId')),
            semestre: parseInt(fd.get('semestre')),
            enseignantIds: []
        };

        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.innerHTML = `<span class="animate-spin inline-block size-4 border-[2px] border-current border-t-transparent text-white rounded-full mr-2"></span> Création...`;
        }

        try {
            await createUeUC.execute(payload);
            if (window.HSOverlay) window.HSOverlay.close("#hs-modal-add-ue");
            GlobalEventNotifier.eventWellDone("UE créée avec succès !");
            currentPage = 0;
            await loadSubjectsPage();
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

// ── Create modal selects (Niveau -> Spécialité) ──────────────────────────────
function initCreateModalSelects() {
    const nivSel = document.getElementById('add-ue-niveau-select');
    const specSel = document.getElementById('add-ue-specialite-select');
    if (!nivSel || !specSel) return;

    // Populate niveaux depuis le cache serveur (window.KEMO_DATA -> allNiveaux)
    const niveaux = allNiveaux || [];

    try {
        if (window.HSSelect) HSSelect.getInstance(nivSel)?.destroy();
    } catch (_) { }

    nivSel.innerHTML = `<option value="" disabled selected>Sélectionner un niveau</option>` +
        (niveaux.map(n => `<option value="${n.id}">Niveau ${n.ordre}</option>`).join(''));

    if (window.HSSelect) new HSSelect(nivSel);

    nivSel.addEventListener('change', async (e) => {
        const val = e.target.value;
        if (val) await loadAddModalSpecialties(val);
    });

    // Si on a des niveaux, précharger la première spécialité pour meilleure UX
    if (niveaux.length > 0) {
        const firstId = String(niveaux[0].id);
        setTimeout(() => {
            try {
                nivSel.value = firstId;
                if (window.HSSelect) HSSelect.getInstance(nivSel)?.setValue(firstId);
            } catch (_) { }
            loadAddModalSpecialties(firstId);
        }, 40);
    }
}

async function loadAddModalSpecialties(niveauId) {
    const specSel = document.getElementById('add-ue-specialite-select');
    if (!specSel) return;

    try {
        specSel.disabled = true;
        if (window.HSSelect) HSSelect.getInstance(specSel)?.destroy();
    } catch (_) { }

    specSel.innerHTML = `<option value="" disabled selected>Chargement...</option>`;

    try {
        const specs = Array.from(await new SpecialiteApi().getByNiveauId(niveauId));
        if (!specs || specs.length === 0) {
            specSel.innerHTML = `<option value="" disabled selected>Aucune spécialité</option>`;
            specSel.disabled = true;
            if (window.HSSelect) new HSSelect(specSel);
            return;
        }

        specSel.disabled = false;
        specSel.innerHTML = `<option value="" disabled selected>Sélectionner une spécialité</option>` +
            specs.map(s => `<option value="${s.id}">${escapeHtml(s.libelle || s.code || s.id)}</option>`).join('');

        if (window.HSSelect) new HSSelect(specSel);
    } catch (err) {
        GlobalErrorHandler.handle(err);
        specSel.innerHTML = `<option value="" disabled selected>Erreur</option>`;
    }
}

// ── View modal ────────────────────────────────────────────────────────────────

function openViewModal(offreId) {
    const offre = currentSubjects.find(s => String(s.id) === String(offreId));
    if (!offre) return;

    currentViewOffre = offre;
    cachedEnseignants = null;
    cachedClasses = null;
    cachedClassesSpecId = null;

    const icon = document.getElementById('view-ue-icon');
    if (icon) {
        icon.style.backgroundColor = (offre.couleur || '#7c3aed') + '19';
        icon.style.color = offre.couleur || '#7c3aed';
    }
    document.getElementById('view-ue-libelle').textContent = offre.libelle;

    document.getElementById('view-desc-credit').textContent = offre.credit;
    document.getElementById('view-desc-vh').textContent = offre.volumeHoraireTotal + 'h';
    document.getElementById('view-desc-semestre').textContent = offre.semestre || '—';
    document.getElementById('view-desc-code').textContent = offre.code;
    document.getElementById('view-desc-text').textContent = offre.description?.trim() || 'Aucune description disponible.';
    document.getElementById('view-desc-created').textContent = offre.createdAt ? formatDate(offre.createdAt) : '—';

    renderTransactionsTab(offre);
    renderProfsTab(offre);
    renderStatsTab(offre);

    document.getElementById('btn-view-to-edit').onclick = () => {
        if (window.HSOverlay) window.HSOverlay.close('#hs-modal-view-offre-ue');
        setTimeout(() => openEditModal({ currentTarget: buildFakeEditBtn(offre) }), 150);
    };

    // Reset to first tab
    document.getElementById('view-tab-desc-btn')?.click();

    if (window.HSOverlay) window.HSOverlay.open('#hs-modal-view-offre-ue');
    setTimeout(() => { if (typeof HSStaticMethods !== 'undefined') HSStaticMethods.autoInit(); }, 50);
}

function buildFakeEditBtn(offre) {
    return {
        dataset: {
            offreId: String(offre.id),
            specialiteId: String(offre.specialiteId),
            libelle: offre.libelle,
            code: offre.code,
            credit: String(offre.credit),
            vh: String(offre.volumeHoraireTotal),
            description: offre.description || '',
            couleur: offre.couleur || '#7c3aed',
            semestre: String(offre.semestre || '1')
        }
    };
}

async function renderTransactionsTab(offre) {
    const container = document.getElementById('view-transactions-list');
    if (!container) return;

    container.innerHTML = `<div class="flex justify-center py-10">
        <div class="animate-spin size-5 border-[2px] border-current border-t-transparent text-primary rounded-full"></div>
    </div>`;

    let versions = [];
    try {
        const response = await offreUeApi.getByUeId(offre.ueId);
        versions = (response?.content || response || [])
            .sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
    } catch (_) {
        // fallback: use only the current offre
        versions = [offre];
    }

    if (versions.length === 0) {
        container.innerHTML = emptyState('bi-clock-history', 'Aucun historique disponible.');
        return;
    }

    const events = buildTimeline(versions);
    container.innerHTML = renderTimeline(events);
}

const DIFF_LABELS = {
    libelle:           'Nom',
    code:              'Code',
    credit:            'Crédits ECTS',
    volumeHoraireTotal:'Volume horaire',
    semestre:          'Semestre',
    description:       'Description',
    couleur:           'Couleur',
};

function buildTimeline(versions) {
    const events = [];

    // First event: UE creation
    const first = versions[0];
    events.push({
        date: first.createdAt,
        title: 'Création de l\'UE',
        subtitle: `${first.code} · ${first.credit} crédit(s) · Semestre ${first.semestre}`,
        changes: [],
        isCreation: true,
    });

    // Subsequent events: diff between consecutive versions
    for (let i = 1; i < versions.length; i++) {
        const prev = versions[i - 1];
        const curr = versions[i];
        const changes = [];

        for (const [key, label] of Object.entries(DIFF_LABELS)) {
            const a = prev[key], b = curr[key];
            if (a !== b) {
                changes.push({ label, from: a ?? '—', to: b ?? '—' });
            }
        }

        // Enseignants diff
        const prevIds = new Set(prev.enseignantIds || []);
        const currIds = new Set(curr.enseignantIds || []);
        const added   = [...currIds].filter(id => !prevIds.has(id));
        const removed = [...prevIds].filter(id => !currIds.has(id));
        if (added.length || removed.length) {
            changes.push({
                label: 'Enseignants',
                from: prevIds.size,
                to: currIds.size,
                detail: [
                    ...added.map(id => `+#${id}`),
                    ...removed.map(id => `-#${id}`)
                ].join(', ')
            });
        }

        const isNewYear = prev.anneeScolaireId !== curr.anneeScolaireId;
        events.push({
            date: curr.createdAt,
            title: isNewYear ? 'Nouvelle offre (nouvelle année scolaire)' : 'Modification',
            subtitle: null,
            changes,
            isCreation: false,
        });
    }

    return events.reverse(); // plus récent en premier
}

function renderTimeline(events) {
    return `<div class="w-full">
        ${events.map(ev => `
        <div class="flex gap-x-3">
            <div class="min-w-[4.5rem] text-end shrink-0">
                <span class="text-xs text-muted-foreground-1 leading-tight">${formatDateCompact(ev.date)}</span>
            </div>
            <div class="relative last:after:hidden after:absolute after:top-7 after:bottom-0 after:start-3.5 after:-translate-x-[0.5px] after:border-s after:border-line-2">
                <div class="relative z-10 size-7 flex justify-center items-center">
                    <div class="size-2 rounded-full ${ev.isCreation ? 'bg-primary' : 'bg-surface-3'}"></div>
                </div>
            </div>
            <div class="grow pt-0.5 pb-8">
                <h3 class="flex items-start gap-x-1.5 font-medium text-foreground text-sm">
                    ${ev.isCreation
                        ? `<i class="bi bi-plus-circle shrink-0 mt-0.5 text-primary"></i>`
                        : `<i class="bi bi-pencil-square shrink-0 mt-0.5 text-muted-foreground-1"></i>`}
                    ${ev.title}
                </h3>
                ${ev.subtitle ? `<p class="mt-0.5 text-xs text-muted-foreground-2">${ev.subtitle}</p>` : ''}
                ${ev.changes.length > 0 ? `
                <div class="mt-2 space-y-1.5">
                    ${ev.changes.map(ch => `
                    <p class="text-xs text-muted-foreground-2 flex items-center gap-x-1.5 flex-wrap">
                        <span class="font-medium text-muted-foreground-1">${escapeHtml(ch.label)} :</span>
                        <span class="line-through opacity-60">${escapeHtml(String(ch.from))}</span>
                        <svg class="size-3 shrink-0 text-muted-foreground-2" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
                        <span class="text-layer-foreground font-medium">${escapeHtml(String(ch.to))}</span>
                        ${ch.detail ? `<span class="text-muted-foreground-2">(${escapeHtml(ch.detail)})</span>` : ''}
                    </p>`).join('')}
                </div>` : ev.isCreation ? '' : `<p class="mt-1 text-xs text-muted-foreground-2 italic">Aucune modification détectée.</p>`}
            </div>
        </div>`).join('')}
    </div>`;
}

async function renderProfsTab(offre) {
    const container = document.getElementById('view-professeurs-list');
    if (!container) return;

    const assignedIds = new Set(offre.enseignantIds || []);

    // Skeleton structure immédiat
    container.innerHTML = `
        <div class="flex items-center justify-between mb-3">
            <p class="text-xs font-semibold text-muted-foreground-2 ">
                ${assignedIds.size} enseignant(s) assigné(s)
            </p>
            <button type="button" id="btn-toggle-add-enseignant"
                title="Ajouter un enseignant"
                class="size-7 inline-flex items-center justify-center rounded-lg bg-layer border border-layer-line text-muted-foreground-1 hover:bg-primary hover:text-white hover:border-primary transition-all">
                <i class="bi bi-plus text-sm"></i>
            </button>
        </div>
        <div id="enseignants-assigned" class="space-y-2">
            ${assignedIds.size === 0
                ? `<p class="text-sm text-muted-foreground-2 text-center py-4 italic">Aucun enseignant assigné.</p>`
                : [...assignedIds].map(id => enseignantSkeletonCard(id)).join('')}
        </div>
        <div id="enseignant-search-zone" class="hidden mt-4 border-t border-layer-line pt-4">
            <div class="relative mb-2">
                <svg class="absolute left-3 top-1/2 -translate-y-1/2 size-3.5 text-muted-foreground-2" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="11" cy="11" r="8"/><path d="m21 21-4.34-4.34"/>
                </svg>
                <input id="enseignant-search-input" type="text"
                    placeholder="Rechercher par nom ou email..."
                    class="pl-8 pr-4 py-2 w-full text-xs bg-layer border border-layer-line rounded-lg text-layer-foreground placeholder:text-muted-foreground-2 focus:outline-none focus:ring-1 focus:ring-primary transition-all"/>
            </div>
            <div id="enseignant-search-results" class="space-y-1.5 max-h-44 overflow-y-auto">
                <p class="text-xs text-muted-foreground-2 text-center py-3">Tapez pour rechercher...</p>
            </div>
        </div>`;

    // Toggle search zone
    document.getElementById('btn-toggle-add-enseignant')?.addEventListener('click', async () => {
        const zone = document.getElementById('enseignant-search-zone');
        const isOpen = !zone.classList.contains('hidden');
        zone.classList.toggle('hidden', isOpen);
        if (!isOpen) {
            document.getElementById('enseignant-search-input')?.focus();
            await loadAndRenderSearchResults('');
        }
    });

    document.getElementById('enseignant-search-input')?.addEventListener('input', async (e) => {
        await loadAndRenderSearchResults(e.target.value.trim());
    });

    // Enrichir les cards assignées avec les vrais noms
    if (assignedIds.size > 0) {
        try {
            const [all, classes] = await Promise.all([getAllEnseignants(), getClassesForOffre(offre)]);
            const assignmentsByEnseignant = buildAssignmentsMap(offre);
            const assigned = container.querySelector('#enseignants-assigned');
            if (!assigned) return;
            assigned.innerHTML = [...assignedIds].map(id => {
                const u = all.find(u => String(u.id) === String(id));
                const assignedClasseIds = assignmentsByEnseignant.get(Number(id)) || new Set();
                return enseignantCard(u || { id }, true, classes, assignedClasseIds);
            }).join('') || `<p class="text-sm text-muted-foreground-2 text-center py-4 italic">Aucun enseignant assigné.</p>`;
            wireRemoveButtons();
            wireClasseChips();
        } catch (_) { /* on garde le skeleton */ }
    }
}

async function getClassesForOffre(offre) {
    if (!offre.specialiteId) return [];
    if (cachedClasses && cachedClassesSpecId === offre.specialiteId) return cachedClasses;
    cachedClasses = (await classeApi.getBySpecialiteId(offre.specialiteId)) || [];
    cachedClassesSpecId = offre.specialiteId;
    return cachedClasses;
}

function buildAssignmentsMap(offre) {
    const map = new Map();
    (offre.enseignantAssignments || []).forEach(a => {
        if (!map.has(a.enseignantId)) map.set(a.enseignantId, new Set());
        map.get(a.enseignantId).add(a.classeId);
    });
    return map;
}

async function getAllEnseignants() {
    if (cachedEnseignants) return cachedEnseignants;
    const res = await userApi.retrieveUsers('?role=TEACHER&size=200&deleted=false');
    cachedEnseignants = res?.content || res || [];
    console.log('Enseignants chargés :',res);
    return cachedEnseignants;
}

async function loadAndRenderSearchResults(query) {
    const resultsEl = document.getElementById('enseignant-search-results');
    if (!resultsEl || !currentViewOffre) return;

    resultsEl.innerHTML = `<div class="flex justify-center py-3">
        <div class="animate-spin size-4 border-[2px] border-current border-t-transparent text-primary rounded-full"></div>
    </div>`;

    try {
        const all = await getAllEnseignants();
        const assignedIds = new Set(currentViewOffre.enseignantIds || []);
        const q = query.toLowerCase();

        const results = all.filter(u => {
            if (assignedIds.has(u.id) || assignedIds.has(String(u.id))) return false;
            if (!q) return true;
            const nom = (u.profile?.nom || '').toLowerCase();
            const prenom = (u.profile?.prenom || '').toLowerCase();
            const email = (u.email || '').toLowerCase();
            return nom.includes(q) || prenom.includes(q) || email.includes(q);
        });

        if (results.length === 0) {
            resultsEl.innerHTML = `<p class="text-xs text-muted-foreground-2 text-center py-3">${query ? 'Aucun résultat.' : 'Tous les enseignants sont déjà assignés.'}</p>`;
            return;
        }

        resultsEl.innerHTML = results.map(u => enseignantCard(u, false)).join('');
        wireAddButtons();
    } catch (err) {
        resultsEl.innerHTML = `<p class="text-xs text-red-500 text-center py-3">Erreur lors du chargement.</p>`;
    }
}

function enseignantSkeletonCard(id) {
    return `<div class="flex items-center gap-3 px-4 py-3 bg-surface rounded-xl">
        <div class="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center text-primary shrink-0">
            <i class="bi bi-person text-sm"></i>
        </div>
        <div class="flex-1 min-w-0">
            <p class="text-xs font-medium text-layer-foreground">Enseignant #${id}</p>
        </div>
    </div>`;
}

function enseignantCard(user, isAssigned, classes = [], assignedClasseIds = new Set()) {
    const nom = user.profile ? `${user.profile.prenom || ''} ${user.profile.nom || ''}`.trim() : `Enseignant #${user.id}`;
    const sub = user.profile?.titre ? `${user.profile.titre}${user.profile.specialite ? ' · ' + user.profile.specialite : ''}` : (user.email || `ID: ${user.id}`);
    const initiale = (user.profile?.nom || 'E').charAt(0).toUpperCase();

    return `<div class="bg-surface rounded-xl px-4 py-3" data-enseignant-id="${user.id}">
        <div class="flex items-center gap-3">
            <div class="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center text-primary font-bold text-xs shrink-0">${initiale}</div>
            <div class="flex-1 min-w-0">
                <p class="text-xs font-semibold text-layer-foreground truncate">${escapeHtml(nom)}</p>
                <p class="text-xs text-muted-foreground-2 truncate">${escapeHtml(sub)}</p>
            </div>
            ${isAssigned
                ? `<button type="button" class="btn-remove-enseignant size-6 inline-flex items-center justify-center rounded-md text-muted-foreground-2 hover:bg-red-500/10 hover:text-red-500 transition-all shrink-0"
                        data-id="${user.id}" title="Retirer">
                    <i class="bi bi-x text-sm"></i>
                  </button>`
                : `<button type="button" class="btn-add-enseignant size-6 inline-flex items-center justify-center rounded-md text-muted-foreground-2 hover:bg-primary/10 hover:text-primary transition-all shrink-0"
                        data-id="${user.id}" title="Ajouter">
                    <i class="bi bi-plus text-sm"></i>
                  </button>`}
        </div>
        ${isAssigned ? renderClasseChips(user.id, classes, assignedClasseIds) : ''}
    </div>`;
}

function renderClasseChips(enseignantId, classes, assignedClasseIds) {
    if (!classes || classes.length === 0) {
        return `<p class="mt-2 pl-11 text-[10px] text-muted-foreground-2 italic">Aucune classe disponible pour cette spécialité.</p>`;
    }
    return `<div class="mt-2 pl-11 flex flex-wrap gap-1.5">
        ${classes.map(c => {
            const active = assignedClasseIds.has(c.id);
            return `<button type="button" class="classe-chip text-[10px] font-medium px-2 py-0.5 rounded-full border transition-all ${active
                    ? 'bg-primary text-white border-primary'
                    : 'bg-layer border-layer-line text-muted-foreground-2 hover:border-primary hover:text-primary'}"
                data-enseignant-id="${enseignantId}" data-classe-id="${c.id}" data-active="${active}"
                title="${active ? 'Cliquer pour retirer cette classe' : 'Cliquer pour assigner cette classe'}">
                ${escapeHtml(c.code)}
            </button>`;
        }).join('')}
    </div>`;
}

function wireRemoveButtons() {
    document.querySelectorAll('.btn-remove-enseignant').forEach(btn => {
        btn.addEventListener('click', () => updateOffreEnseignants(Number(btn.dataset.id), 'remove'));
    });
}

function wireAddButtons() {
    document.querySelectorAll('.btn-add-enseignant').forEach(btn => {
        btn.addEventListener('click', () => updateOffreEnseignants(Number(btn.dataset.id), 'add'));
    });
}

function wireClasseChips() {
    document.querySelectorAll('.classe-chip').forEach(btn => {
        btn.addEventListener('click', () => toggleClasseAssignment(
            Number(btn.dataset.enseignantId),
            Number(btn.dataset.classeId),
            btn.dataset.active === 'true',
        ));
    });
}

async function toggleClasseAssignment(enseignantId, classeId, isActive) {
    if (!currentViewOffre) return;

    try {
        const updated = isActive
            ? await offreUeApi.removeEnseignantClasse(currentViewOffre.id, enseignantId, classeId)
            : await offreUeApi.assignEnseignantClasses(currentViewOffre.id, enseignantId, [classeId]);

        currentViewOffre = {
            ...currentViewOffre,
            enseignantAssignments: updated.enseignantAssignments ?? currentViewOffre.enseignantAssignments,
            enseignantIds: updated.enseignantIds ?? currentViewOffre.enseignantIds,
        };

        const idx = currentSubjects.findIndex(s => s.id === currentViewOffre.id);
        if (idx !== -1) currentSubjects[idx] = currentViewOffre;

        renderProfsTab(currentViewOffre);
    } catch (err) {
        GlobalErrorHandler.handle(err);
    }
}

async function updateOffreEnseignants(enseignantId, action) {
    if (!currentViewOffre) return;

    try {
        const ue = await ueApi.getUeById(currentViewOffre.ueId);

        const currentIds = new Set(ue.enseignantIds || []);
        if (action === 'add') currentIds.add(enseignantId);
        else currentIds.delete(enseignantId);

        const payload = {
            libelle: ue.libelle,
            code: ue.code,
            credit: ue.credit,
            volumeHoraireTotal: ue.volumeHoraireTotal,
            description: ue.description || '',
            couleur: ue.couleur || '#7c3aed',
            semestre: ue.semestre,
            specialiteId: ue.specialiteId,
            enseignantIds: [...currentIds],
        };

        const updated = await ueApi.updateUe(currentViewOffre.ueId, payload);
        currentViewOffre = { ...currentViewOffre, enseignantIds: updated.enseignantIds ?? [...currentIds] };

        // Sync page courante
        const idx = currentSubjects.findIndex(s => s.id === currentViewOffre.id);
        if (idx !== -1) currentSubjects[idx] = currentViewOffre;

        GlobalEventNotifier.eventWellDone(action === 'add' ? 'Enseignant ajouté.' : 'Enseignant retiré.');
        renderProfsTab(currentViewOffre);
    } catch (err) {
        GlobalErrorHandler.handle(err);
    }
}

function renderStatsTab(offre) {
    const container = document.getElementById('view-stats-content');
    if (!container) return;

    const year = new Date().getFullYear();
    const nbProfs = (offre.enseignantIds || []).length;

    container.innerHTML = `
        <div class="mb-5">
            <p class="text-xs font-semibold text-muted-foreground-2 uppercase tracking-wide mb-3">Année ${year}</p>
            <div class="grid grid-cols-2 sm:grid-cols-3 gap-3">
                <div class="bg-surface rounded-xl p-4 text-center">
                    <p class="text-xs text-muted-foreground-2 mb-1">Enseignants</p>
                    <p class="text-2xl font-bold text-layer-foreground">${nbProfs}</p>
                </div>
                <div class="bg-surface rounded-xl p-4 text-center">
                    <p class="text-xs text-muted-foreground-2 mb-1">Volume horaire</p>
                    <p class="text-2xl font-bold text-layer-foreground">${offre.volumeHoraireTotal}h</p>
                </div>
                <div class="bg-surface rounded-xl p-4 text-center">
                    <p class="text-xs text-muted-foreground-2 mb-1">Crédits ECTS</p>
                    <p class="text-2xl font-bold text-layer-foreground">${offre.credit}</p>
                </div>
            </div>
        </div>
        <div class="bg-surface rounded-xl p-5 flex flex-col items-center justify-center gap-2 py-8">
            <i class="bi bi-bar-chart-line text-3xl text-muted-foreground-2"></i>
            <p class="text-sm text-muted-foreground-2 text-center">Les statistiques détaillées (inscriptions, notes, taux de réussite) seront disponibles prochainement.</p>
        </div>`;
}

// ── Helpers ───────────────────────────────────────────────────────────────────

function emptyState(icon, message) {
    return `<div class="flex flex-col items-center justify-center py-12 gap-3 text-muted-foreground-2">
        <i class="bi ${icon} text-3xl"></i>
        <p class="text-sm">${message}</p>
    </div>`;
}

function formatDate(dt) {
    if (!dt) return '—';
    const d = new Date(dt);
    return d.toLocaleDateString('fr-FR', { day: '2-digit', month: 'long', year: 'numeric' });
}

function formatDateCompact(dt) {
    if (!dt) return '—';
    const d = new Date(dt);
    const day  = d.toLocaleDateString('fr-FR', { day: '2-digit' });
    const mon  = d.toLocaleDateString('fr-FR', { month: 'short' }).replace('.', '');
    const year = d.getFullYear();
    return `${day} ${mon}<br><span class="opacity-60">${year}</span>`;
}

function escapeHtml(str) {
    return String(str ?? '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

// Auto-init on load
document.addEventListener('DOMContentLoaded', () => {
    APSubjectsController.init();
});

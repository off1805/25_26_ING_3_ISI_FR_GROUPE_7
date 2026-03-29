import { GlobalErrorHandler } from "../../common/GlobalErrorHandler.js";
import { GlobalEventNotifier } from "../../common/GlobalEventNotifier.js";
import { CycleUseCase } from "../application/CycleUseCase.js";
import { FiliereUseCase } from "../application/FiliereUseCase.js";
import { NiveauUseCase } from "../application/NiveauUseCase.js";
import { SpecialiteUseCase } from "../application/SpecialiteUseCase.js";
import { ClasseUseCase } from "../application/ClasseUseCase.js";
import { CycleApi } from "../infrastructure/CycleApi.js";
import { FiliereApi } from "../infrastructure/FiliereApi.js";
import { NiveauApi } from "../infrastructure/NiveauApi.js";
import { SpecialiteApi } from "../infrastructure/SpecialiteApi.js";
import { ClasseApi } from "../infrastructure/ClasseApi.js";

// ─────────────────────────────────────────────────────────────────────────────
const SELECTORS = {
    // Arbre Preline
    treeRoot: '#hierarchy-tree',
    descPanel: '#tree-description-panel',
    descTypeBadge: '#tree-desc-type-badge',

    dataAttributes: {
        cycleId: 'cycleId',
        filiereId: 'filiereId',
        niveauId: 'niveauId',
        specialiteId: 'specialiteId',
    },

    // Modals d'ajout contextuel
    createFiliereModal: '#hs-modal-add-filiere',
    createNiveauModal: '#hs-modal-add-niveau',
    createSpecialiteModal: '#hs-modal-add-specialite',
    createClasseModal: '#hs-modal-add-classe',
};

// ─── Hiérarchie ───────────────────────────────────────────────────────────────
const HIERARCHY = {
    cycle: { childType: 'filiere', label: 'Filières' },
    filiere: { childType: 'niveau', label: 'Niveaux' },
    niveau: { childType: 'specialite', label: 'Spécialités' },
    specialite: { childType: 'classe', label: 'Classes' },
    classe: { childType: null, label: null },
};

// ─── Config visuelle par type ─────────────────────────────────────────────────
const TYPE_CONFIG = {
    cycle: { color: 'text-violet-500', bg: 'bg-violet-500/10', label: 'Cycle' },
    filiere: { color: 'text-blue-500', bg: 'bg-blue-500/10', label: 'Filière' },
    niveau: { color: 'text-emerald-500', bg: 'bg-emerald-500/10', label: 'Niveau' },
    specialite: { color: 'text-amber-500', bg: 'bg-amber-500/10', label: 'Spécialité' },
    classe: { color: 'text-rose-500', bg: 'bg-rose-500/10', label: 'Classe' },
};

// ─── Icônes par type ──────────────────────────────────────────────────────────
const ICONS = {
    cycle: `<svg class="shrink-0 size-4" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2L2 7l10 5 10-5-10-5z"/><path d="M2 17l10 5 10-5"/><path d="M2 12l10 5 10-5"/></svg>`,
    filiere: `<svg class="shrink-0 size-4" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/></svg>`,
    niveau: `<svg class="shrink-0 size-4" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="18" height="18" rx="2"/><path d="M3 9h18M9 21V9"/></svg>`,
    specialite: `<svg class="shrink-0 size-4" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="M12 8v4l3 3"/></svg>`,
    classe: `<svg class="shrink-0 size-4" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>`,
};

// ─────────────────────────────────────────────────────────────────────────────

export class CycleController {
    constructor(
        cycleApi, filiereApi, niveauApi, specialiteApi, classeApi,
        cycleUseCase, filiereUseCase, niveauUseCase, specialiteUseCase, classeUseCase
    ) {
        console.log('[CycleController] initialisation du controller');
        this.cycleApi = cycleApi;
        this.filiereApi = filiereApi;
        this.niveauApi = niveauApi;
        this.specialiteApi = specialiteApi;
        this.classeApi = classeApi;
        this.cycleUseCase = cycleUseCase;
        this.filiereUseCase = filiereUseCase;
        this.niveauUseCase = niveauUseCase;
        this.specialiteUseCase = specialiteUseCase;
        this.classeUseCase = classeUseCase;

        // Cache : "type:id" → true — évite les re-fetch
        this._loadedNodes = new Set();
        // Dernier .hs-accordion-selectable actif
        this._activeSelectable = null;

        this.init();
    }

    init() {
        // Attendre que Preline initialise les éléments du template
        if (window.HSStaticMethods) {
            window.HSStaticMethods.autoInit();
        }

        // Puis attacher les listeners
        this._bindTree();
        this._bindForms();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  FORMULAIRES 'create-*' (cycle/filiere/niveau/specialite/classe)
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Un seul listener par délégation sur #hierarchy-tree.
     *
     * Deux cibles :
     *  1. .hs-accordion-toggle  → lazy-load des enfants (puis Preline gère open/close)
     *  2. .tree-selectable      → mise à jour du panneau description
     */
    _bindTree() {
        const tree = document.querySelector(SELECTORS.treeRoot);
        console.log("tree view okay");
        if (!tree) return;

        tree.addEventListener('click', async (e) => {
            console.log(e.target);
            console.log(e.target.closest('.hs-accordion'));
            // ── 1. Chevron Preline ──────────────────────────────────────────
            const toggleBtn = e.target.closest('.hs-accordion-toggle');
            if (toggleBtn) {
                e.preventDefault();
                e.stopPropagation();

                const accordion = toggleBtn.closest('.hs-accordion');
                if (!accordion) return;
                const id = accordion.dataset.nodeId;
                const type = accordion.dataset.nodeType;
                if (!id || !type) return;

                const collapseEl = accordion.querySelector(':scope > .hs-accordion-content');
                const isOpen = collapseEl && !collapseEl.classList.contains('hidden');

                if (!isOpen) {

                    await this._loadChildrenAndOpen(toggleBtn, accordion, id, type);
                } else {

                    this._closeAccordion(accordion);
                }
                return;
            }

            const selectable = e.target.closest('.tree-selectable');
            if (!selectable) return;



            const accordion = selectable.closest('.hs-accordion');
            if (accordion) {
                const id = accordion.dataset.nodeId;
                const type = accordion.dataset.nodeType;
                const name = accordion.dataset.nodeName;
                console.log(`[DEBUG] Clic sélectionnable : ${type} ${id} - ${name}`);
                this._setFocus(selectable, { id, type, name });
            } else {
                console.warn('[DEBUG] Aucun accordéon parent trouvé pour selectable');
            }



        });
        const descPanel = document.querySelector(SELECTORS.descPanel);
        if (descPanel) {
            descPanel.addEventListener('click', (e) => {

                if (e.target.closest('.btn-add-filiere')) {
                    const btn = e.target.closest('.btn-add-filiere');
                    this._handleAddFiliere(btn);
                    return;
                }
                if (e.target.closest('.btn-add-niveau')) {
                    const btn = e.target.closest('.btn-add-niveau');
                    this._handleAddNiveau(btn);
                    return;
                }
                if (e.target.closest('.btn-add-specialite')) {
                    const btn = e.target.closest('.btn-add-specialite');
                    this._handleAddSpecialite(btn);
                    return;
                }
                if (e.target.closest('.btn-add-classe')) {
                    const btn = e.target.closest('.btn-add-classe');
                    this._handleAddClasse(btn);
                    return;
                }

                // ── Actions du dropdown unifié ───────────────────────────────
                if (e.target.closest('.btn-edit-entity')) {
                    this._handleEditEntity(
                        this._currentEntityType,
                        this._currentEntityId,
                        this._currentEntityDetails
                    );
                    return;
                }
                if (e.target.closest('.btn-toggle-status')) {
                    this._handleToggleStatus(
                        this._currentEntityType,
                        this._currentEntityId,
                        this._currentEntityDetails
                    );
                    return;
                }
                if (e.target.closest('.btn-delete-entity')) {
                    this._handleDeleteEntity(
                        this._currentEntityType,
                        this._currentEntityId
                    );
                    return;
                }
            });
        }
    }

    _bindForms() {
        const createCycleForm = document.querySelector('form[data-form="create-cycle"]');
        if (createCycleForm) {
            createCycleForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                await this._handleCreateEntity(createCycleForm, this.cycleApi.createCycle.bind(this.cycleApi), 'cycle', null);
            });
        }

        const createFiliereForm = document.querySelector('form[data-form="create-filiere"]');
        if (createFiliereForm) {
            createFiliereForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const cycleId = createFiliereForm.querySelector('#filiere-cycleId').value;
                await this._handleCreateEntity(createFiliereForm, this.filiereApi.createFiliere.bind(this.filiereApi), 'filiere', cycleId);
            });
        }

        const createNiveauForm = document.querySelector('form[data-form="create-niveau"]');
        if (createNiveauForm) {
            createNiveauForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const filiereId = createNiveauForm.querySelector('#niveau-filiereId').value;
                await this._handleCreateEntity(createNiveauForm, this.niveauApi.create.bind(this.niveauApi), 'niveau', filiereId);
            });
        }

        const createSpecialiteForm = document.querySelector('form[data-form="create-specialite"]');
        if (createSpecialiteForm) {
            createSpecialiteForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const niveauId = createSpecialiteForm.querySelector('#specialite-niveauId').value;
                await this._handleCreateEntity(createSpecialiteForm, this.specialiteApi.create.bind(this.specialiteApi), 'specialite', niveauId);
            });
        }

        const createClasseForm = document.querySelector('form[data-form="create-classe"]');
        if (createClasseForm) {
            createClasseForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const specialiteId = createClasseForm.querySelector('#classe-specialiteId').value;
                await this._handleCreateEntity(createClasseForm, this.classeApi.create.bind(this.classeApi), 'classe', specialiteId);
            });
        }

        // ── Formulaires de mise à jour ─────────────────────────────────────
        const updateCycleForm = document.querySelector('form[data-form="update-cycle"]');
        if (updateCycleForm) {
            updateCycleForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const id = updateCycleForm.dataset.editId;
                if (!id) return;
                const data = Object.fromEntries(new FormData(updateCycleForm).entries());
                if (data.durationYears) data.durationYears = Number(data.durationYears);
                try {
                    await this.cycleApi.updateCycle(id, data);
                    GlobalEventNotifier.eventWellDone('Cycle mis à jour avec succès');
                    window.HSOverlay?.close(document.querySelector('#hs-modal-edit-cycle'));
                    if (this._activeSelectable) {
                        const acc = this._activeSelectable.closest('.hs-accordion');
                        acc?.setAttribute('data-node-name', data.name);
                        const nameEl = acc?.querySelector('.tree-selectable .text-sm');
                        if (nameEl) nameEl.textContent = data.name;
                        this._setFocus(this._activeSelectable, { id, type: 'cycle', name: data.name });
                    }
                } catch (err) { GlobalErrorHandler.handle(err); }
            });
        }

        const updateFiliereForm = document.querySelector('form[data-form="update-filiere"]');
        if (updateFiliereForm) {
            updateFiliereForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const id = updateFiliereForm.dataset.editId;
                if (!id) return;
                const data = Object.fromEntries(new FormData(updateFiliereForm).entries());
                try {
                    await this.filiereApi.updateFiliere(id, data);
                    GlobalEventNotifier.eventWellDone('Filière mise à jour avec succès');
                    window.HSOverlay?.close(document.querySelector('#hs-modal-edit-filiere'));
                    if (this._activeSelectable) {
                        const label = data.nom || data.code;
                        const acc = this._activeSelectable.closest('.hs-accordion');
                        acc?.setAttribute('data-node-name', label);
                        const nameEl = acc?.querySelector('.tree-selectable .text-sm');
                        if (nameEl) nameEl.textContent = label;
                        this._setFocus(this._activeSelectable, { id, type: 'filiere', name: label });
                    }
                } catch (err) { GlobalErrorHandler.handle(err); }
            });
        }

        const updateNiveauForm = document.querySelector('form[data-form="update-niveau"]');
        if (updateNiveauForm) {
            updateNiveauForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const id = updateNiveauForm.dataset.editId;
                if (!id) return;
                const data = Object.fromEntries(new FormData(updateNiveauForm).entries());
                if (data.ordre) data.ordre = Number(data.ordre);
                try {
                    await this.niveauApi.update(id, data);
                    GlobalEventNotifier.eventWellDone('Niveau mis à jour avec succès');
                    window.HSOverlay?.close(document.querySelector('#hs-modal-edit-niveau'));
                    if (this._activeSelectable) {
                        this._setFocus(this._activeSelectable, { id, type: 'niveau', name: `Niveau ${data.ordre}` });
                    }
                } catch (err) { GlobalErrorHandler.handle(err); }
            });
        }

        const updateSpecialiteForm = document.querySelector('form[data-form="update-specialite"]');
        if (updateSpecialiteForm) {
            updateSpecialiteForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const id = updateSpecialiteForm.dataset.editId;
                if (!id) return;
                const data = Object.fromEntries(new FormData(updateSpecialiteForm).entries());
                try {
                    await this.specialiteApi.update(id, data);
                    GlobalEventNotifier.eventWellDone('Spécialité mise à jour avec succès');
                    window.HSOverlay?.close(document.querySelector('#hs-modal-edit-specialite'));
                    if (this._activeSelectable) {
                        const label = data.libelle || data.code;
                        const acc = this._activeSelectable.closest('.hs-accordion');
                        acc?.setAttribute('data-node-name', label);
                        const nameEl = acc?.querySelector('.tree-selectable .text-sm');
                        if (nameEl) nameEl.textContent = label;
                        this._setFocus(this._activeSelectable, { id, type: 'specialite', name: label });
                    }
                } catch (err) { GlobalErrorHandler.handle(err); }
            });
        }

        const updateClasseForm = document.querySelector('form[data-form="update-classe"]');
        if (updateClasseForm) {
            updateClasseForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const id = updateClasseForm.dataset.editId;
                if (!id) return;
                const data = Object.fromEntries(new FormData(updateClasseForm).entries());
                try {
                    await this.classeApi.update(id, data);
                    GlobalEventNotifier.eventWellDone('Classe mise à jour avec succès');
                    window.HSOverlay?.close(document.querySelector('#hs-modal-edit-classe'));
                    if (this._activeSelectable) {
                        const acc = this._activeSelectable.closest('.hs-accordion');
                        acc?.setAttribute('data-node-name', data.code);
                        const nameEl = acc?.querySelector('.tree-selectable .text-sm');
                        if (nameEl) nameEl.textContent = data.code;
                        this._setFocus(this._activeSelectable, { id, type: 'classe', name: data.code });
                    }
                } catch (err) { GlobalErrorHandler.handle(err); }
            });
        }
    }

    async _handleCreateEntity(form, apiMethod, entityType, parentId) {
        const data = Object.fromEntries(new FormData(form).entries());

        if (data.durationYears) data.durationYears = Number(data.durationYears);
        if (data.ordre) data.ordre = Number(data.ordre);
        if (data.cycleId) data.cycleId = Number(data.cycleId);
        if (data.filiereId) data.filiereId = Number(data.filiereId);
        if (data.niveauId) data.niveauId = Number(data.niveauId);
        if (data.specialiteId) data.specialiteId = Number(data.specialiteId);

        try {
            const createdEntity = await apiMethod(data);
            GlobalEventNotifier.eventWellDone('Création effectuée avec succès');

            // Fermer le modal
            const modalSelector = `#hs-modal-add-${entityType}`;
            const modal = document.querySelector(modalSelector);
            window.HSOverlay?.close(modal);

            // Insérer l'entité dans l'arbre sans recharger la page
            this._insertEntityIntoTree(createdEntity, entityType, parentId);

            // Reset du formulaire
            form.reset();

        } catch (error) {
            GlobalErrorHandler.handle(error);
        }
    }

    /**
     * Insère une entité nouvellement créée dans l'arbre DOM sans recharger la page.
     */
    _insertEntityIntoTree(createdEntity, entityType, parentId) {
        const cfg = TYPE_CONFIG[entityType] ?? TYPE_CONFIG.classe;
        const name = createdEntity.name ?? createdEntity.nom ?? createdEntity.code ?? '—';
        const id = createdEntity.id;
        const hasChildren = !!HIERARCHY[entityType]?.childType;

        if (entityType === 'cycle') {
            // Pour les cycles : insérer à la racine de l'arbre
            const treeRoot = document.querySelector(SELECTORS.treeRoot);
            const accordionGroup = treeRoot?.querySelector('.hs-accordion-group');

            if (!accordionGroup) return;

            const node = this._createAccordionNode(createdEntity, entityType, hasChildren);
            accordionGroup.appendChild(node);
        } else {
            // Pour les autres entités : insérer dans le collapse de leur parent
            const parentAccordion = document.querySelector(`[data-node-id="${parentId}"][data-node-type="${this._getParentType(entityType)}"]`);
            if (!parentAccordion) return;

            let collapseEl = parentAccordion.querySelector(':scope > .hs-accordion-content');
            if (!collapseEl) {
                // Créer le collapse s'il n'existe pas
                collapseEl = document.createElement('div');
                collapseEl.className = 'hs-accordion-content w-full overflow-hidden transition-[height] duration-300 hidden';
                collapseEl.setAttribute('role', 'group');
                parentAccordion.appendChild(collapseEl);
            }

            let group = collapseEl.querySelector('.hs-accordion-group');
            if (!group) {
                // Créer le groupe s'il n'existe pas
                group = document.createElement('div');
                group.className = [
                    'hs-accordion-group',
                    'ps-7 relative',
                    'before:absolute before:top-0 before:start-3 before:-ms-px',
                    'before:h-full before:border-s before:border-line-2',
                ].join(' ');
                group.setAttribute('role', 'group');
                group.setAttribute('data-hs-accordion-always-open', '');
                collapseEl.appendChild(group);
            }

            const node = this._createAccordionNode(createdEntity, entityType, hasChildren);
            group.appendChild(node);

            // Marquer le parent comme chargé et montrer le chevron
            const cacheKey = `${this._getParentType(entityType)}:${parentId}`;
            this._loadedNodes.add(cacheKey);
            this._showChevron(parentAccordion.querySelector('.hs-accordion-toggle'));
        }

        // Réinitialiser Preline pour les nouveaux éléments
        window.HSStaticMethods?.autoInit();
    }

    /**
     * Crée un nœud d'accordéon pour une entité.
     */
    _createAccordionNode(entity, entityType, hasChildren) {
        const cfg = TYPE_CONFIG[entityType] ?? TYPE_CONFIG.classe;
        const name = entity.name ?? entity.nom ?? entity.code ?? '—';
        const id = entity.id;
        const collapseId = `tree-collapse-${entityType}-${id}`;
        const nodeId = `tree-node-${entityType}-${id}`;

        const node = document.createElement('div');
        node.className = 'hs-accordion';
        node.setAttribute('role', 'treeitem');
        node.setAttribute('id', nodeId);
        node.dataset.nodeId = id;
        node.dataset.nodeType = entityType;
        node.dataset.nodeName = name;

        node.innerHTML = `
            <div class="hs-accordion-heading py-0.5 flex items-center gap-x-0.5 w-full">
                <button class="hs-accordion-toggle size-6 flex justify-center items-center
                               hover:bg-muted-hover rounded-md focus:outline-hidden focus:bg-muted-focus
                               disabled:opacity-50 disabled:pointer-events-none
                               ${!hasChildren ? 'invisible' : ''}"
                        aria-controls="${collapseId}">
                    <svg class="tree-spinner hidden animate-spin size-3 text-muted-foreground-2"
                         xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none"
                         stroke="currentColor" stroke-width="2">
                        <path d="M21 12a9 9 0 1 1-6.219-8.56"/>
                    </svg>
                    <svg class="tree-chevron hs-accordion-active:rotate-90 transition duration-300
                                size-2.5 text-muted-foreground-2 ${!hasChildren ? 'hidden' : ''}"
                         xmlns="http://www.w3.org/2000/svg" width="16" height="16"
                         fill="currentColor" viewBox="0 0 16 16">
                        <path d="m12.14 8.753-5.482 4.796c-.646.566-1.658.106-1.658-.753V3.204a1 1 0 0 1 1.659-.753l5.48 4.796a1 1 0 0 1 0 1.506z"/>
                    </svg>
                </button>
                <div class="grow hs-accordion-selectable hs-accordion-selected:bg-muted-active
                            px-1.5 rounded-md cursor-pointer tree-selectable"
                     data-node-id="${id}"
                     data-node-type="${entityType}"
                     data-node-name="${name}">
                    <div class="flex items-center gap-x-2 py-0.5">
                        <span class="${cfg.color}">${ICONS[entityType] ?? ICONS.classe}</span>
                        <span class="text-sm text-foreground truncate">${name}</span>
                    </div>
                </div>
            </div>
            <div id="${collapseId}"
                 class="hs-accordion-content w-full overflow-hidden transition-[height] duration-300 hidden"
                 role="group"
                 aria-labelledby="${nodeId}">
            </div>
        `;

        return node;
    }

    /**
     * Retourne le type du parent pour un type d'entité donné.
     */
    _getParentType(entityType) {
        switch (entityType) {
            case 'filiere': return 'cycle';
            case 'niveau': return 'filiere';
            case 'specialite': return 'niveau';
            case 'classe': return 'specialite';
            default: return null;
        }
    }

    /**
     * Montre le chevron d'un bouton toggle.
     */
    _showChevron(btn) {
        if (!btn) return;
        const chevron = btn.querySelector('.tree-chevron');
        const spinner = btn.querySelector('.tree-spinner');
        if (chevron) chevron.classList.remove('hidden');
        if (spinner) spinner.classList.add('hidden');
        btn.classList.remove('invisible');
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  NOUVELLES MÉTHODES POUR LA GESTION MANUELLE DES ACCORDÉONS
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Charge les enfants (lazy load) puis ouvre l'accordéon.
     */
    async _loadChildrenAndOpen(btn, accordion, id, type) {
        const cacheKey = `${type}:${id}`;
        const collapseEl = accordion.querySelector(':scope > .hs-accordion-content');
        if (!collapseEl) return;

        // Si déjà chargé, on ouvre directement
        if (this._loadedNodes.has(cacheKey)) {
            this._openAccordion(accordion);
            return;
        }

        // Si c'est une feuille (pas d'enfants), on ne fait rien
        if (!HIERARCHY[type]?.childType) return;

        btn.disabled = true;
        this._setLoading(btn, true);

        try {
            const children = await this._fetchChildren(type, id);
            this._loadedNodes.add(cacheKey);

            if (!children || children.length === 0) {
                this._hideChevron(btn);
            } else {
                const group = this._buildChildAccordionGroup(children, HIERARCHY[type].childType);
                collapseEl.appendChild(group);
                // Réinitialiser Preline pour les nouveaux accordions
                window.HSStaticMethods?.autoInit();

                // Ouvrir après ajout des enfants
                this._openAccordion(accordion);
            }
        } catch (err) {
            GlobalErrorHandler.handle(err);
        } finally {
            this._setLoading(btn, false);
            btn.disabled = false;
        }
    }

    /**
     * Ouvre un accordéon avec animation.
     */
    _openAccordion(accordion) {
        const collapseEl = accordion.querySelector(':scope > .hs-accordion-content');
        if (!collapseEl) return;
        if (!collapseEl.classList.contains('hidden')) return; // déjà ouvert

        // Retirer la classe hidden pour afficher le contenu
        collapseEl.classList.remove('hidden');

        // Force l'animation de hauteur
        collapseEl.style.height = 'auto';
        const height = collapseEl.scrollHeight;
        collapseEl.style.height = '0px';
        // Force le reflow pour que l'animation se déclenche
        collapseEl.offsetHeight;
        collapseEl.style.height = height + 'px';

        // Ajouter la classe active pour le style du chevron
        accordion.classList.add('active');

        // Nettoyer les styles après la transition
        const onTransitionEnd = () => {
            collapseEl.style.height = '';
            collapseEl.removeEventListener('transitionend', onTransitionEnd);
        };
        collapseEl.addEventListener('transitionend', onTransitionEnd);
    }

    /**
     * Ferme un accordéon avec animation.
     */
    _closeAccordion(accordion) {
        const collapseEl = accordion.querySelector(':scope > .hs-accordion-content');
        if (!collapseEl) return;
        if (collapseEl.classList.contains('hidden')) return; // déjà fermé

        // Démarrer l'animation de fermeture
        collapseEl.style.height = collapseEl.scrollHeight + 'px';
        collapseEl.offsetHeight; // force reflow
        collapseEl.style.height = '0px';

        const onTransitionEnd = () => {
            collapseEl.classList.add('hidden');
            collapseEl.style.height = '';
            collapseEl.removeEventListener('transitionend', onTransitionEnd);
        };
        collapseEl.addEventListener('transitionend', onTransitionEnd);

        accordion.classList.remove('active');
    }

    /**
     * Construit le <div class="hs-accordion-group"> des enfants.
     * Respecte exactement la structure Preline hs-accordion-treeview :
     *
     *   .hs-accordion-group  [ps-7 + trait vertical before:]
     *     └── .hs-accordion  [role=treeitem, data-node-*]
     *           ├── .hs-accordion-heading
     *           │     ├── button.hs-accordion-toggle  (chevron + spinner)
     *           │     └── div.hs-accordion-selectable.tree-selectable
     *           └── div.hs-accordion-content          (vide → rempli au prochain clic)
     */
    _buildChildAccordionGroup(items, type) {
        const hasChildren = !!HIERARCHY[type]?.childType;
        const cfg = TYPE_CONFIG[type] ?? TYPE_CONFIG.classe;

        const group = document.createElement('div');
        group.className = [
            'hs-accordion-group',
            'ps-7 relative',
            'before:absolute before:top-0 before:start-3 before:-ms-px',
            'before:h-full before:border-s before:border-line-2',
        ].join(' ');
        group.setAttribute('role', 'group');
        group.setAttribute('data-hs-accordion-always-open', '');

        for (const item of items) {
            const name = item.name ?? item.nom ?? item.code ?? "Niveau " + item.ordre ?? '—';
            const id = item.id;
            const collapseId = `tree-collapse-${type}-${id}`;
            const nodeId = `tree-node-${type}-${id}`;

            const node = document.createElement('div');
            node.className = 'hs-accordion';
            node.setAttribute('role', 'treeitem');
            node.setAttribute('id', nodeId);
            node.dataset.nodeId = id;
            node.dataset.nodeType = type;
            node.dataset.nodeName = name;

            node.innerHTML = `
                <div class="hs-accordion-heading py-0.5 flex items-center gap-x-0.5 w-full">

                    <!-- Bouton chevron Preline -->
                    <button class="hs-accordion-toggle size-6 flex justify-center items-center
                                   hover:bg-muted-hover rounded-md focus:outline-hidden focus:bg-muted-focus
                                   disabled:opacity-50 disabled:pointer-events-none
                                   ${!hasChildren ? 'invisible' : ''}"
                            aria-controls="${collapseId}">
                        <!-- Spinner (caché par défaut) -->
                        <svg class="tree-spinner hidden animate-spin size-3 text-muted-foreground-2"
                             xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none"
                             stroke="currentColor" stroke-width="2">
                            <path d="M21 12a9 9 0 1 1-6.219-8.56"/>
                        </svg>
                        <!-- Chevron Preline -->
                        <svg class="tree-chevron hs-accordion-active:rotate-90 transition duration-300
                                    size-2.5 text-muted-foreground-2 ${!hasChildren ? 'hidden' : ''}"
                             xmlns="http://www.w3.org/2000/svg" width="16" height="16"
                             fill="currentColor" viewBox="0 0 16 16">
                            <path d="m12.14 8.753-5.482 4.796c-.646.566-1.658.106-1.658-.753V3.204a1 1 0 0 1 1.659-.753l5.48 4.796a1 1 0 0 1 0 1.506z"/>
                        </svg>
                    </button>

                    <!-- Zone sélectionnable -->
                    <div class="grow 
                                px-1.5  rounded-md cursor-pointer tree-selectable"
                         data-node-id="${id}"
                         data-node-type="${type}"
                         data-node-name="${name}">
                        <div class="flex items-center gap-x-2 py-1">
                            <span class="${cfg.color}">${ICONS[type] ?? ICONS.classe}</span>
                            <span class="text-sm text-foreground truncate">${name}</span>
                        </div>
                    </div>
                </div>

                <!-- Collapse vide — rempli au prochain clic si hasChildren -->
                <div id="${collapseId}"
                     class="hs-accordion-content w-full overflow-hidden transition-[height] duration-300 hidden"
                     role="group"
                     aria-labelledby="${nodeId}">
                </div>
            `;

            group.appendChild(node);
        }

        return group;
    }

    // ─── Routage API ──────────────────────────────────────────────────────────
    async _fetchChildren(parentType, parentId) {
        switch (parentType) {
            case 'cycle': return this.filiereApi.getFiliereFromCycle(parentId);
            case 'filiere': return this.niveauApi.getByFiliereId(parentId);
            case 'niveau': return this.specialiteApi.getByNiveauId(parentId);
            case 'specialite': return this.classeApi.getBySpecialiteId(parentId);
            default: return [];
        }
    }

    // ─── Panneau description ──────────────────────────────────────────────────
    async _setFocus(selectableEl, { id, type, name }) {
        console.log(`[DEBUG] _setFocus appelé pour ${type} ${id}`);

        // Retirer le focus du précédent élément
        if (this._activeSelectable) {
            this._activeSelectable.classList.remove('bg-muted-active');
            console.log('[DEBUG] Focus retiré de', this._activeSelectable);
        }

        // Ajouter la classe de sélection
        selectableEl.classList.add('bg-muted-active');
        this._activeSelectable = selectableEl;
        console.log('[DEBUG] Classe hs-accordion-selected ajoutée sur', selectableEl);

        // Récupération et affichage des détails (votre code existant)
        const cfg = TYPE_CONFIG[type] ?? TYPE_CONFIG.classe;
        const panel = document.querySelector(SELECTORS.descPanel);
        const badge = document.querySelector(SELECTORS.descTypeBadge);
        if (!panel) return;

        if (badge) {
            badge.textContent = cfg.label;
            badge.className = `text-xs font-bold px-2.5 py-1 rounded-lg ${cfg.bg} ${cfg.color}`;
        }

        // État de chargement
        panel.innerHTML = `<div class="flex items-center justify-center h-full"><div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div></div>`;

        try {
            const details = await this._fetchDetails(type, id);
            console.log(`[DEBUG] Détails reçus pour ${type}:`, details);
            // Stocker l'entité courante pour les handlers du dropdown
            this._currentEntityType = type;
            this._currentEntityId = id;
            this._currentEntityDetails = details;
            panel.innerHTML = this._renderDetailsPanel(type, details, cfg);
            window.HSStaticMethods?.autoInit();
        } catch (err) {
            console.error(`[DEBUG] Erreur chargement détails:`, err);
            GlobalErrorHandler.handle(err);
            panel.innerHTML = `<div class="flex flex-col items-center justify-center text-center h-full gap-4 py-16">...</div>`;
        }
    }

    // ─── Récupération des détails ─────────────────────────────────────────────
    async _fetchDetails(type, id) {
        switch (type) {
            case 'cycle': return this.cycleApi.getCycleById(id);
            case 'filiere': return this.filiereApi.getFiliereById(id);
            case 'niveau': return this.niveauApi.getNiveauById(id);
            case 'specialite': return this.specialiteApi.getSpecialiteById(id);
            case 'classe': return this.classeApi.getClasseById(id);
            default: throw new Error(`Type inconnu: ${type}`);
        }
    }

    // ─── Rendu du panneau de détails ──────────────────────────────────────────
    _renderDetailsPanel(type, details, cfg) {
        const name = details.name ?? details.nom ?? details.code ?? '—';
        const id = details.id;

        let detailsHtml = '';

        switch (type) {
            case 'cycle':
                detailsHtml = `
                <div class=" w-full mx-auto p-5 ">
                                <!-- User ID -->
                                <div class="flex justify-between items-center py-3 border-b border-layer-line">
                                    <span class="text-sm text-muted-foreground w-50">Code:</span>
                                    <span class="text-sm text-foreground">${details.code || '—'}</span>
                                </div>

                                <!-- Email -->
                                <div class="flex  items-center py-3 border-b border-layer-line">
                                    <span class="text-sm text-muted-foreground w-50">Durée:</span>
                                    <span class="text-sm text-foreground">${details.durationYears || '—'}</span>
                                </div>

                                <!-- Phone -->
                                <div class="flex justify-between items-center py-3 border-b border-layer-line">
                                    <span class="text-sm text-muted-foreground ">Statut:</span>
                                    <span class=" inline-flex items-center gap-x-2 text-xs font-medium px-2.5 py-1 rounded-full ${details.status === 'ACTIVE' ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'
                    }">
                                <span class="w-2 h-2 rounded-full ${details.status === 'ACTIVE' ? 'bg-green-600' : 'bg-red-600'}"></span>
                                ${details.status === 'ACTIVE' ? 'Actif' : 'Inactif'}
                            </span>
                                </div>

                                <!-- Address -->
                                <div class="flex justify-between  py-3 border-b border-layer-line">
                                    <span class="text-sm text-muted-foreground">Description:</span>
                                    <span class="text-sm text-foreground text-right">${details.description || '—'}</span>
                    
                                </div>

                               
                            </div>
                `;
                break;
            case 'filiere':
                detailsHtml = `
                <div class=" w-full mx-auto p-5 ">
                    <div class="flex justify-between items-center py-3 border-b border-layer-line">
                        <span class="text-sm text-muted-foreground w-50">Code:</span>
                        <span class="text-sm text-foreground">${details.code || '—'}</span>
                    </div>
                    <div class="flex justify-between items-center py-3 border-b border-layer-line">
                        <span class="text-sm text-muted-foreground w-50">Nom:</span>
                        <span class="text-sm text-foreground">${details.nom || '—'}</span>
                    </div>
                    <div class="flex justify-between py-3 border-b border-layer-line">
                        <span class="text-sm text-muted-foreground">Description:</span>
                        <span class="text-sm text-foreground text-right">${details.description || '—'}</span>
                    </div>
                </div>
                `;
                break;
            case 'niveau':
                detailsHtml = `
                <div class=" w-full mx-auto p-5 ">
                    <div class="flex justify-between items-center py-3 border-b border-layer-line">
                        <span class="text-sm text-muted-foreground w-50">Ordre:</span>
                        <span class="text-sm text-foreground">${details.ordre || '—'}</span>
                    </div>
                </div>
                `;
                break;
            case 'specialite':
                detailsHtml = `
                <div class=" w-full mx-auto p-5 ">
                    <div class="flex justify-between items-center py-3 border-b border-layer-line">
                        <span class="text-sm text-muted-foreground w-50">Code:</span>
                        <span class="text-sm text-foreground">${details.code || '—'}</span>
                    </div>
                    <div class="flex justify-between items-center py-3 border-b border-layer-line">
                        <span class="text-sm text-muted-foreground w-50">Libellé:</span>
                        <span class="text-sm text-foreground">${details.libelle || '—'}</span>
                    </div>
                    <div class="flex justify-between items-center py-3 border-b border-layer-line">
                        <span class="text-sm text-muted-foreground w-50">Branche:</span>
                        <span class="text-sm text-foreground">${details.brancheCode || '—'}</span>
                    </div>
                    <div class="flex justify-between items-center py-3 border-b border-layer-line">
                        <span class="text-sm text-muted-foreground w-50">Niveau min.:</span>
                        <span class="text-sm text-foreground">${details.niveauMinimum || '—'}</span>
                    </div>
                    <div class="flex justify-between items-center py-3 border-b border-layer-line">
                        <span class="text-sm text-muted-foreground">Statut:</span>
                        <span class="inline-flex items-center gap-x-2 text-xs font-medium px-2.5 py-1 rounded-full ${details.status === 'ACTIVE' ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'}">
                            <span class="w-2 h-2 rounded-full ${details.status === 'ACTIVE' ? 'bg-green-600' : 'bg-red-600'}"></span>
                            ${details.status === 'ACTIVE' ? 'Actif' : 'Inactif'}
                        </span>
                    </div>
                    <div class="flex justify-between py-3 border-b border-layer-line">
                        <span class="text-sm text-muted-foreground">Description:</span>
                        <span class="text-sm text-foreground text-right">${details.description || '—'}</span>
                    </div>
                </div>
                `;
                break;
            case 'classe':
                detailsHtml = `
                <div class=" w-full mx-auto p-5 ">
                    <div class="flex justify-between items-center py-3 border-b border-layer-line">
                        <span class="text-sm text-muted-foreground w-50">Code:</span>
                        <span class="text-sm text-foreground">${details.code || '—'}</span>
                    </div>
                </div>
                `;
                break;
        }

        return `
            <div class="flex flex-col  min-h-[calc(100%-1rem)]">
                <div class="space-y-5 w-full max-w-lg mx-auto">
                    <div class="flex items-start gap-4 p-4 bg-muted rounded-xl">
                        <div class="size-11 rounded-xl ${cfg.bg} flex items-center justify-center shrink-0 ${cfg.color}">
                        ${ICONS[type] ?? ICONS.classe}
                    </div>
                    <div class="min-w-0 flex-1">
                        <h3 class="font-semibold text-layer-foreground text-base leading-snug truncate">${cfg.label} ${name}</h3>
                    </div>
                    <div class="flex gap-2">
                     <div class="flex flex-wrap gap-2">
                        ${type === 'cycle' ? `
                            <button type="button" class="btn-add-filiere inline-flex items-center gap-x-2 py-1.5 px-3 text-xs font-medium border rounded-lg bg-surface border-layer-line text-surface-foreground hover:bg-surface-hover focus:outline-none shadow-sm transition-all"
                                    data-cycle-id="${id}">
                                <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M12 5v14m-7-7h14" />
                                </svg>
                                Ajouter filière
                            </button>
                        ` : ''}
                        ${type === 'filiere' ? `
                            <button type="button" class="btn-add-niveau inline-flex items-center gap-x-2 py-1.5 px-3 text-xs font-medium border rounded-lg bg-surface border-layer-line text-surface-foreground hover:bg-surface-hover focus:outline-none shadow-sm transition-all"
                                    data-filiere-id="${id}">
                                <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M12 5v14m-7-7h14" />
                                </svg>
                                Ajouter niveau
                            </button>
                        ` : ''}
                        ${type === 'niveau' ? `
                            <button type="button" class="btn-add-specialite inline-flex items-center gap-x-2 py-1.5 px-3 text-xs font-medium border rounded-lg bg-surface  border-layer-line text-surface-foreground hover:bg-surface-hover focus:outline-none shadow-sm transition-all"
                                    data-niveau-id="${id}">
                                <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M12 5v14m-7-7h14" />
                                </svg>
                                Ajouter spécialité
                            </button>
                        ` : ''}
                        ${type === 'specialite' ? `
                            <button type="button" class="btn-add-classe inline-flex items-center gap-x-2 py-1.5 px-3 text-xs font-medium rounded-lg border bg-surface border-layer-line text-surface-foreground hover:bg-surface-hover focus:outline-none shadow-sm transition-all"
                                    data-specialite-id="${id}">
                                <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M12 5v14m-7-7h14" />
                                </svg>
                                Ajouter classe
                            </button>
                        ` : ''}
                    </div>


                    ${this._renderDropdown(type, id, details)}


                        
                    </div>
                </div>
                ${detailsHtml}
               
               
            </div>
        `;
    }
    // ══════════════════════════════════════════════════════════════════════════
    //  DROPDOWN UNIFIÉ — rendu + actions
    // ══════════════════════════════════════════════════════════════════════════

    _renderDropdown(type, id, details) {
        const hasStatus = ['cycle', 'filiere', 'specialite'].includes(type);
        const isActive = details?.status === 'ACTIVE';
        return `
            <div class="hs-dropdown relative inline-flex">
                <button type="button"
                    class="hs-dropdown-toggle size-8 p-1.5 flex items-center justify-center bg-surface border border-layer-line rounded-lg text-muted-foreground-2 hover:bg-surface-hover hover:text-layer-foreground transition-colors focus:outline-none"
                    aria-haspopup="menu" aria-expanded="false">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="12" cy="5" r="1"/><circle cx="12" cy="12" r="1"/><circle cx="12" cy="19" r="1"/>
                    </svg>
                </button>
                <div class="hs-dropdown-menu transition-[opacity,margin] duration-200 hs-dropdown-open:opacity-100 opacity-0 hidden min-w-36 bg-layer border border-card-line shadow-lg rounded-xl z-20 py-3 flex flex-col gap-1"
                     role="menu">
                    <button type="button"
                        class="btn-edit-entity px-3 py-1 flex items-center rounded-lg text-muted-foreground-2 hover:bg-muted hover:text-layer-foreground transition-colors gap-3"
                        data-entity-id="${id}" data-entity-type="${type}">
                        <span class="text-layer-foreground text-sm">Modifier</span>
                    </button>
                    <hr class="border-layer-line">
                    ${hasStatus ? `
                    <button type="button"
                        class="btn-toggle-status px-3 py-1 flex items-center text-layer-foreground hover:bg-muted transition-colors gap-3"
                        data-entity-id="${id}" data-entity-type="${type}" data-entity-status="${details?.status ?? ''}">
                        <span class="text-layer-foreground text-sm">${isActive ? 'Désactiver' : 'Activer'}</span>
                    </button>
                   
                    ` : ''}
                    <button type="button"
                        class="btn-delete-entity px-3 py-1 flex items-center transition-colors gap-3"
                        data-entity-id="${id}" data-entity-type="${type}">
                        <span class="text-sm text-red-500">Supprimer</span>
                    </button>
                </div>
            </div>
        `;
    }

    _handleEditEntity(type, id, details) {
        const modal = document.querySelector(`#hs-modal-edit-${type}`);
        if (!modal) { console.warn(`Modal #hs-modal-edit-${type} introuvable`); return; }
        const f = modal.querySelector(`form[data-form="update-${type}"]`);
        if (!f) return;
        f.dataset.editId = id;
        switch (type) {
            case 'cycle':
                f.querySelector('[name="name"]').value = details.name ?? '';
                f.querySelector('[name="code"]').value = details.code ?? '';
                f.querySelector('[name="durationYears"]').value = details.durationYears ?? '';
                f.querySelector('[name="description"]').value = details.description ?? '';
                break;
            case 'filiere':
                f.querySelector('[name="code"]').value = details.code ?? '';
                f.querySelector('[name="nom"]').value = details.nom ?? '';
                f.querySelector('[name="description"]').value = details.description ?? '';
                break;
            case 'niveau':
                f.querySelector('[name="ordre"]').value = details.ordre ?? '';
                break;
            case 'specialite':
                f.querySelector('[name="code"]').value = details.code ?? '';
                f.querySelector('[name="libelle"]').value = details.libelle ?? '';
                f.querySelector('[name="description"]').value = details.description ?? '';
                break;
            case 'classe':
                f.querySelector('[name="code"]').value = details.code ?? '';
                break;
        }
        window.HSOverlay?.open(modal);
    }

    async _handleDeleteEntity(type, id) {
        console.log(type);
        console.log(`id ${id} ${typeof id}`)
        if (!confirm('Supprimer cet élément ? Cette action est irréversible.')) return;
        try {
            switch (type) {
                case 'cycle': await this.cycleApi.deleteCycle(id); break;
                case 'filiere': await this.filiereApi.deleteFiliere(id); break;
                case 'niveau': await this.niveauApi.delete(id); break;
                case 'specialite': await this.specialiteApi.delete(id); break;
                case 'classe': await this.classeApi.delete(id); break;
            }
            GlobalEventNotifier.eventWellDone('Suppression effectuée avec succès');
            // Retirer le nœud de l'arbre
            document.querySelector(`[data-node-id="${id}"][data-node-type="${type}"]`)
                ?.closest('.hs-accordion')?.remove();
            // Vider le panneau
            const panel = document.querySelector(SELECTORS.descPanel);
            if (panel) panel.innerHTML = `
                <div class="flex flex-col items-center justify-center text-center h-full gap-4 py-16">
                    <p class="text-sm text-muted-foreground-2">Sélectionnez un élément dans l'arbre.</p>
                </div>`;
            this._activeSelectable = null;
            this._currentEntityType = null;
            this._currentEntityId = null;
            this._currentEntityDetails = null;
        } catch (error) { GlobalErrorHandler.handle(error); }
    }

    async _handleToggleStatus(type, id, details) {
        const newStatus = details?.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
        try {
            switch (type) {
                case 'cycle': await this.cycleApi.updateCycleStatus(id, newStatus); break;
                case 'filiere': await this.filiereApi.updateFiliereStatus(id, newStatus); break;
                case 'specialite': await this.specialiteApi.updateStatus(id, newStatus); break;
            }
            GlobalEventNotifier.eventWellDone('Statut mis à jour');
            // Rafraîchir le panneau
            if (this._activeSelectable) {
                const acc = this._activeSelectable.closest('.hs-accordion');
                if (acc) this._setFocus(this._activeSelectable, { id, type, name: acc.dataset.nodeName });
            }
        } catch (error) { GlobalErrorHandler.handle(error); }
    }

    _setLoading(btn, isLoading) {
        btn.querySelector('.tree-spinner')?.classList.toggle('hidden', !isLoading);
        btn.querySelector('.tree-chevron')?.classList.toggle('hidden', isLoading);
    }

    _hideChevron(btn) {
        btn.classList.add('invisible');
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  HANDLERS POUR AJOUT CONTEXTUEL
    // ══════════════════════════════════════════════════════════════════════════
    _handleAddFiliere(button) {
        const cycleId = button.dataset.cycleId;
        // Le modal utilise une liste déroulante, pas besoin de pré-remplir
        // Mais on pourrait sélectionner le cycle par défaut si on veut
        const modal = document.querySelector('#hs-modal-add-filiere');
        if (modal) {
            // Optionnel: pré-sélectionner le cycle dans la liste déroulante
            const select = modal.querySelector('#filiere-cycleId');
            if (select) {
                select.value = cycleId;
            }
            window.HSOverlay?.open(modal);
        }
    }

    _handleAddNiveau(button) {
        const filiereId = button.dataset.filiereId;
        const modal = document.querySelector('#hs-modal-add-niveau');
        if (modal) {
            const hiddenInput = modal.querySelector('#niveau-filiereId');
            if (hiddenInput) {
                hiddenInput.value = filiereId;
            }
            window.HSOverlay?.open(modal);
        }
    }

    _handleAddSpecialite(button) {
        const niveauId = button.dataset.niveauId;
        const modal = document.querySelector('#hs-modal-add-specialite');
        if (modal) {
            const hiddenInput = modal.querySelector('#specialite-niveauId');
            if (hiddenInput) {
                hiddenInput.value = niveauId;
            }
            window.HSOverlay?.open(modal);
        }
    }

    _handleAddClasse(button) {
        const specialiteId = button.dataset.specialiteId;
        const modal = document.querySelector('#hs-modal-add-classe');
        if (modal) {
            const hiddenInput = modal.querySelector('#classe-specialiteId');
            if (hiddenInput) {
                hiddenInput.value = specialiteId;
            }
            window.HSOverlay?.open(modal);
        }
    }
}

// ─── Initialisation ────────────────────────────────────────────────────────────
const cycleApi = new CycleApi();
const filiereApi = new FiliereApi();
const niveauApi = new NiveauApi();
const specialiteApi = new SpecialiteApi();
const classeApi = new ClasseApi();

const cycleUseCase = new CycleUseCase(cycleApi);
const filiereUseCase = new FiliereUseCase(filiereApi);
const niveauUseCase = new NiveauUseCase(niveauApi);
const specialiteUseCase = new SpecialiteUseCase(specialiteApi);
const classeUseCase = new ClasseUseCase(classeApi);

document.addEventListener('DOMContentLoaded', () => {
    new CycleController(
        cycleApi, filiereApi, niveauApi, specialiteApi, classeApi,
        cycleUseCase, filiereUseCase, niveauUseCase, specialiteUseCase, classeUseCase
    );
});
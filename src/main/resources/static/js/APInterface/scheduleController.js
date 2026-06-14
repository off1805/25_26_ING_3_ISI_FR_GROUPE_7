// @ts-check
/**
 * scheduleController.js — Contrôleur unifié emploi du temps
 * Supporte deux modes via window.scheduleConfig.readonly :
 *   - readonly: false (défaut) → mode édition complet (AP)
 *   - readonly: true          → mode visualisation (enseignant, lecture seule)
 *
 * Connectez ce fichier à EditSchedule.html (mode édition) et à
 * TeacherSchedule.html (mode lecture seule) pour une grille visuellement
 * identique.
 */

import {
    HOURS, DAY_COUNT, PALETTE, SUBJECTS, ICON_SVG,
    getMonday, fmtISO, fmtShort, generatePDF, isAreaOccupied, isWithinBounds
} from './editScheduleUtils.js';
import api from '../common/ClientHttp.js';
import { GlobalErrorHandler } from '../common/GlobalErrorHandler.js';

// ─── Helpers ───────────────────────────────────────────────────────────────

/**
 * @param {string} colorIdOrHex
 * @param {string} [fallbackId]
 * @returns {{ id: string, bg: string, border: string, text: string }}
 */
function resolveColor(colorIdOrHex, fallbackId = 'violet') {
    const isHex = typeof colorIdOrHex === 'string' && colorIdOrHex.startsWith('#') && colorIdOrHex.length === 7;
    if (isHex) return { id: colorIdOrHex, bg: `${colorIdOrHex}1f`, border: colorIdOrHex, text: colorIdOrHex };
    const found = PALETTE.find(p => p.id === colorIdOrHex);
    if (found) return found;
    return PALETTE.find(p => p.id === fallbackId) || PALETTE[0];
}

/** @param {string} inputId @returns {string} */
function getDatePickerValue(inputId) {
    const el = document.getElementById(inputId);
    if (!el) return '';
    return el.dataset.hsDatepickerValue || el.value || '';
}

/** @param {string} inputId @param {string} isoDateStr */
function setDatePickerValue(inputId, isoDateStr) {
    const el = document.getElementById(inputId);
    if (!el || !isoDateStr) return;
    const wrapper = el.closest('[data-hs-datepicker]');
    if (wrapper && window.HSDatepicker) {
        const instance = HSDatepicker.getInstance(wrapper);
        if (instance) { instance.setDate(isoDateStr); return; }
    }
    el.value = isoDateStr;
    el.dataset.hsDatepickerValue = isoDateStr;
}

// ─── Contrôleur principal ──────────────────────────────────────────────────

export class EditScheduleController {
    constructor() {
        /** @type {boolean} En mode lecture seule (enseignant) si true */
        this.readonly = (window.scheduleConfig?.readonly === true);

        this.EVENTS = [
            { id: 'e1', name: 'Pause', iconKey: 'pause', defaultColor: 'yellow' },
            { id: 'e2', name: 'Temps personnel', iconKey: 'perso', defaultColor: 'blue' },
        ];
        /** @type {Array<{id:number,name:string,code:string,defaultColor:string,teachers:Array}>} */
        this.SUBJECTS = [];
        /** @type {Record<string, string>} */
        this.selectedColors = {};
        this.weekOffset = 0;
        this.emploiId = null;
        this.slotHeight = 40;
        /** @type {HTMLElement[]} */
        this.undoStack = [];
        this.activeDragData = null;
        this.popoverKey = null;
        this._pendingDrop = null;
        this._editingBlock = null;
        this.rangeStart = null;
        this.rangeEnd = null;
        this.activeDays = null;
        /** @type {Array} Cache des séances pour la vue mobile */
        this.seancesCache = [];
        /** @type {Record<number, string>} Map classeId → nom de la classe */
        this.classeMap = {};

        this.EVENTS.forEach(ev => { this.selectedColors[ev.id] = ev.defaultColor; });
        this.init().catch(e => console.error('scheduleController init error', e));
    }

    // ── Initialisation ────────────────────────────────────────────────────

    async init() {
        const config = window.scheduleConfig || {};
        const params = new URLSearchParams(window.location.search);

        this.emploiId    = config.emploiId    || (params.get('id')        ? parseInt(params.get('id'))        : null);
        this.classId     = config.classId     || (params.get('classId')   ? parseInt(params.get('classId'))   : null);
        this.teacherId   = config.teacherId   || (params.get('teacherId') ? parseInt(params.get('teacherId')) : null);

        // student : readonly + classId sans teacherId → affiche l'emploi de sa classe
        // teacher : readonly + teacherId → filtre par enseignant côté JS
        this.viewMode = (this.readonly && this.classId && !this.teacherId) ? 'student' : 'teacher';

        // ── Log de vérification (visible dans DevTools → Console) ──────────
        console.group('[ScheduleController] Initialisation');
        console.log('Mode         :', this.readonly ? `readonly / ${this.viewMode}` : 'édition (AP)');
        console.log('emploiId     :', this.emploiId);
        console.log('classId      :', this.classId,  '←', this.viewMode === 'student' ? 'classe de l\'étudiant' : '(mode édition)');
        console.log('teacherId    :', this.teacherId);
        console.log('scheduleConfig reçu :', config);
        console.groupEnd();
        this.fixedStartDate = config.dateDebut || params.get('dateDebut');
        this.fixedEndDate   = config.dateFin   || params.get('dateFin');
        this.semaine        = config.semaine   || params.get('semaine');

        const label = document.getElementById('edit-class-label');
        if (label) label.textContent = config.className || '';

        if (this.fixedStartDate) {
            this.weekOffset = this._computeWeekOffsetFrom(this.fixedStartDate);
            this.rangeStart = new Date(this.fixedStartDate);
            this.rangeStart.setHours(0, 0, 0, 0);
        }
        if (this.fixedEndDate) {
            this.rangeEnd = new Date(this.fixedEndDate);
            this.rangeEnd.setHours(23, 59, 59, 999);
        }

        // view-edit toggle (EditSchedule.html uniquement)
        const viewEdit = document.getElementById('view-edit');
        if (viewEdit) { viewEdit.classList.remove('hidden'); viewEdit.classList.add('flex'); }

        if (!this.readonly) {
            // Mode édition : charger les matières, le panneau latéral, les modales
            if (this.classId) {
                await this._loadSubjectsForClass(this.classId);
            } else {
                this.SUBJECTS = SUBJECTS;
            }
            this.SUBJECTS.forEach(s => { this.selectedColors[`s${s.id}`] = s.defaultColor || 'blue'; });
            this.renderPanel();
            this._bindSubjectDropModal();
            this._bindSubjectSearch();
            window.__onDateStartChange = val => this.handleDatePickerChange(val);
            window.__onDateEndChange   = () => {};
        }

        this._bindControls();

        // ResizeObserver sur le conteneur de la grille :
        // détecte TOUT changement de taille (sidebar pliée/dépliée, resize fenêtre…)
        // window.resize ne suffit pas car le repli de la sidebar est un changement de layout interne.
        // requestAnimationFrame joue le rôle de debounce pendant l'animation de la sidebar.
        this._initResizeObserver();

        this.renderWeek();

        if (this.emploiId) {
            await this.loadExistingEmploi(this.emploiId).catch(e => console.error('Erreur chargement emploi', e));
        } else if (this.readonly && (this.teacherId || this.classId)) {
            // Déclenché pour l'étudiant (classId) ET l'enseignant (teacherId)
            await this._tryLoadForWeek();
        }
    }

    // ── Contrôles ─────────────────────────────────────────────────────────

    _bindControls() {
        if (!this.readonly) {
            // Boutons exclusifs au mode édition
            document.getElementById('btn-exit')?.addEventListener('click', () => this.exitEditMode());
            document.getElementById('btn-undo')?.addEventListener('click', () => this.undoLast());
            document.getElementById('btn-save')?.addEventListener('click', () => this.saveSchedule());
            document.getElementById('date-start')?.addEventListener('change', e => this.handleDatePickerChange(e.target.value));
            document.getElementById('btn-open-event-modal')?.addEventListener('click', () => this.openAddEventModal());
            document.getElementById('btn-confirm-event')?.addEventListener('click', () => this.confirmAddEvent());
            document.querySelectorAll('.btn-close-modal').forEach(btn =>
                btn.addEventListener('click', () => this.hideModal(btn.closest('.modal-container').id))
            );
            document.addEventListener('click', ev => {
                const pop = document.getElementById('color-popover');
                if (pop && !pop.classList.contains('hidden') && !pop.contains(ev.target)) {
                    pop.classList.add('hidden');
                    this.popoverKey = null;
                }
            });
        }

        // Navigation semaine — disponible dans les deux modes
        document.getElementById('prev-week')?.addEventListener('click', () => {
            this.weekOffset--;
            this.renderWeek();
            if (this.readonly) this._tryLoadForWeek();
        });
        document.getElementById('next-week')?.addEventListener('click', () => {
            this.weekOffset++;
            this.renderWeek();
            if (this.readonly) this._tryLoadForWeek();
        });
        document.getElementById('today-btn')?.addEventListener('click', () => {
            this.weekOffset = 0;
            this.renderWeek();
            if (this.readonly) this._tryLoadForWeek();
        });

        // Export PDF — disponible dans les deux modes
        document.getElementById('btn-export-pdf')?.addEventListener('click', () => this.exportPDF());
    }

    _bindSubjectSearch() {
        document.getElementById('subject-search')?.addEventListener('input', () => this._renderSubjects());
    }

    // ── Chargement matières (mode édition) ────────────────────────────────

    async _loadSubjectsForClass(classId) {
        const classe = await api.get(`/api/classes/${classId}`);
        const specialiteId = classe?.specialiteId;
        if (!specialiteId) { this.SUBJECTS = []; return; }

        const [offrePage, teachersPage] = await Promise.all([
            api.get(`/api/offre-ue/active?specialiteId=${encodeURIComponent(specialiteId)}&size=200`),
            api.get('/api/users?role=TEACHER&size=200'),
        ]);

        const teacherMap = new Map();
        (teachersPage?.content || []).forEach(t => {
            if (!t.profile?.id) return;
            const name     = `${t.profile.prenom || ''} ${t.profile.nom || ''}`.trim() || t.email || 'Enseignant';
            const initials = `${(t.profile.prenom?.[0] || '').toUpperCase()}${(t.profile.nom?.[0] || '').toUpperCase()}` || 'EN';
            teacherMap.set(t.profile.id, { id: t.profile.id, name, initials });
        });

        // Seuls les enseignants affectés à cette UE pour CETTE classe doivent apparaître.
        this.SUBJECTS = (offrePage?.content || []).map((/** @type {any} */ offre) => ({
            id:           offre.ueId,
            name:         offre.libelle,
            code:         offre.code,
            defaultColor: (typeof offre.couleur === 'string' && offre.couleur.startsWith('#') && offre.couleur.length === 7) ? offre.couleur : '#3b82f6',
            teachers:     (offre.enseignantAssignments || [])
                .filter((/** @type {any} */ a) => a.classeId === classId)
                .map((/** @type {any} */ a) => teacherMap.get(a.enseignantId))
                .filter(Boolean),
        }));
    }

    _toHex(colorIdOrHex) {
        if (typeof colorIdOrHex === 'string' && colorIdOrHex.startsWith('#')) return colorIdOrHex;
        const p = PALETTE.find(p => p.id === colorIdOrHex);
        return p ? p.border : '#3b82f6';
    }

    // ── Modale drop matière (mode édition) ────────────────────────────────

    _bindSubjectDropModal() {
        document.getElementById('btn-confirm-subject-drop')?.addEventListener('click', () => this._confirmSubjectDrop());
        document.getElementById('btn-cancel-subject-drop')?.addEventListener('click',  () => this._closeDropModal());
        document.getElementById('btn-close-drop-modal')?.addEventListener('click',    () => this._closeDropModal());
        document.getElementById('drop-color-input')?.addEventListener('input', e => {
            if (this._pendingDrop) this._pendingDrop.colorId = e.target.value;
        });
    }

    // ── Helpers modal ─────────────────────────────────────────────────────

    /**
     * Peuple le select enseignant et réinitialise le composant Preline advanced select.
     * @param {Array}       teachers    [{id, name, initials}]
     * @param {number|null} selectedId  ID à pré-sélectionner (mode édition)
     */
    _populateTeacherSelect(teachers, selectedId = null) {
        const sel = document.getElementById('drop-teacher-select');
        if (!sel) return;
        sel.innerHTML = teachers.length
            ? teachers.map(t => `<option value="${t.id}" ${t.id === selectedId ? 'selected' : ''}>${t.name}</option>`).join('')
            : `<option value="">Aucun enseignant assigné</option>`;
        // Réinitialise le composant Preline pour refléter les nouvelles options
        if (window.HSSelect) {
            const inst = HSSelect.getInstance(sel);
            if (inst) inst.destroy();
            new HSSelect(sel);
        } else if (window.HSStaticMethods) {
            HSStaticMethods.autoInit();
        }
    }

    _resetModalTexts() {
        const t = document.getElementById('drop-modal-title');
        const s = document.getElementById('drop-modal-subtitle');
        const b = document.getElementById('btn-confirm-subject-drop');
        if (t) t.textContent = 'Placer la matière';
        if (s) s.textContent = "Choisissez l'enseignant et la couleur du bloc";
        if (b) b.textContent = 'Placer le bloc';
    }

    /** Ferme le modal et remet le contexte à zéro. */
    _closeDropModal() {
        this._pendingDrop  = null;
        this._editingBlock = null;
        this._resetModalTexts();
        const el = document.getElementById('hs-modal-subject-drop');
        if (window.HSOverlay && el) HSOverlay.close(el);
    }

    /** Mode CRÉATION — ouvert après un drop de matière sur la grille. */
    _openSubjectDropModal(hourIndex, dayIndex, subject) {
        this._pendingDrop  = { hourIndex, dayIndex, subject };
        this._editingBlock = null;
        this._resetModalTexts();

        const hexColor = this._toHex(this.selectedColors[`s${subject.id}`] || subject.defaultColor);
        this._populateTeacherSelect(subject.teachers, null);

        const colorInput = document.getElementById('drop-color-input');
        if (colorInput) { colorInput.value = hexColor; this._pendingDrop.colorId = hexColor; }

        const el = document.getElementById('hs-modal-subject-drop');
        if (window.HSOverlay && el) HSOverlay.open(el);
    }

    /** Mode ÉDITION — ouvert par un clic simple sur un bloc existant. */
    _openBlockEditModal(blockEl) {
        this._editingBlock = blockEl;
        this._pendingDrop  = null;

        const subjectId = parseInt(blockEl.dataset.subjectId);
        const subject   = this.SUBJECTS.find(s => s.id === subjectId);

        const t = document.getElementById('drop-modal-title');
        const s = document.getElementById('drop-modal-subtitle');
        const b = document.getElementById('btn-confirm-subject-drop');
        if (t) t.textContent = 'Modifier la séance';
        if (s) s.textContent = "Modifiez l'enseignant et la couleur";
        if (b) b.textContent = 'Mettre à jour';

        const currentTeacherId = parseInt(blockEl.dataset.teacherId) || null;
        this._populateTeacherSelect(subject?.teachers || [], currentTeacherId);

        const colorInput = document.getElementById('drop-color-input');
        if (colorInput) {
            const hex = (blockEl.dataset.colorId || '').startsWith('#')
                ? blockEl.dataset.colorId
                : this._toHex(blockEl.dataset.colorId || 'violet');
            colorInput.value = hex;
        }
        const el = document.getElementById('hs-modal-subject-drop');
        if (window.HSOverlay && el) HSOverlay.open(el);
    }

    _confirmSubjectDrop() {
        const sel       = document.getElementById('drop-teacher-select');
        const teacherId = sel?.value ? parseInt(sel.value) : null;
        const colorId   = document.getElementById('drop-color-input')?.value || '#3b82f6';

        if (this._editingBlock) {
            // Mode ÉDITION
            this._updateBlock(this._editingBlock, teacherId, colorId);
            this._closeDropModal();
            return;
        }

        // Mode CRÉATION
        if (!this._pendingDrop) return;
        const { hourIndex, dayIndex, subject } = this._pendingDrop;
        const teacher = subject.teachers.find(t => t.id === teacherId)
                     || subject.teachers[0]
                     || { id: null, name: 'Enseignant', initials: 'EN' };

        this.selectedColors[`s${subject.id}`] = colorId;
        this._refreshSubjectCard(subject.id, colorId);
        const dropData = {
            type: 'teacher', subjectId: subject.id, subjectName: subject.name, subjectCode: subject.code,
            teacherId: teacher.id, teacherName: teacher.name, teacherInitials: teacher.initials,
            itemKey: `s${subject.id}`, fromBlock: false,
        };
        this._closeDropModal();
        this.undoStack.push(this.placeBlock(hourIndex, dayIndex, dropData, colorId));
    }

    /**
     * Met à jour visuellement un bloc sans reconstruire son DOM.
     * Retrouve les éléments grâce aux classes .block-icon / .teacher-initials / .block-secondary.
     */
    _updateBlock(blockEl, teacherId, colorId) {
        const subjectId    = parseInt(blockEl.dataset.subjectId);
        const subject      = this.SUBJECTS.find(s => s.id === subjectId);
        const teacher      = subject?.teachers.find(t => t.id === teacherId)
                          || subject?.teachers[0]
                          || { id: null, name: '', initials: 'UE' };
        const paletteColor = resolveColor(colorId);

        blockEl.style.background      = paletteColor.bg;
        blockEl.style.border          = `1.5px solid ${paletteColor.border}`;
        blockEl.style.borderLeftWidth = '4px';
        blockEl.style.color           = paletteColor.text;
        blockEl.dataset.colorId       = colorId;
        if (teacher.id != null) blockEl.dataset.teacherId = String(teacher.id);

        const iconEl     = blockEl.querySelector('.block-icon');
        const initialsEl = blockEl.querySelector('.teacher-initials');
        const secEl      = blockEl.querySelector('.block-secondary');
        if (iconEl)     iconEl.style.color    = paletteColor.border;
        if (initialsEl) initialsEl.textContent = teacher.initials || 'UE';
        if (secEl) {
            secEl.textContent = teacher.name || '';
            secEl.classList.toggle('hidden', !teacher.name);
        }

        if (subjectId) {
            this.selectedColors[`s${subjectId}`] = colorId;
            this._refreshSubjectCard(subjectId, colorId);
        }
        this.showToast('Séance mise à jour ✓');
    }

    _refreshSubjectCard(subjectId, colorId) {
        const paletteColor = resolveColor(colorId);
        const swatch = document.getElementById(`swatch-s${subjectId}`);
        if (swatch) { swatch.style.background = paletteColor.bg; swatch.style.borderColor = paletteColor.border; }
    }

    exitEditMode() { window.location.href = '/ap/schedule'; }

    // ── Modales événement (mode édition) ──────────────────────────────────

    openAddEventModal() {
        const modal = document.getElementById('modal-event');
        if (!modal) return;
        document.getElementById('new-event-name').value = '';
        this.showModal('modal-event');
        setTimeout(() => document.getElementById('new-event-name').focus(), 60);
    }

    confirmAddEvent() {
        const nameInput = document.getElementById('new-event-name');
        if (!nameInput) return;
        const name = nameInput.value.trim();
        if (!name) return;
        const id = 'e' + Date.now();
        this.EVENTS.push({ id, name, iconKey: 'event', defaultColor: 'violet' });
        this.selectedColors[id] = 'violet';
        this.hideModal('modal-event');
        this.renderPanel();
        this.showToast('Évènement ajouté');
    }

    showModal(modalId) {
        const el = document.getElementById(modalId);
        if (!el) return;
        el.classList.remove('hidden');
        el.classList.add('flex');
    }

    hideModal(modalId) {
        const el = document.getElementById(modalId);
        if (!el) return;
        el.classList.add('hidden');
        el.classList.remove('flex');
    }

    // ── Sélecteur de couleur (mode édition) ───────────────────────────────

    openColorPicker(itemKey, anchorEl, event) {
        event.stopPropagation();
        const pop = document.getElementById('color-popover');
        if (this.popoverKey === itemKey && !pop.classList.contains('hidden')) {
            pop.classList.add('hidden'); this.popoverKey = null; return;
        }
        this.popoverKey = itemKey;
        document.getElementById('color-popover-grid').innerHTML = PALETTE.map(color => `
            <button class="color-btn size-8 rounded-xl border-2 hover:scale-110 transition-all ${this.selectedColors[itemKey] === color.id ? 'ring-2 ring-offset-1 ring-primary shadow-sm' : ''}"
                    data-color="${color.id}" data-key="${itemKey}" title="${color.id}"
                    style="background:${color.bg};border-color:${color.border}"></button>`).join('');
        document.querySelectorAll('.color-btn').forEach(btn =>
            btn.addEventListener('click', ev => this.setColor(ev.currentTarget.dataset.key, ev.currentTarget.dataset.color, ev.currentTarget))
        );
        const rect = anchorEl.getBoundingClientRect();
        pop.style.top  = `${rect.bottom + 8}px`;
        pop.style.left = `${Math.min(rect.left - 60, window.innerWidth - 172)}px`;
        pop.classList.remove('hidden');
    }

    setColor(itemKey, colorId, button) {
        this.selectedColors[itemKey] = colorId;
        button.closest('#color-popover-grid').querySelectorAll('button')
            .forEach(b => b.classList.remove('ring-2', 'ring-offset-1', 'ring-primary', 'shadow-sm'));
        button.classList.add('ring-2', 'ring-offset-1', 'ring-primary', 'shadow-sm');
        const paletteColor = PALETTE.find(p => p.id === colorId);
        const swatch = document.getElementById(`swatch-${itemKey}`);
        if (swatch && paletteColor) { swatch.style.background = paletteColor.bg; swatch.style.borderColor = paletteColor.border; }
    }

    // ── Panneau latéral (mode édition) ────────────────────────────────────

    renderPanel() {
        this._renderSubjects();
        this._renderEvents();
    }

    _getScheduledSubjectKeySet() {
        const set = new Set();
        document.querySelectorAll('.schedule-block[data-item-key]').forEach(b => {
            const k = b.dataset.itemKey || '';
            if (k.startsWith('s')) set.add(k);
        });
        return set;
    }

    _matchesSubjectQuery(subject, query) {
        if (!query) return true;
        const q = query.toLowerCase();
        return String(subject?.name || '').toLowerCase().includes(q) ||
               String(subject?.code || '').toLowerCase().includes(q);
    }

    _renderSubjectList(containerId, subjects, scheduledKeys) {
        const el = document.getElementById(containerId);
        if (!el) return;
        if (!subjects.length) {
            el.innerHTML = `<div class="px-3 py-3 text-sm text-muted-foreground-2">Aucune matière.</div>`;
            return;
        }
        el.innerHTML = subjects.map(subject => {
            const paletteColor = resolveColor(this.selectedColors[`s${subject.id}`] || subject.defaultColor);
            const isScheduled  = scheduledKeys?.has(`s${subject.id}`);
            return `
            <a class="subject-card group flex flex-col bg-layer rounded-xl hover:bg-layer-hover px-2 focus:outline-hidden focus:shadow-md transition cursor-grab active:cursor-grabbing"
               draggable="true" data-type="subject" data-subject-id="${subject.id}"
               data-subject-name="${subject.name}" data-subject-code="${subject.code}"
               data-item-key="s${subject.id}" href="#">
                <div class="py-2">
                    <div class="flex gap-x-3">
                        <div id="swatch-s${subject.id}" class="mt-0.5 relative shrink-0 size-8 rounded-lg flex items-center justify-center"
                             style="background:${paletteColor.bg};color:${paletteColor.border}">
                            ${ICON_SVG.books}
                            <span class="inline-flex absolute bottom-1 translate-y-1/2 right-1 translate-x-1/2 items-center size-2 rounded-full ${isScheduled ? 'bg-emerald-500' : 'bg-muted'}"></span>
                        </div>
                        <div class="grow min-w-0">
                            <h3 class="group-hover:text-primary font-semibold text-foreground text-[13px] leading-tight truncate">${subject.name}</h3>
                            <p class="text-xs text-muted-foreground-1 mt-0.5 truncate">${subject.code}</p>
                        </div>
                    </div>
                </div>
            </a>`;
        }).join('');
        el.querySelectorAll('.subject-card').forEach(el => {
            el.addEventListener('dragstart', ev => this.onDragStart(ev, el));
            el.addEventListener('dragend',   () => this.onDragEnd(el));
        });
    }

    _renderSubjects() {
        const query         = (document.getElementById('subject-search')?.value || '').trim();
        const scheduledKeys = this._getScheduledSubjectKeySet();
        const all           = this.SUBJECTS.filter(s => this._matchesSubjectQuery(s, query));
        const scheduled     = all.filter(s =>  scheduledKeys.has(`s${s.id}`));
        const unscheduled   = all.filter(s => !scheduledKeys.has(`s${s.id}`));
        this._renderSubjectList('subject-list-all',          all,         scheduledKeys);
        this._renderSubjectList('subject-list-unscheduled',  unscheduled, scheduledKeys);
        this._renderSubjectList('subject-list-scheduled',    scheduled,   scheduledKeys);
    }

    _renderEvents() {
        const eventListEl = document.getElementById('event-list');
        if (!eventListEl) return;
        eventListEl.innerHTML = this.EVENTS.map(event => {
            const paletteColor = resolveColor(this.selectedColors[event.id] || event.defaultColor);
            return `
            <div draggable="true" data-type="event" data-event-name="${event.name}"
                 data-item-key="${event.id}" data-icon-key="${event.iconKey}"
                 class="event-item flex items-center gap-2 px-3 py-2 bg-layer text-layer-foreground rounded-xl hover:bg-layer-hover transition-all cursor-grab active:cursor-grabbing text-[12px] font-bold">
                <div class="size-8 rounded-lg flex items-center justify-center shrink-0" style="background:${paletteColor.bg};color:${paletteColor.border}">
                    ${ICON_SVG[event.iconKey] || ICON_SVG.event}
                </div>
                <span class="truncate">${event.name}</span>
            </div>`;
        }).join('');
        eventListEl.querySelectorAll('.event-item').forEach(el => {
            el.addEventListener('dragstart', ev => this.onDragStart(ev, el));
            el.addEventListener('dragend',   () => this.onDragEnd(el));
        });
    }

    // ── Rendu de la grille ────────────────────────────────────────────────

    renderWeek() {
        const monday = getMonday(this.weekOffset);

        // La grille commence au premier jour de la plage choisie (pas forcément lundi).
        // Si rangeStart est un mercredi, la colonne 0 = mercredi.
        const gridStart = this.rangeStart ? new Date(this.rangeStart) : new Date(monday);
        gridStart.setHours(0, 0, 0, 0);
        this.gridStart = gridStart; // référence utilisée par _placeSeanceFromData

        const weekDays = Array.from({ length: DAY_COUNT }, (_, d) => {
            const day = new Date(gridStart); day.setDate(gridStart.getDate() + d); return day;
        });

        // Colonnes actives = dans la plage ; hors plage = grisées
        this.activeDays = weekDays.map(day => {
            if (!this.rangeStart || !this.rangeEnd) return true;
            const d = new Date(day); d.setHours(12, 0, 0, 0);
            const s = new Date(this.rangeStart); s.setHours(0, 0, 0, 0);
            const e = new Date(this.rangeEnd);   e.setHours(23, 59, 59, 999);
            return d >= s && d <= e;
        });

        // date-start = gridStart (= rangeStart ou lundi)
        setDatePickerValue('date-start', this._formatDateLocal(weekDays[0]));
        setDatePickerValue('date-end',   this._formatDateLocal(weekDays[DAY_COUNT - 1]));

        const weekLabel = document.getElementById('week-label');
        if (weekLabel) weekLabel.textContent = `${fmtShort(weekDays[0])} – ${fmtShort(weekDays[DAY_COUNT - 1])}`;

        const headersEl = document.getElementById('day-headers');
        if (!headersEl) return;
        headersEl.style.gridTemplateColumns = `${this.timeColumnWidth}px repeat(${DAY_COUNT},${this.dayColumnWidth()})`;
        headersEl.innerHTML = `<div class="py-2 text-xs text-center text-muted-foreground-2 font-bold border-r-2 border-card-line sticky left-0 bg-card z-30">Horaires</div>`;
        weekDays.forEach((dayDate, di) => {
            const isActive = this.activeDays[di];
            const dayName  = dayDate.toLocaleDateString('fr-FR', { weekday: 'short' }).slice(0, 3).toUpperCase();
            const isToday  = dayDate.toDateString() === new Date().toDateString();
            headersEl.innerHTML += `
                <div class="cell py-1.5 text-center border-l border-card-line ${isActive ? 'bg-card' : 'bg-muted/50'}">
                    <p class="text-[10px] font-bold ${isActive ? 'text-muted-foreground-2' : 'text-muted-foreground-2/40'}">${dayName}</p>
                    <p class="text-sm font-extrabold ${isToday && isActive ? 'text-primary' : (isActive ? 'text-layer-foreground' : 'text-muted-foreground-2/40')}">${dayDate.getDate()}</p>
                </div>`;
        });
        this.buildGrid();
    }

    handleDatePickerChange(isoValue) {
        if (!isoValue) return;
        this.weekOffset = Math.round((new Date(isoValue) - getMonday(0)) / (7 * 86400000));
        this.renderWeek();
    }

    handleDateChange(e) { this.handleDatePickerChange(e?.target?.value || e); }

    recalcSlotHeight() {
        const firstSlot = document.getElementById('schedule-body')?.children[0];
        if (firstSlot) this.slotHeight = firstSlot.getBoundingClientRect().height;
    }

    get timeColumnWidth() { return 72; }
    dayColumnWidth() { return '1fr'; }

    buildGrid() {
        const gridBody = document.getElementById('schedule-body');
        if (!gridBody) return;
        gridBody.style.gridTemplateColumns = `${this.timeColumnWidth}px repeat(${DAY_COUNT},${this.dayColumnWidth()})`;
        gridBody.style.gridTemplateRows    = `repeat(${HOURS.length}, 1fr)`;

        let html = '';
        HOURS.forEach((hour, hi) => {
            html += `<div style="grid-column:1;grid-row:${hi + 1}"
                          class="time-cell cell flex items-center justify-center border-b border-r-2 border-card-line bg-card sticky left-0 z-20">
                         <span class="text-[11px] font-mono font-semibold text-muted-foreground-2 whitespace-nowrap">${hour}h–${hour + 1}h</span>
                     </div>`;
            for (let di = 0; di < DAY_COUNT; di++) {
                const isActive = this.activeDays ? this.activeDays[di] : true;
                const disabled = isActive ? '' : 'bg-muted/40 opacity-40 cursor-not-allowed';
                html += `<div style="grid-column:${di + 2};grid-row:${hi + 1}"
                              class="cell slot border-b border-card-line border-l bg-card/80 transition-colors ${disabled}"
                              data-hour-index="${hi}" data-day-index="${di}"
                              ${isActive ? '' : 'data-disabled="true"'}></div>`;
            }
        });
        gridBody.innerHTML = html;

        // Drag-and-drop uniquement en mode édition
        if (!this.readonly) {
            gridBody.querySelectorAll('.slot').forEach(slot => {
                slot.addEventListener('dragover',  ev => this.onSlotDragOver(ev, slot));
                slot.addEventListener('dragleave', () => this.onSlotDragLeave(slot));
                slot.addEventListener('drop',      ev => this.onSlotDrop(ev, slot));
            });
        }
        this.undoStack = [];
    }

    _getSlotElement(hourIndex, dayIndex) {
        return document.querySelector(`.slot[data-hour-index="${hourIndex}"][data-day-index="${dayIndex}"]`);
    }

    _syncBlockToSlot(blockEl) {
        const hi   = parseInt(blockEl.dataset.hourIndex);
        const di   = parseInt(blockEl.dataset.dayIndex);
        const rs   = parseInt(blockEl.dataset.rs || '1');
        const slot = this._getSlotElement(hi, di);
        if (slot) {
            blockEl.style.left   = `${slot.offsetLeft + 1}px`;
            blockEl.style.top    = `${slot.offsetTop  + 1}px`;
            blockEl.style.width  = `${slot.offsetWidth  - 2}px`;
            blockEl.style.height = `${(slot.offsetHeight * rs) - 2}px`;
        }
    }

    // ── Drag & Drop (mode édition uniquement) ─────────────────────────────

    onDragStart(ev, el) {
        this.activeDragData = {
            type: el.dataset.type, subjectId: el.dataset.subjectId ? parseInt(el.dataset.subjectId) : null,
            subjectName: el.dataset.subjectName || '', subjectCode: el.dataset.subjectCode || '',
            teacherName: el.dataset.teacherName || '', eventName: el.dataset.eventName || '',
            itemKey: el.dataset.itemKey, iconKey: el.dataset.iconKey || '', fromBlock: false, rowSpan: 1,
        };
        ev.dataTransfer.effectAllowed = 'copy';
        el.style.opacity = '.4';
    }

    onDragEnd(el) { el.style.opacity = ''; this._clearDragHighlights(); }

    onSlotDragOver(ev, slot) {
        ev.preventDefault();
        if (!this.activeDragData) return;
        const h = parseInt(slot.dataset.hourIndex);
        const d = parseInt(slot.dataset.dayIndex);
        const rs = this.activeDragData.rowSpan || 1;
        const blockEl = this.activeDragData.fromBlock ? this.activeDragData.blockEl : null;
        const isOk = isWithinBounds(h, d, rs, 1) && !isAreaOccupied(h, d, rs, 1, blockEl) && this._isRangeActive(h, d, rs);
        this._clearDragHighlights();
        for (let i = 0; i < rs; i++) {
            const s = this._getSlotElement(h + i, d);
            if (s) s.classList.add('drag-highlight', isOk ? '!bg-primary/20' : '!bg-red-500/20');
        }
    }

    onSlotDragLeave(slot) {
        slot.classList.remove('drag-highlight', '!bg-primary/20', '!bg-red-500/20');
    }

    _isRangeActive(h, d, rs) { return this.activeDays ? this.activeDays[d] : true; }

    _clearDragHighlights() {
        document.querySelectorAll('.drag-highlight').forEach(el =>
            el.classList.remove('drag-highlight', '!bg-primary/20', '!bg-red-500/20')
        );
    }

    onSlotDrop(ev, slot) {
        ev.preventDefault();
        this._clearDragHighlights();
        if (!this.activeDragData) return;
        const hi = parseInt(slot.dataset.hourIndex);
        const di = parseInt(slot.dataset.dayIndex);
        const rs = this.activeDragData.rowSpan || 1;
        const blockEl = this.activeDragData.fromBlock ? this.activeDragData.blockEl : null;
        if (!isWithinBounds(hi, di, rs, 1) || isAreaOccupied(hi, di, rs, 1, blockEl) || !this._isRangeActive(hi, di, rs)) {
            this.showToast('Emplacement invalide ou hors période');
            this.activeDragData = null; return;
        }
        if (this.activeDragData.fromBlock) {
            blockEl.dataset.hourIndex = hi; blockEl.dataset.dayIndex = di;
            this._syncBlockToSlot(blockEl); this.activeDragData = null; return;
        }
        if (this.activeDragData.type === 'subject') {
            const subject = this.SUBJECTS.find(s => s.id === this.activeDragData.subjectId);
            if (subject) this._openSubjectDropModal(hi, di, subject);
            this.activeDragData = null; return;
        }
        this.undoStack.push(this.placeBlock(hi, di, this.activeDragData));
        this.activeDragData = null;
    }

    // ── Placement de blocs ────────────────────────────────────────────────

    /**
     * Crée et positionne un bloc sur la grille.
     * En mode readonly : pas de bouton supprimer, pas de redimensionnement, pas de drag.
     */
    placeBlock(hourIndex, dayIndex, blockData, forcedColorId = null) {
        const colorId         = forcedColorId || this.selectedColors[blockData.itemKey] || 'violet';
        const paletteColor    = resolveColor(colorId);
        const primaryLabel   = blockData.type === 'teacher' ? blockData.subjectName : blockData.eventName;
        // En édition : nom enseignant  |  étudiant readonly : salle  |  enseignant readonly : classe
        const secondaryLabel = blockData.type === 'teacher'
            ? (this.readonly
                ? (this.viewMode === 'student'
                    ? (blockData.salle || null)
                    : (blockData.className || blockData.teacherName || null))
                : blockData.teacherName)
            : null;
        const teacherInitials = blockData.teacherInitials ||
            (blockData.teacherName?.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase()) || 'UE';

        const blockEl = document.createElement('div');
        blockEl.className = `group relative schedule-block shadow-sm`;
        blockEl.dataset.hourIndex = hourIndex;
        blockEl.dataset.dayIndex  = dayIndex;
        blockEl.dataset.rs        = '1';
        if (blockData?.itemKey)    blockEl.dataset.itemKey   = blockData.itemKey;
        if (blockData?.subjectId  != null) blockEl.dataset.subjectId  = String(blockData.subjectId);
        if (blockData?.teacherId  != null) blockEl.dataset.teacherId  = String(blockData.teacherId);
        if (blockData?.type)       blockEl.dataset.blockType = blockData.type;
        if (colorId)               blockEl.dataset.colorId   = colorId;
        if (blockData?.iconKey)    blockEl.dataset.iconKey   = blockData.iconKey;
        if (blockData?.eventName)  blockEl.dataset.eventName = blockData.eventName;

        Object.assign(blockEl.style, {
            position:        'absolute',
            background:      paletteColor.bg,
            border:          `1.5px solid ${paletteColor.border}`,
            borderLeftWidth: '4px',
            color:           paletteColor.text,
            borderRadius:    '12px',
            padding:         '8px',
            pointerEvents:   'all',
            zIndex:          '10',
            cursor:          this.readonly ? 'default' : 'grab',
            userSelect:      'none',
        });

        this._syncBlockToSlot(blockEl);

        // Contenu commun (icône + labels) — classes .block-icon / .teacher-initials / .block-secondary
        // permettent à _updateBlock() de retrouver les éléments sans refaire le DOM complet.
        const innerContent = `
            <div class="flex items-start gap-2 h-full w-full overflow-hidden">
                <div class="block-icon size-7 rounded-lg shrink-0 flex items-center justify-center bg-white/50" style="color:${paletteColor.border}">
                    ${blockData.type === 'event'
                        ? (ICON_SVG[blockData.iconKey] || ICON_SVG.event)
                        : `<span class="teacher-initials text-[10px] font-bold">${teacherInitials}</span>`}
                </div>
                <div class="flex-1 min-w-0 px-1" style="z-index:10">
                    <p class="font-bold text-[13px] leading-tight truncate">${primaryLabel}</p>
                    ${secondaryLabel ? `<p class="block-secondary text-[11px] opacity-80 truncate font-medium mt-0.5">${secondaryLabel}</p>` : '<p class="block-secondary text-[11px] opacity-80 truncate font-medium mt-0.5 hidden"></p>'}
                </div>
            </div>`;

        if (this.readonly) {
            // Mode lecture seule : pas d'éléments interactifs
            blockEl.innerHTML = innerContent;
        } else {
            // Mode édition :
            //   • Clic simple  → ouvre le modal de configuration (enseignant + couleur)
            //   • Double-clic  → retire la séance de la grille
            //   • Poignées S/E → redimensionnement
            blockEl.innerHTML = `
                ${innerContent}
                <div data-r="s" class="absolute bottom-0 left-0 right-0 h-2 cursor-s-resize hover:bg-black/5 rounded-b-xl z-20"></div>
                <div data-r="e" class="absolute top-0 bottom-0 right-0 w-2 cursor-e-resize hover:bg-black/5 rounded-r-xl z-20"></div>`;

            let _clickTimer = null;
            blockEl.addEventListener('click', ev => {
                if (ev.target.closest('[data-r]')) return;
                clearTimeout(_clickTimer);
                _clickTimer = setTimeout(() => {
                    _clickTimer = null;
                    if (blockEl.dataset.blockType === 'teacher') this._openBlockEditModal(blockEl);
                }, 210);
            });
            blockEl.addEventListener('dblclick', ev => {
                if (ev.target.closest('[data-r]')) return;
                clearTimeout(_clickTimer);
                _clickTimer = null;
                blockEl.remove();
                this.undoStack = this.undoStack.filter(b => b !== blockEl);
                this._renderSubjects();
                this.showToast('Séance retirée du planning');
            });

            blockEl.setAttribute('draggable', 'true');
            blockEl.addEventListener('dragstart', ev => {
                if (ev.target.dataset.r) { ev.preventDefault(); return; }
                this.activeDragData = { fromBlock: true, blockEl, rowSpan: parseInt(blockEl.dataset.rs || '1') };
                ev.dataTransfer.effectAllowed = 'move';
                setTimeout(() => blockEl.style.opacity = '.35', 0);
            });
            blockEl.addEventListener('dragend', () => this.onDragEnd(blockEl));
            this._bindResizers(blockEl, blockData, paletteColor, primaryLabel, secondaryLabel, teacherInitials);
        }

        document.getElementById('blocks-layer').appendChild(blockEl);
        if (!this.readonly) this._renderSubjects();
        return blockEl;
    }

    _makeSiblingBlock(originalBlock, siblingDayIndex, blockData, paletteColor, primaryLabel, secondaryLabel, teacherInitials) {
        const sib = document.createElement('div');
        sib.className = 'group relative schedule-block shadow-sm';
        ['hourIndex','dayIndex','rs','itemKey','subjectId','teacherId','blockType','colorId','iconKey','eventName'].forEach(k => {
            if (originalBlock.dataset[k]) sib.dataset[k] = originalBlock.dataset[k];
        });
        sib.dataset.dayIndex  = siblingDayIndex;
        sib.dataset.isSibling = 'true';
        sib.style.cssText     = originalBlock.style.cssText;
        sib.style.width       = originalBlock.style.width;
        this._syncBlockToSlot(sib);
        sib.innerHTML = `
            <div class="flex items-start gap-2 h-full w-full overflow-hidden">
                <div class="size-7 rounded-lg shrink-0 flex items-center justify-center bg-white/50" style="color:${paletteColor.border}">
                    ${blockData.type === 'event' ? (ICON_SVG[blockData.iconKey] || ICON_SVG.event) : `<span class="text-[10px] font-bold">${teacherInitials}</span>`}
                </div>
                <div class="flex-1 min-w-0 px-1" style="z-index:10">
                    <p class="font-bold text-[13px] leading-tight truncate">${primaryLabel}</p>
                    ${secondaryLabel ? `<p class="text-[11px] opacity-80 truncate font-medium mt-0.5">${secondaryLabel}</p>` : ''}
                </div>
            </div>
            <div class="absolute top-1.5 right-1.5 opacity-0 group-hover:opacity-100 transition-opacity" style="z-index:30">
                <button type="button" class="btn-delete-block size-6 flex items-center justify-center rounded-md bg-white/90 hover:bg-red-50 hover:text-red-500 text-foreground shadow-sm transition-colors">
                    <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2M10 11v6M14 11v6"/></svg>
                </button>
            </div>
            <div data-r="s" class="absolute bottom-0 left-0 right-0 h-2 cursor-s-resize hover:bg-black/5 rounded-b-xl z-20"></div>
            <div data-r="e" class="absolute top-0 bottom-0 right-0 w-2 cursor-e-resize hover:bg-black/5 rounded-r-xl z-20"></div>`;
        sib.querySelector('.btn-delete-block').addEventListener('click', () => { sib.remove(); this._renderSubjects(); });
        sib.setAttribute('draggable', 'true');
        sib.addEventListener('dragstart', ev => {
            if (ev.target.dataset.r) { ev.preventDefault(); return; }
            this.activeDragData = { fromBlock: true, blockEl: sib, rowSpan: parseInt(sib.dataset.rs || '1') };
            ev.dataTransfer.effectAllowed = 'move';
            setTimeout(() => sib.style.opacity = '.35', 0);
        });
        sib.addEventListener('dragend', () => this.onDragEnd(sib));
        this._bindResizers(sib, blockData, paletteColor, primaryLabel, secondaryLabel, teacherInitials);
        document.getElementById('blocks-layer').appendChild(sib);
        this._renderSubjects();
        return sib;
    }

    _bindResizers(blockEl, blockData, paletteColor, primaryLabel, secondaryLabel, teacherInitials) {
        blockEl.querySelector('[data-r="s"]').addEventListener('mousedown', e => {
            e.stopPropagation(); e.preventDefault();
            blockEl.style.zIndex = '50';
            const startY = e.clientY, startH = blockEl.offsetHeight;
            const allB = Array.from(document.querySelectorAll('.schedule-block'));
            const onMM = me => {
                const step = this._getSlotElement(0, 0)?.offsetHeight || 40;
                const rs = Math.max(1, Math.round((startH + me.clientY - startY) / step));
                if (!isAreaOccupied(parseInt(blockEl.dataset.hourIndex), parseInt(blockEl.dataset.dayIndex), rs, 1, blockEl, allB)) {
                    blockEl.dataset.rs = rs; this._syncBlockToSlot(blockEl);
                }
            };
            const onMU = () => { blockEl.style.zIndex = '10'; removeEventListener('mousemove', onMM); removeEventListener('mouseup', onMU); };
            addEventListener('mousemove', onMM); addEventListener('mouseup', onMU);
        });
        blockEl.querySelector('[data-r="e"]').addEventListener('mousedown', e => {
            e.stopPropagation(); e.preventDefault();
            blockEl.style.zIndex = '50';
            const startX = e.clientX, startW = blockEl.offsetWidth;
            const originD = parseInt(blockEl.dataset.dayIndex);
            const hourIndex = parseInt(blockEl.dataset.hourIndex);
            const allB = Array.from(document.querySelectorAll('.schedule-block'));
            const filteredBlocks = allB.filter(b => b !== blockEl && !(b.dataset.hourIndex == hourIndex && b.querySelector('p')?.textContent === primaryLabel));
            let lastSpan = 1, siblings = [];
            const onMM = me => {
                const step = this._getSlotElement(0, 0)?.offsetWidth || 40;
                const targetSpan = Math.max(1, Math.round((startW + me.clientX - startX) / step));
                const rs = parseInt(blockEl.dataset.rs || '1');
                let allowed = 1;
                for (let s = 1; s < targetSpan; s++) {
                    if (originD + s >= DAY_COUNT || isAreaOccupied(hourIndex, originD + s, rs, 1, blockEl, filteredBlocks)) break;
                    allowed++;
                }
                if (allowed !== lastSpan) {
                    blockEl.style.width = `${(allowed * step) - 2}px`;
                    if (allowed > lastSpan) {
                        for (let s = lastSpan; s < allowed; s++) siblings.push(this._makeSiblingBlock(blockEl, originD + s, blockData, paletteColor, primaryLabel, secondaryLabel, teacherInitials));
                    } else {
                        for (let s = lastSpan - 1; s >= allowed; s--) if (siblings[s - 1]) { siblings[s - 1].remove(); siblings.splice(s - 1, 1); }
                    }
                    lastSpan = allowed;
                }
            };
            const onMU = () => {
                blockEl.style.zIndex = '10'; removeEventListener('mousemove', onMM); removeEventListener('mouseup', onMU);
                this._syncBlockToSlot(blockEl); siblings.forEach(s => this.undoStack.push(s));
            };
            addEventListener('mousemove', onMM); addEventListener('mouseup', onMU);
        });
    }

    refreshBlocksPositions() { document.querySelectorAll('.schedule-block').forEach(b => this._syncBlockToSlot(b)); }

    undoLast() {
        if (this.undoStack.length) { this.undoStack.pop().remove(); this._renderSubjects(); }
    }

    // ── Chargement emploi du temps ─────────────────────────────────────────

    async loadExistingEmploi(emploiId) {
        const emploi = await api.get(`/api/emplois-temps/${emploiId}`);
        if (!emploi) return;
        this.weekOffset = this._computeWeekOffsetFrom(emploi.dateDebut);
        this.renderWeek();
        setDatePickerValue('date-start', emploi.dateDebut);
        setDatePickerValue('date-end',   emploi.dateFin);
        // gridStart est mis à jour par renderWeek() juste au-dessus
        const gridStart = this.gridStart || getMonday(this.weekOffset);

        if (this.readonly && emploi.classeId) {
            await this._loadClasseNames([emploi.classeId]);
            this.seancesCache = [];
        }

        (emploi.seances || []).forEach(seance => {
            const enriched = { ...seance, classeId: emploi.classeId, className: this.classeMap[emploi.classeId] || '' };
            if (this.readonly) this.seancesCache.push(enriched);
            this._placeSeanceFromData(enriched, gridStart);
        });

        if (this.readonly) this._renderMobileView(gridStart);
    }

    /**
     * Mode lecture seule : charge les séances de l'enseignant pour la semaine courante.
     *
     * Stratégie :
     *  1. GET /api/emplois-temps?startDateBefore={samedi}&endDateAfter={lundi}&size=50
     *     → renvoie tous les emplois qui chevauchent la semaine
     *  2. Filtrage client-side sur seance.enseignantId === this.teacherId
     *  3. Placement des blocs sur la grille (lecture seule)
     *
     * La réponse HTTP complète est affichée dans la console pour vérification.
     */
    async _tryLoadForWeek() {
        const monday   = getMonday(this.weekOffset);
        const saturday = new Date(monday); saturday.setDate(monday.getDate() + 5);

        // Vider la couche des blocs avant de recharger
        const layer = document.getElementById('blocks-layer');
        if (layer) layer.innerHTML = '';

        const dateDebut = this._formatDateLocal(monday);
        const dateFin   = this._formatDateLocal(saturday);

        // startDateBefore=samedi → emploi.dateDebut <= samedi
        // endDateAfter=lundi    → emploi.dateFin   >= lundi
        // Étudiant : filtre direct par classeId (API le supporte nativement)
        // Enseignant : charge tout et filtre côté JS sur enseignantId
        const baseFilter = `startDateBefore=${dateFin}&endDateAfter=${dateDebut}`;
        const url = this.viewMode === 'student' && this.classId
            ? `/api/emplois-temps?classeId=${this.classId}&${baseFilter}&size=10`
            : `/api/emplois-temps?${baseFilter}&size=50`;

        console.group(`[Schedule] Chargement semaine ${dateDebut} → ${dateFin} (mode: ${this.viewMode})`);
        console.log('URL :', url);
        if (this.viewMode === 'student') console.log('classId :', this.classId);
        if (this.viewMode === 'teacher') console.log('teacherId attendu :', this.teacherId);

        try {
            const result = await api.get(url);

            // ── Affichage complet de la réponse HTTP ──
            console.log('Réponse HTTP complète :', result);

            const list = Array.isArray(result) ? result : (result?.content || []);
            console.log(`Emplois du temps reçus : ${list.length}`);

            if (!list.length) {
                console.warn('Aucun emploi du temps trouvé pour cette semaine.');
                console.groupEnd();
                this.showToast('Aucun emploi du temps pour cette semaine');
                return;
            }

            // Pré-charger les noms de classes pour enrichir les blocs et la vue mobile
            const classeIds = [...new Set(list.map(e => e.classeId).filter(Boolean))];
            await this._loadClasseNames(classeIds);

            const gridStart = this.gridStart || getMonday(this.weekOffset);
            this.seancesCache = [];
            let placedCount  = 0;

            list.forEach(emploi => {
                const className     = this.classeMap[emploi.classeId] || `Classe #${emploi.classeId}`;
                const seancesEmploi = emploi.seances || [];
                // Étudiant : toutes les séances (l'API a déjà filtré par classe)
                // Enseignant : filtre client-side par enseignantId
                const seancesFiltrees = seancesEmploi.filter(s => {
                    if (s.deleted) return false;
                    if (this.viewMode === 'teacher' && this.teacherId) {
                        return s.enseignantId === this.teacherId;
                    }
                    return true;
                });

                console.log(
                    `  Emploi #${emploi.id} (${className})` +
                    ` → ${seancesEmploi.length} séance(s) au total,` +
                    ` ${seancesFiltrees.length} retenue(s)` +
                    (this.viewMode === 'teacher' ? ` pour l'enseignant ${this.teacherId ?? '(tous)'}` : ''),
                    seancesFiltrees
                );

                seancesFiltrees.forEach(seance => {
                    const enriched = { ...seance, classeId: emploi.classeId, className };
                    this.seancesCache.push(enriched);
                    this._placeSeanceFromData(enriched, gridStart);
                    placedCount++;
                });
            });

            // ── Récapitulatif lisible séance par séance ────────────────────
            if (this.seancesCache.length) {
                console.group(`✅ ${this.seancesCache.length} séance(s) chargée(s) pour la grille`);
                this.seancesCache.forEach((s, i) => {
                    const debut = String(s.heureDebut || '').slice(0, 5);
                    const fin   = String(s.heureFin   || '').slice(0, 5);
                    console.log(
                        `  [${i + 1}] ${s.dateSeance}  ${debut}–${fin}` +
                        `  │  ${s.libelle || '(sans titre)'}` +
                        (s.salle     ? `  │  ${s.salle}`     : '') +
                        (s.className ? `  │  ${s.className}` : '')
                    );
                });
                console.groupEnd();
            } else {
                console.warn('⚠️  Aucune séance dans seancesCache — la grille sera vide.');
            }

            this._renderMobileView(gridStart);
            if (placedCount === 0) {
                this.showToast('Aucune séance pour cette semaine');
            }
        } catch (e) {
            console.error('[Schedule] Erreur lors du chargement :', e);
            this.showToast('Erreur de chargement du planning');
        } finally {
            console.groupEnd();
        }
    }

    _placeSeanceFromData(seance, gridStart) {
        if (!seance?.dateSeance || !seance?.heureDebut || !seance?.heureFin) return;

        // Parse en heure LOCALE pour éviter le décalage UTC.
        // new Date("2024-05-08") = minuit UTC ≠ minuit local si TZ ≠ 0.
        const [sy, sm, sd] = String(seance.dateSeance).split('-').map(Number);
        const seanceDate = new Date(sy, sm - 1, sd); // minuit local

        // Math.round plutôt que floor : élimine les erreurs d'arrondi (~quelques ms)
        const dayIndex = Math.round((seanceDate.getTime() - gridStart.getTime()) / 86400000);
        if (dayIndex < 0 || dayIndex >= DAY_COUNT) return;
        const startHour = parseInt(String(seance.heureDebut).split(':')[0]);
        const endHour   = parseInt(String(seance.heureFin).split(':')[0]);
        const hourIndex = HOURS.indexOf(startHour);
        if (hourIndex === -1) return;
        const rowSpan = Math.max(1, endHour - startHour);
        const colorId = seance.couleur || null;

        if (seance.type === 'EVENEMENT') {
            const iconKey   = seance.iconKey || 'event';
            const blockData = { type: 'event', eventName: seance.libelle || 'Événement', itemKey: `ev-${seance.id || Math.random()}`, iconKey };
            const block     = this.placeBlock(hourIndex, dayIndex, blockData, colorId || 'violet');
            block.dataset.rs        = String(rowSpan);
            block.dataset.eventName = seance.libelle || 'Événement';
            block.dataset.iconKey   = iconKey;
            const slot = this._getSlotElement(hourIndex, dayIndex);
            if (slot) block.style.height = `${(slot.offsetHeight * rowSpan) - 2}px`;
            this._syncBlockToSlot(block);
        } else {
            const subject   = this.SUBJECTS.find(s => s.id === seance.coursId);
            const blockData = {
                type: 'teacher', subjectId: seance.coursId, teacherId: seance.enseignantId,
                subjectName: subject?.name || seance.libelle || 'Séance', subjectCode: subject?.code || '',
                teacherName: '', teacherInitials: '',
                className: seance.className || '',
                salle:     seance.salle     || '',
                itemKey: seance.coursId ? `s${seance.coursId}` : `s-${seance.id || Math.random()}`,
            };
            const block = this.placeBlock(hourIndex, dayIndex, blockData, colorId);
            block.dataset.rs = String(rowSpan);
            const slot = this._getSlotElement(hourIndex, dayIndex);
            if (slot) block.style.height = `${(slot.offsetHeight * rowSpan) - 2}px`;
            this._syncBlockToSlot(block);
        }
    }

    // ── Sauvegarde (mode édition) ──────────────────────────────────────────

    _buildSeances() {
        const startStr = getDatePickerValue('date-start');
        if (!startStr) return [];
        // Parse en heure locale — new Date("YYYY-MM-DD") serait UTC minuit,
        // ce qui décale la date d'un jour dans les fuseaux négatifs.
        const [sy, sm, sd] = startStr.split('-').map(Number);
        const startDate = new Date(sy, sm - 1, sd); // minuit local
        const seances   = [];
        document.querySelectorAll('.schedule-block').forEach(block => {
            const blockType = block.dataset.blockType;
            const dayIndex  = parseInt(block.dataset.dayIndex);
            const hourIndex = parseInt(block.dataset.hourIndex);
            const rowSpan   = parseInt(block.dataset.rs || '1');
            const date      = new Date(startDate); date.setDate(startDate.getDate() + dayIndex);
            const startHour = HOURS[hourIndex];
            const endHour   = startHour + rowSpan;
            const colorId   = block.dataset.colorId || null;
            const dateStr   = this._formatDateLocal(date);
            if (blockType === 'teacher') {
                const subjectId = parseInt(block.dataset.subjectId);
                const teacherId = parseInt(block.dataset.teacherId) || 1;
                if (!subjectId) return;
                seances.push({
                    libelle: block.querySelector('p')?.textContent?.trim() || `Séance ${subjectId}`,
                    salle: 'Salle 1', dateSeance: dateStr,
                    heureDebut: this._formatHour(startHour), heureFin: this._formatHour(endHour),
                    coursId: subjectId, enseignantId: teacherId, type: 'SEANCE', couleur: colorId, iconKey: null,
                });
            } else if (blockType === 'event') {
                seances.push({
                    libelle: block.dataset.eventName || block.querySelector('p')?.textContent?.trim() || 'Événement',
                    salle: null, dateSeance: dateStr,
                    heureDebut: this._formatHour(startHour), heureFin: this._formatHour(endHour),
                    coursId: null, enseignantId: null, type: 'EVENEMENT',
                    couleur: colorId, iconKey: block.dataset.iconKey || 'event',
                });
            }
        });
        return seances;
    }

    async saveSchedule() {
        const dateDebut = this.fixedStartDate || getDatePickerValue('date-start');
        const dateFin   = this.fixedEndDate   || getDatePickerValue('date-end');
        if (!this.classId || !dateDebut || !dateFin) { this.showToast('Classe ou dates manquantes'); return; }
        const seances = this._buildSeances();
        if (!seances.length) { this.showToast('Ajoutez au moins une séance dans la grille'); return; }
        const payload = {
            id: this.emploiId || undefined, dateDebut, dateFin,
            semaine: this.semaine || this._computeWeekNumber(dateDebut),
            classeId: this.classId, seances,
        };
        try {
            if (this.emploiId) {
                await api.put(`/api/emplois-temps/${this.emploiId}/with-seances`, payload);
                this.showToast('Emploi du temps mis à jour ✓');
            } else {
                await api.post('/api/emplois-temps/with-seances', payload);
                this.showToast('Emploi du temps sauvegardé ✓');
            }
        } catch (error) {
            GlobalErrorHandler.handle(error);
            this.showToast(error?.response?.data || 'Erreur lors de la sauvegarde');
        }
    }

    // ── Utilitaires date ───────────────────────────────────────────────────

    _formatDateLocal(dateObj) {
        const tz    = dateObj.getTimezoneOffset();
        const local = new Date(dateObj.getTime() - tz * 60000);
        return local.toISOString().split('T')[0];
    }

    _formatHour(hour) { return `${String(hour).padStart(2, '0')}:00`; }

    _computeWeekOffsetFrom(dateStr) {
        if (!dateStr) return 0;
        const target = new Date(dateStr); target.setHours(0, 0, 0, 0);
        return Math.round((target.getTime() - getMonday(0).getTime()) / (7 * 86400000));
    }

    _computeWeekNumber(dateStr) {
        if (!dateStr) return null;
        const d      = new Date(dateStr);
        const target = new Date(Date.UTC(d.getFullYear(), d.getMonth(), d.getDate()));
        const dayNr  = (target.getUTCDay() + 6) % 7;
        target.setUTCDate(target.getUTCDate() - dayNr + 3);
        const firstThursday = new Date(Date.UTC(target.getUTCFullYear(), 0, 4));
        return 1 + Math.round(((target - firstThursday) / 86400000 - 3) / 7);
    }

    // ── Export & Toast ─────────────────────────────────────────────────────

    _initResizeObserver() {
        let raf = null;

        const onResize = () => {
            if (raf) cancelAnimationFrame(raf);
            raf = requestAnimationFrame(() => {
                this.recalcSlotHeight();
                // En mode édition, on rebuild la grille complète pour conserver
                // les listeners drag-drop ; en readonly, un simple repositionnement suffit.
                if (!this.readonly) {
                    this.buildGrid();
                }
                this.refreshBlocksPositions();
            });
        };

        // Observe le conteneur de la grille (réagit au repli de sidebar)
        const wrapper = document.getElementById('schedule-wrapper');
        if (wrapper) {
            new ResizeObserver(onResize).observe(wrapper);
        }

        // Fallback : window resize pour les cas où schedule-wrapper n'est pas visible (ex: mobile)
        window.addEventListener('resize', onResize);
    }

    async exportPDF() {
        // ── Année académique depuis la date de début ──────────────────────
        const dateStr = this.fixedStartDate || getDatePickerValue('date-start') || '';
        let anneeAcademique = null;
        let semestre        = null; // valeur par défaut (heuristique date)
        if (dateStr) {
            const [y, m] = dateStr.split('-').map(Number);
            if (m >= 9)      { anneeAcademique = `${y}/${y + 1}`; semestre = 1; }
            else if (m <= 1) { anneeAcademique = `${y - 1}/${y}`; semestre = 1; }
            else             { anneeAcademique = `${y - 1}/${y}`; semestre = 2; }
        }

        // ── Semestre RÉEL via GET /api/semestres/actif?niveauId=X ────────
        // Chaîne : classes/{id} → specialiteId → specialites/{id} → niveauId
        //        → semestres/actif?niveauId={id} → { numero, libelle, ... }
        const classId = this.classId || window.scheduleConfig?.classId || null;
        if (classId) {
            try {
                const classe     = await api.get(`/api/classes/${classId}`);
                const specialite = classe?.specialiteId
                    ? await api.get(`/api/specialites/${classe.specialiteId}`)
                    : null;
                if (specialite?.niveauId) {
                    const semestreActif = await api.get(
                        `/api/semestres/actif?niveauId=${specialite.niveauId}`
                    );
                    if (semestreActif?.numero != null) {
                        semestre = semestreActif.numero;
                    }
                }
            } catch (e) {
                console.warn('PDF : semestre actif non recupere', e);
            }
        }

        // ── Label semaine : DD/MM au DD/MM ───────────────────────────────
        // Priorité : élément #week-label (teacher/student) → calcul depuis gridStart (AP)
        const weekLabel = (() => {
            const elText = document.getElementById('week-label')?.textContent?.trim();
            if (elText) return elText;
            // Fallback pour EditSchedule.html qui n'a pas de #week-label
            const start = this.gridStart;
            if (!start) return '';
            const end = new Date(start); end.setDate(start.getDate() + (DAY_COUNT - 1));
            return `${fmtShort(start)} au ${fmtShort(end)}`;
        })();

        // ── Assemblage des métadonnées ────────────────────────────────────
        const config = window.scheduleConfig || {};
        const pdfMeta = {
            logoUrl: '/uploads/download.png',
            anneeAcademique,
            semestre,
            className: config.className
                    || document.getElementById('edit-class-label')?.textContent?.trim()
                    || '',
            weekLabel,
            semaine: config.semaine || this.semaine || '',
        };

        await generatePDF('main-page', msg => this.showToast(msg), pdfMeta);
    }

    // ── Vue mobile (lecture seule uniquement) ──────────────────────────────

    /**
     * Charge les noms de classes depuis l'API et les met en cache dans `this.classeMap`.
     * @param {number[]} classeIds
     */
    async _loadClasseNames(classeIds) {
        const unique = [...new Set(classeIds.filter(Boolean))];
        await Promise.all(unique.map(async id => {
            if (this.classeMap[id]) return; // déjà en cache
            try {
                const classe = await api.get(`/api/classes/${id}`);
                this.classeMap[id] = classe?.code || classe?.libelle || classe?.nom || `Classe #${id}`;
            } catch {
                this.classeMap[id] = `Classe #${id}`;
            }
        }));
    }

    /**
     * Construit les pills de jours et affiche les séances du jour courant (ou du lundi).
     * Appelée après chaque chargement de semaine en mode readonly.
     * @param {Date} monday
     */
    _renderMobileView(monday) {
        const pillsEl = document.getElementById('mobile-day-pills');
        if (!pillsEl) return; // pas en mode mobile / pas dans TeacherSchedule.html

        const DAYS_FR   = ['Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam'];
        const DAYS_FULL = ['Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi'];
        const today     = new Date();

        // Trouver le jour courant dans la semaine (−1 si hors semaine)
        let defaultDay = 0;
        for (let i = 0; i < 6; i++) {
            const d = new Date(monday); d.setDate(monday.getDate() + i);
            if (d.toDateString() === today.toDateString()) { defaultDay = i; break; }
        }

        pillsEl.innerHTML = DAYS_FR.map((day, i) => {
            const date    = new Date(monday); date.setDate(monday.getDate() + i);
            const isToday = date.toDateString() === today.toDateString();
            const hasSessions = this.seancesCache.some(s => {
                const d = new Date(s.dateSeance); return d.toDateString() === date.toDateString();
            });
            return `
            <button type="button"
                    class="mobile-day-pill flex flex-col items-center px-3 py-2 rounded-xl border-2 min-w-[52px] transition-all
                           ${isToday ? 'bg-primary text-white border-primary' : 'bg-card border-card-line text-muted-foreground-2'}"
                    data-day-idx="${i}">
                <span class="text-[10px] font-bold uppercase tracking-widest leading-none">${day}</span>
                <span class="text-base font-extrabold mt-0.5 leading-none">${date.getDate()}</span>
                ${hasSessions
                    ? `<span class="w-1.5 h-1.5 rounded-full mt-1 ${isToday ? 'bg-white/70' : 'bg-primary'}"></span>`
                    : `<span class="w-1.5 h-1.5 mt-1"></span>`}
            </button>`;
        }).join('');

        // Liaison clics
        pillsEl.querySelectorAll('.mobile-day-pill').forEach(btn => {
            btn.addEventListener('click', () => {
                pillsEl.querySelectorAll('.mobile-day-pill').forEach(b => {
                    b.classList.remove('bg-primary', 'text-white', 'border-primary');
                    b.classList.add('bg-card', 'border-card-line', 'text-muted-foreground-2');
                });
                btn.classList.remove('bg-card', 'border-card-line', 'text-muted-foreground-2');
                btn.classList.add('bg-primary', 'text-white', 'border-primary');
                this._renderMobileDaySessions(parseInt(btn.dataset.dayIdx), monday);
            });
        });

        this._renderMobileDaySessions(defaultDay, monday);
    }

    /**
     * Affiche la liste des séances d'un jour donné dans la vue mobile.
     * @param {number} dayIndex  0 = lundi … 5 = samedi
     * @param {Date}   monday
     */
    _renderMobileDaySessions(dayIndex, monday) {
        const listEl = document.getElementById('mobile-session-list');
        if (!listEl) return;

        const DAYS_FULL = ['Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi'];
        const targetDate = new Date(monday); targetDate.setDate(monday.getDate() + dayIndex);

        const sessions = this.seancesCache
            .filter(s => { const d = new Date(s.dateSeance); return d.toDateString() === targetDate.toDateString(); })
            .sort((a, b) => String(a.heureDebut || '').localeCompare(String(b.heureDebut || '')));

        if (!sessions.length) {
            listEl.innerHTML = `
                <div class="flex flex-col items-center justify-center py-16 text-center select-none">
                    <div class="size-16 rounded-2xl bg-muted flex items-center justify-center mb-4">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"
                             fill="none" stroke="currentColor" stroke-width="1.5" class="text-muted-foreground-2">
                            <rect x="3" y="4" width="18" height="18" rx="2"/>
                            <path d="M16 2v4M8 2v4M3 10h18"/>
                        </svg>
                    </div>
                    <p class="text-sm font-bold text-layer-foreground">${DAYS_FULL[dayIndex]}</p>
                    <p class="text-xs text-muted-foreground-2 mt-1">Aucune séance ce jour</p>
                </div>`;
            return;
        }

        listEl.innerHTML = sessions.map(s => {
            const color      = resolveColor(s.couleur || '#7c3aed');
            const heureDebut = String(s.heureDebut || '').slice(0, 5);
            const heureFin   = String(s.heureFin   || '').slice(0, 5);
            const isEvent    = s.type === 'EVENEMENT';
            // Étudiant : salle uniquement (classe déjà connue)
            // Enseignant : classe + salle
            const subLine = this.viewMode === 'student'
                ? [s.salle].filter(Boolean).join(' · ')
                : [s.className, s.salle].filter(Boolean).join(' · ');
            return `
            <div class="rounded-2xl bg-card border border-card-line shadow-sm overflow-hidden"
                 style="border-left: 4px solid ${color.border}">
                <div class="px-4 py-3.5">
                    <div class="flex items-start justify-between gap-3">
                        <div class="flex-1 min-w-0">
                            <p class="text-xs font-semibold text-muted-foreground-2 tabular-nums">
                                ${heureDebut} – ${heureFin}
                            </p>
                            <p class="font-bold text-sm text-layer-foreground mt-0.5 leading-snug">${s.libelle || 'Séance'}</p>
                            ${subLine ? `<p class="text-xs text-muted-foreground-2 mt-0.5">${subLine}</p>` : ''}
                        </div>
                        <span class="shrink-0 mt-0.5 px-2.5 py-1 text-[11px] font-bold rounded-lg"
                              style="background:${color.bg};color:${color.border}">
                            ${isEvent ? 'Évènement' : 'Cours'}
                        </span>
                    </div>
                </div>
            </div>`;
        }).join('');
    }

    showToast(message) {
        const t  = document.getElementById('toast');
        const tm = document.getElementById('toast-msg');
        if (!t || !tm) return;
        tm.textContent = message;
        t.classList.remove('hidden');
        if (this.toastTimer) clearTimeout(this.toastTimer);
        this.toastTimer = setTimeout(() => t.classList.add('hidden'), 2800);
    }
}

document.addEventListener('DOMContentLoaded', () => { window.editScheduleCtrl = new EditScheduleController(); });

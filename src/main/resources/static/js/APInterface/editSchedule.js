import {
    HOURS, DAY_COUNT, PALETTE, SUBJECTS, ICON_SVG,
    getMonday, fmtISO, fmtShort, generatePDF, isAreaOccupied, isWithinBounds
} from './editScheduleUtils.js';
import api from '../common/ClientHttp.js';

function resolveColor(colorIdOrHex, fallbackId = 'violet') {
    const isHex = typeof colorIdOrHex === 'string' && colorIdOrHex.startsWith('#') && colorIdOrHex.length === 7;
    if (isHex) {
        return { id: colorIdOrHex, bg: `${colorIdOrHex}1f`, border: colorIdOrHex, text: colorIdOrHex };
    }
    const found = PALETTE.find(p => p.id === colorIdOrHex);
    if (found) return found;
    return PALETTE.find(p => p.id === fallbackId) || PALETTE[0];
}

/**
 * Lit la valeur d'un input Preline datepicker ou d'un input date natif.
 * Retourne une chaîne au format YYYY-MM-DD ou '' si vide.
 */
function getDatePickerValue(inputId) {
    const el = document.getElementById(inputId);
    if (!el) return '';
    // Preline stocke la valeur ISO dans data-hs-datepicker-value
    // Si elle n'est pas disponible, on retombe sur la valeur brute de l'input
    return el.dataset.hsDatepickerValue || el.value || '';
}

/**
 * Injecte une valeur dans un datepicker Preline (ou dans un input natif).
 * La valeur doit être au format YYYY-MM-DD.
 */
function setDatePickerValue(inputId, isoDateStr) {
    const el = document.getElementById(inputId);
    if (!el || !isoDateStr) return;

    // Méthode Preline: passer par l'instance HSDatepicker si disponible
    const wrapper = el.closest('[data-hs-datepicker]');
    if (wrapper && window.HSDatepicker) {
        const instance = HSDatepicker.getInstance(wrapper);
        if (instance) {
            instance.setDate(isoDateStr);
            return;
        }
    }
    // Fallback: écriture directe + dataset
    el.value = isoDateStr;
    el.dataset.hsDatepickerValue = isoDateStr;
}

export class EditScheduleController {
    constructor() {
        this.EVENTS = [
            { id: 'e1', name: 'Pause', iconKey: 'pause', defaultColor: 'yellow' },
            { id: 'e2', name: 'Temps personnel', iconKey: 'perso', defaultColor: 'blue' },
        ];
        this.SUBJECTS = [];
        this.selectedColors = {};
        this.weekOffset = 0;
        this.emploiId = null;
        this.slotHeight = 40;
        this.undoStack = [];
        this.activeDragData = null;
        this.popoverKey = null;
        this._pendingDrop = null;

        this.EVENTS.forEach(event => this.selectedColors[event.id] = event.defaultColor);

        this.init().catch((e) => console.error(e));
    }

    async init() {
        const params = new URLSearchParams(window.location.search);
        const className = params.get('class');
        const classId = params.get('classId');
        const emploiId = params.get('emploiId');
        this.classId = classId ? parseInt(classId) : null;
        this.emploiId = emploiId ? parseInt(emploiId) : null;
        const label = document.getElementById('edit-class-label');
        if (label) label.textContent = className || 'Nouvelle classe';

        const viewEdit = document.getElementById('view-edit');
        if (viewEdit) { viewEdit.classList.remove('hidden'); viewEdit.classList.add('flex'); }

        if (classId) {
            await this._loadSubjectsForClass(classId);
        } else {
            this.SUBJECTS = SUBJECTS;
        }
        this.SUBJECTS.forEach(subject => {
            this.selectedColors[`s${subject.id}`] = subject.defaultColor || 'blue';
        });

        this.renderPanel();
        this._bindControls();
        this._bindSubjectDropModal();
        this._bindSubjectSearch();

        // Exposer les callbacks Preline datepicker sur window AVANT l'init Preline
        window.__onDateStartChange = (val) => this.handleDatePickerChange(val);
        window.__onDateEndChange = (val) => { /* la date de fin ne change pas la semaine */ };

        window.addEventListener('resize', () => {
            this.recalcSlotHeight();
            this.buildGrid();
            this.refreshBlocksPositions();
        });

        this.renderWeek();

        if (this.emploiId) {
            this.loadExistingEmploi(this.emploiId).catch((e) => console.error('Erreur chargement emploi existant', e));
        }
    }

    _bindSubjectSearch() {
        const input = document.getElementById('subject-search');
        if (!input) return;
        input.addEventListener('input', () => this._renderSubjects());
    }

    async _loadSubjectsForClass(classId) {
        const classe = await api.get(`/api/classes/${classId}`);
        const specialiteId = classe?.specialiteId;
        if (!specialiteId) {
            this.SUBJECTS = [];
            return;
        }
        const page = await api.get(`/api/ue?specialiteId=${encodeURIComponent(specialiteId)}&deleted=false&size=200`);
        const ues = page?.content || [];
        this.SUBJECTS = ues.map((ue) => ({
            id: ue.id,
            name: ue.libelle,
            code: ue.code,
            defaultColor: (typeof ue.couleur === 'string' && ue.couleur.startsWith('#') && ue.couleur.length === 7) ? ue.couleur : '#3b82f6',
            teachers: [],
        }));
    }

    _bindControls() {
        document.getElementById('btn-exit')?.addEventListener('click', () => this.exitEditMode());
        document.getElementById('btn-undo')?.addEventListener('click', () => this.undoLast());
        document.getElementById('btn-prev-week')?.addEventListener('click', () => { this.weekOffset--; this.renderWeek(); });
        document.getElementById('btn-next-week')?.addEventListener('click', () => { this.weekOffset++; this.renderWeek(); });
        document.getElementById('btn-export-pdf')?.addEventListener('click', () => this.exportPDF());
        document.getElementById('btn-save')?.addEventListener('click', () => this.saveSchedule());

        // Compatibilité : écoute aussi le changement natif au cas où Preline n'est pas chargé
        document.getElementById('date-start')?.addEventListener('change', e => this.handleDatePickerChange(e.target.value));

        document.getElementById('btn-open-event-modal')?.addEventListener('click', () => this.openAddEventModal());
        document.getElementById('btn-confirm-event')?.addEventListener('click', () => this.confirmAddEvent());
        document.querySelectorAll('.btn-close-modal').forEach(button =>
            button.addEventListener('click', () => this.hideModal(button.closest('.modal-container').id))
        );

        document.addEventListener('click', event => {
            const popoverElement = document.getElementById('color-popover');
            if (popoverElement && !popoverElement.classList.contains('hidden') && !popoverElement.contains(event.target)) {
                popoverElement.classList.add('hidden');
                this.popoverKey = null;
            }
        });
    }

    _bindSubjectDropModal() {
        const modal = document.getElementById('modal-subject-drop');
        const confirmButton = document.getElementById('btn-confirm-subject-drop');
        const cancelButton = document.getElementById('btn-cancel-subject-drop');
        if (!modal) return;

        confirmButton?.addEventListener('click', () => this._confirmSubjectDrop());
        cancelButton?.addEventListener('click', () => {
            this.hideModal('modal-subject-drop');
            this._pendingDrop = null;
        });
        modal.querySelectorAll('.btn-close-modal').forEach(button =>
            button.addEventListener('click', () => {
                this.hideModal('modal-subject-drop');
                this._pendingDrop = null;
            })
        );
    }

    _openSubjectDropModal(hourIndex, dayIndex, subject) {
        this._pendingDrop = { hourIndex, dayIndex, subject };
        const teacherSelect = document.getElementById('drop-teacher-select');
        if (teacherSelect) {
            const options = subject.teachers.length
                ? subject.teachers.map(teacher =>
                    `<option value="${teacher.id}">${teacher.name}</option>`
                  ).join('')
                : `<option value="1">Enseignant par défaut (id 1)</option>`;
            teacherSelect.innerHTML = options;
        }

        const currentSubjectColorId = this.selectedColors[`s${subject.id}`] || subject.defaultColor;
        const colorGrid = document.getElementById('drop-color-grid');
        if (colorGrid) {
            colorGrid.innerHTML = PALETTE.map(color => `
                <button type="button"
                    class="drop-color-btn size-8 rounded-xl border-2 hover:scale-110 transition-all ${currentSubjectColorId === color.id ? 'ring-2 ring-offset-1 ring-primary shadow-sm' : ''}"
                    data-color="${color.id}" title="${color.id}"
                    style="background:${color.bg};border-color:${color.border}"></button>`
            ).join('');
            colorGrid.querySelectorAll('.drop-color-btn').forEach(button => {
                button.addEventListener('click', () => {
                    colorGrid.querySelectorAll('.drop-color-btn').forEach(b => b.classList.remove('ring-2', 'ring-offset-1', 'ring-primary', 'shadow-sm'));
                    button.classList.add('ring-2', 'ring-offset-1', 'ring-primary', 'shadow-sm');
                    if (this._pendingDrop) this._pendingDrop.colorId = button.dataset.color;
                });
            });
        }

        this._pendingDrop.colorId = currentSubjectColorId;
        this.showModal('modal-subject-drop');
    }

    _confirmSubjectDrop() {
        if (!this._pendingDrop) return;
        const { hourIndex, dayIndex, subject, colorId } = this._pendingDrop;
        const teacherSelect = document.getElementById('drop-teacher-select');
        const teacherId = teacherSelect ? parseInt(teacherSelect.value) || 1 : (subject.teachers[0]?.id || 1);
        const teacher = subject.teachers.find(t => t.id === teacherId) || subject.teachers[0] || { id: 1, name: 'Enseignant', initials: 'PR' };

        this.selectedColors[`s${subject.id}`] = colorId;
        this._refreshSubjectCard(subject.id, colorId);

        const dropData = {
            type: 'teacher',
            subjectId: subject.id,
            subjectName: subject.name,
            subjectCode: subject.code,
            teacherId: teacherId,
            teacherName: teacher?.name || 'Enseignant',
            teacherInitials: teacher?.initials || 'PR',
            itemKey: `s${subject.id}`,
            fromBlock: false,
        };

        this.hideModal('modal-subject-drop');
        this._pendingDrop = null;
        this.undoStack.push(this.placeBlock(hourIndex, dayIndex, dropData, colorId));
    }

    _refreshSubjectCard(subjectId, colorId) {
        const paletteColor = resolveColor(colorId);
        const colorSwatch = document.getElementById(`swatch-s${subjectId}`);
        if (colorSwatch) {
            colorSwatch.style.background = paletteColor.bg;
            colorSwatch.style.borderColor = paletteColor.border;
        }
    }

    exitEditMode() { window.location.href = '/ap/schedule'; }

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
        const modalElement = document.getElementById(modalId);
        if (!modalElement) return;
        modalElement.classList.remove('hidden');
        modalElement.classList.add('flex');
    }

    hideModal(modalId) {
        const modalElement = document.getElementById(modalId);
        if (!modalElement) return;
        modalElement.classList.add('hidden');
        modalElement.classList.remove('flex');
    }

    openColorPicker(itemKey, anchorElement, event) {
        event.stopPropagation();
        const popoverElement = document.getElementById('color-popover');
        if (this.popoverKey === itemKey && !popoverElement.classList.contains('hidden')) {
            popoverElement.classList.add('hidden');
            this.popoverKey = null;
            return;
        }
        this.popoverKey = itemKey;
        document.getElementById('color-popover-grid').innerHTML = PALETTE.map(color => `
            <button class="color-btn size-8 rounded-xl border-2 hover:scale-110 transition-all ${this.selectedColors[itemKey] === color.id ? 'ring-2 ring-offset-1 ring-primary shadow-sm' : ''}"
                    data-color="${color.id}" data-key="${itemKey}" title="${color.id}"
                    style="background:${color.bg};border-color:${color.border}"></button>`).join('');
        document.querySelectorAll('.color-btn').forEach(button =>
            button.addEventListener('click', ev => this.setColor(ev.currentTarget.dataset.key, ev.currentTarget.dataset.color, ev.currentTarget))
        );
        const anchorRect = anchorElement.getBoundingClientRect();
        popoverElement.style.top = `${anchorRect.bottom + 8}px`;
        popoverElement.style.left = `${Math.min(anchorRect.left - 60, window.innerWidth - 172)}px`;
        popoverElement.classList.remove('hidden');
    }

    setColor(itemKey, colorId, button) {
        this.selectedColors[itemKey] = colorId;
        button.closest('#color-popover-grid').querySelectorAll('button')
            .forEach(btn => btn.classList.remove('ring-2', 'ring-offset-1', 'ring-primary', 'shadow-sm'));
        button.classList.add('ring-2', 'ring-offset-1', 'ring-primary', 'shadow-sm');
        const paletteColor = PALETTE.find(p => p.id === colorId);
        const colorSwatch = document.getElementById(`swatch-${itemKey}`);
        if (colorSwatch) {
            colorSwatch.style.background = paletteColor.bg;
            colorSwatch.style.borderColor = paletteColor.border;
        }
    }

    renderPanel() {
        this._renderSubjects();
        this._renderEvents();
    }

    _getScheduledSubjectKeySet() {
        const set = new Set();
        document.querySelectorAll('.schedule-block[data-item-key]').forEach((b) => {
            const k = b.dataset.itemKey || '';
            if (k.startsWith('s')) set.add(k);
        });
        return set;
    }

    _matchesSubjectQuery(subject, query) {
        if (!query) return true;
        const q = query.toLowerCase();
        return (
            String(subject?.name || '').toLowerCase().includes(q) ||
            String(subject?.code || '').toLowerCase().includes(q)
        );
    }

    _renderSubjectList(containerId, subjects, scheduledKeys) {
        const el = document.getElementById(containerId);
        if (!el) return;

        if (!subjects.length) {
            el.innerHTML = `
                <div class="px-3 py-3 text-sm text-muted-foreground-2">
                    Aucune matière.
                </div>
            `;
            return;
        }

        el.innerHTML = subjects.map(subject => {
            const paletteColor = resolveColor(this.selectedColors[`s${subject.id}`] || subject.defaultColor);
            const isScheduled = scheduledKeys?.has(`s${subject.id}`);
            return `
            <a class="subject-card group flex flex-col bg-layer rounded-xl hover:bg-layer-hover px-2 focus:outline-hidden focus:shadow-md transition cursor-grab active:cursor-grabbing"
               draggable="true"
               data-type="subject"
               data-subject-id="${subject.id}"
               data-subject-name="${subject.name}"
               data-subject-code="${subject.code}"
               data-item-key="s${subject.id}"
               href="#">
                <div class="py-2 ">
                    <div class="flex gap-x-3">
                        <div class="mt-0.5 relative shrink-0 size-8 rounded-lg flex items-center justify-center" style="background:${paletteColor.bg};color:${paletteColor.border}">
                            ${ICON_SVG.books}
                            <span class="inline-flex absolute bottom-1 translate-y-1/2 right-1 translate-x-1/2 items-center size-2 rounded-full ${isScheduled ? 'bg-emerald-500' : 'bg-muted'}"></span>
                        </div>
                        <div class="grow min-w-0">
                            <h3 class="group-hover:text-primary font-semibold text-foreground text-[13px] leading-tight truncate">${subject.name}</h3>
                            <div class="flex items-center justify-between gap-1">
                                <p class="text-xs text-muted-foreground-1 mt-0.5 truncate">${subject.code}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </a>`;
        }).join('');

        el.querySelectorAll('.subject-card').forEach(element => {
            element.addEventListener('dragstart', event => this.onDragStart(event, element));
            element.addEventListener('dragend', () => this.onDragEnd(element));
        });
    }

    _renderSubjects() {
        const query = (document.getElementById('subject-search')?.value || '').trim();
        const scheduledKeys = this._getScheduledSubjectKeySet();

        const all = this.SUBJECTS.filter(s => this._matchesSubjectQuery(s, query));
        const scheduled = all.filter(s => scheduledKeys.has(`s${s.id}`));
        const unscheduled = all.filter(s => !scheduledKeys.has(`s${s.id}`));

        this._renderSubjectList('subject-list-all', all, scheduledKeys);
        this._renderSubjectList('subject-list-unscheduled', unscheduled, scheduledKeys);
        this._renderSubjectList('subject-list-scheduled', scheduled, scheduledKeys);
    }

    _renderEvents() {
        const eventListElement = document.getElementById('event-list');
        if (!eventListElement) return;
        eventListElement.innerHTML = this.EVENTS.map(event => {
            const paletteColor = resolveColor(this.selectedColors[event.id] || event.defaultColor);
            return `
            <div draggable="true" data-type="event" data-event-name="${event.name}" data-item-key="${event.id}" data-icon-key="${event.iconKey}"
                 class="event-item flex items-center gap-2 px-3 py-2 bg-layer text-layer-foreground rounded-xl hover:bg-layer-hover transition-all cursor-grab active:cursor-grabbing text-[12px] font-bold">
                <div class="size-8 rounded-lg flex items-center justify-center shrink-0" style="background:${paletteColor.bg};color:${paletteColor.border}">
                    ${ICON_SVG[event.iconKey] || ICON_SVG.event}
                </div>
                <span class="truncate">${event.name}</span>
            </div>`;
        }).join('');

        eventListElement.querySelectorAll('.event-item').forEach(element => {
            element.addEventListener('dragstart', event => this.onDragStart(event, element));
            element.addEventListener('dragend', () => this.onDragEnd(element));
        });
    }

    renderWeek() {
        const mondayDate = getMonday(this.weekOffset);
        const weekDays = Array.from({ length: DAY_COUNT }, (_, dayIndex) => {
            const dayDate = new Date(mondayDate);
            dayDate.setDate(mondayDate.getDate() + dayIndex);
            return dayDate;
        });

        // Mise à jour des datepickers Preline (ou des inputs natifs en fallback)
        setDatePickerValue('date-start', this._formatDateLocal(weekDays[0]));
        setDatePickerValue('date-end', this._formatDateLocal(weekDays[5]));

        document.getElementById('week-label').textContent = `${fmtShort(weekDays[0])} – ${fmtShort(weekDays[5])}`;

        const headersElement = document.getElementById('day-headers');
        if (!headersElement) return;
        headersElement.style.gridTemplateColumns = `${this.timeColumnWidth}px repeat(${DAY_COUNT},${this.dayColumnWidth()})`;
        headersElement.innerHTML = `<div class="py-2 text-xs text-center text-muted-foreground-2 font-bold border-r-2 border-card-line sticky left-0 bg-card z-30">Horaires</div>`;
        weekDays.forEach(dayDate => {
            const dayName = dayDate.toLocaleDateString('fr-FR', { weekday: 'short' }).slice(0, 3).toUpperCase();
            const isToday = dayDate.toDateString() === new Date().toDateString();
            headersElement.innerHTML += `<div class="cell py-1.5 text-center border-l border-card-line bg-card">
                <p class="text-[10px] font-bold  text-muted-foreground-2">${dayName}</p>
                <p class="text-sm font-extrabold ${isToday ? 'text-primary' : 'text-layer-foreground'}">${dayDate.getDate()}</p>
            </div>`;
        });

        this.buildGrid();
    }

    /** Appelé par le callback Preline ou le change natif */
    handleDatePickerChange(isoValue) {
        if (!isoValue) return;
        this.weekOffset = Math.round((new Date(isoValue) - getMonday(0)) / (7 * 86400000));
        this.renderWeek();
    }

    /** Rétrocompatibilité avec l'ancienne signature handleDateChange(e) */
    handleDateChange(e) {
        this.handleDatePickerChange(e?.target?.value || e);
    }

    recalcSlotHeight() {
        const gridElement = document.getElementById('schedule-body');
        const firstSlotElement = gridElement?.children[0];
        if (firstSlotElement) {
            this.slotHeight = firstSlotElement.getBoundingClientRect().height;
        }
    }

    get timeColumnWidth() { return 72; }
    dayColumnWidth() { return "1fr"; }

    buildGrid() {
        const gridBody = document.getElementById('schedule-body');
        if (!gridBody) return;
        gridBody.style.gridTemplateColumns = `${this.timeColumnWidth}px repeat(${DAY_COUNT},${this.dayColumnWidth()})`;
        gridBody.style.gridTemplateRows = `repeat(${HOURS.length}, 1fr)`;

        let gridHtml = '';
        HOURS.forEach((hour, hourIndex) => {
            gridHtml += `<div style="grid-column:1;grid-row:${hourIndex + 1}"
                          class="time-cell cell flex items-center justify-center border-b  border-r-2 border-card-line bg-card sticky left-0 z-20">
                       <span class="text-[11px] font-mono font-semibold text-muted-foreground-2 whitespace-nowrap">${hour}h–${hour + 1}h</span>
                     </div>`;
            for (let dayIndex = 0; dayIndex < DAY_COUNT; dayIndex++) {
                gridHtml += `<div style="grid-column:${dayIndex + 2};grid-row:${hourIndex + 1}"
                              class="cell slot border-b border-card-line border-l bg-card/80 transition-colors "
                              data-hour-index="${hourIndex}" data-day-index="${dayIndex}"></div>`;
            }
        });
        gridBody.innerHTML = gridHtml;

        gridBody.querySelectorAll('.slot').forEach(slotElement => {
            slotElement.addEventListener('dragover', event => this.onSlotDragOver(event, slotElement));
            slotElement.addEventListener('dragleave', () => this.onSlotDragLeave(slotElement));
            slotElement.addEventListener('drop', event => this.onSlotDrop(event, slotElement));
        });
        this.undoStack = [];
    }

    _getSlotElement(hourIndex, dayIndex) {
        return document.querySelector(`.slot[data-hour-index="${hourIndex}"][data-day-index="${dayIndex}"]`);
    }

    _syncBlockToSlot(blockElement) {
        const hourIndex = parseInt(blockElement.dataset.hourIndex);
        const dayIndex = parseInt(blockElement.dataset.dayIndex);
        const rowSpan = parseInt(blockElement.dataset.rs || "1");
        const slot = this._getSlotElement(hourIndex, dayIndex);
        if (slot) {
            blockElement.style.left = `${slot.offsetLeft + 1}px`;
            blockElement.style.top = `${slot.offsetTop + 1}px`;
            blockElement.style.width = `${slot.offsetWidth - 2}px`;
            blockElement.style.height = `${(slot.offsetHeight * rowSpan) - 2}px`;
        }
    }

    onDragStart(event, element) {
        this.activeDragData = {
            type: element.dataset.type,
            subjectId: element.dataset.subjectId ? parseInt(element.dataset.subjectId) : null,
            subjectName: element.dataset.subjectName || '',
            subjectCode: element.dataset.subjectCode || '',
            teacherName: element.dataset.teacherName || '',
            eventName: element.dataset.eventName || '',
            itemKey: element.dataset.itemKey,
            iconKey: element.dataset.iconKey || '',
            fromBlock: false,
            rowSpan: 1,
        };
        event.dataTransfer.effectAllowed = 'copy';
        element.style.opacity = '.4';
    }

    onDragEnd(element) {
        element.style.opacity = '';
        this._clearDragHighlights();
    }

    onSlotDragOver(event, slotElement) {
        event.preventDefault();
        if (!this.activeDragData) return;

        const h = parseInt(slotElement.dataset.hourIndex);
        const d = parseInt(slotElement.dataset.dayIndex);
        const rs = this.activeDragData.rowSpan || 1;
        const blockEl = this.activeDragData.fromBlock ? this.activeDragData.blockEl : null;

        const isOk = isWithinBounds(h, d, rs, 1) && !isAreaOccupied(h, d, rs, 1, blockEl);
        const highlightClass = isOk ? '!bg-primary/20' : '!bg-red-500/20';

        this._clearDragHighlights();

        for (let i = 0; i < rs; i++) {
            const slot = this._getSlotElement(h + i, d);
            if (slot) {
                slot.classList.add('drag-highlight', highlightClass);
            }
        }
    }

    onSlotDragLeave() {
        // cleared on next dragover or drop
    }

    _clearDragHighlights() {
        document.querySelectorAll('.drag-highlight').forEach(el => {
            el.classList.remove('drag-highlight', '!bg-primary/20', '!bg-red-500/20');
        });
    }

    onSlotDrop(event, slotElement) {
        event.preventDefault();
        this._clearDragHighlights();

        if (!this.activeDragData) return;

        const hourIndex = parseInt(slotElement.dataset.hourIndex);
        const dayIndex = parseInt(slotElement.dataset.dayIndex);
        const rs = this.activeDragData.rowSpan || 1;
        const blockEl = this.activeDragData.fromBlock ? this.activeDragData.blockEl : null;

        if (!isWithinBounds(hourIndex, dayIndex, rs, 1)) {
            this.showToast('Déborde de la grille');
            this.activeDragData = null;
            return;
        }

        if (isAreaOccupied(hourIndex, dayIndex, rs, 1, blockEl)) {
            this.showToast('Espace déjà occupé');
            this.activeDragData = null;
            return;
        }

        // Move existing block
        if (this.activeDragData.fromBlock) {
            blockEl.dataset.hourIndex = hourIndex;
            blockEl.dataset.dayIndex = dayIndex;
            this._syncBlockToSlot(blockEl);
            this.activeDragData = null;
            return;
        }

        // Subject drop
        if (this.activeDragData.type === 'subject') {
            const subject = this.SUBJECTS.find(s => s.id === this.activeDragData.subjectId);
            if (subject) this._openSubjectDropModal(hourIndex, dayIndex, subject);
            this.activeDragData = null;
            return;
        }

        // Event drop
        const newBlock = this.placeBlock(hourIndex, dayIndex, this.activeDragData);
        this.undoStack.push(newBlock);
        this.activeDragData = null;
    }

    placeBlock(hourIndex, dayIndex, blockData, forcedColorId = null) {
        const colorId = forcedColorId || this.selectedColors[blockData.itemKey] || 'violet';
        const paletteColor = resolveColor(colorId);
        const primaryLabel = blockData.type === 'teacher' ? blockData.subjectName : blockData.eventName;
        const secondaryLabel = blockData.type === 'teacher' ? blockData.teacherName : null;
        const teacherInitials = blockData.teacherInitials || (blockData.teacherName?.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase()) || 'UE';

        const blockElement = document.createElement('div');
        blockElement.className = 'group relative schedule-block shadow-sm';
        blockElement.dataset.hourIndex = hourIndex;
        blockElement.dataset.dayIndex = dayIndex;
        blockElement.dataset.rs = "1";
        if (blockData?.itemKey) blockElement.dataset.itemKey = blockData.itemKey;
        if (blockData?.subjectId != null) blockElement.dataset.subjectId = String(blockData.subjectId);
        if (blockData?.teacherId != null) blockElement.dataset.teacherId = String(blockData.teacherId);
        if (blockData?.type) blockElement.dataset.blockType = blockData.type;
        // Stocker la couleur et l'iconKey pour les événements (utile à la sauvegarde)
        if (colorId) blockElement.dataset.colorId = colorId;
        if (blockData?.iconKey) blockElement.dataset.iconKey = blockData.iconKey;
        if (blockData?.eventName) blockElement.dataset.eventName = blockData.eventName;

        blockElement.style.position = 'absolute';
        blockElement.style.background = paletteColor.bg;
        blockElement.style.border = `1.5px solid ${paletteColor.border}`;
        blockElement.style.borderLeftWidth = '4px';
        blockElement.style.color = paletteColor.text;
        blockElement.style.borderRadius = '12px';
        blockElement.style.padding = '8px';
        blockElement.style.pointerEvents = 'all';
        blockElement.style.zIndex = '10';
        blockElement.style.cursor = 'grab';
        blockElement.style.userSelect = 'none';

        this._syncBlockToSlot(blockElement);

        blockElement.innerHTML = `
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
            <div data-r="e" class="absolute top-0 bottom-0 right-0 w-2 cursor-e-resize hover:bg-black/5 rounded-r-xl z-20"></div>
        `;

        blockElement.querySelector('.btn-delete-block').addEventListener('click', () => {
            blockElement.remove();
            this.undoStack = this.undoStack.filter(b => b !== blockElement);
            this._renderSubjects();
        });

        blockElement.setAttribute('draggable', 'true');
        blockElement.addEventListener('dragstart', event => {
            if (event.target.dataset.r) { event.preventDefault(); return; }
            this.activeDragData = {
                fromBlock: true,
                blockEl: blockElement,
                rowSpan: parseInt(blockElement.dataset.rs || "1")
            };
            event.dataTransfer.effectAllowed = 'move';
            setTimeout(() => blockElement.style.opacity = '.35', 0);
        });
        blockElement.addEventListener('dragend', () => this.onDragEnd(blockElement));

        this._bindResizers(blockElement, blockData, paletteColor, primaryLabel, secondaryLabel, teacherInitials);

        document.getElementById('blocks-layer').appendChild(blockElement);
        this._renderSubjects();
        return blockElement;
    }

    _makeSiblingBlock(originalBlock, siblingDayIndex, blockData, paletteColor, primaryLabel, secondaryLabel, teacherInitials) {
        const sib = document.createElement('div');
        sib.className = 'group relative schedule-block shadow-sm';
        sib.dataset.hourIndex = originalBlock.dataset.hourIndex;
        sib.dataset.dayIndex = siblingDayIndex;
        sib.dataset.rs = originalBlock.dataset.rs || "1";
        sib.dataset.isSibling = "true";
        if (originalBlock.dataset.itemKey) sib.dataset.itemKey = originalBlock.dataset.itemKey;
        if (originalBlock.dataset.subjectId) sib.dataset.subjectId = originalBlock.dataset.subjectId;
        if (originalBlock.dataset.teacherId) sib.dataset.teacherId = originalBlock.dataset.teacherId;
        if (originalBlock.dataset.blockType) sib.dataset.blockType = originalBlock.dataset.blockType;
        if (originalBlock.dataset.colorId) sib.dataset.colorId = originalBlock.dataset.colorId;
        if (originalBlock.dataset.iconKey) sib.dataset.iconKey = originalBlock.dataset.iconKey;
        if (originalBlock.dataset.eventName) sib.dataset.eventName = originalBlock.dataset.eventName;
        sib.style.cssText = originalBlock.style.cssText;
        sib.style.width = originalBlock.style.width;

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
            <div data-r="e" class="absolute top-0 bottom-0 right-0 w-2 cursor-e-resize hover:bg-black/5 rounded-r-xl z-20"></div>
        `;
        sib.querySelector('.btn-delete-block').addEventListener('click', () => {
            sib.remove();
            this._renderSubjects();
        });

        sib.setAttribute('draggable', 'true');
        sib.addEventListener('dragstart', event => {
            if (event.target.dataset.r) { event.preventDefault(); return; }
            this.activeDragData = {
                fromBlock: true,
                blockEl: sib,
                rowSpan: parseInt(sib.dataset.rs || "1")
            };
            event.dataTransfer.effectAllowed = 'move';
            setTimeout(() => sib.style.opacity = '.35', 0);
        });
        sib.addEventListener('dragend', () => this.onDragEnd(sib));

        this._bindResizers(sib, blockData, paletteColor, primaryLabel, secondaryLabel, teacherInitials);
        document.getElementById('blocks-layer').appendChild(sib);
        this._renderSubjects();
        return sib;
    }

    _bindResizers(blockElement, blockData, paletteColor, primaryLabel, secondaryLabel, teacherInitials) {
        // South
        blockElement.querySelector('[data-r="s"]').addEventListener('mousedown', e => {
            e.stopPropagation(); e.preventDefault();
            blockElement.style.zIndex = '50';
            const startY = e.clientY, startH = blockElement.offsetHeight, allB = Array.from(document.querySelectorAll('.schedule-block'));
            const onMM = me => {
                const step = this._getSlotElement(0, 0)?.offsetHeight || 40;
                const rs = Math.max(1, Math.round((startH + me.clientY - startY) / step));
                if (!isAreaOccupied(parseInt(blockElement.dataset.hourIndex), parseInt(blockElement.dataset.dayIndex), rs, 1, blockElement, allB)) {
                    blockElement.dataset.rs = rs; this._syncBlockToSlot(blockElement);
                }
            };
            const onMU = () => { blockElement.style.zIndex = '10'; removeEventListener('mousemove', onMM); removeEventListener('mouseup', onMU); };
            addEventListener('mousemove', onMM); addEventListener('mouseup', onMU);
        });

        // East
        blockElement.querySelector('[data-r="e"]').addEventListener('mousedown', e => {
            e.stopPropagation(); e.preventDefault();
            blockElement.style.zIndex = '50';
            const startX = e.clientX, startW = blockElement.offsetWidth, originD = parseInt(blockElement.dataset.dayIndex), allB = Array.from(document.querySelectorAll('.schedule-block'));
            const hourIndex = parseInt(blockElement.dataset.hourIndex);
            const filteredBlocks = allB.filter(b => {
                const isSelf = b === blockElement;
                const isSameSession = b.dataset.hourIndex == hourIndex &&
                    (b.querySelector('p')?.textContent === primaryLabel);
                return !isSelf && !isSameSession;
            });

            let lastSpan = 1, siblings = [];
            const onMM = me => {
                const step = this._getSlotElement(0, 0)?.offsetWidth || 40;
                const targetSpan = Math.max(1, Math.round((startW + me.clientX - startX) / step));
                const h = hourIndex, rs = parseInt(blockElement.dataset.rs || "1");
                let allowed = 1;
                for (let s = 1; s < targetSpan; s++) {
                    if (originD + s >= DAY_COUNT || isAreaOccupied(h, originD + s, rs, 1, blockElement, filteredBlocks)) break;
                    allowed++;
                }
                if (allowed !== lastSpan) {
                    blockElement.style.width = `${(allowed * step) - 2}px`;
                    if (allowed > lastSpan) {
                        for (let s = lastSpan; s < allowed; s++) siblings.push(this._makeSiblingBlock(blockElement, originD + s, blockData, paletteColor, primaryLabel, secondaryLabel, teacherInitials));
                    } else {
                        for (let s = lastSpan - 1; s >= allowed; s--) if (siblings[s - 1]) { siblings[s - 1].remove(); siblings.splice(s - 1, 1); }
                    }
                    lastSpan = allowed;
                }
            };
            const onMU = () => {
                blockElement.style.zIndex = '10'; removeEventListener('mousemove', onMM); removeEventListener('mouseup', onMU);
                this._syncBlockToSlot(blockElement); siblings.forEach(s => this.undoStack.push(s));
            };
            addEventListener('mousemove', onMM); addEventListener('mouseup', onMU);
        });
    }

    refreshBlocksPositions() { document.querySelectorAll('.schedule-block').forEach(b => this._syncBlockToSlot(b)); }

    undoLast() {
        if (this.undoStack.length) {
            this.undoStack.pop().remove();
            this._renderSubjects();
        }
    }

    _formatDateLocal(dateObj) {
        const tzOffset = dateObj.getTimezoneOffset();
        const local = new Date(dateObj.getTime() - tzOffset * 60000);
        return local.toISOString().split('T')[0];
    }

    _formatHour(hour) {
        return `${String(hour).padStart(2, '0')}:00`;
    }

    _computeWeekOffsetFrom(dateStr) {
        if (!dateStr) return 0;
        const target = new Date(dateStr);
        target.setHours(0, 0, 0, 0);
        const monday0 = getMonday(0);
        const diffMs = target.getTime() - monday0.getTime();
        return Math.round(diffMs / (7 * 86400000));
    }

    _computeWeekNumber(dateStr) {
        if (!dateStr) return null;
        const d = new Date(dateStr);
        const target = new Date(Date.UTC(d.getFullYear(), d.getMonth(), d.getDate()));
        const dayNr = (target.getUTCDay() + 6) % 7;
        target.setUTCDate(target.getUTCDate() - dayNr + 3);
        const firstThursday = new Date(Date.UTC(target.getUTCFullYear(), 0, 4));
        const week = 1 + Math.round(((target - firstThursday) / 86400000 - 3) / 7);
        return week;
    }

    /**
     * Construit le tableau de séances (cours + événements) à envoyer au backend.
     * Les événements sont envoyés avec type="EVENEMENT", sans coursId ni enseignantId.
     */
    _buildSeances() {
        const startStr = getDatePickerValue('date-start');
        if (!startStr) return [];
        const startDate = new Date(startStr);

        const blocks = document.querySelectorAll('.schedule-block');
        const seances = [];

        blocks.forEach(block => {
            const blockType = block.dataset.blockType;
            const dayIndex = parseInt(block.dataset.dayIndex);
            const hourIndex = parseInt(block.dataset.hourIndex);
            const rowSpan = parseInt(block.dataset.rs || "1");

            const date = new Date(startDate);
            date.setDate(startDate.getDate() + dayIndex);
            const startHour = HOURS[hourIndex];
            const endHour = startHour + rowSpan;
            const dateSeance = this._formatDateLocal(date);
            const heureDebut = this._formatHour(startHour);
            const heureFin = this._formatHour(endHour);
            const colorId = block.dataset.colorId || null;

            if (blockType === 'teacher') {
                // Séance de cours
                const subjectId = parseInt(block.dataset.subjectId);
                const teacherId = parseInt(block.dataset.teacherId) || 1;
                if (!subjectId) return;

                const libelle = block.querySelector('p')?.textContent?.trim() || `Séance ${subjectId}`;
                seances.push({
                    libelle,
                    salle: 'Salle 1',
                    dateSeance,
                    heureDebut,
                    heureFin,
                    coursId: subjectId,
                    enseignantId: teacherId,
                    type: 'SEANCE',
                    couleur: colorId,
                    iconKey: null,
                });
            } else if (blockType === 'event') {
                // Événement (Pause, Délibération, etc.)
                const eventName = block.dataset.eventName || block.querySelector('p')?.textContent?.trim() || 'Événement';
                const iconKey = block.dataset.iconKey || 'event';
                seances.push({
                    libelle: eventName,
                    salle: null,
                    dateSeance,
                    heureDebut,
                    heureFin,
                    coursId: null,
                    enseignantId: null,
                    type: 'EVENEMENT',
                    couleur: colorId,
                    iconKey,
                });
            }
        });

        return seances;
    }

    async loadExistingEmploi(emploiId) {
        const emploi = await api.get(`/api/emplois-temps/${emploiId}`);
        if (!emploi) return;

        this.weekOffset = this._computeWeekOffsetFrom(emploi.dateDebut);
        this.renderWeek();

        // Mettre à jour les datepickers avec les vraies dates de l'emploi
        setDatePickerValue('date-start', emploi.dateDebut);
        setDatePickerValue('date-end', emploi.dateFin);

        // Placer séances ET événements
        const monday = getMonday(this.weekOffset);
        (emploi.seances || []).forEach(seance => this._placeSeanceFromData(seance, monday));
    }

    _placeSeanceFromData(seance, weekMonday) {
        if (!seance?.dateSeance || !seance?.heureDebut || !seance?.heureFin) return;

        const seanceDate = new Date(seance.dateSeance);
        const dayIndex = Math.floor((seanceDate - weekMonday) / 86400000);
        if (dayIndex < 0 || dayIndex >= DAY_COUNT) return;

        const startHour = parseInt(String(seance.heureDebut).split(':')[0]);
        const endHour = parseInt(String(seance.heureFin).split(':')[0]);
        const hourIndex = HOURS.indexOf(startHour);
        if (hourIndex === -1) return;

        const rowSpan = Math.max(1, endHour - startHour);
        const colorId = seance.couleur || null;

        if (seance.type === 'EVENEMENT') {
            // Restaurer un événement
            const iconKey = seance.iconKey || 'event';
            const blockData = {
                type: 'event',
                eventName: seance.libelle || 'Événement',
                itemKey: `ev-${seance.id || Math.random()}`,
                iconKey,
            };
            const block = this.placeBlock(hourIndex, dayIndex, blockData, colorId || 'violet');
            block.dataset.rs = String(rowSpan);
            block.dataset.eventName = seance.libelle || 'Événement';
            block.dataset.iconKey = iconKey;
            const slot = this._getSlotElement(hourIndex, dayIndex);
            if (slot) block.style.height = `${(slot.offsetHeight * rowSpan) - 2}px`;
            this._syncBlockToSlot(block);
        } else {
            // Restaurer une séance de cours
            const subjectId = seance.coursId;
            const teacherId = seance.enseignantId;
            const subject = this.SUBJECTS.find(s => s.id === subjectId);

            const blockData = {
                type: 'teacher',
                subjectId,
                teacherId,
                subjectName: subject?.name || seance.libelle || 'Séance',
                subjectCode: subject?.code || '',
                teacherName: '',
                teacherInitials: '',
                itemKey: subjectId ? `s${subjectId}` : `s-${seance.id || Math.random()}`,
            };

            const block = this.placeBlock(hourIndex, dayIndex, blockData, colorId);
            block.dataset.rs = String(rowSpan);
            const slot = this._getSlotElement(hourIndex, dayIndex);
            if (slot) block.style.height = `${(slot.offsetHeight * rowSpan) - 2}px`;
            this._syncBlockToSlot(block);
        }
    }

    async saveSchedule() {
        const dateDebut = getDatePickerValue('date-start');
        const dateFin = getDatePickerValue('date-end');
        const classeId = this.classId;

        if (!classeId || !dateDebut || !dateFin) {
            this.showToast('Classe ou dates manquantes');
            return;
        }

        const seances = this._buildSeances();
        if (!seances.length) {
            this.showToast('Ajoutez au moins une séance dans la grille');
            return;
        }

        const payload = {
            id: this.emploiId || undefined,
            dateDebut,
            dateFin,
            semaine: this._computeWeekNumber(dateDebut),
            classeId,
            seances,
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
            console.error('Erreur sauvegarde emploi du temps', error);
            const msg = error?.response?.data || 'Erreur lors de la sauvegarde';
            this.showToast(msg);
        }
    }

    async exportPDF() { await generatePDF('main-page', (msg) => this.showToast(msg)); }

    showToast(message) {
        const t = document.getElementById('toast'), tm = document.getElementById('toast-msg');
        if (!t || !tm) return; tm.textContent = message; t.classList.remove('hidden');
        if (this.toastTimer) clearTimeout(this.toastTimer); this.toastTimer = setTimeout(() => t.classList.add('hidden'), 2800);
    }
}

document.addEventListener('DOMContentLoaded', () => { window.editScheduleCtrl = new EditScheduleController(); });

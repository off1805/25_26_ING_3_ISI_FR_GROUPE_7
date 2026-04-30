import {
    HOURS, DAY_COUNT, PALETTE, SUBJECTS, ICON_SVG,
    getMonday, fmtISO, fmtShort, generatePDF, isAreaOccupied, isWithinBounds
} from '../../APInterface/editScheduleUtils.js';
import { GlobalErrorHandler } from '../../common/GlobalErrorHandler.js';

function resolveColor(colorIdOrHex, fallbackId = 'violet') {
    const isHex = typeof colorIdOrHex === 'string' && colorIdOrHex.startsWith('#') && colorIdOrHex.length === 7;
    if (isHex) return { id: colorIdOrHex, bg: `${colorIdOrHex}1f`, border: colorIdOrHex, text: colorIdOrHex };
    return PALETTE.find(p => p.id === colorIdOrHex) || PALETTE.find(p => p.id === fallbackId) || PALETTE[0];
}

function getDatePickerValue(inputId) {
    const el = document.getElementById(inputId);
    if (!el) return '';
    return el.dataset.hsDatepickerValue || el.value || '';
}

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

export class EditScheduleController {
    constructor(loadClassSubjectsUC, getEmploiTempsUC, saveEmploiTempsUC) {
        this._loadClassSubjectsUC = loadClassSubjectsUC;
        this._getEmploiTempsUC    = getEmploiTempsUC;
        this._saveEmploiTempsUC   = saveEmploiTempsUC;

        this.EVENTS = [
            { id: 'e1', name: 'Pause',           iconKey: 'pause', defaultColor: 'yellow' },
            { id: 'e2', name: 'Temps personnel',  iconKey: 'perso', defaultColor: 'blue'   },
        ];
        this.SUBJECTS        = [];
        this.selectedColors  = {};
        this.weekOffset      = 0;
        this.emploiId        = null;
        this.slotHeight      = 40;
        this.undoStack       = [];
        this.activeDragData  = null;
        this.popoverKey      = null;
        this._pendingDrop    = null;

        this.EVENTS.forEach(e => (this.selectedColors[e.id] = e.defaultColor));
        this.init().catch(e => console.error(e));
    }

    async init() {
        const config = window.scheduleConfig || {};
        const params = new URLSearchParams(window.location.search);

        this.emploiId      = config.emploiId  || (params.get('id')        ? parseInt(params.get('id'))        : null);
        this.classId       = config.classId   || (params.get('classId')   ? parseInt(params.get('classId'))   : null);
        this.fixedStartDate = config.dateDebut || params.get('dateDebut');
        this.fixedEndDate   = config.dateFin   || params.get('dateFin');
        this.semaine        = config.semaine   || params.get('semaine');

        const label = document.getElementById('edit-class-label');
        if (label) label.textContent = config.className || 'Nouvelle classe';

        if (this.fixedStartDate) {
            this.weekOffset = this._computeWeekOffsetFrom(this.fixedStartDate);
            this.rangeStart = new Date(this.fixedStartDate);
            this.rangeStart.setHours(0, 0, 0, 0);
        }
        if (this.fixedEndDate) {
            this.rangeEnd = new Date(this.fixedEndDate);
            this.rangeEnd.setHours(23, 59, 59, 999);
        }

        const viewEdit = document.getElementById('view-edit');
        if (viewEdit) { viewEdit.classList.remove('hidden'); viewEdit.classList.add('flex'); }

        if (this.classId) {
            this.SUBJECTS = await this._loadClassSubjectsUC.execute(this.classId);
        } else {
            this.SUBJECTS = SUBJECTS;
        }
        this.SUBJECTS.forEach(s => { this.selectedColors[`s${s.id}`] = s.defaultColor || 'blue'; });

        this.renderPanel();
        this._bindControls();
        this._bindSubjectDropModal();
        this._bindSubjectSearch();

        window.__onDateStartChange = val => this.handleDatePickerChange(val);
        window.__onDateEndChange   = () => {};

        window.addEventListener('resize', () => {
            this.recalcSlotHeight();
            this.buildGrid();
            this.refreshBlocksPositions();
        });

        this.renderWeek();

        if (this.emploiId) {
            this.loadExistingEmploi(this.emploiId).catch(e => console.error('Erreur chargement emploi existant', e));
        }
    }

    _bindSubjectSearch() {
        document.getElementById('subject-search')
            ?.addEventListener('input', () => this._renderSubjects());
    }

    _bindControls() {
        document.getElementById('btn-exit')?.addEventListener('click', () => this.exitEditMode());
        document.getElementById('btn-undo')?.addEventListener('click', () => this.undoLast());
        document.getElementById('btn-export-pdf')?.addEventListener('click', () => this.exportPDF());
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

    _bindSubjectDropModal() {
        const modal = document.getElementById('modal-subject-drop');
        if (!modal) return;
        document.getElementById('btn-confirm-subject-drop')?.addEventListener('click', () => this._confirmSubjectDrop());
        document.getElementById('btn-cancel-subject-drop')?.addEventListener('click', () => {
            this.hideModal('modal-subject-drop');
            this._pendingDrop = null;
        });
        modal.querySelectorAll('.btn-close-modal').forEach(btn =>
            btn.addEventListener('click', () => { this.hideModal('modal-subject-drop'); this._pendingDrop = null; })
        );
    }

    _openSubjectDropModal(hourIndex, dayIndex, subject) {
        this._pendingDrop = { hourIndex, dayIndex, subject };
        const teacherSelect = document.getElementById('drop-teacher-select');
        if (teacherSelect) {
            teacherSelect.innerHTML = subject.teachers.length
                ? subject.teachers.map(t => `<option value="${t.id}">${t.name}</option>`).join('')
                : `<option value="1">Enseignant par défaut (id 1)</option>`;
        }
        const currentColorId = this.selectedColors[`s${subject.id}`] || subject.defaultColor;
        const colorGrid = document.getElementById('drop-color-grid');
        if (colorGrid) {
            colorGrid.innerHTML = PALETTE.map(c => `
                <button type="button" class="drop-color-btn size-8 rounded-xl border-2 hover:scale-110 transition-all ${currentColorId === c.id ? 'ring-2 ring-offset-1 ring-primary shadow-sm' : ''}"
                    data-color="${c.id}" title="${c.id}" style="background:${c.bg};border-color:${c.border}"></button>`
            ).join('');
            colorGrid.querySelectorAll('.drop-color-btn').forEach(btn => {
                btn.addEventListener('click', () => {
                    colorGrid.querySelectorAll('.drop-color-btn').forEach(b => b.classList.remove('ring-2', 'ring-offset-1', 'ring-primary', 'shadow-sm'));
                    btn.classList.add('ring-2', 'ring-offset-1', 'ring-primary', 'shadow-sm');
                    if (this._pendingDrop) this._pendingDrop.colorId = btn.dataset.color;
                });
            });
        }
        this._pendingDrop.colorId = currentColorId;
        this.showModal('modal-subject-drop');
    }

    _confirmSubjectDrop() {
        if (!this._pendingDrop) return;
        const { hourIndex, dayIndex, subject, colorId } = this._pendingDrop;
        const teacherSelect = document.getElementById('drop-teacher-select');
        const teacherId = teacherSelect ? parseInt(teacherSelect.value) || 1 : (subject.teachers[0]?.id || 1);
        const teacher   = subject.teachers.find(t => t.id === teacherId) || subject.teachers[0] || { id: 1, name: 'Enseignant', initials: 'PR' };
        this.selectedColors[`s${subject.id}`] = colorId;
        this._refreshSubjectCard(subject.id, colorId);
        this.hideModal('modal-subject-drop');
        this._pendingDrop = null;
        this.undoStack.push(this.placeBlock(hourIndex, dayIndex, {
            type: 'teacher', subjectId: subject.id, subjectName: subject.name, subjectCode: subject.code,
            teacherId, teacherName: teacher?.name || 'Enseignant', teacherInitials: teacher?.initials || 'PR',
            itemKey: `s${subject.id}`, fromBlock: false,
        }, colorId));
    }

    _refreshSubjectCard(subjectId, colorId) {
        const c = resolveColor(colorId);
        const sw = document.getElementById(`swatch-s${subjectId}`);
        if (sw) { sw.style.background = c.bg; sw.style.borderColor = c.border; }
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

    showModal(id) {
        const el = document.getElementById(id);
        if (!el) return;
        el.classList.remove('hidden'); el.classList.add('flex');
    }
    hideModal(id) {
        const el = document.getElementById(id);
        if (!el) return;
        el.classList.add('hidden'); el.classList.remove('flex');
    }

    openColorPicker(itemKey, anchorEl, ev) {
        ev.stopPropagation();
        const pop = document.getElementById('color-popover');
        if (this.popoverKey === itemKey && !pop.classList.contains('hidden')) {
            pop.classList.add('hidden'); this.popoverKey = null; return;
        }
        this.popoverKey = itemKey;
        document.getElementById('color-popover-grid').innerHTML = PALETTE.map(c =>
            `<button class="color-btn size-8 rounded-xl border-2 hover:scale-110 transition-all ${this.selectedColors[itemKey] === c.id ? 'ring-2 ring-offset-1 ring-primary shadow-sm' : ''}"
                data-color="${c.id}" data-key="${itemKey}" title="${c.id}" style="background:${c.bg};border-color:${c.border}"></button>`
        ).join('');
        document.querySelectorAll('.color-btn').forEach(btn =>
            btn.addEventListener('click', ev2 => this.setColor(ev2.currentTarget.dataset.key, ev2.currentTarget.dataset.color, ev2.currentTarget))
        );
        const r = anchorEl.getBoundingClientRect();
        pop.style.top  = `${r.bottom + 8}px`;
        pop.style.left = `${Math.min(r.left - 60, window.innerWidth - 172)}px`;
        pop.classList.remove('hidden');
    }

    setColor(itemKey, colorId, btn) {
        this.selectedColors[itemKey] = colorId;
        btn.closest('#color-popover-grid').querySelectorAll('button')
            .forEach(b => b.classList.remove('ring-2', 'ring-offset-1', 'ring-primary', 'shadow-sm'));
        btn.classList.add('ring-2', 'ring-offset-1', 'ring-primary', 'shadow-sm');
        const c  = PALETTE.find(p => p.id === colorId);
        const sw = document.getElementById(`swatch-${itemKey}`);
        if (sw && c) { sw.style.background = c.bg; sw.style.borderColor = c.border; }
    }

    renderPanel() { this._renderSubjects(); this._renderEvents(); }

    _getScheduledSubjectKeySet() {
        const set = new Set();
        document.querySelectorAll('.schedule-block[data-item-key]').forEach(b => {
            const k = b.dataset.itemKey || '';
            if (k.startsWith('s')) set.add(k);
        });
        return set;
    }

    _matchesSubjectQuery(s, q) {
        if (!q) return true;
        const lq = q.toLowerCase();
        return String(s?.name || '').toLowerCase().includes(lq) || String(s?.code || '').toLowerCase().includes(lq);
    }

    _renderSubjectList(containerId, subjects, scheduledKeys) {
        const el = document.getElementById(containerId);
        if (!el) return;
        if (!subjects.length) {
            el.innerHTML = `<div class="px-3 py-3 text-sm text-muted-foreground-2">Aucune matière.</div>`;
            return;
        }
        el.innerHTML = subjects.map(s => {
            const c = resolveColor(this.selectedColors[`s${s.id}`] || s.defaultColor);
            const scheduled = scheduledKeys?.has(`s${s.id}`);
            return `
            <a class="subject-card group flex flex-col bg-layer rounded-xl hover:bg-layer-hover px-2 focus:outline-hidden focus:shadow-md transition cursor-grab active:cursor-grabbing"
               draggable="true" data-type="subject" data-subject-id="${s.id}" data-subject-name="${s.name}"
               data-subject-code="${s.code}" data-item-key="s${s.id}" href="#">
                <div class="py-2">
                    <div class="flex gap-x-3">
                        <div class="mt-0.5 relative shrink-0 size-8 rounded-lg flex items-center justify-center" style="background:${c.bg};color:${c.border}">
                            ${ICON_SVG.books}
                            <span class="inline-flex absolute bottom-1 translate-y-1/2 right-1 translate-x-1/2 items-center size-2 rounded-full ${scheduled ? 'bg-emerald-500' : 'bg-muted'}"></span>
                        </div>
                        <div class="grow min-w-0">
                            <h3 class="group-hover:text-primary font-semibold text-foreground text-[13px] leading-tight truncate">${s.name}</h3>
                            <p class="text-xs text-muted-foreground-1 mt-0.5 truncate">${s.code}</p>
                        </div>
                    </div>
                </div>
            </a>`;
        }).join('');
        el.querySelectorAll('.subject-card').forEach(el2 => {
            el2.addEventListener('dragstart', ev => this.onDragStart(ev, el2));
            el2.addEventListener('dragend',   () => this.onDragEnd(el2));
        });
    }

    _renderSubjects() {
        const q = (document.getElementById('subject-search')?.value || '').trim();
        const scheduled = this._getScheduledSubjectKeySet();
        const all = this.SUBJECTS.filter(s => this._matchesSubjectQuery(s, q));
        this._renderSubjectList('subject-list-all',          all,                                  scheduled);
        this._renderSubjectList('subject-list-unscheduled',  all.filter(s => !scheduled.has(`s${s.id}`)), scheduled);
        this._renderSubjectList('subject-list-scheduled',    all.filter(s =>  scheduled.has(`s${s.id}`)), scheduled);
    }

    _renderEvents() {
        const el = document.getElementById('event-list');
        if (!el) return;
        el.innerHTML = this.EVENTS.map(e => {
            const c = resolveColor(this.selectedColors[e.id] || e.defaultColor);
            return `
            <div draggable="true" data-type="event" data-event-name="${e.name}" data-item-key="${e.id}" data-icon-key="${e.iconKey}"
                 class="event-item flex items-center gap-2 px-3 py-2 bg-layer text-layer-foreground rounded-xl hover:bg-layer-hover transition-all cursor-grab active:cursor-grabbing text-[12px] font-bold">
                <div class="size-8 rounded-lg flex items-center justify-center shrink-0" style="background:${c.bg};color:${c.border}">
                    ${ICON_SVG[e.iconKey] || ICON_SVG.event}
                </div>
                <span class="truncate">${e.name}</span>
            </div>`;
        }).join('');
        el.querySelectorAll('.event-item').forEach(el2 => {
            el2.addEventListener('dragstart', ev => this.onDragStart(ev, el2));
            el2.addEventListener('dragend',   () => this.onDragEnd(el2));
        });
    }

    renderWeek() {
        const monday = getMonday(this.weekOffset);
        const days   = Array.from({ length: DAY_COUNT }, (_, i) => {
            const d = new Date(monday); d.setDate(monday.getDate() + i); return d;
        });
        this.activeDays = days.map(day => {
            if (!this.rangeStart || !this.rangeEnd) return true;
            const d = new Date(day); d.setHours(12, 0, 0, 0);
            const s = new Date(this.rangeStart); s.setHours(0, 0, 0, 0);
            const e = new Date(this.rangeEnd);   e.setHours(23, 59, 59, 999);
            return d >= s && d <= e;
        });
        setDatePickerValue('date-start', this._formatDateLocal(days[0]));
        setDatePickerValue('date-end',   this._formatDateLocal(days[5]));
        const wl = document.getElementById('week-label');
        if (wl) wl.textContent = `${fmtShort(days[0])} – ${fmtShort(days[5])}`;
        const heads = document.getElementById('day-headers');
        if (!heads) return;
        heads.style.gridTemplateColumns = `${this.timeColumnWidth}px repeat(${DAY_COUNT},${this.dayColumnWidth()})`;
        heads.innerHTML = `<div class="py-2 text-xs text-center text-muted-foreground-2 font-bold border-r-2 border-card-line sticky left-0 bg-card z-30">Horaires</div>`;
        days.forEach(d => {
            const name    = d.toLocaleDateString('fr-FR', { weekday: 'short' }).slice(0, 3).toUpperCase();
            const isToday = d.toDateString() === new Date().toDateString();
            heads.innerHTML += `<div class="cell py-1.5 text-center border-l border-card-line bg-card">
                <p class="text-[10px] font-bold text-muted-foreground-2">${name}</p>
                <p class="text-sm font-extrabold ${isToday ? 'text-primary' : 'text-layer-foreground'}">${d.getDate()}</p>
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
        const first = document.getElementById('schedule-body')?.children[0];
        if (first) this.slotHeight = first.getBoundingClientRect().height;
    }

    get timeColumnWidth() { return 72; }
    dayColumnWidth() { return '1fr'; }

    buildGrid() {
        const body = document.getElementById('schedule-body');
        if (!body) return;
        body.style.gridTemplateColumns = `${this.timeColumnWidth}px repeat(${DAY_COUNT},${this.dayColumnWidth()})`;
        body.style.gridTemplateRows    = `repeat(${HOURS.length}, 1fr)`;
        let html = '';
        HOURS.forEach((hour, hi) => {
            html += `<div style="grid-column:1;grid-row:${hi + 1}" class="time-cell cell flex items-center justify-center border-b border-r-2 border-card-line bg-card sticky left-0 z-20">
                       <span class="text-[11px] font-mono font-semibold text-muted-foreground-2 whitespace-nowrap">${hour}h–${hour + 1}h</span>
                     </div>`;
            for (let di = 0; di < DAY_COUNT; di++) {
                const active  = this.activeDays ? this.activeDays[di] : true;
                const disabled = active ? '' : 'bg-muted/40 opacity-40 cursor-not-allowed';
                html += `<div style="grid-column:${di + 2};grid-row:${hi + 1}" class="cell slot border-b border-card-line border-l bg-card/80 transition-colors ${disabled}"
                              data-hour-index="${hi}" data-day-index="${di}" ${active ? '' : 'data-disabled="true"'}></div>`;
            }
        });
        body.innerHTML = html;
        body.querySelectorAll('.slot').forEach(sl => {
            sl.addEventListener('dragover', ev => this.onSlotDragOver(ev, sl));
            sl.addEventListener('dragleave', () => this.onSlotDragLeave(sl));
            sl.addEventListener('drop', ev => this.onSlotDrop(ev, sl));
        });
        this.undoStack = [];
    }

    _getSlotElement(hi, di) {
        return document.querySelector(`.slot[data-hour-index="${hi}"][data-day-index="${di}"]`);
    }

    _syncBlockToSlot(block) {
        const hi   = parseInt(block.dataset.hourIndex);
        const di   = parseInt(block.dataset.dayIndex);
        const rs   = parseInt(block.dataset.rs || '1');
        const slot = this._getSlotElement(hi, di);
        if (slot) {
            block.style.left   = `${slot.offsetLeft + 1}px`;
            block.style.top    = `${slot.offsetTop  + 1}px`;
            block.style.width  = `${slot.offsetWidth  - 2}px`;
            block.style.height = `${(slot.offsetHeight * rs) - 2}px`;
        }
    }

    onDragStart(ev, el) {
        this.activeDragData = {
            type: el.dataset.type, subjectId: el.dataset.subjectId ? parseInt(el.dataset.subjectId) : null,
            subjectName: el.dataset.subjectName || '', subjectCode: el.dataset.subjectCode || '',
            teacherName: el.dataset.teacherName || '', eventName: el.dataset.eventName || '',
            itemKey: el.dataset.itemKey, iconKey: el.dataset.iconKey || '',
            fromBlock: false, rowSpan: 1,
        };
        ev.dataTransfer.effectAllowed = 'copy';
        el.style.opacity = '.4';
    }
    onDragEnd(el) { el.style.opacity = ''; this._clearDragHighlights(); }

    onSlotDragOver(ev, sl) {
        ev.preventDefault();
        if (!this.activeDragData) return;
        const h = parseInt(sl.dataset.hourIndex), d = parseInt(sl.dataset.dayIndex);
        const rs = this.activeDragData.rowSpan || 1;
        const blockEl = this.activeDragData.fromBlock ? this.activeDragData.blockEl : null;
        const ok = isWithinBounds(h, d, rs, 1) && !isAreaOccupied(h, d, rs, 1, blockEl) && this._isRangeActive(h, d, rs);
        this._clearDragHighlights();
        for (let i = 0; i < rs; i++) {
            const slot = this._getSlotElement(h + i, d);
            if (slot) slot.classList.add('drag-highlight', ok ? '!bg-primary/20' : '!bg-red-500/20');
        }
    }
    onSlotDragLeave(sl) { sl.classList.remove('drag-highlight', '!bg-primary/20', '!bg-red-500/20'); }
    _isRangeActive(h, d, rs) { return this.activeDays ? this.activeDays[d] : true; }
    _clearDragHighlights() {
        document.querySelectorAll('.drag-highlight').forEach(el =>
            el.classList.remove('drag-highlight', '!bg-primary/20', '!bg-red-500/20')
        );
    }

    onSlotDrop(ev, sl) {
        ev.preventDefault();
        this._clearDragHighlights();
        if (!this.activeDragData) return;
        const hi = parseInt(sl.dataset.hourIndex), di = parseInt(sl.dataset.dayIndex);
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

    placeBlock(hi, di, blockData, forcedColorId = null) {
        const colorId = forcedColorId || this.selectedColors[blockData.itemKey] || 'violet';
        const c       = resolveColor(colorId);
        const primary   = blockData.type === 'teacher' ? blockData.subjectName : blockData.eventName;
        const secondary = blockData.type === 'teacher' ? blockData.teacherName : null;
        const initials  = blockData.teacherInitials || (blockData.teacherName?.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase()) || 'UE';

        const block = document.createElement('div');
        block.className = 'group relative schedule-block shadow-sm';
        block.dataset.hourIndex = hi; block.dataset.dayIndex = di; block.dataset.rs = '1';
        if (blockData?.itemKey)    block.dataset.itemKey   = blockData.itemKey;
        if (blockData?.subjectId != null) block.dataset.subjectId = String(blockData.subjectId);
        if (blockData?.teacherId != null) block.dataset.teacherId = String(blockData.teacherId);
        if (blockData?.type)       block.dataset.blockType = blockData.type;
        if (colorId)               block.dataset.colorId   = colorId;
        if (blockData?.iconKey)    block.dataset.iconKey   = blockData.iconKey;
        if (blockData?.eventName)  block.dataset.eventName = blockData.eventName;

        Object.assign(block.style, {
            position: 'absolute', background: c.bg, border: `1.5px solid ${c.border}`,
            borderLeftWidth: '4px', color: c.text, borderRadius: '12px',
            padding: '8px', pointerEvents: 'all', zIndex: '10', cursor: 'grab', userSelect: 'none',
        });
        this._syncBlockToSlot(block);

        block.innerHTML = `
            <div class="flex items-start gap-2 h-full w-full overflow-hidden">
                <div class="size-7 rounded-lg shrink-0 flex items-center justify-center bg-white/50" style="color:${c.border}">
                    ${blockData.type === 'event' ? (ICON_SVG[blockData.iconKey] || ICON_SVG.event) : `<span class="text-[10px] font-bold">${initials}</span>`}
                </div>
                <div class="flex-1 min-w-0 px-1" style="z-index:10">
                    <p class="font-bold text-[13px] leading-tight truncate">${primary}</p>
                    ${secondary ? `<p class="text-[11px] opacity-80 truncate font-medium mt-0.5">${secondary}</p>` : ''}
                </div>
            </div>
            <div class="absolute top-1.5 right-1.5 opacity-0 group-hover:opacity-100 transition-opacity" style="z-index:30">
                <button type="button" class="btn-delete-block size-6 flex items-center justify-center rounded-md bg-white/90 hover:bg-red-50 hover:text-red-500 text-foreground shadow-sm transition-colors">
                    <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2M10 11v6M14 11v6"/></svg>
                </button>
            </div>
            <div data-r="s" class="absolute bottom-0 left-0 right-0 h-2 cursor-s-resize hover:bg-black/5 rounded-b-xl z-20"></div>
            <div data-r="e" class="absolute top-0 bottom-0 right-0 w-2 cursor-e-resize hover:bg-black/5 rounded-r-xl z-20"></div>`;

        block.querySelector('.btn-delete-block').addEventListener('click', () => {
            block.remove(); this.undoStack = this.undoStack.filter(b => b !== block); this._renderSubjects();
        });
        block.setAttribute('draggable', 'true');
        block.addEventListener('dragstart', ev => {
            if (ev.target.dataset.r) { ev.preventDefault(); return; }
            this.activeDragData = { fromBlock: true, blockEl: block, rowSpan: parseInt(block.dataset.rs || '1') };
            ev.dataTransfer.effectAllowed = 'move';
            setTimeout(() => (block.style.opacity = '.35'), 0);
        });
        block.addEventListener('dragend', () => this.onDragEnd(block));
        this._bindResizers(block, blockData, c, primary, secondary, initials);
        document.getElementById('blocks-layer').appendChild(block);
        this._renderSubjects();
        return block;
    }

    _makeSiblingBlock(orig, sibDi, blockData, c, primary, secondary, initials) {
        const sib = document.createElement('div');
        sib.className = 'group relative schedule-block shadow-sm';
        sib.dataset.hourIndex = orig.dataset.hourIndex; sib.dataset.dayIndex = sibDi;
        sib.dataset.rs = orig.dataset.rs || '1'; sib.dataset.isSibling = 'true';
        ['itemKey','subjectId','teacherId','blockType','colorId','iconKey','eventName'].forEach(k => {
            if (orig.dataset[k]) sib.dataset[k] = orig.dataset[k];
        });
        sib.style.cssText = orig.style.cssText; sib.style.width = orig.style.width;
        this._syncBlockToSlot(sib);
        sib.innerHTML = `
            <div class="flex items-start gap-2 h-full w-full overflow-hidden">
                <div class="size-7 rounded-lg shrink-0 flex items-center justify-center bg-white/50" style="color:${c.border}">
                    ${blockData.type === 'event' ? (ICON_SVG[blockData.iconKey] || ICON_SVG.event) : `<span class="text-[10px] font-bold">${initials}</span>`}
                </div>
                <div class="flex-1 min-w-0 px-1" style="z-index:10">
                    <p class="font-bold text-[13px] leading-tight truncate">${primary}</p>
                    ${secondary ? `<p class="text-[11px] opacity-80 truncate font-medium mt-0.5">${secondary}</p>` : ''}
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
            setTimeout(() => (sib.style.opacity = '.35'), 0);
        });
        sib.addEventListener('dragend', () => this.onDragEnd(sib));
        this._bindResizers(sib, blockData, c, primary, secondary, initials);
        document.getElementById('blocks-layer').appendChild(sib);
        this._renderSubjects();
        return sib;
    }

    _bindResizers(block, blockData, c, primary, secondary, initials) {
        block.querySelector('[data-r="s"]').addEventListener('mousedown', e => {
            e.stopPropagation(); e.preventDefault();
            block.style.zIndex = '50';
            const startY = e.clientY, startH = block.offsetHeight, allB = Array.from(document.querySelectorAll('.schedule-block'));
            const onMM = me => {
                const step = this._getSlotElement(0, 0)?.offsetHeight || 40;
                const rs = Math.max(1, Math.round((startH + me.clientY - startY) / step));
                if (!isAreaOccupied(parseInt(block.dataset.hourIndex), parseInt(block.dataset.dayIndex), rs, 1, block, allB)) {
                    block.dataset.rs = rs; this._syncBlockToSlot(block);
                }
            };
            const onMU = () => { block.style.zIndex = '10'; removeEventListener('mousemove', onMM); removeEventListener('mouseup', onMU); };
            addEventListener('mousemove', onMM); addEventListener('mouseup', onMU);
        });
        block.querySelector('[data-r="e"]').addEventListener('mousedown', e => {
            e.stopPropagation(); e.preventDefault();
            block.style.zIndex = '50';
            const startX = e.clientX, startW = block.offsetWidth, origDi = parseInt(block.dataset.dayIndex);
            const hi = parseInt(block.dataset.hourIndex);
            const allB = Array.from(document.querySelectorAll('.schedule-block')).filter(b => {
                return b !== block && !(b.dataset.hourIndex == hi && b.querySelector('p')?.textContent === primary);
            });
            let lastSpan = 1, siblings = [];
            const onMM = me => {
                const step = this._getSlotElement(0, 0)?.offsetWidth || 40;
                const target = Math.max(1, Math.round((startW + me.clientX - startX) / step));
                const rs = parseInt(block.dataset.rs || '1');
                let allowed = 1;
                for (let s = 1; s < target; s++) {
                    if (origDi + s >= DAY_COUNT || isAreaOccupied(hi, origDi + s, rs, 1, block, allB)) break;
                    allowed++;
                }
                if (allowed !== lastSpan) {
                    block.style.width = `${(allowed * step) - 2}px`;
                    if (allowed > lastSpan) {
                        for (let s = lastSpan; s < allowed; s++) siblings.push(this._makeSiblingBlock(block, origDi + s, blockData, c, primary, secondary, initials));
                    } else {
                        for (let s = lastSpan - 1; s >= allowed; s--) if (siblings[s - 1]) { siblings[s - 1].remove(); siblings.splice(s - 1, 1); }
                    }
                    lastSpan = allowed;
                }
            };
            const onMU = () => {
                block.style.zIndex = '10'; removeEventListener('mousemove', onMM); removeEventListener('mouseup', onMU);
                this._syncBlockToSlot(block); siblings.forEach(s => this.undoStack.push(s));
            };
            addEventListener('mousemove', onMM); addEventListener('mouseup', onMU);
        });
    }

    refreshBlocksPositions() { document.querySelectorAll('.schedule-block').forEach(b => this._syncBlockToSlot(b)); }
    undoLast() { if (this.undoStack.length) { this.undoStack.pop().remove(); this._renderSubjects(); } }

    _formatDateLocal(d) {
        const local = new Date(d.getTime() - d.getTimezoneOffset() * 60000);
        return local.toISOString().split('T')[0];
    }
    _formatHour(h)   { return `${String(h).padStart(2, '0')}:00`; }

    _computeWeekOffsetFrom(dateStr) {
        if (!dateStr) return 0;
        const t = new Date(dateStr); t.setHours(0, 0, 0, 0);
        return Math.round((t.getTime() - getMonday(0).getTime()) / (7 * 86400000));
    }

    _computeWeekNumber(dateStr) {
        if (!dateStr) return null;
        const d = new Date(dateStr);
        const t = new Date(Date.UTC(d.getFullYear(), d.getMonth(), d.getDate()));
        t.setUTCDate(t.getUTCDate() - (t.getUTCDay() + 6) % 7 + 3);
        const ft = new Date(Date.UTC(t.getUTCFullYear(), 0, 4));
        return 1 + Math.round(((t - ft) / 86400000 - 3) / 7);
    }

    _buildSeances() {
        const startStr = this.fixedStartDate || getDatePickerValue('date-start');
        if (!startStr) return [];
        const startDate = new Date(startStr);
        return Array.from(document.querySelectorAll('.schedule-block')).flatMap(block => {
            const bType    = block.dataset.blockType;
            const di       = parseInt(block.dataset.dayIndex);
            const hi       = parseInt(block.dataset.hourIndex);
            const rs       = parseInt(block.dataset.rs || '1');
            const date     = new Date(startDate); date.setDate(startDate.getDate() + di);
            const startH   = HOURS[hi], endH = startH + rs;
            const colorId  = block.dataset.colorId || null;
            const dateSeance = this._formatDateLocal(date);
            if (bType === 'teacher') {
                const subjectId = parseInt(block.dataset.subjectId);
                if (!subjectId) return [];
                return [{ libelle: block.querySelector('p')?.textContent?.trim() || `Séance ${subjectId}`,
                           salle: 'Salle 1', dateSeance,
                           heureDebut: this._formatHour(startH), heureFin: this._formatHour(endH),
                           coursId: subjectId, enseignantId: parseInt(block.dataset.teacherId) || 1,
                           type: 'SEANCE', couleur: colorId, iconKey: null }];
            } else if (bType === 'event') {
                return [{ libelle: block.dataset.eventName || block.querySelector('p')?.textContent?.trim() || 'Événement',
                           salle: null, dateSeance,
                           heureDebut: this._formatHour(startH), heureFin: this._formatHour(endH),
                           coursId: null, enseignantId: null,
                           type: 'EVENEMENT', couleur: colorId, iconKey: block.dataset.iconKey || 'event' }];
            }
            return [];
        });
    }

    async loadExistingEmploi(emploiId) {
        const emploi = await this._getEmploiTempsUC.execute(emploiId);
        if (!emploi) return;
        this.weekOffset = this._computeWeekOffsetFrom(emploi.dateDebut);
        this.renderWeek();
        setDatePickerValue('date-start', emploi.dateDebut);
        setDatePickerValue('date-end',   emploi.dateFin);
        const monday = getMonday(this.weekOffset);
        (emploi.seances || []).forEach(s => this._placeSeanceFromData(s, monday));
    }

    _placeSeanceFromData(seance, monday) {
        if (!seance?.dateSeance || !seance?.heureDebut || !seance?.heureFin) return;
        const di = Math.floor((new Date(seance.dateSeance) - monday) / 86400000);
        if (di < 0 || di >= DAY_COUNT) return;
        const startH  = parseInt(String(seance.heureDebut).split(':')[0]);
        const endH    = parseInt(String(seance.heureFin).split(':')[0]);
        const hi      = HOURS.indexOf(startH);
        if (hi === -1) return;
        const rs      = Math.max(1, endH - startH);
        const colorId = seance.couleur || null;
        if (seance.type === 'EVENEMENT') {
            const block = this.placeBlock(hi, di, {
                type: 'event', eventName: seance.libelle || 'Événement',
                itemKey: `ev-${seance.id || Math.random()}`, iconKey: seance.iconKey || 'event',
            }, colorId || 'violet');
            block.dataset.rs = String(rs); block.dataset.eventName = seance.libelle || 'Événement'; block.dataset.iconKey = seance.iconKey || 'event';
            const slot = this._getSlotElement(hi, di);
            if (slot) block.style.height = `${(slot.offsetHeight * rs) - 2}px`;
            this._syncBlockToSlot(block);
        } else {
            const subject = this.SUBJECTS.find(s => s.id === seance.coursId);
            const block   = this.placeBlock(hi, di, {
                type: 'teacher', subjectId: seance.coursId, teacherId: seance.enseignantId,
                subjectName: subject?.name || seance.libelle || 'Séance', subjectCode: subject?.code || '',
                teacherName: '', teacherInitials: '', itemKey: seance.coursId ? `s${seance.coursId}` : `s-${seance.id || Math.random()}`,
            }, colorId);
            block.dataset.rs = String(rs);
            const slot = this._getSlotElement(hi, di);
            if (slot) block.style.height = `${(slot.offsetHeight * rs) - 2}px`;
            this._syncBlockToSlot(block);
        }
    }

    async saveSchedule() {
        const dateDebut = this.fixedStartDate || getDatePickerValue('date-start');
        const dateFin   = this.fixedEndDate   || getDatePickerValue('date-end');
        if (!this.classId || !dateDebut || !dateFin) { this.showToast('Classe ou dates manquantes'); return; }
        const seances = this._buildSeances();
        if (!seances.length) { this.showToast('Ajoutez au moins une séance dans la grille'); return; }
        try {
            await this._saveEmploiTempsUC.execute({
                id: this.emploiId || undefined,
                dateDebut, dateFin,
                semaine:  this.semaine || this._computeWeekNumber(dateDebut),
                classeId: this.classId,
                seances,
            });
            this.showToast(this.emploiId ? 'Emploi du temps mis à jour ✓' : 'Emploi du temps sauvegardé ✓');
        } catch (error) {
            GlobalErrorHandler.handle(error);
            this.showToast(error?.response?.data || 'Erreur lors de la sauvegarde');
        }
    }

    async exportPDF() { await generatePDF('main-page', msg => this.showToast(msg)); }

    showToast(message) {
        const t = document.getElementById('toast'), tm = document.getElementById('toast-msg');
        if (!t || !tm) return;
        tm.textContent = message; t.classList.remove('hidden');
        if (this.toastTimer) clearTimeout(this.toastTimer);
        this.toastTimer = setTimeout(() => t.classList.add('hidden'), 2800);
    }
}

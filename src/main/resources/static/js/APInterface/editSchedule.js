q// ═══════════════════════════════════════
//  DATA
// ═══════════════════════════════════════
const HOURS = [7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18];
const DAY_COUNT = 6;

const PALETTE = [
    { id: 'violet', bg: '#ede9fe', border: '#7c3aed', text: '#4c1d95' },
    { id: 'blue', bg: '#dbeafe', border: '#3b82f6', text: '#1e3a8a' },
    { id: 'green', bg: '#d1fae5', border: '#10b981', text: '#065f46' },
    { id: 'orange', bg: '#ffedd5', border: '#f97316', text: '#9a3412' },
    { id: 'pink', bg: '#fce7f3', border: '#ec4899', text: '#831843' },
    { id: 'teal', bg: '#ccfbf1', border: '#14b8a6', text: '#134e4a' },
    { id: 'yellow', bg: '#fef9c3', border: '#eab308', text: '#713f12' },
    { id: 'red', bg: '#fee2e2', border: '#ef4444', text: '#7f1d1d' },
];

const SUBJECTS = [
    { id: 1, name: 'Algorithmique', code: 'INF301', defaultColor: 'violet', teachers: [{ id: 1, name: 'Dr. Kamga Paul', initials: 'KP' }, { id: 2, name: 'Prof. Nkemeni Alice', initials: 'NA' }] },
    { id: 2, name: 'Structures de données', code: 'INF302', defaultColor: 'blue', teachers: [{ id: 3, name: 'Dr. Fouda Martin', initials: 'FM' }, { id: 4, name: 'Mme. Biya Sandra', initials: 'BS' }] },
    { id: 3, name: 'Bases de données', code: 'INF401', defaultColor: 'green', teachers: [{ id: 5, name: 'Prof. Ela Roger', initials: 'ER' }, { id: 6, name: 'Dr. Mbarga Celine', initials: 'MC' }] },
    { id: 4, name: 'Réseaux info.', code: 'RSX201', defaultColor: 'orange', teachers: [{ id: 7, name: 'Dr. Abanda Serge', initials: 'AS' }, { id: 8, name: 'M. Njoya Victor', initials: 'NV' }] },
    { id: 5, name: 'POO', code: 'INF303', defaultColor: 'pink', teachers: [{ id: 9, name: 'Mme. Ngono Estelle', initials: 'NE' }, { id: 10, name: 'Dr. Beyala Jules', initials: 'BJ' }] },
    { id: 6, name: 'Génie logiciel', code: 'INF501', defaultColor: 'teal', teachers: [{ id: 11, name: 'Prof. Mbouda Henri', initials: 'MH' }, { id: 12, name: 'Dr. Ngo Bum Claire', initials: 'NC' }] },
];

const ICON_SVG = {
    pause: `<svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 8h1a4 4 0 0 1 0 8h-1"/><path d="M2 8h16v9a4 4 0 0 1-4 4H6a4 4 0 0 1-4-4V8z"/><line x1="6" y1="1" x2="6" y2="4"/><line x1="10" y1="1" x2="10" y2="4"/><line x1="14" y1="1" x2="14" y2="4"/></svg>`,
    perso: `<svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/></svg>`,
    event: `<svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><path d="M16 2v4M8 2v4M3 10h18"/></svg>`,
};

let EVENTS = [
    { id: 'e1', name: 'Pause', iconKey: 'pause', defaultColor: 'yellow' },
    { id: 'e2', name: 'Temps personnel', iconKey: 'perso', defaultColor: 'blue' },
];

const CLASSES = [
    'Licence 1 — Info', 'Licence 2 — Info', 'Licence 3 — Info',
    'Master 1 — SIR', 'Master 1 — IA', 'Master 2 — IA',
    'BTS 1 — Dev', 'BTS 2 — Réseaux',
];

const selectedColors = {};
SUBJECTS.forEach(s => selectedColors[`s${s.id}`] = s.defaultColor);
EVENTS.forEach(e => selectedColors[e.id] = e.defaultColor);

// ═══════════════════════════════════════
//  STANDALONE INITIALIZATION
// ═══════════════════════════════════════
document.addEventListener('DOMContentLoaded', () => {
    // 1. Get class from URL params
    const params = new URLSearchParams(window.location.search);
    const className = params.get('class');

    // 2. Initialize the view
    const label = document.getElementById('edit-class-label');
    if (label) label.textContent = className || 'Nouvelle classe';

    renderPanel();
    renderWeek();

    // Initial resize call
    recalcSlotH();
    buildGrid();
});

// ═══════════════════════════════════════
//  VIEW SWITCH / NAVIGATION
// ═══════════════════════════════════════


function exitEditMode() {
    // Redirect back to main schedule page via controller route
    window.location.href = '/ap/schedule';
}


// Keep the old function signature if needed for other parts, or replace it
function enterEditMode(className) {
    window.location.href = `/ap/schedule/edit?class=${encodeURIComponent(className)}`;
}



// ═══════════════════════════════════════
//  MODALS
// ═══════════════════════════════════════
function openAddEventModal() {
    document.getElementById('new-event-name').value = '';
    showModal('modal-event');
    setTimeout(() => document.getElementById('new-event-name').focus(), 60);
}


const closeAddEventModal = () => hideModal('modal-event');


function confirmAddEvent() {
    const name = document.getElementById('new-event-name').value.trim();
    if (!name) return;
    const id = 'e' + Date.now();
    EVENTS.push({ id, name, iconKey: 'event', defaultColor: 'violet' });
    selectedColors[id] = 'violet';
    closeAddEventModal();
    renderPanel();
    showToast('Évènement ajouté');
}

function showModal(id) {
    const m = document.getElementById(id);
    m.classList.remove('hidden');
    m.classList.add('flex');
}
function hideModal(id) {
    const m = document.getElementById(id);
    m.classList.add('hidden');
    m.classList.remove('flex');
}

// ═══════════════════════════════════════
//  COLOR PICKER POPOVER
// ═══════════════════════════════════════
let popoverKey = null;

function openColorPicker(key, anchorEl, e) {
    e.stopPropagation();
    const pop = document.getElementById('color-popover');
    if (popoverKey === key && !pop.classList.contains('hidden')) {
        pop.classList.add('hidden'); popoverKey = null; return;
    }
    popoverKey = key;

    document.getElementById('color-popover-grid').innerHTML = PALETTE.map(p => `
        <button onclick="setColor('${key}','${p.id}',this)"
                title="${p.id}"
                class="size-8 rounded-xl border-2 hover:scale-110 transition-all ${selectedColors[key] === p.id ? 'ring-2 ring-offset-1 ring-primary shadow-sm' : ''}"
                style="background:${p.bg};border-color:${p.border}"></button>`).join('');

    const rect = anchorEl.getBoundingClientRect();
    pop.style.top = `${rect.bottom + 8}px`;
    pop.style.left = `${Math.min(rect.left - 60, window.innerWidth - 172)}px`;
    pop.classList.remove('hidden');
}

function setColor(key, colorId, btn) {
    selectedColors[key] = colorId;
    btn.closest('#color-popover-grid').querySelectorAll('button')
        .forEach(b => b.classList.remove('ring-2', 'ring-offset-1', 'ring-primary', 'shadow-sm'));
    btn.classList.add('ring-2', 'ring-offset-1', 'ring-primary', 'shadow-sm');
    const col = PALETTE.find(p => p.id === colorId);
    const sw = document.getElementById(`swatch-${key}`);
    if (sw) { sw.style.background = col.bg; sw.style.borderColor = col.border; }
}

document.addEventListener('click', e => {
    const pop = document.getElementById('color-popover');
    if (pop && !pop.classList.contains('hidden') && !pop.contains(e.target)) {
        pop.classList.add('hidden'); popoverKey = null;
    }
});

// ═══════════════════════════════════════
//  PANEL
// ═══════════════════════════════════════
let openPanelId = null;

function toggleItem(cid) {
    const el = document.getElementById(cid);
    if (!el) return;
    const isOpen = !el.classList.contains('hidden');
    if (openPanelId && openPanelId !== cid) {
        const prev = document.getElementById(openPanelId);
        if (prev) prev.classList.add('hidden');
        const ch = document.querySelector(`[data-chev="${openPanelId}"]`);
        if (ch) ch.style.transform = '';
    }
    el.classList.toggle('hidden', isOpen);
    const ch = document.querySelector(`[data-chev="${cid}"]`);
    if (ch) ch.style.transform = isOpen ? '' : 'rotate(90deg)';
    openPanelId = isOpen ? null : cid;
}

function renderPanel() {
    const container = document.getElementById('segment-1');
    if (!container) return;

    let html = '';

    // UEs section (Scrollable)
    html += `<div class="flex-1 overflow-y-auto pr-1  " style="scrollbar-width:thin">
        <div class="space-y-1 pb-4">
            ${SUBJECTS.map(s => {
        const col = PALETTE.find(p => p.id === (selectedColors[`s${s.id}`] || s.defaultColor));
        return s.teachers.map(t => `
                    <div draggable="true" data-type="teacher" data-subject-name="${s.name}" data-teacher-name="${t.name}" data-item-key="s${s.id}"
                         ondragstart="onDragStart(event,this)" ondragend="onDragEnd(this)"
                         class="group flex flex-col bg-layer rounded-xl hover:bg-select-item-hover transition-all cursor-grab active:cursor-grabbing">
                        <div class="p-2.5">
                            <div class="flex items-center gap-x-3">
                                <div class="size-9 rounded-xl flex items-center justify-center shrink-0 bg-primary/10" style="background:${col.bg}">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="${col.border}" stroke-width="2.5"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/></svg>
                                </div>
                                <div class="grow min-w-0">
                                    <h3 class="group-hover:text-primary font-bold text-foreground text-[13px] truncate leading-tight">${s.code}</h3>
                                    <p class="text-[11px] text-muted-foreground-2 truncate font-medium mt-1">${s.name}</p>
                                </div>
                            </div>
                        </div>
                    </div>`).join('');
    }).join('')}
        </div>
    </div>`;

    // Events section (Non-scrollable, bottom)
    html += `<div class="shrink-0 pt-4 border-t border-card-line bg-sidebar">
        <p class="text-[10px] font-bold text-muted-foreground-2 uppercase tracking-wider mb-2.5 px-1 truncate">Évènements</p>
        <div class="space-y-2 max-h-48 overflow-y-auto" style="scrollbar-width:thin">
            ${EVENTS.map(ev => {
        const col = PALETTE.find(p => p.id === (selectedColors[ev.id] || ev.defaultColor));
        return `
                    <div draggable="true" data-type="event" data-event-name="${ev.name}" data-item-key="${ev.id}"
                         ondragstart="onDragStart(event,this)" ondragend="onDragEnd(this)"
                         class="group flex items-center gap-3 px-3 py-2.5 bg-layer border border-card-line rounded-xl hover:bg-muted/60 transition-all cursor-grab active:cursor-grabbing shadow-sm hover:shadow-md">
                        <div class="size-8 rounded-lg flex items-center justify-center shrink-0" style="background:${col.bg};color:${col.border}">
                            ${ICON_SVG[ev.iconKey] || ICON_SVG.event}
                        </div>
                        <p class="text-[13px] font-bold text-layer-foreground truncate flex-1">${ev.name}</p>
                    </div>`;
    }).join('')}
        </div>
    </div>`;

    container.innerHTML = html;
}

// ═══════════════════════════════════════
//  WEEK
// ═══════════════════════════════════════
let weekOffset = 0, slotH = 40, undoStack = [];

function getMonday(off = 0) {
    const d = new Date(), day = d.getDay(), m = new Date(d);
    m.setDate(d.getDate() - day + (day === 0 ? -6 : 1) + off * 7);
    m.setHours(0, 0, 0, 0); return m;
}
const fmtISO = d => d.toISOString().split('T')[0];
const fmtShort = d => d.toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit' });

function renderWeek() {
    const mon = getMonday(weekOffset);
    const days = Array.from({ length: DAY_COUNT }, (_, i) => { const d = new Date(mon); d.setDate(mon.getDate() + i); return d; });
    document.getElementById('date-start').value = fmtISO(days[0]);
    document.getElementById('date-end').value = fmtISO(days[5]);
    document.getElementById('week-label').textContent = `${fmtShort(days[0])} – ${fmtShort(days[5])}`;

    const h = document.getElementById('day-headers');
    if (!h) return;
    h.style.gridTemplateColumns = `44px repeat(${DAY_COUNT},1fr)`;
    h.innerHTML = `<div class="py-2 text-xs text-center text-muted-foreground-2 font-bold border-r border-card-line">h</div>`;
    days.forEach(d => {
        const name = d.toLocaleDateString('fr-FR', { weekday: 'short' }).slice(0, 3).toUpperCase();
        const today = d.toDateString() === new Date().toDateString();
        h.innerHTML += `<div class="py-1.5 text-center border-l border-card-line">
            <p class="text-[10px] font-bold text-muted-foreground-2">${name}</p>
            <p class="text-sm font-extrabold ${today ? 'text-primary' : 'text-layer-foreground'}">${d.getDate()}</p>
        </div>`;
    });
    recalcSlotH(); buildGrid();
}
function recalcSlotH() {
    const wrapper = document.getElementById('schedule-wrapper');
    if (!wrapper) return;
    slotH = Math.floor((wrapper.offsetHeight || 480) / HOURS.length);
}
function buildGrid() {
    const body = document.getElementById('schedule-body');
    if (!body) return;
    body.style.gridTemplateColumns = `44px repeat(${DAY_COUNT},1fr)`;
    let html = '';
    HOURS.forEach((hr, hi) => {
        const last = hi === HOURS.length - 1;
        html += `<div style="height:${slotH}px;grid-column:1;grid-row:${hi + 1}" class="flex items-center justify-center border-b ${last ? 'border-transparent' : 'border-card-line'} border-r border-card-line bg-card">
                  <span class="text-xs font-mono font-semibold text-muted-foreground-2">${String(hr).padStart(2, '0')}h</span>
               </div>`;
        for (let di = 0; di < DAY_COUNT; di++) {
            html += `<div style="height:${slotH}px;grid-column:${di + 2};grid-row:${hi + 1}"
                        class="border-b ${last ? 'border-transparent' : 'border-card-line'} border-l border-card-line bg-card transition-colors"
                        data-hi="${hi}" data-di="${di}"
                        ondragover="onSlotDragOver(event,this)"
                        ondragleave="onSlotDragLeave(this)"
                        ondrop="onSlotDrop(event,this)"></div>`;
        }
    });
    body.innerHTML = html;
    const blocksLayer = document.getElementById('blocks-layer');
    if (blocksLayer) blocksLayer.innerHTML = '';
    undoStack = [];
}
const prevWeek = () => { weekOffset--; renderWeek(); };
const nextWeek = () => { weekOffset++; renderWeek(); };

const dateStartEl = document.getElementById('date-start');
if (dateStartEl) {
    dateStartEl.addEventListener('change', e => {
        weekOffset = Math.round((new Date(e.target.value) - getMonday(0)) / (7 * 86400000));
        renderWeek();
    });
}

// ═══════════════════════════════════════
//  DRAG & DROP
// ═══════════════════════════════════════
let activeDragData = null;
function onDragStart(e, el) {
    activeDragData = {
        type: el.dataset.type,
        subjectName: el.dataset.subjectName || '',
        teacherName: el.dataset.teacherName || '',
        eventName: el.dataset.eventName || '',
        itemKey: el.dataset.itemKey,
        fromBlock: false
    };
    e.dataTransfer.effectAllowed = 'copy'; el.style.opacity = '.4';
}
function onDragEnd(el) { el.style.opacity = ''; }
function onSlotDragOver(e, el) { e.preventDefault(); el.style.background = 'rgba(124,58,237,.07)'; }
function onSlotDragLeave(el) { el.style.background = ''; }
function onSlotDrop(e, el) {
    e.preventDefault(); el.style.background = '';
    const hi = parseInt(el.dataset.hi), di = parseInt(el.dataset.di);
    if (activeDragData?.fromBlock) {
        const cw = colW();
        activeDragData.blockEl.style.left = `${di * cw}px`;
        activeDragData.blockEl.style.top = `${hi * slotH}px`;
        activeDragData = null; return;
    }
    if (!activeDragData) return;
    undoStack.push(placeBlock(hi, di, activeDragData));
    activeDragData = null;
}
const colW = () => {
    const wrapper = document.getElementById('schedule-wrapper');
    return wrapper ? (wrapper.offsetWidth - 44) / DAY_COUNT : 0;
};

// ═══════════════════════════════════════
//  PLACE BLOCK
// ═══════════════════════════════════════
function placeBlock(hi, di, data) {
    const cw = colW();
    const colorId = selectedColors[data.itemKey] || 'violet';
    const col = PALETTE.find(p => p.id === colorId);
    const label1 = data.type === 'teacher' ? data.subjectName : data.eventName;
    const label2 = data.type === 'teacher' ? data.teacherName : null;

    const block = document.createElement('div');
    block.className = 'group relative';
    block.style.cssText = `position:absolute;left:${di * cw}px;top:${hi * slotH}px;width:${cw - 2}px;height:${2 * slotH - 2}px;
        background:${col.bg};border:1.5px solid ${col.border};border-left-width:4px;
        color:${col.text};border-radius:12px;padding:8px;pointer-events:all;overflow:visible;
        z-index:10;box-shadow:0 4px 6px -1px rgba(0,0,0,0.1);cursor:grab;user-select:none;`;

    block.innerHTML = `
        <div class="flex items-start gap-2 h-full w-full overflow-hidden">
            <div class="size-7 rounded-lg shrink-0 flex items-center justify-center bg-white/50" style="color:${col.border}">
                ${data.type === 'event' ? (ICON_SVG[data.iconKey] || ICON_SVG.event) : `<span class="text-[10px] font-bold">${data.teacherName?.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase() || 'UE'}</span>`}
            </div>
            <div class="flex-1 min-w-0 px-1">
                <p class="font-bold text-[13px] leading-tight truncate">${label1}</p>
                ${label2 ? `<p class="text-[11px] opacity-80 truncate font-medium mt-0.5">${label2}</p>` : ''}
            </div>
        </div>
        
        <!-- Hover Dropdown Trigger -->
        <div class="absolute top-1.5 right-1.5 opacity-0 group-hover:opacity-100 transition-opacity z-20">
             <div class="hs-dropdown relative inline-flex">
                <button type="button" class="size-5 flex items-center justify-center rounded-md bg-white/90 hover:bg-white text-foreground shadow-sm">
                    <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="1"/><circle cx="12" cy="5" r="1"/><circle cx="12" cy="19" r="1"/></svg>
                </button>
                <div class="hs-dropdown-menu transition-[opacity,margin] duration-150 hs-dropdown-open:opacity-100 opacity-0 hidden z-50 min-w-[150px] bg-white border border-gray-200 rounded-xl shadow-lg p-1">
                    <button onclick="this.closest('.group').remove()" class="w-full flex items-center gap-2 px-3 py-2 text-[11px] font-medium text-red-600 hover:bg-red-50 rounded-lg">
                        <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2M10 11v6M14 11v6"/></svg>
                        Supprimer
                    </button>
                    ${data.type === 'teacher' ? `
                    <div class="hs-dropdown relative w-full">
                        <button type="button" class="hs-dropdown-toggle w-full flex items-center justify-between gap-2 px-3 py-2 text-[11px] font-medium text-gray-700 hover:bg-gray-50 rounded-lg">
                            <div class="flex items-center gap-2">
                                <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                                Enseignant
                            </div>
                            <svg class="hs-dropdown-open:rotate-180 size-2.5" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m6 9 6 6 6-6"/></svg>
                        </button>
                        <div class="hs-dropdown-menu hs-dropdown-open:opacity-100 opacity-0 hidden transition-[opacity,margin] duration-150 z-50 min-w-[140px] bg-white border border-gray-200 rounded-xl shadow-lg p-1">
                            ${(SUBJECTS.find(s => `s${s.id}` === data.itemKey)?.teachers || []).map(t => `
                                <button onclick="updateBlockTeacher(this.closest('.group'), '${t.name}')" class="w-full flex items-center gap-2 px-3 py-1.5 text-[11px] text-gray-700 hover:bg-gray-50 rounded-lg">
                                    <span class="size-4 rounded-full bg-primary/10 flex items-center justify-center text-[8px] font-bold text-primary">${t.initials}</span>
                                    ${t.name}
                                </button>
                            `).join('')}
                        </div>
                    </div>
                    ` : ''}
                </div>
             </div>
        </div>

        <div data-r="s" class="absolute bottom-0 left-0 right-0 h-1.5 cursor-s-resize hover:bg-black/5 rounded-b-xl"></div>
        <div data-r="e" class="absolute top-0 bottom-0 right-0 w-1.5 cursor-e-resize hover:bg-black/5 rounded-r-xl"></div>
    `;

    // Initialize Preline for new dropdown
    if (window.HSStaticMethods) {
        setTimeout(() => window.HSStaticMethods.autoInit(), 50);
    }

    block.setAttribute('draggable', 'true');
    block.addEventListener('dragstart', e => {
        if (e.target.dataset.r) { e.preventDefault(); return; }
        activeDragData = { fromBlock: true, blockEl: block };
        e.dataTransfer.effectAllowed = 'move';
        setTimeout(() => block.style.opacity = '.35', 0);
    });
    block.addEventListener('dragend', () => block.style.opacity = '1');
    block.addEventListener('dblclick', () => { block.remove(); undoStack = undoStack.filter(b => b !== block); });

    // Resize S
    block.querySelector('[data-r="s"]').addEventListener('mousedown', e => {
        e.stopPropagation(); e.preventDefault();
        const sy = e.clientY, sh = block.offsetHeight;
        const mv = ev => { block.style.height = Math.max(slotH, Math.round((sh + ev.clientY - sy) / slotH) * slotH - 2) + 'px'; };
        const up = () => { removeEventListener('mousemove', mv); removeEventListener('mouseup', up); };
        addEventListener('mousemove', mv); addEventListener('mouseup', up);
    });

    // Resize E
    block.querySelector('[data-r="e"]').addEventListener('mousedown', e => {
        e.stopPropagation(); e.preventDefault();
        const sx = e.clientX, sw = block.offsetWidth, cw2 = colW();
        const mv = ev => { block.style.width = Math.max(cw2 - 2, Math.round((sw + ev.clientX - sx) / cw2) * cw2 - 2) + 'px'; };
        const up = () => { removeEventListener('mousemove', mv); removeEventListener('mouseup', up); };
        addEventListener('mousemove', mv); addEventListener('mouseup', up);
    });

    const blocksLayer = document.getElementById('blocks-layer');
    if (blocksLayer) blocksLayer.appendChild(block);
    return block;
}

// ═══════════════════════════════════════
//  ACTIONS
// ═══════════════════════════════════════
function undoLast() { if (undoStack.length) undoStack.pop().remove(); }

function saveSchedule() { showToast('Emploi du temps sauvegardé'); }

async function exportPDF() {
    const el = document.getElementById('schedule-card');
    if (!el) return;
    try {
        showToast('Génération du PDF…');
        const bl = document.getElementById('blocks-layer');
        const oldPE = bl.style.pointerEvents; bl.style.pointerEvents = 'all';
        const canvas = await html2canvas(el, { scale: 2, useCORS: true, logging: false, allowTaint: true });
        bl.style.pointerEvents = oldPE;
        const { jsPDF } = window.jspdf;
        const pdf = new jsPDF({ orientation: 'landscape', unit: 'mm', format: 'a4' });
        const pw = pdf.internal.pageSize.getWidth(), ph = pdf.internal.pageSize.getHeight();
        const iw = canvas.width, ih = canvas.height;
        const ratio = Math.min(pw / iw * 100, ph / ih * 100) / 100;
        pdf.addImage(canvas.toDataURL('image/png'), 'PNG', 0, 0, iw * ratio, ih * ratio);
        pdf.save('emploi-du-temps.pdf');
        showToast('PDF exporté avec succès');
    } catch (err) { console.error(err); showToast('Erreur lors de l\'export'); }
}

let toastTimer;
function showToast(msg) {
    const t = document.getElementById('toast');
    if (!t) return;
    document.getElementById('toast-msg').textContent = msg;
    t.classList.remove('hidden'); clearTimeout(toastTimer);
    toastTimer = setTimeout(() => t.classList.add('hidden'), 2800);
}

window.addEventListener('resize', () => { recalcSlotH(); buildGrid(); });

function updateBlockTeacher(blockEl, teacherName) {
    // Find teacher name paragraph - more robust selector
    const p = Array.from(blockEl.querySelectorAll('p')).find(el => el.classList.contains('opacity-80'));
    if (p) p.textContent = teacherName;

    const initialSpan = blockEl.querySelector('span.text-\\[10px\\]');
    if (initialSpan) {
        initialSpan.textContent = teacherName.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase();
    }
    showToast(`Enseignant modifié : ${teacherName}`);
}

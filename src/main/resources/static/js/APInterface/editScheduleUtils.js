// ═══════════════════════════════════════
//  CONSTANTS & DATA
// ═══════════════════════════════════════
export const HOURS = [8, 9, 10, 11, 12, 13, 14, 15, 16]; // slots 8h-17h
export const DAY_COUNT = 6;

export const CLASSES = [
    'Licence 1 — Info', 'Licence 2 — Info', 'Licence 3 — Info',
    'Master 1 — SIR', 'Master 1 — IA', 'Master 2 — IA',
    'BTS 1 — Dev', 'BTS 2 — Réseaux',
];

export const PALETTE = [
    { id: 'violet', bg: '#ede9fe', border: '#7c3aed', text: '#4c1d95' },
    { id: 'blue', bg: '#dbeafe', border: '#3b82f6', text: '#1e3a8a' },
    { id: 'green', bg: '#d1fae5', border: '#10b981', text: '#065f46' },
    { id: 'orange', bg: '#ffedd5', border: '#f97316', text: '#9a3412' },
    { id: 'pink', bg: '#fce7f3', border: '#ec4899', text: '#831843' },
    { id: 'teal', bg: '#ccfbf1', border: '#14b8a6', text: '#134e4a' },
    { id: 'yellow', bg: '#fef9c3', border: '#eab308', text: '#713f12' },
    { id: 'red', bg: '#fee2e2', border: '#ef4444', text: '#7f1d1d' },
];

export const SUBJECTS = [
    { id: 1, name: 'Algorithmique', code: 'INF301', defaultColor: 'violet', teachers: [{ id: 1, name: 'Dr. Kamga Paul', initials: 'KP' }, { id: 2, name: 'Prof. Nkemeni Alice', initials: 'NA' }] },
    { id: 2, name: 'Structures de données', code: 'INF302', defaultColor: 'blue', teachers: [{ id: 3, name: 'Dr. Fouda Martin', initials: 'FM' }, { id: 4, name: 'Mme. Biya Sandra', initials: 'BS' }] },
    { id: 3, name: 'Bases de données', code: 'INF401', defaultColor: 'green', teachers: [{ id: 5, name: 'Prof. Ela Roger', initials: 'ER' }, { id: 6, name: 'Dr. Mbarga Celine', initials: 'MC' }] },
    { id: 4, name: 'Réseaux info.', code: 'RSX201', defaultColor: 'orange', teachers: [{ id: 7, name: 'Dr. Abanda Serge', initials: 'AS' }, { id: 8, name: 'M. Njoya Victor', initials: 'NV' }] },
    { id: 5, name: 'POO', code: 'INF303', defaultColor: 'pink', teachers: [{ id: 9, name: 'Mme. Ngono Estelle', initials: 'NE' }, { id: 10, name: 'Dr. Beyala Jules', initials: 'BJ' }] },
    { id: 6, name: 'Génie logiciel', code: 'INF501', defaultColor: 'teal', teachers: [{ id: 11, name: 'Prof. Mbouda Henri', initials: 'MH' }, { id: 12, name: 'Dr. Ngo Bum Claire', initials: 'NC' }] },
];

export const ICON_SVG = {
    pause: `<svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 8h1a4 4 0 0 1 0 8h-1"/><path d="M2 8h16v9a4 4 0 0 1-4 4H6a4 4 0 0 1-4-4V8z"/><line x1="6" y1="1" x2="6" y2="4"/><line x1="10" y1="1" x2="10" y2="4"/><line x1="14" y1="1" x2="14" y2="4"/></svg>`,
    perso: `<svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/></svg>`,
    event: `<svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><path d="M16 2v4M8 2v4M3 10h18"/></svg>`,
    users: `<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>`,
};

// ═══════════════════════════════════════
//  COLLISION DETECTION
// ═══════════════════════════════════════

/** 
 * Check if an area is occupied by any block other than excludeBlock 
 */
export function isAreaOccupied(hourIndex, dayIndex, rowSpan, colSpan, excludeBlock = null, blocks = null) {
    const blocksToCheck = blocks || document.querySelectorAll('.schedule-block');
    for (const block of blocksToCheck) {
        if (block === excludeBlock) continue;
        
        const bHi = parseInt(block.dataset.hourIndex);
        const bDi = parseInt(block.dataset.dayIndex);
        const bRs = parseInt(block.dataset.rs || "1");
        const bCs = 1; // logical blocks always occupy 1 col per DOM element

        // Overlap logic: (StartA < EndB) && (EndA > StartB)
        const verticalOverlap = (hourIndex < bHi + bRs) && (hourIndex + rowSpan > bHi);
        const horizontalOverlap = (dayIndex < bDi + bCs) && (dayIndex + colSpan > bDi);

        if (verticalOverlap && horizontalOverlap) return true;
    }
    return false;
}

/**
 * Check if a block of given dimensions fits within the grid boundaries
 */
export function isWithinBounds(hourIndex, dayIndex, rowSpan, colSpan) {
    if (hourIndex < 0 || dayIndex < 0) return false;
    if (dayIndex + colSpan > DAY_COUNT) return false;
    if (hourIndex + rowSpan > HOURS.length) return false;
    return true;
}

// ═══════════════════════════════════════
//  DATE FORMATTING
// ═══════════════════════════════════════

export function getMonday(weekOffset = 0) {
    const todayDate = new Date(), dayOfWeek = todayDate.getDay(), mondayDate = new Date(todayDate);
    mondayDate.setDate(todayDate.getDate() - dayOfWeek + (dayOfWeek === 0 ? -6 : 1) + weekOffset * 7);
    mondayDate.setHours(0, 0, 0, 0);
    return mondayDate;
}

export function fmtISO(date) { 
    return date.toISOString().split('T')[0]; 
}

export function fmtShort(date) { 
    return date.toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit' }); 
}

// ═══════════════════════════════════════
//  EXPORT PDF
// ═══════════════════════════════════════

export async function generatePDF(elementId, showToastFn) {
    const scheduleCardElement = document.getElementById(elementId);
    if (!scheduleCardElement) return;
    try {
        if (typeof html2canvas === 'undefined') { showToastFn('html2canvas non chargé'); return; }
        if (!window.jspdf) { showToastFn('jsPDF non chargé'); return; }
        
        showToastFn('Génération du PDF…');
        const blocksLayer = document.getElementById('blocks-layer');
        const oldPointerEvents = blocksLayer.style.pointerEvents;
        blocksLayer.style.pointerEvents = 'all';
        
        const canvas = await html2canvas(scheduleCardElement, { scale: 2, useCORS: true, logging: false, allowTaint: true });
        blocksLayer.style.pointerEvents = oldPointerEvents;
        
        const { jsPDF } = window.jspdf;
        const pdf = new jsPDF({ orientation: 'landscape', unit: 'mm', format: 'a4' });
        const pageWidth = pdf.internal.pageSize.getWidth(), pageHeight = pdf.internal.pageSize.getHeight();
        const canvasWidth = canvas.width, canvasHeight = canvas.height;
        const ratio = Math.min(pageWidth / canvasWidth * 100, pageHeight / canvasHeight * 100) / 100;
        
        pdf.addImage(canvas.toDataURL('image/png'), 'PNG', 0, 0, canvasWidth * ratio, canvasHeight * ratio);
        pdf.save('emploi-du-temps.pdf');
        showToastFn('PDF exporté avec succès ✓');
    } catch (err) { 
        console.error(err); 
        showToastFn("Erreur lors de l'export"); 
    }
}

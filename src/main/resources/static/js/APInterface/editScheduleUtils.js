
import { oklabStringToRgb } from "../common/ColorConverter.js";
import { oklchStringToRgb } from "../common/ColorConverter.js";



export const HOURS = [8, 9, 10, 11, 12, 13, 14, 15, 16];
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
    books: `<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"/><path d="M5 5a1 1 0 0 1 1 -1h2a1 1 0 0 1 1 1v14a1 1 0 0 1 -1 1h-2a1 1 0 0 1 -1 -1l0 -14" /><path d="M9 5a1 1 0 0 1 1 -1h2a1 1 0 0 1 1 1v14a1 1 0 0 1 -1 1h-2a1 1 0 0 1 -1 -1l0 -14" /><path d="M5 8h4" /><path d="M9 16h4" /><path d="M13.803 4.56l2.184 -.53c.562 -.135 1.133 .19 1.282 .732l3.695 13.418a1.02 1.02 0 0 1 -.634 1.219l-.133 .041l-2.184 .53c-.562 .135 -1.133 -.19 -1.282 -.732l-3.695 -13.418a1.02 1.02 0 0 1 .634 -1.219l.133 -.041" /><path d="M14 9l4 -1" /><path d="M16 16l3.923 -.98" /></svg>`,
};


//Verifie si une zone du planing est deja occupe par un autre bloc
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


export function getMonday(weekOffset = 0) {
    const todayDate = new Date();
    const dayOfWeek = todayDate.getDay(); // 0 (Sunday) to 6 (Saturday)
    
    // Pour obtenir le lundi de la semaine courant:
    // Si dimanche (0), ajouter 1 jour pour obtenir lundi
    // Sinon, soustraire (dayOfWeek - 1) jours pour obtenir le lundi
    const daysToAdjust = dayOfWeek === 0 ? 1 : 1 - dayOfWeek;
    
    const mondayDate = new Date(todayDate);
    mondayDate.setDate(todayDate.getDate() + daysToAdjust + weekOffset * 7);
    mondayDate.setHours(0, 0, 0, 0);
    return mondayDate;
}

export function fmtISO(date) {
    return date.toISOString().split('T')[0];
}



export function fmtShort(date) {
    return date.toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit' });
}



export async function generatePDF(elementId, showToastFn) {
    const root = document.getElementById(elementId);
    if (!root) return;

    if (typeof html2canvas === "undefined") {
        showToastFn?.("html2canvas non chargé");
        return;
    }

    const jsPDFCtor = (window.jspdf && window.jspdf.jsPDF) || window.jsPDF;
    if (!jsPDFCtor) {
        showToastFn?.("jsPDF non chargé");
        return;
    }

    const wrapper = root.querySelector("#page-content");
    if (!wrapper) {
        showToastFn?.("PDF: zone planning introuvable");
        return;
    }

    const injectExportStyles = (doc) => {
        
    doc.documentElement.classList.remove("dark");

        // Ajoute les styles nécessaires pour PDF
        const style = doc.createElement("style");
        style.textContent = `
         #page-content {
            background: #ffffff !important;
        }
           
        .schedule-block p, .schedule-block span {
            overflow: visible !important;
            text-overflow: clip !important;
            white-space: normal !important; /* Désactive le truncate */
            line-height: 1.4 !important;    /* Donne de l'air au texte */
            display: block !important;
        }

      
        .schedule-block div {
            overflow: visible !important;
            height: auto !important; /* Laisse le bloc respirer */
        }

      
        .btn-delete-block, [data-r="s"], [data-r="e"] {
            display: none !important;
        }

        #day-headers { position: static !important; top: auto !important; }
        .time-cell { position: static !important; left: auto !important; }
    `;
        doc.head.appendChild(style);

        // Parcours tous les éléments pour corriger uniquement les couleurs non supportées
        const allElements = doc.querySelectorAll("*");
        allElements.forEach((el) => {
            const computed = window.getComputedStyle(el);

            // Fonction pour transformer okLab ou autres en rgb
            const safeColor = (color) => {
                console.log("Couleur originale:", color);
                if (!color) return color;
                // Si c’est déjà rgb ou hex, on garde
                if (color.startsWith("rgb") || color.startsWith("#")) return color;
                // Sinon, on met fallback noir (ou blanc si c’est bg)
                if (color.startsWith("oklch")) return oklchStringToRgb(color);
                if (color.startsWith("oklab")) return oklabStringToRgb(color);

                return color; // fallback, on espère que jsPDF gérera une couleur valide ou ignorera
            };

            // Texte
            if (computed.color) el.style.color = safeColor(computed.color);
            // Background
            if (computed.backgroundColor && computed.backgroundColor !== "rgba(0, 0, 0, 0)")
                el.style.backgroundColor = safeColor(computed.backgroundColor);
            // Border
            if (computed.borderColor) el.style.borderColor = safeColor(computed.borderColor);
        });
    };
    const rootStyle = root.style.cssText;
    const wrapStyle = wrapper.style.cssText;

    try {
        showToastFn?.("Génération du PDF…");

        // Capture l'élément directement
        const canvas = await html2canvas(wrapper, {
            scale: 2,                 // meilleure résolution
            useCORS: true,
            allowTaint: true,
            logging: false,
            backgroundColor: "#ffffff",
            onclone: injectExportStyles,
        });

        const imgData = canvas.toDataURL("image/png");

        // Création du PDF
        const pdf = new jsPDFCtor({ orientation: "landscape", unit: "mm", format: "a4" });
        const pageW = pdf.internal.pageSize.getWidth();
        const pageH = pdf.internal.pageSize.getHeight();

        const imgRatio = canvas.width / canvas.height;
        const pageRatio = pageW / pageH;

        let imgPrintW, imgPrintH;
        if (imgRatio > pageRatio) {
            imgPrintW = pageW;
            imgPrintH = pageW / imgRatio;
        } else {
            imgPrintH = pageH;
            imgPrintW = pageH * imgRatio;
        }

        const offsetX = (pageW - imgPrintW) / 2;
        const offsetY = (pageH - imgPrintH) / 2;

        pdf.addImage(imgData, "PNG", offsetX, offsetY, imgPrintW, imgPrintH);
        pdf.save("Emploi_du_temps.pdf");
        showToastFn?.("PDF exporté ✓");
    } catch (error) {
        console.error("Erreur lors de la génération du PDF:", error);
        showToastFn?.(`Erreur export PDF: ${error?.message || error}`);
    } finally {
        root.style.cssText = rootStyle;
        wrapper.style.cssText = wrapStyle;
    }
}




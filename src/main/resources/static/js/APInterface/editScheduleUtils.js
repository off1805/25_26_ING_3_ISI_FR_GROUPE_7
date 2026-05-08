
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



/**
 * Charge une image same-origin et retourne { dataUrl, width, height }.
 *
 * Pipeline : fetch → Blob → ObjectURL → Image.onload → Canvas → toDataURL
 *
 * Pourquoi ce pipeline ?
 * - fetch same-origin : pas de CORS, pas de canvas tainté
 * - On dessine soi-même dans un canvas offscreen → toDataURL('image/png') produit
 *   un PNG standard que jsPDF 2.x digère parfaitement
 * - Évite le parseur PNG interne de jsPDF (source de l'erreur .data undefined)
 *
 * @param {string} url URL relative same-origin (ex: "/uploads/download.png")
 * @returns {Promise<{dataUrl: string, width: number, height: number}>}
 */
async function _loadLogoImage(url) {
    const resp = await fetch(url);
    if (!resp.ok) throw new Error(`HTTP ${resp.status} en chargeant le logo : ${url}`);

    const blob   = await resp.blob();
    const objUrl = URL.createObjectURL(blob);

    const img = await new Promise((resolve, reject) => {
        const i    = new Image();
        i.onload  = () => { URL.revokeObjectURL(objUrl); resolve(i); };
        i.onerror = () => { URL.revokeObjectURL(objUrl); reject(new Error(`Image non chargée : ${url}`)); };
        i.src = objUrl;
    });

    // Canvas offscreen : same-origin → non tainté → toDataURL OK
    const canvas = document.createElement('canvas');
    canvas.width  = img.naturalWidth;
    canvas.height = img.naturalHeight;
    canvas.getContext('2d').drawImage(img, 0, 0);

    return {
        dataUrl: canvas.toDataURL('image/png'),
        width:   img.naturalWidth,
        height:  img.naturalHeight,
    };
}

/**
 * Génère un PDF A4 paysage de la grille de planning avec un en-tête.
 *
 * @param {string}   elementId   ID du conteneur racine (ex: "main-page")
 * @param {Function} showToastFn Callback d’affichage des notifications
 * @param {object}   pdfMeta     Métadonnées du header PDF :
 *   - logoUrl          {string}         URL du logo (ex: "/images/logo.png")
 *   - anneeAcademique  {string}         "2025/2026"
 *   - semestre         {number|string}  1 ou 2
 *   - className        {string}         Libellé de la classe
 *   - weekLabel        {string}         "05/05 – 10/05"
 *   - semaine          {number|string}  Numéro de semaine ISO
 */
export async function generatePDF(elementId, showToastFn, pdfMeta = {}) {
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
        const style = doc.createElement("style");
        style.textContent = `
            #page-content { background: #ffffff !important; }
            .schedule-block p, .schedule-block span {
                overflow: visible !important;
                text-overflow: clip !important;
                white-space: normal !important;
                line-height: 1.4 !important;
                display: block !important;
            }
            .schedule-block div { overflow: visible !important; height: auto !important; }
            .btn-delete-block, [data-r="s"], [data-r="e"] { display: none !important; }
            #day-headers { position: static !important; top: auto !important; }
            .time-cell { position: static !important; left: auto !important; }
        `;
        doc.head.appendChild(style);

        doc.querySelectorAll("*").forEach((el) => {
            const computed = window.getComputedStyle(el);
            const safeColor = (color) => {
                if (!color) return color;
                if (color.startsWith("rgb") || color.startsWith("#")) return color;
                if (color.startsWith("oklch")) return oklchStringToRgb(color);
                if (color.startsWith("oklab")) return oklabStringToRgb(color);
                return color;
            };
            if (computed.color) el.style.color = safeColor(computed.color);
            if (computed.backgroundColor && computed.backgroundColor !== "rgba(0, 0, 0, 0)")
                el.style.backgroundColor = safeColor(computed.backgroundColor);
            if (computed.borderColor) el.style.borderColor = safeColor(computed.borderColor);
        });
    };

    const rootStyle = root.style.cssText;
    const wrapStyle = wrapper.style.cssText;

    try {
        showToastFn?.("Génération du PDF…");

        // ── Capture de la grille ──────────────────────────────────────────
        const canvas = await html2canvas(wrapper, {
            scale: 2,
            useCORS: true,
            allowTaint: true,
            logging: false,
            backgroundColor: "#ffffff",
            onclone: injectExportStyles,
        });

        // ── Création du PDF ───────────────────────────────────────────────
        const pdf    = new jsPDFCtor({ orientation: "landscape", unit: "mm", format: "a4" });
        const pageW  = pdf.internal.pageSize.getWidth();  // 297 mm
        const pageH  = pdf.internal.pageSize.getHeight(); // 210 mm
        const margin = 8;   // marge horizontale et verticale
        let   yPos   = margin;

        // ── LOGO centré (ratio naturel conservé) ─────────────────────────
        if (pdfMeta.logoUrl) {
            try {
                // _loadLogoImage retourne { dataUrl, width, height }
                // dataUrl = canvas.toDataURL(‘image/png’) → PNG standard, jsPDF l’accepte toujours
                const logo  = await _loadLogoImage(pdfMeta.logoUrl);
                const maxH  = 22;   // hauteur max (mm)
                const maxW  = 60;   // largeur max (mm)
                const ratio = logo.width / logo.height;

                let logoH = maxH;
                let logoW = logoH * ratio;
                if (logoW > maxW) { logoW = maxW; logoH = logoW / ratio; }

                pdf.addImage(logo.dataUrl, 'PNG', (pageW - logoW) / 2, yPos, logoW, logoH);
                yPos += logoH + 4;
            } catch (e) {
                console.warn("PDF : logo non charge :", e.message);
                yPos += 4;
            }
        }

        // ── Ligne d’en-tête ───────────────────────────────────────────────
        const parts = [];
        if (pdfMeta.anneeAcademique) parts.push(`Annee academique ${pdfMeta.anneeAcademique}`);
        if (pdfMeta.semestre != null) parts.push(`Semestre ${pdfMeta.semestre}`);
        if (pdfMeta.className)        parts.push(`Classe : ${pdfMeta.className}`);

        // Semaine au format "du DD/MM au DD/MM" (dates debut - fin uniquement)
        if (pdfMeta.weekLabel) parts.push(`Semaine du ${pdfMeta.weekLabel}`);

        if (parts.length) {
            pdf.setFont("helvetica", "normal");
            pdf.setFontSize(9);
            pdf.setTextColor(120, 120, 120);
            pdf.text(parts.join("   |   "), pageW / 2, yPos + 5, { align: "center" });
            yPos += 9;
        }

        // Pas de séparateur — la grille commence directement après le texte
        yPos += 2;

        // ── Grille du planning ────────────────────────────────────────────
        const imgData   = canvas.toDataURL("image/png");
        const availW    = pageW - margin * 2;
        const availH    = pageH - yPos - margin;
        const imgRatio  = canvas.width / canvas.height;
        const availRatio = availW / availH;

        let imgPrintW, imgPrintH;
        if (imgRatio > availRatio) {
            imgPrintW = availW;
            imgPrintH = availW / imgRatio;
        } else {
            imgPrintH = availH;
            imgPrintW = availH * imgRatio;
        }

        const imgX = margin + (availW - imgPrintW) / 2;
        pdf.addImage(imgData, "PNG", imgX, yPos, imgPrintW, imgPrintH);

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




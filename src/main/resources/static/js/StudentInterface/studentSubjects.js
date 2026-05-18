import api from "../common/ClientHttp.js";

const specialiteId = window.KEMO_STUDENT?.specialiteId ?? null;

let allSubjects = [];  // MatiereStatsEtudiantDTO[]
let currentSemFilter = "all";
let searchDebounce   = null;

// ── Bootstrap ──────────────────────────────────────────────────────────────────

document.addEventListener("DOMContentLoaded", async () => {
    await loadStats();
    computeGlobalStats();
    renderSubjects();
    bindFilters();
});

// ── Fetch ──────────────────────────────────────────────────────────────────────

async function loadStats() {
    if (!specialiteId) {
        showGrid(`<div class="col-span-full py-12 text-center text-muted-foreground-2">
            <p>Aucune spécialité associée à votre classe.</p>
        </div>`);
        return;
    }
    try {
        // Un seul appel — toute l'agrégation est faite en SQL côté serveur.
        allSubjects = await api.get(`/api/presences/matieres/stats?specialiteId=${specialiteId}`);
    } catch {
        allSubjects = [];
        showGrid(`<div class="col-span-full py-12 text-center text-red-400">
            <p>Erreur lors du chargement des matières.</p>
        </div>`);
    }
}

// ── Stats globales ─────────────────────────────────────────────────────────────

function computeGlobalStats() {
    const tauxEl  = document.getElementById("global-taux");
    const countEl = document.getElementById("global-ue-count");
    if (countEl) countEl.textContent = allSubjects.length;

    const totalMin = allSubjects.reduce((s, m) => s + (m.totalMinutesProgrammes ?? 0), 0);
    const absMin   = allSubjects.reduce((s, m) => s + (m.minutesAbsence ?? 0), 0);
    const taux     = totalMin > 0 ? Math.round(((totalMin - absMin) / totalMin) * 100) : 100;
    if (tauxEl) tauxEl.textContent = `${Math.max(0, taux)}%`;
}

// ── Rendu des cartes ───────────────────────────────────────────────────────────

function filtered() {
    const q = (document.getElementById("subject-search")?.value ?? "").toLowerCase().trim();
    return allSubjects.filter(s => {
        const semOk = currentSemFilter === "all" || String(s.semestre) === currentSemFilter;
        const qOk   = !q || s.libelle.toLowerCase().includes(q) || (s.code ?? "").toLowerCase().includes(q);
        return semOk && qOk;
    });
}

function renderSubjects() {
    const list = filtered();
    if (list.length === 0) {
        showGrid(`<div class="col-span-full py-16 text-center text-muted-foreground-2">
            <i class="bi bi-book text-4xl mb-3 block"></i>
            <p class="text-sm font-semibold text-layer-foreground mb-1">Aucune matière trouvée</p>
            <p class="text-sm">Aucune matière pour ce filtre.</p>
        </div>`);
        return;
    }
    showGrid(list.map(card).join(""));

    document.querySelectorAll(".student-ue-card").forEach(el => {
        el.addEventListener("click", () => {
            const id = parseInt(el.dataset.id, 10);
            const s  = allSubjects.find(x => x.offreUeId === id);
            if (s) openModal(s);
        });
    });
}

function tauxBadgeCls(t) {
    if (t >= 80) return "bg-green-400/10  text-green-400  border-green-400/25";
    if (t >= 60) return "bg-yellow-400/10 text-yellow-400 border-yellow-400/25";
    return            "bg-red-400/10    text-red-400    border-red-400/25";
}

function card(s) {
    const taux   = s.tauxPresence ?? 100;
    const color  = s.couleur ?? "#7c3aed";
    const semLbl = s.semestre ? `S${s.semestre}` : "—";
    const nbAbs  = s.nbAbsences ?? 0;

    return `
    <div class="student-ue-card flex items-center gap-4 px-5 py-4 bg-card border border-card-line rounded-xl
                hover:shadow-sm hover:border-primary/30 transition-all cursor-pointer"
         data-id="${s.offreUeId}">

        <div class="flex-shrink-0 size-10 rounded-lg flex items-center justify-center text-lg"
             style="background-color:${color}19; color:${color};">
            <i class="bi bi-journal-text"></i>
        </div>

        <div class="flex-1 min-w-0">
            <p class="text-sm font-semibold text-layer-foreground truncate">${esc(s.libelle)}</p>
            <p class="text-xs text-muted-foreground-2 mt-0.5">
                ${s.volumeHoraireTotal ?? 0}h &middot; ${s.credit ?? 0} crédit(s) &middot; ${semLbl}
                ${nbAbs > 0
                    ? `&middot; <span class="text-red-400">${nbAbs} absence${nbAbs > 1 ? "s" : ""}</span>`
                    : ""}
            </p>
        </div>

        <div class="flex items-center gap-2 shrink-0">
            <span class="text-[11px] font-semibold px-2.5 py-0.5 rounded-full border ${tauxBadgeCls(taux)}">${taux}%</span>
            <i class="bi bi-chevron-right text-muted-foreground-2 text-xs"></i>
        </div>
    </div>`;
}

function showGrid(html) {
    const el      = document.getElementById("subjects-grid");
    const loading = document.getElementById("subjects-loading");
    if (loading) loading.remove();
    if (el) el.innerHTML = html;
}

// ── Modal ──────────────────────────────────────────────────────────────────────

function openModal(s) {
    const taux     = s.tauxPresence ?? 100;
    const color    = s.couleur ?? "#7c3aed";
    const toH      = m => ((m ?? 0) / 60).toFixed(1) + "h";
    const presMin  = Math.max(0, (s.totalMinutesProgrammes ?? 0) - (s.minutesAbsence ?? 0));

    // Header
    const badge = document.getElementById("modal-color-badge");
    if (badge) badge.style.background = color;
    setText("modal-libelle", s.libelle);
    setText("modal-code",    s.code ?? "");

    // Jauge circulaire
    const circle = document.getElementById("modal-presence-circle");
    if (circle) {
        circle.style.strokeDashoffset = (314 - (taux / 100) * 314).toFixed(2);
        circle.style.color = taux >= 80 ? "#4ade80" : taux >= 60 ? "#facc15" : "#f87171";
    }
    setText("modal-taux-label", `${taux}%`);

    // Cards heures (calculées côté serveur, affichées directement)
    setText("modal-heures-totales",  toH(s.totalMinutesProgrammes));
    setText("modal-heures-presence", toH(presMin));
    setText("modal-heures-absence",  toH(s.minutesAbsence));

    // Breakdown justificatifs (données serveur directes)
    const total = (s.nbAbsences ?? 0) || 1;
    const justifRows = [
        { count: s.nbJustifiees ?? 0,    label: "Justifiées",     bar: "bg-green-400",  txt: "text-green-400" },
        { count: s.nbEnCours ?? 0,       label: "En cours",       bar: "bg-yellow-400", txt: "text-yellow-400" },
        { count: s.nbNonJustifiees ?? 0, label: "Non justifiées", bar: "bg-red-400",    txt: "text-red-400" },
        { count: s.nbRejetees ?? 0,      label: "Rejetées",       bar: "bg-gray-400",   txt: "text-gray-400" },
    ].map(({ count, label, bar, txt }) => {
        const pct = Math.round((count / total) * 100);
        return `<div class="flex items-center gap-2">
            <div class="w-28 shrink-0 flex items-center gap-1.5">
                <div class="size-2 rounded-full ${bar}"></div>
                <span class="text-xs text-muted-foreground-2">${label}</span>
            </div>
            <div class="flex-1 h-1.5 bg-surface-2 rounded-full overflow-hidden">
                <div class="h-full ${bar} rounded-full" style="width:${count > 0 ? pct : 0}%"></div>
            </div>
            <span class="text-xs font-semibold ${txt} w-5 text-right">${count}</span>
        </div>`;
    }).join("");
    setHtml("modal-justif-breakdown", justifRows);

    // Historique : on réutilise les absences détaillées (endpoint existant)
    loadAbsencesForSubject(s.libelle);

    // Onglet Infos
    setText("info-code",        s.code ?? "—");
    setText("info-credit",      `${s.credit ?? "—"} crédit(s)`);
    setText("info-vh",          `${s.volumeHoraireTotal ?? "—"} heures`);
    setText("info-semestre",    s.semestre ? `Semestre ${s.semestre}` : "—");
    setText("info-description", s.description?.trim() || "Aucune description disponible.");

    document.getElementById("tab-overview-btn")?.click();

    if (window.HSOverlay) window.HSOverlay.open("#hs-modal-subject-stats");
    setTimeout(() => {
        if (typeof HSStaticMethods !== "undefined") HSStaticMethods.autoInit();
    }, 50);
}

async function loadAbsencesForSubject(libelle) {
    const absListEl = document.getElementById("modal-absences-list");
    const noAbsEl   = document.getElementById("modal-no-absences");
    if (absListEl) absListEl.innerHTML = `<div class="flex justify-center py-6">
        <div class="animate-spin size-5 border-[2px] border-current border-t-transparent text-primary rounded-full"></div>
    </div>`;

    try {
        const all  = await api.get("/api/presences/absences/etudiant");
        const norm = str => (str ?? "").trim().toLowerCase();
        const abs  = (all ?? []).filter(a => norm(a.matiere) === norm(libelle))
                                .sort((a, b) => new Date(b.dateSeance) - new Date(a.dateSeance));

        if (abs.length === 0) {
            if (absListEl) absListEl.innerHTML = "";
            noAbsEl?.classList.remove("hidden");
        } else {
            noAbsEl?.classList.add("hidden");
            if (absListEl) absListEl.innerHTML = abs.map(absenceRow).join("");
        }
    } catch {
        if (absListEl) absListEl.innerHTML = `<p class="text-xs text-red-400 py-4 text-center">Impossible de charger l'historique.</p>`;
    }
}

const STATUT_CFG = {
    JUSTIFIEE:     { label: "Justifiée",     cls: "bg-green-400/15  text-green-400  border-green-400/25" },
    EN_COURS:      { label: "En cours",      cls: "bg-yellow-400/15 text-yellow-400 border-yellow-400/25" },
    NON_JUSTIFIEE: { label: "Non justifiée", cls: "bg-red-400/15    text-red-400    border-red-400/25" },
    REJETEE:       { label: "Rejetée",       cls: "bg-gray-400/15   text-gray-400   border-gray-400/25" },
};

function absenceRow(a) {
    const date = a.dateSeance
        ? new Date(a.dateSeance).toLocaleDateString("fr-FR", { day: "2-digit", month: "short", year: "numeric" })
        : "—";
    const h  = ((a.minutesAbsence ?? 0) / 60).toFixed(1);
    const st = STATUT_CFG[a.statutJustificatif]
        ?? { label: a.statutJustificatif ?? "—", cls: "bg-surface text-muted-foreground-2 border-layer-line" };

    return `
    <div class="flex items-center justify-between gap-3 px-3 py-2.5 bg-surface rounded-xl border border-layer-line">
        <div class="flex items-center gap-2.5">
            <div class="size-8 rounded-lg bg-red-400/10 flex items-center justify-center shrink-0">
                <i class="bi bi-calendar-x text-red-400 text-sm"></i>
            </div>
            <div>
                <p class="text-xs font-semibold text-layer-foreground">${date}</p>
                <p class="text-[11px] text-muted-foreground-2">${h}h d'absence</p>
            </div>
        </div>
        <span class="text-[10px] font-semibold px-2.5 py-1 rounded-full border ${st.cls}">${st.label}</span>
    </div>`;
}

// ── Filtres ────────────────────────────────────────────────────────────────────

function bindFilters() {
    document.querySelectorAll(".sem-filter-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            document.querySelectorAll(".sem-filter-btn").forEach(b => {
                b.classList.remove("bg-primary", "text-white", "border-primary");
                b.classList.add("bg-layer", "text-muted-foreground-1", "border-layer-line");
            });
            btn.classList.add("bg-primary", "text-white", "border-primary");
            btn.classList.remove("bg-layer", "text-muted-foreground-1", "border-layer-line");
            currentSemFilter = btn.dataset.semFilter ?? "all";
            renderSubjects();
        });
    });

    document.getElementById("subject-search")?.addEventListener("input", () => {
        clearTimeout(searchDebounce);
        searchDebounce = setTimeout(renderSubjects, 250);
    });
}

// ── Utilitaires ────────────────────────────────────────────────────────────────

function setText(id, txt) {
    const el = document.getElementById(id);
    if (el) el.textContent = txt;
}

function setHtml(id, html) {
    const el = document.getElementById(id);
    if (el) el.innerHTML = html;
}

function esc(str) {
    return String(str ?? "")
        .replace(/&/g, "&amp;").replace(/</g, "&lt;")
        .replace(/>/g, "&gt;").replace(/"/g, "&quot;");
}

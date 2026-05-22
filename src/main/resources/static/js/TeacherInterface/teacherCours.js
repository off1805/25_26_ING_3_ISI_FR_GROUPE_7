import api from "../common/ClientHttp.js";

// ── État global ────────────────────────────────────────────────────────────────
let allCards = [];
let currentSem    = "all";
let currentClasse = "all";
let searchDebounce = null;

// Contexte du modal cours ouvert
let activeOffreUeId = null;
let activeClasseId  = null;

// ── Bootstrap ──────────────────────────────────────────────────────────────────
document.addEventListener("DOMContentLoaded", async () => {
    await loadCards();
    populateClasseFilter();
    renderGrid();
    bindFilters();
});

// ── Chargement ─────────────────────────────────────────────────────────────────
async function loadCards() {
    try {
        allCards = await api.get("/api/teacher/cours");
    } catch {
        allCards = [];
        setGrid(`<div class="col-span-full py-16 text-center text-red-400">
            <i class="bi bi-exclamation-triangle text-3xl mb-2 block"></i>
            <p class="text-sm">Erreur lors du chargement des cours.</p>
        </div>`);
    }
    document.getElementById("cours-loading")?.remove();
}

// ── Filtre et rendu des cartes ─────────────────────────────────────────────────
function filtered() {
    const q = (document.getElementById("cours-search")?.value ?? "").toLowerCase().trim();
    return allCards.filter(c => {
        const semOk    = currentSem    === "all" || String(c.semestre) === currentSem;
        const classeOk = currentClasse === "all" || String(c.classeId) === currentClasse;
        const qOk      = !q || c.libelle.toLowerCase().includes(q) || (c.code ?? "").toLowerCase().includes(q) || (c.classeCode ?? "").toLowerCase().includes(q);
        return semOk && classeOk && qOk;
    });
}

function renderGrid() {
    const list = filtered();
    if (list.length === 0) {
        setGrid(`<div class="col-span-full py-16 text-center text-muted-foreground-2">
            <i class="bi bi-journal-x text-4xl mb-3 block"></i>
            <p class="text-sm font-semibold text-layer-foreground mb-1">Aucun cours trouvé</p>
            <p class="text-sm">Vous n'avez aucun cours pour ces filtres.</p>
        </div>`);
        return;
    }
    setGrid(list.map(coursCard).join(""));
    document.querySelectorAll(".teacher-cours-card").forEach(el => {
        el.addEventListener("click", () => {
            const offreUeId = parseInt(el.dataset.offre, 10);
            const classeId  = parseInt(el.dataset.classe, 10);
            const card = allCards.find(c => c.offreUeId === offreUeId && c.classeId === classeId);
            if (card) openCoursModal(card);
        });
    });
}

function coursCard(c) {
    const color   = c.couleur ?? "#7c3aed";
    const semLbl  = c.semestre ? `S${c.semestre}` : "—";
    const nbEtu   = c.nbEtudiants   ?? 0;
    const nbSeances = c.nbSeancesFaites ?? 0;

    return `
    <div class="teacher-cours-card flex flex-col gap-3 p-5 bg-card border border-card-line rounded-xl
                hover:shadow-md hover:border-primary/30 transition-all cursor-pointer"
         data-offre="${c.offreUeId}" data-classe="${c.classeId}">

        <!-- Header carte -->
        <div class="flex items-start gap-3">
            <div class="flex-shrink-0 size-11 rounded-xl flex items-center justify-center text-xl shadow-sm"
                 style="background-color:${color}19; color:${color};">
                <i class="bi bi-book-half"></i>
            </div>
            <div class="flex-1 min-w-0">
                <p class="text-sm font-bold text-layer-foreground truncate">${esc(c.libelle)}</p>
                <p class="text-xs text-muted-foreground-2 mt-0.5 truncate">${esc(c.code ?? "")}</p>
            </div>
            <span class="text-[10px] font-semibold px-2 py-0.5 rounded-full border shrink-0"
                  style="background-color:${color}15; color:${color}; border-color:${color}30;">
                ${semLbl}
            </span>
        </div>

        <!-- Classe badge -->
        <div class="flex items-center gap-2 px-3 py-2 bg-surface rounded-lg border border-layer-line">
            <i class="bi bi-building text-muted-foreground-2 text-sm shrink-0"></i>
            <span class="text-xs font-semibold text-layer-foreground">${esc(c.classeCode ?? "—")}</span>
        </div>

        <!-- Stats rapides -->
        <div class="grid grid-cols-2 gap-2 text-xs text-muted-foreground-2">
            <div class="flex items-center gap-1.5">
                <i class="bi bi-people shrink-0"></i>
                <span><strong class="text-layer-foreground">${nbEtu}</strong> étudiant${nbEtu > 1 ? "s" : ""}</span>
            </div>
            <div class="flex items-center gap-1.5">
                <i class="bi bi-calendar-check shrink-0"></i>
                <span><strong class="text-layer-foreground">${nbSeances}</strong> séance${nbSeances > 1 ? "s" : ""}</span>
            </div>
            <div class="flex items-center gap-1.5">
                <i class="bi bi-clock shrink-0"></i>
                <span>${c.volumeHoraireTotal ?? 0}h prévues</span>
            </div>
            <div class="flex items-center gap-1.5">
                <i class="bi bi-award shrink-0"></i>
                <span>${c.credit ?? 0} crédit${(c.credit ?? 0) > 1 ? "s" : ""}</span>
            </div>
        </div>

        <div class="flex justify-end">
            <span class="text-xs text-muted-foreground-2 flex items-center gap-1 hover:text-primary transition-colors">
                Voir détails <i class="bi bi-arrow-right text-xs"></i>
            </span>
        </div>
    </div>`;
}

function setGrid(html) {
    const el = document.getElementById("cours-grid");
    if (el) el.innerHTML = html;
}

// ── Filtre classe ──────────────────────────────────────────────────────────────
function populateClasseFilter() {
    const sel = document.getElementById("classe-filter");
    if (!sel) return;
    const classes = [...new Map(allCards.map(c => [c.classeId, { id: c.classeId, code: c.classeCode }])).values()];
    classes.sort((a, b) => (a.code ?? "").localeCompare(b.code ?? ""));
    classes.forEach(c => {
        const opt = document.createElement("option");
        opt.value = String(c.id);
        opt.textContent = c.code ?? `Classe ${c.id}`;
        sel.appendChild(opt);
    });
}

// ── Bindings filtres ───────────────────────────────────────────────────────────
function bindFilters() {
    document.querySelectorAll(".sem-filter-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            document.querySelectorAll(".sem-filter-btn").forEach(b => {
                b.classList.remove("bg-surface-active", "text-layer-foreground");
                b.classList.add("bg-transparent", "text-muted-foreground-1");
            });
            btn.classList.add("bg-surface-active", "text-layer-foreground");
            btn.classList.remove("bg-transparent", "text-muted-foreground-1");
            currentSem = btn.dataset.sem ?? "all";
            renderGrid();
        });
    });

    document.getElementById("classe-filter")?.addEventListener("change", e => {
        currentClasse = e.target.value;
        renderGrid();
    });

    document.getElementById("cours-search")?.addEventListener("input", () => {
        clearTimeout(searchDebounce);
        searchDebounce = setTimeout(renderGrid, 250);
    });
}

// ── Modal cours ────────────────────────────────────────────────────────────────
async function openCoursModal(card) {
    activeOffreUeId = card.offreUeId;
    activeClasseId  = card.classeId;
    const color = card.couleur ?? "#7c3aed";

    // Header
    const dot = document.getElementById("modal-color-dot");
    if (dot) dot.style.background = color;
    setText("modal-cours-libelle", card.libelle);
    setText("modal-cours-meta", `${card.code ?? ""} · Classe ${card.classeCode ?? "—"} · ${card.nbEtudiants ?? 0} étudiant(s)`);

    // Stats spinner
    setHtml("modal-stats-content", spinner());

    // Lien vers la page de liste
    const link = document.getElementById("modal-liste-link");
    if (link) link.href = `/teacher/cours/${card.offreUeId}/classe/${card.classeId}/liste`;

    // Ouvrir
    if (window.HSOverlay) window.HSOverlay.open("#hs-modal-cours-detail");
    setTimeout(() => { if (typeof HSStaticMethods !== "undefined") HSStaticMethods.autoInit(); }, 50);

    // Charger les statistiques
    loadCoursStats(card.offreUeId, card.classeId);
}

async function loadCoursStats(offreUeId, classeId) {
    try {
        const s = await api.get(`/api/teacher/cours/${offreUeId}/classe/${classeId}/stats`);
        renderCoursStats(s);
    } catch {
        setHtml("modal-stats-content", `<p class="text-xs text-red-400 text-center py-6">Impossible de charger les statistiques.</p>`);
    }
}

function renderCoursStats(s) {
    const taux  = s.tauxPresenceGlobal ?? 0;
    const toH   = m => ((m ?? 0) / 60).toFixed(1) + "h";
    const color = taux >= 80 ? "#4ade80" : taux >= 60 ? "#facc15" : "#f87171";
    const offset = (314 - (taux / 100) * 314).toFixed(2);

    const html = `
    <!-- Jauge + KPIs -->
    <div class="flex flex-col sm:flex-row items-center gap-6 py-2">
        <!-- Jauge circulaire -->
        <div class="relative size-28 shrink-0">
            <svg class="size-full -rotate-90" viewBox="0 0 100 100">
                <circle cx="50" cy="50" r="45" fill="none" stroke="currentColor" stroke-width="8" class="text-surface-2"/>
                <circle cx="50" cy="50" r="45" fill="none" stroke="${color}" stroke-width="8"
                    stroke-dasharray="283" stroke-dashoffset="${offset}" stroke-linecap="round"
                    style="transition: stroke-dashoffset .6s ease"/>
            </svg>
            <div class="absolute inset-0 flex flex-col items-center justify-center">
                <span class="text-xl font-black text-layer-foreground">${taux}%</span>
                <span class="text-[10px] text-muted-foreground-2">présence</span>
            </div>
        </div>
        <!-- KPI cards -->
        <div class="grid grid-cols-2 gap-3 flex-1 w-full">
            ${kpiCard("bi-calendar2-check", "Séances données", s.totalSeances ?? 0, "")}
            ${kpiCard("bi-people", "Étudiants", s.nbEtudiants ?? 0, "")}
            ${kpiCard("bi-clock", "Heures données", toH(s.totalMinutesProgrammes), "")}
            ${kpiCard("bi-person-check", "Présents total", s.nbPresencesTotal ?? 0, "text-green-400")}
        </div>
    </div>

    <!-- Répartition présences/absences -->
    <div class="bg-surface rounded-xl border border-layer-line p-4 mt-2">
        <p class="text-xs font-semibold text-layer-foreground mb-3">Répartition des présences</p>
        <div class="space-y-2.5">
            ${presenceBar("Présents", s.nbPresencesTotal ?? 0, (s.nbPresencesTotal ?? 0) + (s.nbAbsencesTotal ?? 0), "bg-green-400")}
            ${presenceBar("Absences", s.nbAbsencesTotal ?? 0, (s.nbPresencesTotal ?? 0) + (s.nbAbsencesTotal ?? 0), "bg-red-400")}
        </div>
    </div>`;
    setHtml("modal-stats-content", html);
}

function kpiCard(icon, label, value, cls) {
    return `<div class="bg-surface rounded-xl border border-layer-line p-3">
        <div class="flex items-center gap-2 mb-1">
            <i class="bi ${icon} text-muted-foreground-2 text-sm"></i>
            <span class="text-[11px] text-muted-foreground-2">${label}</span>
        </div>
        <p class="text-lg font-black text-layer-foreground ${cls}">${value}</p>
    </div>`;
}

function presenceBar(label, count, total, barCls) {
    const pct = total > 0 ? Math.round((count / total) * 100) : 0;
    return `<div class="flex items-center gap-3">
        <span class="text-xs text-muted-foreground-2 w-20 shrink-0">${label}</span>
        <div class="flex-1 h-2 bg-surface-2 rounded-full overflow-hidden">
            <div class="h-full ${barCls} rounded-full" style="width:${pct}%; transition: width .5s ease"></div>
        </div>
        <span class="text-xs font-semibold text-layer-foreground w-8 text-right">${count}</span>
    </div>`;
}

// ── Utilitaires ────────────────────────────────────────────────────────────────
function spinner() {
    return `<div class="flex justify-center py-8"><div class="animate-spin size-5 border-[2px] border-current border-t-transparent text-primary rounded-full"></div></div>`;
}

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

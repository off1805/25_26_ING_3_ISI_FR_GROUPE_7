import api from "../common/ClientHttp.js";

const offreUeId = window.OFFRE_UE_ID;
const classeId  = window.CLASSE_ID;

let allStudents = [];

document.addEventListener("DOMContentLoaded", async () => {
    await loadCourseInfo();
    await loadStudents();
    bindSearch();
});

// ── En-tête de la page ─────────────────────────────────────────────────────────
async function loadCourseInfo() {
    try {
        const cards = await api.get("/api/teacher/cours");
        const card  = cards.find(c => c.offreUeId === offreUeId && c.classeId === classeId);
        if (card) {
            setText("page-cours-libelle", card.libelle ?? "—");
            setText("page-cours-meta",
                `${card.code ?? ""} · Classe ${card.classeCode ?? "—"} · Sem. ${card.semestre ?? "—"}`);
        }
    } catch {
        // Non bloquant — l'en-tête reste avec le texte par défaut
    }
}

// ── Chargement des étudiants ───────────────────────────────────────────────────
async function loadStudents() {
    try {
        allStudents = await api.get(`/api/teacher/cours/${offreUeId}/classe/${classeId}/etudiants`);
        const countEl = document.getElementById("student-count");
        if (countEl) countEl.textContent = allStudents.length;
        renderList(allStudents);
    } catch {
        setHtml("student-list",
            `<p class="text-xs text-red-400 text-center py-10">Impossible de charger les étudiants.</p>`);
    }
}

function renderList(students) {
    const list = document.getElementById("student-list");
    if (!list) return;

    if (!students || students.length === 0) {
        list.innerHTML = `
        <div class="flex flex-col items-center justify-center py-10 gap-2 text-muted-foreground-2">
            <i class="bi bi-people text-3xl"></i>
            <p class="text-sm">Aucun étudiant dans cette classe.</p>
        </div>`;
        return;
    }

    list.innerHTML = `<div class="space-y-1.5" id="student-rows">${students.map(studentRow).join("")}</div>`;

    document.querySelectorAll(".student-item").forEach(el => {
        el.addEventListener("click", () => {
            const id      = parseInt(el.dataset.id, 10);
            const student = allStudents.find(s => s.etudiantId === id);
            if (student) selectStudent(student);
        });
    });
}

function studentRow(s) {
    const taux   = s.tauxPresence ?? 100;
    const color  = attendanceColor(taux);
    const bdrCls = taux >= 80 ? "border-green-400/20" : taux >= 60 ? "border-yellow-400/20" : "border-red-400/20";
    const init   = initials(s);

    return `
    <div class="student-item flex items-center gap-3 px-3 py-2.5 rounded-xl border ${bdrCls}
                bg-surface hover:border-primary/30 hover:bg-surface-2 transition-all cursor-pointer"
         data-id="${s.etudiantId}" data-name="${esc((s.prenom ?? "") + " " + (s.nom ?? ""))}">
        <div class="size-9 rounded-full flex items-center justify-center text-xs font-bold shrink-0 text-white"
             style="background: ${color}90;">${init}</div>
        <div class="flex-1 min-w-0">
            <p class="text-sm font-semibold text-layer-foreground truncate">${esc(s.prenom ?? "")} ${esc(s.nom ?? "")}</p>
            <p class="text-[11px] text-muted-foreground-2">${esc(s.matricule ?? "")}</p>
        </div>
        <div class="flex items-center gap-2 shrink-0">
            <span class="text-xs font-bold" style="color:${color}">${taux}%</span>
            ${s.nbAbsences > 0
                ? `<span class="text-[10px] px-1.5 py-0.5 bg-red-400/10 text-red-400 rounded-full border border-red-400/20">${s.nbAbsences} abs</span>`
                : ""}
            <i class="bi bi-chevron-right text-muted-foreground-2 text-xs"></i>
        </div>
    </div>`;
}

// ── Sélection d'un étudiant ────────────────────────────────────────────────────
function selectStudent(student) {
    // Surligner la ligne active
    document.querySelectorAll(".student-item").forEach(el => {
        const active = parseInt(el.dataset.id) === student.etudiantId;
        el.classList.toggle("ring-2",          active);
        el.classList.toggle("ring-primary/40", active);
        el.classList.toggle("border-primary/40", active);
    });

    if (window.innerWidth < 768) {
        openDrawer(student);
    } else {
        showDetailRight(student);
    }
}

// ── Panneau droit (desktop) ────────────────────────────────────────────────────
function showDetailRight(student) {
    const empty  = document.getElementById("right-empty");
    const detail = document.getElementById("right-detail");
    if (empty)  empty.classList.add("hidden");
    if (detail) {
        detail.classList.remove("hidden");
        detail.innerHTML = buildDetailHTML(student);
    }
}

// ── Drawer mobile (bottom sheet) ───────────────────────────────────────────────
function openDrawer(student) {
    const drawer   = document.getElementById("student-drawer");
    const backdrop = document.getElementById("drawer-backdrop");

    const color = attendanceColor(student.tauxPresence ?? 100);
    const init  = initials(student);

    // Remplir l'en-tête fixe du drawer
    const avatar = document.getElementById("drawer-avatar");
    if (avatar) { avatar.textContent = init; avatar.style.background = `${color}90`; }
    setText("drawer-student-name",      `${student.prenom ?? ""} ${student.nom ?? ""}`);
    setText("drawer-student-matricule", student.matricule ?? "—");

    // Remplir le contenu — sans le bloc avatar/nom (déjà dans l'en-tête)
    const content = document.getElementById("drawer-content");
    if (content) content.innerHTML = buildDetailHTML(student, false);

    // Partir du bas de l'écran, puis animer vers le haut via style.transform
    drawer.style.transform = "translateY(100%)";
    drawer.classList.remove("hidden");
    backdrop.classList.remove("hidden");
    requestAnimationFrame(() => requestAnimationFrame(() => {
        drawer.style.transform = "translateY(0)";
    }));
}

window.closeStudentDrawer = function () {
    const drawer   = document.getElementById("student-drawer");
    const backdrop = document.getElementById("drawer-backdrop");
    drawer.style.transform = "translateY(100%)";
    setTimeout(() => {
        drawer.classList.add("hidden");
        backdrop.classList.add("hidden");
    }, 300);
};

// ── Construction du HTML de détail ────────────────────────────────────────────
// showHeader=true  → panneau droit desktop (inclut avatar + nom)
// showHeader=false → contenu du drawer mobile (l'en-tête fixe le montre déjà)
function buildDetailHTML(s, showHeader = true) {
    const taux    = s.tauxPresence ?? 100;
    const color   = attendanceColor(taux);
    const offset  = (283 - (taux / 100) * 283).toFixed(2);
    const toH     = m => ((m ?? 0) / 60).toFixed(1) + "h";
    const presMin = Math.max(0, (s.totalMinutesProgrammes ?? 0) - (s.minutesAbsence ?? 0));
    const init    = initials(s);

    return `
    <div class="space-y-5">

        ${showHeader ? `
        <!-- Avatar + nom (desktop uniquement) -->
        <div class="flex items-center gap-4">
            <div class="size-14 rounded-2xl flex items-center justify-center text-xl font-bold text-white shrink-0"
                 style="background: ${color}90;">${init}</div>
            <div class="min-w-0">
                <h2 class="text-lg font-bold text-layer-foreground">${esc(s.prenom ?? "")} ${esc(s.nom ?? "")}</h2>
                <p class="text-sm text-muted-foreground-2">${esc(s.matricule ?? "—")}</p>
            </div>
        </div>` : ""}

        <!-- Jauge + métriques -->
        <div class="flex items-center gap-5 bg-surface rounded-2xl border border-layer-line p-4">
            <div class="relative size-24 shrink-0">
                <svg class="size-full -rotate-90" viewBox="0 0 100 100">
                    <circle cx="50" cy="50" r="45" fill="none" stroke="currentColor" stroke-width="9"
                        class="text-surface-2"/>
                    <circle cx="50" cy="50" r="45" fill="none" stroke="${color}" stroke-width="9"
                        stroke-dasharray="283" stroke-dashoffset="${offset}" stroke-linecap="round"
                        style="transition: stroke-dashoffset .6s ease"/>
                </svg>
                <div class="absolute inset-0 flex flex-col items-center justify-center">
                    <span class="text-lg font-black text-layer-foreground">${taux}%</span>
                    <span class="text-[9px] text-muted-foreground-2">présence</span>
                </div>
            </div>
            <dl class="flex-1 space-y-0">
                ${statRow("Heures programmées", toH(s.totalMinutesProgrammes))}
                ${statRow("Heures de présence", toH(presMin))}
                ${statRow("Heures d'absence",   toH(s.minutesAbsence))}
                ${statRow("Nb d'absences",       s.nbAbsences ?? 0)}
            </dl>
        </div>

        <!-- Répartition du temps -->
        <div class="bg-surface rounded-2xl border border-layer-line p-4">
            <p class="text-xs font-semibold text-layer-foreground mb-3">Répartition du temps</p>
            <div class="space-y-2.5">
                ${presenceBar("Présence", presMin,             s.totalMinutesProgrammes ?? 1, "bg-green-400")}
                ${presenceBar("Absence",  s.minutesAbsence ?? 0, s.totalMinutesProgrammes ?? 1, "bg-red-400")}
            </div>
        </div>

    </div>`;
}

function statRow(label, value) {
    return `<div class="flex justify-between items-center py-1.5 border-b border-dashed border-layer-line last:border-0">
        <dt class="text-xs text-muted-foreground-2">${label}</dt>
        <dd class="text-xs font-semibold text-layer-foreground">${value}</dd>
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

// ── Recherche ──────────────────────────────────────────────────────────────────
function bindSearch() {
    document.getElementById("student-search")?.addEventListener("input", e => {
        const q = e.target.value.toLowerCase();
        document.querySelectorAll(".student-item").forEach(el => {
            const name = (el.dataset.name ?? "").toLowerCase();
            el.style.display = !q || name.includes(q) ? "" : "none";
        });
    });
}

// ── Utilitaires ────────────────────────────────────────────────────────────────
function attendanceColor(taux) {
    return taux >= 80 ? "#4ade80" : taux >= 60 ? "#facc15" : "#f87171";
}

function initials(s) {
    return ((s.prenom ?? "?")[0] + (s.nom ?? "?")[0]).toUpperCase();
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

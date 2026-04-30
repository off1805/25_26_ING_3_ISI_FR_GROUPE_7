import { TokenService } from "../common/application/TokenService.js";

function $(id) { return document.getElementById(id); }

function showFeedback(message, tone = "info") {
    const box = $("config-feedback");
    if (!box) return;
    const styles = {
        info:    "border-blue-200 bg-blue-50 text-blue-700",
        success: "border-green-200 bg-green-50 text-green-700",
        error:   "border-red-200 bg-red-50 text-red-700"
    };
    box.className = `rounded-2xl border px-4 py-3 text-sm font-semibold ${styles[tone] ?? styles.info}`;
    box.textContent = message;
    box.classList.remove("hidden");
    setTimeout(() => box.classList.add("hidden"), 5000);
}

async function apiRequest(url, options = {}) {
    const token = await TokenService.getToken();
    const response = await fetch(url, {
        headers: {
            "Content-Type": "application/json",
            ...(token ? { Authorization: `Bearer ${token}` } : {})
        },
        ...options
    });
    if (!response.ok) {
        const payload = await response.json().catch(() => null);
        throw new Error(payload?.message || `Erreur ${response.status}`);
    }
    if (response.status === 204) return null;
    return response.json();
}

function renderAnneeItem(annee) {
    const item = document.createElement("div");
    item.className = `flex items-center justify-between gap-4 rounded-2xl border px-4 py-4 transition-colors ${
        annee.active
            ? "border-primary/40 bg-primary/5"
            : "border-card-line bg-background-1"
    }`;

    const label = document.createElement("div");
    label.className = "flex items-center gap-3 min-w-0";

    const badge = document.createElement("span");
    if (annee.active) {
        badge.className = "inline-flex items-center rounded-full bg-primary/10 px-2.5 py-1 text-[11px] font-bold uppercase tracking-wide text-primary shrink-0";
        badge.textContent = "Active";
    } else {
        badge.className = "inline-flex items-center rounded-full bg-muted px-2.5 py-1 text-[11px] font-bold uppercase tracking-wide text-muted-foreground-2 shrink-0";
        badge.textContent = "Inactive";
    }

    const text = document.createElement("span");
    text.className = "text-sm font-bold text-layer-foreground";
    text.textContent = `${annee.anneeDebut} – ${annee.anneeFin}`;

    label.appendChild(badge);
    label.appendChild(text);

    const actions = document.createElement("div");

    if (!annee.active) {
        const btn = document.createElement("button");
        btn.type = "button";
        btn.className = "inline-flex items-center gap-1.5 rounded-xl bg-primary px-4 py-2 text-xs font-bold text-white hover:bg-primary/90 active:scale-95 transition-all";
        btn.innerHTML = `
            <svg xmlns="http://www.w3.org/2000/svg" width="13" height="13" viewBox="0 0 24 24"
                 fill="none" stroke="currentColor" stroke-width="2.5">
                <polyline points="20 6 9 17 4 12"/>
            </svg>
            Activer
        `;
        btn.addEventListener("click", () => activateAnnee(annee.id));
        actions.appendChild(btn);
    } else {
        const indicator = document.createElement("div");
        indicator.className = "flex items-center gap-1.5 text-xs font-semibold text-primary";
        indicator.innerHTML = `
            <span class="size-1.5 rounded-full bg-primary animate-pulse inline-block"></span>
            En cours
        `;
        actions.appendChild(indicator);
    }

    item.appendChild(label);
    item.appendChild(actions);
    return item;
}

async function loadAnnees() {
    const list = $("annees-list");
    if (!list) return;

    try {
        const annees = await apiRequest("/api/annee-scolaire");

        list.innerHTML = "";

        if (!annees || annees.length === 0) {
            list.innerHTML = `<p class="text-sm text-muted-foreground-2">Aucune année scolaire enregistrée.</p>`;
            return;
        }

        annees
            .sort((a, b) => b.anneeDebut - a.anneeDebut)
            .forEach(annee => list.appendChild(renderAnneeItem(annee)));

    } catch (error) {
        list.innerHTML = `<p class="text-sm text-red-500">${error.message}</p>`;
    }
}

async function activateAnnee(id) {
    try {
        await apiRequest(`/api/annee-scolaire/${id}/activate`, { method: "PATCH" });
        showFeedback("Année scolaire activée avec succès.", "success");
        await loadAnnees();
    } catch (error) {
        showFeedback(error.message || "Impossible d'activer cette année.", "error");
    }
}

document.addEventListener("DOMContentLoaded", () => {
    if (typeof HSStaticMethods !== "undefined") HSStaticMethods.autoInit();

    loadAnnees();

    $("create-annee-form")?.addEventListener("submit", async (event) => {
        event.preventDefault();
        const anneeDebut = parseInt($("annee-debut")?.value, 10);

        if (!anneeDebut || anneeDebut < 2000 || anneeDebut > 2100) {
            showFeedback("Saisissez une année de début valide (ex : 2025).", "error");
            return;
        }

        try {
            await apiRequest("/api/annee-scolaire", {
                method: "POST",
                body: JSON.stringify({ anneeDebut, anneeFin: anneeDebut + 1 })
            });
            $("annee-debut").value = "";
            showFeedback(`Année ${anneeDebut} – ${anneeDebut + 1} créée.`, "success");
            await loadAnnees();
        } catch (error) {
            showFeedback(error.message || "Impossible de créer l'année.", "error");
        }
    });
});

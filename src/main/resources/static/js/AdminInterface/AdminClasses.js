import { ClasseApi } from "../common/application/ClasseApi.js";
import { GlobalErrorHandler } from "../common/GlobalErrorHandler.js";

let allClasses = [];

async function loadClasses() {
    try {
        allClasses = await ClasseApi.getAll();
        renderCards(allClasses);
        renderTableRows(allClasses);
        updateCount(allClasses.length);
    } catch (e) {
        GlobalErrorHandler.handle(e);
    }
}

function renderCards(classes) {
    const container = document.getElementById("cards-container");
    if (!container) return;
    container.innerHTML = classes.map(c => `
        <div class="bg-card rounded-2xl shadow-sm p-4 flex flex-col gap-3 hover:shadow-md transition-shadow duration-200">
            <div class="flex items-start justify-between">
                <div class="size-9 rounded-xl bg-primary/15 flex items-center justify-center">
                    <svg xmlns="http://www.w3.org/2000/svg" width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="text-primary"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/></svg>
                </div>
                <button onclick="deleteClasse(${c.id})" class="size-6 flex items-center justify-center rounded-md text-red-400 hover:bg-red-50 transition-colors">
                    <svg xmlns="http://www.w3.org/2000/svg" width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                </button>
            </div>
            <div class="flex-1">
                <p class="text-sm font-bold text-layer-foreground mb-1.5">${c.code}</p>
                <p class="text-xs text-muted-foreground-2 leading-relaxed">${c.description || ''}</p>
            </div>
            <div class="flex items-center justify-between mb-1">
                <span class="flex items-center gap-1 text-xs font-semibold text-green-600">
                    <span class="size-1.5 rounded-full bg-green-500"></span>Actif
                </span>
            </div>
        </div>
    `).join("");
}

function renderTableRows(classes) {
    const tbody = document.getElementById("table-body");
    if (!tbody) return;
    if (classes.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="4" class="px-6 py-16 text-center">
                    <p class="text-sm font-semibold text-layer-foreground">Aucune classe trouvée</p>
                </td>
            </tr>`;
        return;
    }
    tbody.innerHTML = classes.map(c => `
        <tr class="hover:bg-muted/20 transition-colors group">
            <td class="px-6 py-4">
                <p class="text-sm font-bold text-layer-foreground">${c.code}</p>
            </td>
            <td class="px-6 py-4 hidden md:table-cell">
                <p class="text-xs text-muted-foreground-2">${c.description || '—'}</p>
            </td>
            <td class="px-6 py-4">
                <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-xs font-bold bg-green-500/10 text-green-600">
                    <span class="size-1.5 rounded-full bg-green-500"></span>Actif
                </span>
            </td>
            <td class="px-6 py-4 text-right">
                <button onclick="deleteClasse(${c.id})" class="py-1 px-3 text-xs font-semibold text-red-500 hover:bg-red-50 rounded-lg transition-colors">
                    Supprimer
                </button>
            </td>
        </tr>
    `).join("");
}

function updateCount(count) {
    const el = document.getElementById("classes-count");
    if (el) el.textContent = count;
}

async function deleteClasse(id) {
    if (!confirm("Supprimer cette classe ?")) return;
    try {
        await ClasseApi.delete(id);
        await loadClasses();
    } catch (e) {
        GlobalErrorHandler.handle(e);
    }
}

window.deleteClasse = deleteClasse;

document.addEventListener("DOMContentLoaded", () => {
    loadClasses();

    const searchInput = document.getElementById("search-classes");
    if (searchInput) {
        searchInput.addEventListener("input", (e) => {
            const q = e.target.value.toLowerCase();
            const filtered = allClasses.filter(c =>
                c.code.toLowerCase().includes(q) ||
                (c.description && c.description.toLowerCase().includes(q))
            );
            renderCards(filtered);
            renderTableRows(filtered);
        });
    }
});

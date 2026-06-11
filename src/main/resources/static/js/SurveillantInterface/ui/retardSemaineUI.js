import { retardCellUI } from './retardCellUI.js';

function escapeHtml(value) {
    const div = document.createElement('div');
    div.textContent = value ?? '';
    return div.innerHTML;
}

function addDays(isoDate, days) {
    const [year, month, day] = isoDate.split('-').map(Number);
    const date = new Date(Date.UTC(year, month - 1, day));
    date.setUTCDate(date.getUTCDate() + days);
    const y = date.getUTCFullYear();
    const m = String(date.getUTCMonth() + 1).padStart(2, '0');
    const d = String(date.getUTCDate()).padStart(2, '0');
    return `${y}-${m}-${d}`;
}

function formatDate(isoDate, withYear) {
    const [year, month, day] = isoDate.split('-');
    return withYear ? `${day}/${month}/${year}` : `${day}/${month}`;
}

function retardEtudiantRowUI(etu) {
    return `
        <tr>
            <td class="px-4 py-3 sticky left-0 bg-card">
                <p class="font-semibold text-layer-foreground truncate">${escapeHtml(etu.prenom)} ${escapeHtml(etu.nom)}</p>
            </td>
            ${etu.cellules.map((cell) => `<td class="px-3 py-3 text-center">${retardCellUI(cell)}</td>`).join('')}
        </tr>`;
}

function retardEmptyEtudiantsUI() {
    return `
        <div class="flex flex-col items-center justify-center gap-3 py-12 text-center">
            <div class="size-12 rounded-xl bg-muted flex items-center justify-center">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.25" class="text-muted-foreground-2">
                    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                    <circle cx="9" cy="7" r="4"/>
                </svg>
            </div>
            <p class="text-sm font-semibold text-layer-foreground">Aucun étudiant inscrit dans cette classe</p>
        </div>`;
}

export function retardSemaineUI(semaine) {
    const theadCells = semaine.jours.map((jour, index) => `
        <th class="text-center font-semibold text-layer-foreground px-3 py-3 min-w-[88px]">
            <div>${escapeHtml(jour)}</div>
            <div class="text-[10px] font-normal text-muted-foreground-2 mt-0.5">${formatDate(addDays(semaine.semaineDebut, index), false)}</div>
        </th>`).join('');

    const rows = semaine.etudiants.map(retardEtudiantRowUI).join('');

    return `
        <div id="retard-semaine-block" class="flex flex-col gap-4">
            <div class="bg-card border border-card-line rounded-2xl p-5 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                <div>
                    <h2 class="text-lg font-bold text-layer-foreground">Retards — ${escapeHtml(semaine.classeCode)}</h2>
                    <p class="text-sm text-muted-foreground-2 mt-0.5">
                        Semaine du <strong>${formatDate(semaine.semaineDebut, true)}</strong> au <strong>${formatDate(semaine.semaineFin, true)}</strong>
                    </p>
                </div>
                <p class="text-xs text-muted-foreground-2 italic">Cliquez sur une case pour marquer / annuler un retard — sauvegarde immédiate</p>
            </div>

            <div class="bg-card border border-card-line rounded-2xl overflow-hidden">
                <div class="overflow-x-auto">
                    <table class="w-full text-sm" id="retard-table">
                        <thead>
                            <tr class="border-b border-layer-line bg-muted/40">
                                <th class="text-left font-semibold text-layer-foreground px-4 py-3 sticky left-0 bg-muted/40">Étudiant</th>
                                ${theadCells}
                            </tr>
                        </thead>
                        <tbody class="divide-y divide-layer-line">
                            ${rows}
                        </tbody>
                    </table>
                </div>

                ${semaine.etudiants.length === 0 ? retardEmptyEtudiantsUI() : ''}

                <div class="flex items-center gap-5 px-4 py-3 border-t border-layer-line text-xs text-muted-foreground-2">
                    <span class="flex items-center gap-1.5">
                        <span class="size-3 rounded-md bg-muted border-2 border-layer-line inline-block"></span>
                        À l'heure
                    </span>
                    <span class="flex items-center gap-1.5">
                        <span class="size-3 rounded-md bg-orange-500/15 border-2 border-orange-500 inline-block"></span>
                        En retard
                    </span>
                </div>
            </div>
        </div>`;
}

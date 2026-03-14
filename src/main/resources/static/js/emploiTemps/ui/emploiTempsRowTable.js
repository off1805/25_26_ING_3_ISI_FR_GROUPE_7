export function emploiTempsRowTable(libelle, dateDebut, dateFin, semaine, filiereNom, niveauNom, emploiId, seancesCount = 0) {
    const row = document.createElement("tr");
    row.className = "hover:bg-muted/20 transition-colors";

    row.dataset.emploiId = emploiId;
    row.dataset.libelle = libelle;
    row.dataset.dateDebut = dateDebut;
    row.dataset.dateFin = dateFin;
    row.dataset.semaine = semaine || '';

    const initial = libelle.charAt(0).toUpperCase();

    row.innerHTML = `
        <td class="px-6 py-4">
            <div class="flex items-center gap-3">
                <div class="size-9 rounded-xl bg-primary/10 flex items-center justify-center shrink-0 text-sm font-bold text-primary">
                    ${initial}
                </div>
                <span class="text-sm font-bold text-layer-foreground">${libelle}</span>
            </div>
        </td>

        <td class="px-6 py-4">
            <span class="text-sm">${dateDebut} - ${dateFin}</span>
        </td>

        <td class="px-6 py-4">
            <span class="inline-flex items-center px-2.5 py-1 rounded-lg text-xs font-bold bg-purple-500/10 text-purple-500">
                Semaine ${semaine || 'N/A'}
            </span>
        </td>

        <td class="px-6 py-4">
            <span class="text-sm">${filiereNom || 'N/A'}</span>
        </td>

        <td class="px-6 py-4 hidden lg:table-cell">
            <span class="text-sm">${niveauNom || 'N/A'}</span>
        </td>

        <td class="px-6 py-4 hidden lg:table-cell">
            <span class="inline-flex items-center px-2.5 py-1 rounded-lg text-xs font-bold bg-blue-500/10 text-blue-500">
                ${seancesCount} séance(s)
            </span>
        </td>

        <td class="px-6 py-4 text-right">
            <div class="hs-dropdown relative inline-flex">
                <button type="button"
                        class="hs-dropdown-toggle size-8 flex items-center justify-center rounded-lg text-muted-foreground-2 hover:bg-muted hover:text-layer-foreground transition-colors focus:outline-none"
                        aria-haspopup="menu" aria-expanded="false">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24"
                         fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="12" cy="5" r="1"/>
                        <circle cx="12" cy="12" r="1"/>
                        <circle cx="12" cy="19" r="1"/>
                    </svg>
                </button>
                <div class="hs-dropdown-menu transition-[opacity,margin] duration-200 hs-dropdown-open:opacity-100 opacity-0 hidden min-w-44 bg-layer border border-card-line shadow-lg rounded-xl z-20 p-1"
                     role="menu">
                    <button type="button"
                            class="btn-view-seances flex items-center gap-x-2.5 w-full py-2 px-3 text-sm text-layer-foreground hover:bg-muted rounded-lg transition-colors"
                            data-emploi-id="${emploiId}">
                        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24"
                             fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <circle cx="12" cy="12" r="3"/>
                            <path d="M22 12c-2.667 4.667-6 7-10 7s-7.333-2.333-10-7c2.667-4.667 6-7 10-7s7.333 2.333 10 7z"/>
                        </svg>
                        Voir séances
                    </button>
                    <button type="button"
                            class="btn-edit-emploi flex items-center gap-x-2.5 w-full py-2 px-3 text-sm text-layer-foreground hover:bg-muted rounded-lg transition-colors"
                            data-emploi-id="${emploiId}">
                        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24"
                             fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                        </svg>
                        Modifier
                    </button>
                    <button type="button"
                            class="btn-add-seance-to-emploi flex items-center gap-x-2.5 w-full py-2 px-3 text-sm text-green-600 hover:bg-green-50 rounded-lg transition-colors"
                            data-emploi-id="${emploiId}">
                        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24"
                             fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <circle cx="12" cy="12" r="10"/>
                            <line x1="12" y1="8" x2="12" y2="16"/>
                            <line x1="8" y1="12" x2="16" y2="12"/>
                        </svg>
                        Ajouter séance
                    </button>
                    <button type="button"
                            class="btn-delete-emploi flex items-center gap-x-2.5 w-full py-2 px-3 text-sm text-red-500 hover:bg-red-50 rounded-lg transition-colors"
                            data-emploi-id="${emploiId}">
                        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24"
                             fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <polyline points="3 6 5 6 21 6"/>
                            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                        </svg>
                        Supprimer
                    </button>
                </div>
            </div>
        </td>
    `;

    return row;
}
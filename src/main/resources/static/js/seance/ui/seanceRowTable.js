export function seanceRowTable(seance, seanceDate, heureDebut, heureFin, salle, seanceId, enseignantNom, coursNom) {
    const row = document.createElement("tr");
    row.className = "hover:bg-muted/20 transition-colors";

    row.dataset.seanceId = seanceId;
    row.dataset.libelle = seance;
    row.dataset.dateSeance = seanceDate;
    row.dataset.heureDebut = heureDebut;
    row.dataset.heureFin = heureFin;
    row.dataset.salle = salle;

    const initial = seance.charAt(0).toUpperCase();

    row.innerHTML = `
        <td class="px-6 py-4">
            <div class="flex items-center gap-3">
                <div class="size-9 rounded-xl bg-primary/10 flex items-center justify-center shrink-0 text-sm font-bold text-primary">
                    ${initial}
                </div>
                <span class="text-sm font-bold text-layer-foreground">${seance}</span>
            </div>
        </td>

        <td class="px-6 py-4">
            <span class="text-sm">${seanceDate}</span>
        </td>

        <td class="px-6 py-4">
            <span class="text-sm font-medium">${heureDebut} - ${heureFin}</span>
        </td>

        <td class="px-6 py-4">
            <span class="inline-flex items-center px-2.5 py-1 rounded-lg text-xs font-bold bg-blue-500/10 text-blue-500">
                ${salle}
            </span>
        </td>

        <td class="px-6 py-4 hidden lg:table-cell">
            <span class="text-sm">${enseignantNom || 'N/A'}</span>
        </td>

        <td class="px-6 py-4 hidden lg:table-cell">
            <span class="text-sm">${coursNom || 'N/A'}</span>
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
                <div class="hs-dropdown-menu transition-[opacity,margin] duration-200 hs-dropdown-open:opacity-100 opacity-0 hidden min-w-36 bg-layer border border-card-line shadow-lg rounded-xl z-20 p-1"
                     role="menu">
                    <button type="button"
                            class="btn-edit-seance flex items-center gap-x-2.5 w-full py-2 px-3 text-sm text-layer-foreground hover:bg-muted rounded-lg transition-colors"
                            data-seance-id="${seanceId}">
                        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24"
                             fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                        </svg>
                        Modifier
                    </button>
                    <button type="button"
                            class="btn-delete-seance flex items-center gap-x-2.5 w-full py-2 px-3 text-sm text-red-500 hover:bg-red-50 rounded-lg transition-colors"
                            data-seance-id="${seanceId}">
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
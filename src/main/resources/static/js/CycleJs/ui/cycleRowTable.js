/**
 * Génère une ligne <tr> pour le tableau des cycles.
 *
 * @param {Object} cycle - Données du cycle
 * @param {string|number} cycle.id
 * @param {string} cycle.name
 * @param {string} cycle.code
 * @param {number} cycle.durationYears
 * @param {string} cycle.description
 * @param {string} cycle.status  - "ACTIVE" | "INACTIVE"
 * @returns {HTMLTableRowElement}
 */
export function cycleRowTable(cycle) {
    const row = document.createElement("tr");
    row.className = "hover:bg-muted/20 transition-colors group";
    row.dataset.cycleId = cycle.id;

    const isActive = cycle.status === 'ACTIVE';
    const statusLabel = isActive ? 'Actif' : 'Inactif';
    const statusClass = isActive
        ? 'bg-green-500/10 text-green-600'
        : 'bg-red-500/10 text-red-500';

    row.innerHTML = `
        <!-- Nom -->
        <td class="px-6 py-2">
            <span class="text-sm text-layer-foreground">${cycle.name}</span>
        </td>
        <!-- Code -->
        <td class="px-6 py-2">
            <span class="text-sm font-mono text-layer-foreground">${cycle.code}</span>
        </td>
        <!-- Durée -->
        <td class="px-6 py-2">
            <span class="text-sm text-layer-foreground">${cycle.durationYears} an(s)</span>
        </td>
        <!-- Statut -->
        <td class="px-6 py-2">
            <span class="inline-flex items-center px-6 py-1 rounded-full font-semibold text-xs ${statusClass}">
                ${statusLabel}
            </span>
        </td>
        <!-- ID -->
        <td class="px-6 py-2 hidden lg:table-cell">
            <span class="inline-flex items-center text-foreground text-xs">${cycle.id}</span>
        </td>
        <!-- Actions -->
        <td class="px-6 py-2 text-right">
            <div class="hs-dropdown relative inline-flex">
                <button type="button"
                    class="hs-dropdown-toggle size-8 flex items-center justify-center rounded-lg text-muted-foreground-2 hover:bg-muted hover:text-layer-foreground transition-colors focus:outline-none"
                    aria-haspopup="menu" aria-expanded="false">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"
                        fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                        <path stroke="none" d="M0 0h24v24H0z" fill="none"/>
                        <path d="M4 12a1 1 0 1 0 2 0a1 1 0 1 0 -2 0"/>
                        <path d="M11 12a1 1 0 1 0 2 0a1 1 0 1 0 -2 0"/>
                        <path d="M18 12a1 1 0 1 0 2 0a1 1 0 1 0 -2 0"/>
                    </svg>
                </button>
                <div class="hs-dropdown-menu transition-[opacity,margin] duration-200 hs-dropdown-open:opacity-100 opacity-0 hidden min-w-36 bg-dropdown border border-card-line shadow-lg rounded-xl z-20 p-1"
                    role="menu">
                    <button type="button"
                        class="btn-toggle-cycle-status flex items-center w-full text-sm rounded-lg transition-colors gap-x-3 py-2 px-3 text-dropdown-item-foreground hover:bg-dropdown-item-hover focus:outline-hidden focus:bg-dropdown-item-focus"
                        data-cycle-id="${cycle.id}" data-cycle-status="${cycle.status}">
                        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24"
                            fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M18.36 6.64A9 9 0 1 1 5.64 5.64"/>
                            <line x1="12" y1="2" x2="12" y2="12"/>
                        </svg>
                        ${isActive ? 'Désactiver' : 'Activer'}
                    </button>
                    <button type="button"
                        class="btn-delete-cycle flex items-center w-full text-sm rounded-lg transition-colors gap-x-3 py-2 px-3 text-dropdown-item-foreground hover:bg-dropdown-item-hover focus:outline-hidden focus:bg-dropdown-item-focus"
                        data-cycle-id="${cycle.id}">
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

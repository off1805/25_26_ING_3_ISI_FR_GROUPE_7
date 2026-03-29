export function specialiteRowUI(specialite) {
    return `
    <li class="flex items-center gap-x-2 p-3 text-sm bg-layer border border-layer-line text-layer-foreground last:rounded-b-lg"
        data-specialite-id="${specialite.id}">
        <div class="w-full flex justify-between items-center truncate">
            <div class="flex items-center gap-x-3">
                <div class="size-8 rounded-lg bg-primary/10 flex items-center justify-center shrink-0">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.75" stroke-linecap="round" stroke-linejoin="round" class="text-primary">
                        <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5" />
                    </svg>
                </div>
                <div class="truncate">
                    <a href="/admin/specialites/${specialite.id}" class="font-bold text-primary hover:underline">${specialite.libelle}</a>
                    <span class="text-muted-foreground-2 block text-xs">${specialite.code}</span>
                </div>
            </div>
            <div class="hs-dropdown relative inline-flex">
                <button type="button" class="hs-dropdown-toggle size-8 flex items-center justify-center rounded-lg text-muted-foreground-2 hover:bg-muted hover:text-layer-foreground transition-colors">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="12" cy="5" r="1" /><circle cx="12" cy="12" r="1" /><circle cx="12" cy="19" r="1" />
                    </svg>
                </button>
                <div class="hs-dropdown-menu transition-[opacity,margin] duration-200 hs-dropdown-open:opacity-100 opacity-0 hidden min-w-36 bg-layer border border-card-line shadow-lg rounded-xl z-20 p-1">
                    <button type="button" class="btn-delete-specialite flex items-center gap-x-2.5 w-full py-2 px-3 text-sm text-red-500 hover:bg-red-50 rounded-lg transition-colors" data-specialite-id="${specialite.id}">
                        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <polyline points="3 6 5 6 21 6" /><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
                        </svg>
                        Supprimer
                    </button>
                </div>
            </div>
        </div>
    </li>
    `;
}

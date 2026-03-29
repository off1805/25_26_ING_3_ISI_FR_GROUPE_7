export function classeRowUI(classe) {
    return `
    <li class="flex items-center gap-x-2 p-3 text-sm bg-layer border border-layer-line text-layer-foreground last:rounded-b-lg"
        data-classe-id="${classe.id}">
        <div class="w-full flex justify-between items-center truncate">
            <div class="flex items-center gap-x-3">
                <div class="size-8 rounded-lg bg-primary/10 flex items-center justify-center shrink-0">
                    <i class="bi bi-people text-primary"></i>
                </div>
                <div class="truncate">
                    <span class="font-bold text-primary">${classe.code}</span>
                    <span class="text-muted-foreground-2 block text-xs">${classe.description || ''}</span>
                </div>
            </div>
            <div class="hs-dropdown relative inline-flex">
                <button type="button" class="hs-dropdown-toggle size-8 flex items-center justify-center rounded-lg text-muted-foreground-2 hover:bg-muted hover:text-layer-foreground transition-colors">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="12" cy="5" r="1" /><circle cx="12" cy="12" r="1" /><circle cx="12" cy="19" r="1" />
                    </svg>
                </button>
                <div class="hs-dropdown-menu transition-[opacity,margin] duration-200 hs-dropdown-open:opacity-100 opacity-0 hidden min-w-36 bg-layer border border-card-line shadow-lg rounded-xl z-20 p-1">
                    <button type="button" class="btn-delete-classe flex items-center gap-x-2.5 w-full py-2 px-3 text-sm text-red-500 hover:bg-red-50 rounded-lg transition-colors" data-classe-id="${classe.id}">
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

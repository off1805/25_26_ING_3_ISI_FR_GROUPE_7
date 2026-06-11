export const RETARD_CELL_ON_TIME_CLASSES = ['bg-muted', 'border-layer-line', 'text-muted-foreground-2', 'hover:border-primary/40'];
export const RETARD_CELL_LATE_CLASSES     = ['bg-orange-500/15', 'border-orange-500', 'text-orange-500'];

export function retardCellTitle(enRetard) {
    return enRetard
        ? "En retard — cliquer pour annuler"
        : "À l'heure — cliquer pour marquer un retard";
}

const CLOCK_ICON_PATH = '<circle cx="12" cy="12" r="10"/><path d="M12 6v6l3 3"/>';

export function retardCellUI(cell) {
    const stateClasses = cell.enRetard ? RETARD_CELL_LATE_CLASSES : RETARD_CELL_ON_TIME_CLASSES;
    return `
        <button type="button"
                class="retard-cell inline-flex items-center justify-center size-8 rounded-xl border-2 transition-all ${stateClasses.join(' ')}"
                data-info-id="${cell.id}"
                title="${retardCellTitle(cell.enRetard)}">
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                ${CLOCK_ICON_PATH}
            </svg>
        </button>`;
}

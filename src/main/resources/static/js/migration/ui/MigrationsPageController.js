import { MigrationApi } from '../infrastructure/MigrationApi.js';
import { GlobalEventNotifier } from '../../common/GlobalEventNotifier.js';
import { GlobalErrorHandler } from '../../common/GlobalErrorHandler.js';
import { customAlert } from '../../common/CustomAlert.js';
import { openDestinationModal, wireDestinationModal } from './DestinationModal.js';

const migrationApi = new MigrationApi();

document.addEventListener('DOMContentLoaded', () => {
    wireDestinationModal(() => loadPendingMigrations());
    wireSearch();
    loadPendingMigrations();
});

// ── Liste des migrations en attente ──────────────────────────────────────────

async function loadPendingMigrations() {
    const filiereId = document.body.getAttribute('data-filiere-id');
    const tbody = document.getElementById('migrations-table');
    if (!filiereId || !tbody) return;

    try {
        const migrations = await migrationApi.getPending(filiereId);
        renderPendingMigrations(migrations);
        applySearchFilter();
    } catch (err) {
        tbody.innerHTML = `
            <tr class="empty-row">
                <td colspan="5" class="px-6 py-16 text-center">
                    <p class="text-sm text-red-500">Erreur lors du chargement des migrations.</p>
                </td>
            </tr>`;
    }
}

// ── Recherche côté client ─────────────────────────────────────────────────

function wireSearch() {
    document.getElementById('migration-search')?.addEventListener('input', applySearchFilter);
}

function applySearchFilter() {
    const query = document.getElementById('migration-search')?.value.toLowerCase().trim() ?? '';
    document.querySelectorAll('#migrations-table tr:not(.empty-row)').forEach(row => {
        row.style.display = row.textContent.toLowerCase().includes(query) ? '' : 'none';
    });
}

function renderPendingMigrations(migrations) {
    const tbody = document.getElementById('migrations-table');
    if (!tbody) return;

    if (!migrations || migrations.length === 0) {
        tbody.innerHTML = `
            <tr class="empty-row">
                <td colspan="5" class="px-6 py-16 text-center">
                    <p class="text-sm font-semibold text-layer-foreground">Aucune migration en attente</p>
                    <p class="text-xs text-muted-foreground-2 mt-1">
                        Les retraits de classe créeront ici des migrations en attente pour la prochaine année scolaire.
                    </p>
                </td>
            </tr>`;
        return;
    }

    tbody.innerHTML = migrations.map(m => `
        <tr class="hover:bg-muted/10 transition-colors">
            <td class="px-4 py-3">
                <p class="text-sm font-semibold text-layer-foreground">${m.nom ?? ''} ${m.prenom ?? ''}</p>
            </td>
            <td class="px-4 py-3 hidden lg:table-cell">
                <span class="inline-flex items-center px-2.5 py-1 rounded-lg text-xs font-bold bg-gray-100 text-gray-500">${m.matricule ?? '—'}</span>
            </td>
            <td class="px-4 py-3 text-sm text-muted-foreground-2">${m.classeSourceCode ?? '—'}</td>
            <td class="px-4 py-3 text-sm font-semibold text-layer-foreground">${m.classeDestinationCode ?? '—'}</td>
            <td class="pr-4 sm:pr-5 pl-2 py-3 text-right">
                <div class="flex items-center justify-end gap-1">
                    <button type="button" data-id="${m.id}" data-classe-source-id="${m.classeSourceId}"
                        data-destination-id="${m.classeDestinationId}"
                        class="btn-modify-migration size-8 flex items-center justify-center rounded-lg text-muted-foreground-2 hover:bg-muted hover:text-layer-foreground transition-colors"
                        title="Modifier la destination">
                        <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5z" />
                        </svg>
                    </button>
                    <button type="button" data-id="${m.id}"
                        class="btn-cancel-migration size-8 flex items-center justify-center rounded-lg text-muted-foreground-2 hover:bg-red-50 hover:text-red-500 dark:hover:bg-red-500/10 transition-colors"
                        title="Annuler la migration">
                        <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M18 6 6 18" />
                            <path d="m6 6 12 12" />
                        </svg>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');

    tbody.querySelectorAll('.btn-modify-migration').forEach(btn => {
        btn.addEventListener('click', () => {
            openDestinationModal({
                mode: 'update',
                migrationId: parseInt(btn.dataset.id, 10),
                classeSourceId: parseInt(btn.dataset.classeSourceId, 10),
                currentDestinationId: parseInt(btn.dataset.destinationId, 10),
            });
        });
    });

    tbody.querySelectorAll('.btn-cancel-migration').forEach(btn => {
        btn.addEventListener('click', async () => {
            const confirmed = await customAlert(
                'Annuler la migration',
                "Cette migration sera annulée et l'étudiant restera dans sa classe actuelle l'année prochaine.",
                'Annuler la migration',
                'Retour'
            );
            if (!confirmed) return;

            try {
                await migrationApi.cancel(parseInt(btn.dataset.id, 10));
                GlobalEventNotifier.eventWellDone('Migration annulée.');
                await loadPendingMigrations();
            } catch (err) {
                GlobalErrorHandler.handle(err);
            }
        });
    });
}

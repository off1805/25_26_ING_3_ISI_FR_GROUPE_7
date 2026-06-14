import { MigrationApi } from '../infrastructure/MigrationApi.js';
import { GlobalEventNotifier } from '../../common/GlobalEventNotifier.js';

const migrationApi = new MigrationApi();

// Contexte de la modale "Choisir la classe de destination"
// { mode: 'create', userIds: string[], classeSourceId } ou { mode: 'update', migrationId, classeSourceId, currentDestinationId }
let destinationContext = null;

export async function openDestinationModal(context) {
    destinationContext = context;

    const titleEl = document.getElementById('destination-modal-title');
    const msgEl = document.getElementById('destination-modal-msg');
    const errorEl = document.getElementById('destination-error');
    const select = document.getElementById('destination-classe-select');
    const confirmBtn = document.getElementById('confirm-destination-btn');

    errorEl.classList.add('hidden');
    errorEl.textContent = '';
    confirmBtn.disabled = true;

    if (context.mode === 'update') {
        titleEl.textContent = 'Modifier la migration';
        msgEl.textContent = 'Choisissez la nouvelle classe de destination pour cette migration.';
    } else {
        titleEl.textContent = context.count > 1 ? 'Migrer les étudiants vers une autre classe' : 'Migrer vers une autre classe';
        msgEl.textContent = (context.count > 1
            ? "Les étudiants resteront dans la classe jusqu'à la fin de l'année. Choisissez la classe vers laquelle ils migreront"
            : "L'étudiant restera dans la classe jusqu'à la fin de l'année. Choisissez la classe vers laquelle il migrera")
            + " à l'activation de la prochaine année scolaire.";
    }

    select.innerHTML = '<option value="" disabled selected>Chargement...</option>';
    select.disabled = true;
    try {
        if (window.HSSelect) HSSelect.getInstance(select)?.destroy();
    } catch (_) { }

    if (typeof HSOverlay !== 'undefined') {
        HSOverlay.open(document.getElementById('modal-choose-destination'));
    }

    try {
        const classes = await migrationApi.getEligibleClasses(context.classeSourceId);

        if (!classes || classes.length === 0) {
            select.innerHTML = '<option value="" disabled selected>Aucune classe disponible</option>';
            errorEl.textContent = "Aucune classe de destination compatible n'est disponible dans cette filière.";
            errorEl.classList.remove('hidden');
        } else {
            select.disabled = false;
            select.innerHTML = '<option value="" disabled selected>Sélectionner une classe</option>' +
                classes.map(c => `<option value="${c.id}" ${context.currentDestinationId == c.id ? 'selected' : ''}>${c.code}</option>`).join('');
            confirmBtn.disabled = false;
        }
    } catch (err) {
        select.innerHTML = '<option value="" disabled selected>Erreur</option>';
        errorEl.textContent = err.payload || err.message || 'Erreur lors du chargement des classes.';
        errorEl.classList.remove('hidden');
    }

    if (window.HSSelect) new HSSelect(select);
}

/**
 * @param {(context: object) => Promise<void> | void} onSuccess Appelé après une migration créée/modifiée avec succès.
 */
export function wireDestinationModal(onSuccess) {
    document.getElementById('confirm-destination-btn')?.addEventListener('click', async () => {
        const select = document.getElementById('destination-classe-select');
        const errorEl = document.getElementById('destination-error');
        const classeDestinationId = select?.value ? parseInt(select.value, 10) : null;

        if (!classeDestinationId || !destinationContext) {
            errorEl.textContent = 'Veuillez sélectionner une classe de destination.';
            errorEl.classList.remove('hidden');
            return;
        }

        const btn = document.getElementById('confirm-destination-btn');
        btn.disabled = true;

        try {
            if (destinationContext.mode === 'update') {
                await migrationApi.update(destinationContext.migrationId, classeDestinationId);
                GlobalEventNotifier.eventWellDone('Migration mise à jour.');
            } else {
                let success = 0;
                for (const userId of destinationContext.userIds) {
                    try {
                        await migrationApi.create(userId, classeDestinationId);
                        success++;
                    } catch (err) {
                        GlobalEventNotifier.eventError(`Échec pour l'étudiant ${userId} : ${err.payload || err.message}`);
                    }
                }
                if (success === 0) {
                    btn.disabled = false;
                    return;
                }
                GlobalEventNotifier.eventWellDone(`${success} migration(s) enregistrée(s) pour la prochaine année scolaire.`);
            }

            HSOverlay.close(document.getElementById('modal-choose-destination'));
            await onSuccess?.(destinationContext);
        } catch (err) {
            errorEl.textContent = err.payload || err.message || 'Une erreur est survenue.';
            errorEl.classList.remove('hidden');
        } finally {
            btn.disabled = false;
        }
    });
}

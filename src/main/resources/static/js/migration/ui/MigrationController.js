import { MigrationApi } from '../infrastructure/MigrationApi.js';
import { GlobalEventNotifier } from '../../common/GlobalEventNotifier.js';
import { GlobalErrorHandler } from '../../common/GlobalErrorHandler.js';
import { customAlert } from '../../common/CustomAlert.js';
import { getActiveClasseId, handleClasseChange } from '../../StudentJs/ui/StudentController.js';
import { openDestinationModal, wireDestinationModal } from './DestinationModal.js';

const migrationApi = new MigrationApi();

// Contexte de la modale "Renvoyer l'étudiant"
let expelContext = null;

document.addEventListener('DOMContentLoaded', () => {
    wireDestinationModal((context) => {
        if (context.mode !== 'update') window.clearSelection();
    });
    wireExpelModal();
    wireRemoveAll();
    wireGraduationEligibility();
});

// ── Sélection multiple ──────────────────────────────────────────────────────

function getCheckedUserIds() {
    return Array.from(document.querySelectorAll('.checkebox-student:checked'))
        .map(cb => cb.getAttribute('data-user-id'))
        .filter(Boolean);
}

window.clearSelection = function () {
    document.querySelectorAll('.checkebox-student').forEach(cb => { cb.checked = false; });
    const selectAllInput = document.querySelector('#select-all input[type="checkbox"]');
    if (selectAllInput) selectAllInput.checked = false;
};

function wireRemoveAll() {
    document.getElementById('remove-all')?.addEventListener('click', () => {
        window.removeSelected();
    });
}

// ── Migration : retrait individuel / groupé → choix de la classe de destination ──

window.removeSingle = function (btn) {
    const userId = btn.getAttribute('data-user-id');
    const classeSourceId = btn.getAttribute('data-classe-id') || getActiveClasseId();
    if (!userId || !classeSourceId) return;

    openDestinationModal({ mode: 'create', userIds: [userId], classeSourceId, count: 1 });
};

window.removeSelected = function () {
    const userIds = getCheckedUserIds();
    if (userIds.length === 0) {
        GlobalEventNotifier.eventError('Aucun étudiant sélectionné.');
        return;
    }

    const classeSourceId = getActiveClasseId();
    openDestinationModal({ mode: 'create', userIds, classeSourceId, count: userIds.length });
};

// ── Archivage (fin de cursus) ────────────────────────────────────────────────

window.archiveStudent = async function (btn) {
    const userId = btn.getAttribute('data-user-id');
    const classeId = btn.getAttribute('data-classe-id') || getActiveClasseId();

    const confirmed = await customAlert(
        "Archiver l'étudiant",
        "L'étudiant sera marqué comme diplômé et retiré de sa classe. Cette action est immédiate et irréversible.",
        'Archiver',
        'Annuler'
    );
    if (!confirmed) return;

    try {
        await migrationApi.archiveStudent(userId);
        GlobalEventNotifier.eventWellDone('Étudiant archivé.');
        if (classeId) await handleClasseChange(classeId);
    } catch (err) {
        GlobalErrorHandler.handle(err);
    }
};

// ── Renvoi (expulsion) ────────────────────────────────────────────────────────

window.expelStudent = function (btn) {
    expelContext = {
        userId: btn.getAttribute('data-user-id'),
        classeId: btn.getAttribute('data-classe-id') || getActiveClasseId(),
    };

    document.getElementById('form-expel')?.reset();
    document.getElementById('expel-error')?.classList.add('hidden');

    if (typeof HSOverlay !== 'undefined') {
        HSOverlay.open(document.getElementById('modal-expel-student'));
    }
};

function wireExpelModal() {
    document.getElementById('confirm-expel-btn')?.addEventListener('click', async () => {
        const form = document.getElementById('form-expel');
        const errorEl = document.getElementById('expel-error');
        const motif = form.motif.value.trim();
        const file = form.justificatif.files[0];

        if (!motif || !file) {
            errorEl.textContent = 'Le motif et le justificatif sont obligatoires.';
            errorEl.classList.remove('hidden');
            return;
        }
        if (!expelContext) return;

        const btn = document.getElementById('confirm-expel-btn');
        btn.disabled = true;

        try {
            await migrationApi.expelStudent(expelContext.userId, motif, file);
            GlobalEventNotifier.eventWellDone('Étudiant renvoyé.');
            HSOverlay.close(document.getElementById('modal-expel-student'));
            if (expelContext.classeId) await handleClasseChange(expelContext.classeId);
        } catch (err) {
            errorEl.textContent = err.payload || err.message || 'Une erreur est survenue.';
            errorEl.classList.remove('hidden');
        } finally {
            btn.disabled = false;
        }
    });
}

// ── Éligibilité à l'archivage (phase terminale) ──────────────────────────────

function wireGraduationEligibility() {
    document.addEventListener('classe-students-rendered', (e) => {
        updateArchiveButtonsVisibility(e.detail.classeId);
    });

    const initialClasseId = getActiveClasseId();
    if (initialClasseId) updateArchiveButtonsVisibility(initialClasseId);
}

async function updateArchiveButtonsVisibility(classeId) {
    if (!classeId) return;

    try {
        const result = await migrationApi.checkGraduationEligibility(classeId);
        document.querySelectorAll('.archive-btn').forEach(btn => {
            btn.classList.toggle('hidden', !result.eligible);
            btn.classList.toggle('flex', result.eligible);
        });
    } catch (_) {
        // Classe non éligible ou erreur réseau : on laisse le bouton masqué.
    }
}

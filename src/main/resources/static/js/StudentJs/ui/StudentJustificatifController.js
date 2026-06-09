import { JustificatifApi } from '../infrastructure/JustificatifApi.js';
import { TokenService } from '../../common/application/TokenService.js';

// ── Init ─────────────────────────────────────────────────────────────────────

document.addEventListener('DOMContentLoaded', async () => {
    await chargerJustificatifs();
    attacherFormulaire();
});

// ── Chargement des justificatifs ──────────────────────────────────────────────

async function chargerJustificatifs() {
    const etudiantId = window.ETUDIANT_PROFILE_ID;
    if (!etudiantId) return;

    try {
        const justificatifs = await JustificatifApi.getByEtudiant(etudiantId);
        afficherStats(justificatifs);
        afficherListes(justificatifs);
    } catch (e) {
        console.error('Erreur chargement justificatifs:', e);
    }
}

function afficherStats(justificatifs) {
    const pending  = justificatifs.filter(j => j.statut === 'PENDING').length;
    const approved = justificatifs.filter(j => j.statut === 'APPROVED').length;
    const rejected = justificatifs.filter(j => j.statut === 'REJECTED').length;

    document.querySelectorAll('[data-stat-pending]').forEach(el => el.textContent = pending);
    document.querySelectorAll('[data-stat-approved]').forEach(el => el.textContent = approved);
    document.querySelectorAll('[data-stat-rejected]').forEach(el => el.textContent = rejected);
}

function afficherListes(justificatifs) {
    renderListe('list-pending',  justificatifs.filter(j => j.statut === 'PENDING'),  'orange');
    renderListe('list-approved', justificatifs.filter(j => j.statut === 'APPROVED'), 'green');
    renderListe('list-rejected', justificatifs.filter(j => j.statut === 'REJECTED'), 'red');
}

function renderListe(containerId, items, color) {
    const container = document.getElementById(containerId);
    if (!container) return;

    if (items.length === 0) {
        container.innerHTML = `<p class="px-4 py-4 text-center text-xs text-muted-foreground-2">Aucune entrée</p>`;
        return;
    }

    container.innerHTML = items.map(j => `
        <div class="px-4 py-3 border-b border-card-line last:border-b-0">
            <p class="text-sm font-semibold text-layer-foreground truncate">${j.motif}</p>
            <p class="text-xs text-muted-foreground-2 mt-0.5">${formatDate(j.dateAbsence)}</p>
            ${j.commentaireAP ? `<p class="text-xs text-${color}-600 mt-1 font-medium italic">${j.commentaireAP}</p>` : ''}
        </div>
    `).join('');
}

function formatDate(dateStr) {
    if (!dateStr) return '—';
    const d = new Date(dateStr);
    return d.toLocaleDateString('fr-FR', { day: '2-digit', month: 'short', year: 'numeric' });
}

// ── Formulaire de soumission ──────────────────────────────────────────────────

function attacherFormulaire() {
    const form = document.getElementById('form-justificatif');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const btn = form.querySelector('button[type="submit"]');
        btn.disabled = true;
        btn.textContent = 'Envoi…';

        const etudiantId = window.ETUDIANT_PROFILE_ID;
        const formData = new FormData();
        formData.append('etudiantId', etudiantId);
        formData.append('motif', form.querySelector('[name="motif"]').value.trim());
        formData.append('dateAbsence', form.querySelector('[name="dateAbsence"]').value);

        const seanceSelect = form.querySelector('[name="seanceId"]');
        if (seanceSelect?.value) formData.append('seanceId', seanceSelect.value);

        const fichier = form.querySelector('[name="fichier"]')?.files[0];
        if (fichier) formData.append('fichier', fichier);

        try {
            await JustificatifApi.soumettre(formData);
            afficherFeedback('Justificatif soumis avec succès.', 'success');
            form.reset();
            await chargerJustificatifs();
        } catch (err) {
            afficherFeedback('Erreur lors de la soumission.', 'error');
        } finally {
            btn.disabled = false;
            btn.textContent = 'Soumettre';
        }
    });
}

function afficherFeedback(message, type) {
    const box = document.getElementById('feedback-justificatif');
    if (!box) return;
    box.textContent = message;
    box.className = `rounded-xl px-4 py-3 text-sm font-semibold text-center mb-3 ${
        type === 'success' ? 'bg-green-50 border border-green-200 text-green-700' : 'bg-red-50 border border-red-200 text-red-700'
    }`;
    box.classList.remove('hidden');
    setTimeout(() => box.classList.add('hidden'), 4000);
}

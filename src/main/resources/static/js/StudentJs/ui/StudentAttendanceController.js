import api from '../../common/ClientHttp.js';
import { TokenService } from '../../common/application/TokenService.js';

// ── Init ─────────────────────────────────────────────────────────────────────

document.addEventListener('DOMContentLoaded', async () => {
    const token = await TokenService.getToken();
    if (!token) {
        showState('not-logged-state');
        return;
    }

    // Vérifier si un code QR est dans l'URL (scan automatique)
    const params = new URLSearchParams(window.location.search);
    const code = params.get('code');

    if (code) {
        await handleQRScan(code);
    } else {
        showState('pin-form-state');
        attacherFormulairePIN();
    }
});

// ── Gestion QR ────────────────────────────────────────────────────────────────

async function handleQRScan(code) {
    showState('auto-scan-state');
    try {
        await api.get(`/api/presences/scan?code=${encodeURIComponent(code)}`);
        showState('success-state');
    } catch (err) {
        showState('pin-form-state');
        afficherFeedback('Code QR invalide ou expiré.', 'error');
        attacherFormulairePIN();
    }
}

// ── Gestion PIN ───────────────────────────────────────────────────────────────

function attacherFormulairePIN() {
    const form = document.getElementById('pin-form');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const pin = document.getElementById('pin-input')?.value?.trim();
        if (!pin) return;

        const btn = form.querySelector('button[type="submit"]');
        btn.disabled = true;
        btn.textContent = 'Vérification…';

        try {
            await api.get(`/api/presences/scan?code=${encodeURIComponent(pin)}`);
            showState('success-state');
        } catch (err) {
            afficherFeedback('Code PIN invalide ou expiré.', 'error');
        } finally {
            btn.disabled = false;
            btn.textContent = 'Confirmer ma présence';
        }
    });
}

// ── Helpers ───────────────────────────────────────────────────────────────────

function showState(stateId) {
    ['auto-scan-state', 'pin-form-state', 'success-state', 'not-logged-state'].forEach(id => {
        document.getElementById(id)?.classList.add('hidden');
    });
    document.getElementById(stateId)?.classList.remove('hidden');
}

function afficherFeedback(message, type) {
    const box = document.getElementById('feedback-box');
    if (!box) return;
    box.textContent = message;
    box.className = `rounded-2xl border px-4 py-3 text-sm font-semibold text-center ${
        type === 'success' ? 'bg-green-50 border-green-200 text-green-700' : 'bg-red-50 border-red-200 text-red-700'
    }`;
    box.classList.remove('hidden');
    setTimeout(() => box.classList.add('hidden'), 4000);
}

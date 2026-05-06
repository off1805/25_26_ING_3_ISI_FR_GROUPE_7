import { TokenService } from "../common/application/TokenService.js";

function $(id) {
    return document.getElementById(id);
}

function showState(id) {
    ["auto-scan-state", "pin-form-state", "success-state", "not-logged-state"].forEach((stateId) => {
        $(stateId)?.classList.toggle("hidden", stateId !== id);
    });
}

function showFeedback(message, tone = "info") {
    const box = $("feedback-box");
    if (!box) return;

    const styles = {
        info: "border-blue-200 bg-blue-50 text-blue-700",
        success: "border-green-200 bg-green-50 text-green-700",
        error: "border-red-200 bg-red-50 text-red-700"
    };
    box.className = `rounded-2xl border px-4 py-3 text-sm font-semibold ${styles[tone] ?? styles.info}`;
    box.textContent = message;
    box.classList.remove("hidden");
}

import api from "../common/ClientHttp.js";

async function markPresent(code) {
    try {
        await api.get(`/api/presences/scan?code=${encodeURIComponent(code)}`);
        showState("success-state");
    } catch (error) {
        if (error.response && error.response.status === 401) {
            showState("not-logged-state");
            return;
        }
        throw new Error(error.message || `Erreur lors de l'enregistrement.`);
    }
}

document.addEventListener("DOMContentLoaded", async () => {
    const params = new URLSearchParams(window.location.search);
    const code = params.get("code");

    if (code) {
        showState("auto-scan-state");
        try {
            await markPresent(code);
        } catch (error) {
            showState(null);
            showFeedback(error.message || "Impossible d'enregistrer la présence.", "error");
        }
        return;
    }

    showState("pin-form-state");

    $("pin-form")?.addEventListener("submit", async (event) => {
        event.preventDefault();
        const pinValue = $("pin-input")?.value.trim();

        if (!pinValue) {
            showFeedback("Saisis le code PIN avant de valider.", "error");
            return;
        }

        try {
            await markPresent(pinValue);
        } catch (error) {
            showFeedback(error.message || "Code PIN invalide ou expiré.", "error");
        }
    });
});

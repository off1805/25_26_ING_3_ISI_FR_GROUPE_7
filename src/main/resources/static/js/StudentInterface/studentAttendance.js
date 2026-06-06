import api from "../common/ClientHttp.js";

function $(id) {
    return document.getElementById(id);
}

function showState(id) {
    ["auto-scan-state", "geo-check-state", "pin-form-state", "success-state", "not-logged-state"].forEach((stateId) => {
        $(stateId)?.classList.toggle("hidden", stateId !== id);
    });
}

function showFeedback(message, tone = "info") {
    const box = $("feedback-box");
    if (!box) return;
    const styles = {
        info:    "border-blue-200 bg-blue-50 text-blue-700",
        success: "border-green-200 bg-green-50 text-green-700",
        error:   "border-red-200 bg-red-50 text-red-700",
        warning: "border-amber-200 bg-amber-50 text-amber-700",
    };
    box.className = `rounded-2xl border px-4 py-3 text-sm font-semibold ${styles[tone] ?? styles.info}`;
    box.textContent = message;
    box.classList.remove("hidden");
}

function haversineDistance(lat1, lng1, lat2, lng2) {
    const R = 6371000;
    const phi1 = (lat1 * Math.PI) / 180;
    const phi2 = (lat2 * Math.PI) / 180;
    const dphi = ((lat2 - lat1) * Math.PI) / 180;
    const dlambda = ((lng2 - lng1) * Math.PI) / 180;
    const a =
        Math.sin(dphi / 2) ** 2 +
        Math.cos(phi1) * Math.cos(phi2) * Math.sin(dlambda / 2) ** 2;
    return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
}

async function checkGeolocation() {
    let school;
    try {
        school = await api.get("/api/schools/current");
    } catch {
        return; // école non configurée → pas de vérification géographique
    }

    if (!school || !school.latitude || !school.longitude || !school.rayon) {
        return;
    }

    if (!navigator.geolocation) {
        throw new Error(
            "La géolocalisation n'est pas supportée par votre navigateur. " +
            "Impossible de vérifier votre position avant de confirmer la présence."
        );
    }

    if (!window.isSecureContext) {
        throw new Error(
            "La vérification de position nécessite une connexion sécurisée (HTTPS). " +
            "Veuillez contacter votre administrateur."
        );
    }

    return new Promise((resolve, reject) => {
        navigator.geolocation.getCurrentPosition(
            (position) => {
                const dist = haversineDistance(
                    position.coords.latitude,
                    position.coords.longitude,
                    school.latitude,
                    school.longitude
                );

                if (dist > school.rayon) {
                    reject(new Error(
                        `Vous êtes trop loin de l'école pour confirmer votre présence. ` +
                        `Votre distance détectée : ${Math.round(dist)} m — ` +
                        `périmètre autorisé : ${Math.round(school.rayon)} m. ` +
                        `Rapprochez-vous de l'école et réessayez.`
                    ));
                } else {
                    resolve();
                }
            },
            (err) => {
                const messages = {
                    1: "Permission de localisation refusée. Autorisez l'accès à votre position dans les paramètres du navigateur pour pouvoir confirmer votre présence.",
                    2: "Impossible de déterminer votre position. Vérifiez que la localisation est activée sur votre appareil.",
                    3: "La vérification de position a pris trop de temps. Réessayez dans un endroit avec un meilleur signal GPS.",
                };
                reject(new Error(messages[err.code] || "Erreur de géolocalisation. Impossible de vérifier votre position."));
            },
            { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 }
        );
    });
}

async function markPresent(code) {
    try {
        await api.get(`/api/presences/scan?code=${encodeURIComponent(code)}`);
        showState("success-state");
    } catch (error) {
        if (error.response && error.response.status === 401) {
            showState("not-logged-state");
            return;
        }
        throw new Error(error.message || "Erreur lors de l'enregistrement.");
    }
}

document.addEventListener("DOMContentLoaded", async () => {
    const params = new URLSearchParams(window.location.search);
    const code = params.get("code");

    if (code) {
        showState("geo-check-state");
        try {
            await checkGeolocation();
            showState("auto-scan-state");
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

        const btn = event.currentTarget.querySelector("button[type=submit]");
        const originalLabel = btn?.textContent;
        if (btn) {
            btn.disabled = true;
            btn.textContent = "Vérification de la position…";
        }

        try {
            await checkGeolocation();
            if (btn) btn.textContent = "Enregistrement…";
            await markPresent(pinValue);
        } catch (error) {
            showFeedback(error.message || "Code PIN invalide ou expiré.", "error");
        } finally {
            if (btn) {
                btn.disabled = false;
                btn.textContent = originalLabel ?? "Confirmer ma présence";
            }
        }
    });
});

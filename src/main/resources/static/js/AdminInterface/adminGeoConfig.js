import api from "../common/ClientHttp.js";

const DEFAULT_LAT = 3.8480;
const DEFAULT_LNG = 11.5021;
const DEFAULT_ZOOM = 14;

let map = null;
let marker = null;
let circle = null;
let currentSchoolId = null;

function $(id) { return document.getElementById(id); }

function showGeoFeedback(message, tone = "info") {
    const box = $("geo-feedback");
    if (!box) return;
    const styles = {
        info:    "border-blue-200 bg-blue-50 text-blue-700",
        success: "border-green-200 bg-green-50 text-green-700",
        error:   "border-red-200 bg-red-50 text-red-700"
    };
    box.className = `rounded-2xl border px-4 py-3 text-sm font-semibold mb-4 ${styles[tone] ?? styles.info}`;
    box.textContent = message;
    box.classList.remove("hidden");
    setTimeout(() => box.classList.add("hidden"), 5000);
}

function getRadiusValue() {
    return parseFloat($("geo-rayon")?.value) || 200;
}

function updateMarkerAndCircle(lat, lng) {
    const radius = getRadiusValue();

    if (marker) {
        marker.setLatLng([lat, lng]);
    } else {
        marker = L.marker([lat, lng], { draggable: true }).addTo(map);
        marker.on("dragend", () => {
            const pos = marker.getLatLng();
            syncInputs(pos.lat, pos.lng);
            if (circle) circle.setLatLng([pos.lat, pos.lng]);
        });
    }

    if (circle) {
        circle.setLatLng([lat, lng]);
        circle.setRadius(radius);
    } else {
        circle = L.circle([lat, lng], {
            radius,
            color: "var(--color-primary, #6366f1)",
            fillColor: "var(--color-primary, #6366f1)",
            fillOpacity: 0.12,
            weight: 2,
        }).addTo(map);
    }

    map.setView([lat, lng], map.getZoom());
}

function syncInputs(lat, lng) {
    const latInput = $("geo-latitude");
    const lngInput = $("geo-longitude");
    if (latInput) latInput.value = lat.toFixed(6);
    if (lngInput) lngInput.value = lng.toFixed(6);
}

function initMap(lat, lng) {
    if (map) return;

    map = L.map("geo-map").setView([lat, lng], DEFAULT_ZOOM);

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
        maxZoom: 19,
    }).addTo(map);

    updateMarkerAndCircle(lat, lng);

    map.on("click", (e) => {
        syncInputs(e.latlng.lat, e.latlng.lng);
        updateMarkerAndCircle(e.latlng.lat, e.latlng.lng);
    });
}

async function loadCurrentSchool() {
    try {
        const school = await api.get("/api/schools/current");
        if (!school) {
            initMap(DEFAULT_LAT, DEFAULT_LNG);
            return;
        }

        currentSchoolId = school.id;
        const lat = school.latitude || DEFAULT_LAT;
        const lng = school.longitude || DEFAULT_LNG;
        const rayon = school.rayon || 200;

        if ($("geo-latitude")) $("geo-latitude").value = lat.toFixed(6);
        if ($("geo-longitude")) $("geo-longitude").value = lng.toFixed(6);
        if ($("geo-rayon")) $("geo-rayon").value = rayon;

        initMap(lat, lng);
    } catch {
        initMap(DEFAULT_LAT, DEFAULT_LNG);
    }
}

const BTN_GEO_LABEL = `
    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24"
         fill="none" stroke="currentColor" stroke-width="2">
        <circle cx="12" cy="12" r="3"/>
        <path d="M12 2v3M12 19v3M2 12h3M19 12h3"/>
        <circle cx="12" cy="12" r="9" stroke-dasharray="4 2"/>
    </svg>
    Détecter ma position automatiquement
`;

function resetBtn(btn) {
    btn.disabled = false;
    btn.innerHTML = BTN_GEO_LABEL;
}

document.addEventListener("DOMContentLoaded", () => {
    loadCurrentSchool();

    // Mise à jour du cercle quand le rayon change
    $("geo-rayon")?.addEventListener("input", () => {
        if (!circle || !marker) return;
        const radius = getRadiusValue();
        circle.setRadius(radius);
    });

    // Mise à jour du marqueur quand lat/lng changent manuellement
    function onCoordInput() {
        const lat = parseFloat($("geo-latitude")?.value);
        const lng = parseFloat($("geo-longitude")?.value);
        if (!isNaN(lat) && !isNaN(lng) && map) {
            updateMarkerAndCircle(lat, lng);
        }
    }
    $("geo-latitude")?.addEventListener("change", onCoordInput);
    $("geo-longitude")?.addEventListener("change", onCoordInput);

    // Géolocalisation automatique
    $("btn-auto-geo")?.addEventListener("click", () => {
        const btn = $("btn-auto-geo");

        // Les navigateurs modernes bloquent la géolocalisation sur HTTP (sauf localhost)
        if (!window.isSecureContext) {
            showGeoFeedback(
                "La géolocalisation nécessite une connexion sécurisée (HTTPS). Saisissez les coordonnées manuellement.",
                "error"
            );
            return;
        }

        if (!navigator.geolocation) {
            showGeoFeedback("La géolocalisation n'est pas supportée par ce navigateur.", "error");
            return;
        }

        btn.disabled = true;
        btn.innerHTML = `
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24"
                 fill="none" stroke="currentColor" stroke-width="2" class="animate-spin">
                <path d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83"/>
            </svg>
            Détection en cours…
        `;

        // Timeout de sécurité côté client — au cas où le navigateur ne déclenche jamais le callback
        let settled = false;
        const clientTimeout = setTimeout(() => {
            if (settled) return;
            settled = true;
            resetBtn(btn);
            showGeoFeedback("Temps dépassé. Autorisez la localisation dans votre navigateur puis réessayez.", "error");
        }, 12000);

        navigator.geolocation.getCurrentPosition(
            (pos) => {
                if (settled) return;
                settled = true;
                clearTimeout(clientTimeout);

                const lat = pos.coords.latitude;
                const lng = pos.coords.longitude;
                syncInputs(lat, lng);
                if (map) {
                    updateMarkerAndCircle(lat, lng);
                    map.setView([lat, lng], DEFAULT_ZOOM);
                } else {
                    initMap(lat, lng);
                }
                showGeoFeedback("Position détectée. Cliquez sur Enregistrer pour sauvegarder.", "success");
                resetBtn(btn);
            },
            (err) => {
                if (settled) return;
                settled = true;
                clearTimeout(clientTimeout);

                const messages = {
                    1: "Permission refusée. Autorisez l'accès à la localisation dans votre navigateur puis réessayez.",
                    2: "Position indisponible sur cet appareil.",
                    3: "La détection a pris trop de temps. Réessayez ou saisissez les coordonnées manuellement.",
                };
                showGeoFeedback(messages[err.code] || "Erreur de géolocalisation.", "error");
                resetBtn(btn);
            },
            { enableHighAccuracy: false, timeout: 8000, maximumAge: 0 }
        );
    });

    // Enregistrement
    $("btn-save-geo")?.addEventListener("click", async () => {
        const lat = parseFloat($("geo-latitude")?.value);
        const lng = parseFloat($("geo-longitude")?.value);
        const rayon = parseFloat($("geo-rayon")?.value);

        if (isNaN(lat) || isNaN(lng)) {
            showGeoFeedback("Veuillez définir une position valide (latitude et longitude).", "error");
            return;
        }
        if (isNaN(rayon) || rayon < 10) {
            showGeoFeedback("Le rayon doit être d'au moins 10 mètres.", "error");
            return;
        }
        if (!currentSchoolId) {
            showGeoFeedback("Aucune école trouvée. Créez d'abord une école.", "error");
            return;
        }

        try {
            const school = await api.get(`/api/schools/${currentSchoolId}`);
            await api.put(`/api/schools/${currentSchoolId}`, {
                name: school.name,
                latitude: lat,
                longitude: lng,
                rayon,
            });
            showGeoFeedback("Géolocalisation enregistrée avec succès.", "success");
        } catch (err) {
            showGeoFeedback(err.message || "Impossible d'enregistrer la géolocalisation.", "error");
        }
    });
});

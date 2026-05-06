/**
 * sidebar-loader.js
 *
 * Utilitaire de chargement dynamique de la sidebar.
 * Appelle GET /api/sidebar avec le token JWT, récupère le HTML
 * pré-formatté côté serveur, et l'injecte dans #sidebar-placeholder.
 *
 * Usage :
 *   <script src="/js/common/sidebar-loader.js"></script>
 *   <div id="sidebar-placeholder"></div>
 *   <script>
 *     loadSidebar('subjects', 3);   // activePage, subjectsFiliereId (optionnel)
 *   </script>
 */

/**
 * Charge et injecte la sidebar dans #sidebar-placeholder.
 *
 * @param {string} activePage        - Identifiant de la page active (ex: 'subjects', 'users').
 * @param {number|null} filiereId    - ID de la filière pour les liens AP (optionnel).
 */
async function loadSidebar(activePage = '') {
    const placeholder = document.getElementById('sidebar-placeholder');
    console.log("activePage", activePage);
    if (!placeholder) {
        console.warn('[sidebar-loader] #sidebar-placeholder introuvable dans le DOM.');
        return;
    }

    const token = localStorage.getItem('token');
    if (!token) {
        // Pas de token → redirection login
        window.location.href = '/auth/login';
        return;
    }

    // Construire l'URL avec les paramètres
    const params = new URLSearchParams();
    if (activePage) params.set('activePage', activePage);

    try {
        const response = await fetch(`/api/sidebar?${params.toString()}`, {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token,
                'Accept': 'text/html'
            }
        });
        console.log(response);

        if (response.status === 401 || response.status === 403) {
            // Token expiré ou invalide → tenter un refresh avant de rediriger
            const refreshed = await _trySidebarTokenRefresh();
            if (refreshed) {
                return loadSidebar(activePage); // Retry avec nouveau token
            } else {
                window.location.href = 'auth/login';
                return;
            }
        }

        if (!response.ok) {
            console.error('[sidebar-loader] Erreur lors du chargement de la sidebar :', response.status);
            return;
        }

        const html = await response.text();
        placeholder.innerHTML = html;

        // Réinitialiser les composants Preline après injection du HTML
        if (window.HSOverlay) {
            window.HSOverlay.autoInit();
        }
        if (window.HSStaticMethods) {
            window.HSStaticMethods.autoInit();
        }

    } catch (error) {
        console.error('[sidebar-loader] Erreur réseau :', error);
    }
}

/**
 * Tente de rafraîchir le token JWT via /refresh.
 * Retourne true si le refresh a réussi, false sinon.
 * @private
 */
async function _trySidebarTokenRefresh() {
    const refreshToken = localStorage.getItem('refresh_token');
    if (!refreshToken) return false;

    try {
        const res = await fetch('/refresh', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ refreshToken })
        });

        if (!res.ok) return false;

        const data = await res.json();
        if (data.token) {
            localStorage.setItem('token', data.token);
        }
        if (data.refreshToken) {
            localStorage.setItem('refresh_token', data.refreshToken);
        }
        return true;
    } catch {
        return false;
    }
}

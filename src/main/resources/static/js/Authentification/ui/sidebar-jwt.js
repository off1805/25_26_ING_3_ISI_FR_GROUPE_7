/**
 * Gestion de la sidebar avec le système JWT
 */
document.addEventListener("DOMContentLoaded", () => {
    const userInfo = getUserInfoFromToken();

    if (userInfo) {
        updateSidebarWithUserInfo(userInfo);
        filterMenuByRole(userInfo.role);
    } else {
        console.warn('Aucun token JWT trouvé');
        showDefaultSidebar();
    }
});

/**
 * Récupère les informations utilisateur depuis le token JWT
 */
function getUserInfoFromToken() {
    const token = getTokenFromStorage();
    if (!token) return null;

    const decoded = decodeJWT(token);
    if (!decoded) return null;

    return {
        role: normalizeRole(decoded.role || decoded.roles),
        displayName: buildDisplayName(decoded)
    };
}

/**
 * Récupère le token depuis les cookies ou localStorage
 */
function getTokenFromStorage() {
    const cookies = document.cookie.split(';');
    for (let cookie of cookies) {
        const [name, value] = cookie.trim().split('=');
        if (name === 'authToken' || name === 'token') {
            return value;
        }
    }
    return localStorage.getItem('authToken') || localStorage.getItem('token');
}

/**
 * Décode un token JWT
 */
function decodeJWT(token) {
    try {
        const payload = token.split('.')[1];
        return JSON.parse(atob(payload));
    } catch (error) {
        console.error('Erreur décodage token:', error);
        return null;
    }
}

/**
 * Construit le nom d'affichage
 */
function buildDisplayName(decoded) {
    const prenoms = decoded.prenoms || decoded.firstName;
    const nom = decoded.nom || decoded.lastName;
    const email = decoded.email;

    if (prenoms && nom) return `${prenoms} ${nom}`;
    if (nom) return nom;
    if (prenoms) return prenoms;
    if (email) return email.split('@')[0];

    return 'Utilisateur';
}

/**
 * Normalise le rôle
 */
function normalizeRole(role) {
    if (!role) return null;
    if (Array.isArray(role)) role = role[0];

    const roleMap = {
        'ROLE_ADMIN': 'ADMIN',
        'ADMIN': 'ADMIN',
        'ROLE_TEACHER': 'TEACHER',
        'TEACHER': 'TEACHER',
        'ROLE_AP': 'AP',
        'AP': 'AP'
    };

    return roleMap[role.toUpperCase()] || role.toUpperCase();
}

/**
 * Met à jour la sidebar avec les informations utilisateur
 */
function updateSidebarWithUserInfo(userInfo) {
    console.log('JWT User Info:', userInfo);

    // Debug
    const debugRole = document.getElementById("sidebar-debug-role");
    const debugName = document.getElementById("sidebar-debug-name");
    if (debugRole) debugRole.textContent = "currentRole = " + (userInfo.role || "NULL");
    if (debugName) debugName.textContent = "displayName = " + (userInfo.displayName || "NULL");

    // Labels
    const roleLabelTop = document.getElementById("sidebar-role-label");
    if (roleLabelTop) roleLabelTop.textContent = userInfo.role || "UTILISATEUR";

    const roleLabelBottom = document.getElementById("sidebar-role-text");
    if (roleLabelBottom) roleLabelBottom.textContent = userInfo.role || "UTILISATEUR";

    const nameTarget = document.getElementById("sidebar-display-name");
    if (nameTarget) nameTarget.textContent = userInfo.displayName || "Utilisateur";
}

/**
 * Filtre les menus selon le rôle
 */
function filterMenuByRole(userRole) {
    console.log('Filtering menu for role:', userRole);

    document.querySelectorAll("[data-role]").forEach((el) => {
        const allowedRole = (el.getAttribute("data-role") || "").trim();
        el.style.display = (userRole && allowedRole === userRole) ? "" : "none";
    });
}

/**
 * Affiche la sidebar par défaut
 */
function showDefaultSidebar() {
    document.querySelectorAll("[data-role]").forEach((el) => {
        el.style.display = "none";
    });

    const roleLabelTop = document.getElementById("sidebar-role-label");
    if (roleLabelTop) roleLabelTop.textContent = "NON CONNECTÉ";

    const roleLabelBottom = document.getElementById("sidebar-role-text");
    if (roleLabelBottom) roleLabelBottom.textContent = "NON CONNECTÉ";

    const nameTarget = document.getElementById("sidebar-display-name");
    if (nameTarget) nameTarget.textContent = "Utilisateur";
}

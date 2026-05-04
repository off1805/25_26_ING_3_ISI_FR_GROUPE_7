document.addEventListener("DOMContentLoaded", () => {
    const userInfo = getUserInfoFromToken();

    if (userInfo) {
        updateSidebarWithUserInfo(userInfo);
        filterMenuByRole(userInfo.role);
    } else {
        console.warn('Aucun token JWT trouvé');
        showDefaultSidebar();
    }

    const logoutBtn = document.getElementById('sidebar-logout-btn');
    if (logoutBtn) logoutBtn.addEventListener('click', performLogout);
});

function getUserInfoFromToken() {
    const token = localStorage.getItem('token');
    if (!token) return null;

    const decoded = decodeJWT(token);
    if (!decoded) return null;

    return {
        role: normalizeRole(decoded.role || decoded.roles),
        displayName: buildDisplayName(decoded)
    };
}

function decodeJWT(token) {
    try {
        return JSON.parse(atob(token.split('.')[1]));
    } catch (error) {
        console.error('Erreur décodage token:', error);
        return null;
    }
}

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

function updateSidebarWithUserInfo(userInfo) {
    const debugRole = document.getElementById("sidebar-debug-role");
    const debugName = document.getElementById("sidebar-debug-name");
    if (debugRole) debugRole.textContent = "currentRole = " + (userInfo.role || "NULL");
    if (debugName) debugName.textContent = "displayName = " + (userInfo.displayName || "NULL");

    const roleLabelTop = document.getElementById("sidebar-role-label");
    if (roleLabelTop) roleLabelTop.textContent = userInfo.role || "UTILISATEUR";

    const roleLabelBottom = document.getElementById("sidebar-role-text");
    if (roleLabelBottom) roleLabelBottom.textContent = userInfo.role || "UTILISATEUR";

    const nameTarget = document.getElementById("sidebar-display-name");
    if (nameTarget) nameTarget.textContent = userInfo.displayName || "Utilisateur";
}

function filterMenuByRole(userRole) {
    document.querySelectorAll("[data-role]").forEach((el) => {
        const allowedRole = (el.getAttribute("data-role") || "").trim();
        el.style.display = (userRole && allowedRole === userRole) ? "" : "none";
    });
}

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

/**
 * Déconnecte l'utilisateur : appelle POST /logout, vide le localStorage, redirige vers auth/login
 */
function performLogout() {
    const refreshToken = getRefreshTokenFromStorage();

    const finalize = () => {
        // Vider uniquement le localStorage
        localStorage.clear();
        window.location.href = '/auth/login';
    };

    if (refreshToken) {
        fetch('/logout', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ refreshToken })
        }).catch(() => {}).finally(finalize);
    } else {
        finalize();
    }
}

function getRefreshTokenFromStorage() {
    const cookies = document.cookie.split(';');
    for (const cookie of cookies) {
        const [name, value] = cookie.trim().split('=');
        if (name === 'refreshToken') return decodeURIComponent(value || '');
    }
    return localStorage.getItem('refreshToken');
}

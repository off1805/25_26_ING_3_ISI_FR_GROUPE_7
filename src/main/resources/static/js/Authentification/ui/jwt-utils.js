/**
 * Utilitaires pour gérer les tokens JWT
 */
class JWTUtils {
    /**
     * Récupère le token JWT depuis les cookies ou localStorage
     */
    static getToken() {
        // Essayer d'abord les cookies
        const cookies = document.cookie.split(';');
        for (let cookie of cookies) {
            const [name, value] = cookie.trim().split('=');
            if (name === 'authToken' || name === 'token') {
                return value;
            }
        }

        // Essayer localStorage
        return localStorage.getItem('authToken') || localStorage.getItem('token');
    }

    /**
     * Décode un token JWT sans vérification de signature
     */
    static decodeToken(token) {
        try {
            const payload = token.split('.')[1];
            const decoded = JSON.parse(atob(payload));
            return decoded;
        } catch (error) {
            console.error('Erreur lors du décodage du token:', error);
            return null;
        }
    }

    /**
     * Récupère les informations utilisateur depuis le token
     */
    static getUserFromToken() {
        const token = this.getToken();
        if (!token) {
            return null;
        }

        const decoded = this.decodeToken(token);
        if (!decoded) {
            return null;
        }

        return {
            id: decoded.id || decoded.userId || decoded.sub,
            email: decoded.email || decoded.username,
            nom: decoded.nom || decoded.lastName || decoded.family_name,
            prenoms: decoded.prenoms || decoded.firstName || decoded.given_name,
            role: decoded.role || decoded.roles || decoded.authorities,
            displayName: this.buildDisplayName(decoded)
        };
    }

    /**
     * Construit le nom d'affichage depuis les données du token
     */
    static buildDisplayName(decoded) {
        const prenoms = decoded.prenoms || decoded.firstName || decoded.given_name;
        const nom = decoded.nom || decoded.lastName || decoded.family_name;
        const email = decoded.email || decoded.username;

        if (prenoms && nom) {
            return `${prenoms} ${nom}`;
        } else if (nom) {
            return nom;
        } else if (prenoms) {
            return prenoms;
        } else if (email) {
            return email.split('@')[0]; // Prendre la partie avant @
        }

        return 'Utilisateur';
    }

    /**
     * Normalise le rôle pour correspondre aux data-role des menus
     */
    static normalizeRole(role) {
        if (!role) return null;

        // Si c'est un tableau, prendre le premier rôle
        if (Array.isArray(role)) {
            role = role[0];
        }

        // Normaliser les différents formats possibles
        const roleMap = {
            'ROLE_ADMIN': 'ADMIN',
            'ADMIN': 'ADMIN',
            'ROLE_TEACHER': 'TEACHER',
            'TEACHER': 'TEACHER',
            'ENSEIGNANT': 'TEACHER',
            'ROLE_AP': 'AP',
            'AP': 'AP',
            'ASSISTANT': 'AP',
            'ROLE_SURVEILLANT': 'SURVEILLANT',
            'SURVEILLANT': 'SURVEILLANT'
        };

        return roleMap[role.toUpperCase()] || role.toUpperCase();
    }

    /**
     * Vérifie si le token est expiré
     */
    static isTokenExpired(token) {
        const decoded = this.decodeToken(token);
        if (!decoded || !decoded.exp) {
            return true;
        }

        const currentTime = Math.floor(Date.now() / 1000);
        return decoded.exp < currentTime;
    }
}

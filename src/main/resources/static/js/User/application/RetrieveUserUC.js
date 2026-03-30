/** Rôles affichés sur la page admin « Personnel » (aligné sur AdminController). */
export const STAFF_ROLES = ['AP', 'TEACHER', 'SURVEILLANT'];

/**
 * Construit la query string pour GET /api/users (Spring @ModelAttribute + Pageable).
 * @param {object} options
 * @param {string} [options.email]
 * @param {string} [options.roleFilter] — 'ALL' | 'AP' | 'TEACHER' | 'SURVEILLANT'
 * @param {string} [options.statusFilter] — 'ALL' | 'ACTIVE' | 'BLOCKED'
 * @param {number} [options.page]
 * @param {number} [options.size]
 */
export function buildStaffUserSearchParams({
    email = '',
    roleFilter = 'ALL',
    statusFilter = 'ALL',
    page = 0,
    size = 50
} = {}) {
    const params = new URLSearchParams();
    const trimmed = typeof email === 'string' ? email.trim() : '';
    if (trimmed) {
        params.set('email', trimmed);
    }

    if (roleFilter === 'ALL' || !roleFilter) {
        STAFF_ROLES.forEach((r) => params.append('role', r));
    } else {
        params.append('role', roleFilter);
    }

    if (statusFilter && statusFilter !== 'ALL') {
        params.set('status', statusFilter);
    }

    params.set('page', String(page));
    params.set('size', String(size));
    params.set('sort', 'id,desc');
    return params.toString();
}

export class RetrieveUserUC {
    constructor(userApi) {
        this.userApi = userApi;
    }

    /**
     * @param {object|string} filtre — objet pour la recherche staff, ou chaîne déjà préfixée (?a=b)
     */
    async execute(filtre) {
        const query =
            typeof filtre === 'string'
                ? filtre.replace(/^\?/, '')
                : buildStaffUserSearchParams(filtre);
        const filtreString = query ? `?${query}` : '';
        try {
            return await this.userApi.retrieveUsers(filtreString);
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la récupération des utilisateurs.");
        }
    }
}

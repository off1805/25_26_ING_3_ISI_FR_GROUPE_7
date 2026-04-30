export const STAFF_ROLES = ['AP', 'TEACHER', 'SURVEILLANT'];

export function buildStaffUserSearchParams({
    email        = '',
    roleFilter   = 'ALL',
    statusFilter = 'ALL',
    page         = 0,
    size         = 50
} = {}) {
    const params = new URLSearchParams();
    const trimmed = typeof email === 'string' ? email.trim() : '';
    if (trimmed) params.set('email', trimmed);

    if (roleFilter === 'ALL' || !roleFilter) {
        STAFF_ROLES.forEach(r => params.append('role', r));
    } else {
        params.append('role', roleFilter);
    }

    if (statusFilter && statusFilter !== 'ALL') params.set('status', statusFilter);
    params.set('page', String(page));
    params.set('size', String(size));
    params.set('sort', 'id,desc');
    return params.toString();
}

export class RetrieveUserUC {
    constructor(userApi) {
        this.userApi = userApi;
    }

    async execute(filtre) {
        const query = typeof filtre === 'string'
            ? filtre.replace(/^\?/, '')
            : buildStaffUserSearchParams(filtre);
        return this.userApi.retrieveUsers(query ? `?${query}` : '');
    }
}

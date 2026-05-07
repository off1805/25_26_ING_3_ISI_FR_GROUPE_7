
export class TokenService{

    static async getToken(){
        return localStorage.getItem('token');
    }

    static async getRefreshToken(){
        return localStorage.getItem('refresh_token');
    }

    static async setRefreshToken(rToken){
        localStorage.setItem('refresh_token', rToken);
    }

    static async setToken(token){
        localStorage.setItem('token', token);
    }

    static parseToken(token){
        return JSON.parse(atob(token.split('.')[1]));
    }

    /**
     * Décode le payload JWT et stocke chaque claim dans le localStorage.
     * Claims attendus : sub (userId), email, role, permissions, iat, exp.
     * @param {string} token - Le token JWT brut
     * @returns {object} Les claims décodés
     */
    static decodeAndStoreClaims(token) {
        const claims = this.parseToken(token);

        localStorage.setItem('userId',      String(claims.sub ?? ''));
        localStorage.setItem('email',       claims.email       ?? '');
        localStorage.setItem('role',        claims.role        ?? '');
        localStorage.setItem('permissions', JSON.stringify(claims.permissions ?? []));
        localStorage.setItem('token_iat',   String(claims.iat  ?? ''));
        localStorage.setItem('token_exp',   String(claims.exp  ?? ''));

        return claims;
    }

    static getUserClaims() {
        return {
            userId:      localStorage.getItem('userId'),
            email:       localStorage.getItem('email'),
            role:        localStorage.getItem('role'),
            permissions: JSON.parse(localStorage.getItem('permissions') ?? '[]'),
            iat:         localStorage.getItem('token_iat'),
            exp:         localStorage.getItem('token_exp'),
        };
    }

    static clearClaims() {
        ['userId', 'email', 'role', 'permissions', 'token_iat', 'token_exp',
         'token', 'refresh_token', 'displayName']
            .forEach(key => localStorage.removeItem(key));
    }

}
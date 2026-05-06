
class UserService {
    constructor(baseUrl) {
        this.baseUrl = baseUrl;
    }

    async _getApi() {
        const { default: api } = await import('./common/ClientHttp.js');
        return api;
    }

    async createUser(email, password,idPermissions,idRole) {
        const api = await this._getApi();
        return await api.post(`${this.baseUrl}/createUser`, { email, password,idPermissions,idRole });
    }

    async updateUser(id, updatedFields) {
        const api = await this._getApi();
        return await api.post(`${this.baseUrl}/updateUser`, { id, ...updatedFields });
    }

    async deleteUser(id) {
        const api = await this._getApi();
        return await api.post(`${this.baseUrl}/`, { id });
    }
}
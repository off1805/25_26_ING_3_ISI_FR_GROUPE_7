
import api from '../../common/ClientHttp.js';

export class UserApi {

    async createUser(userData) {
        return api.post("/api/users", userData);
    }

    async deleteUser(id) {
        return api.delete(`/api/users/${id}`);
    }

    async updateUserStatus(id, status) {
        return api.put(`/api/users/${id}/status`, { status: status });
    }

    async getPermissionsByRole(roleName) {
        return api.get(`/api/roles/${roleName}/permissions`);
    }
}

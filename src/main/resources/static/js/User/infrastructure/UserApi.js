
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

    async updateUser(id, userData) {
        // Le backend expose uniquement la modification de statut via PUT /api/users/{id}/status
        // On met à jour le statut si fourni, puis on retourne les données fusionnées
        if (userData.status) {
            await api.put(`/api/users/${id}/status`, { status: userData.status });
        }
        // Récupère l'utilisateur mis à jour
        return api.get(`/api/users/${id}`);
    }

    async getPermissionsByRole(roleName) {
        return api.get(`/api/roles/${roleName}/permissions`);
    }

    async retrieveUsers(filtre) {
        return api.get('/api/users' + filtre);
    }

}

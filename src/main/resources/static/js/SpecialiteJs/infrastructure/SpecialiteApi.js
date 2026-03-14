import api from '../../common/ClientHttp.js';

export class SpecialiteApi {

    async createSpecialite(data) {
        return api.post("/api/specialites", data);
    }

    async getAllSpecialites() {
        return api.get("/api/specialites");
    }

    async getSpecialiteById(id) {
        return api.get(`/api/specialites/${id}`);
    }

    async getSpecialitesByBranche(brancheCode, niveau = null) {
        const params = niveau !== null ? { niveau } : {};
        return api.get(`/api/specialites/branche/${brancheCode}`, { params });
    }

    async updateSpecialite(id, data) {
        return api.put(`/api/specialites/${id}`, data);
    }

    async deleteSpecialite(id) {
        return api.delete(`/api/specialites/${id}`);
    }

    async activerSpecialite(id) {
        return api.patch(`/api/specialites/${id}/activer`);
    }

    async desactiverSpecialite(id) {
        return api.patch(`/api/specialites/${id}/desactiver`);
    }
}

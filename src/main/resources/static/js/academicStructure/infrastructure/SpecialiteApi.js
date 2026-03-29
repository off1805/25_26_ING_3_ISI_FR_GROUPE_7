import api from '../../common/ClientHttp.js';

export class SpecialiteApi {
    constructor() {
        this.baseUrl = '/api/specialites';
    }

    async create(request) {
        return api.post(this.baseUrl, request);
    }

    async update(id, data) {
        return api.put(`${this.baseUrl}/${id}`, data);
    }

    async updateStatus(id, status) {
        return api.put(`${this.baseUrl}/${id}/status`, { status });
    }

    async delete(id) {
        return api.delete(`${this.baseUrl}/${id}`);
    }

    async getSpecialiteById(id) {
        return api.get(`${this.baseUrl}/${id}`);
    }

    async getByNiveauId(niveauId) {
        return api.get(`${this.baseUrl}/niveau/${niveauId}`);
    }
}

import api from '../../common/ClientHttp.js';

export class ClasseApi {
    constructor() {
        this.baseUrl = '/api/classes';
    }

    async create(request) {
        return api.post(this.baseUrl, request);
    }

    async update(id, data) {
        return api.put(`${this.baseUrl}/${id}`, data);
    }

    async delete(id) {
        return api.delete(`${this.baseUrl}/${id}`);
    }

    async getClasseById(id) {
        return api.get(`${this.baseUrl}/${id}`);
    }

    async getBySpecialiteId(specialiteId) {
        return api.get(`${this.baseUrl}/specialite/${specialiteId}`);
    }
}

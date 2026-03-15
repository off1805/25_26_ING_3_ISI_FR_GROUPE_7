import api from '../../common/ClientHttp.js';

export class NiveauApi {
    constructor() {
        this.baseUrl = '/api/niveau';
    }

    async create(request) {
        return api.post(this.baseUrl, request);
    }

    async delete(id) {
        return api.delete(`${this.baseUrl}/${id}`);
    }

    async getByFiliereId(filiereId) {
        return api.get(`${this.baseUrl}/filiere/${filiereId}`);
    }
}

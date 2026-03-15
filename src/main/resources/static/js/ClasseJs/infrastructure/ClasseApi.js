import api from '../../common/ClientHttp.js';

export class ClasseApi {
    constructor() {
        this.baseUrl = '/api/classes';
    }

    async create(request) {
        return api.post(this.baseUrl, request);
    }

    async delete(id) {
        return api.delete(`${this.baseUrl}/${id}`);
    }
}

import api from '../../common/ClientHttp.js';

export class SpecialiteApi {
    constructor() {
        this.baseUrl = '/api/specialites';
    }

    async create(request) {
        return api.post(this.baseUrl, request);
    }

    async delete(id) {
        return api.delete(`${this.baseUrl}/${id}`);
    }
}

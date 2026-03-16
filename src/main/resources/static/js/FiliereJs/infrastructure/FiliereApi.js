import api from '../../common/ClientHttp.js';

export class FiliereApi {
    constructor() {
        this.baseUrl = "/api/filiere";
    }

    async getAllActiveFilieres() {
        return api.get(`${this.baseUrl}/active`);
    }

    async createFiliere(filiereData) {
        return api.post(this.baseUrl, filiereData);
    }

    async deleteFiliere(id) {
        return api.delete(`${this.baseUrl}/${id}`);
    }

    async getFiliereById(id) {
        return api.get(`${this.baseUrl}/${id}`);
    }
}

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

    async getFiliereFromCycle(id){
        return api.get('/api/filiere?cycleId='+id);
    }

    async updateFiliere(id, filiereData) {
        return api.put(`${this.baseUrl}/${id}`, filiereData);
    }

    async updateFiliereStatus(id, status) {
        return api.put(`${this.baseUrl}/${id}/status`, { status });
    }

    async deleteFiliere(id) {
        return api.delete(`${this.baseUrl}/${id}`);
    }

    async getFiliereById(id) {
        return api.get(`${this.baseUrl}/${id}`);
    }
}

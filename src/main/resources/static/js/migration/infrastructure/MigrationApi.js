import api from '../../common/ClientHttp.js';

export class MigrationApi {
    constructor() {
        this.baseUrl = '/api/migrations';
    }

    async getPending(filiereId) {
        return api.get(`${this.baseUrl}/pending?filiereId=${filiereId}`);
    }

    async create(userId, classeDestinationId) {
        return api.post(this.baseUrl, { userId, classeDestinationId });
    }

    async update(id, classeDestinationId) {
        return api.put(`${this.baseUrl}/${id}`, { classeDestinationId });
    }

    async cancel(id) {
        return api.delete(`${this.baseUrl}/${id}`);
    }

    async getEligibleClasses(classeSourceId) {
        return api.get(`${this.baseUrl}/eligible-classes?classeSourceId=${classeSourceId}`);
    }

    async checkGraduationEligibility(classeId) {
        return api.get(`${this.baseUrl}/classes/${classeId}/graduation-eligible`);
    }

    async archiveStudent(userId) {
        return api.post(`${this.baseUrl}/students/${userId}/archive`);
    }

    async expelStudent(userId, motif, justificatifFile) {
        const formData = new FormData();
        formData.append('motif', motif);
        formData.append('justificatif', justificatifFile);
        return api.post(`${this.baseUrl}/students/${userId}/expel`, formData);
    }
}

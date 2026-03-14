import api from '../../common/ClientHttp.js';

export class SeanceApi {

    async createSeance(seanceData) {
        return api.post("/api/seances", seanceData);
    }

    async deleteSeance(id) {
        return api.delete(`/api/seances/${id}`);
    }

    async updateSeance(id, seanceData) {
        return api.put(`/api/seances/${id}`, seanceData);
    }

    async getSeanceById(id) {
        return api.get(`/api/seances/${id}`);
    }

    async retrieveSeances(filtre) {
        let param = new URLSearchParams(filtre).toString();
        let filtreString = filtre ? '?' + param : '';
        return api.get('/api/seances' + filtreString);
    }

    async getSeancesByDate(date) {
        return api.get(`/api/seances/date/${date}`);
    }

    async getSeancesByEnseignant(enseignantId) {
        return api.get(`/api/seances/enseignant/${enseignantId}`);
    }

    async getSeancesByCours(coursId) {
        return api.get(`/api/seances/cours/${coursId}`);
    }

    async getActiveSeances() {
        return api.get('/api/seances/active');
    }

    async getDeletedSeances() {
        return api.get('/api/seances/deleted');
    }
}
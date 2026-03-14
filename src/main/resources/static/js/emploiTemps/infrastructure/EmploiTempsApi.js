import api from '../../common/ClientHttp.js';

export class EmploiTempsApi {

    async createEmploiTemps(emploiData) {
        return api.post("/api/emplois-temps", emploiData);
    }

    async deleteEmploiTemps(id) {
        return api.delete(`/api/emplois-temps/${id}`);
    }

    async updateEmploiTemps(id, emploiData) {
        return api.put(`/api/emplois-temps/${id}`, emploiData);
    }

    async getEmploiTempsById(id) {
        return api.get(`/api/emplois-temps/${id}`);
    }

    async retrieveEmploisTemps(filtre) {
        let param = new URLSearchParams(filtre).toString();
        let filtreString = filtre ? '?' + param : '';
        return api.get('/api/emplois-temps' + filtreString);
    }

    async addSeanceToEmploi(emploiTempsId, seanceId) {
        return api.post('/api/emplois-temps/seances', {
            emploiTempsId: emploiTempsId,
            seanceId: seanceId
        });
    }

    async removeSeanceFromEmploi(emploiTempsId, seanceId) {
        return api.delete('/api/emplois-temps/seances', {
            data: {
                emploiTempsId: emploiTempsId,
                seanceId: seanceId
            }
        });
    }

    async getEmploisByFiliere(filiereId, includeDeleted = false) {
        return api.get(`/api/emplois-temps/filiere/${filiereId}?includeDeleted=${includeDeleted}`);
    }

    async getEmploisByNiveau(niveauId, includeDeleted = false) {
        return api.get(`/api/emplois-temps/niveau/${niveauId}?includeDeleted=${includeDeleted}`);
    }

    async getEmploisBySemaine(semaine, includeDeleted = false) {
        return api.get(`/api/emplois-temps/semaine/${semaine}?includeDeleted=${includeDeleted}`);
    }

    async getActiveEmplois() {
        return api.get('/api/emplois-temps/active');
    }

    async getDeletedEmplois() {
        return api.get('/api/emplois-temps/deleted');
    }
}
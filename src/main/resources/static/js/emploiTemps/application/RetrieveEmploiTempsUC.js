export class RetrieveEmploiTempsUC {
    constructor(emploiTempsApi) {
        this.emploiTempsApi = emploiTempsApi;
    }

    async execute(filtre) {
        let param = new URLSearchParams(filtre).toString();
        let filtreString = filtre ? '?' + param : '';

        try {
            const response = await this.emploiTempsApi.retrieveEmploisTemps(filtreString);
            return response;
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la récupération des emplois du temps.");
        }
    }

    async getById(id) {
        try {
            return await this.emploiTempsApi.getEmploiTempsById(id);
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la récupération de l'emploi du temps.");
        }
    }

    async getByFiliere(filiereId, includeDeleted = false) {
        try {
            return await this.emploiTempsApi.getEmploisByFiliere(filiereId, includeDeleted);
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la récupération des emplois par filière.");
        }
    }

    async getByNiveau(niveauId, includeDeleted = false) {
        try {
            return await this.emploiTempsApi.getEmploisByNiveau(niveauId, includeDeleted);
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la récupération des emplois par niveau.");
        }
    }

    async getBySemaine(semaine, includeDeleted = false) {
        try {
            return await this.emploiTempsApi.getEmploisBySemaine(semaine, includeDeleted);
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la récupération des emplois par semaine.");
        }
    }

    async getActive() {
        try {
            return await this.emploiTempsApi.getActiveEmplois();
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la récupération des emplois actifs.");
        }
    }

    async getDeleted() {
        try {
            return await this.emploiTempsApi.getDeletedEmplois();
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la récupération des emplois supprimés.");
        }
    }
}
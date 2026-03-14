export class RetrieveSeanceUC {
    constructor(seanceApi) {
        this.seanceApi = seanceApi;
    }

    async execute(filtre) {
        let param = new URLSearchParams(filtre).toString();
        let filtreString = filtre ? '?' + param : '';

        try {
            const response = await this.seanceApi.retrieveSeances(filtreString);
            return response;
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la récupération des séances.");
        }
    }

    async getById(id) {
        try {
            return await this.seanceApi.getSeanceById(id);
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la récupération de la séance.");
        }
    }

    async getByDate(date) {
        try {
            return await this.seanceApi.getSeancesByDate(date);
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la récupération des séances par date.");
        }
    }

    async getByEnseignant(enseignantId) {
        try {
            return await this.seanceApi.getSeancesByEnseignant(enseignantId);
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la récupération des séances par enseignant.");
        }
    }
}
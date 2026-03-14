export class RemoveSeanceFromEmploiUC {
    constructor(emploiTempsApi) {
        this.emploiTempsApi = emploiTempsApi;
    }

    async execute(emploiTempsId, seanceId) {
        if (!emploiTempsId || !seanceId) {
            throw new Error("Les IDs de l'emploi du temps et de la séance sont requis");
        }

        try {
            const response = await this.emploiTempsApi.removeSeanceFromEmploi(emploiTempsId, seanceId);
            return response;
        } catch (e) {
            throw new Error(e.message || "Erreur lors du retrait de la séance de l'emploi du temps.");
        }
    }
}
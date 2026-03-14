export class AddSeanceToEmploiUC {
    constructor(emploiTempsApi) {
        this.emploiTempsApi = emploiTempsApi;
    }

    async execute(emploiTempsId, seanceId) {
        if (!emploiTempsId || !seanceId) {
            throw new Error("Les IDs de l'emploi du temps et de la séance sont requis");
        }

        try {
            const response = await this.emploiTempsApi.addSeanceToEmploi(emploiTempsId, seanceId);
            return response;
        } catch (e) {
            throw new Error(e.message || "Erreur lors de l'ajout de la séance à l'emploi du temps.");
        }
    }
}
export class DeleteEmploiTempsUC {
    constructor(emploiTempsApi) {
        this.emploiTempsApi = emploiTempsApi;
    }

    async execute(emploiId) {
        if (!emploiId) {
            throw new Error("ID de l'emploi du temps requis pour la suppression");
        }
        try {
            await this.emploiTempsApi.deleteEmploiTemps(emploiId);
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la suppression de l'emploi du temps.");
        }
    }
}
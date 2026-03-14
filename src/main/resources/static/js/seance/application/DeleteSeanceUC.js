export class DeleteSeanceUC {
    constructor(seanceApi) {
        this.seanceApi = seanceApi;
    }

    async execute(seanceId) {
        if (!seanceId) {
            throw new Error("ID de la séance requis pour la suppression");
        }
        try {
            await this.seanceApi.deleteSeance(seanceId);
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la suppression de la séance.");
        }
    }
}
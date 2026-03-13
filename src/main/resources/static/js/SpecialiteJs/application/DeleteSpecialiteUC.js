export class DeleteSpecialiteUC {
    constructor(specialiteApi) {
        this.specialiteApi = specialiteApi;
    }

    async execute(id) {
        if (!id) {
            throw new Error("L'ID de la spécialité est requis pour la suppression.");
        }
        try {
            return await this.specialiteApi.deleteSpecialite(id);
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la suppression de la spécialité.");
        }
    }
}

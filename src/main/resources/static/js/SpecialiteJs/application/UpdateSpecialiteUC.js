export class UpdateSpecialiteUC {
    constructor(specialiteApi) {
        this.specialiteApi = specialiteApi;
    }

    async execute(id, data) {
        if (!id) {
            throw new Error("L'ID de la spécialité est requis pour la mise à jour.");
        }
        if (!data || !data.code || !data.libelle) {
            throw new Error("Le code et le libellé de la spécialité sont requis.");
        }

        const payload = {
            code: data.code,
            libelle: data.libelle,
            description: data.description || '',
            brancheCode: data.brancheCode,
            niveauMinimum: data.niveauMinimum,
        };

        try {
            return await this.specialiteApi.updateSpecialite(id, payload);
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la mise à jour de la spécialité.");
        }
    }
}

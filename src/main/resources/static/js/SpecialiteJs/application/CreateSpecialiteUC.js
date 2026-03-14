export class CreateSpecialiteUC {
    constructor(specialiteApi) {
        this.specialiteApi = specialiteApi;
    }

    async execute(data) {
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
            return await this.specialiteApi.createSpecialite(payload);
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la création de la spécialité.");
        }
    }
}

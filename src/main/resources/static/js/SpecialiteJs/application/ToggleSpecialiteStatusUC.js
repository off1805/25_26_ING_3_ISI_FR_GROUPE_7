export class ToggleSpecialiteStatusUC {
    constructor(specialiteApi) {
        this.specialiteApi = specialiteApi;
    }

    /**
     * @param {string|number} id
     * @param {boolean} activate - true pour activer, false pour désactiver
     */
    async execute(id, activate) {
        if (!id) {
            throw new Error("L'ID de la spécialité est requis.");
        }
        try {
            if (activate) {
                return await this.specialiteApi.activerSpecialite(id);
            } else {
                return await this.specialiteApi.desactiverSpecialite(id);
            }
        } catch (e) {
            throw new Error(e.message || "Erreur lors du changement de statut de la spécialité.");
        }
    }
}

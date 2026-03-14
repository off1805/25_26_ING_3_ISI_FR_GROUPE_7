export class ModifyCycleStatusUC {
    constructor(cycleApi) {
        this.cycleApi = cycleApi;
    }

    async execute(id, newStatus) {
        if (!id || !newStatus) {
            throw new Error("L'ID du cycle et le nouveau statut sont requis.");
        }
        try {
            return await this.cycleApi.updateCycleStatus(id, newStatus);
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la modification du statut du cycle.");
        }
    }
}

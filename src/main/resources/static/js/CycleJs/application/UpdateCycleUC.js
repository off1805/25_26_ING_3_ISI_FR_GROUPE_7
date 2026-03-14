export class UpdateCycleUC {
    constructor(cycleApi) {
        this.cycleApi = cycleApi;
    }

    async execute(id, cycleData) {
        if (!id) {
            throw new Error("L'ID du cycle est requis pour la mise à jour.");
        }
        if (!cycleData || !cycleData.name || !cycleData.code) {
            throw new Error("Le nom et le code du cycle sont requis.");
        }

        const payload = {
            name: cycleData.name,
            code: cycleData.code,
            durationYears: cycleData.durationYears,
            description: cycleData.description || ''
        };

        try {
            return await this.cycleApi.updateCycle(id, payload);
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la mise à jour du cycle.");
        }
    }
}

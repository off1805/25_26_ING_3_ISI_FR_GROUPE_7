export class CreateCycleUC {
    constructor(cycleApi) {
        this.cycleApi = cycleApi;
    }

    async execute(cycleData) {
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
            return await this.cycleApi.createCycle(payload);
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la création du cycle.");
        }
    }
}

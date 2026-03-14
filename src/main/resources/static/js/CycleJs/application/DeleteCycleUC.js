export class DeleteCycleUC {
    constructor(cycleApi) {
        this.cycleApi = cycleApi;
    }

    async execute(id) {
        if (!id) {
            throw new Error("L'ID du cycle est requis pour la suppression.");
        }
        try {
            return await this.cycleApi.deleteCycle(id);
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la suppression du cycle.");
        }
    }
}

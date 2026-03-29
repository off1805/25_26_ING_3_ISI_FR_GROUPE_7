export class CycleUseCase {
    constructor(cycleApi) {
        this.cycleApi = cycleApi;
    }

    async create(cycleData) {
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

    async delete(id) {
        if (!id) {
            throw new Error("L'ID du cycle est requis pour la suppression.");
        }
        try {
            return await this.cycleApi.deleteCycle(id);
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la suppression du cycle.");
        }
    }

    async modifyStatus(id, newStatus) {
        if (!id || !newStatus) {
            throw new Error("L'ID du cycle et le nouveau statut sont requis.");
        }
        try {
            return await this.cycleApi.updateCycleStatus(id, newStatus);
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la modification du statut du cycle.");
        }
    }

    async update(id, cycleData) {
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
export class FiliereUseCase {
    constructor(filiereApi) {
        this.filiereApi = filiereApi;
    }

    async create(filiereData) {
        if (!filiereData.code || !filiereData.nom || !filiereData.cycleId) {
            throw new Error("Tous les champs obligatoires doivent être remplis");
        }
        return await this.filiereApi.createFiliere(filiereData);
    }

    async delete(id) {
        if (!id) throw new Error("ID de la filière manquant");
        return await this.filiereApi.deleteFiliere(id);
    }

    async findById(id) {
        if (!id) throw new Error("ID de la filière manquant");
        return await this.filiereApi.getFiliereById(id);
    }

    async findByCycleId(cycleId) {
        if (!cycleId) throw new Error("ID du cycle manquant");
        return await this.filiereApi.getFiliereFromCycle(cycleId);
    }
}
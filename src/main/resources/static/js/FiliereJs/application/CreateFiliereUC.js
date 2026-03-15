export class CreateFiliereUC {
    constructor(filiereApi) {
        this.filiereApi = filiereApi;
    }

    async execute(filiereData) {
        if (!filiereData.code || !filiereData.nom || !filiereData.cycleId) {
            throw new Error("Tous les champs obligatoires doivent être remplis");
        }
        return await this.filiereApi.createFiliere(filiereData);
    }
}

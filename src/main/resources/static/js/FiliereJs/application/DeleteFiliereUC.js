export class DeleteFiliereUC {
    constructor(filiereApi) {
        this.filiereApi = filiereApi;
    }

    async execute(id) {
        if (!id) throw new Error("ID de la filière manquant");
        return await this.filiereApi.deleteFiliere(id);
    }
}

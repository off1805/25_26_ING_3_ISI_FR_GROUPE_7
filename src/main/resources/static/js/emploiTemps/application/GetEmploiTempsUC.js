export class GetEmploiTempsUC {
    constructor(emploiTempsApi) {
        this._api = emploiTempsApi;
    }

    async execute(id) {
        if (!id) throw new Error("ID de l'emploi du temps requis");
        return this._api.getEmploiTempsById(id);
    }
}

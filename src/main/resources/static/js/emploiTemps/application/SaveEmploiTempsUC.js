export class SaveEmploiTempsUC {
    constructor(emploiTempsApi) {
        this._api = emploiTempsApi;
    }

    async execute(payload) {
        if (!payload?.classeId || !payload?.dateDebut || !payload?.dateFin) {
            throw new Error("Classe et dates requises pour la sauvegarde");
        }
        return payload.id
            ? this._api.updateWithSeances(payload.id, payload)
            : this._api.createWithSeances(payload);
    }
}

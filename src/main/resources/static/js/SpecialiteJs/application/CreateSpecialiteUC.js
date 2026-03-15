export class CreateSpecialiteUC {
    constructor(api) {
        this.api = api;
    }

    async execute(request) {
        if (!request.niveauId || !request.code || !request.libelle) {
            throw new Error('Tous les champs obligatoires doivent être remplis');
        }
        return await this.api.create(request);
    }
}

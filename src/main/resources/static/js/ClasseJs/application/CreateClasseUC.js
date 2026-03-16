export class CreateClasseUC {
    constructor(api) {
        this.api = api;
    }

    async execute(request) {
        if (!request.specialiteId || !request.code) {
            throw new Error('Tous les champs obligatoires doivent être remplis');
        }
        return await this.api.create(request);
    }
}

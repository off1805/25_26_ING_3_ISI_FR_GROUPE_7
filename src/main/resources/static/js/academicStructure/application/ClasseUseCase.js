export class ClasseUseCase {
    constructor(api) {
        this.api = api;
    }

    async create(request) {
        if (!request.specialiteId || !request.code) {
            throw new Error('Tous les champs obligatoires doivent être remplis');
        }
        return await this.api.create(request);
    }

    async delete(id) {
        if (!id) {
            throw new Error('L\'ID de la classe est requis');
        }
        return await this.api.delete(id);
    }

    async findBySpecialiteId(specialiteId) {
        if (!specialiteId) {
            throw new Error('L\'ID de la spécialité est requis');
        }
        return await this.api.getBySpecialiteId(specialiteId);
    }
}
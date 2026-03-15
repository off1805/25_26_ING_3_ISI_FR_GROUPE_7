export class DeleteSpecialiteUC {
    constructor(api) {
        this.api = api;
    }

    async execute(id) {
        if (!id) {
            throw new Error('L\'ID de la spécialité est requis');
        }
        return await this.api.delete(id);
    }
}

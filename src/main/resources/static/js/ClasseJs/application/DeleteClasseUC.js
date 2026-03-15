export class DeleteClasseUC {
    constructor(api) {
        this.api = api;
    }

    async execute(id) {
        if (!id) {
            throw new Error('L\'ID de la classe est requis');
        }
        return await this.api.delete(id);
    }
}

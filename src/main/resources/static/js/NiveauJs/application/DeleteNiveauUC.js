export class DeleteNiveauUC {
    constructor(api) {
        this.api = api;
    }

    async execute(id) {
        if (!id) {
            throw new Error('L\'ID du niveau est requis');
        }
        return await this.api.delete(id);
    }
}

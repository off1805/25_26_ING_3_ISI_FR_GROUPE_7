export class CreateNiveauUC {
    constructor(api) {
        this.api = api;
    }

    async execute(request) {
        if (!request.filiereId) {
            throw new Error('L\'ID de la filière est requis');
        }
        if (!request.ordre) {
            throw new Error('L\'ordre du niveau est requis');
        }
        return await this.api.create(request);
    }
}

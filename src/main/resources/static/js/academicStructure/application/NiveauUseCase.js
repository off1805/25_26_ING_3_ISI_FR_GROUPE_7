export class NiveauUseCase {
    constructor(api) {
        this.api = api;
    }

    async create(request) {
        if (!request.filiereId) {
            throw new Error('L\'ID de la filière est requis');
        }
        if (!request.ordre) {
            throw new Error('L\'ordre du niveau est requis');
        }
        return await this.api.create(request);
    }

    async delete(id) {
        if (!id) {
            throw new Error('L\'ID du niveau est requis');
        }
        return await this.api.delete(id);
    }
    async findByFiliereId(filiereId) {
        if (!filiereId) {
            throw new Error('L\'ID de la filière est requis');
        }
        return await this.api.getByFiliereId(filiereId);
    }
}
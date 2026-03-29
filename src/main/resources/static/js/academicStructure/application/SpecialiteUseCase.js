


export class SpecialiteUseCase {
    constructor(specialiteApi) {
        this.specialiteApi = specialiteApi;
    }

    async create(request) {
        if (!request.niveauId || !request.code || !request.libelle) {
            throw new Error('Tous les champs obligatoires doivent être remplis');
        }
        return await this.specialiteApi.create(request);
    }

    async delete(id) {
        if (!id) {
            throw new Error('L\'ID de la spécialité est requis');
        }
        return await this.specialiteApi.delete(id);
    }

    async toggleStatus(id, activate) {
        if (!id) {
            throw new Error("L'ID de la spécialité est requis.");
        }
        try {
            if (activate) {
                return await this.specialiteApi.activerSpecialite(id);
            } else {
                return await this.specialiteApi.desactiverSpecialite(id);
            }
        } catch (e) {
            throw new Error(e.message || "Erreur lors du changement de statut de la spécialité.");
        }
    }

    async update(id, data) {
        if (!id) {
            throw new Error("L'ID de la spécialité est requis pour la mise à jour.");
        }
        if (!data || !data.code || !data.libelle) {
            throw new Error("Le code et le libellé de la spécialité sont requis.");
        }

        const payload = {
            code: data.code,
            libelle: data.libelle,
            description: data.description || '',
            brancheCode: data.brancheCode,
            niveauMinimum: data.niveauMinimum,
        };

        try {
            return await this.specialiteApi.updateSpecialite(id, payload);
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la mise à jour de la spécialité.");
        }
    }
    async findByNiveauId(niveauId) {
        if (!niveauId) {
            throw new Error('L\'ID du niveau est requis');
        }
        return await this.specialiteApi.getByNiveauId(niveauId);
    }
}
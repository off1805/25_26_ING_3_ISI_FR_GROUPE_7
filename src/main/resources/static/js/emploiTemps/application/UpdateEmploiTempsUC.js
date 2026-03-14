export class UpdateEmploiTempsUC {
    constructor(emploiTempsApi) {
        this.emploiTempsApi = emploiTempsApi;
    }

    async execute(emploiId, emploiData) {
        if (!emploiId) {
            throw new Error("L'ID de l'emploi du temps est requis");
        }

        const payload = {
            id: parseInt(emploiId),
            libelle: emploiData.libelle,
            dateDebut: emploiData.dateDebut,
            dateFin: emploiData.dateFin,
            semaine: emploiData.semaine || null,
            filiereId: parseInt(emploiData.filiereId),
            niveauId: parseInt(emploiData.niveauId)
        };

        try {
            const response = await this.emploiTempsApi.updateEmploiTemps(emploiId, payload);
            return response;
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la modification de l'emploi du temps.");
        }
    }
}
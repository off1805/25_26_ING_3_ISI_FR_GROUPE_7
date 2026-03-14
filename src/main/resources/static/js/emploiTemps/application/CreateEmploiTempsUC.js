export class CreateEmploiTempsUC {
    constructor(emploiTempsApi) {
        this.emploiTempsApi = emploiTempsApi;
    }

    async execute(emploiData) {
        if (!emploiData || !emploiData.libelle) {
            throw new Error("Le libellé de l'emploi du temps est requis.");
        }
        if (!emploiData.dateDebut || !emploiData.dateFin) {
            throw new Error("Les dates de début et de fin sont requises.");
        }
        if (!emploiData.filiereId || !emploiData.niveauId) {
            throw new Error("La filière et le niveau sont requis.");
        }


        const payload = {
            libelle: emploiData.libelle,
            dateDebut: emploiData.dateDebut,
            dateFin: emploiData.dateFin,
            semaine: emploiData.semaine || null,
            filiereId: parseInt(emploiData.filiereId),
            niveauId: parseInt(emploiData.niveauId)
        };

        try {
            const response = await this.emploiTempsApi.createEmploiTemps(payload);
            return response;
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la création de l'emploi du temps.");
        }
    }
}
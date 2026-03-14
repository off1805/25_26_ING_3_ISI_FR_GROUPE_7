export class CreateSeanceUC {
    constructor(seanceApi) {
        this.seanceApi = seanceApi;
    }

    async execute(seanceData) {
        if (!seanceData || !seanceData.libelle) {
            throw new Error("Le libellé de la séance est requis.");
        }
        if (!seanceData.dateSeance) {
            throw new Error("La date de la séance est requise.");
        }
        if (!seanceData.heureDebut || !seanceData.heureFin) {
            throw new Error("Les heures de début et de fin sont requises.");
        }


        const payload = {
            libelle: seanceData.libelle,
            dateSeance: seanceData.dateSeance,
            heureDebut: seanceData.heureDebut,
            heureFin: seanceData.heureFin,
            coursId: seanceData.coursId,
            enseignantId: seanceData.enseignantId,
            salle: seanceData.salle
        };

        try {
            const response = await this.seanceApi.createSeance(payload);
            return response;
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la création de la séance.");
        }
    }
}
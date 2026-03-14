export class UpdateSeanceUC {
    constructor(seanceApi) {
        this.seanceApi = seanceApi;
    }

    async execute(seanceId, seanceData) {
        if (!seanceId) {
            throw new Error("L'ID de la séance est requis");
        }

        const payload = {
            id: seanceId,
            libelle: seanceData.libelle,
            heureDebut: seanceData.heureDebut,
            heureFin: seanceData.heureFin,
            salle: seanceData.salle
        };

        try {
            const response = await this.seanceApi.updateSeance(seanceId, payload);
            return response;
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la modification de la séance.");
        }
    }
}
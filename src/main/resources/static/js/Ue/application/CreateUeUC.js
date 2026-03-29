export class CreateUeUC {
  constructor(ueApi) {
    this.ueApi = ueApi;
  }

  async execute(ueData) {
    if (!ueData) throw new Error("Données UE manquantes.");
    if (!ueData.libelle) throw new Error("Le libellé est requis.");
    if (!ueData.code) throw new Error("Le code est requis.");
    if (!ueData.credit || Number(ueData.credit) < 1) throw new Error("Le nombre de crédits doit être >= 1.");
    if (!ueData.volumeHoraireTotal || Number(ueData.volumeHoraireTotal) < 1) {
      throw new Error("Le volume horaire total doit être >= 1.");
    }
    if (!ueData.specialiteId || Number(ueData.specialiteId) < 1) {
      throw new Error("Spécialité invalide.");
    }

    // Ensure format matches CreateUeRequestDTO (backend).
    const payload = {
      libelle: String(ueData.libelle).trim(),
      code: String(ueData.code).trim(),
      credit: Number(ueData.credit),
      volumeHoraireTotal: Number(ueData.volumeHoraireTotal),
      description: ueData.description ? String(ueData.description).trim() : null,
      couleur: ueData.couleur ? String(ueData.couleur) : "#ffffff",
      specialiteId: Number(ueData.specialiteId),
      enseignantIds: Array.isArray(ueData.enseignantIds) ? ueData.enseignantIds.map(Number) : [],
    };

    try {
      return await this.ueApi.createUe(payload);
    } catch (e) {
      // ClientHttp throws Error("HTTP xxx") with payload.
      const msg =
        e?.payload?.message ||
        (typeof e?.payload === "string" ? e.payload : null) ||
        e?.message ||
        "Erreur lors de la création de l'UE.";
      throw new Error(msg);
    }
  }
}


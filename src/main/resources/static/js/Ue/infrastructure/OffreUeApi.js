import api from "../../common/ClientHttp.js";

export class OffreUeApi {
    async getActiveBySpecialite(specialiteId, page = 0, size = 5, filtre = {}) {
        const params = new URLSearchParams({ specialiteId, page, size });
        if (filtre.semestre != null) params.append('semestre', filtre.semestre);
        if (filtre.libelle) params.append('libelle', filtre.libelle);
        return api.get(`/api/offre-ue/active?${params}`);
    }

    async getByUeId(ueId) {
        return api.get(`/api/offre-ue?ueId=${ueId}&size=100`);
    }

    async updateOffreUe(id, payload) {
        return api.put(`/api/offre-ue/${id}`, payload);
    }
}

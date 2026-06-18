import api from '../../common/ClientHttp.js';

const ENDPOINT = '/api/justificatifs';

export const JustificatifApi = {

    /**
     * Soumet un justificatif avec fichier optionnel
     * @param {FormData} formData
     */
    soumettre: (formData) => api.post(ENDPOINT, formData),

    /**
     * Récupère les justificatifs d'un étudiant
     * @param {number} etudiantId
     */
    getByEtudiant: (etudiantId) => api.get(`${ENDPOINT}/etudiant/${etudiantId}`),
};

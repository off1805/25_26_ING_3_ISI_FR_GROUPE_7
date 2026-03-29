import api from '../../common/ClientHttp.js';

const ENDPOINT = '/api/students';

export const StudentApi = {
    /**
     * Inscrit un étudiant dans une classe.
     * @param {Object} data - { email, nom, prenom, matricule, numeroTelephone, classeId }
     * @returns {Promise<{userId, email, nom, prenom, matricule, classeId, created}>}
     */
    enroll: (data) => api.post(`${ENDPOINT}/enroll`, data),
};

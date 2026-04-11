
import api from '../../common/ClientHttp.js';

const ENDPOINT = '/api/students';
const ENDPOINT_USERS = '/api/users';

export const StudentApi = {
    /**
     * Inscrit un étudiant dans une classe.
     * @param {Object} data - { email, nom, prenom, matricule, numeroTelephone, classeId }
     * @returns {Promise<{userId, email, nom, prenom, matricule, classeId, created}>}
     */
    enroll: (data) => api.post(`${ENDPOINT}/enroll`, data),
    getStudentOfClass: (classId) => api.get(`${ENDPOINT_USERS}?classId=${classId}`),
    removeFromClass: (userId, classeId) => api.delete(`${ENDPOINT}/${userId}/classes/${classeId}`),
};

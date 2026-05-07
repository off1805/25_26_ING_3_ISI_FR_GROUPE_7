
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
    getStudentOfClass: (classId, page = 0, size = 10) => api.get(`${ENDPOINT_USERS}?classId=${classId}&page=${page}&size=${size}`),
    removeFromClass: (userId, classeId) => api.delete(`${ENDPOINT}/${userId}/classes/${classeId}`),
};

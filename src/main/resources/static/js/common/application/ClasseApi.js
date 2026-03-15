import api from "../ClientHttp.js";

const ENDPOINT = "/api/classes";

export const ClasseApi = {
    getAll: () => api.get(ENDPOINT),
    getById: (id) => api.get(`${ENDPOINT}/${id}`),
    create: (data) => api.post(ENDPOINT, data),
    update: (id, data) => api.put(`${ENDPOINT}/${id}`, data),
    delete: (id) => api.delete(`${ENDPOINT}/${id}`)
};

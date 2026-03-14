import api from "../ClientHttp.js";

const ENDPOINT = "/api/niveau";

export const NiveauApi = {
    getAll: (params = {}) => {
        const query = new URLSearchParams(params).toString();
        return api.get(query ? `${ENDPOINT}?${query}` : ENDPOINT);
    },
    getById: (id) => api.get(`${ENDPOINT}/${id}`),
    create: (data) => api.post(ENDPOINT, data),
    update: (id, data) => api.put(`${ENDPOINT}/${id}`, data),
    delete: (id) => api.delete(`${ENDPOINT}/${id}`)
};

import api from '../../common/ClientHttp.js';

export class CycleApi {

    async createCycle(cycleData) {
        return api.post("/api/cycles", cycleData);
    }

    async getAllCycles() {
        return api.get("/api/cycles");
    }

    async getCycleById(id) {
        return api.get(`/api/cycles/${id}`);
    }

    async updateCycle(id, cycleData) {
        return api.put(`/api/cycles/${id}`, cycleData);
    }

    async updateCycleStatus(id, status) {
        return api.put(`/api/cycles/${id}/status`, { status });
    }

    async deleteCycle(id) {
        return api.delete(`/api/cycles/${id}`);
    }
}

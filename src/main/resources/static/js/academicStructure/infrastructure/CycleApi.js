import api from '../../common/ClientHttp.js';

export class CycleApi {

    async createCycle(cycleData) {
        return api.post("/api/cycle", cycleData);
    }

    async getAllCycles() {
        return api.get("/api/cycle");
    }

    async getCycleById(id) {
        return api.get(`/api/cycle/${id}`);
    }

    async updateCycle(id, cycleData) {
        return api.put(`/api/cycle/${id}`, cycleData);
    }

    

    async updateCycleStatus(id, status) {
        return api.put(`/api/cycle/${id}/status`, { status });
    }

    async deleteCycle(id) {
        return api.delete(`/api/cycle/${id}`);
    }
}

import api from "../../common/ClientHttp.js";

export class UeApi {
  async createUe(payload) {
    return api.post("/api/ue", payload);
  }

  async searchUes(params = "") {
    return api.get(`/api/ue${params}`);
  }

  async getUeById(id) {
    return api.get(`/api/ue/${id}`);
  }

  async updateUe(id, payload) {
    return api.put(`/api/ue/${id}`, payload);
  }

  async getTeachers() {
    return api.get("/api/users?role=TEACHER&size=100");
  }
}


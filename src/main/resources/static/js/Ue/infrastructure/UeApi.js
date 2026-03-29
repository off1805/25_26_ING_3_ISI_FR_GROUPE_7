import api from "../../common/ClientHttp.js";

export class UeApi {
  async createUe(payload) {
    return api.post("/api/ue", payload);
  }

  async searchUes(params = "") {
    return api.get(`/api/ue${params}`);
  }

  async getTeachers() {
    return api.get("/api/users?role=TEACHER&size=100");
  }
}


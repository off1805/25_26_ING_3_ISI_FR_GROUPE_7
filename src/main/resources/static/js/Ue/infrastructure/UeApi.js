import api from "../../common/ClientHttp.js";

export class UeApi {
  async createUe(payload) {
    return api.post("/api/ue", payload);
  }

  async searchUes(params = "") {
    // params can be query string starting with '?'
    return api.get(`/api/ue${params}`);
  }
}


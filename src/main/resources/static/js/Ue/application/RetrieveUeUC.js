export class RetrieveUeUC {
    constructor(ueApi) {
        this.ueApi = ueApi;
    }

    async getBySpecialite(specialiteId) {
        const response = await this.ueApi.searchUes(`?specialiteId=${specialiteId}&deleted=false`);
        return response?.content || response || [];
    }
}

import api from '../../common/ClientHttp.js';

export class RetardApi {

    async getSemaine(classeId) {
        return api.get(`/surveillant/retards/${classeId}/data`);
    }

    async toggleInfoRow(infoRowId) {
        return api.put(`/api/retards/info-rows/${infoRowId}/toggle`);
    }
}

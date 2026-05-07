import api from '../../common/ClientHttp.js';
import { TokenService } from '../../common/application/TokenService.js';

export class ProfileApi {

    async getMyProfile() {
        return api.get('/api/profils/me');
    }

    async uploadPhoto(profileId, file) {
        const formData = new FormData();
        formData.append('photo', file);
        return await api.post(`/api/profils/${profileId}/photo`, formData);
    }
}

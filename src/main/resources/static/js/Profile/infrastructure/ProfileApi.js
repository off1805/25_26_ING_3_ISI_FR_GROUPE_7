import api from '../../common/ClientHttp.js';
import { TokenService } from '../../common/application/TokenService.js';

export class ProfileApi {

    async getMyProfile() {
        return api.get('/api/profils/me');
    }

    async uploadPhoto(profileId, file) {
        const token = await TokenService.getToken();
        const formData = new FormData();
        formData.append('photo', file);

        const response = await fetch(`/api/profils/${profileId}/photo`, {
            method: 'POST',
            headers: token ? { 'Authorization': `Bearer ${token}` } : {},
            body: formData
        });

        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        return response.json();
    }
}

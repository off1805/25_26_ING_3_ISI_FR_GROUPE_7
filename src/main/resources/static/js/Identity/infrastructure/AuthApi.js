import api from '../../common/ClientHttp.js';

export class AuthApi {
    async login(userEmail, userPassword) {
        return api.post("/login", { email: userEmail, password: userPassword });
    }
}

import api from '../../common/ClientHttp.js';

export class AuthApi{

    async deleteUser(id){
        let body= {email:userEmail, password:userPassword};
        return api.post("/auth/login",body);
    }

}

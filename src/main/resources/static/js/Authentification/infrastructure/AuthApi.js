import api from '../../common/ClientHttp.js';

export class AuthApi{
   async login(userEmail, userPassword){
        let body= {email:userEmail, password:userPassword};
        return api.post("/login",body);
    }
}

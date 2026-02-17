
export class TokenService{

    static async getToken(){
        return localStorage.getItem('token');
    }

    static async  getRefreshToken(){
        return localStorage.getItem('refresh_token');
    }

    static async  setRefreshToken(rToken){
        localStorage.setItem('refresh_token',rToken);
    }

    static async  setToken(token){
        localStorage.setItem('token',token);
    }

    static parseToken(token){
        return JSON.parse(atob(token.split('.')[1]));
    }

}
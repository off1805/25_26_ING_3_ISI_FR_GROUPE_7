import {TokenService} from "../../common/application/TokenService.js";
import {MapperRoleRedirectionPage} from "./MapperRoleRedirectionPage.js";

export class LoginUC{
    constructor(authApi) {
        this.authAPi= authApi;
    }

    async execute(userCredential){
        if(!userCredential.email || !userCredential.password){
            throw new Error("Email et mot de passe sont requis");
        }
        try{
            const response= await this.authAPi.login(userCredential.email,userCredential.password);
            const {token,refreshToken}=response;
            if(!token){
                throw  new Error('Connexion echoue: pas de token recu');
            }
            await TokenService.setToken(token);
            await TokenService.setRefreshToken(refreshToken);
            const payload= TokenService.parseToken(token);
            console.log("Connexion reussie");

            window.location.href=MapperRoleRedirectionPage(payload.role);


        }catch (e){
            throw new Error(e.message || "Erreur lors de la connexion.");
        }

    }

}

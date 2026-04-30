import { TokenService }             from "../../common/application/TokenService.js";
import { MapperRoleRedirectionPage } from "./MapperRoleRedirectionPage.js";

export class LoginUC {
    constructor(authApi) {
        this.authApi = authApi;
    }

    async execute(userCredential) {
        if (!userCredential.email || !userCredential.password) {
            throw new Error("Email et mot de passe sont requis");
        }

        const response = await this.authApi.login(userCredential.email, userCredential.password);
        const { token, refreshToken, role, displayName } = response;

        if (!token) throw new Error("Connexion échouée : pas de token reçu");
        if (!role)  throw new Error("Le backend n'a pas renvoyé le rôle.");

        await TokenService.setToken(token);
        await TokenService.setRefreshToken(refreshToken);
        localStorage.setItem("role", role);
        if (displayName) localStorage.setItem("displayName", displayName);

        window.location.href = MapperRoleRedirectionPage(role);
        return response;
    }
}

import { TokenService } from "../../common/application/TokenService.js";
import { MapperRoleRedirectionPage } from "./MapperRoleRedirectionPage.js";

export class LoginUC {
    constructor(authApi) {
        this.authAPi = authApi;
    }

    async execute(userCredential) {
        if (!userCredential.email || !userCredential.password) {
            throw new Error("Email et mot de passe sont requis");
        }

        try {
            const response = await this.authAPi.login(
                userCredential.email,
                userCredential.password
            );
            console.log("LOGIN RESPONSE =", response);
            console.log("ROLE RESPONSE =", response.role);
            console.log("DISPLAYNAME RESPONSE =", response.displayName);

            const { token, refreshToken, role, displayName } = response;

            if (!token) {
                throw new Error("Connexion échouée : pas de token reçu");
            }

            await TokenService.setToken(token);
            await TokenService.setRefreshToken(refreshToken);

            console.log("ROLE AVANT STOCKAGE =", role);

            if (!role) {
                throw new Error("Le backend n'a pas renvoyé le rôle.");
            }
            localStorage.setItem("role", role);

            if (displayName) {
                localStorage.setItem("displayName", displayName);
            }

            console.log("Connexion réussie");

            window.location.href = MapperRoleRedirectionPage(role);

            return response;
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la connexion.");
        }
    }

}
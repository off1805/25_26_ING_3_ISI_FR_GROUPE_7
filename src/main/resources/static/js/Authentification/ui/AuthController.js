import { LoginUC } from "../application/LoginUC.js";
import { AuthApi } from "../infrastructure/AuthApi.js";
import { UserCredentials } from "../domain/UserCredentials.js";
import { GlobalErrorHandler } from "../../common/GlobalErrorHandler.js";
import { GlobalEventNotifier } from "../../common/GlobalEventNotifier.js";
import { MapperRoleRedirectionPage } from "../application/MapperRoleRedirectionPage.js";

export class AuthController {
    constructor(loginUc) {
        this.loginUC = loginUc;
    }

    async login(event) {
        event.preventDefault();

        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;

        try {
            const data = await this.loginUC.execute(new UserCredentials(email, password));
            console.log(data)

            localStorage.setItem("token", data.token);
            localStorage.setItem("refreshToken", data.refreshToken);
            localStorage.setItem("role", data.role);
            localStorage.setItem("displayName", data.displayName);

            const redirect = MapperRoleRedirectionPage(data.role);
            if (redirect !== "/") {
                window.location.href = redirect;
            } else {
                console.log("Rôle inconnu :", data.role);
            }

        } catch (e) {
            alert(e);

            GlobalErrorHandler.handle(e);
            console.log("Erreur de connexion.", e.message);
        }
    }
}

const authApi = new AuthApi();
const loginUc = new LoginUC(authApi);
const authController = new AuthController(loginUc);

document.getElementById("loginForm").addEventListener("submit", (e) => authController.login(e));
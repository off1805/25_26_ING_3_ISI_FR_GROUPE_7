import { LoginUC } from "../application/LoginUC.js";
import { AuthApi } from "../infrastructure/AuthApi.js";
import { UserCredentials } from "../domain/UserCredentials.js";

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

            localStorage.setItem("token", data.token);
            localStorage.setItem("refreshToken", data.refreshToken);
            localStorage.setItem("role", data.role);
            localStorage.setItem("displayName", data.displayName);

            if (data.role === "ADMIN") {
                window.location.href = "/admin/annee-scolaire";
            } else if (data.role === "AP") {
                window.location.href = "/ap/classes";
            } else if (data.role === "TEACHER") {
                window.location.href = "/teacher/dashboard";
            } else {
                console.log("Rôle inconnu :", data.role);
            }

        } catch (e) {
            console.log("Erreur de connexion.", e.message);
        }
    }
}

const authApi = new AuthApi();
const loginUc = new LoginUC(authApi);
const authController = new AuthController(loginUc);

document.getElementById("loginForm").addEventListener("submit", (e) => authController.login(e));
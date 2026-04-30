import { AuthApi }        from "../infrastructure/AuthApi.js";
import { LoginUC }        from "../application/LoginUC.js";
import { UserCredentials } from "../domain/UserCredentials.js";
import { showError, setLoading, hideError } from "./loginPage.js";

class AuthController {
    constructor(loginUC) {
        this._loginUC = loginUC;
    }

    async login(event) {
        event.preventDefault();
        hideError();
        setLoading(true);
        const email    = document.getElementById("email").value;
        const password = document.getElementById("password").value;
        try {
            await this._loginUC.execute(new UserCredentials(email, password));
        } catch (e) {
            showError(e.message ?? "Erreur de connexion");
        } finally {
            setLoading(false);
        }
    }
}

const _controller = new AuthController(new LoginUC(new AuthApi()));
document.getElementById("loginForm")?.addEventListener("submit", e => _controller.login(e));

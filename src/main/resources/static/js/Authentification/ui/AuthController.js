import {LoginUC} from "../application/LoginUC.js";
import {AuthApi} from "../infrastructure/AuthApi.js";


export class AuthController{
    constructor(loginUc) {
        this.loginUC=loginUc;
    }

    async login(event){
        event.preventDefault();
        const email= document.getElementById('email').value;
        const password= document.getElementById('password').value;

        try{
            await this.loginUC.execute(email,password);
            alert("Bienvenue");
        }catch (e){
            console.log("Erreur de connexion.",e.message);
        }
    }
}

const authApi= new AuthApi();
const loginUc=new LoginUC(authApi);
const authController= new AuthController(loginUc);

document.getElementById('loginForm').addEventListener('submit',(e)=>authController.login(e));






export class RetrieveUserUC {
    constructor(userApi) {
        this.userApi = userApi;
    }

    async execute(filtre) {


        // Ensure format matches CreateUserRequestDTO
         let param= new URLSearchParams(filtre).toString();
        let filtreString= filtre?'?'+param:'';
       

        try { 
            const response = await this.userApi.retrieveUsers(filtreString);
            return response;
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la création de l'utilisateur.");
        }
    }
}

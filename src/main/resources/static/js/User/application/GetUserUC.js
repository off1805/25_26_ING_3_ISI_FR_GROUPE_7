export class RetrieveUserUC {
    constructor(userApi) {
        this.userApi = userApi;
    }

    async execute(email,role,status,includeDeleted=false) {


        // Ensure format matches CreateUserRequestDTO
        const payload = {
            email: userData.email,
            idRole: userData.idRole,
            idPermissions: Array.isArray(userData.idPermissions)
                ? userData.idPermissions
                : (userData.idPermissions ? [userData.idPermissions] : [])
        };

        try { 
            const response = await this.userApi.createUser(payload);
            return response;
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la création de l'utilisateur.");
        }
    }
}

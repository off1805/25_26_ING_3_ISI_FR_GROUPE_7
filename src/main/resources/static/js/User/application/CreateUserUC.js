export class CreateUserUC {
    constructor(userApi) {
        this.userApi = userApi;
    }

    async execute(userData) {

        if (!userData || !userData.email) {
            throw new Error("L'email de l'utilisateur est requis.");
        }

        // Ensure format matches CreateUserRequestDTO
        const payload = {
            email: userData.email,
            idRole: userData.idRole,
            idPermissions: Array.isArray(userData.idPermissions)
                ? userData.idPermissions
                : (userData.idPermissions ? [userData.idPermissions] : []),
            profile:userData.profile
        };

        try {
            const response = await this.userApi.createUser(payload);
            return response;
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la création de l'utilisateur.");
        }
    }
}

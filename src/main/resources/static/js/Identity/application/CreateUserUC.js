export class CreateUserUC {
    constructor(userApi) {
        this.userApi = userApi;
    }

    async execute(userData) {
        if (!userData?.email) throw new Error("L'email de l'utilisateur est requis.");

        const payload = {
            email:          userData.email,
            idRole:         userData.idRole,
            idPermissions:  Array.isArray(userData.idPermissions)
                                ? userData.idPermissions
                                : (userData.idPermissions ? [userData.idPermissions] : []),
            profile:        userData.profile
        };

        return this.userApi.createUser(payload);
    }
}

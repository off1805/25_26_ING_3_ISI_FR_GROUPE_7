export class DeleteUserUC {
    constructor(userApi) {
        this.userApi = userApi;
    }

    async execute(userId) {
        if (!userId) throw new Error("ID de l'utilisateur requis pour la suppression");
        return this.userApi.deleteUser(userId);
    }
}

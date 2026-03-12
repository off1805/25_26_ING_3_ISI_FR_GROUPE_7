export class DeleteUserUC {
    constructor(userApi) {
        this.userApi = userApi;
    }

    async execute(userId) {
        if (!userId) {
            throw new Error("ID de l'utilisateur requis pour la suppression");
        }
        try {
            await this.userApi.deleteUser(userId);
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la suppression de l'utilisateur.");
        }
    }
}

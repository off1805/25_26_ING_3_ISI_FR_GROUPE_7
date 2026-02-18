export class ModifyUserStatusUC {
    constructor(userApi) {
        this.userApi = userApi;
    }

    async execute(userId, newStatus) {
        if (!userId || !newStatus) {
            throw new Error("L'ID de l'utilisateur et le nouveau statut sont requis");
        }
        try {
            const response = await this.userApi.updateUserStatus(userId, newStatus);
            return response;
        } catch (e) {
            throw new Error(e.message || "Erreur lors de la modification du statut de l'utilisateur.");
        }
    }
}

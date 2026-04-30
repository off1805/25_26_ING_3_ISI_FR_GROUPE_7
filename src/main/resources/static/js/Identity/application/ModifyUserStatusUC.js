export class ModifyUserStatusUC {
    constructor(userApi) {
        this.userApi = userApi;
    }

    async execute(userId, newStatus) {
        if (!userId || !newStatus) throw new Error("L'ID et le nouveau statut sont requis");
        return this.userApi.updateUserStatus(userId, newStatus);
    }
}

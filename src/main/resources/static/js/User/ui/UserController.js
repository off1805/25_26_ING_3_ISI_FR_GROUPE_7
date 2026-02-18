
import { CreateUserUC } from "../application/CreateUserUC.js";
import { DeleteUserUC } from "../application/DeleteUserUC.js";
import { ModifyUserStatusUC } from "../application/ModifyUserStatusUC.js";
import { UserApi } from "../infrastructure/UserApi.js";

export class UserController {
    constructor(userApi, createUserUC, deleteUserUC, modifyUserStatusUC) {
        this.userApi = userApi;
        this.createUserUC = createUserUC;
        this.deleteUserUC = deleteUserUC;
        this.modifyUserStatusUC = modifyUserStatusUC;
        this.currentPermissions = []; // Store fetched permissions
        this.init();
    }

    init() {
        const addUserForm = document.querySelector('form[method="post"]');
        if (addUserForm) {
            addUserForm.addEventListener('submit', (e) => this.handleCreateUser(e));

            const roleSelect = addUserForm.querySelector('select[name="role"]');
            if (roleSelect) {
                roleSelect.addEventListener('change', (e) => this.handleRoleChange(e));
                // Initial fetch for the default selected role
                if (roleSelect.value) {
                    this.userApi.getPermissionsByRole(roleSelect.value)
                        .then(permissions => {
                            this.currentPermissions = permissions || [];
                            this.updatePermissionsUI(this.currentPermissions);
                        })
                        .catch(e => console.error("Erreur init permissions", e));
                }
            }
        }

        document.querySelectorAll('.js-delete-user').forEach(btn => {
            btn.addEventListener('click', (e) => this.handleDeleteUser(e));
        });

        document.querySelectorAll('.js-block-user').forEach(btn => {
            btn.addEventListener('click', (e) => this.handleBlockUser(e));
        });
    }

    async handleCreateUser(event) {
        event.preventDefault();
        const formData = new FormData(event.target);

        // Read checked permissions from the container (not disabled)
        const checkedPermissions = [...document.querySelectorAll('#permissionsContainer input[type="checkbox"]:checked')]
            .map(cb => cb.value);

        const userData = {
            email: formData.get('email'),
            password: "Pass1234@School",
            idRole: formData.get('role'),
            idPermissions: checkedPermissions.length > 0 ? checkedPermissions : this.currentPermissions.map(p => p.id)
        };

        if (!userData.idPermissions || userData.idPermissions.length === 0) {
            alert("Veuillez sélectionner au moins une permission.");
            return;
        }

        try {
            await this.createUserUC.execute(userData);
            alert("Utilisateur créé avec succès !");
            window.location.reload();
        } catch (e) {
            alert("Erreur lors de la création : " + e.message);
            console.error(e);
        }
    }

    async handleDeleteUser(event) {
        if (confirm("Voulez-vous vraiment supprimer cet utilisateur ?")) {
            const button = event.currentTarget;
            const userId = button.getAttribute('data-user-id');

            if (!userId) {
                alert("Erreur: ID utilisateur introuvable.");
                return;
            }

            try {
                await this.deleteUserUC.execute(userId);
                alert("Utilisateur supprimé avec succès !");
                window.location.reload();
            } catch (e) {
                alert("Erreur lors de la suppression : " + e.message);
                console.error(e);
            }
        }
    }

    async handleBlockUser(event) {
        if (confirm("Voulez-vous vraiment bloquer/débloquer cet utilisateur ?")) {
            const button = event.currentTarget;
            const userId = button.getAttribute('data-user-id');

            if (!userId) {
                alert("Erreur: ID utilisateur introuvable.");
                return;
            }

            try {
                await this.modifyUserStatusUC.execute(userId, "BLOCKED");
                alert("Statut utilisateur modifié avec succès !");
                window.location.reload();
            } catch (e) {
                alert("Erreur lors de la modification du statut : " + e.message);
                console.error(e);
            }
        }
    }
    async handleRoleChange(event) {
        const roleName = event.target.value;
        if (!roleName) return;

        try {
            const permissions = await this.userApi.getPermissionsByRole(roleName);
            this.currentPermissions = permissions || [];
            this.updatePermissionsUI(this.currentPermissions);
        } catch (e) {
            console.error("Erreur lors de la récupération des permissions", e);
        }
    }

    updatePermissionsUI(permissions) {
        const container = document.getElementById('permissionsContainer');
        if (!container) return;

        container.innerHTML = ''; // Clear existing

        if (!permissions || permissions.length === 0) {
            container.innerHTML = '<div class="col-12 text-secondary small">Aucune permission pour ce rôle.</div>';
            return;
        }

        permissions.forEach(p => {
            const div = document.createElement('div');
            div.className = 'col-12 col-sm-6';
            div.innerHTML = `
                <label class="border rounded-3 p-2 d-flex align-items-center gap-2 w-100 bg-body-tertiary cursor-pointer">
                    <input class="form-check-input m-0" type="checkbox" name="permissions" value="${p.id}" checked />
                    <span class="fw-semibold small">${p.label || p.id}</span>
                </label>
            `;
            container.appendChild(div);
        });
    }
}


const userApi = new UserApi();
const createUserUC = new CreateUserUC(userApi);
const deleteUserUC = new DeleteUserUC(userApi);
const modifyUserStatusUC = new ModifyUserStatusUC(userApi);
new UserController(userApi, createUserUC, deleteUserUC, modifyUserStatusUC);

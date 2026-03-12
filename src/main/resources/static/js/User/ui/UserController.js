
import { GlobalErrorHandler } from "../../common/GlobalErrorHandler.js";
import { CreateUserUC } from "../application/CreateUserUC.js";
import { DeleteUserUC } from "../application/DeleteUserUC.js";
import { ModifyUserStatusUC } from "../application/ModifyUserStatusUC.js";
import { UserApi } from "../infrastructure/UserApi.js";
import { userRowTable } from "./userRowTable.js";
import { GlobalEventNotifier } from "../../common/GlobalEventNotifier.js";
import { RetrieveUserUC } from "../application/RetrieveUserUC.js";

export class UserController {
    constructor(userApi, createUserUC, deleteUserUC, modifyUserStatusUC, retrieveUserUc) {
        this.userApi = userApi;
        this.createUserUC = createUserUC;
        this.deleteUserUC = deleteUserUC;
        this.modifyUserStatusUC = modifyUserStatusUC;
        this.retrieveUserUc = retrieveUserUc;
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

        document.getElementById('table-body').addEventListener('click', (e) => {
            if (e.target.closest('.btn-delete-user')) {
                this.handleDeleteUser({ currentTarget: e.target.closest('.btn-delete-user') });
            }
            if (e.target.closest('.btn-block-user')) {
                this.handleBlockUser({ currentTarget: e.target.closest('.btn-block-user') });
            }
        });

        let input = document.getElementById("search-by-email-on-personnel");
        if(!input){
            console.log("element not present yet on DOM.");
        }
        input.addEventListener('keydown', async (e) => {
            if (e.key === 'Enter') { // touche Entrée
                e.preventDefault();
                const query = input.value.trim();
                await this._reloadView()
                console.log('Recherche lancée pour:', query);
                // appel API ou filtrage
            }
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
            idRole: formData.get('role'),
            idPermissions: checkedPermissions.length > 0 ? checkedPermissions : this.currentPermissions.map(p => p.id)
        };

        if (!userData.idPermissions || userData.idPermissions.length === 0) {
            alert("Veuillez sélectionner au moins une permission.");
            return;
        }

        try {
            const newUser = await this.createUserUC.execute(userData);
            HSOverlay.close('#hs-modal-add-user');
            GlobalEventNotifier.eventWellDone("Utilisateur créé avec succès !");
            this._addUserRow(newUser);



        } catch (e) {
            GlobalErrorHandler.handle(e);
        }
    }

    async _reloadView() {
        let pageUser = await this.retrieveUserUc.execute(this._getFilter());
        console.log(pageUser);
        const tableBody = document.getElementById('table-body');
        tableBody.innerHTML = '';
        pageUser.content.forEach(user => this._addUserRow(user));


    }

    _getFilter() {
        const searchBar = document.getElementById("search-by-email-on-personnel");
        if (!searchBar) return;
        let emailForm = searchBar.value;
        let filter = { email: emailForm, role: ["ADMIN", "AP", "SURVEILLANT"] };
        return filter;
    }

    _addUserRow(user) {



        const tableBody = document.getElementById('table-body');

        const statusClass = user.status === 'ACTIVE' ? 'bg-green-500/10 text-green-600' : 'bg-red-500/10 text-red-500';
        const role = user.roleName === 'TEACHER' ? 'Enseignant' : (user.roleName === 'SURVEILLANT' ? 'Surveillant' : (user.roleName === 'AP' ? 'Ap' : 'Autre'));
        const roleClass = user.roleName === 'TEACHER' ? 'bg-blue-400/10 text-blue-500' : (user.roleName === 'SURVEILLANT' ? 'bg-indigo-400/10 text-indigo-500' : (user.roleName === 'AP' ? 'bg-orange-400/10 text-orange-500' : 'bg-muted text-muted-foreground-2'));
        const status = user.status === 'ACTIVE' ? 'Actif' : 'bloque';
        const row = userRowTable(user.email, role, status, user.id, roleClass, statusClass);
        tableBody.appendChild(row);
        window.HSStaticMethods.autoInit();
    }

    _removeUserRow(id) {
        const table = document.getElementById("table-body");
        let childrenArray = [...table.children];
        console.log(childrenArray);

        const target = childrenArray.find((m) =>
            m.dataset.userId === `${id}`
        );

        target.remove();

    }

    async handleDeleteUser(event) {
        if (confirm("Voulez-vous vraiment supprimer cet utilisateur ?")) {
            const button = event.currentTarget;
            const userId = button.dataset.userId;

            if (!userId) {
                alert("Erreur: ID utilisateur introuvable.");
                return;
            }

            try {
                await this.deleteUserUC.execute(userId);
                alert(`Utilisateur supprimé avec succès ! + ${userId}`);
                this._removeUserRow(userId);
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

    _createPermissionsBadge(permissionName) {
        const span = document.createElement('span');
        span.id = `badge-${permissionName}`;
        span.className = 'inline-flex items-center gap-x-1.5 py-1.5 ps-3 pe-2 me-3 mb-2 rounded-full text-xs  font-medium lowercase  bg-primary-50 text-primary-800 dark:bg-primary-400/20 dark:text-primary-400 border border-primary-100 dark:border-primary-500/20 ';

        const badge = `
       
        ${permissionName}
       
            
            <button type="button" class="remove-btn shrink-0 size-4 inline-flex items-center justify-center rounded-full hover:bg-primary-200 focus:outline-hidden focus:bg-primary-200 dark:hover:bg-primary-900 dark:focus:bg-primary-900">
                <span class="sr-only">Remove badge</span>
                <svg class="shrink-0 size-3" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 6 6 18"/><path d="m6 6 12 12"/></svg>
            </button>
           
        `;
        span.innerHTML = badge;
        return span;


        view.appendChild(span);


    }

    updatePermissionsUI(permissions) {
        const container = document.getElementById('permissionsContainer');
        const view = document.getElementById('permissions-view');


        if (!container) return;

        container.innerHTML = ''; // Clear existing
        view.innerHTML = '';

        if (!permissions || permissions.length === 0) {
            container.innerHTML = '<div class="col-12 text-secondary small">Aucune permission pour ce rôle.</div>';
            return;
        }

        permissions.forEach(p => {
            const div = document.createElement('div');
            div.className = 'flex gap-x-3 py-2 px-3 rounded-lg hover:bg-dropdown-item-hover focus:outline-hidden focus:bg-dropdown-item-focus';
            div.innerHTML = `
               
                                    <input id="hs-dropdown-item-checkbox-${p.id}" name="hs-dropdown-item-checkbox-${p.id}"
                                        type="checkbox"
                                        class="mt-0.5 shrink-0 size-4 bg-transparent border-line-3 rounded-sm shadow-2xs text-primary focus:ring-0 focus:ring-offset-0 checked:bg-primary-checked checked:border-primary-checked disabled:opacity-50 disabled:pointer-events-none"
                                        aria-describedby="hs-dropdown-item-checkbox-${p.id}-description" checked>
                                    <label for="hs-dropdown-item-checkbox-${p.id}" value="${p.id}">
                                        <span class="block text-sm font-semibold text-foreground">${p.id}</span>
                                        <span id="hs-dropdown-item-checkbox-${p.id}-description"
                                            class="block text-sm text-muted-foreground-2">${p.label}.
                                            </span>
                                    </label>
                            
            `;
            const badge = this._createPermissionsBadge(p.id);
            badge.querySelector('.remove-btn').addEventListener('click', () => {
                if (view.children.length > 1) {
                    badge.remove();
                    div.querySelector('input').checked = false;
                }


            });
            div.querySelector('input').addEventListener('change', (e) => {
                if (e.target.checked) {
                    view.appendChild(badge);
                } else {
                    if (view.children.length > 1) {
                        badge.remove();
                    } else {
                        e.target.checked = true;
                    }
                }
            });
            view.appendChild(badge);

            container.appendChild(div);
        });
    }
}


const userApi = new UserApi();
const createUserUC = new CreateUserUC(userApi);
const deleteUserUC = new DeleteUserUC(userApi);
const modifyUserStatusUC = new ModifyUserStatusUC(userApi);
const retrieveUserUc = new RetrieveUserUC(userApi);
new UserController(userApi, createUserUC, deleteUserUC, modifyUserStatusUC, retrieveUserUc);

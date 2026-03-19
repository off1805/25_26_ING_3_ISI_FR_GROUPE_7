import { GlobalErrorHandler } from "../../common/GlobalErrorHandler.js";
import { CreateUserUC } from "../application/CreateUserUC.js";
import { DeleteUserUC } from "../application/DeleteUserUC.js";
import { ModifyUserStatusUC } from "../application/ModifyUserStatusUC.js";
import { UserApi } from "../infrastructure/UserApi.js";
import { userRowTable } from "./userRowTable.js";
import { GlobalEventNotifier } from "../../common/GlobalEventNotifier.js";

const SELECTORS = {
    createCompteForm: 'form[data-form="create-compte"]',
    createProfilForm: 'form[data-form="create-profil"]',
    createUserTabItemCompte: '#tab-create-user-item-1',
    createUserTabItemProfil: '#tab-create-user-item-2',

};

export class UserController {
    constructor(userApi, createUserUC, deleteUserUC, modifyUserStatusUC) {
        this.userApi = userApi;
        this.createUserUC = createUserUC;
        this.deleteUserUC = deleteUserUC;
        this.modifyUserStatusUC = modifyUserStatusUC;
        this.currentPermissions = [];
        this.currentUserCreateInfo={};
        this.init();
    }

    init() {
        // ── Formulaire CRÉER ──
        const addUserForm = document.querySelector(SELECTORS.createCompteForm);
        if (addUserForm) {
            addUserForm.addEventListener('submit', (e) => this.switchToProfilEdition(e));
            const roleSelect = addUserForm.querySelector('select[name="role"]');
            if (roleSelect) {
                roleSelect.addEventListener('change', (e) => this.handleRoleChange(e));
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

        const profilForm = document.querySelector(SELECTORS.createProfilForm);
        if (profilForm) {
            profilForm.addEventListener('submit', (e) => this.handleCreateUser(e));
        }

        const modal = document.getElementById('hs-modal-add-user');

        const observer = new MutationObserver(() => {
            if (modal.classList.contains('hidden')) {
                this.currentUserCreateInfo={};
                this.currentPermissions = [];
            }
        });

        observer.observe(modal, { attributes: true, attributeFilter: ['class'] });




        // ── Formulaire MODIFIER ──
        const editUserForm = document.getElementById('editUserForm');
        if (editUserForm) {
            editUserForm.addEventListener('submit', (e) => this.handleEditUser(e));
        }

        // ── Boutons DELETE & EDIT existants (rendu Thymeleaf) ──
        this._bindTableActions(document.getElementById('table-body'));
    }

    // ── Bind clics sur une tbody (ou un tr) ──────────────────────────────────
    _bindTableActions(container) {
        if (!container) return;

        container.querySelectorAll('.btn-delete-user').forEach(btn => {
            btn.addEventListener('click', (e) => this.handleDeleteUser(e));
        });

        container.querySelectorAll('.btn-edit-user').forEach(btn => {
            btn.addEventListener('click', (e) => this.handleOpenEditModal(e));
        });
    }

    _getRoleClass(roleName) {
        switch (roleName) {
            case 'TEACHER': return 'bg-blue-500/10 text-blue-500';
            case 'ADMIN':
            case 'SURVEILLANT': return 'bg-indigo-500/10 text-indigo-500';
            case 'AP': return 'bg-orange-500/10 text-orange-500';
            default: return 'bg-muted text-muted-foreground-2';
        }
    }

    _getRoleLabel(roleName) {
        switch (roleName) {
            case 'TEACHER': return 'ENSEIGNANT';
            case 'ADMIN':
            case 'SURVEILLANT': return 'SURVEILLANT';
            case 'AP': return 'AP';
            default: return 'AUTRE';
        }
    }

    _getStatusClass(status) {
        return status === 'ACTIVE'
            ? 'bg-green-500/10 text-green-600'
            : 'bg-red-500/10 text-red-500';
    }

    _getStatusLabel(status) {
        return status === 'ACTIVE' ? 'Actif' : 'Bloqué';
    }

    // ── Sync empty state ─────────────────────────────────────────────────────
    _syncEmptyState() {
        const tbody = document.getElementById('table-body');
        const emptyRow = document.getElementById('empty-state-row');
        if (!tbody || !emptyRow) return;
        const realRows = tbody.querySelectorAll('tr:not(#empty-state-row)').length;
        emptyRow.classList.toggle('hidden', realRows > 0);
    }

    switchToProfilEdition(event) {
        event.preventDefault();
        const formData = new FormData(event.target);
        const checkedPermissions = [
            ...document.querySelectorAll('#permissionsContainer input[type="checkbox"]:checked')
        ].map(cb => cb.value);

        const userData = {
            email: formData.get('email'),
            idRole: formData.get('role'),
            idPermissions: checkedPermissions.length > 0
                ? checkedPermissions
                : this.currentPermissions.map(p => p.id)
        };
       

        if (!userData.idPermissions || userData.idPermissions.length === 0) {
            alert("Veuillez sélectionner au moins une permission.");
            return;
        }
         this.currentUserCreateInfo=userData;

        const tabItemCompte = document.querySelector(SELECTORS.createUserTabItemCompte);
        const tabItemProfil = document.querySelector(SELECTORS.createUserTabItemProfil);
        if (tabItemCompte && tabItemProfil) {
            tabItemProfil.removeAttribute('disabled');
            tabItemProfil.click();
        }


    }

    // ── CRÉER ────────────────────────────────────────────────────────────────
    async handleCreateUser(event) {
        event.preventDefault();
        const formData = new FormData(event.target);
         const profilData = {
            nom: formData.get('noms'),
           prenom: formData.get('prenoms'),
           matricule: formData.get('matricule'),
           numeroTelephone: formData.get('telephone'),
        };
        this.currentUserCreateInfo["profile"]=profilData;
        console.log("Données complètes pour création :", this.currentUserCreateInfo);


        try {
            const newUser = await this.createUserUC.execute(this.currentUserCreateInfo);
           
            GlobalEventNotifier.eventWellDone("Utilisateur créé avec succès !");
            this._addUserRow(newUser);
        } catch (e) {
            GlobalErrorHandler.handle(e);
        }finally{
             HSOverlay.close('#hs-modal-add-user');
             document.querySelector(SELECTORS.createCompteForm)?.reset();
             document.querySelector(SELECTORS.createProfilForm)?.reset();
             const tabItemCompte = document.querySelector(SELECTORS.createUserTabItemCompte);
             const tabItemProfil = document.querySelector(SELECTORS.createUserTabItemProfil);
                if (tabItemCompte && tabItemProfil) {
                    tabItemProfil.setAttribute('disabled', 'disabled');
                    tabItemCompte.click();
                }
        }
    }

    _addUserRow(user) {
        const tableBody = document.getElementById('table-body');
        const row = userRowTable(
            user.email,
            this._getRoleLabel(user.roleName),
            this._getStatusLabel(user.status),
            user.id,
            this._getRoleClass(user.roleName),
            this._getStatusClass(user.status)
        );

        // Stocker les données brutes sur la ligne pour l'édition
        row.dataset.userId = user.id;
        row.dataset.userEmail = user.email;
        row.dataset.userRole = user.roleName;
        row.dataset.userStatus = user.status;

        // Bind les boutons de la nouvelle ligne
        row.querySelector('.btn-delete-user')?.addEventListener('click', (e) => this.handleDeleteUser(e));
        row.querySelector('.btn-edit-user')?.addEventListener('click', (e) => this.handleOpenEditModal(e));

        tableBody.appendChild(row);
        window.HSStaticMethods.autoInit();
        this._syncEmptyState();
    }

    _removeUserRow(id) {
        const table = document.getElementById("table-body");
        const target = [...table.children].find(tr => tr.dataset.userId === `${id}`);
        if (target) target.remove();
        this._syncEmptyState();
    }

    // ── SUPPRIMER ────────────────────────────────────────────────────────────
    async handleDeleteUser(event) {
        if (!confirm("Voulez-vous vraiment supprimer cet utilisateur ?")) return;

        const button = event.currentTarget;
        const userId = button.dataset.userId;

        if (!userId) {
            alert("Erreur: ID utilisateur introuvable.");
            return;
        }

        try {
            await this.deleteUserUC.execute(userId);
            GlobalEventNotifier.eventWellDone("Utilisateur supprimé avec succès !");
            this._removeUserRow(userId);
        } catch (e) {
            GlobalErrorHandler.handle(e);
        }
    }

    // ── OUVRIR MODAL MODIFIER ────────────────────────────────────────────────
    handleOpenEditModal(event) {
        const button = event.currentTarget;
        // Remonter jusqu'à la <tr> pour lire les data
        const row = button.closest('tr');
        const userId = row?.dataset.userId || button.dataset.userId;
        const userEmail = row?.dataset.userEmail || button.dataset.userEmail || '';
        const userRole = row?.dataset.userRole || button.dataset.userRole || '';
        const userStatus = row?.dataset.userStatus || button.dataset.userStatus || '';

        // Pré-remplir le modal
        document.getElementById('edit-user-id').value = userId;
        document.getElementById('edit-user-email').value = userEmail;

        // Sélectionner la bonne option rôle
        const roleSelect = document.getElementById('edit-user-role');
        const statusSelect = document.getElementById('edit-user-status');
        if (roleSelect) roleSelect.value = userRole;
        if (statusSelect) statusSelect.value = userStatus;

        // Ré-initialiser les hs-select pour qu'ils reflètent la valeur
        window.HSStaticMethods.autoInit();

        // Ouvrir le modal
        HSOverlay.open('#hs-modal-edit-user');
    }

    // ── SAUVEGARDER MODIFICATION ─────────────────────────────────────────────
    async handleEditUser(event) {
        event.preventDefault();
        const formData = new FormData(event.target);

        const userId = formData.get('id');
        const newEmail = formData.get('email');
        const newRole = formData.get('role');
        const newStatus = formData.get('status');

        try {
            // Adapter selon votre API — exemple :
            const updatedUser = await this.userApi.updateUser(userId, {
                email: newEmail,
                role: newRole,
                status: newStatus
            });

            HSOverlay.close('#hs-modal-edit-user');
            GlobalEventNotifier.eventWellDone("Utilisateur modifié avec succès !");

            // Mettre à jour la ligne dans le tableau
            this._updateUserRow(updatedUser ?? { id: userId, email: newEmail, roleName: newRole, status: newStatus });
        } catch (e) {
            GlobalErrorHandler.handle(e);
        }
    }

    _updateUserRow(user) {
        const tbody = document.getElementById('table-body');
        const oldRow = [...tbody.children].find(tr => tr.dataset.userId === `${user.id}`);
        if (!oldRow) return;

        // Recréer la ligne avec les nouvelles données
        const newRow = userRowTable(
            user.email,
            this._getRoleLabel(user.roleName),
            this._getStatusLabel(user.status),
            user.id,
            this._getRoleClass(user.roleName),
            this._getStatusClass(user.status)
        );

        newRow.dataset.userId = user.id;
        newRow.dataset.userEmail = user.email;
        newRow.dataset.userRole = user.roleName;
        newRow.dataset.userStatus = user.status;

        newRow.querySelector('.btn-delete-user')?.addEventListener('click', (e) => this.handleDeleteUser(e));
        newRow.querySelector('.btn-edit-user')?.addEventListener('click', (e) => this.handleOpenEditModal(e));

        tbody.replaceChild(newRow, oldRow);
        window.HSStaticMethods.autoInit();
    }

    // ── BLOQUER ──────────────────────────────────────────────────────────────
    async handleBlockUser(event) {
        if (!confirm("Voulez-vous vraiment bloquer/débloquer cet utilisateur ?")) return;

        const button = event.currentTarget;
        const userId = button.dataset.userId;

        if (!userId) { alert("Erreur: ID utilisateur introuvable."); return; }

        try {
            await this.modifyUserStatusUC.execute(userId, "BLOCKED");
            GlobalEventNotifier.eventWellDone("Statut utilisateur modifié avec succès !");
            window.location.reload();
        } catch (e) {
            GlobalErrorHandler.handle(e);
        }
    }

    // ── CHANGEMENT DE RÔLE (formulaire add) ──────────────────────────────────
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

    // ── PERMISSIONS UI ────────────────────────────────────────────────────────
    _createPermissionsBadge(permissionName) {
        const span = document.createElement('span');
        span.id = `badge-${permissionName}`;
        span.className = 'inline-flex items-center gap-x-1.5 py-1.5 ps-3 pe-2 me-3 mb-2 rounded-full text-xs font-medium lowercase bg-primary-50 text-primary-800 dark:bg-primary-400/20 dark:text-primary-400 border border-primary-100 dark:border-primary-500/20';
        span.innerHTML = `
            ${permissionName}
            <button type="button" class="remove-btn shrink-0 size-4 inline-flex items-center justify-center rounded-full hover:bg-primary-200 focus:outline-hidden">
                <span class="sr-only">Remove badge</span>
                <svg class="shrink-0 size-3" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 6 6 18"/><path d="m6 6 12 12"/></svg>
            </button>
        `;
        return span;
    }

    updatePermissionsUI(permissions) {
        const container = document.getElementById('permissionsContainer');
        const view = document.getElementById('permissions-view');
        if (!container) return;

        container.innerHTML = '';
        view.innerHTML = '';

        if (!permissions || permissions.length === 0) {
            container.innerHTML = '<div class="py-2 px-3 text-sm text-muted-foreground-2">Aucune permission pour ce rôle.</div>';
            return;
        }

        permissions.forEach(p => {
            const div = document.createElement('div');
            div.className = 'flex items-start gap-3 py-2 px-3 rounded-lg hover:bg-dropdown-item-hover cursor-pointer transition-colors';

            div.innerHTML = `
               <div class="relative shrink-0 mt-0.5">
                   <input id="hs-dropdown-item-checkbox-${p.id}"
                          name="hs-dropdown-item-checkbox-${p.id}"
                          type="checkbox"
                          value="${p.id}"
                          class="peer sr-only"
                          aria-describedby="hs-dropdown-item-checkbox-${p.id}-description"
                          checked>
                   <div class="size-5 rounded-md border-2 border-layer-line bg-layer
                               peer-checked:bg-primary peer-checked:border-primary
                               flex items-center justify-center transition-all">
                       <svg class="hidden peer-checked:block size-3 text-white"
                            xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"
                            fill="none" stroke="currentColor" stroke-width="3.5">
                           <polyline points="20 6 9 17 4 12"/>
                       </svg>
                   </div>
               </div>
               <label for="hs-dropdown-item-checkbox-${p.id}" class="cursor-pointer">
                   <span class="block text-sm font-semibold text-primary">${p.id}</span>
                   <span id="hs-dropdown-item-checkbox-${p.id}-description"
                         class="block text-xs text-muted-foreground-2 mt-0.5">${p.label}</span>
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
                    if (view.children.length > 1) badge.remove();
                    else e.target.checked = true;
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
new UserController(userApi, createUserUC, deleteUserUC, modifyUserStatusUC);
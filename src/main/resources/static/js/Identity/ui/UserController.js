import { GlobalErrorHandler }   from "../../common/GlobalErrorHandler.js";
import { GlobalEventNotifier }  from "../../common/GlobalEventNotifier.js";
import { customAlert }          from "../../common/CustomAlert.js";

import { UserApi }              from "../infrastructure/UserApi.js";
import { CreateUserUC }         from "../application/CreateUserUC.js";
import { DeleteUserUC }         from "../application/DeleteUserUC.js";
import { ModifyUserStatusUC }   from "../application/ModifyUserStatusUC.js";
import { RetrieveUserUC }       from "../application/RetrieveUserUC.js";
import { userRowTable }         from "./userRowTable.js";

const SELECTORS = {
    createCompteForm:       'form[data-form="create-compte"]',
    createProfilForm:       'form[data-form="create-profil"]',
    createUserTabItemCompte:'#tab-create-user-item-1',
    createUserTabItemProfil:'#tab-create-user-item-2',
};

class UserController {
    constructor(userApi, createUserUC, deleteUserUC, modifyUserStatusUC, retrieveUserUC) {
        this.userApi            = userApi;
        this.createUserUC       = createUserUC;
        this.deleteUserUC       = deleteUserUC;
        this.modifyUserStatusUC = modifyUserStatusUC;
        this.retrieveUserUC     = retrieveUserUC;
        this.currentPermissions     = [];
        this.currentUserCreateInfo  = {};
        this._staffSearchDebounce   = null;
        this.init();
    }

    init() {
        const addUserForm = document.querySelector(SELECTORS.createCompteForm);
        if (addUserForm) {
            addUserForm.addEventListener('submit', e => this.switchToProfilEdition(e));
            const roleSelect = addUserForm.querySelector('select[name="role"]');
            if (roleSelect) {
                roleSelect.addEventListener('change', e => this.handleRoleChange(e));
                if (roleSelect.value) {
                    this.userApi.getPermissionsByRole(roleSelect.value)
                        .then(p => { this.currentPermissions = p || []; this.updatePermissionsUI(p || []); })
                        .catch(e => console.error("Erreur init permissions", e));
                }
            }
        }

        document.querySelector(SELECTORS.createProfilForm)
            ?.addEventListener('submit', e => this.handleCreateUser(e));

        const modal = document.getElementById('hs-modal-add-user');
        if (modal) {
            new MutationObserver(() => {
                if (modal.classList.contains('hidden')) {
                    this.currentUserCreateInfo = {};
                    this.currentPermissions    = [];
                }
            }).observe(modal, { attributes: true, attributeFilter: ['class'] });
        }

        document.getElementById('editUserForm')
            ?.addEventListener('submit', e => this.handleEditUser(e));

        this._bindTableActions(document.getElementById('table-body'));
        this._initStaffSearch();
    }

    _initStaffSearch() {
        const searchInput  = document.getElementById('user-search-input');
        if (!searchInput) return;
        const roleSelect   = document.getElementById('hs-basic-usage-example');
        const statusSelect = document.getElementById('select-status');

        searchInput.addEventListener('input', () => {
            clearTimeout(this._staffSearchDebounce);
            this._staffSearchDebounce = setTimeout(() => this._fetchAndRenderStaffList(), 320);
        });
        roleSelect?.addEventListener('change',  () => this._fetchAndRenderStaffList());
        statusSelect?.addEventListener('change', () => this._fetchAndRenderStaffList());
    }

    async _fetchAndRenderStaffList() {
        const searchInput  = document.getElementById('user-search-input');
        const roleSelect   = document.getElementById('hs-basic-usage-example');
        const statusSelect = document.getElementById('select-status');
        try {
            const page = await this.retrieveUserUC.execute({
                email:        searchInput?.value  ?? '',
                roleFilter:   roleSelect?.value   ?? 'ALL',
                statusFilter: statusSelect?.value ?? 'ALL',
                page: 0, size: 50
            });
            this._renderStaffPage(page);
        } catch (e) { GlobalErrorHandler.handle(e); }
    }

    _renderStaffPage(page) {
        const tbody       = document.getElementById('table-body');
        const emptyRow    = document.getElementById('empty-state-row');
        const pagInfo     = document.getElementById('user-pagination-info');
        if (!tbody) return;

        const users = page?.content ?? [];
        tbody.querySelectorAll('tr:not(#empty-state-row)').forEach(tr => tr.remove());

        if (users.length === 0) {
            emptyRow?.classList.remove('hidden');
            if (pagInfo) pagInfo.textContent = '0 résultat';
            return;
        }
        emptyRow?.classList.add('hidden');

        const fragment = document.createDocumentFragment();
        for (const u of users) {
            const row = userRowTable(u.email, this._getRoleLabel(u.roleName), this._getStatusLabel(u.status),
                                     u.id, this._getRoleClass(u.roleName), this._getStatusClass(u.status));
            row.dataset.userId    = String(u.id);
            row.dataset.userEmail = u.email;
            row.dataset.userRole  = u.roleName;
            row.dataset.userStatus = u.status;
            row.querySelector('.btn-delete-user')?.addEventListener('click', e => this.handleDeleteUser(e));
            row.querySelector('.btn-edit-user')?.addEventListener('click',   e => this.handleOpenEditModal(e));
            fragment.appendChild(row);
        }
        tbody.appendChild(fragment);
        window.HSStaticMethods?.autoInit();

        const total = page?.totalElements ?? users.length;
        const num   = page?.number ?? 0;
        const size  = page?.size   ?? users.length;
        const from  = total === 0 ? 0 : num * size + 1;
        const to    = Math.min(num * size + users.length, total);
        if (pagInfo) pagInfo.textContent = total ? `${from} – ${to} sur ${total}` : '0 résultat';
    }

    _bindTableActions(container) {
        if (!container) return;
        container.querySelectorAll('.btn-delete-user').forEach(b => b.addEventListener('click', e => this.handleDeleteUser(e)));
        container.querySelectorAll('.btn-edit-user').forEach(b =>   b.addEventListener('click', e => this.handleOpenEditModal(e)));
    }

    _getRoleClass(r) {
        switch (r) {
            case 'TEACHER':    return 'bg-blue-500/10 text-blue-500';
            case 'ADMIN':
            case 'SURVEILLANT':return 'bg-indigo-500/10 text-indigo-500';
            case 'AP':         return 'bg-orange-500/10 text-orange-500';
            default:           return 'bg-muted text-muted-foreground-2';
        }
    }
    _getRoleLabel(r) {
        switch (r) {
            case 'TEACHER':    return 'ENSEIGNANT';
            case 'ADMIN':
            case 'SURVEILLANT':return 'SURVEILLANT';
            case 'AP':         return 'AP';
            default:           return 'AUTRE';
        }
    }
    _getStatusClass(s)  { return s === 'ACTIVE' ? 'bg-green-500/10 text-green-600' : 'bg-red-500/10 text-red-500'; }
    _getStatusLabel(s)  { return s === 'ACTIVE' ? 'Actif' : 'Bloqué'; }

    _syncEmptyState() {
        const tbody    = document.getElementById('table-body');
        const emptyRow = document.getElementById('empty-state-row');
        if (!tbody || !emptyRow) return;
        emptyRow.classList.toggle('hidden', tbody.querySelectorAll('tr:not(#empty-state-row)').length > 0);
    }

    switchToProfilEdition(event) {
        event.preventDefault();
        const formData = new FormData(event.target);
        const checked  = [...document.querySelectorAll('#permissionsContainer input[type="checkbox"]:checked')]
                            .map(cb => cb.value);

        const userData = {
            email:          formData.get('email'),
            idRole:         formData.get('role'),
            idPermissions:  checked.length > 0 ? checked : this.currentPermissions.map(p => p.id)
        };

        if (!userData.idPermissions?.length) { alert("Veuillez sélectionner au moins une permission."); return; }
        this.currentUserCreateInfo = userData;

        const t1 = document.querySelector(SELECTORS.createUserTabItemCompte);
        const t2 = document.querySelector(SELECTORS.createUserTabItemProfil);
        if (t1 && t2) { t2.removeAttribute('disabled'); t2.click(); }
    }

    async handleCreateUser(event) {
        event.preventDefault();
        const fd = new FormData(event.target);
        this.currentUserCreateInfo.profile = {
            nom:             fd.get('noms'),
            prenom:          fd.get('prenoms'),
            matricule:       fd.get('matricule'),
            numeroTelephone: fd.get('telephone'),
        };

        try {
            const newUser = await this.createUserUC.execute(this.currentUserCreateInfo);
            GlobalEventNotifier.eventWellDone("Utilisateur créé avec succès !");
            this._addUserRow(newUser);
        } catch (e) { GlobalErrorHandler.handle(e); }
        finally {
            HSOverlay.close('#hs-modal-add-user');
            document.querySelector(SELECTORS.createCompteForm)?.reset();
            document.querySelector(SELECTORS.createProfilForm)?.reset();
            const t1 = document.querySelector(SELECTORS.createUserTabItemCompte);
            const t2 = document.querySelector(SELECTORS.createUserTabItemProfil);
            if (t1 && t2) { t2.setAttribute('disabled', 'disabled'); t1.click(); }
        }
    }

    _addUserRow(user) {
        const tbody = document.getElementById('table-body');
        const row = userRowTable(user.email, this._getRoleLabel(user.roleName), this._getStatusLabel(user.status),
                                  user.id, this._getRoleClass(user.roleName), this._getStatusClass(user.status));
        row.dataset.userId    = user.id;
        row.dataset.userEmail = user.email;
        row.dataset.userRole  = user.roleName;
        row.dataset.userStatus = user.status;
        row.querySelector('.btn-delete-user')?.addEventListener('click', e => this.handleDeleteUser(e));
        row.querySelector('.btn-edit-user')?.addEventListener('click',   e => this.handleOpenEditModal(e));
        tbody.appendChild(row);
        window.HSStaticMethods?.autoInit();
        this._syncEmptyState();
    }

    _removeUserRow(id) {
        const tbody = document.getElementById("table-body");
        [...tbody.children].find(tr => tr.dataset.userId === `${id}`)?.remove();
        this._syncEmptyState();
    }

    async handleDeleteUser(event) {
        const ok = await customAlert("Supprimer un utilisateur",
            "Êtes-vous sûr de vouloir supprimer cet utilisateur ? Cette action n'est pas réversible.",
            "Oui", "Abandonner");
        if (!ok) return;

        const userId = event.target.dataset.userId;
        if (!userId) { alert("Erreur: ID utilisateur introuvable."); return; }

        try {
            await this.deleteUserUC.execute(userId);
            GlobalEventNotifier.eventWellDone("Utilisateur supprimé avec succès !");
            this._removeUserRow(userId);
        } catch (e) { GlobalErrorHandler.handle(e); }
    }

    handleOpenEditModal(event) {
        const button = event.currentTarget;
        const row    = button.closest('tr');
        const userId = row?.dataset.userId   || button.dataset.userId;
        const email  = row?.dataset.userEmail || button.dataset.userEmail || '';
        const role   = row?.dataset.userRole  || button.dataset.userRole  || '';
        const status = row?.dataset.userStatus || button.dataset.userStatus || '';

        document.getElementById('edit-user-id').value    = userId;
        document.getElementById('edit-user-email').value = email;
        const roleSelect   = document.getElementById('edit-user-role');
        const statusSelect = document.getElementById('edit-user-status');
        if (roleSelect)   roleSelect.value   = role;
        if (statusSelect) statusSelect.value = status;
        window.HSStaticMethods?.autoInit();
        HSOverlay.open('#hs-modal-edit-user');
    }

    async handleEditUser(event) {
        event.preventDefault();
        const fd     = new FormData(event.target);
        const userId = fd.get('id');

        try {
            const updated = await this.userApi.updateUser(userId, {
                email:  fd.get('email'),
                role:   fd.get('role'),
                status: fd.get('status'),
            });
            HSOverlay.close('#hs-modal-edit-user');
            GlobalEventNotifier.eventWellDone("Utilisateur modifié avec succès !");
            this._updateUserRow(updated ?? { id: userId, email: fd.get('email'), roleName: fd.get('role'), status: fd.get('status') });
        } catch (e) { GlobalErrorHandler.handle(e); }
    }

    _updateUserRow(user) {
        const tbody  = document.getElementById('table-body');
        const oldRow = [...tbody.children].find(tr => tr.dataset.userId === `${user.id}`);
        if (!oldRow) return;
        const newRow = userRowTable(user.email, this._getRoleLabel(user.roleName), this._getStatusLabel(user.status),
                                     user.id, this._getRoleClass(user.roleName), this._getStatusClass(user.status));
        newRow.dataset.userId    = user.id;
        newRow.dataset.userEmail = user.email;
        newRow.dataset.userRole  = user.roleName;
        newRow.dataset.userStatus = user.status;
        newRow.querySelector('.btn-delete-user')?.addEventListener('click', e => this.handleDeleteUser(e));
        newRow.querySelector('.btn-edit-user')?.addEventListener('click',   e => this.handleOpenEditModal(e));
        tbody.replaceChild(newRow, oldRow);
        window.HSStaticMethods?.autoInit();
    }

    async handleRoleChange(event) {
        const roleName = event.target.value;
        if (!roleName) return;
        try {
            const permissions = await this.userApi.getPermissionsByRole(roleName);
            this.currentPermissions = permissions || [];
            this.updatePermissionsUI(this.currentPermissions);
        } catch (e) { console.error("Erreur récupération permissions", e); }
    }

    _createPermissionsBadge(permissionName) {
        const span = document.createElement('span');
        span.id        = `badge-${permissionName}`;
        span.className = 'inline-flex items-center gap-x-1.5 py-1.5 ps-3 pe-2 me-3 mb-2 rounded-full text-xs font-medium lowercase bg-primary-50 text-primary-800 dark:bg-primary-400/20 dark:text-primary-400 border border-primary-100 dark:border-primary-500/20';
        span.innerHTML = `
            ${permissionName}
            <button type="button" class="remove-btn shrink-0 size-4 inline-flex items-center justify-center rounded-full hover:bg-primary-200 focus:outline-hidden">
                <span class="sr-only">Remove badge</span>
                <svg class="shrink-0 size-3" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M18 6 6 18"/><path d="m6 6 12 12"/>
                </svg>
            </button>`;
        return span;
    }

    updatePermissionsUI(permissions) {
        const container = document.getElementById('permissionsContainer');
        const view      = document.getElementById('permissions-view');
        if (!container) return;

        container.innerHTML = '';
        if (view) view.innerHTML = '';

        if (!permissions?.length) {
            container.innerHTML = '<div class="py-2 px-3 text-sm text-muted-foreground-2">Aucune permission pour ce rôle.</div>';
            return;
        }

        permissions.forEach(p => {
            const div = document.createElement('div');
            div.className = 'flex items-start gap-3 py-2 px-3 rounded-lg hover:bg-dropdown-item-hover cursor-pointer transition-colors';
            div.innerHTML = `
               <div class="relative shrink-0 mt-0.5">
                   <input id="hs-dropdown-item-checkbox-${p.id}" name="hs-dropdown-item-checkbox-${p.id}"
                          type="checkbox" value="${p.id}" class="peer sr-only"
                          aria-describedby="hs-dropdown-item-checkbox-${p.id}-description" checked>
                   <div class="size-5 rounded-md border-2 border-layer-line bg-layer peer-checked:bg-primary peer-checked:border-primary flex items-center justify-center transition-all">
                       <svg class="hidden peer-checked:block size-3 text-white" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3.5">
                           <polyline points="20 6 9 17 4 12"/>
                       </svg>
                   </div>
               </div>
               <label for="hs-dropdown-item-checkbox-${p.id}" class="cursor-pointer">
                   <span class="block text-sm font-semibold text-primary">${p.id}</span>
                   <span id="hs-dropdown-item-checkbox-${p.id}-description" class="block text-xs text-muted-foreground-2 mt-0.5">${p.label}</span>
               </label>`;

            const badge = this._createPermissionsBadge(p.id);
            badge.querySelector('.remove-btn').addEventListener('click', () => {
                if (view.children.length > 1) { badge.remove(); div.querySelector('input').checked = false; }
            });
            div.querySelector('input').addEventListener('change', e => {
                if (e.target.checked) { view.appendChild(badge); }
                else { if (view.children.length > 1) badge.remove(); else e.target.checked = true; }
            });

            view?.appendChild(badge);
            container.appendChild(div);
        });
    }
}

const _userApi = new UserApi();
new UserController(
    _userApi,
    new CreateUserUC(_userApi),
    new DeleteUserUC(_userApi),
    new ModifyUserStatusUC(_userApi),
    new RetrieveUserUC(_userApi)
);

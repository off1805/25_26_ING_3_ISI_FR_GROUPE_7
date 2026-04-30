/**
 * KemOSchool UserController - Mode Backend
 * 
 * Intégration du DataTable avec appels API backend
 * Chaque changement (recherche, filtre, tri, pagination) = appel API
 */

class UserManagementController {
  constructor() {
    this.dataTable = null;
    this.init();
  }

  init() {
    this.initializeDataTable();
    this.attachModalEvents();
  }

  /**
   * Initialiser le DataTable en mode backend
   */
  initializeDataTable() {
    this.dataTable = new DataTable({
      containerId: 'data-table-users',
      title: 'Gestion des utilisateurs',
      description: 'Créez, modifiez ou suivez les utilisateurs. Attribuez facilement les rôles et permissions.',
      
      // ══════════════════════════════════════
      // MODE BACKEND ACTIVÉ
      // ══════════════════════════════════════
      isRemoteData: true,
      loadingIndicator: true,
      
      data: [],  // Sera rempli par les callbacks
      searchableFields: ['email'],
      itemsPerPage: 50,

      // ══════════════════════════════════════
      // COLONNES
      // ══════════════════════════════════════
      columns: [
        {
          key: 'email',
          label: 'Utilisateur',
          type: 'avatar',
          sortable: true,
          getter: (item) => item.email,
        },
        {
          key: 'roleName',
          label: 'Rôle',
          type: 'badge',
          sortable: true,
          filterable: true,
          filterType: 'select',
          filterOptions: [
            { value: 'TEACHER', label: 'Enseignant' },
            { value: 'SURVEILLANT', label: 'Surveillant' },
            { value: 'AP', label: 'Assistant pédagogique' },
          ],
          format: (value) => {
            const roleMap = {
              'TEACHER': 'ENSEIGNANT',
              'SURVEILLANT': 'SURVEILLANT',
              'AP': 'AP',
            };
            return roleMap[value] || value;
          },
        },
        {
          key: 'status',
          label: 'Statut',
          type: 'status',
          sortable: true,
          filterable: true,
          filterType: 'select',
          filterOptions: [
            { value: 'ACTIVE', label: 'Actif' },
            { value: 'BLOCKED', label: 'Bloqué' },
          ],
          getter: (item) => {
            if (typeof item.status === 'string') return item.status;
            if (item.status?.name) return item.status.name();
            return 'UNKNOWN';
          },
          hiddenOn: 'hidden xl:table-cell',
        },
        {
          key: 'id',
          label: 'User ID',
          format: (value) => {
            return `<span class="inline-flex items-center px-2.5 py-1 rounded-lg text-xs font-bold bg-gray-100 text-gray-500">${value}</span>`;
          },
          hiddenOn: 'hidden xl:table-cell',
        },
      ],

      // ══════════════════════════════════════
      // ACTIONS
      // ══════════════════════════════════════
      actions: [
        {
          type: 'edit',
          label: 'Modifier',
          icon: 'M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7 M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z',
          className: 'text-layer-foreground hover:bg-muted',
        },
        {
          type: 'delete',
          label: 'Supprimer',
          icon: 'M3 6h18 M8 6v12 M16 6v12 M6 6l1.5 12 M18 6l-1.5 12',
          className: 'text-red-500 hover:bg-red-50',
        },
      ],

      onAction: (actionType, user) => {
        this.handleUserAction(actionType, user);
      },

      // ══════════════════════════════════════
      // CALLBACKS BACKEND
      // ══════════════════════════════════════

      /**
       * Recherche globale
       * Appelé quand l'utilisateur tape dans la barre de recherche
       */
      onSearch: async (query, filters, sort, page, itemsPerPage) => {
        try {
          console.log('🔍 Recherche:', { query, filters, sort, page, itemsPerPage });

          const response = await fetch('/api/users/search', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'X-CSRF-Token': this.getCsrfToken(),  // Pour Thymeleaf/Spring Security
            },
            body: JSON.stringify({
              query,
              filters,
              sort,
              page,
              limit: itemsPerPage,
            }),
          });

          if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
          }

          const result = await response.json();
          console.log('📊 Résultats:', result.totalItems, 'items');

          return result;
        } catch (error) {
          console.error('❌ Erreur recherche:', error);
          throw error;
        }
      },

      /**
       * Filtre
       * Appelé quand un filtre (role, statut) change
       */
      onFilter: async (filters, search, sort, page, itemsPerPage) => {
        try {
          console.log('🔽 Filtre:', filters);

          const response = await fetch('/api/users/filter', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'X-CSRF-Token': this.getCsrfToken(),
            },
            body: JSON.stringify({
              filters,
              search,
              sort,
              page,
              limit: itemsPerPage,
            }),
          });

          if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
          }

          return await response.json();
        } catch (error) {
          console.error('❌ Erreur filtre:', error);
          throw error;
        }
      },

      /**
       * Tri
       * Appelé quand l'utilisateur clique sur un en-tête de colonne
       */
      onSort: async (sort, search, filters, page, itemsPerPage) => {
        try {
          console.log('↕️ Tri:', sort.key, sort.direction);

          const response = await fetch('/api/users/sort', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'X-CSRF-Token': this.getCsrfToken(),
            },
            body: JSON.stringify({
              sort: { key: sort.key, direction: sort.direction },
              search,
              filters,
              page,
              limit: itemsPerPage,
            }),
          });

          if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
          }

          return await response.json();
        } catch (error) {
          console.error('❌ Erreur tri:', error);
          throw error;
        }
      },

      /**
       * Changement de page
       * Appelé quand l'utilisateur clique sur précédent/suivant
       */
      onPageChange: async (page, itemsPerPage, search, filters, sort) => {
        try {
          console.log('📄 Page:', page);

          const response = await fetch('/api/users/page', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'X-CSRF-Token': this.getCsrfToken(),
            },
            body: JSON.stringify({
              page,
              limit: itemsPerPage,
              search,
              filters,
              sort,
            }),
          });

          if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
          }

          return await response.json();
        } catch (error) {
          console.error('❌ Erreur changement page:', error);
          throw error;
        }
      },

      /**
       * Changement du nombre d'items par page
       * Appelé quand l'utilisateur change "Lignes par page"
       */
      onItemsPerPageChange: async (itemsPerPage, page, search, filters, sort) => {
        try {
          console.log('📦 Items par page:', itemsPerPage);

          const response = await fetch('/api/users/items-per-page', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'X-CSRF-Token': this.getCsrfToken(),
            },
            body: JSON.stringify({
              limit: itemsPerPage,
              page,
              search,
              filters,
              sort,
            }),
          });

          if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
          }

          return await response.json();
        } catch (error) {
          console.error('❌ Erreur changement limite:', error);
          throw error;
        }
      },
    });

    // Charger les données initiales
    this.loadInitialData();
  }

  /**
   * Charger les données initiales
   */
  async loadInitialData() {
    try {
      const result = await this.dataTable.config.onSearch(
        '', // query
        {}, // filters
        null, // sort
        1, // page
        this.dataTable.config.itemsPerPage
      );

      this.dataTable.config.data = result.data || [];
      this.dataTable.state.totalItems = result.totalItems || 0;
      this.dataTable.render();
    } catch (error) {
      console.error('Erreur lors du chargement initial:', error);
      this.showToast('Erreur lors du chargement des données', 'error');
    }
  }

  /**
   * Gestion des actions utilisateur
   */
  handleUserAction(actionType, user) {
    switch (actionType) {
      case 'edit':
        this.openEditModal(user);
        break;
      case 'delete':
        this.deleteUser(user);
        break;
      default:
        console.warn('Action inconnue:', actionType);
    }
  }

  /**
   * Ouvrir la modale d'édition
   */
  openEditModal(user) {
    const modal = document.getElementById('hs-modal-edit-user');
    if (!modal) {
      console.error('Modal #hs-modal-edit-user non trouvée');
      return;
    }

    // Remplir le formulaire
    document.getElementById('edit-user-id').value = user.id;
    document.getElementById('edit-user-email').value = user.email;
    document.getElementById('edit-user-role').value = user.roleName;

    let status = user.status;
    if (typeof status === 'object' && status.name) {
      status = status.name();
    }
    document.getElementById('edit-user-status').value = status;

    // Ouvrir la modale
    const hsOverlay = HSOverlay.getInstance(modal);
    if (hsOverlay) {
      hsOverlay.open();
    } else {
      modal.classList.remove('hidden');
    }
  }

  /**
   * Supprimer un utilisateur
   */
  async deleteUser(user) {
    if (!confirm(`Êtes-vous sûr de vouloir supprimer ${user.email} ?`)) {
      return;
    }

    try {
      const response = await fetch(`/api/users/${user.id}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          'X-CSRF-Token': this.getCsrfToken(),
        },
      });

      if (!response.ok) {
        throw new Error('Erreur lors de la suppression');
      }

      this.showToast('Utilisateur supprimé avec succès', 'success');

      // Rafraîchir le tableau (peut réinitialiser la page si nécessaire)
      await this.dataTable.config.onSearch(
        this.dataTable.state.searchQuery,
        this.dataTable.state.filters,
        this.dataTable.state.sortConfig,
        this.dataTable.state.currentPage,
        this.dataTable.state.itemsPerPage
      ).then(result => {
        this.dataTable.config.data = result.data;
        this.dataTable.state.totalItems = result.totalItems;
        this.dataTable.render();
      });
    } catch (error) {
      console.error('Erreur lors de la suppression:', error);
      this.showToast('Erreur lors de la suppression', 'error');
    }
  }

  /**
   * Attacher les événements des modales
   */
  attachModalEvents() {
    const editForm = document.getElementById('editUserForm');
    const addForm1 = document.getElementById('addUserForm1');
    const addForm2 = document.getElementById('addUserForm2');

    if (editForm) {
      editForm.addEventListener('submit', (e) => {
        e.preventDefault();
        this.submitEditForm(editForm);
      });
    }

    if (addForm2) {
      addForm2.addEventListener('submit', (e) => {
        e.preventDefault();
        this.submitAddForm(addForm2);
      });
    }
  }

  /**
   * Soumettre le formulaire d'édition
   */
  async submitEditForm(form) {
    try {
      const formData = new FormData(form);
      const userId = formData.get('id');

      const response = await fetch(`/api/users/${userId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'X-CSRF-Token': this.getCsrfToken(),
        },
        body: JSON.stringify({
          email: formData.get('email'),
          role: formData.get('role'),
          status: formData.get('status'),
        }),
      });

      if (!response.ok) {
        throw new Error('Erreur lors de la modification');
      }

      this.showToast('Utilisateur modifié avec succès', 'success');

      // Fermer la modale
      const modal = document.getElementById('hs-modal-edit-user');
      const hsOverlay = HSOverlay.getInstance(modal);
      if (hsOverlay) hsOverlay.close();

      // Rafraîchir le tableau
      await this.dataTable.config.onSearch(
        this.dataTable.state.searchQuery,
        this.dataTable.state.filters,
        this.dataTable.state.sortConfig,
        this.dataTable.state.currentPage,
        this.dataTable.state.itemsPerPage
      ).then(result => {
        this.dataTable.config.data = result.data;
        this.dataTable.state.totalItems = result.totalItems;
        this.dataTable.render();
      });
    } catch (error) {
      console.error('Erreur:', error);
      this.showToast('Erreur lors de la modification', 'error');
    }
  }

  /**
   * Soumettre le formulaire d'ajout
   */
  async submitAddForm(form) {
    try {
      const formData = new FormData(form);

      const response = await fetch('/api/users', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'X-CSRF-Token': this.getCsrfToken(),
        },
        body: JSON.stringify({
          email: formData.get('email'),
          role: formData.get('role'),
          noms: formData.get('noms'),
          prenoms: formData.get('prenoms'),
          matricule: formData.get('matricule'),
          telephone: formData.get('telephone'),
        }),
      });

      if (!response.ok) {
        throw new Error('Erreur lors de la création');
      }

      this.showToast('Utilisateur créé avec succès', 'success');

      // Fermer la modale
      form.reset();
      const modal = document.getElementById('hs-modal-add-user');
      const hsOverlay = HSOverlay.getInstance(modal);
      if (hsOverlay) hsOverlay.close();

      // Rafraîchir le tableau
      this.loadInitialData();
    } catch (error) {
      console.error('Erreur:', error);
      this.showToast('Erreur lors de la création', 'error');
    }
  }

  /**
   * Afficher une notification
   */
  showToast(message, type = 'info') {
    if (typeof Toastify !== 'undefined') {
      Toastify({
        text: message,
        duration: 3000,
        gravity: 'top',
        position: 'right',
        backgroundColor: type === 'success' ? '#22c55e' : '#ef4444',
      }).showToast();
    } else {
      console.log(`[${type.toUpperCase()}] ${message}`);
    }
  }

  /**
   * Récupérer le token CSRF (pour Spring Security)
   */
  getCsrfToken() {
    // Option 1: Depuis un meta tag
    const meta = document.querySelector('meta[name="_csrf"]');
    if (meta) return meta.getAttribute('content');

    // Option 2: Depuis un input hidden
    const input = document.querySelector('input[name="_csrf"]');
    if (input) return input.value;

    return '';
  }
}

// Initialiser quand le DOM est prêt
document.addEventListener('DOMContentLoaded', () => {
  new UserManagementController();
});

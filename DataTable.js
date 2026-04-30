/**
 * DataTable - Composant réutilisable en JavaScript vanilla + Preline
 * 
 * Hautement configurable pour:
 * - Colonnes personnalisées (types, formatage, tri, filtres)
 * - Filtres multiples (select, search) avec callbacks backend
 * - Actions (edit, delete, custom)
 * - Pagination et gestion des items par page
 * - Recherche globale avec callback backend
 * - Tri avec callback backend
 * 
 * @param {Object} config - Configuration du tableau
 */
class DataTable {
  constructor(config) {
    this.config = {
      containerId: 'data-table-container',
      title: 'Tableau',
      description: '',
      data: [],
      columns: [],
      actions: [],
      searchableFields: [],
      itemsPerPage: 50,
      
      // Callbacks pour les opérations backend
      onSearch: null,           // (query, filters, sort, page) => Promise<data>
      onFilter: null,           // (filters, search, sort, page) => Promise<data>
      onSort: null,             // (sortConfig, search, filters, page) => Promise<data>
      onPageChange: null,       // (page, itemsPerPage, search, filters, sort) => Promise<data>
      onItemsPerPageChange: null, // (itemsPerPage, page, search, filters, sort) => Promise<data>
      onAction: null,           // (actionType, item) => void
      
      // Options
      isRemoteData: false,      // true = appels backend, false = filtrage local
      loadingIndicator: true,   // Afficher un indicateur de chargement
      
      ...config,
    };

    this.state = {
      currentPage: 1,
      itemsPerPage: this.config.itemsPerPage,
      sortConfig: null,
      searchQuery: '',
      filters: {},
      isLoading: false,
      totalItems: 0,
    };

    this.init();
  }

  /**
   * Initialisation du tableau
   */
  init() {
    this.container = document.getElementById(this.config.containerId);
    if (!this.container) {
      console.error(`Container #${this.config.containerId} not found`);
      return;
    }

    this.render();
    this.attachEventListeners();
  }

  /**
   * Rendu principal du tableau
   */
  render() {
    const { data, columns, actions, searchableFields, title, description, isRemoteData, loadingIndicator } = this.config;
    const { currentPage, itemsPerPage, sortConfig, searchQuery, filters, isLoading, totalItems } = this.state;

    // Déterminer les données à afficher
    let displayData = data;
    let totalPages = 0;
    let startIndex = 0;
    let endIndex = 0;

    if (isRemoteData) {
      // Données du backend - utiliser totalItems fourni par le backend
      totalPages = Math.ceil(totalItems / itemsPerPage);
      startIndex = (currentPage - 1) * itemsPerPage + 1;
      endIndex = Math.min(currentPage * itemsPerPage, totalItems);
    } else {
      // Filtrage local
      let filteredData = this.applyFilters(data);
      filteredData = this.applySearch(filteredData, searchQuery);
      let sortedData = this.applySorting(filteredData, sortConfig);

      totalPages = Math.ceil(sortedData.length / itemsPerPage);
      startIndex = (currentPage - 1) * itemsPerPage + 1;
      endIndex = Math.min(currentPage * itemsPerPage, sortedData.length);
      displayData = sortedData.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);
      this.state.totalItems = sortedData.length;
    }

    const isEmpty = displayData.length === 0 && !isLoading;

    // HTML principal
    let html = `
      <div class="w-full">
        <!-- Header -->
        <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
          <div>
            <h3 class="text-lg font-semibold text-layer-foreground">${title}</h3>
            ${description ? `<p class="text-sm text-muted-foreground-2 mt-1">${description}</p>` : ''}
          </div>
          ${searchableFields.length > 0 && !isLoading ? this.renderSearchInput(searchQuery) : ''}
        </div>

        <!-- Card Container -->
        <div class="bg-card rounded-2xl border border-card-line overflow-hidden">
          ${isLoading ? this.renderLoadingState(loadingIndicator) : (isEmpty ? this.renderEmptyState(searchQuery, filters) : this.renderTable(columns, displayData, actions, sortConfig))}
          
          ${!isEmpty && !isLoading ? this.renderPagination(totalItems || displayData.length, currentPage, totalPages, itemsPerPage) : ''}
        </div>
      </div>
    `;

    this.container.innerHTML = html;

    // Réinitialiser Preline après le rendu
    if (typeof HSStaticMethods !== 'undefined') {
      HSStaticMethods.autoInit();
    }
  }

  /**
   * Rendu de l'état de chargement
   */
  renderLoadingState(showIndicator) {
    if (!showIndicator) {
      return '<div class="px-6 py-16 text-center"><p class="text-muted-foreground-2">Chargement...</p></div>';
    }

    return `
      <div class="px-6 py-16 text-center">
        <div class="flex flex-col items-center gap-4">
          <div class="animate-spin">
            <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="text-primary">
              <path d="M21.5 2v6h-6M2.5 22v-6h6M2 11.5a10 10 0 0 1 18.8-4.3M22 12.5a10 10 0 0 1-18.8 4.2" />
            </svg>
          </div>
          <p class="text-sm font-semibold text-layer-foreground">Chargement des données...</p>
        </div>
      </div>
    `;
  }

  /**
   * Rendu du champ de recherche
   */
  renderSearchInput(searchQuery) {
    return `
      <div class="relative sm:w-64">
        <svg class="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-muted-foreground-2"
          xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"
          fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="11" cy="11" r="8" />
          <path d="m21 21-4.34-4.34" />
        </svg>
        <input type="text" id="dt-search-input" placeholder="Rechercher…" value="${searchQuery}"
          class="pl-9 pr-4 py-2 w-full text-sm bg-layer border border-layer-line rounded-xl text-layer-foreground placeholder:text-muted-foreground-2 focus:outline-none focus:ring-1 focus:ring-primary focus:border-primary transition-colors" />
      </div>
    `;
  }

  /**
   * Rendu du tableau
   */
  renderTable(columns, data, actions, sortConfig) {
    const headerHTML = columns
      .map((col) => this.renderTableHeader(col, sortConfig))
      .join('');

    const rowsHTML = data
      .map((item, idx) => this.renderTableRow(item, columns, actions, idx))
      .join('');

    return `
      <div class="overflow-x-auto">
        <table class="w-full text-left">
          <thead>
            <tr class="border-y border-card-line bg-muted/30">
              ${headerHTML}
              ${actions.length > 0 ? '<th class="px-3 sm:px-6 py-3 text-xs font-medium text-muted-foreground-2/60 uppercase tracking-widest text-right">Actions</th>' : ''}
            </tr>
          </thead>
          <tbody class="divide-y divide-card-line">
            ${rowsHTML}
          </tbody>
        </table>
      </div>
    `;
  }

  /**
   * Rendu d'une cellule d'en-tête
   */
  renderTableHeader(column, sortConfig) {
    let filterHTML = '';

    // Afficher le filtre si applicable
    if (column.filterable && column.filterType === 'select') {
      const filterOptions = column.filterOptions || [];
      const selectedValue = this.state.filters[column.key] || 'ALL';

      filterHTML = `
        <div class="mt-2 pt-2 border-t border-card-line">
          <select class="dt-filter-select text-xs font-medium uppercase tracking-widest text-layer-foreground bg-transparent border-none focus:outline-none cursor-pointer hover:text-primary-hover transition-colors" 
            data-filter-key="${column.key}">
            <option value="ALL">Tous</option>
            ${filterOptions.map((opt) => `<option value="${opt.value}" ${selectedValue === opt.value ? 'selected' : ''}>${opt.label}</option>`).join('')}
          </select>
        </div>
      `;
    }

    const isSortable = column.sortable;
    const isSorted = sortConfig?.key === column.key;
    const sortIcon = isSorted ? (sortConfig.direction === 'asc' ? '↑' : '↓') : '⇅';

    const clickHandler = isSortable ? 'class="cursor-pointer hover:text-primary" data-sortable="true"' : '';

    return `
      <th class="px-3 sm:px-6 py-3 text-xs font-medium text-muted-foreground-2/60 uppercase tracking-widest ${column.hidden ? 'hidden' : ''} ${column.hiddenOn || ''}" data-column-key="${column.key}" ${clickHandler} data-column-sortable="${isSortable}">
        <div class="flex items-center gap-2">
          <span>${column.label}</span>
          ${isSortable ? `<span class="text-muted-foreground-2/40">${sortIcon}</span>` : ''}
        </div>
        ${filterHTML}
      </th>
    `;
  }

  /**
   * Rendu d'une ligne du tableau
   */
  renderTableRow(item, columns, actions, idx) {
    const cellsHTML = columns
      .map((col) => this.renderTableCell(item, col))
      .join('');

    const actionsHTML = actions.length > 0 ? `<td class="px-3 sm:px-6 py-4 text-right whitespace-nowrap shrink-0">${this.renderActionsDropdown(item, actions)}</td>` : '';

    return `
      <tr class="hover:bg-muted/20 transition-colors" data-item-id="${item.id || idx}">
        ${cellsHTML}
        ${actionsHTML}
      </tr>
    `;
  }

  /**
   * Rendu d'une cellule de données
   */
  renderTableCell(item, column) {
    const { key, type, format, getter, hidden, hiddenOn } = column;
    let value = getter ? getter(item) : item[key];

    let cellContent = '';

    // Formatage personnalisé
    if (format) {
      cellContent = format(value, item);
    } else {
      // Formatage par type
      cellContent = this.formatCellByType(value, type, item);
    }

    return `
      <td class="px-3 sm:px-6 py-4 ${hidden ? 'hidden' : ''} ${hiddenOn || ''} ${type === 'avatar' ? 'min-w-0' : ''}">
        ${cellContent}
      </td>
    `;
  }

  /**
   * Formatage automatique par type
   */
  formatCellByType(value, type, item) {
    switch (type) {
      case 'avatar': {
        const initial = String(value).charAt(0).toUpperCase();
        return `
          <div class="flex items-center gap-3">
            <div class="size-9 rounded-xl bg-primary/10 flex items-center justify-center shrink-0 text-sm font-bold text-primary">
              ${initial}
            </div>
            <span class="text-sm font-semibold text-layer-foreground truncate max-w-[160px]">${value}</span>
          </div>
        `;
      }

      case 'status': {
        const isActive = value === 'ACTIVE' || value?.name?.() === 'ACTIVE';
        return `
          <div class="flex items-center gap-1.5">
            <span class="size-1.5 rounded-full shrink-0 ${isActive ? 'bg-green-500' : 'bg-red-500'}"></span>
            <span class="text-xs font-semibold ${isActive ? 'text-green-600' : 'text-red-500'}">
              ${isActive ? 'Actif' : 'Bloqué'}
            </span>
          </div>
        `;
      }

      case 'badge': {
        const badgeColors = {
          'TEACHER': 'bg-blue-500/10 text-blue-500',
          'ENSEIGNANT': 'bg-blue-500/10 text-blue-500',
          'SURVEILLANT': 'bg-indigo-500/10 text-indigo-500',
          'AP': 'bg-orange-500/10 text-orange-500',
        };
        const colorClass = badgeColors[value] || 'bg-muted text-muted-foreground-2';
        return `<span class="inline-flex items-center px-2.5 py-1 rounded-lg text-xs font-bold ${colorClass}">${value}</span>`;
      }

      case 'date': {
        const date = new Date(value);
        return date.toLocaleDateString('fr-FR');
      }

      case 'phone': {
        const phone = String(value).replace(/(\d{3})(\d{3})(\d{3})/, '$1 $2 $3');
        return phone;
      }

      case 'boolean': {
        return value ? '✓' : '-';
      }

      default:
        return value || '-';
    }
  }

  /**
   * Rendu du menu actions (dropdown)
   */
  renderActionsDropdown(item, actions) {
    const itemId = item.id || Math.random();
    const dropdownId = `dropdown-${itemId}`;

    const actionsHTML = actions
      .map((action) => {
        const iconClass = action.iconClass || 'size-4';
        const btnClass = action.className || 'text-layer-foreground hover:bg-muted';
        return `
          <button type="button" class="dt-action-btn flex items-center gap-x-2.5 w-full py-2 px-3 text-sm ${btnClass} rounded-lg transition-colors" 
            data-action-type="${action.type}" data-item-id="${itemId}">
            ${action.icon ? `<svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${iconClass}"><path d="${action.icon}"/></svg>` : ''}
            ${action.label}
          </button>
        `;
      })
      .join('');

    return `
      <div class="hs-dropdown relative inline-flex">
        <button type="button" class="hs-dropdown-toggle size-8 flex items-center justify-center rounded-lg text-muted-foreground-2 hover:bg-muted hover:text-layer-foreground transition-colors focus:outline-none"
          aria-haspopup="menu" aria-expanded="false">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="5" r="1" />
            <circle cx="12" cy="12" r="1" />
            <circle cx="12" cy="19" r="1" />
          </svg>
        </button>
        <div class="hs-dropdown-menu transition-[opacity,margin] duration-200 hs-dropdown-open:opacity-100 opacity-0 hidden min-w-36 bg-layer border border-card-line shadow-lg rounded-xl z-20 p-1"
          role="menu">
          ${actionsHTML}
        </div>
      </div>
    `;
  }

  /**
   * Rendu de l'état vide
   */
  renderEmptyState(searchQuery, filters) {
    const hasFilters = Object.values(filters).some((f) => f && f !== 'ALL');
    const message = searchQuery || hasFilters ? 'Aucun résultat trouvé' : 'Aucune donnée disponible';
    const subtitle = searchQuery || hasFilters ? "Essayez d'ajuster vos filtres ou votre recherche" : 'Ajoutez des données pour commencer';

    return `
      <div class="px-6 py-16 text-center">
        <div class="flex flex-col items-center gap-3">
          <div class="size-14 rounded-2xl bg-muted flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.25" class="text-muted-foreground-2">
              <path d="M5 7a4 4 0 1 0 8 0a4 4 0 1 0-8 0" />
              <path d="M3 21v-2a4 4 0 0 1 4-4h4a4 4 0 0 1 4 4v2" />
              <path d="M16 3.13a4 4 0 0 1 0 7.75" />
              <path d="M21 21v-2a4 4 0 0 0-3-3.85" />
            </svg>
          </div>
          <p class="text-sm font-semibold text-layer-foreground">${message}</p>
          <p class="text-xs text-muted-foreground-2">${subtitle}</p>
        </div>
      </div>
    `;
  }

  /**
   * Rendu de la pagination
   */
  renderPagination(totalItems, currentPage, totalPages, itemsPerPage) {
    const startIndex = (currentPage - 1) * itemsPerPage + 1;
    const endIndex = Math.min(currentPage * itemsPerPage, totalItems);

    return `
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 px-6 py-4 border-t border-card-line">
        <!-- Items per page -->
        <div class="flex items-center gap-2">
          <span class="text-xs font-semibold text-muted-foreground-2">Lignes par page :</span>
          <select id="dt-items-per-page" class="py-1.5 px-2.5 text-sm bg-layer border border-layer-line rounded-lg text-layer-foreground focus:outline-none focus:ring-1 focus:ring-primary">
            <option value="10" ${itemsPerPage === 10 ? 'selected' : ''}>10</option>
            <option value="25" ${itemsPerPage === 25 ? 'selected' : ''}>25</option>
            <option value="50" ${itemsPerPage === 50 ? 'selected' : ''}>50</option>
            <option value="100" ${itemsPerPage === 100 ? 'selected' : ''}>100</option>
          </select>
        </div>

        <!-- Info & Navigation -->
        <div class="flex items-center gap-3">
          <span class="text-xs text-muted-foreground-2 font-semibold">${startIndex}-${endIndex} sur ${totalItems}</span>
          <div class="flex items-center gap-1">
            <button type="button" id="dt-prev-page" class="size-8 flex items-center justify-center rounded-lg bg-layer border border-layer-line text-muted-foreground-2 hover:bg-muted transition-colors disabled:opacity-40 disabled:pointer-events-none" ${currentPage === 1 ? 'disabled' : ''}>
              <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="m15 18-6-6 6-6" />
              </svg>
            </button>

            <span class="text-xs font-semibold text-layer-foreground px-2">${currentPage} / ${totalPages}</span>

            <button type="button" id="dt-next-page" class="size-8 flex items-center justify-center rounded-lg bg-layer border border-layer-line text-muted-foreground-2 hover:bg-muted transition-colors disabled:opacity-40 disabled:pointer-events-none" ${currentPage === totalPages ? 'disabled' : ''}>
              <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="m9 18 6-6-6-6" />
              </svg>
            </button>
          </div>
        </div>
      </div>
    `;
  }

  /**
   * Attachement des écouteurs d'événements
   */
  attachEventListeners() {
    // Recherche
    const searchInput = this.container.querySelector('#dt-search-input');
    if (searchInput) {
      searchInput.addEventListener('input', async (e) => {
        this.state.searchQuery = e.target.value;
        this.state.currentPage = 1;
        
        if (this.config.isRemoteData && this.config.onSearch) {
          await this.executeSearch();
        } else {
          this.render();
        }
      });
    }

    // Filtres
    this.container.querySelectorAll('.dt-filter-select').forEach((select) => {
      select.addEventListener('change', async (e) => {
        const key = e.target.dataset.filterKey;
        const value = e.target.value;
        this.state.filters[key] = value;
        this.state.currentPage = 1;
        
        if (this.config.isRemoteData && this.config.onFilter) {
          await this.executeFilter();
        } else {
          this.render();
        }
      });
    });

    // Tri
    this.container.querySelectorAll('th[data-sortable="true"]').forEach((th) => {
      th.addEventListener('click', async () => {
        const columnKey = th.dataset.columnKey;
        if (this.state.sortConfig?.key === columnKey) {
          this.state.sortConfig.direction = this.state.sortConfig.direction === 'asc' ? 'desc' : 'asc';
        } else {
          this.state.sortConfig = { key: columnKey, direction: 'asc' };
        }
        this.state.currentPage = 1;
        
        if (this.config.isRemoteData && this.config.onSort) {
          await this.executeSort();
        } else {
          this.render();
        }
      });
    });

    // Actions
    this.container.querySelectorAll('.dt-action-btn').forEach((btn) => {
      btn.addEventListener('click', (e) => {
        const actionType = btn.dataset.actionType;
        const itemId = btn.dataset.itemId;
        const item = this.config.data.find((d) => d.id === Number(itemId) || d.id === itemId);
        this.config.onAction?.(actionType, item);
      });
    });

    // Pagination
    const prevBtn = this.container.querySelector('#dt-prev-page');
    const nextBtn = this.container.querySelector('#dt-next-page');
    const itemsSelect = this.container.querySelector('#dt-items-per-page');

    if (prevBtn) {
      prevBtn.addEventListener('click', async () => {
        const totalPages = Math.ceil(this.state.totalItems / this.state.itemsPerPage);
        if (this.state.currentPage > 1) {
          this.state.currentPage--;
          
          if (this.config.isRemoteData && this.config.onPageChange) {
            await this.executePageChange();
          } else {
            this.render();
          }
        }
      });
    }

    if (nextBtn) {
      nextBtn.addEventListener('click', async () => {
        const totalPages = Math.ceil(this.state.totalItems / this.state.itemsPerPage);
        if (this.state.currentPage < totalPages) {
          this.state.currentPage++;
          
          if (this.config.isRemoteData && this.config.onPageChange) {
            await this.executePageChange();
          } else {
            this.render();
          }
        }
      });
    }

    if (itemsSelect) {
      itemsSelect.addEventListener('change', async (e) => {
        this.state.itemsPerPage = Number(e.target.value);
        this.state.currentPage = 1;
        
        if (this.config.isRemoteData && this.config.onItemsPerPageChange) {
          await this.executeItemsPerPageChange();
        } else {
          this.render();
        }
      });
    }

    // Réinitialiser Preline après attachement d'événements
    if (typeof HSStaticMethods !== 'undefined') {
      HSStaticMethods.autoInit();
    }
  }

  /**
   * Exécuter la recherche via callback backend
   */
  async executeSearch() {
    try {
      this.state.isLoading = true;
      this.render();

      const result = await this.config.onSearch(
        this.state.searchQuery,
        this.state.filters,
        this.state.sortConfig,
        this.state.currentPage,
        this.state.itemsPerPage
      );

      this.config.data = result.data || [];
      this.state.totalItems = result.totalItems || this.config.data.length;
    } catch (error) {
      console.error('Erreur lors de la recherche:', error);
      this.showError('Erreur lors de la recherche');
    } finally {
      this.state.isLoading = false;
      this.render();
    }
  }

  /**
   * Exécuter le filtre via callback backend
   */
  async executeFilter() {
    try {
      this.state.isLoading = true;
      this.render();

      const result = await this.config.onFilter(
        this.state.filters,
        this.state.searchQuery,
        this.state.sortConfig,
        this.state.currentPage,
        this.state.itemsPerPage
      );

      this.config.data = result.data || [];
      this.state.totalItems = result.totalItems || this.config.data.length;
    } catch (error) {
      console.error('Erreur lors du filtrage:', error);
      this.showError('Erreur lors du filtrage');
    } finally {
      this.state.isLoading = false;
      this.render();
    }
  }

  /**
   * Exécuter le tri via callback backend
   */
  async executeSort() {
    try {
      this.state.isLoading = true;
      this.render();

      const result = await this.config.onSort(
        this.state.sortConfig,
        this.state.searchQuery,
        this.state.filters,
        this.state.currentPage,
        this.state.itemsPerPage
      );

      this.config.data = result.data || [];
      this.state.totalItems = result.totalItems || this.config.data.length;
    } catch (error) {
      console.error('Erreur lors du tri:', error);
      this.showError('Erreur lors du tri');
    } finally {
      this.state.isLoading = false;
      this.render();
    }
  }

  /**
   * Exécuter le changement de page via callback backend
   */
  async executePageChange() {
    try {
      this.state.isLoading = true;
      this.render();

      const result = await this.config.onPageChange(
        this.state.currentPage,
        this.state.itemsPerPage,
        this.state.searchQuery,
        this.state.filters,
        this.state.sortConfig
      );

      this.config.data = result.data || [];
      this.state.totalItems = result.totalItems || this.config.data.length;
    } catch (error) {
      console.error('Erreur lors du changement de page:', error);
      this.showError('Erreur lors du changement de page');
    } finally {
      this.state.isLoading = false;
      this.render();
    }
  }

  /**
   * Exécuter le changement du nombre d'items par page via callback backend
   */
  async executeItemsPerPageChange() {
    try {
      this.state.isLoading = true;
      this.render();

      const result = await this.config.onItemsPerPageChange(
        this.state.itemsPerPage,
        this.state.currentPage,
        this.state.searchQuery,
        this.state.filters,
        this.state.sortConfig
      );

      this.config.data = result.data || [];
      this.state.totalItems = result.totalItems || this.config.data.length;
    } catch (error) {
      console.error('Erreur lors du changement du nombre d\'items:', error);
      this.showError('Erreur lors du changement du nombre d\'items');
    } finally {
      this.state.isLoading = false;
      this.render();
    }
  }

  /**
   * Afficher un message d'erreur
   */
  showError(message) {
    const errorDiv = document.createElement('div');
    errorDiv.className = 'fixed top-4 right-4 bg-red-500 text-white px-4 py-3 rounded-lg shadow-lg';
    errorDiv.textContent = message;
    document.body.appendChild(errorDiv);

    setTimeout(() => {
      errorDiv.remove();
    }, 3000);
  }

  /**
   * Filtrage des données
   */
  applyFilters(data) {
    const { filters } = this.state;
    const { columns } = this.config;

    return data.filter((item) => {
      return Object.entries(filters).every(([key, value]) => {
        if (!value || value === 'ALL') return true;

        const column = columns.find((col) => col.key === key);
        if (!column) return true;

        const cellValue = column.getter ? column.getter(item) : item[key];
        const cellString = String(cellValue).toLowerCase();
        const filterString = String(value).toLowerCase();

        return cellString.includes(filterString);
      });
    });
  }

  /**
   * Recherche globale
   */
  applySearch(data, query) {
    if (!query.trim()) return data;

    const { searchableFields } = this.config;
    const lowerQuery = query.toLowerCase();

    return data.filter((item) => {
      return searchableFields.some((field) => {
        const value = typeof field === 'function' ? field(item) : item[field];
        return String(value).toLowerCase().includes(lowerQuery);
      });
    });
  }

  /**
   * Tri des données
   */
  applySorting(data, sortConfig) {
    if (!sortConfig) return data;

    const { columns } = this.config;
    const sorted = [...data];

    sorted.sort((a, b) => {
      const column = columns.find((col) => col.key === sortConfig.key);
      if (!column) return 0;

      const aValue = column.getter ? column.getter(a) : a[sortConfig.key];
      const bValue = column.getter ? column.getter(b) : b[sortConfig.key];

      if (aValue < bValue) return sortConfig.direction === 'asc' ? -1 : 1;
      if (aValue > bValue) return sortConfig.direction === 'asc' ? 1 : -1;
      return 0;
    });

    return sorted;
  }

  /**
   * Mise à jour des données
   */
  updateData(newData) {
    this.config.data = newData;
    this.state.currentPage = 1;
    this.render();
  }

  /**
   * Réinitialisation des filtres
   */
  resetFilters() {
    this.state.filters = {};
    this.state.searchQuery = '';
    this.state.currentPage = 1;
    this.render();
  }

  /**
   * Récupération de l'état du tableau
   */
  getState() {
    return { ...this.state };
  }
}

// Export pour utilisation en module
if (typeof module !== 'undefined' && module.exports) {
  module.exports = DataTable;
}

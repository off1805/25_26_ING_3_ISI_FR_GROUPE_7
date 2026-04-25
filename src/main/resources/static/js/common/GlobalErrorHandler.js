/**
 * globalErrorHandler.js — Module commun de gestion des erreurs API
 * ─────────────────────────────────────────────────────────────────
 * Utilisation dans n'importe quelle page :
 *
 *   import { apiFetch, AppErrorHandler } from '/js/common/globalErrorHandler.js';
 *
 *   // 1) Wrapper fetch intelligent (gère tout automatiquement)
 *   const data = await apiFetch('/api/students', { method: 'POST', body: … });
 *
 *   // 2) Affichage manuel d'une erreur
 *   AppErrorHandler.show({ category: 'CONFLICT', message: 'Étudiant déjà inscrit.' });
 *
 * Catégories disponibles :
 *   CONFLICT | NOT_FOUND | VALIDATION | AUTH | FORBIDDEN | SERVER | NETWORK | INFO
 */

// ═══════════════════════════════════════════════════════════════════════════════
// CONFIG DES CATÉGORIES D'ERREURS
// ═══════════════════════════════════════════════════════════════════════════════

const ERROR_CATEGORIES = {

  CONFLICT: {
    title:     'Déjà existant',
    subtitle:  'Cet élément existe déjà dans le système.',
    color:     'orange',
    icon: `<svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 24 24"
               fill="none" stroke="currentColor" stroke-width="1.75"
               stroke-linecap="round" stroke-linejoin="round">
             <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/>
             <circle cx="9" cy="7" r="4"/>
             <line x1="17" y1="8" x2="23" y2="14"/>
             <line x1="23" y1="8" x2="17" y2="14"/>
           </svg>`,
    badge:     'Conflit',
    actionLabel: 'Fermer',
    showRetry: false,
  },

  NOT_FOUND: {
    title:     'Introuvable',
    subtitle:  'L\'élément demandé n\'existe pas ou a été supprimé.',
    color:     'blue',
    icon: `<svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 24 24"
               fill="none" stroke="currentColor" stroke-width="1.75"
               stroke-linecap="round" stroke-linejoin="round">
             <circle cx="11" cy="11" r="8"/>
             <line x1="21" y1="21" x2="16.65" y2="16.65"/>
             <line x1="11" y1="8" x2="11" y2="11"/>
             <line x1="11" y1="14" x2="11.01" y2="14"/>
           </svg>`,
    badge:     'Introuvable',
    actionLabel: 'Fermer',
    showRetry: false,
  },

  VALIDATION: {
    title:     'Données invalides',
    subtitle:  'Veuillez vérifier les informations saisies.',
    color:     'yellow',
    icon: `<svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 24 24"
               fill="none" stroke="currentColor" stroke-width="1.75"
               stroke-linecap="round" stroke-linejoin="round">
             <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
             <line x1="12" y1="9" x2="12" y2="13"/>
             <line x1="12" y1="17" x2="12.01" y2="17"/>
           </svg>`,
    badge:     'Validation',
    actionLabel: 'Corriger',
    showRetry: false,
  },

  AUTH: {
    title:     'Session expirée',
    subtitle:  'Votre session a expiré. Veuillez vous reconnecter.',
    color:     'red',
    icon: `<svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 24 24"
               fill="none" stroke="currentColor" stroke-width="1.75"
               stroke-linecap="round" stroke-linejoin="round">
             <rect width="18" height="11" x="3" y="11" rx="2" ry="2"/>
             <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
             <line x1="12" y1="16" x2="12.01" y2="16"/>
           </svg>`,
    badge:     'Authentification',
    actionLabel: 'Se reconnecter',
    showRetry: false,
    onAction: () => { window.location.href = '/login'; },
  },

  FORBIDDEN: {
    title:     'Accès refusé',
    subtitle:  'Vous n\'avez pas les droits nécessaires pour cette action.',
    color:     'red',
    icon: `<svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 24 24"
               fill="none" stroke="currentColor" stroke-width="1.75"
               stroke-linecap="round" stroke-linejoin="round">
             <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
             <line x1="9.5" y1="9.5" x2="14.5" y2="14.5"/>
             <line x1="14.5" y1="9.5" x2="9.5" y2="14.5"/>
           </svg>`,
    badge:     'Accès refusé',
    actionLabel: 'Fermer',
    showRetry: false,
  },

  SERVER: {
    title:     'Erreur serveur',
    subtitle:  'Une erreur interne est survenue. Nos équipes sont notifiées.',
    color:     'red',
    icon: `<svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 24 24"
               fill="none" stroke="currentColor" stroke-width="1.75"
               stroke-linecap="round" stroke-linejoin="round">
             <rect width="20" height="8" x="2" y="2" rx="2" ry="2"/>
             <rect width="20" height="8" x="2" y="14" rx="2" ry="2"/>
             <line x1="6" y1="6" x2="6.01" y2="6"/>
             <line x1="6" y1="18" x2="6.01" y2="18"/>
             <line x1="12" y1="18" x2="18" y2="18"/>
             <line x1="15" y1="15" x2="15" y2="21"/>
           </svg>`,
    badge:     'Erreur 500',
    actionLabel: 'Réessayer',
    showRetry: true,
  },

  NETWORK: {
    title:     'Problème de connexion',
    subtitle:  'Impossible de joindre le serveur. Vérifiez votre connexion internet.',
    color:     'gray',
    icon: `<svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 24 24"
               fill="none" stroke="currentColor" stroke-width="1.75"
               stroke-linecap="round" stroke-linejoin="round">
             <line x1="1" y1="1" x2="23" y2="23"/>
             <path d="M16.72 11.06A10.94 10.94 0 0 1 19 12.55"/>
             <path d="M5 12.55a10.94 10.94 0 0 1 5.17-2.39"/>
             <path d="M10.71 5.05A16 16 0 0 1 22.56 9"/>
             <path d="M1.42 9a15.91 15.91 0 0 1 4.7-2.88"/>
             <path d="M8.53 16.11a6 6 0 0 1 6.95 0"/>
             <line x1="12" y1="20" x2="12.01" y2="20"/>
           </svg>`,
    badge:     'Hors ligne',
    actionLabel: 'Réessayer',
    showRetry: true,
  },

  INFO: {
    title:     'Information',
    subtitle:  '',
    color:     'blue',
    icon: `<svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 24 24"
               fill="none" stroke="currentColor" stroke-width="1.75"
               stroke-linecap="round" stroke-linejoin="round">
             <circle cx="12" cy="12" r="10"/>
             <line x1="12" y1="8" x2="12" y2="12"/>
             <line x1="12" y1="16" x2="12.01" y2="16"/>
           </svg>`,
    badge:     'Info',
    actionLabel: 'Fermer',
    showRetry: false,
  },
};

// ═══════════════════════════════════════════════════════════════════════════════
// MAPPING errorCode (backend) → category (frontend)
// ═══════════════════════════════════════════════════════════════════════════════

const ERROR_CODE_MAP = {
  ENTITY_ALREADY_EXISTS: 'CONFLICT',
  ENTITY_NOT_FOUND:      'NOT_FOUND',
  DOMAIN_ERROR:          'VALIDATION',
  VALIDATION_ERROR:      'VALIDATION',
  INVALID_TOKEN:         'AUTH',
  ACCESS_DENIED:         'FORBIDDEN',
  TECHNICAL_ERROR:       'SERVER',
  INTERNAL_ERROR:        'SERVER',
};

// Mapping par code HTTP (fallback si errorCode absent)
function categoryFromStatus(status) {
  if (status === 409) return 'CONFLICT';
  if (status === 404) return 'NOT_FOUND';
  if (status === 400) return 'VALIDATION';
  if (status === 401) return 'AUTH';
  if (status === 403) return 'FORBIDDEN';
  if (status >= 500)  return 'SERVER';
  return 'SERVER';
}

// ═══════════════════════════════════════════════════════════════════════════════
// PALETTE DE COULEURS (classes Tailwind pré-définies pour éviter la purge CSS)
// ═══════════════════════════════════════════════════════════════════════════════

const COLOR_CLASSES = {
  orange: {
    bg:     'bg-orange-50 dark:bg-orange-950/30',
    border: 'border-orange-200 dark:border-orange-800',
    icon:   'bg-orange-100 text-orange-600 dark:bg-orange-900/50 dark:text-orange-400',
    badge:  'bg-orange-100 text-orange-700 dark:bg-orange-900/50 dark:text-orange-300',
    btn:    'bg-orange-500 hover:bg-orange-600 text-white',
    bar:    'bg-orange-500',
  },
  blue: {
    bg:     'bg-blue-50 dark:bg-blue-950/30',
    border: 'border-blue-200 dark:border-blue-800',
    icon:   'bg-blue-100 text-blue-600 dark:bg-blue-900/50 dark:text-blue-400',
    badge:  'bg-blue-100 text-blue-700 dark:bg-blue-900/50 dark:text-blue-300',
    btn:    'bg-blue-500 hover:bg-blue-600 text-white',
    bar:    'bg-blue-500',
  },
  yellow: {
    bg:     'bg-yellow-50 dark:bg-yellow-950/30',
    border: 'border-yellow-200 dark:border-yellow-800',
    icon:   'bg-yellow-100 text-yellow-600 dark:bg-yellow-900/50 dark:text-yellow-400',
    badge:  'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/50 dark:text-yellow-300',
    btn:    'bg-yellow-500 hover:bg-yellow-600 text-white',
    bar:    'bg-yellow-500',
  },
  red: {
    bg:     'bg-red-50 dark:bg-red-950/30',
    border: 'border-red-200 dark:border-red-800',
    icon:   'bg-red-100 text-red-600 dark:bg-red-900/50 dark:text-red-400',
    badge:  'bg-red-100 text-red-700 dark:bg-red-900/50 dark:text-red-300',
    btn:    'bg-red-500 hover:bg-red-600 text-white',
    bar:    'bg-red-500',
  },
  gray: {
    bg:     'bg-gray-50 dark:bg-gray-900/30',
    border: 'border-gray-200 dark:border-gray-700',
    icon:   'bg-gray-100 text-gray-600 dark:bg-gray-800 dark:text-gray-400',
    badge:  'bg-gray-100 text-gray-700 dark:bg-gray-800 dark:text-gray-300',
    btn:    'bg-gray-600 hover:bg-gray-700 text-white',
    bar:    'bg-gray-500',
  },
};

// ═══════════════════════════════════════════════════════════════════════════════
// INJECTION DU MODAL DANS LE DOM (une seule fois)
// ═══════════════════════════════════════════════════════════════════════════════

function injectModalIfNeeded() {
  if (document.getElementById('app-error-modal')) return;

  const tpl = document.createElement('div');
  tpl.innerHTML = `
<!-- ── AppErrorModal ────────────────────────────────────────────── -->
<div id="app-error-modal"
     class="fixed inset-0 z-[9999] flex items-center justify-center p-4 hidden"
     role="alertdialog" aria-modal="true" aria-labelledby="aem-title">

  <!-- Backdrop -->
  <div id="aem-backdrop"
       class="absolute inset-0 bg-black/50 backdrop-blur-sm transition-opacity duration-200 opacity-0"></div>

  <!-- Panel -->
  <div id="aem-panel"
       class="relative w-full max-w-md rounded-2xl shadow-2xl border overflow-hidden
              transition-all duration-200 scale-95 opacity-0 bg-card">

    <!-- Barre colorée en haut -->
    <div id="aem-bar" class="h-1 w-full"></div>

    <!-- Corps du modal -->
    <div id="aem-body" class="p-6">

      <!-- Header : icône + badge + titre + sous-titre -->
      <div class="flex items-start gap-4 mb-5">
        <div id="aem-icon-wrap"
             class="shrink-0 size-12 rounded-xl flex items-center justify-center">
          <span id="aem-icon"></span>
        </div>
        <div class="flex-1 min-w-0">
          <div class="flex items-center gap-2 mb-1 flex-wrap">
            <span id="aem-badge"
                  class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-semibold"></span>
          </div>
          <h3 id="aem-title"
              class="text-base font-bold text-layer-foreground leading-tight"></h3>
          <p id="aem-subtitle"
             class="text-xs text-muted-foreground-2 mt-0.5 leading-relaxed"></p>
        </div>
      </div>

      <!-- Message principal -->
      <div id="aem-message-wrap"
           class="rounded-xl border p-4 mb-5 text-sm text-layer-foreground leading-relaxed">
        <p id="aem-message"></p>
      </div>

      <!-- Détails techniques (accordéon, masqué par défaut) -->
      <details id="aem-details-wrap" class="mb-5 hidden">
        <summary class="text-xs text-muted-foreground-2 cursor-pointer select-none hover:text-layer-foreground transition-colors">
          Détails techniques
        </summary>
        <pre id="aem-details"
             class="mt-2 text-xs text-muted-foreground-2 bg-muted rounded-lg p-3 overflow-x-auto whitespace-pre-wrap break-all leading-relaxed"></pre>
      </details>

      <!-- Actions -->
      <div class="flex items-center justify-end gap-3">
        <button id="aem-btn-retry"
                class="hidden px-4 py-2 text-sm font-semibold rounded-xl border border-layer-line
                       text-layer-foreground hover:bg-muted transition-colors focus:outline-none
                       focus:ring-2 focus:ring-offset-1">
          <svg class="inline w-3.5 h-3.5 mr-1.5 -mt-0.5" xmlns="http://www.w3.org/2000/svg"
               viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <polyline points="1 4 1 10 7 10"/><path d="M3.51 15a9 9 0 1 0 .49-3.51"/>
          </svg>
          Réessayer
        </button>
        <button id="aem-btn-action"
                class="px-5 py-2 text-sm font-semibold rounded-xl transition-colors
                       focus:outline-none focus:ring-2 focus:ring-offset-1">
        </button>
      </div>

    </div><!-- /aem-body -->
  </div><!-- /aem-panel -->
</div>
<!-- ── /AppErrorModal ────────────────────────────────────────────── -->
  `;
  document.body.appendChild(tpl.firstElementChild);

  // Fermer en cliquant le backdrop
  document.getElementById('aem-backdrop').addEventListener('click', AppErrorHandler.close);
  // Fermer avec Échap
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') AppErrorHandler.close();
  });
}

// ═══════════════════════════════════════════════════════════════════════════════
// API PUBLIQUE
// ═══════════════════════════════════════════════════════════════════════════════

let _retryCallback = null;

export const AppErrorHandler = {

  /**
   * Affiche le modal d'erreur.
   *
   * @param {object} opts
   * @param {string}   opts.category    - Clé de ERROR_CATEGORIES (ex : 'CONFLICT')
   * @param {string}   [opts.message]   - Message principal à afficher
   * @param {string}   [opts.details]   - Détails techniques (stacktrace, JSON…)
   * @param {Function} [opts.onRetry]   - Callback appelé si l'user clique "Réessayer"
   * @param {Function} [opts.onAction]  - Override du callback du bouton principal
   */
  show({ category = 'SERVER', message = '', details = '', onRetry = null, onAction = null } = {}) {
    injectModalIfNeeded();

    const cfg    = ERROR_CATEGORIES[category] ?? ERROR_CATEGORIES.SERVER;
    const colors = COLOR_CLASSES[cfg.color]   ?? COLOR_CLASSES.red;

    // Barre colorée
    document.getElementById('aem-bar').className = `h-1 w-full ${colors.bar}`;

    // Corps (fond coloré doux)
    const body = document.getElementById('aem-body');
    body.className = `p-6 ${colors.bg}`;

    // Icône
    const iconWrap = document.getElementById('aem-icon-wrap');
    iconWrap.className = `shrink-0 size-12 rounded-xl flex items-center justify-center ${colors.icon}`;
    document.getElementById('aem-icon').innerHTML = cfg.icon;

    // Badge
    const badge = document.getElementById('aem-badge');
    badge.className = `inline-flex items-center px-2 py-0.5 rounded-full text-xs font-semibold ${colors.badge}`;
    badge.textContent = cfg.badge;

    // Textes
    document.getElementById('aem-title').textContent    = cfg.title;
    document.getElementById('aem-subtitle').textContent = message || cfg.subtitle;

    // Message principal
    const msgWrap = document.getElementById('aem-message-wrap');
    msgWrap.className = `rounded-xl border ${colors.border} p-4 mb-5 text-sm text-layer-foreground leading-relaxed`;
    document.getElementById('aem-message').textContent = message || cfg.subtitle;

    // Détails techniques
    const detailsWrap = document.getElementById('aem-details-wrap');
    if (details) {
      detailsWrap.classList.remove('hidden');
      document.getElementById('aem-details').textContent = details;
    } else {
      detailsWrap.classList.add('hidden');
    }

    // Bouton Réessayer
    const btnRetry = document.getElementById('aem-btn-retry');
    if (cfg.showRetry && onRetry) {
      _retryCallback = onRetry;
      btnRetry.classList.remove('hidden');
      btnRetry.onclick = () => { AppErrorHandler.close(); onRetry(); };
    } else {
      btnRetry.classList.add('hidden');
    }

    // Bouton principal
    const btnAction = document.getElementById('aem-btn-action');
    btnAction.className = `px-5 py-2 text-sm font-semibold rounded-xl transition-colors focus:outline-none focus:ring-2 focus:ring-offset-1 ${colors.btn}`;
    btnAction.textContent = cfg.actionLabel;
    const actionFn = onAction ?? cfg.onAction ?? AppErrorHandler.close;
    btnAction.onclick = actionFn;

    // Animation d'ouverture
    const modal = document.getElementById('app-error-modal');
    modal.classList.remove('hidden');
    requestAnimationFrame(() => {
      document.getElementById('aem-backdrop').classList.replace('opacity-0', 'opacity-100');
      const panel = document.getElementById('aem-panel');
      panel.classList.replace('scale-95', 'scale-100');
      panel.classList.replace('opacity-0', 'opacity-100');
    });

    // Focus trap
    btnAction.focus();
  },

  /** Ferme le modal. */
  close() {
    const modal    = document.getElementById('app-error-modal');
    const backdrop = document.getElementById('aem-backdrop');
    const panel    = document.getElementById('aem-panel');
    if (!modal) return;

    backdrop.classList.replace('opacity-100', 'opacity-0');
    panel.classList.replace('scale-100', 'scale-95');
    panel.classList.replace('opacity-100', 'opacity-0');

    setTimeout(() => {
      modal.classList.add('hidden');
      _retryCallback = null;
    }, 200);
  },

  /**
   * Traite une Response fetch et affiche le bon modal si erreur.
   * Retourne le body JSON si la réponse est OK, lance une erreur sinon.
   *
   * @param {Response}  response  - Objet Response de fetch()
   * @param {Function}  [onRetry] - Callback "Réessayer" optionnel
   * @returns {Promise<any>}
   */
  async handleResponse(response, onRetry = null) {
    if (response.ok) {
      const text = await response.text();
      return text ? JSON.parse(text) : null;
    }

    let errorBody = null;
    let rawText   = '';
    try {
      rawText   = await response.text();
      errorBody = rawText ? JSON.parse(rawText) : null;
    } catch (_) { /* ignore parse error */ }

    // Déterminer la catégorie
    const category = errorBody?.errorCode
      ? (ERROR_CODE_MAP[errorBody.errorCode] ?? categoryFromStatus(response.status))
      : categoryFromStatus(response.status);

    const message = errorBody?.message || _defaultMessage(response.status);
    const details = rawText !== message ? rawText : '';

    this.show({ category, message, details, onRetry });

    const err = new Error(message);
    err.status    = response.status;
    err.errorCode = errorBody?.errorCode ?? null;
    err.handled   = true;          // indique que le modal a déjà été affiché
    throw err;
  },

  /**
   * Traite une erreur réseau (fetch rejeté, timeout…).
   *
   * @param {Error}    err      - L'erreur levée par fetch()
   * @param {Function} [onRetry]
   */
  handleNetworkError(err, onRetry = null) {
    this.show({
      category: 'NETWORK',
      message:  'Impossible de joindre le serveur. Vérifiez votre connexion internet et réessayez.',
      details:  err?.message ?? '',
      onRetry,
    });
  },
};

// ═══════════════════════════════════════════════════════════════════════════════
// WRAPPER FETCH INTELLIGENT
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Remplace fetch() avec gestion automatique des erreurs API.
 *
 * Exemple :
 *   const student = await apiFetch('/api/students/1');
 *   const created = await apiFetch('/api/students', {
 *     method: 'POST',
 *     headers: { 'Content-Type': 'application/json' },
 *     body: JSON.stringify(payload),
 *   }, { onRetry: () => submitForm() });
 *
 * @param {string}  url
 * @param {object}  [fetchOptions]   - Options natives de fetch()
 * @param {object}  [handlerOptions]
 * @param {Function} [handlerOptions.onRetry] - Callback si l'user veut réessayer
 * @returns {Promise<any>}           - Body JSON parsé ou null
 */
export async function apiFetch(url, fetchOptions = {}, { onRetry } = {}) {
  // CSRF token si présent dans le DOM (Spring Security)
  const csrfToken  = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

  const headers = { ...(fetchOptions.headers ?? {}) };
  if (csrfToken && csrfHeader) headers[csrfHeader] = csrfToken;

  let response;
  try {
    response = await fetch(url, { ...fetchOptions, headers });
  } catch (networkErr) {
    AppErrorHandler.handleNetworkError(networkErr, onRetry);
    const e = new Error('Network error');
    e.handled = true;
    throw e;
  }

  return AppErrorHandler.handleResponse(response, onRetry);
}

// ═══════════════════════════════════════════════════════════════════════════════
// UTILITAIRES PRIVÉS
// ═══════════════════════════════════════════════════════════════════════════════

function _defaultMessage(status) {
  const msgs = {
    400: 'Les données envoyées sont incorrectes ou incomplètes.',
    401: 'Votre session a expiré. Veuillez vous reconnecter.',
    403: 'Vous n\'avez pas les droits pour effectuer cette action.',
    404: 'La ressource demandée est introuvable.',
    409: 'Cet élément existe déjà dans le système.',
    500: 'Une erreur interne est survenue. Veuillez réessayer plus tard.',
    503: 'Le service est temporairement indisponible.',
  };
  return msgs[status] ?? `Une erreur est survenue (code ${status}).`;
}

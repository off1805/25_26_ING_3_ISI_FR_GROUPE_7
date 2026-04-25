/**
 * ClientHttp.js — Client HTTP générique basé sur apiFetch
 * ─────────────────────────────────────────────────────────
 * Fournit une API simple (get / post / put / patch / delete)
 * en s'appuyant sur apiFetch de globalErrorHandler.js.
 * Toutes les erreurs API sont donc gérées automatiquement par
 * le système de modals catégorisés.
 *
 * Utilisation :
 *   import api from '/js/common/ClientHttp.js';
 *
 *   const students = await api.get('/api/students');
 *   const created  = await api.post('/api/students', payload);
 *   const updated  = await api.put('/api/students/1', payload);
 *   await api.delete('/api/students/1');
 */

import { apiFetch } from '/js/common/globalErrorHandler.js';

// ── Helpers internes ───────────────────────────────────────────────────────

function jsonHeaders() {
  return { 'Content-Type': 'application/json' };
}

function buildOptions(method, body, extraOptions = {}) {
  const opts = { method, ...extraOptions };
  if (body !== undefined) {
    opts.headers = { ...jsonHeaders(), ...(extraOptions.headers ?? {}) };
    opts.body = typeof body === 'string' ? body : JSON.stringify(body);
  }
  return opts;
}

// ── API publique ───────────────────────────────────────────────────────────

const api = {
  /**
   * GET — récupérer une ressource.
   * @param {string}   url
   * @param {object}   [handlerOpts]        { onRetry }
   * @returns {Promise<any>}
   */
  get(url, handlerOpts = {}) {
    return apiFetch(url, { method: 'GET' }, handlerOpts);
  },

  /**
   * POST — créer une ressource.
   * @param {string}   url
   * @param {any}      body                 Corps à sérialiser en JSON
   * @param {object}   [handlerOpts]        { onRetry }
   * @returns {Promise<any>}
   */
  post(url, body, handlerOpts = {}) {
    return apiFetch(url, buildOptions('POST', body), handlerOpts);
  },

  /**
   * PUT — remplacer une ressource.
   * @param {string}   url
   * @param {any}      body
   * @param {object}   [handlerOpts]
   * @returns {Promise<any>}
   */
  put(url, body, handlerOpts = {}) {
    return apiFetch(url, buildOptions('PUT', body), handlerOpts);
  },

  /**
   * PATCH — modifier partiellement une ressource.
   * @param {string}   url
   * @param {any}      body
   * @param {object}   [handlerOpts]
   * @returns {Promise<any>}
   */
  patch(url, body, handlerOpts = {}) {
    return apiFetch(url, buildOptions('PATCH', body), handlerOpts);
  },

  /**
   * DELETE — supprimer une ressource.
   * @param {string}   url
   * @param {object}   [handlerOpts]
   * @returns {Promise<any>}
   */
  delete(url, handlerOpts = {}) {
    return apiFetch(url, { method: 'DELETE' }, handlerOpts);
  },

  /**
   * Upload — envoyer un FormData (multipart/form-data).
   * Ne pose pas de Content-Type (le navigateur le fait automatiquement).
   * @param {string}   url
   * @param {FormData} formData
   * @param {object}   [handlerOpts]
   * @returns {Promise<any>}
   */
  upload(url, formData, handlerOpts = {}) {
    return apiFetch(url, { method: 'POST', body: formData }, handlerOpts);
  },
};

export default api;

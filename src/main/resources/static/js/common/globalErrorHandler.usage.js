/**
 * Exemple d'intégration de globalErrorHandler.js dans un contrôleur existant.
 *
 * Ce fichier montre comment migrer un StudentController (ou tout autre contrôleur)
 * pour utiliser apiFetch() à la place de fetch() natif.
 *
 * ─── AVANT (fetch natif, aucune gestion d'erreur) ───────────────────────────
 *
 *   const res = await fetch('/api/students', {
 *     method: 'POST',
 *     headers: { 'Content-Type': 'application/json' },
 *     body: JSON.stringify(payload),
 *   });
 *   if (!res.ok) {
 *     console.error('Erreur', res.status);    // ← "Error 500" opaque
 *     return;
 *   }
 *   const data = await res.json();
 *
 * ─── APRÈS (apiFetch, modal explicite automatique) ──────────────────────────
 *
 *   import { apiFetch } from '/js/common/globalErrorHandler.js';
 *
 *   try {
 *     const data = await apiFetch('/api/students', {
 *       method: 'POST',
 *       headers: { 'Content-Type': 'application/json' },
 *       body: JSON.stringify(payload),
 *     }, { onRetry: () => submitForm() });
 *
 *     // Ici : succès garanti, data est le JSON retourné
 *     showToast('Étudiant inscrit avec succès !', 'success');
 *
 *   } catch (err) {
 *     if (!err.handled) {
 *       // Erreur inattendue non gérée (rare) — logguer ou afficher un toast basique
 *       console.error('Erreur non gérée :', err);
 *     }
 *     // Si err.handled === true, le modal est déjà affiché → ne rien faire de plus
 *   }
 *
 * ─── EXEMPLES PAR CAS MÉTIER ────────────────────────────────────────────────
 *
 * 1) Étudiant déjà inscrit (backend lance EntityAlreadyExistException) :
 *    → Backend retourne { status:409, errorCode:"ENTITY_ALREADY_EXISTS", message:"L'étudiant ISI-2024-001 est déjà inscrit." }
 *    → Modal CONFLICT : titre "Déjà existant", message du backend, bouton "Fermer"
 *
 * 2) Classe introuvable (EntityNotFoundException) :
 *    → Backend retourne { status:404, errorCode:"ENTITY_NOT_FOUND", message:"Aucune classe avec l'id 42." }
 *    → Modal NOT_FOUND : titre "Introuvable", icône loupe, bouton "Fermer"
 *
 * 3) Formulaire invalide (@Valid échoue) :
 *    → Backend retourne { status:400, errorCode:"VALIDATION_ERROR", message:"email : doit être une adresse valide" }
 *    → Modal VALIDATION : titre "Données invalides", fond jaune
 *
 * 4) Token expiré :
 *    → Backend retourne { status:401, errorCode:"INVALID_TOKEN" }
 *    → Modal AUTH : titre "Session expirée", bouton "Se reconnecter" → redirect /login
 *
 * 5) Coupure réseau :
 *    → fetch() rejette (TypeError: Failed to fetch)
 *    → Modal NETWORK : titre "Problème de connexion", bouton "Réessayer"
 *
 * ─── AFFICHAGE MANUEL ───────────────────────────────────────────────────────
 *
 *   import { AppErrorHandler } from '/js/common/globalErrorHandler.js';
 *
 *   // Validation côté client avant même l'appel API
 *   if (!form.checkValidity()) {
 *     AppErrorHandler.show({
 *       category: 'VALIDATION',
 *       message:  'Veuillez remplir tous les champs obligatoires.',
 *     });
 *     return;
 *   }
 *
 *   // Accès refusé détecté localement
 *   AppErrorHandler.show({ category: 'FORBIDDEN' });
 *
 * ─── TOASTIFY (notifications légères, non bloquantes) ───────────────────────
 *
 *   // Pour les succès ou infos, continuer à utiliser Toastify :
 *   Toastify({ text: '✓ Étudiant ajouté', duration: 3000 }).showToast();
 *
 *   // Les erreurs importantes → modal AppErrorHandler (bloquant, explicite)
 *   // Les infos mineures     → Toastify             (non bloquant, discret)
 */

// Ce fichier est purement documentaire — il n'exporte aucune fonction.
export {};

/**
 * StudentController.js
 * Contrôleur principal de la page "Gestion des étudiants".
 *
 * Fonctionnalités :
 *  - Bouton dropdown "Ajouter" → "Manuellement" / "Importer depuis Excel"
 *  - Modal ajout manuel (un étudiant à la fois)
 *  - Modal import Excel :
 *      1. Upload du fichier + paramétrage des colonnes (coordonnées titres)
 *      2. Barre de progression pendant l'inscription en séquence
 *      3. Toast pour chaque échec, le processus continue
 *      4. Récapitulatif des échecs en fin de traitement
 */

import { ClasseApi }        from '../../common/application/ClasseApi.js';
import { enrollStudentUC }  from '../application/EnrollStudentUC.js';
import { EtudiantExcelParser } from '../../ExcelJs/application/infrastructure/Lecture.js';
import { showToast }        from './studentToast.js';

// ── État global ──────────────────────────────────────────────────────────────

let classeSelectionneeId = null;

// ── Init ─────────────────────────────────────────────────────────────────────

document.addEventListener('DOMContentLoaded', async () => {
    attacherEvenements();
});

// ── Helpers ──────────────────────────────────────────────────────────────────

function getActiveClasseId() {
    // Dans APClasses.html, l'onglet actif a la classe "active" (ajoutée par Thymeleaf ou Preline)
    const activeTab = document.querySelector('#classes-tablist button.active');
    if (!activeTab) return null;
    return parseInt(activeTab.getAttribute('data-classe-id'), 10);
}

// ── Attachement des événements ────────────────────────────────────────────────

function attacherEvenements() {
    // Dropdown → Ajouter manuellement
    document.getElementById('btn-add-manual')?.addEventListener('click', () => {
        ouvrirModal('modal-manual');
    });

    // Dropdown → Importer depuis Excel
    document.getElementById('btn-add-excel')?.addEventListener('click', () => {
        resetModalExcel();
    });

    // Fermeture des modals
    document.querySelectorAll('[data-close-modal]').forEach(btn => {
        btn.addEventListener('click', (e) => {
            const modalEl = e.target.closest('.hs-overlay');
            if (modalEl && typeof HSOverlay !== 'undefined') {
                HSOverlay.close(modalEl);
            }
        });
    });

    // Clic en dehors d'un modal le ferme
    document.querySelectorAll('.modal-overlay').forEach(overlay => {
        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) fermerTousLesModals();
        });
    });

    // Formulaire ajout manuel
    document.getElementById('form-manual')?.addEventListener('submit', soumettreFormManuel);

    // Changement de fichier Excel → afficher le nom
    document.getElementById('input-excel-file')?.addEventListener('change', (e) => {
        const fichier = e.target.files[0];
        const label = document.getElementById('excel-file-label');
        if (label) label.textContent = fichier ? fichier.name : 'Aucun fichier sélectionné';
    });

    // Bouton "Importer" dans le modal Excel
    document.getElementById('btn-start-import')?.addEventListener('click', lancerImportExcel);
}

// ── Helpers modals ────────────────────────────────────────────────────────────

function fermerModal(id) {
    const el = document.getElementById(id);
    if (el && typeof HSOverlay !== 'undefined') {
        HSOverlay.close(el);
    }
}

// ── Ajout manuel ──────────────────────────────────────────────────────────────

async function soumettreFormManuel(e) {
    e.preventDefault();
    const form = e.target;
    const btnSubmit = form.querySelector('button[type="submit"]');

    const data = {
        email:           form.email.value.trim(),
        nom:             form.nom.value.trim(),
        prenom:          form.prenom.value.trim(),
        matricule:       form.matricule.value.trim(),
        numeroTelephone: form.numeroTelephone.value.trim(),
        classeId:        getActiveClasseId(),
    };

    if (!data.classeId) {
        showToast('Erreur : impossible d\'identifier la classe active.', 'error');
        return;
    }

    btnSubmit.disabled = true;
    btnSubmit.textContent = 'Inscription…';

    try {
        const res = await enrollStudentUC(data);
        const msg = res.created
            ? `✓ ${res.nom} ${res.prenom} créé(e) et inscrit(e).`
            : `✓ ${res.nom} ${res.prenom} inscrit(e) dans la classe.`;
        showToast(msg, 'success');
        form.reset();
        fermerModal('modal-manual');
        // Refresh to see the new student in the table
        setTimeout(() => window.location.reload(), 1000);
    } catch (err) {
        showToast(err.message, 'error');
    } finally {
        btnSubmit.disabled = false;
        btnSubmit.textContent = 'Inscrire';
    }
}

// ── Import Excel ──────────────────────────────────────────────────────────────

function resetModalExcel() {
    // Réinitialise l'état du modal Excel
    const zones = ['excel-step-upload', 'excel-step-progress', 'excel-step-result'];
    zones.forEach(id => document.getElementById(id)?.classList.add('hidden'));
    document.getElementById('excel-step-upload')?.classList.remove('hidden');

    const fileInput = document.getElementById('input-excel-file');
    if (fileInput) fileInput.value = '';

    const label = document.getElementById('excel-file-label');
    if (label) label.textContent = 'Aucun fichier sélectionné';

    // Remettre les colonnes par défaut dans les inputs
    const defauts = EtudiantExcelParser.COLONNES_DEFAUT;
    setInputVal('col-email',    defauts.email);
    setInputVal('col-nom',      defauts.nom);
    setInputVal('col-prenom',   defauts.prenom);
    setInputVal('col-matricule',defauts.matricule);
    setInputVal('col-telephone',defauts.numeroTelephone);
    setInputVal('ligne-debut',  '2');
}

function setInputVal(id, val) {
    const el = document.getElementById(id);
    if (el) el.value = val;
}

function getInputVal(id) {
    return document.getElementById(id)?.value?.trim() || '';
}

async function lancerImportExcel() {
    const fichier  = document.getElementById('input-excel-file')?.files[0];
    const classeId = getActiveClasseId();

    if (!fichier) {
        showToast('Veuillez sélectionner un fichier Excel.', 'warning');
        return;
    }
    if (!classeId) {
        showToast('Erreur : impossible d\'identifier la classe cible.', 'error');
        return;
    }

    const colonnes = {
        email:           getInputVal('col-email')    || 'A',
        nom:             getInputVal('col-nom')      || 'B',
        prenom:          getInputVal('col-prenom')   || 'C',
        matricule:       getInputVal('col-matricule')|| 'D',
        numeroTelephone: getInputVal('col-telephone')|| 'E',
    };
    const debutLigne = parseInt(getInputVal('ligne-debut'), 10) || 2;

    // Lecture du fichier Excel
    let etudiants;
    try {
        etudiants = await EtudiantExcelParser.lireFichier(fichier, debutLigne, colonnes);
    } catch (err) {
        showToast('Lecture du fichier : ' + err.message, 'error');
        return;
    }

    // Validation préliminaire
    const erreursValidation = etudiants.flatMap(et => EtudiantExcelParser.validerEtudiant(et));
    if (erreursValidation.length > 0) {
        showToast(`${erreursValidation.length} erreur(s) de format dans le fichier. Corrigez-les avant d'importer.`, 'warning');
        afficherErreursValidation(erreursValidation);
        return;
    }

    // Passer à l'étape progression
    afficherEtape('excel-step-progress');

    const total  = etudiants.length;
    const echecs = [];

    for (let i = 0; i < total; i++) {
        const et = etudiants[i];
        mettreAJourProgression(i, total, et);

        try {
            await enrollStudentUC({ ...et, classeId });
        } catch (err) {
            echecs.push({ etudiant: et, raison: err.message });
            showToast(`Échec ligne ${et.ligne} (${et.email}) : ${err.message}`, 'error');
        }

        // Petite pause pour ne pas saturer le serveur
        await pause(120);
    }

    // Résultat final
    mettreAJourProgression(total, total, null);
    await pause(400);
    afficherResultat(total, echecs);

    // Si des succès ont eu lieu, on rafraîchit la page à la fermeture pour voir les changements
    if (echecs.length < total) {
        // On attend que l'utilisateur ferme le modal de résultat ou on peut forcer un reload après un délai
        // Pour rester simple et efficace :
        setTimeout(() => {
            // On ne reload que si l'utilisateur n'a pas déjà fermé (ou on force le reload quoi qu'il arrive)
            // L'idéal est de reload quand on ferme le modal final.
        }, 2000);
    }
}

function afficherEtape(idVisible) {
    ['excel-step-upload', 'excel-step-progress', 'excel-step-result'].forEach(id => {
        document.getElementById(id)?.classList.toggle('hidden', id !== idVisible);
    });
}

function mettreAJourProgression(traites, total, etudiantEnCours) {
    const pct = Math.round((traites / total) * 100);

    const bar  = document.getElementById('progress-bar');
    const pctEl = document.getElementById('progress-pct');
    const infoEl = document.getElementById('progress-info');

    if (bar)   { bar.style.width = pct + '%'; bar.setAttribute('aria-valuenow', pct); }
    if (pctEl) pctEl.textContent = pct + '%';
    if (infoEl) {
        if (etudiantEnCours) {
            infoEl.textContent = `Inscription de ${etudiantEnCours.nom} ${etudiantEnCours.prenom}… (${traites + 1}/${total})`;
        } else {
            infoEl.textContent = `Traitement terminé — ${total} étudiant(s) traité(s).`;
        }
    }
}

function afficherResultat(total, echecs) {
    afficherEtape('excel-step-result');

    const succes = total - echecs.length;

    // Si au moins un succès, on prépare le rafraîchissement à la fermeture
    if (succes > 0) {
        const btnFermer = document.querySelector('#excel-step-result [data-close-modal]');
        if (btnFermer) {
            btnFermer.addEventListener('click', () => window.location.reload(), { once: true });
        }
    }
    const resumeEl = document.getElementById('result-resume');
    if (resumeEl) {
        resumeEl.innerHTML = `
            <p class="text-sm text-layer-foreground">
                <span class="font-bold text-green-600">${succes}</span> inscription(s) réussie(s) sur ${total}.
                ${echecs.length > 0 ? `<span class="font-bold text-red-500">${echecs.length}</span> échec(s).` : ''}
            </p>`;
    }

    const listeEl = document.getElementById('result-echecs');
    if (listeEl) {
        if (echecs.length === 0) {
            listeEl.innerHTML = '';
        } else {
            listeEl.innerHTML = `
                <p class="text-xs font-bold text-red-500 mt-3 mb-1">Étudiants non inscrits :</p>
                <ul class="space-y-1">
                    ${echecs.map(e => `
                        <li class="text-xs bg-red-50 border border-red-200 rounded-lg px-3 py-2">
                            <span class="font-semibold">${e.etudiant.nom} ${e.etudiant.prenom}</span>
                            <span class="text-muted-foreground-2 ml-1">(${e.etudiant.email})</span>
                            — ${e.raison}
                        </li>`).join('')}
                </ul>`;
        }
    }
}

function afficherErreursValidation(erreurs) {
    // Affiche les erreurs de format sous le bouton "Importer"
    let container = document.getElementById('excel-validation-errors');
    if (!container) return;
    container.innerHTML = erreurs.slice(0, 10).map(msg =>
        `<p class="text-xs text-red-500">• ${msg}</p>`
    ).join('') + (erreurs.length > 10 ? `<p class="text-xs text-muted-foreground-2">…et ${erreurs.length - 10} autre(s)</p>` : '');
    container.classList.remove('hidden');
}

function pause(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

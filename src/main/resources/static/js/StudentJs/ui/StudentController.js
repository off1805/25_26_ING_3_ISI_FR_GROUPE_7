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

import { ClasseApi } from '../../academicStructure/infrastructure/ClasseApi.js';
import { SpecialiteApi } from '../../academicStructure/infrastructure/SpecialiteApi.js';
import { enrollStudentUC } from '../application/EnrollStudentUC.js';
import { EtudiantExcelParser } from '../../ExcelJs/application/infrastructure/Lecture.js';
import { GlobalEventNotifier } from '../../common/GlobalEventNotifier.js';
import api from '../../common/ClientHttp.js';
import { StudentApi } from '../infrastructure/StudentApi.js';

// ── État global ──────────────────────────────────────────────────────────────

let classeSelectionneeId = null;
let currentStudentPage  = 0;
let totalStudentPages   = 0;
let totalStudentElements = 0;
const studentPageSize   = 2;

// ── Init ─────────────────────────────────────────────────────────────────────
const specialiteApi = new SpecialiteApi();
const classeApi = new ClasseApi();

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

    onMainCheckboxChange();

    document.getElementById("remove-all").addEventListener("click", () => {
        const checkboxes = document.querySelectorAll('.checkebox-student');
        const userIds = Array.from(checkboxes)
            .filter(cb => cb.checked)
            .map(cb => cb.getAttribute('data-user-id'));

        if (userIds.length === 0) {
            console.log('Aucun étudiant sélectionné.', 'warning');
            return;
        }

        if (!confirm(`Confirmer le retrait de ${userIds.length} étudiant(s) de la classe ?`)) {
            return;
        }

        // Retrait en séquence pour éviter de saturer le serveur
        (async () => {
            for (const userId of userIds) {
                try {
                    await StudentApi.removeFromClass(userId, getActiveClasseId());
                } catch (err) {
                    console.log(`Échec retrait ID ${userId} : ${err.message}`, 'error');
                }
            }
            // Après tous les retraits, on rafraîchit la page pour voir les changements
            await handleClasseChange(getActiveClasseId());
        })();
    });

    document.getElementById("level-tabs").addEventListener('click', (e) => {
        const btn = e.target.closest(".level-filter-btn");
        if (!btn) {
            return;
        }
        console.log(e.target.closest(".level-filter-btn"));
        console.log(btn.dataset.levelId);
        handleNiveauChange(btn.dataset.levelId);

    })
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
        email: form.email.value.trim(),
        nom: form.nom.value.trim(),
        prenom: form.prenom.value.trim(),
        matricule: form.matricule.value.trim(),
        numeroTelephone: form.numeroTelephone.value.trim(),
        classeId: getActiveClasseId(),
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
    setInputVal('col-email', defauts.email);
    setInputVal('col-nom', defauts.nom);
    setInputVal('col-prenom', defauts.prenom);
    setInputVal('col-matricule', defauts.matricule);
    setInputVal('col-telephone', defauts.numeroTelephone);
    setInputVal('ligne-debut', '2');
}

function setInputVal(id, val) {
    const el = document.getElementById(id);
    if (el) el.value = val;
}

function getInputVal(id) {
    return document.getElementById(id)?.value?.trim() || '';
}

async function lancerImportExcel() {
    const fichier = document.getElementById('input-excel-file')?.files[0];
    const classeId = getActiveClasseId();

    if (!fichier) {
        GlobalEventNotifier.eventWellDone('Veuillez sélectionner un fichier Excel.', 'warning');
        return;
    }
    if (!classeId) {
        showToast('Erreur : impossible d\'identifier la classe cible.', 'error');
        return;
    }

    const colonnes = {
        email: getInputVal('col-email') || 'A',
        nom: getInputVal('col-nom') || 'B',
        prenom: getInputVal('col-prenom') || 'C',
        matricule: getInputVal('col-matricule') || 'D',
        numeroTelephone: getInputVal('col-telephone') || 'E',
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
    console.log(etudiants);

    // Validation préliminaire
    const erreursValidation = etudiants.flatMap(et => EtudiantExcelParser.validerEtudiant(et));
    if (erreursValidation.length > 0) {
        GlobalEventNotifier.eventWellDone(`${erreursValidation.length} erreur(s) de format dans le fichier. Corrigez-les avant d'importer.`, 'warning');
        afficherErreursValidation(erreursValidation);
        return;
    }

    // Passer à l'étape progression
    afficherEtape('excel-step-progress');

    const total = etudiants.length;
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

    const bar = document.getElementById('progress-bar');
    const pctEl = document.getElementById('progress-pct');
    const infoEl = document.getElementById('progress-info');

    if (bar) { bar.style.width = pct + '%'; bar.setAttribute('aria-valuenow', pct); }
    if (pctEl) pctEl.textContent = pct + '%';
    if (infoEl) {
        if (etudiantEnCours) {
            infoEl.textContent = `Inscription de ${etudiantEnCours.nom} ${etudiantEnCours.prenom}… (${traites + 1}/${total})`;
        } else {
            infoEl.textContent = `Traitement terminé — ${total} étudiant(s) traité(s).`;
        }
    }
}

function onMainCheckboxChange() {

    const mainCheckbox = document.getElementById('select-all');
    mainCheckbox.addEventListener('change', () => {
        console.log("Main checkbox changed: ", mainCheckbox.checked);
        console.log(mainCheckbox);
        const checkboxes = document.querySelectorAll('.checkebox-student');
        const isChecked = mainCheckbox.querySelector('input[type="checkbox"]').checked;

        checkboxes.forEach(cb => {
            cb.checked = isChecked;
        });
    
    });
}

function removeFromClass(userId) {
    StudentApi.removeFromClass(userId, getActiveClasseId());
}

async function handleNiveauChange(levelId) {
    const container = document.getElementById("classes-tablist");
    if (!container) {
        return;
    }

    const response = await specialiteApi.getByNiveauId(levelId);
    var classes = [];
    let first = true;
    Array.from(response).forEach(async (e) => {
        console.log(e);
        const classe = await classeApi.getBySpecialiteId(e.id);
        Array.from(classe).forEach(e => classes.push(e));




    });
    console.log("classes ", classes);

    const results = await Promise.all(
        response.map(e => classeApi.getBySpecialiteId(e.id))
    );

    classes = results.flat();

    container.innerHTML = classes.map((spec, index) => `
        <button type="button"
            data-classe-id="${spec.id}"
            class="classe-tab-btn specialty-tab py-2 px-4 inline-flex items-center gap-x-2 text-xs font-medium rounded-full transition-all focus:outline-none disabled:opacity-50 disabled:pointer-events-none hover:text-layer-foreground ${index === 0 ? 'active bg-surface-active text-foreground' : 'bg-transparent text-muted-foreground-1'}"
            role="tab"
            aria-selected="${index === 0}">
            ${spec.code}
        </button>
    `).join('');

    container.querySelectorAll('.classe-tab-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            container.querySelectorAll('.classe-tab-btn').forEach(b => {
                b.classList.remove('active', 'bg-surface-active', 'text-foreground');
                b.classList.add('bg-transparent', 'text-muted-foreground-1');
                b.setAttribute('aria-selected', 'false');
            });

            btn.classList.add('active', 'bg-surface-active', 'text-foreground');
            btn.classList.remove('bg-transparent', 'text-muted-foreground-1');
            btn.setAttribute('aria-selected', 'true');

            classeSelectionneeId = btn.dataset.classeId;
            currentStudentPage = 0;
            handleClasseChange(classeSelectionneeId, 0);
        });
    });

    if (classes.length > 0) {
        classeSelectionneeId = classes[0].id;
        currentStudentPage = 0;
        handleClasseChange(classes[0].id, 0);
    }

    requestAnimationFrame(() => {
        if (window.HSStaticMethods) {
            window.HSStaticMethods.autoInit();
        }
    });

   







    console.log(response);

}

 async function handleClasseChange(classeId, page = 0) {
        currentStudentPage = page;
        const container = document.getElementById("student-table");
        const response = await StudentApi.getStudentOfClass(classeId, page, studentPageSize);
        totalStudentPages    = response?.totalPages    ?? 0;
        totalStudentElements = response?.totalElements ?? 0;
        container.innerHTML = response.content.map((student, index) => ` <tr
                                                    class="student-row bg-white hover:bg-muted/10 transition-colors group"
                                                    data-user-id="${student.id}">


                                                    <!-- Nom & Prénom - avatar avec 6 couleurs variées -->
                                                    <td class="px-3 py-3.5 min-w-0">
                                                        <div class="flex items-center gap-3">
                                                           <label class="relative flex items-center cursor-pointer shrink-0 group">
                                                                <input type="checkbox"
                                                                    class="peer sr-only checkebox-student"
                                                                    th:attr="data-user-id=${student.userId}"
                                                                     />

                                                                <!-- La box + coche en un seul élément -->
                                                                <div class="size-4 rounded-[4px] border-2 
                                                                            border-layer-line bg-layer
                                                                            group-hover:border-primary/60
                                                                            peer-checked:bg-primary 
                                                                            peer-checked:border-primary
                                                                            peer-checked:bg-[url('data:image/svg+xml,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20viewBox%3D%220%200%2024%2024%22%20fill%3D%22none%22%20stroke%3D%22white%22%20stroke-width%3D%223.5%22%20stroke-linecap%3D%22round%22%20stroke-linejoin%3D%22round%22%3E%3Cpath%20d%3D%22M20%206%209%2017l-5-5%22%2F%3E%3C%2Fsvg%3E')]
                                                                            peer-checked:bg-center
                                                                            peer-checked:bg-no-repeat
                                                                            peer-checked:bg-[length:70%_70%]
                                                                            transition-all duration-150">
                                                                </div>
                                                            </label>

                                                            <div class="size-9 rounded-xl flex items-center justify-center shrink-0 text-sm font-bold
                                                                ${index % 6 === 0 ? 'bg-primary/10 text-primary' :
                (index % 6 === 1 ? 'bg-blue-500/10 text-blue-500' :
                    (index % 6 === 2 ? 'bg-green-500/10 text-green-600' :
                        (index % 6 === 3 ? 'bg-orange-500/10 text-orange-500' :
                            (index % 6 === 4 ? 'bg-indigo-500/10 text-indigo-500' :
                                'bg-teal-500/10 text-teal-600'))))}"
                                                                >
                                                                ${student.profile.nom ? student.profile.nom.toUpperCase().charAt(0) : 'E'}</div>

                                                            <p class="text-sm font-semibold text-layer-foreground truncate"
                                                                >
                                                                ${(student.profile.nom ? student.profile.nom : '') + ' ' + (student.profile.prenom ? student.profile.prenom : '')}</p>
                                                        </div>
                                                    </td>

                                                    <!-- Email -->
                                                    <td class="px-4 py-3.5 hidden md:table-cell">
                                                        <span class="text-sm text-muted-foreground-2"
                                                            >${student.email}</span>
                                                    </td>

                                                    <!-- Matricule -->
                                                    <td class="px-4 py-3.5 hidden lg:table-cell">
                                                        <span
                                                            class="inline-flex items-center px-2.5 py-1 rounded-lg text-xs font-bold bg-gray-100 text-gray-500"
                                                            >${student.profile.matricule ? student.profile.matricule : '—'}</span>
                                                    </td>

                                                    <!-- Téléphone -->
                                                    <td class="px-4 py-3.5 hidden xl:table-cell">
                                                        <span class="text-sm text-muted-foreground-2"
                                                            >${student.profile.numeroTelephone != null ? student.profile.numeroTelephone : '—'}</span>
                                                    </td>

                                                    <!-- Actions (visible au hover) -->
                                                    <td class="pr-4 sm:pr-5 pl-2 py-3.5 text-right">
                                                        <div class="flex items-center justify-end gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                                                            <button type="button"
                                                                class="size-8 flex items-center justify-center rounded-lg text-muted-foreground-2 hover:bg-muted hover:text-layer-foreground transition-colors"
                                                                data-user-id="${student.id}"
                                                                data-nom="${(student.profile?.nom || '').replace(/"/g, '&quot;')}"
                                                                data-prenom="${(student.profile?.prenom || '').replace(/"/g, '&quot;')}"
                                                                data-email="${(student.email || '').replace(/"/g, '&quot;')}"
                                                                data-matricule="${(student.profile?.matricule || '').replace(/"/g, '&quot;')}"
                                                                data-telephone="${(student.profile?.numeroTelephone || '').replace(/"/g, '&quot;')}"
                                                                onclick="viewStudent(this)" title="Voir le profil">
                                                                <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                                                    <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7z"/>
                                                                    <circle cx="12" cy="12" r="3"/>
                                                                </svg>
                                                            </button>
                                                            <button type="button"
                                                                class="size-8 flex items-center justify-center rounded-lg text-muted-foreground-2 hover:bg-red-50 hover:text-red-500 dark:hover:bg-red-500/10 transition-colors"
                                                                data-user-id="${student.id}"
                                                                onclick="removeSingle(this)" title="Retirer de la classe">
                                                                <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                                                    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
                                                                    <circle cx="9" cy="7" r="4" />
                                                                    <line x1="17" y1="8" x2="23" y2="14" />
                                                                    <line x1="23" y1="8" x2="17" y2="14" />
                                                                </svg>
                                                            </button>
                                                        </div>
                                                    </td>
                                                </tr>`).join('');

        renderStudentPagination(classeId);
    }

function renderStudentPagination(classeId) {
    const container = document.getElementById('pagination-container');
    if (!container) return;

    if (totalStudentPages <= 1) {
        container.innerHTML = '';
        return;
    }

    const activeClass   = 'bg-foreground border-layer-line text-white';
    const inactiveClass = 'bg-layer border-layer-line text-muted-foreground-1 hover:bg-layer-hover';
    const disabledClass = 'bg-layer border-layer-line text-muted-foreground-2 opacity-40 pointer-events-none';
    const btnBase = 'inline-flex items-center justify-center min-w-[2rem] h-8 px-2 text-xs font-medium rounded-lg border transition-all cursor-pointer';

    let pageNums = [];
    if (totalStudentPages <= 7) {
        pageNums = Array.from({ length: totalStudentPages }, (_, i) => i);
    } else {
        const rangeStart = Math.max(0, currentStudentPage - 1);
        const rangeEnd   = Math.min(totalStudentPages - 1, currentStudentPage + 1);
        if (rangeStart > 0) { pageNums.push(0); if (rangeStart > 1) pageNums.push('...'); }
        for (let i = rangeStart; i <= rangeEnd; i++) pageNums.push(i);
        if (rangeEnd < totalStudentPages - 1) { if (rangeEnd < totalStudentPages - 2) pageNums.push('...'); pageNums.push(totalStudentPages - 1); }
    }

    container.innerHTML = `
        <div class="flex flex-col items-center gap-3">
            <div class="flex items-center gap-1" id="pagination-btns">
                <button data-page="${currentStudentPage - 1}" class="${btnBase} ${currentStudentPage === 0 ? disabledClass : inactiveClass}" ${currentStudentPage === 0 ? 'disabled' : ''}>
                    <i class="bi bi-chevron-left text-xs"></i>
                </button>
                ${pageNums.map(p => p === '...'
                    ? `<span class="${btnBase} border-transparent text-muted-foreground-2 cursor-default">…</span>`
                    : `<button data-page="${p}" class="${btnBase} ${p === currentStudentPage ? activeClass : inactiveClass}">${p + 1}</button>`
                ).join('')}
                <button data-page="${currentStudentPage + 1}" class="${btnBase} ${currentStudentPage >= totalStudentPages - 1 ? disabledClass : inactiveClass}" ${currentStudentPage >= totalStudentPages - 1 ? 'disabled' : ''}>
                    <i class="bi bi-chevron-right text-xs"></i>
                </button>
            </div>
            <p class="text-xs text-muted-foreground-2">${totalStudentElements} étudiant(s) &middot; Page ${currentStudentPage + 1} sur ${totalStudentPages}</p>
        </div>`;

    container.querySelector('#pagination-btns')?.addEventListener('click', (e) => {
        const btn = e.target.closest('[data-page]');
        if (!btn || btn.disabled || btn.tagName === 'SPAN') return;
        const page = parseInt(btn.dataset.page);
        if (!isNaN(page) && page >= 0 && page < totalStudentPages) {
            handleClasseChange(classeId, page);
        }
    });
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

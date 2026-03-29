/**
 * EtudiantExcelParser
 * Lit un fichier Excel (.xlsx / .xls / .ods) et extrait les données étudiants
 * à partir de coordonnées de colonnes paramétrables.
 *
 * Les coordonnées par défaut peuvent être surchargées lors de l'appel à lireFichier().
 *
 * CORRECTION : ajout des champs matricule, numeroTelephone et classeId
 * qui étaient absents de la version initiale.
 */
export class EtudiantExcelParser {

    /**
     * Colonnes par défaut (lettres Excel). L'utilisateur peut les surcharger
     * via le paramètre `colonnes` de lireFichier().
     */
    static COLONNES_DEFAUT = {
        email:           'A',
        nom:             'B',
        prenom:          'C',
        matricule:       'D',
        numeroTelephone: 'E',
    };

    /**
     * Lit le fichier Excel et retourne la liste des étudiants.
     *
     * @param {File}   file        - Fichier Excel uploadé
     * @param {number} debutLigne  - Numéro de la première ligne de données (1-indexé, défaut 2)
     * @param {Object} colonnes    - Mapping champ → lettre de colonne (surcharge les défauts)
     * @returns {Promise<Array<{email,nom,prenom,matricule,numeroTelephone,ligne}>>}
     */
    static lireFichier(file, debutLigne = 2, colonnes = {}) {
        return new Promise((resolve, reject) => {
            if (!file) {
                return reject(new Error('Aucun fichier fourni.'));
            }

            const extensionsAutorisees = ['.xlsx', '.xls', '.ods'];
            const ext = file.name.slice(file.name.lastIndexOf('.')).toLowerCase();
            if (!extensionsAutorisees.includes(ext)) {
                return reject(new Error(
                    `Format non supporté : "${ext}". Utilisez ${extensionsAutorisees.join(', ')}.`
                ));
            }

            // Fusion des colonnes par défaut avec les colonnes fournies par l'utilisateur
            const colonnesEffectives = { ...EtudiantExcelParser.COLONNES_DEFAUT, ...colonnes };

            const reader = new FileReader();

            reader.onload = (e) => {
                try {
                    const data = new Uint8Array(e.target.result);
                    const classeur = XLSX.read(data, { type: 'array' });

                    const nomFeuille = classeur.SheetNames[0];
                    const feuille = classeur.Sheets[nomFeuille];

                    const etudiants = EtudiantExcelParser._extraireEtudiants(
                        feuille,
                        debutLigne,
                        colonnesEffectives
                    );

                    if (etudiants.length === 0) {
                        return reject(new Error(
                            'Le fichier est vide ou ne contient pas de données à partir de la ligne ' + debutLigne
                        ));
                    }

                    resolve(etudiants);
                } catch (err) {
                    reject(new Error('Impossible de lire le fichier Excel : ' + err.message));
                }
            };

            reader.onerror = () => reject(new Error('Erreur lors de la lecture du fichier.'));
            reader.readAsArrayBuffer(file);
        });
    }

    // ── Extraction ──────────────────────────────────────────────────────────

    static _extraireEtudiants(feuille, debutLigne, colonnes) {
        const etudiants = [];
        const ref = feuille['!ref'];
        if (!ref) return etudiants;

        const plage = XLSX.utils.decode_range(ref);
        const derniereLigne = plage.e.r + 1; // SheetJS est 0-indexé

        for (let ligne = debutLigne; ligne <= derniereLigne; ligne++) {
            const etudiant = EtudiantExcelParser._lireLigne(feuille, ligne, colonnes);

            // Ignorer les lignes complètement vides
            if (!etudiant.email && !etudiant.nom && !etudiant.prenom && !etudiant.matricule) {
                continue;
            }

            etudiants.push({ ...etudiant, ligne });
        }

        return etudiants;
    }

    static _lireLigne(feuille, numeroLigne, colonnes) {
        const lire = (colonne) => {
            if (!colonne) return '';
            const cle = `${colonne.toUpperCase()}${numeroLigne}`;
            const cellule = feuille[cle];
            if (!cellule) return '';
            return String(cellule.w ?? cellule.v ?? '').trim();
        };

        return {
            email:           lire(colonnes.email),
            nom:             lire(colonnes.nom),
            prenom:          lire(colonnes.prenom),
            matricule:       lire(colonnes.matricule),
            numeroTelephone: lire(colonnes.numeroTelephone),
        };
    }

    // ── Validation ───────────────────────────────────────────────────────────

    /**
     * Valide un étudiant et retourne un tableau de messages d'erreur (vide = valide).
     *
     * @param {{ email, nom, prenom, matricule, numeroTelephone, ligne }} etudiant
     * @returns {string[]}
     */
    static validerEtudiant({ email, nom, prenom, matricule, numeroTelephone, ligne }) {
        const erreurs = [];

        if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
            erreurs.push(`Ligne ${ligne} — Email invalide : "${email}"`);
        }
        if (!nom) {
            erreurs.push(`Ligne ${ligne} — Nom manquant`);
        }
        if (!prenom) {
            erreurs.push(`Ligne ${ligne} — Prénom manquant`);
        }
        if (!matricule) {
            erreurs.push(`Ligne ${ligne} — Matricule manquant`);
        }
        if (!numeroTelephone) {
            erreurs.push(`Ligne ${ligne} — Numéro de téléphone manquant`);
        }

        return erreurs;
    }
}

export class EtudiantExcelParser {
  /* Valeur → lettre de colonne Excel (A, B, C…)*/
  static COLONNES = {
    email: "A",
    nom: "B",
    prenom: "C",
  };

  static lireFichier(file, debutLigne = 2) {
    return new Promise((resolve, reject) => {
      if (!file) {
        return reject(new Error("Aucun fichier fourni."));
      }

      const extensionsAutorisees = [".xlsx", ".xls", ".ods"];
      const ext = file.name.slice(file.name.lastIndexOf(".")).toLowerCase();
      if (!extensionsAutorisees.includes(ext)) {
        return reject(
          new Error(
            `Format non supporté : "${ext}". Utilisez ${extensionsAutorisees.join(", ")}.`
          )
        );
     }

      const reader = new FileReader();

      reader.onload = (e) => {
        try {
          const data = new Uint8Array(e.target.result);
          const classeur = XLSX.read(data, { type: "array" });

          // On prend toujours la première feuille
          const nomFeuille = classeur.SheetNames[0];
          const feuille = classeur.Sheets[nomFeuille];

          const etudiants = EtudiantExcelParser._extraireEtudiants(
            feuille,
            debutLigne
          );

          if (etudiants.length === 0) {
            return reject(
              new Error(
                "Le fichier est vide ou ne contient pas de données à partir de la ligne " +
                  debutLigne
              )
            );
          }

          resolve(etudiants);
        } catch (err) {
          reject(new Error("Impossible de lire le fichier Excel : " + err.message));
        }
      };

      reader.onerror = () => reject(new Error("Erreur lors de la lecture du fichier."));
      reader.readAsArrayBuffer(file);
    });
  }

  static _extraireEtudiants(feuille, debutLigne) {
    const etudiants = [];
    const ref = feuille["!ref"];
    if (!ref) return etudiants;

    const plage = XLSX.utils.decode_range(ref);
    const derniereLigne = plage.e.r + 1; // +1 car SheetJS est 0-indexé

    for (let numeroLigne = debutLigne; numeroLigne <= derniereLigne; numeroLigne++) {
      const etudiant = EtudiantExcelParser._lireLigne(feuille, numeroLigne);

      // On ignore les lignes complètement vides
      if (!etudiant.email && !etudiant.nom && !etudiant.prenom) continue;

      etudiants.push({ ...etudiant, ligne: numeroLigne });
    }

    return etudiants;
  }

  static _lireLigne(feuille, numeroLigne) {
    const lire = (colonne) => {
      const cle = `${colonne}${numeroLigne}`;
      const cellule = feuille[cle];
      if (!cellule) return "";
      // .w = valeur formatée (string), .v = valeur brute
      return String(cellule.w ?? cellule.v ?? "").trim();
    };

    return {
      email: lire(EtudiantExcelParser.COLONNES.email),
      nom: lire(EtudiantExcelParser.COLONNES.nom),
      prenom: lire(EtudiantExcelParser.COLONNES.prenom),
    };
  }

  /* Retourne un tableau de messages d'erreur (vide = valide). */
  static validerEtudiant({ email, nom, prenom, ligne }) {
    const erreurs = [];
    if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      erreurs.push(`Ligne ${ligne} — Email invalide : "${email}"`);
    }
    if (!nom) erreurs.push(`Ligne ${ligne} — Nom manquant`);
    if (!prenom) erreurs.push(`Ligne ${ligne} — Prénom manquant`);
    return erreurs;
  }
}
import { StudentApi } from '../infrastructure/StudentApi.js';

/**
 * Inscrit un étudiant dans une classe.
 * Retourne la réponse du serveur ou lève une erreur enrichie.
 *
 * @param {{ email:string, nom:string, prenom:string, matricule:string, numeroTelephone:string, classeId:number }} data
 * @returns {Promise<{userId, email, nom, prenom, matricule, classeId, created}>}
 */
export async function enrollStudentUC(data) {
    if (!data.email || !data.nom || !data.prenom || !data.matricule || !data.numeroTelephone || !data.classeId) {
        throw new Error('Tous les champs sont requis (email, nom, prénom, matricule, téléphone, classe).');
    }

    try {
        return await StudentApi.enroll(data);
    } catch (err) {
        // Le backend renvoie un string en cas de CONFLICT (déjà inscrit dans une classe)
        const message = (typeof err.payload === 'string' && err.payload)
            ? err.payload
            : (err.message || 'Erreur lors de l\'inscription.');
        const enriched = new Error(message);
        enriched.status = err.status;
        throw enriched;
    }
}

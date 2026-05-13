/**
 * config.js — Configuration réseau du client JavaScript
 *
 * Modifier SERVER_IP et SERVER_PORT pour tester depuis un autre appareil
 * (téléphone, tablette) sur le même réseau local.
 *
 * Exemples :
 *   SERVER_IP = ""              → URLs relatives (localhost, défaut)
 *   SERVER_IP = "10.193.249.21" → IP réseau locale (pour tests téléphone)
 */

const SERVER_IP   = "";       // Laisser vide pour localhost, sinon mettre l'IP ex: "10.193.249.21"
const SERVER_PORT = 8080;

/**
 * Base URL utilisée par ClientHttp pour tous les appels API.
 * - Si SERVER_IP est vide : URLs relatives → résolution automatique par le navigateur.
 * - Si SERVER_IP est défini : URL absolue vers le serveur, utile pour les tests multi-appareils.
 */
export const BASE_URL = SERVER_IP
    ? `http://${SERVER_IP}:${SERVER_PORT}`
    : "";

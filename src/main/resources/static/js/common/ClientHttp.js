import { TokenService } from "./application/TokenService.js";

class HttpClient {

    static REFRESH_TOKEN_ENDPOINT = "/refresh";

    constructor(baseURL) {
        this.baseURL = baseURL;
    }

    async _buildHeaders(customHeaders = {}) {
        const token = await TokenService.getToken();
        return {
            "Content-Type": "application/json",
            ...(token ? { Authorization: `Bearer ${token}` } : {}),
            ...customHeaders
        };
    }

    async _parseResponse(response) {
        if (response.status === 204) {
            return null;
        }

        const contentType = response.headers.get("content-type");
        const isJson = contentType?.includes("application/json");
        return isJson ? await response.json() : await response.text();
    }

    async _request(method, endpoint, { body, headers = {}, retry = true } = {}) {
        const options = {
            method,
            headers: await this._buildHeaders(headers),
            ...(body !== undefined ? { body: JSON.stringify(body) } : {})
        };

        const response = await fetch(this.baseURL + endpoint, options);

        //token invalide/expiré, tentative de refresh.
        if (response.status === 401 && retry) {
            const refreshed = await this.refreshToken();
            if (refreshed) {
                return await this._request(method, endpoint, { body, headers, retry: false });
            }
            window.location.href = "/login";
            return null;
        }

        if (response.status === 403) {
            throw new Error("Vous n'avez pas les droits nécessaires.");
        }

        if (!response.ok) {
            const errorPayload = await this._parseResponse(response).catch(() => null);
            const error = new Error(`HTTP ${response.status}`);
            error.status = response.status;
            error.payload = errorPayload;
            throw error;
        }
        return await this._parseResponse(response);
    }

    get(endpoint, headers = {}) {
        return this._request("GET", endpoint, { headers });
    }

    post(endpoint, body = {}, headers = {}) {
        return this._request("POST", endpoint, { body, headers });
    }

    put(endpoint, body = {}, headers = {}) {
        return this._request("PUT", endpoint, { body, headers });
    }

    delete(endpoint, headers = {}) {
        return this._request("DELETE", endpoint, { headers });
    }

    /**
     * Cette méthode permet de rafraîchir le token d'authentification.
     * @returns {Promise<boolean>}
     */
    async refreshToken() {
        const refreshToken = await TokenService.getRefreshToken();

        if (!refreshToken) {
            return false;
        }

        const response = await fetch(this.baseURL + HttpClient.REFRESH_TOKEN_ENDPOINT, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ refreshToken })
        });

        if (!response.ok) {
            return false;
        }

        const newTokens = await response.json();
        await TokenService.setToken(newTokens.token);
        await TokenService.setRefreshToken(newTokens.refreshToken);
        return true;
    }
}

const BASE_URL = "http://localhost:8080";

const api = new HttpClient(BASE_URL);

// Exportation de l'instance
export default api;

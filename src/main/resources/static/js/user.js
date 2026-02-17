
class UserService {
    constructor(baseUrl) {
        this.baseUrl = baseUrl;
    }


    async _handleResponse(response) {
        const contentType = response.headers.get("content-type");
        let data = null;

        if (contentType && contentType.includes("application/json")) {
            data = await response.json();
        }

        if (!response.ok) {

            const error = {
                status: response.status,
                message: data?.message || "Une erreur est survenue",
                details: data
            };
            throw error;
        }

        return data;
    }


    async createUser(email, password,idPermissions,idRole) {
        const response = await fetch(`${this.baseUrl}/createUser`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password,idPermissions,idRole })
        });
        return this._handleResponse(response);
    }


    async updateUser(id, updatedFields) {
        const response = await fetch(`${this.baseUrl}/updateUser`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ id, ...updatedFields })
        });
        return this._handleResponse(response);
    }


    async deleteUser(id) {
        const response = await fetch(`${this.baseUrl}/`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ id })
        });
        return this._handleResponse(response);
    }
}
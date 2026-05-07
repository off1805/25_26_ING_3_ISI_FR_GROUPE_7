import { ProfileApi } from '../infrastructure/ProfileApi.js';

const profileApi = new ProfileApi();

class ProfileController {
    constructor() {
        this._profileId = null;
        this.init();
    }

    async init() {
        await this._loadProfile();
        this._bindPhotoUpload();
    }

    async _loadProfile() {
        try {
            const data = await profileApi.getMyProfile();
            this._profileId = data.profileId;
            this._render(data);
        } catch (e) {
            if (e.status === 401) {
                window.location.href = '/login';
            } else {
                console.error('Erreur chargement profil', e);
            }
        }
    }

    _render(data) {
        const img      = document.getElementById('avatar-img');
        const initiale = document.getElementById('avatar-initiale');

        if (data.photoUrl) {
            img.src = data.photoUrl;
            img.classList.remove('hidden');
            initiale.classList.add('hidden');
        } else {
            initiale.textContent = data.nom ? data.nom.charAt(0).toUpperCase() : 'U';
        }

        document.getElementById('profile-full-name').textContent =
            [data.nom, data.prenom].filter(Boolean).join(' ') || 'Nom Prénom';
        document.getElementById('profile-email').textContent       = data.email      ?? '';
        document.getElementById('profile-role-badge').textContent  = data.role       ?? '';
        document.getElementById('profile-nom').textContent         = data.nom        ?? '—';
        document.getElementById('profile-prenom').textContent      = data.prenom     ?? '—';
        document.getElementById('profile-email-field').textContent = data.email      ?? '—';
        document.getElementById('profile-matricule').textContent   = data.matricule  ?? '—';
        document.getElementById('profile-telephone').textContent   = data.telephone  ?? '—';
        document.getElementById('profile-role-field').textContent  = data.role       ?? '—';
    }

    _bindPhotoUpload() {
        document.getElementById('photo-input')?.addEventListener('change', async (e) => {
            const file = e.target.files[0];
            if (!file || !this._profileId) return;

            const statusEl = document.getElementById('upload-status');
            statusEl.textContent = 'Envoi en cours…';
            statusEl.classList.remove('hidden', 'text-red-500', 'text-green-600');
            statusEl.classList.add('text-muted-foreground-2');

            try {
                const { photoUrl } = await profileApi.uploadPhoto(this._profileId, file);

                const img      = document.getElementById('avatar-img');
                const initiale = document.getElementById('avatar-initiale');
                img.src = photoUrl + '?t=' + Date.now();
                img.classList.remove('hidden');
                if (initiale) initiale.classList.add('hidden');

                statusEl.textContent = 'Photo mise à jour avec succès.';
                statusEl.classList.replace('text-muted-foreground-2', 'text-green-600');
            } catch {
                statusEl.textContent = 'Erreur lors de l\'envoi. Réessayez.';
                statusEl.classList.replace('text-muted-foreground-2', 'text-red-500');
            }
        });
    }
}

document.addEventListener('DOMContentLoaded', () => new ProfileController());

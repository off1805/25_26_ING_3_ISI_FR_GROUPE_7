export class LoadClassSubjectsUC {
    constructor(classeApi, ueApi) {
        this._classeApi = classeApi;
        this._ueApi     = ueApi;
    }

    async execute(classId) {
        const classe = await this._classeApi.getById(classId);
        const specialiteId = classe?.specialiteId;
        if (!specialiteId) return [];

        const [uePage, teachersPage] = await Promise.all([
            this._ueApi.searchUes(`?specialiteId=${encodeURIComponent(specialiteId)}&deleted=false&size=200`),
            this._ueApi.getTeachers(),
        ]);

        const ues = uePage?.content || [];

        // Map profileId → {id, name, initials}
        const teacherMap = new Map();
        (teachersPage?.content || []).forEach(t => {
            if (!t.profile?.id) return;
            const name    = `${t.profile.prenom || ''} ${t.profile.nom || ''}`.trim() || t.email || 'Enseignant';
            const initials = `${(t.profile.prenom?.[0] || '').toUpperCase()}${(t.profile.nom?.[0] || '').toUpperCase()}` || 'EN';
            teacherMap.set(t.profile.id, { id: t.profile.id, name, initials });
        });

        return ues.map(ue => ({
            id:           ue.id,
            name:         ue.libelle,
            code:         ue.code,
            defaultColor: (typeof ue.couleur === 'string' && ue.couleur.startsWith('#') && ue.couleur.length === 7)
                              ? ue.couleur
                              : '#3b82f6',
            teachers:     (ue.enseignantIds || [])
                              .map(id => teacherMap.get(id))
                              .filter(Boolean),
        }));
    }
}

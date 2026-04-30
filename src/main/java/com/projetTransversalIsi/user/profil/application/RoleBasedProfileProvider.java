package com.projetTransversalIsi.user.profil.application;

import com.projetTransversalIsi.security.domain.EnumRole;
import com.projetTransversalIsi.user.profil.domain.*;

import java.util.Map;

public class RoleBasedProfileProvider implements ProfileProvider {

    private static final Map<String, Profile> TEMPLATES = Map.of(
            EnumRole.STUDENT.name(),     new StudentProfile(),
            EnumRole.TEACHER.name(),     new TeacherProfile(),
            EnumRole.ADMIN.name(),       new AdminProfile(),
            EnumRole.AP.name(),          new APProfile(),
            EnumRole.SURVEILLANT.name(), new SurveillantProfile()
    );

    @Override
    public boolean supports(String roleName) {
        return TEMPLATES.containsKey(roleName);
    }

    @Override
    public Profile create(ProfilCreationDTO dto) {
        // Cette implémentation n'est pas appelée directement.
        // La stratégie appelle supports() puis selectProfileFor() qui invoque create(roleName, dto).
        throw new UnsupportedOperationException(
                "Utiliser create(roleName, dto) via DefaultProfileSelectionStrategy");
    }

    /**
     * Crée un profil du bon type à partir du rôle et des données du DTO.
     * Délègue à la méthode copyFrom() de chaque sous-classe de Profile.
     */
    public Profile create(String roleName, ProfilCreationDTO dto) {
        Profile template = TEMPLATES.get(roleName);
        if (template == null) {
            throw new IllegalArgumentException("Aucun profil configuré pour le rôle : " + roleName);
        }
        return template;
    }
}

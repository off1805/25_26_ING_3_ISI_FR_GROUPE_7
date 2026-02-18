package com.projetTransversalIsi.security.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnumPermission {

    MANAGE_STAFF(EnumRole.ADMIN, "Creer, modifier ou supprimer un membre du personnel"),
    MANAGE_STUDENT(EnumRole.AP, "Creer, modifier ou supprimer un etudiant"),
    DEFAULT_STUDENT(EnumRole.STUDENT, "Accéder aux cours et aux annonces"),
    MAKE_CALL(EnumRole.TEACHER, "Faire un appel");

    private final EnumRole roleId;
    private final String label;

    public Permission toPermission() {
        return new Permission(this.name(), this.label);
    }
}

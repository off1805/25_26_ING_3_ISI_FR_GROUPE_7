package com.projetTransversalIsi.security.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnumPermission {

    // admin
    CREATE_PERSONNEL(EnumRole.ADMIN,"Ajouter des membres du personnel."),
    ;
    private final EnumRole roleId;
    private final String label;

    public Permission toPermission(){
        return new Permission(this.name(),this.label);
    }
}

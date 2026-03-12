package com.projetTransversalIsi.user.dto;

import com.projetTransversalIsi.security.domain.Permission;
import lombok.Data;

import java.util.Set;


public record PermissionResponseDTO(String id,String label) {
    public static PermissionResponseDTO fromPermissionDomain(Permission perm){
        return new PermissionResponseDTO(
                perm.getName(),
                perm.getLabel()
        );
    }
}

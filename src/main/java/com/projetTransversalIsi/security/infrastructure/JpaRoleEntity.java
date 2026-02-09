package com.projetTransversalIsi.security.infrastructure;

import com.projetTransversalIsi.user.infrastructure.JpaUserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name="role")
public class JpaRoleEntity {
    @Id
    private String name;

    @OneToMany(mappedBy = "role",fetch = FetchType.LAZY)
    private List<JpaUserEntity> users= new ArrayList<>();

    @ManyToMany(mappedBy = "roles",fetch = FetchType.LAZY)
    private Set<JpaPermissionEntity> permissions = new HashSet<>();

    public void setPermissions(Set<JpaPermissionEntity> perm) {
        this.permissions= perm;
        perm.forEach(permission->permission.addRole(this));
    }
}

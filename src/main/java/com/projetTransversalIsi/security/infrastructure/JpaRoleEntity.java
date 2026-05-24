package com.projetTransversalIsi.security.infrastructure;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name="role")
public class JpaRoleEntity {
    @Id
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "permission_role",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<JpaPermissionEntity> permissions = new HashSet<>();
}

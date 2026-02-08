package com.projetTransversalIsi.security.infrastructure;
import com.projetTransversalIsi.user.infrastructure.JpaUserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;


@Entity
@Table(name= "permission")
@Setter @Getter
public class JpaPermissionEntity {
    @Id
    private String name;

    @Column(nullable = true)
    private String label;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "permission_role",
            joinColumns = @JoinColumn(name = "permission_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<JpaRoleEntity> roles =new HashSet<>();

    @ManyToMany(mappedBy = "permissions",fetch = FetchType.LAZY)
    private List<JpaUserEntity> users =new ArrayList<>();

    public void addRole(JpaRoleEntity role){
        this.roles.add(role);
    }
}

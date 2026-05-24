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

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<JpaRoleEntity> roles = new HashSet<>();

    @ManyToMany(mappedBy = "permissions",fetch = FetchType.LAZY)
    private List<JpaUserEntity> users =new ArrayList<>();
}

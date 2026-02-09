package com.projetTransversalIsi.user.infrastructure;

import com.projetTransversalIsi.profil.infrastructure.JpaProfileEntity;
import com.projetTransversalIsi.security.infrastructure.JpaPermissionEntity;
import com.projetTransversalIsi.security.infrastructure.JpaRoleEntity;
import com.projetTransversalIsi.user.domain.UserStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;


@Data
@Entity
@Table(name="user")
public class JpaUserEntity {

    @Override
    public String toString(){
        return id+" status: "+status+" permissions: "+permissions;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @PrePersist
    public void applyDefaultStatus(){
        if(status==null){
            status=UserStatus.ACTIVE;
        }
    }

    @Column(nullable = false)
    private String password;

    @Column(unique = true,nullable = false)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="role_id")
    private JpaRoleEntity role;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name= "user_permission",
            joinColumns = @JoinColumn(name= "user_id"),
            inverseJoinColumns = @JoinColumn(name= "permission_id")
    )
    private Set<JpaPermissionEntity> permissions;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "profile_id",referencedColumnName = "id")
    private JpaProfileEntity profile = null;
}

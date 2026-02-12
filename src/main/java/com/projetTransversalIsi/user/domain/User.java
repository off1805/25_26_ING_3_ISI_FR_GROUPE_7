package com.projetTransversalIsi.user.domain;

import com.projetTransversalIsi.profil.domain.Profile;
import com.projetTransversalIsi.security.domain.Permission;
import com.projetTransversalIsi.security.domain.Role;
import java.util.Set;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class User {
    private Long id;
    private UserStatus status;
    private String email;
    private String password;

    private Role role;
    private Set<String> idPermissions;


    public User(String email,Role role,UserStatus status, Long id){
        this.email=email;
        this.status=status;
        this.role=role;
        this.id=id;
    }



    public User(String email,Role role){
        this.email=email;
        this.status=UserStatus.ACTIVE;
        this.role=role;
    }

    public boolean isActive(){
        return this.status==UserStatus.ACTIVE;
    }
}

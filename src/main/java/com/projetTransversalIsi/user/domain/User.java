package com.projetTransversalIsi.user.domain;

import com.projetTransversalIsi.security.domain.Permission;
import com.projetTransversalIsi.security.domain.Role;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class User {
    private Long id;
    private UserStatus status;
    private String email;
    private String password;
    private Long profileId;

    private Role role;
    private Set<Permission> permissions;
    private boolean deleted= false;
    private LocalDateTime deletedAt;



    public User(String email,Role role,UserStatus status, Long id,boolean deleted , LocalDateTime deletedAt){
        this.email=email;
        this.status=status;
        this.role=role;
        this.id=id;
        this.deleted=deleted;
        this.deletedAt=deletedAt;
    }



    public User(String email,Role role){
        this.email=email;
        this.status=UserStatus.ACTIVE;
        this.role=role;
        this.deleted=false;
    }


    public boolean isActive(){
        return this.status==UserStatus.ACTIVE;
    }

    public void delete(){
        this.deleted = true;
        this.deletedAt= LocalDateTime.now();
    }

    public void restore(){
        this.deleted=false;
        this.deletedAt=null;
    }


}

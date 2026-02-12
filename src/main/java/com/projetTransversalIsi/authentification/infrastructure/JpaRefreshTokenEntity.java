package com.projetTransversalIsi.authentification.infrastructure;

import com.projetTransversalIsi.user.domain.UserStatus;
import com.projetTransversalIsi.user.infrastructure.JpaUserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class JpaRefreshTokenEntity {
    @Id
    private String token;

    @ManyToOne
    @JoinColumn(name="user_id",nullable = false)
    private JpaUserEntity user;

    private Date createdAt;
    private Date expiresAt;

    @Column(columnDefinition = "boolean default true")
    private boolean revoked;
}

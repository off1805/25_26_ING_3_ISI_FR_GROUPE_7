package com.projetTransversalIsi.authentification.domain;

import lombok.Data;

import java.util.Date;

@Data
public class RefreshToken {
    private String token;
    private Long userId;
    private Date createdAt;
    private Date expiresAt;
    private boolean revoked;

}

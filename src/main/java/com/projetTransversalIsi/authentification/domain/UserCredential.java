package com.projetTransversalIsi.authentification.domain;

import lombok.Data;

@Data
public class UserCredential {
    private String password;
    private String email;
}

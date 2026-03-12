package com.projetTransversalIsi.user.services;

import java.util.Optional;

public interface GetPasswordByEmail {

    Optional<String> getPasswordByEmail(String email);
}

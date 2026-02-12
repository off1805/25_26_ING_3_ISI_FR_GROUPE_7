package com.projetTransversalIsi.user.application.services;

import java.util.Optional;

public interface GetPasswordByEmail {

    Optional<String> getPasswordByEmail(String email);
}

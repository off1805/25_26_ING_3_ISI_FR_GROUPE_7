package com.projetTransversalIsi.user.services.interfaces;

import java.util.Optional;

public interface GetPasswordByEmail {

    Optional<String> getPasswordByEmail(String email);
}

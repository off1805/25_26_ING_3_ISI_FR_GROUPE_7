package com.projetTransversalIsi.authentification.application.exceptions;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface KeyManagement {
    PublicKey loadPublicKey();
    PrivateKey loadPrivateKey();
}

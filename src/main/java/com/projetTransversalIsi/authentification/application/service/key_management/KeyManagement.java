package com.projetTransversalIsi.authentification.application.service.key_management;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface KeyManagement {
    PublicKey loadPublicKey();
    PrivateKey loadPrivateKey();
}

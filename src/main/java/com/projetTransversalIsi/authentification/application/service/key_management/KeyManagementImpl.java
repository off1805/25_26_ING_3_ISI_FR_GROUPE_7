package com.projetTransversalIsi.authentification.application.service.key_management;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class KeyManagementImpl implements KeyManagement {
    @Value("${security.jwt.private-key-path}")
    private Resource privateKeyResource;

    @Value("${security.jwt.public-key-path}")
    private Resource publicKeyResource;


    @Override
    public PrivateKey loadPrivateKey(){
        try {

            String key = Files.readString(privateKeyResource.getFile().toPath());
            key = key
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] decoded = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);

            return KeyFactory.getInstance("EC").generatePrivate(spec);
        }catch (Exception e){
            throw new IllegalStateException("Invalid ECDSA private key", e);

        }

    }

    @Override
    public PublicKey loadPublicKey(){
        try {
            String key = Files.readString(publicKeyResource.getFile().toPath());
            key = key
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] decoded = Base64.getDecoder().decode(key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);

            return KeyFactory.getInstance("EC").generatePublic(spec);
        } catch (Exception e) {
            throw new IllegalStateException("Invalid ECDSA public key", e);
        }
    }
}

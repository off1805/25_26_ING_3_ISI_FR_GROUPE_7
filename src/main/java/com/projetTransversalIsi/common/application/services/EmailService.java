package com.projetTransversalIsi.common.application.services;

public interface EmailService {
    void sendInitPassword(String email, String password, String name);
    void sendConfirmationEmail(String email, String name);
}

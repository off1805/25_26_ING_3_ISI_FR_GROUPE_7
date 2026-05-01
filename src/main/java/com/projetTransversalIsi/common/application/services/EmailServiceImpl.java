package com.projetTransversalIsi.common.application.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendInitPassword(String email, String password, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("Bienvenue sur KemoSchool - Vos identifiants de connexion");

            String htmlContent = """
                <!DOCTYPE html>
                <html lang="fr">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Bienvenue sur KemoSchool</title>
                    <style>
                        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                        .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
                        .header { background-color: #4285f4; color: #ffffff; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                        .content { padding: 20px; color: #333333; line-height: 1.6; }
                        .password-box { background-color: #f0f0f0; border: 1px solid #ddd; padding: 15px; border-radius: 4px; font-family: monospace; font-size: 16px; text-align: center; margin: 20px 0; }
                        .footer { text-align: center; padding: 20px; color: #666666; font-size: 12px; border-top: 1px solid #eeeeee; }
                        .button { display: inline-block; background-color: #4285f4; color: #ffffff; padding: 10px 20px; text-decoration: none; border-radius: 4px; margin-top: 20px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Bienvenue sur KemoSchool</h1>
                        </div>
                        <div class="content">
                            <p>Cher(e) %s,</p>
                            <p>Votre compte a été créé avec succès sur la plateforme KemoSchool.</p>
                            <p>Voici vos identifiants de connexion :</p>
                            <div class="password-box">
                                <strong>Email :</strong> %s<br>
                                <strong>Mot de passe :</strong> %s
                            </div>
                            <p>Nous vous recommandons de changer votre mot de passe lors de votre première connexion pour des raisons de sécurité.</p>
                            <p>Si vous avez des questions, n'hésitez pas à contacter notre équipe support.</p>
                            <p>Cordialement,<br>L'équipe KemoSchool</p>
                        </div>
                        <div class="footer">
                            <p>Cet email a été envoyé automatiquement. Veuillez ne pas y répondre.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(name, email, password);

            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            // Log the error, but don't throw to avoid breaking user creation
            System.err.println("Erreur lors de l'envoi de l'email de mot de passe : " + e.getMessage());
        }
    }

    @Override
    public void sendConfirmationEmail(String email, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("Confirmation de création de compte KemoSchool");

            String htmlContent = """
                <!DOCTYPE html>
                <html lang="fr">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Confirmation de compte</title>
                    <style>
                        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                        .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
                        .header { background-color: #34a853; color: #ffffff; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                        .content { padding: 20px; color: #333333; line-height: 1.6; }
                        .footer { text-align: center; padding: 20px; color: #666666; font-size: 12px; border-top: 1px solid #eeeeee; }
                        .button { display: inline-block; background-color: #34a853; color: #ffffff; padding: 10px 20px; text-decoration: none; border-radius: 4px; margin-top: 20px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Confirmation de compte</h1>
                        </div>
                        <div class="content">
                            <p>Cher(e) %s,</p>
                            <p>Félicitations ! Votre compte sur KemoSchool a été créé avec succès.</p>
                            <p>Vous pouvez maintenant accéder à toutes les fonctionnalités de notre plateforme éducative.</p>
                            <p>Un email séparé contenant vos identifiants de connexion vous a été envoyé.</p>
                            <p>Bienvenue dans la communauté KemoSchool !</p>
                            <p>Cordialement,<br>L'équipe KemoSchool</p>
                        </div>
                        <div class="footer">
                            <p>Cet email a été envoyé automatiquement. Veuillez ne pas y répondre.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(name);

            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            // Log the error, but don't throw to avoid breaking user creation
            System.err.println("Erreur lors de l'envoi de l'email de confirmation : " + e.getMessage());
        }
    }
}

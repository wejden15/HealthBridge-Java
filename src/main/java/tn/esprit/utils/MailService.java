package tn.esprit.utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.File;
import java.util.Properties;

public class MailService {

    public static void sendEmailWithAttachment(String to, File attachmentFile) {
        final String username = "houssemeddin.abdelal@esprit.tn"; // Your email
        final String password = "noway123456A"; // Use App Password (not normal password)

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );
            message.setSubject("🎫 Votre Pass Événement - HealthBridge");

            // Body
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Bonjour,\n\nVeuillez trouver votre QR Code d’accès à l'événement ci-joint.\n\nCordialement,\nHealthBridge");

            // Attachment
            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(attachmentFile);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);

            Transport.send(message);
            System.out.println("✅ Email envoyé à " + to);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Erreur lors de l'envoi de l'email");
        }
    }
}

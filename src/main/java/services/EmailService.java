package services;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import javafx.scene.control.Alert;
import javafx.application.Platform;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EmailService {
    private static final String HOST = "smtp.gmail.com";
    private static final int PORT = 465;
    private static final boolean SSL_FLAG = true;
    private String username;
    private String password;
    private boolean isConfigured = false;

    public EmailService() {
        loadConfiguration();
    }

    private void loadConfiguration() {
        // First try environment variables
        username = System.getenv("QUIZ_EMAIL_USERNAME");
        password = System.getenv("QUIZ_EMAIL_PASSWORD");

        // If environment variables are not set, try loading from properties file
        if (username == null || password == null) {
            try (InputStream input = getClass().getClassLoader().getResourceAsStream("email.properties")) {
                if (input != null) {
                    Properties prop = new Properties();
                    prop.load(input);
                    username = prop.getProperty("email.username");
                    password = prop.getProperty("email.password");
                    
                    // Debug logging
                    System.out.println("Email Configuration loaded:");
                    System.out.println("Username: " + username);
                    System.out.println("Password length: " + (password != null ? password.length() : 0));
                    System.out.println("Host: " + HOST);
                    System.out.println("Port: " + PORT);
                }
            } catch (IOException e) {
                System.err.println("Failed to load email configuration: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        }

        isConfigured = (username != null && !username.isEmpty() && 
                       password != null && !password.isEmpty());
        
        if (!isConfigured) {
            System.err.println("Email configuration is missing. Please set environment variables or create email.properties file.");
        } else {
            System.out.println("Email service configured successfully");
        }
    }

    public void sendQuizResults(String recipientEmail, String quizName, double score, int correctAnswers, int totalQuestions) {
        if (!isConfigured) {
            Platform.runLater(() -> showAlert("Configuration Error", 
                "Email service is not properly configured. Please contact the administrator.", 
                Alert.AlertType.ERROR));
            return;
        }

        String subject = "Quiz Results: " + quizName;
        String message = createQuizResultMessage(quizName, score, correctAnswers, totalQuestions);
        
        try {
            Email email = new SimpleEmail();
            email.setHostName(HOST);
            email.setSmtpPort(PORT);
            email.setAuthenticator(new DefaultAuthenticator(username, password));
            email.setSSLOnConnect(SSL_FLAG);
            email.setStartTLSEnabled(true); // Enable STARTTLS
            email.setFrom(username, "Quiz System");
            email.setSubject(subject);
            email.setMsg(message);
            email.addTo(recipientEmail);
            
            System.out.println("Attempting to send quiz results to " + recipientEmail);
            System.out.println("Using SMTP configuration:");
            System.out.println("Host: " + HOST);
            System.out.println("Port: " + PORT);
            System.out.println("SSL: " + SSL_FLAG);
            System.out.println("From: " + username);
            
            email.send();
            System.out.println("Quiz results sent successfully to " + recipientEmail);
            
            Platform.runLater(() -> showAlert("Success", "Email sent successfully!", Alert.AlertType.INFORMATION));
            
        } catch (EmailException e) {
            System.err.println("Failed to send quiz results to " + recipientEmail);
            System.err.println("Error message: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Root cause: " + e.getCause().getMessage());
                e.getCause().printStackTrace();
            }
            e.printStackTrace();
            
            Platform.runLater(() -> showAlert("Error", 
                "Failed to send email. Error: " + e.getMessage(), 
                Alert.AlertType.ERROR));
        }
    }

    private String createQuizResultMessage(String quizName, double score, int correctAnswers, int totalQuestions) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Quiz Results Summary\n");
        messageBuilder.append("===================\n\n");
        messageBuilder.append("Quiz Name: ").append(quizName).append("\n");
        messageBuilder.append("Score: ").append(String.format("%.2f%%", score)).append("\n");
        messageBuilder.append("Correct Answers: ").append(correctAnswers).append(" out of ").append(totalQuestions).append("\n\n");
        
        // Add performance assessment
        if (score >= 90) {
            messageBuilder.append("Excellent performance! Keep up the great work!");
        } else if (score >= 70) {
            messageBuilder.append("Good job! You're doing well!");
        } else if (score >= 50) {
            messageBuilder.append("You passed! With more practice, you can improve your score.");
        } else {
            messageBuilder.append("You might want to review the material and try again.");
        }
        
        messageBuilder.append("\n\nThis is an automated message from the Quiz System.");
        
        return messageBuilder.toString();
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Keep the original method for backward compatibility
    public void sendQuizResults(String recipientEmail, String subject, String message) {
        try {
            Email email = new SimpleEmail();
            email.setHostName(HOST);
            email.setSmtpPort(PORT);
            email.setAuthenticator(new DefaultAuthenticator(username, password));
            email.setSSLOnConnect(SSL_FLAG);
            email.setFrom(username, "Quiz System");
            email.setSubject(subject);
            email.setMsg(message);
            email.addTo(recipientEmail);
            
            email.send();
        } catch (EmailException e) {
            System.err.println("Failed to send email to " + recipientEmail);
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to test the email configuration
    public boolean testEmailConfiguration() {
        try {
            Email email = new SimpleEmail();
            email.setHostName(HOST);
            email.setSmtpPort(PORT);
            email.setAuthenticator(new DefaultAuthenticator(username, password));
            email.setSSLOnConnect(SSL_FLAG);
            email.setFrom(username);
            return true;
        } catch (EmailException e) {
            System.err.println("Email configuration test failed: " + e.getMessage());
            return false;
        }
    }
} 
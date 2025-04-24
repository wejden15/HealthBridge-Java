package utils;

import services.EmailService;
import javafx.scene.control.Alert;

public class EmailConfigTester {
    public static void main(String[] args) {
        EmailService emailService = new EmailService();
        
        if (emailService.testEmailConfiguration()) {
            System.out.println("Email configuration is correct!");
            showAlert("Success", "Email configuration is correct!", Alert.AlertType.INFORMATION);
        } else {
            System.out.println("Email configuration failed!");
            showAlert("Error", "Email configuration failed!", Alert.AlertType.ERROR);
        }
    }

    private static void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 
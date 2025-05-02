package tn.esprit.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

public class IndexController {
    @FXML
    private PasswordField passwordField; // Ensure this is defined in your controller

    private boolean isPasswordVisible = false; // Flag to check if the password is visible
    @FXML
    private ImageView eyeIcon;
    @FXML
    public void handlePasswordCheck(ActionEvent event) {
        String enteredPassword = passwordField.getText();  // Get the text from password field

        // Check if the password matches the predefined one
        if ("healthbridge".equals(enteredPassword)) {
            // Password is correct
            System.out.println("Password correct, access granted.");

            // You can redirect to another page, open the admin dashboard, etc.
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/adminDashboard.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Admin Dashboard");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Password is incorrect
            System.out.println("Incorrect password! Try again.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Incorrect Password");
            alert.setHeaderText(null);
            alert.setContentText("❌ Incorrect password! Please try again.");
            alert.showAndWait();
        }
    }

    // Method to toggle password visibility
    @FXML
    public void togglePasswordVisibility(ActionEvent event) {
        if (isPasswordVisible) {
            passwordField.setPromptText("Enter password"); // Hide the password
            passwordField.setText(passwordField.getText()); // Retain the password text
            // Update the eye icon to show the password is hidden (set closed eye image)
            eyeIcon.setImage(new Image(getClass().getResourceAsStream("/img/eye_icon.png")));
        } else {
            passwordField.setPromptText("Password is visible"); // Indicate password is visible
            passwordField.setText(passwordField.getText()); // Retain the password text
            // Update the eye icon to show the password is visible (set open eye image)
            eyeIcon.setImage(new Image(getClass().getResourceAsStream("/img/eye_open_icon.png")));
        }
        isPasswordVisible = !isPasswordVisible; // Toggle the flag
    }

    // This method is triggered when the "Admin Dashboard" button is clicked
    @FXML
    public void handleAdminAccess(ActionEvent event) {
        // Create a dialog to ask for the password
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Accès Administrateur");
        dialog.setHeaderText("Mot de passe requis");
        dialog.setContentText("Entrez le mot de passe :");

        // Capture the result of the dialog (password entered by the user)
        Optional<String> result = dialog.showAndWait();

        // Check if the password is correct
        if (result.isPresent() && result.get().equals("healthbridge")) {
            try {
                // If correct password, open the Admin Dashboard
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/adminDashboard.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Admin Dashboard");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace(); // Handle exception if file loading fails
            }
        } else {
            // If password is incorrect, show an error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Accès refusé");
            alert.setHeaderText(null);
            alert.setContentText("❌ Mot de passe incorrect !");
            alert.showAndWait();
        }
    }

}

package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class DashboardController {

    @FXML
    private Button logoutButton;

    @FXML
    private Button viewProfileButton;

    // Handle logout button click
    @FXML
    private void handleLogout() {
        System.out.println("Logging out...");

        // Close the current window (Dashboard) and show the login screen
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.close();

        // Here, you can add code to navigate back to the login screen if needed
        // For example, load login scene (or reload login view) here
    }

    // Handle view profile button click
    @FXML
    private void handleViewProfile() {
        System.out.println("Viewing profile...");
        // Implement logic for viewing profile (e.g., open a profile view)
    }
}

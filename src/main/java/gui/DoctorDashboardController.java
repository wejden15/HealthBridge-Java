package gui;

import entities.Doctor;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import services.UserSession;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class DoctorDashboardController extends BaseController {
    @FXML private BorderPane rootPane;
    @FXML private Label doctorNameLabel;
    @FXML private Label doctorSpecialtyLabel;
    @FXML private Label usernameLabel;
    @FXML private Label sessionInfoLabel;
    @FXML private Label statusLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setRoot(rootPane); // Must call this first
        super.initialize(location, resources);
    }

    @Override
    protected void onInitialized() {
        // This will only execute if user is logged in
        if (!isDoctor()) {
            showErrorAndRedirect("Access Denied", "Doctor privileges required");
            return;
        }

        // Display user information
        updateUserInfo();

        // Test session access
        testSessionAccess();
    }

    private void updateUserInfo() {
        String username = getCurrentUsername();
        usernameLabel.setText("Username: " + username);
        statusLabel.setText("Logged in as: " + username);

        // You can also get other user info
        int userId = getCurrentUserId();
        String userRole = UserSession.getCurrentUserRole();

        // Display in the test section
        sessionInfoLabel.setText(String.format(
                "User ID: %d\nRole: %s\nFull Name: %s",
                userId,
                userRole,
                UserSession.getCurrentUser().getFullName()
        ));
    }

    @FXML
    private void testSessionAccess() {
        StringBuilder sb = new StringBuilder();
        sb.append("Session Test Results:\n");
        sb.append("Logged in: ").append(UserSession.isLoggedIn()).append("\n");
        sb.append("Username: ").append(getCurrentUsername()).append("\n");
        sb.append("User ID: ").append(getCurrentUserId()).append("\n");
        sb.append("Role: ").append(UserSession.getCurrentUserRole()).append("\n");

        sessionInfoLabel.setText(sb.toString());
        statusLabel.setText("Session test completed - see results above");
    }

    @FXML
    private void handleLogout() {
        try {
            // Confirm logout
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Logout");
            confirm.setHeaderText(null);
            confirm.setContentText("Are you sure you want to logout?");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Perform logout
                redirectToLogin();

                // Additional cleanup if needed
                cleanupBeforeLogout();
            }
        } catch (Exception e) {
            System.err.println("Logout failed: " + e.getMessage());
            e.printStackTrace();
            // Fallback to system exit if everything fails
            Platform.exit();
        }
    }

    private void cleanupBeforeLogout() {
        // Add any specific cleanup needed before logout
        // For example:
        // - Clear sensitive data
        // - Save state
        // - Close connections
    }

    private void showErrorAndRedirect(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

        if (isAdmin()) {
            redirectTo("dashboard_admin.fxml");
        } else if (isPatient()) {
            redirectTo("dashboard_patient.fxml");
        } else {
            redirectToLogin();
        }
    }

    public void setDoctorData(Doctor doctor) {
    }
}
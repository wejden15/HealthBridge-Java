package gui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.UserSession;
import java.net.URL;
import java.util.ResourceBundle;

public abstract class BaseController implements Initializable {
    private Stage currentStage;
    private Parent root;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Verify login status
        if (!UserSession.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        // Additional initialization for logged-in users
        onInitialized();
    }

    /**
     * Called after successful login verification
     */
    protected void onInitialized() {
        // Can be overridden by child classes
    }

    protected void redirectToLogin() {
        try {
            // Clear the session first
            UserSession.logout();

            // Load the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent loginRoot = loader.load();

            // Get the current stage
            Stage currentStage = getCurrentStage();
            if (currentStage != null) {
                // Replace scene content
                Scene currentScene = currentStage.getScene();
                if (currentScene == null) {
                    currentScene = new Scene(loginRoot);
                    currentStage.setScene(currentScene);
                } else {
                    currentScene.setRoot(loginRoot);
                }

                // Reset window properties if needed
                currentStage.setTitle("HealthBridge - Login");
                currentStage.sizeToScene();
                currentStage.centerOnScreen();
            } else {
                // Fallback - create new stage
                Stage newStage = new Stage();
                newStage.setTitle("HealthBridge - Login");
                newStage.setScene(new Scene(loginRoot));
                newStage.show();
            }

        } catch (Exception e) {
            System.err.println("Failed to redirect to login: " + e.getMessage());
            e.printStackTrace();
            // Fallback to system exit if everything fails
            Platform.exit();
        }
    }
    protected Stage getCurrentStage() {
        if (currentStage == null && root != null && root.getScene() != null) {
            currentStage = (Stage) root.getScene().getWindow();
        }
        return currentStage;
    }
    protected void redirectTo(String fxmlPath) {
        try {
            Stage stage = getCurrentStage();
            Parent newRoot = FXMLLoader.load(getClass().getResource(fxmlPath));
            stage.setScene(new Scene(newRoot));
        } catch (Exception e) {
            System.err.println("Failed to redirect to " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }



    protected Parent getRoot() {
        if (root == null) {
            throw new IllegalStateException("Root not set. Call setRoot() in controller initialization.");
        }
        return root;
    }

    protected void setRoot(Parent root) {
        this.root = root;
    }

    // Helper methods for child controllers
    protected boolean isAdmin() {
        return UserSession.isLoggedIn() && "Admin".equals(UserSession.getCurrentUserRole());
    }

    protected boolean isDoctor() {
        return UserSession.isLoggedIn() && "Doctor".equals(UserSession.getCurrentUserRole());
    }

    protected boolean isPatient() {
        return UserSession.isLoggedIn() && "Patient".equals(UserSession.getCurrentUserRole());
    }

    protected String getCurrentUsername() {
        return UserSession.getCurrentUsername();
    }

    protected int getCurrentUserId() {
        return UserSession.getCurrentUserId();
    }
}
package tn.esprit.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.io.InputStream;

public class AdminDashboardController {

    @FXML
    private ImageView logoImage;

    @FXML
    private VBox sidebar;

    @FXML
    private AnchorPane contentArea;

    private boolean sidebarExpanded = true;  // Flag for sidebar state

    // Initialize the logo image from resources
    @FXML
    public void initialize() {
        InputStream is = getClass().getResourceAsStream("/img/healthbridge_logo.png");
        if (is != null) {
            logoImage.setImage(new Image(is));
        }
    }

    // This method toggles the sidebar visibility (expand/collapse)
    @FXML
    public void toggleSidebar(ActionEvent event) {
        if (sidebarExpanded) {
            sidebar.setPrefWidth(60);  // Collapse sidebar
            sidebar.getChildren().forEach(node -> {
                if (node instanceof Button) {
                    ((Button) node).setText("");  // Remove text when collapsed
                }
            });
        } else {
            sidebar.setPrefWidth(200);  // Expand sidebar
            sidebar.getChildren().forEach(node -> {
                if (node instanceof Button) {
                    String text = ((Button) node).getId();  // Set the button text
                    switch (text) {
                        case "toggleButton" -> ((Button) node).setText("≡");
                        case "addEventButton" -> ((Button) node).setText("➕ Ajouter Événement");
                        case "eventListButton" -> ((Button) node).setText("📋 Liste des Événements");
                        case "statsButton" -> ((Button) node).setText("📊 Statistiques");
                        case "donorsButton" -> ((Button) node).setText("💳 Voir Donateurs");
                        case "homeButton" -> ((Button) node).setText("⬅️ Retour à l'accueil");
                    }
                }
            });
        }
        sidebarExpanded = !sidebarExpanded;  // Toggle the state
    }

    // Helper method to handle navigation between scenes
    private void navigate(ActionEvent event, String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Navigation methods for buttons in the sidebar
    @FXML
    public void goToAddEvent(ActionEvent event) {
        navigate(event, "/fxml/evenementForm.fxml", "Ajouter Événement");
    }

    @FXML
    public void goToEventList(ActionEvent event) {
        navigate(event, "/fxml/evenementList.fxml", "Liste des Événements");
    }

    @FXML
    public void goToStats(ActionEvent event) {
        navigate(event, "/fxml/evenementStats.fxml", "Statistiques");
    }

    @FXML
    public void goToDonors(ActionEvent event) {
        navigate(event, "/fxml/adminDonations.fxml", "Donateurs");
    }

    // Button to go back to the home page
    @FXML
    public void goToHome(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/template.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene currentScene = stage.getScene();  // Get current scene
            double width = currentScene.getWidth();
            double height = currentScene.getHeight();

            Scene newScene = new Scene(root, width, height);  // Keep previous size
            stage.setScene(newScene);
            stage.setTitle("HealthBridge - Accueil");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package tn.esprit.controller;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.layout.VBox;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class TemplateController {

    @FXML
    private StackPane contentArea;
    @FXML
    private VBox sidebar;


    private boolean isSidebarVisible = true;

    @FXML
    public void toggleSidebar() {
        TranslateTransition slide = new TranslateTransition(Duration.millis(300), sidebar);

        if (isSidebarVisible) {
            slide.setToX(-sidebar.getWidth());  // slide out
            slide.play();
            isSidebarVisible = false;
        } else {
            slide.setToX(0);  // slide in
            slide.play();
            isSidebarVisible = true;
        }
    }


    @FXML
    private Button toggleButton;

    private boolean isSidebarExpanded = true;

    @FXML
    private ImageView logoImage;

    @FXML
    public void goToDonationHistory() {
        loadContent("/fxml/donationHistory.fxml");
    }

    @FXML
    private Button homeButton;
    @FXML
    private Button listButton;
    @FXML
    private Button statsButton;
    @FXML
    private Button calendarButton;

    private Button activeButton;
    @FXML
    private Button addButton; // New button
    @FXML
    private Button scanButton;

    public void goToScanQR(ActionEvent event) {
        loadPage("/fxml/scanQR.fxml");
        highlightButton(scanButton);
    }
    @FXML
    private Button patientButton;

    public void goToPatientEvents(ActionEvent event) {
        loadPage("/fxml/patientEventList.fxml");
        highlightButton(patientButton);
    }
    @FXML
    public void goToAdminDonations(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/adminDonations.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin - Donations");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToAdd(ActionEvent event) {
        loadPage("/fxml/evenementForm.fxml");
        highlightButton(addButton);
    }
    public void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();
            contentArea.getChildren().setAll(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        // Load the logo
        logoImage.setImage(new Image(getClass().getResourceAsStream("/img/healthbridge_logo.png")));

        // Load default page
        loadPage("/fxml/index.fxml");

        // Set initial active button
        highlightButton(homeButton);
        sidebar.setTranslateX(0); // or -sidebar.getWidth() if hidden by default
    }
    @FXML
    private void toggleSidebar(ActionEvent event) {
        if (isSidebarExpanded) {
            sidebar.setPrefWidth(60); // Collapse sidebar to small width
            logoImage.setVisible(false);
            homeButton.setText("");
            listButton.setText("");
            statsButton.setText("");
            calendarButton.setText("");
        } else {
            sidebar.setPrefWidth(200); // Expand sidebar to full width
            logoImage.setVisible(true);
            homeButton.setText("🏠 Accueil");
            listButton.setText("📋 Liste des Événements");
            statsButton.setText("📊 Statistiques");
            calendarButton.setText("📅 Calendrier");
        }
        isSidebarExpanded = !isSidebarExpanded;
    }

    private void loadPage(String fxmlPath) {
        try {
            Parent pane = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(pane);

            // Smooth fade transition
            FadeTransition ft = new FadeTransition(Duration.millis(500), pane);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ➡️ Your updated navigation methods are here:

    public void goToHome(ActionEvent event) {
        loadPage("/fxml/index.fxml");
        highlightButton(homeButton);
    }

    public void goToList(ActionEvent event) {
        loadPage("/fxml/evenementList.fxml");
        highlightButton(listButton);
    }

    public void goToStats(ActionEvent event) {
        loadPage("/fxml/evenementStats.fxml");
        highlightButton(statsButton);
    }

    public void goToCalendar(ActionEvent event) {
        loadPage("/fxml/evenementCalendar.fxml");
        highlightButton(calendarButton);
    }

    private void highlightButton(Button button) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("sidebar-button-active");
            if (!activeButton.getStyleClass().contains("sidebar-button")) {
                activeButton.getStyleClass().add("sidebar-button");
            }
        }

        activeButton = button;
        activeButton.getStyleClass().remove("sidebar-button");
        if (!activeButton.getStyleClass().contains("sidebar-button-active")) {
            activeButton.getStyleClass().add("sidebar-button-active");
        }
    }
}

package tn.esprit.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import tn.esprit.entity.Evenement;
import tn.esprit.service.EvenementService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CalendrierController {

    @FXML
    private DatePicker calendar;

    @FXML
    private ListView<String> eventListView;

    private final EvenementService evenementService = new EvenementService();
    private Set<LocalDate> eventDates;

    @FXML
    public void initialize() {
        // 🔹 1. Load all event dates
        List<Evenement> allEvents = evenementService.getAllEvenements();

        eventDates = allEvents.stream()
                .map(Evenement::getDate)
                .collect(Collectors.toSet());

        // 🔹 2. Highlight calendar days with events
        calendar.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                if (!empty && eventDates.contains(date)) {
                    setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
                }

                if (!empty && date.equals(LocalDate.now())) {
                    setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                }
            }
        });

        // 🔹 3. When clicking a date ➔ show events of that day
        calendar.valueProperty().addListener((obs, oldDate, newDate) -> {
            List<Evenement> dayEvents = allEvents.stream()
                    .filter(e -> e.getDate().equals(newDate))
                    .collect(Collectors.toList());

            ObservableList<String> eventDescriptions = FXCollections.observableArrayList();
            for (Evenement e : dayEvents) {
                eventDescriptions.add(e.getNom() + " - " + e.getCategorie());
            }

            eventListView.setItems(eventDescriptions);
        });
    }
    @FXML
    public void retourAccueil(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/template.fxml"));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene currentScene = stage.getScene(); // Get current scene
            double width = currentScene.getWidth();
            double height = currentScene.getHeight();

            Scene newScene = new Scene(root, width, height); // Keep previous size
            stage.setScene(newScene);
            stage.setTitle("HealthBridge - Accueil");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

package tn.esprit.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import tn.esprit.entity.Evenement;
import tn.esprit.service.EvenementService;
import tn.esprit.service.IEvenementService;
import tn.esprit.utils.QRCodeGenerator;
import com.google.zxing.WriterException;
import java.time.LocalDate;
import java.util.stream.Collectors;
import java.io.IOException;
import java.time.LocalDate;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.scene.control.Alert; // ⬅️ Très important : importer Alert !
import java.util.List;
import javafx.scene.input.KeyEvent; // ⬅️ très important pour KeyEvent
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.scene.control.Tooltip; // ⬅️ Très important : ajouter cet import si pas encore fait




public class EvenementController {

    // 🎯 Pour evenementForm.fxml (Ajouter un événement)
    @FXML
    private TextField nomField;

    @FXML
    private DatePicker calendar;

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private TextField categorieField;

    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField searchField;

    // 🎯 Pour evenementList.fxml (Afficher liste d'événements)
    @FXML
    private TableView<Evenement> evenementTable;

    @FXML
    private TableColumn<Evenement, Integer> idColumn;

    @FXML
    private TableColumn<Evenement, String> nomColumn;

    @FXML
    private TableColumn<Evenement, String> categorieColumn;

    @FXML
    private TableColumn<Evenement, LocalDate> dateColumn;

    // 🔥 Service pour gérer les événements
    private final IEvenementService evenementService = new EvenementService();
    @FXML
    private void openEditWindow() {
        Evenement selected = evenementTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editEvent.fxml"));
                Parent root = loader.load();
                EditEventController controller = loader.getController();
                controller.setEvenement(selected);

                Stage stage = new Stage();
                stage.setTitle("Modifier Événement");
                stage.setScene(new Scene(root));
                stage.showAndWait();

                refreshTable(); // Reload updated data
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void refreshTable() {
        afficherTousLesEvenements(); // ou le nom de ta méthode principale pour recharger les données
    }

    // 🛠️ Appelé automatiquement quand un FXML se charge
    @FXML
    public void initialize() {
        if (evenementTable != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
            categorieColumn.setCellValueFactory(new PropertyValueFactory<>("categorie"));
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
            evenementTable.getItems().setAll(evenementService.getAllEvenements());
        }

        if (barChart != null) {
            chargerStatistiques();
        }

        if (calendar != null) {
            chargerCalendrier();
        }
    }

    public void chargerCalendrier() {
        List<Evenement> evenements = evenementService.getAllEvenements();

        calendar.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                if (date != null && !empty) {
                    List<Evenement> eventsThisDay = evenements.stream()
                            .filter(e -> e.getDate().equals(date))
                            .collect(Collectors.toList());

                    if (!eventsThisDay.isEmpty()) {
                        // Colorier le jour
                        this.setStyle("-fx-background-color: #81C784;");

                        // Créer le texte du tooltip
                        String tooltipText = eventsThisDay.stream()
                                .map(e -> e.getNom() + " (" + e.getCategorie() + ")")
                                .collect(Collectors.joining("\n"));

                        Tooltip tooltip = new Tooltip(tooltipText);
                        Tooltip.install(this, tooltip);
                    }
                }
            }
        });
    }
    public void goToCalendrier(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/evenementCalendar.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Calendrier des Événements");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // ➡️ Bouton : Aller au formulaire Ajouter Événement
    public void goToAjouterEvenement(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/evenementForm.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un Événement");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ➡️ Bouton : Aller à la liste des événements
    public void goToListeEvenement(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/evenementList.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Événements");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void filtrerEvenementsAVenir(ActionEvent event) {
        List<Evenement> futurs = evenementService.getAllEvenements()
                .stream()
                .filter(e -> e.getDate().isAfter(LocalDate.now()))
                .collect(Collectors.toList());

        evenementTable.getItems().setAll(futurs);
    }

    public void filtrerEvenementsPasses(ActionEvent event) {
        List<Evenement> passes = evenementService.getAllEvenements()
                .stream()
                .filter(e -> e.getDate().isBefore(LocalDate.now()))
                .collect(Collectors.toList());

        evenementTable.getItems().setAll(passes);
    }
    public void backupNow(ActionEvent event) {
        try {
            // Créer le dossier backup s'il n'existe pas
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get("backup"));

            // Appeler le service de sauvegarde
            tn.esprit.utils.BackupService.backupEvenementTable("backup/evenement_backup.sql");

            // ✅ Afficher une alerte de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sauvegarde Réussie");
            alert.setHeaderText(null);
            alert.setContentText("✅ Sauvegarde des événements effectuée avec succès !");
            alert.showAndWait(); // attend que l'utilisateur ferme l'alerte

            System.out.println("✅ Sauvegarde effectuée avec succès !");
        } catch (Exception e) {
            e.printStackTrace();

            // ❌ Afficher une alerte d'erreur si problème
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur Sauvegarde");
            alert.setHeaderText(null);
            alert.setContentText("❌ Une erreur est survenue lors du backup.");
            alert.showAndWait();
        }
    }
    public void supprimerEvenement(ActionEvent event) {
        Evenement selected = evenementTable.getSelectionModel().getSelectedItem();

        if (selected != null) {
            evenementService.supprimerEvenement(selected.getId());
            evenementTable.getItems().remove(selected);

            // ✅ Afficher une alerte de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Suppression");
            alert.setHeaderText(null);
            alert.setContentText("✅ Événement supprimé avec succès !");
            alert.showAndWait();
        } else {
            // ❌ Aucun événement sélectionné
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText("❗ Veuillez sélectionner un événement à supprimer.");
            alert.showAndWait();
        }
    }
    public void rechercherEvenement(KeyEvent event) {
        String keyword = searchField.getText().toLowerCase();

        List<Evenement> filtered = evenementService.getAllEvenements()
                .stream()
                .filter(e -> e.getNom().toLowerCase().contains(keyword) ||
                        e.getCategorie().toLowerCase().contains(keyword))
                .collect(Collectors.toList());

        evenementTable.getItems().setAll(filtered);
    }
    public void reinitialiserRecherche(ActionEvent event) {
        searchField.clear(); // Vide le champ de recherche
        evenementTable.getItems().setAll(evenementService.getAllEvenements()); // Recharge tous les événements
    }
    public void goToStatistiques(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/evenementStats.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Statistiques des Événements");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generatePassCode() {
        int randomNumber = (int)(Math.random() * 9000) + 1000; // Random 4-digit code
        return "PASS-" + randomNumber;
    }


    public void chargerStatistiques() {
        List<Evenement> evenements = evenementService.getAllEvenements();

        Map<String, Long> statistiques = evenements.stream()
                .collect(Collectors.groupingBy(Evenement::getCategorie, Collectors.counting()));

        barChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (Map.Entry<String, Long> entry : statistiques.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().add(series);
    }
    @FXML
    public void modifierEvenement() {
        Evenement selected = evenementTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun événement sélectionné");
            alert.setContentText("Veuillez sélectionner un événement à modifier.");
            alert.show();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editEventForm.fxml"));
            Parent root = loader.load();

            // Passer les données à modifier
            EditEventController controller = loader.getController();
            controller.setEvenementToEdit(selected); // ✅ custom setter

            Stage stage = new Stage();
            stage.setTitle("Modifier Événement");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void afficherTousLesEvenements() {
        List<Evenement> evenements = evenementService.getAllEvenements();
        ObservableList<Evenement> observableList = FXCollections.observableArrayList(evenements);
        evenementTable.setItems(observableList);
    }

    // ➕ Bouton Ajouter dans evenementForm.fxml
    public void ajouterEvenement(ActionEvent event) {
        String nom = nomField.getText();
        String categorie = categorieField.getText();
        LocalDate date = datePicker.getValue();

        if (nom.isEmpty() || categorie.isEmpty() || date == null) {
            System.out.println("❗Veuillez remplir tous les champs !");
            return;
        }

        Evenement evenement = new Evenement();
        evenement.setNom(nom);
        evenement.setCategorie(categorie);
        evenement.setDate(date);

        // 🔥 Generate a PassCode automatically
        evenement.setPassCode(generatePassCode());

        evenementService.ajouterEvenement(evenement);

        System.out.println("✅ Événement ajouté avec passcode: " + evenement.getPassCode());

        // ✅ Now qrContent includes the PASS
        String qrContent = "Nom=" + nom +
                ";Catégorie=" + categorie +
                ";Date=" + date.toString() +
                ";Pass=" + evenement.getPassCode();


        String filePath = "qrcodes/" + nom.replaceAll("\\s+", "_") + ".png";

        try {
            Files.createDirectories(Paths.get("qrcodes"));
            QRCodeGenerator.generateQRCode(qrContent, filePath);
            System.out.println("✅ QR Code généré : " + filePath);

            // Afficher le QR Code dans une pop-up
            javafx.scene.image.Image qrImage = new javafx.scene.image.Image("file:" + filePath);
            javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(qrImage);
            imageView.setFitWidth(300);
            imageView.setFitHeight(300);
            imageView.setPreserveRatio(true);

            Stage qrStage = new Stage();
            qrStage.setTitle("QR Code de l'Événement : " + nom);
            Scene qrScene = new Scene(new javafx.scene.layout.StackPane(imageView), 350, 350);
            qrStage.setScene(qrScene);
            qrStage.showAndWait();

        } catch (IOException | WriterException e) {
            e.printStackTrace();
        }

        System.out.println("✅ Événement ajouté avec succès : " + nom);

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/template.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("HealthBridge");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    // 🔙 Bouton Retour dans toutes les pages
    public void retourAccueil(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/admindashboard.fxml"));

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

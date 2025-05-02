package tn.esprit.controller;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entity.Evenement;
import tn.esprit.service.EvenementService;
import tn.esprit.utils.MailService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PatientEventController {

    @FXML
    private TextField searchField;
    @FXML private Button donateButton;

    @FXML
    private ListView<String> eventListView;
    @FXML
    private TextField emailField;

    @FXML
    private ImageView qrImageView;

    @FXML
    private Button downloadButton;

    private final EvenementService evenementService = new EvenementService();
    private List<Evenement> evenements;
    private ObservableList<String> eventNames;
    private Path currentQRPath;
    private List<Integer> claimedEventIndices = new ArrayList<>();
    @FXML
    private Button chooseButton;
    private ObservableList<Evenement> displayedEvents;

    private Evenement currentSelectedEvent;
    private int currentSelectedIndex = -1;



    @FXML
    public void initialize() {
        evenements = evenementService.getAllEvenements();

        eventNames = FXCollections.observableArrayList();
        displayedEvents = FXCollections.observableArrayList(evenements); // 🔥 New displayed events list

        for (Evenement e : displayedEvents) {
            eventNames.add(e.getNom() + " - " + e.getCategorie());
        }

        eventListView.setItems(eventNames);
        eventListView.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setDisable(false);
                    setStyle("");
                } else {
                    setText(item);
                    int index = getIndex();

                    Evenement evt = displayedEvents.get(index);
                    boolean isExpired = evt.isExpired();
                    boolean isClaimed = claimedEventIndices.contains(index);

                    if (isExpired) {
                        setText(item + " (expiré)");
                        setDisable(true);
                        setOpacity(0.5);
                        setStyle("-fx-background-color: lightgray;");
                    } else if (isClaimed) {
                        setDisable(true);
                        setOpacity(0.5);
                        setStyle("-fx-background-color: #eee;");
                    } else {
                        setDisable(false);
                        setOpacity(1.0);
                        setStyle("");
                    }
                }
            }
        });


        eventListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            int index = newVal.intValue();

            if (index >= 0 && index < displayedEvents.size()) {
                if (!claimedEventIndices.contains(index)) {
                    currentSelectedEvent = displayedEvents.get(index); // 🔥 Use displayedEvents, not evenements
                    currentSelectedIndex = index;
                    generateQRCode(currentSelectedEvent);
                    chooseButton.setVisible(true);
                    downloadButton.setVisible(true);
                } else {
                    chooseButton.setVisible(false);
                    downloadButton.setVisible(false);
                }
            }
        });
        eventListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                int index = eventListView.getSelectionModel().getSelectedIndex();
                if (index >= 0 && index < evenements.size()) {
                    currentSelectedEvent = evenements.get(index);
                    currentSelectedIndex = index;
                    generateQRCode(currentSelectedEvent);
                    chooseButton.setVisible(true);
                    downloadButton.setVisible(true);
                    donateButton.setVisible(true); // ✅ show the donate button
                }
            }
        });

    }
    @FXML
    public void sendQrByEmail() {
        if (currentQRPath != null && emailField.getText() != null && !emailField.getText().isEmpty()) {
            File qrFile = currentQRPath.toFile();
            String email = emailField.getText().trim();

            // Send email
            MailService.sendEmailWithAttachment(email, qrFile);

            // 🎉 Confirmation Alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Email envoyé");
            alert.setHeaderText("✅ Succès");
            alert.setContentText("Le QR Code a été envoyé à :\n" + email);
            alert.showAndWait();

        } else {
            // ❗ Email missing or QR not generated
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur");
            alert.setHeaderText("Champs manquants");
            alert.setContentText("Veuillez entrer une adresse email valide et générer un QR Code avant l'envoi.");
            alert.showAndWait();
        }
    }
    @FXML
    public void openDonationForm() {
        if (currentSelectedEvent == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/donationForm.fxml"));
            Parent root = loader.load();

            // Pass the selected event's ID to the donation controller
            tn.esprit.controller.DonationController controller = loader.getController();
            controller.setEvenementId(currentSelectedEvent.getId());

            Stage stage = new Stage();
            stage.setTitle("Faire un don");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void chooseEvent() {
        if (currentSelectedIndex != -1 && currentSelectedEvent != null) {
            claimedEventIndices.add(currentSelectedIndex); // Just mark as claimed ✅

            qrImageView.setImage(null);
            chooseButton.setVisible(false);
            downloadButton.setVisible(false);

            currentSelectedIndex = -1;
            currentSelectedEvent = null;

            eventListView.refresh(); // 🔥 Important: refresh the ListView
            System.out.println("✅ Événement choisi avec succès !");
        }
    }


    @FXML
    public void filterEvents() {
        String searchText = searchField.getText().toLowerCase();

        List<Evenement> filtered = evenements.stream()
                .filter(e -> (e.getNom() + " " + e.getCategorie()).toLowerCase().contains(searchText))
                .collect(Collectors.toList());

        displayedEvents.setAll(filtered);

        ObservableList<String> filteredNames = FXCollections.observableArrayList();
        for (Evenement e : displayedEvents) {
            filteredNames.add(e.getNom() + " - " + e.getCategorie());
        }

        eventListView.setItems(filteredNames);
    }


    private void generateQRCode(Evenement evenement) {
        try {
            Files.createDirectories(Paths.get("patient_qrcodes"));

            String qrContent = "Nom=" + evenement.getNom()
                    + ";Catégorie=" + evenement.getCategorie()
                    + ";Date=" + evenement.getDate()
                    + ";Pass=" + evenement.getPassCode();

            currentQRPath = Paths.get("patient_qrcodes/" + evenement.getNom().replaceAll("\\s+", "_") + ".png");

            BitMatrix matrix = new MultiFormatWriter().encode(qrContent, BarcodeFormat.QR_CODE, 300, 300);
            MatrixToImageWriter.writeToPath(matrix, "PNG", currentQRPath);

            qrImageView.setImage(new Image(currentQRPath.toUri().toString()));
            downloadButton.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void downloadQRCode() {
        if (currentQRPath != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer QR Code");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image PNG", "*.png"));

            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                try (FileOutputStream out = new FileOutputStream(file)) {
                    Files.copy(currentQRPath, out);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

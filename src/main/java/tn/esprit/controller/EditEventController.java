package tn.esprit.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entity.Evenement;
import tn.esprit.service.EvenementService;

import java.time.LocalDate;

public class EditEventController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField categorieField;

    @FXML
    private DatePicker datePicker;

    private Evenement evenementToEdit;

    private final EvenementService evenementService = new EvenementService();

    public void setEvenement(Evenement e) {
        this.evenementToEdit = e;

        // Remplir les champs avec les données existantes
        nomField.setText(e.getNom());
        categorieField.setText(e.getCategorie());
        datePicker.setValue(e.getDate());
    }
    public void setEvenementToEdit(Evenement evenement) {
        setEvenement(evenement); // appelle la méthode déjà existante
    }

    @FXML
    public void modifierEvenement(ActionEvent event) {
        if (evenementToEdit != null) {
            evenementToEdit.setNom(nomField.getText());
            evenementToEdit.setCategorie(categorieField.getText());
            evenementToEdit.setDate(datePicker.getValue());

            evenementService.modifierEvenement(evenementToEdit);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setContentText("✅ Événement modifié avec succès !");
            alert.showAndWait();

            // Fermer la fenêtre
            ((Stage) nomField.getScene().getWindow()).close();
        }
    }
}

package tn.esprit.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import tn.esprit.entity.Donation;
import tn.esprit.service.DonationService;

public class DonationController {

    @FXML private TextField donorNameField;
    @FXML private TextField amountField;
    @FXML private TextArea commentField;

    private final DonationService donationService = new DonationService();
    private int evenementId;

    public void setEvenementId(int id) {
        this.evenementId = id;
    }

    @FXML
    public void submitDonation() {
        try {
            Donation d = new Donation();
            d.setEvenementId(evenementId);
            d.setDonorName(donorNameField.getText());
            d.setAmount(Double.parseDouble(amountField.getText()));
            d.setComment(commentField.getText());

            donationService.addDonation(d);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText("Don enregistré");
            alert.setContentText("Merci pour votre contribution !");
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible d'enregistrer le don");
            alert.setContentText("Veuillez vérifier les informations saisies.");
            alert.showAndWait();
        }
    }
}

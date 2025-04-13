package gui;

import entities.Commande;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import services.ServiceCommande;
import services.ServiceProduit;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class itemCommandeController implements Initializable {

    @FXML
    private Button btnModifierCommande;

    @FXML
    private Button btnSupprimerCommande;

    @FXML
    private AnchorPane itemCommandePane;

    @FXML
    private Label labelDate;

    @FXML
    private Label labelProduit;

    @FXML
    private Label labelStatut;

    @FXML
    private Label labelTotale;


    private static int id;

    public int getId(){
        return this.id;
    }

    Commande cmd;


    ServiceProduit sp = new ServiceProduit();

    public void setData (Commande cmd){
        this.cmd = cmd;

        try {
            labelProduit.setText(sp.getById(cmd.getProduits_id()).getNom());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        labelDate.setText(String.valueOf(cmd.getDate()));
        labelTotale.setText(String.valueOf(cmd.getTotale()));
        labelStatut.setText(cmd.getStatut());
        this.id=cmd.getId();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    void open_UpdateCommande(ActionEvent event) throws IOException {
        Parent fxml= FXMLLoader.load(getClass().getResource("updateCommande.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Update Commande");
        stage.setScene(new Scene(fxml));
        stage.showAndWait();

    }

    @FXML
    void supprimerCommande(ActionEvent event) throws SQLException {
        ServiceCommande sc = new ServiceCommande();

        // Afficher une boîte de dialogue de confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Voulez-vous vraiment supprimer cette Commande ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Récupérer l'ID de la commande sélectionnée
            int id = this.cmd.getId();

            // Supprimer la commande de la base de données
            sc.supprimer(id);
        }

    }
}

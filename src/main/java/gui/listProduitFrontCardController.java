package gui;

import entities.Commande;
import entities.Produit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import services.ServiceCommande;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class listProduitFrontCardController implements Initializable {

    @FXML
    private Label labelDate;

    @FXML
    private ImageView labelImg;

    @FXML
    private Label labelNom;

    @FXML
    private Label labelPrix;

    @FXML
    private Label labelType;


    Produit prod;
    private static int idProd;

    public int getIdEve(){
        return this.idProd;
    }
    public void setData (Produit prod){
        this.prod = prod;
        labelNom.setText(prod.getNom());
        labelDate.setText(String.valueOf(prod.getDate()));
        labelType.setText(prod.getType());
        labelPrix.setText(String.valueOf(prod.getPrix()));
        labelImg.setImage(new Image("C:\\Users\\telli\\OneDrive\\Bureau\\piJava\\src\\main\\java\\uploads\\"+prod.getImage()));
        this.idProd=prod.getId();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    void ajouterCommande(ActionEvent event) throws IOException {
        int produit_id = this.idProd;
        Date date = null;
        try {
            LocalDate localDate = LocalDate.now();
            if (localDate != null) {
                Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
                date = Date.from(instant);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        float totale = prod.getPrix();
        String statut = "En Attente";

        if((produit_id != 0) && (date != null) && (statut != null)){
            Commande cmd = new Commande(
                    produit_id, date, totale, statut);
            ServiceCommande sc = new ServiceCommande();
            sc.ajouter(cmd);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Ajouté avec succès");
            alert.setHeaderText(null);
            alert.setContentText("Votre Commande a été ajoutée avec succès.");
            Optional<ButtonType> option = alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Probleme d'ajout !");
            alert.setHeaderText(null);
            alert.setContentText("Vous devez vérifier tous les détails concernant votre Commande et Produit.");
            Optional<ButtonType> option = alert.showAndWait();
        }




    }
}

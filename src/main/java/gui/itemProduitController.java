package gui;

import entities.Produit;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import services.ServiceProduit;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class itemProduitController implements Initializable {

    @FXML
    private Button btnModifierProduit;

    @FXML
    private Button btnSupprimerProduit;

    @FXML
    private AnchorPane itemProduitPane;

    @FXML
    private Label labelDate;

    @FXML
    private Label labelDescription;

    @FXML
    private ImageView labelImage;

    @FXML
    private Label labelNom;

    @FXML
    private Label labelPrix;

    @FXML
    private Label labelType;


    private static int id;

    public int getId(){
        return this.id;
    }

    Produit prod;
    public void setData (Produit prod){
        this.prod = prod;

        labelNom.setText(prod.getNom());
        labelDescription.setText(prod.getDescription());
        labelType.setText(prod.getType());
        labelDate.setText(String.valueOf(prod.getDate()));
        labelPrix.setText(String.valueOf(prod.getPrix())+" DT");
        labelImage.setImage(new Image("file:src/main/java/uploads/"+prod.getImage()));
        this.id=prod.getId();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    void open_UpdateProduit(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("updateProduit.fxml"));
        Parent root = loader.load();
        updateProduitController controller = loader.getController();
        controller.setProductId(this.id);
        Stage stage = new Stage();
        stage.setTitle("Update Produit");
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    @FXML
    void supprimerProduit(ActionEvent event) throws SQLException {
        ServiceProduit sp = new ServiceProduit();

        // Afficher une boîte de dialogue de confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Voulez-vous vraiment supprimer ce Produit ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Récupérer l'ID du produit sélectionnée
            int id = this.prod.getId();

            // Supprimer le produit de la base de données
            sp.supprimer(id);
        }
    }
}

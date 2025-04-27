package gui;

import entities.Produit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.ServiceProduit;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class listProduitController implements Initializable {

    @FXML
    private AnchorPane listProduitPane;

    @FXML
    private VBox vBox;

    public void refreshProductList() {
        vBox.getChildren().clear(); // Clear existing products
        try {
            ServiceProduit sp = new ServiceProduit();
            List<Produit> prods = sp.Show();

            for(int i=0;i<prods.size();i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("itemProduit.fxml"));
                try {
                    AnchorPane anchorPane = fxmlLoader.load();
                    HBox hBox = (HBox) anchorPane.getChildren().get(0);
                    itemProduitController itemController = fxmlLoader.getController();
                    itemController.setData(prods.get(i));
                    vBox.getChildren().add(hBox);
                } catch (IOException ex) {
                    Logger.getLogger(itemProduitController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshProductList();
    }


    @FXML
    void open_Stat(ActionEvent event) throws IOException{
        Parent fxml= FXMLLoader.load(getClass().getResource("Statistiques.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Statistiques des Produits");
        stage.setScene(new Scene(fxml));
        stage.showAndWait();
    }
}

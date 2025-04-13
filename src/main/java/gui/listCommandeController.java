package gui;

import entities.Commande;
import entities.Produit;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import services.ServiceCommande;
import services.ServiceProduit;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class listCommandeController implements Initializable {

    @FXML
    private AnchorPane listCommandePane;

    @FXML
    private VBox vBox;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            ServiceCommande sc = new ServiceCommande();
            List<Commande> cmds = sc.Show();

            for(int i=0;i<cmds.size();i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("itemCommande.fxml"));
                try {
                    AnchorPane anchorPane = fxmlLoader.load();
                    HBox hBox = (HBox) anchorPane.getChildren().get(0);
                    itemCommandeController itemController = fxmlLoader.getController();
                    itemController.setData(cmds.get(i));
                    vBox.getChildren().add(hBox);
                } catch (IOException ex) {
                    Logger.getLogger(itemCommandeController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

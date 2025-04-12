package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class gestionCommandeController implements Initializable {

    @FXML
    private Button btnAddCommande;

    @FXML
    private Button btnListCommande;

    @FXML
    private AnchorPane gestionCommandePane;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    void goToPages(ActionEvent event) throws IOException {
        if(event.getSource()== btnAddCommande){
            Parent fxml= FXMLLoader.load(getClass().getResource("addCommande.fxml"));
            gestionCommandePane.getChildren().removeAll();
            gestionCommandePane.getChildren().setAll(fxml);
        }else if(event.getSource()==btnListCommande){
            Parent fxml= FXMLLoader.load(getClass().getResource("listCommande.fxml"));
            gestionCommandePane.getChildren().removeAll();
            gestionCommandePane.getChildren().setAll(fxml);
        }
    }
}

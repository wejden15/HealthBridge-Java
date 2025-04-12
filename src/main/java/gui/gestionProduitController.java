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

public class gestionProduitController implements Initializable {


    @FXML
    private Button btnAddProduit;

    @FXML
    private Button btnListProduit;

    @FXML
    private AnchorPane gestionProduitPane;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    void goToPages(ActionEvent event) throws IOException {
        if(event.getSource()== btnAddProduit){
            Parent fxml= FXMLLoader.load(getClass().getResource("addProduit.fxml"));
            gestionProduitPane.getChildren().removeAll();
            gestionProduitPane.getChildren().setAll(fxml);
        }else if(event.getSource()==btnListProduit){
            Parent fxml= FXMLLoader.load(getClass().getResource("listProduit.fxml"));
            gestionProduitPane.getChildren().removeAll();
            gestionProduitPane.getChildren().setAll(fxml);
        }
    }
}

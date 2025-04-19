package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    private Button btnCommandes;

    @FXML
    private Button btnHome;

    @FXML
    private Button btnProduits;

    @FXML
    private AnchorPane view_pages;
    @FXML
    private Button btnAppointment;

    @FXML
    private Button btnQuizzes;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    @FXML
    void switchForm(ActionEvent event) throws IOException {
        if(event.getSource()== btnProduits){
            Parent fxml= FXMLLoader.load(getClass().getResource("gestionProduit.fxml"));
            view_pages.getChildren().removeAll();
            view_pages.getChildren().setAll(fxml);
        }else if (event.getSource()==btnCommandes){
            Parent fxml= FXMLLoader.load(getClass().getResource("gestionCommande.fxml"));
            view_pages.getChildren().removeAll();
            view_pages.getChildren().setAll(fxml);
        } else if (event.getSource()==btnAppointment){

            Parent fxml= FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
            view_pages.getChildren().removeAll();
            view_pages.getChildren().setAll(fxml);
        }
        else if (event.getSource()==btnQuizzes){

            Parent fxml= FXMLLoader.load(getClass().getResource("quiz.fxml"));
            view_pages.getChildren().removeAll();
            view_pages.getChildren().setAll(fxml);
        }


    }
}
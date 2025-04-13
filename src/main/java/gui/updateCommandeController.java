package gui;

import entities.Commande;
import entities.Produit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import services.ServiceCommande;
import services.ServiceProduit;

import java.io.IOException;
import java.net.URL;
import java.security.Provider;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class updateCommandeController implements Initializable {

    @FXML
    private Button btnClearCommande;

    @FXML
    private Button btnUpdateCommande;

    @FXML
    private DatePicker txtDate;

    @FXML
    private ComboBox<String> txtProduit;

    @FXML
    private ComboBox<String> txtStatut;

    @FXML
    private TextField txtTotale;

    @FXML
    private AnchorPane updateCommandePane;


    ServiceProduit sp = new ServiceProduit();
    List<Produit> prods = sp.Show();
    private int idProd=-1;


    Commande cmd;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtStatut.getItems().addAll("En Attente","Livrée","Expediée");

        Map<String, Integer> valuesMap = new HashMap<>();
        for(Produit p : prods){
            txtProduit.getItems().add(p.getNom());
            valuesMap.put(p.getNom(),p.getId());
        }

        txtProduit.setOnAction(event ->{
            String SelectedOption = null;
            SelectedOption = txtProduit.getValue();
            int SelectedValue = 0;
            SelectedValue = valuesMap.get(SelectedOption);
            idProd = SelectedValue;
        });



        //Remplissage du formulaire à partir de la bd
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("itemCommande.fxml"));
        try {
            AnchorPane anchorPane = fxmlLoader.load();
            HBox hBox = (HBox) anchorPane.getChildren().get(0);
            itemCommandeController item = fxmlLoader.getController();
            ServiceCommande sc = new ServiceCommande();


            cmd = sc.getById(item.getId());
            idProd = cmd.getProduits_id();
            txtProduit.setValue(sp.getById(idProd).getNom());
            txtTotale.setText(String.valueOf(cmd.getTotale()));

            /* Convert Date */
            LocalDate ConvertedDate = null;
            if (cmd.getDate() instanceof java.sql.Date) {
                ConvertedDate = ((java.sql.Date) cmd.getDate()).toLocalDate();
            } else {
                ConvertedDate = cmd.getDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            }
            /* End */

            txtDate.setValue(ConvertedDate);
            txtStatut.setValue(cmd.getStatut());

        } catch (IOException ex) {
            Logger.getLogger(itemCommandeController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void UpdateCommande(ActionEvent event) {
        //check if not empty
        if(event.getSource() == btnUpdateCommande){
            if (txtTotale.getText().isEmpty() || txtStatut.getItems().isEmpty()
                    || idProd == -1)
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Information manquante");
                alert.setHeaderText(null);
                alert.setContentText("Vous devez remplir tous les détails concernant votre Commande.");
                Optional<ButtonType> option = alert.showAndWait();

            } else {
                modifierCommande();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Modifiée avec succès");
                alert.setHeaderText(null);
                alert.setContentText("Votre Commande a été modifiée avec succès.");
                Optional<ButtonType> option = alert.showAndWait();
            }
        }
        if(event.getSource() == btnClearCommande){
            clearFieldsCommande();
        }
    }

    @FXML
    void clearFieldsCommande() {
        txtDate.getEditor().clear();
        txtTotale.clear();
    }

    void modifierCommande(){
        int produit_id = idProd;
        Date date = null;
        try {
            LocalDate localDate = txtDate.getValue();
            if (localDate != null) {
                Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
                date = Date.from(instant);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        Float totale = Float.parseFloat(txtTotale.getText());
        String statut = txtStatut.getValue();


        Commande commande = new Commande(
                cmd.getId(),
                produit_id, date, totale, statut);
        ServiceCommande sc = new ServiceCommande();
        sc.modifier(commande);
    }
}

package gui;

import entities.Commande;
import entities.Produit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import services.ServiceCommande;
import services.ServiceProduit;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class addCommandeController implements Initializable {

    @FXML
    private AnchorPane addCommandePane;

    @FXML
    private Button btnAddCommande;

    @FXML
    private Button btnClearCommande;

    @FXML
    private ComboBox<String> txtProduit;

    @FXML
    private ComboBox<String> txtStatut;

    @FXML
    private TextField txtTotale;


    ServiceProduit sp = new ServiceProduit();
    List<Produit> prods = sp.Show();
    private int idProd=-1;

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

    }

    @FXML
    void AjoutCommande(ActionEvent event) {
        //check if not empty
        if(event.getSource() == btnAddCommande){
            if (txtTotale.getText().isEmpty() || txtStatut.getItems().isEmpty() ||
                    idProd == -1 )
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Information manquante");
                alert.setHeaderText(null);
                alert.setContentText("Vous devez remplir tous les détails concernant votre Commande.");
                Optional<ButtonType> option = alert.showAndWait();

            } else {
                ajouterCommande();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Ajouté avec succès");
                alert.setHeaderText(null);
                alert.setContentText("Votre Commande a été ajoutée avec succès.");
                Optional<ButtonType> option = alert.showAndWait();

                clearFieldsCommande();
            }
        }
        if(event.getSource() == btnClearCommande){
            clearFieldsCommande();
        }
    }

    @FXML
    void clearFieldsCommande() {
        txtTotale.clear();
    }

    private void ajouterCommande() {
        // From Formulaire
        int produit_id = idProd;
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
        float totale = Float.parseFloat(txtTotale.getText());
        String statut = txtStatut.getValue();


        Commande cmd = new Commande(
                produit_id, date, totale, statut);
        ServiceCommande sc = new ServiceCommande();
        sc.ajouter(cmd);
    }
}

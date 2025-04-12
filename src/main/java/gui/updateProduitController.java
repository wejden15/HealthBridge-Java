package gui;

import entities.Commande;
import entities.Produit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import services.ServiceCommande;
import services.ServiceProduit;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class updateProduitController implements Initializable {

    @FXML
    private Button btnClearProduit;

    @FXML
    private Button btnUpdateProduit;

    @FXML
    private ImageView imageInput;

    @FXML
    private DatePicker txtDate;

    @FXML
    private TextArea txtDescription;

    @FXML
    private TextField txtNom;

    @FXML
    private TextField txtPrix;

    @FXML
    private ComboBox<String> txtType;

    @FXML
    private AnchorPane updateProduitPane;


    Produit prod;


    private File selectedImageFile;
    private String imageName = null ;

    @FXML
    void ajouterImage(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        selectedImageFile = fileChooser.showOpenDialog(imageInput.getScene().getWindow());
        if (selectedImageFile != null) {
            Image image = new Image(selectedImageFile.toURI().toString());
            imageInput.setImage(image);

            // Générer un nom de fichier unique pour l'image
            String uniqueID = UUID.randomUUID().toString();
            String extension = selectedImageFile.getName().substring(selectedImageFile.getName().lastIndexOf("."));
            imageName = uniqueID + extension;

            Path destination = Paths.get(System.getProperty("user.dir"), "src", "main", "java", "uploads", imageName);
            Files.copy(selectedImageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtType.getItems().addAll("Cosmétique","Paramedical","Visage","Cheveux","etc");

        /*Remplissage du formulaire à partit de la bd*/
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("itemProduit.fxml"));
        try {
            AnchorPane anchorPane = fxmlLoader.load();
            HBox hBox = (HBox) anchorPane.getChildren().get(0);
            itemProduitController item = fxmlLoader.getController();
            ServiceProduit sp = new ServiceProduit();


            prod = sp.getById(item.getId());

            txtNom.setText(prod.getNom());
            txtType.setValue(prod.getType());
            txtPrix.setText(String.valueOf(prod.getPrix()));

            /* Convert Date */
            LocalDate ConvertedDate = null;
            if (prod.getDate() instanceof java.sql.Date) {
                ConvertedDate = ((java.sql.Date) prod.getDate()).toLocalDate();
            } else {
                ConvertedDate = prod.getDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            }
            /* End */
            txtDate.setValue(ConvertedDate);
            txtDescription.setText(prod.getDescription());
            imageName = prod.getImage();
            imageInput.setImage(new Image("C:\\Users\\telli\\OneDrive\\Bureau\\piJava\\src\\main\\java\\uploads\\"+imageName));

        } catch (IOException ex) {
            Logger.getLogger(itemCommandeController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }




    @FXML
    void UpdateProduit(ActionEvent event) {
        //check if not empty
        if(event.getSource() == btnUpdateProduit){
            if (txtNom.getText().isEmpty() || txtType.getItems().isEmpty() || txtPrix.getText().isEmpty()
                    || txtDescription.getText().isEmpty() || imageName == null)
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Information manquante");
                alert.setHeaderText(null);
                alert.setContentText("Vous devez remplir tous les détails concernant votre Produit.");
                Optional<ButtonType> option = alert.showAndWait();

            } else {
                modifierProduit();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Modifiée avec succès");
                alert.setHeaderText(null);
                alert.setContentText("Votre Produit a été modifiée avec succès.");
                Optional<ButtonType> option = alert.showAndWait();
            }
        }
        if(event.getSource() == btnClearProduit){
            clearFieldsProduit();
        }
    }

    @FXML
    void clearFieldsProduit() {
        txtDate.getEditor().clear();
        txtDescription.clear();
        txtNom.clear();
        txtPrix.clear();
        txtType.getItems().clear();
    }


    void modifierProduit(){
        // From Formulaire
        String nom = txtNom.getText();
        String description = txtDescription.getText();
        String type = txtType.getValue();
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
        float prix = Float.parseFloat(txtPrix.getText());
        String image = imageName;


        Produit produit = new Produit(
                prod.getId(),
                nom, description, type, date, prix, image);
        ServiceProduit sp = new ServiceProduit();
        sp.modifier(produit);
    }

}

package gui;

import entities.Produit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.ServiceProduit;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;

public class addProduitController implements Initializable {

    @FXML
    private AnchorPane addProduitPane;

    @FXML
    private Button btnAddProduit;

    @FXML
    private Button btnClearProduit;

    @FXML
    private ImageView imageInput;

    @FXML
    private TextArea txtDescription;

    @FXML
    private TextField txtNom;

    @FXML
    private TextField txtPrix;

    @FXML
    private ComboBox<String> txtType;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtType.getItems().addAll("Cosmétique","Paramedical","Visage","Cheveux","etc");
    }

    @FXML
    void AjoutProduit(ActionEvent event) {
        //check if not empty
        if(event.getSource() == btnAddProduit){
            if (txtDescription.getText().isEmpty() || txtNom.getText().isEmpty() || txtPrix.getText().isEmpty()
                    || txtType.getItems().isEmpty() || imageName == null)
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Information manquante");
                alert.setHeaderText(null);
                alert.setContentText("Vous devez remplir tous les détails concernant votre Produit.");
                Optional<ButtonType> option = alert.showAndWait();

            } else {
                ajouterProduit();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Ajouté avec succès");
                alert.setHeaderText(null);
                alert.setContentText("Votre Produit a été ajoutée avec succès.");
                Optional<ButtonType> option = alert.showAndWait();

                clearFieldsProduit();
            }
        }
        if(event.getSource() == btnClearProduit){
            clearFieldsProduit();
        }
    }


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

    @FXML
    void clearFieldsProduit() {
        txtDescription.clear();
        txtNom.clear();
        txtPrix.clear();
        txtType.getItems().clear();
        imageInput.setImage(null);
    }

    private void ajouterProduit() {
        // From Formulaire
        String nom = filterWords(txtNom.getText());
        String description = filterWords(txtDescription.getText());
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

        Produit prod = new Produit(
                nom, description, type, date, prix, image);
        ServiceProduit sp = new ServiceProduit();
        sp.ajouter(prod);
    }

    public static String filterWords(String text) {
        String[] filterWords = {"ahla", "word2", "word3"};
        String[] data = text.split("\\s+");
        String str = "";
        for (String s : data) {
            boolean g = false;
            for (String lib : filterWords) {
                if (s.equals(lib)) {
                    String t = "";
                    for (int i = 0; i < s.length(); i++) t += "*";
                    str += t + " ";
                    g = true;
                    break;
                }
            }
            if (!g) str += s + " ";
        }
        return str.trim();
    }
}

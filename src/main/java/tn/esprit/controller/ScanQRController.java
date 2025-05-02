package tn.esprit.controller;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ScanQRController {

    @FXML
    private VBox eventDetails;

    @FXML
    public void handleScan(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image QR Code");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                Result result = new MultiFormatReader().decode(bitmap);
                String qrText = result.getText();

                displayEventDetails(qrText);

            } catch (IOException | NotFoundException e) {
                eventDetails.getChildren().clear();
                eventDetails.getChildren().add(new Label("❗ QR Code non reconnu !"));
                e.printStackTrace();
            }
        }
    }

    private void displayEventDetails(String qrText) {
        eventDetails.getChildren().clear();

        String[] fields = qrText.split(";");

        for (String field : fields) {
            Label label = new Label(field.replace("=", ": "));
            label.setStyle("-fx-font-size: 18px; -fx-text-fill: #333;");
            eventDetails.getChildren().add(label);
        }
    }
}

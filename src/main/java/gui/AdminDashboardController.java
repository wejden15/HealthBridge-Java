package gui;

import entities.Doctor;
import entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import services.DoctorService;
import services.UserService;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

public class AdminDashboardController extends DashboardController {
    @FXML private TableView<User> doctorTableView;
    @FXML private TextField specialtyField;
    @FXML private ImageView imagePreview;

    private File selectedImageFile;
    private final UserService userService = new UserService();
    private final DoctorService doctorService = new DoctorService();

    @FXML
    public void initialize() {
        // Load only users with Doctor role
        List<User> doctors = userService.afficher().stream()
                .filter(user -> user.getRole().equalsIgnoreCase("Doctor"))
                .collect(Collectors.toList());
        doctorTableView.getItems().addAll(doctors);
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        selectedImageFile = fileChooser.showOpenDialog(imagePreview.getScene().getWindow());
        if (selectedImageFile != null) {
            imagePreview.setImage(new Image(selectedImageFile.toURI().toString()));
        }
    }

    @FXML
    private void handleApproveDoctor() {
        User selectedUser = doctorTableView.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            showAlert("Error", "Please select a doctor from the list");
            return;
        }

        if (specialtyField.getText().isEmpty()) {
            showAlert("Error", "Please enter a specialty");
            return;
        }

        if (selectedImageFile == null) {
            showAlert("Error", "Please select a profile picture");
            return;
        }

        try {
            // Create uploads directory if not exists
            File uploadDir = new File("uploads");
            if (!uploadDir.exists()) uploadDir.mkdir();

            // Copy image to uploads directory
            File destFile = new File(uploadDir, selectedImageFile.getName());
            Files.copy(selectedImageFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Create doctor with email as name
            Doctor doctor = new Doctor(
                    selectedUser.getUsername(),  // Use email as name
                    specialtyField.getText(),
                    destFile.getAbsolutePath()
            );

            doctorService.addDoctor(doctor);
            showAlert("Success", "Doctor approved successfully!");
            clearFields();
        } catch (Exception e) {
            showAlert("Error", "Failed to approve doctor: " + e.getMessage());
        }
    }

    private void clearFields() {
        specialtyField.clear();
        imagePreview.setImage(null);
        selectedImageFile = null;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
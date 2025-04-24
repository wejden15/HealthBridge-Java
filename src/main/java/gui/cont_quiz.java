package gui;

import entities.quiz;
import javafx.scene.input.KeyCombination;
import services.QuizService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.sql.Date;
import java.util.function.Predicate;
import java.time.ZoneId;


public class cont_quiz implements Initializable {
    @FXML
    private TableView<quiz> quizTable;
    @FXML
    private TableColumn<quiz, String> nameColumn;
    @FXML
    private TableColumn<quiz, String> typeColumn;
    @FXML
    private TableColumn<quiz, Date> dateColumn;
    @FXML
    private TableColumn<quiz, Void> actionColumn;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> searchTypeComboBox;

    private final QuizService quizService = new QuizService();
    private final ObservableList<quiz> quizList = FXCollections.observableArrayList();
    private FilteredList<quiz> filteredQuizzes;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupSearchControls();
        setupSearch();
        loadQuizzes();
    }

    private void setupSearchControls() {
        // Initialize the search type combo box
        searchTypeComboBox.getItems().addAll("Name", "Type", "Date");
        searchTypeComboBox.setValue("Name");
        
        // Add listener for sorting
        searchTypeComboBox.setOnAction(event -> {
            String sortType = searchTypeComboBox.getValue();
            switch (sortType) {
                case "Name":
                    quizList.sort((q1, q2) -> q1.getName().compareToIgnoreCase(q2.getName()));
                    break;
                case "Type":
                    quizList.sort((q1, q2) -> q1.getType().compareToIgnoreCase(q2.getType()));
                    break;
                case "Date":
                    quizList.sort((q1, q2) -> q1.getDate().compareTo(q2.getDate()));
                    break;
            }
            quizTable.setItems(quizList);
        });
    }

    private void setupSearch() {
        // Initialize filtered list
        filteredQuizzes = new FilteredList<>(quizList, p -> true);
        
        // Add listener to search field
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredQuizzes.setPredicate(createPredicate(newValue));
        });

        // Add listener to search type combo box
        searchTypeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredQuizzes.setPredicate(createPredicate(searchField.getText()));
        });

        // Bind the filtered list to the table
        quizTable.setItems(filteredQuizzes);
    }

    private Predicate<quiz> createPredicate(String searchText) {
        return quiz -> {
            if (searchText == null || searchText.isEmpty()) return true;
            
            String lowerCaseFilter = searchText.toLowerCase();
            String searchType = searchTypeComboBox.getValue();

            switch (searchType) {
                case "Name":
                    return quiz.getName().toLowerCase().contains(lowerCaseFilter);
                case "Type":
                    return quiz.getType().toLowerCase().contains(lowerCaseFilter);
                case "Date":
                    return quiz.getDate().toString().toLowerCase().contains(lowerCaseFilter);
                default: // "All"
                    return quiz.getName().toLowerCase().contains(lowerCaseFilter)
                        || quiz.getType().toLowerCase().contains(lowerCaseFilter)
                        || quiz.getDate().toString().toLowerCase().contains(lowerCaseFilter);
            }
        };
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        actionColumn.setCellFactory(new Callback<TableColumn<quiz, Void>, TableCell<quiz, Void>>() {
            @Override
            public TableCell<quiz, Void> call(final TableColumn<quiz, Void> param) {
                return new TableCell<quiz, Void>() {
                    private final Button updateButton = new Button("Update");
                    private final Button deleteButton = new Button("Delete");
                    private final HBox buttons = new HBox(updateButton, deleteButton);

                    {
                        buttons.setSpacing(5);
                        updateButton.setOnAction(event -> {
                            quiz quiz = getTableView().getItems().get(getIndex());
                            updateQuiz(quiz);
                        });
                        deleteButton.setOnAction(event -> {
                            quiz quiz = getTableView().getItems().get(getIndex());
                            deleteQuiz(quiz);
                        });

                        // Style the buttons
                        updateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(buttons);
                        }
                    }
                };
            }
        });

        dateColumn.setCellFactory(column -> new TableCell<quiz, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toLocalDate().toString());
                }
            }
        });
    }

    private void loadQuizzes() {
        quizList.clear();
        quizList.addAll(quizService.getAll());
    }

    private void deleteQuiz(quiz quiz) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Quiz");
        alert.setHeaderText("Are you sure you want to delete this quiz?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                quizService.delete(quiz.getId());
                loadQuizzes();
            }
        });
    }

    private void updateQuiz(quiz quiz) {
        Dialog<quiz> dialog = new Dialog<>();
        dialog.setTitle("Update Quiz");
        dialog.setHeaderText(null);
        
        // Apply CSS styling
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/quiz.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-dialog");

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Create form container with proper styling
        VBox formContainer = new VBox(15);
        formContainer.getStyleClass().add("dialog-form");
        formContainer.setPadding(new Insets(20, 20, 10, 10));

        TextField nameField = new TextField(quiz.getName());
        nameField.setPromptText("Quiz Name");
        
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Single Choice", "Multiple Choice");
        typeComboBox.setValue(quiz.getType());

        DatePicker datePicker = new DatePicker(quiz.getDate().toLocalDate());
        datePicker.setPromptText("Select Quiz Date");

        // Add form controls to container with labels
        formContainer.getChildren().addAll(
            new Label("Name:"), nameField,
            new Label("Type:"), typeComboBox,
            new Label("Date:"), datePicker
        );

        dialog.getDialogPane().setContent(formContainer);

        Platform.runLater(nameField::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                LocalDate selectedDate = datePicker.getValue();
                if (selectedDate == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Please select a date for the quiz.");
                    
                    // Apply CSS styling to error dialog
                    DialogPane errorPane = alert.getDialogPane();
                    errorPane.getStylesheets().add(getClass().getResource("/styles/quiz.css").toExternalForm());
                    errorPane.getStyleClass().add("custom-dialog");
                    
                    alert.showAndWait();
                    return null;
                }
                quiz updatedQuiz = new quiz(
                    quiz.getId(),
                    nameField.getText(),
                    typeComboBox.getValue(),
                    Date.valueOf(selectedDate)
                );
                return updatedQuiz;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            try {
                quizService.update(result);
                loadQuizzes();
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("An error occurred while updating the quiz: " + e.getMessage());
                
                // Apply CSS styling to error dialog
                DialogPane errorPane = alert.getDialogPane();
                errorPane.getStylesheets().add(getClass().getResource("/styles/quiz.css").toExternalForm());
                errorPane.getStyleClass().add("custom-dialog");
                
                alert.showAndWait();
            }
        });
    }


    @FXML
    private void handleQuizNavigation() {

    }

    @FXML
    private void handleQuestionNavigation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("question.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) quizTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Questions");
            stage.setFullScreenExitHint(""); // No "Press ESC to exit fullscreen" message
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // Disable ESC key exit
            stage.setFullScreen(true); // Go fullscreen!
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAnswerNavigation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("answer.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) quizTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Answers");
            stage.setFullScreenExitHint(""); // No "Press ESC to exit fullscreen" message
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // Disable ESC key exit
            stage.setFullScreen(true); // Go fullscreen!
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTakeQuizNavigation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("take_quiz.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) quizTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Take Quiz");
            stage.setFullScreenExitHint(""); // No "Press ESC to exit fullscreen" message
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // Disable ESC key exit
            stage.setFullScreen(true); // Go fullscreen!
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddQuiz() {
        try {
            Dialog<quiz> dialog = new Dialog<>();
            dialog.setTitle("Add New Quiz");
            dialog.setHeaderText(null);
            
            // Apply CSS styling
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/styles/quiz.css").toExternalForm());
            dialogPane.getStyleClass().add("custom-dialog");
            
            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            // Create form controls with proper styling
            VBox formContainer = new VBox(15);
            formContainer.getStyleClass().add("dialog-form");
            
            Label nameLabel = new Label("Quiz Name:");
            TextField nameField = new TextField();
            nameField.setPromptText("Enter quiz name");
            
            Label typeLabel = new Label("Quiz Type:");
            ComboBox<String> typeComboBox = new ComboBox<>();
            typeComboBox.getItems().addAll("Multiple Choice", "True/False", "Short Answer");
            typeComboBox.setPromptText("Select Quiz Type");

            Label dateLabel = new Label("Quiz Date:");
            DatePicker datePicker = new DatePicker(LocalDate.now());
            datePicker.setPromptText("Select Quiz Date");

            formContainer.getChildren().addAll(
                nameLabel, nameField,
                typeLabel, typeComboBox,
                dateLabel, datePicker
            );

            dialog.getDialogPane().setContent(formContainer);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    LocalDate selectedDate = datePicker.getValue();
                    if (selectedDate == null) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Date Required");
                        alert.setContentText("Please select a date for the quiz.");
                        alert.showAndWait();
                        return null;
                    }
                    quiz newQuiz = new quiz();
                    newQuiz.setName(nameField.getText());
                    newQuiz.setType(typeComboBox.getValue());
                    newQuiz.setDate(Date.valueOf(selectedDate));
                    return newQuiz;
                }
                return null;
            });

            dialog.showAndWait().ifPresent(newQuiz -> {
                quizService.add(newQuiz);
                loadQuizzes(); // Refresh the table
            });
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error Adding Quiz");
            alert.setContentText("An error occurred while adding the quiz: " + e.getMessage());
            alert.showAndWait();
        }
    }

   
    @FXML
    private void handleCalendarNavigation(ActionEvent event) {
        try {
            // First add all quizzes to the calendar
            models.GoogleCalendar.addQuizzesToCalendar();

            // Then display the calendar
            models.GoogleCalendar.displayCalendar();

            // Show success message to user
            showAlert("Success", "Calendar has been updated with all quizzes", Alert.AlertType.INFORMATION);

        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update or display calendar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }



}
package com.esprit.controllers;

import com.esprit.models.quiz;
import com.esprit.services.QuizService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class cont_quiz implements Initializable {
    @FXML
    private TableView<quiz> quizTable;
    @FXML
    private TableColumn<quiz, String> nameColumn;
    @FXML
    private TableColumn<quiz, String> typeColumn;
    @FXML
    private TableColumn<quiz, Void> actionColumn;
    @FXML
    private Button createQuizButton;

    private final QuizService quizService = new QuizService();
    private final ObservableList<quiz> quizList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadQuizzes();
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

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
    }

    private void loadQuizzes() {
        quizList.clear();
        quizList.addAll(quizService.getAll());
        quizTable.setItems(quizList);
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
        dialog.setHeaderText("Please update quiz details");


        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);


        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField nameField = new TextField(quiz.getName());
        nameField.setPromptText("Quiz Name");
        
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Single Choice", "Multiple Choice");
        typeComboBox.setValue(quiz.getType());

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeComboBox, 1, 1);

        dialog.getDialogPane().setContent(grid);


        Platform.runLater(nameField::requestFocus);


        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                quiz updatedQuiz = new quiz(quiz.getId(), nameField.getText(), typeComboBox.getValue());
                return updatedQuiz;
            }
            return null;
        });


        dialog.showAndWait().ifPresent(result -> {
            quizService.update(result);
            loadQuizzes();
        });
    }

    @FXML
    private void handleCreateQuiz() {

        Dialog<quiz> dialog = new Dialog<>();
        dialog.setTitle("Create New Quiz");
        dialog.setHeaderText("Please enter quiz details");


        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);


        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Quiz Name");
        
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Single Choice", "Multiple Choice");
        typeComboBox.setValue("Single Choice"); // Set default value

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeComboBox, 1, 1);

        dialog.getDialogPane().setContent(grid);


        Platform.runLater(nameField::requestFocus);


        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return new quiz(nameField.getText(), typeComboBox.getValue());
            }
            return null;
        });


        dialog.showAndWait().ifPresent(result -> {
            quizService.add(result);
            loadQuizzes();
        });
    }

    @FXML
    private void handleQuizNavigation() {

    }

    @FXML
    private void handleQuestionNavigation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/question.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) quizTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Questions");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAnswerNavigation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/answer.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) quizTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Answers");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTakeQuizNavigation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/take_quiz.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) quizTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Take Quiz");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddQuiz() {
        try {

            Dialog<quiz> dialog = new Dialog<>();
            dialog.setTitle("Add New Quiz");
            dialog.setHeaderText("Enter quiz details");


            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);


            TextField nameField = new TextField();
            nameField.setPromptText("Quiz Name");
            ComboBox<String> typeComboBox = new ComboBox<>();
            typeComboBox.getItems().addAll("Multiple Choice", "True/False", "Short Answer");
            typeComboBox.setPromptText("Select Quiz Type");


            dialog.getDialogPane().setContent(new VBox(10, 
                new Label("Quiz Name:"), nameField,
                new Label("Quiz Type:"), typeComboBox));


            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    quiz newQuiz = new quiz();
                    newQuiz.setName(nameField.getText());
                    newQuiz.setType(typeComboBox.getValue());
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

    private void handleEditQuiz(quiz selectedQuiz) {
        try {

            Dialog<quiz> dialog = new Dialog<>();
            dialog.setTitle("Edit Quiz");
            dialog.setHeaderText("Edit quiz details");


            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);


            TextField nameField = new TextField(selectedQuiz.getName());
            ComboBox<String> typeComboBox = new ComboBox<>();
            typeComboBox.getItems().addAll("Multiple Choice", "True/False", "Short Answer");
            typeComboBox.setValue(selectedQuiz.getType());


            dialog.getDialogPane().setContent(new VBox(10, 
                new Label("Quiz Name:"), nameField,
                new Label("Quiz Type:"), typeComboBox));


            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    selectedQuiz.setName(nameField.getText());
                    selectedQuiz.setType(typeComboBox.getValue());
                    return selectedQuiz;
                }
                return null;
            });


            dialog.showAndWait().ifPresent(updatedQuiz -> {
                quizService.update(updatedQuiz);
                loadQuizzes(); // Refresh the table
            });
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error Editing Quiz");
            alert.setContentText("An error occurred while editing the quiz: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void handleDeleteQuiz(quiz selectedQuiz) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setHeaderText("Delete Quiz");
        confirmDialog.setContentText("Are you sure you want to delete the quiz '" + selectedQuiz.getName() + "'?");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    quizService.delete(selectedQuiz.getId());
                    loadQuizzes(); // Refresh the table
                } catch (Exception e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error Deleting Quiz");
                    alert.setContentText("An error occurred while deleting the quiz: " + e.getMessage());
                    alert.showAndWait();
                }
            }
        });
    }
}

package com.esprit.controllers;

import com.esprit.models.answers;
import com.esprit.models.question;
import com.esprit.models.quiz;
import com.esprit.services.AnswerService;
import com.esprit.services.QuestionService;
import com.esprit.services.QuizService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class cont_answer implements Initializable {
    @FXML
    private TableView<answers> answerTable;
    @FXML
    private TableColumn<answers, String> textColumn;
    @FXML
    private TableColumn<answers, String> questionColumn;
    @FXML
    private TableColumn<answers, Boolean> correctColumn;
    @FXML
    private TableColumn<answers, Void> actionColumn;
    @FXML
    private Button createAnswerButton;

    private final AnswerService answerService = new AnswerService();
    private final QuestionService questionService = new QuestionService();
    private final QuizService quizService = new QuizService();
    private final ObservableList<answers> answerList = FXCollections.observableArrayList();
    private final Map<Integer, String> questionTexts = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadQuestions();
        loadAnswers();
    }

    private void setupTableColumns() {
        textColumn.setCellValueFactory(cellData -> {
            String text = cellData.getValue().gettext_ans();
            return new javafx.beans.property.SimpleStringProperty(text);
        });
        
        correctColumn.setCellValueFactory(cellData -> {
            byte isCorrect = cellData.getValue().getis_correct();
            return new SimpleBooleanProperty(isCorrect == 1);
        });
        

        questionColumn.setCellFactory(column -> new TableCell<answers, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    answers a = getTableView().getItems().get(getIndex());
                    setText(questionTexts.get(a.getId_quest()));
                }
            }
        });


        actionColumn.setCellFactory(new Callback<TableColumn<answers, Void>, TableCell<answers, Void>>() {
            @Override
            public TableCell<answers, Void> call(final TableColumn<answers, Void> param) {
                return new TableCell<answers, Void>() {
                    private final Button updateButton = new Button("Update");
                    private final Button deleteButton = new Button("Delete");
                    private final HBox buttons = new HBox(5, updateButton, deleteButton);

                    {
                        updateButton.setOnAction(event -> {
                            answers answer = getTableView().getItems().get(getIndex());
                            showUpdateDialog(answer);
                        });

                        deleteButton.setOnAction(event -> {
                            answers answer = getTableView().getItems().get(getIndex());
                            deleteAnswer(answer);
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

    private void loadQuestions() {

        for (question q : questionService.rechercher()) {
            questionTexts.put(q.getId_ques(), q.getText());
        }
    }

    private void loadAnswers() {
        answerList.clear();
        answerList.addAll(answerService.rechercher());
        answerTable.setItems(answerList);
    }

    private void deleteAnswer(answers answer) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Answer");
        alert.setHeaderText("Are you sure you want to delete this answer?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                answerService.supprimer(answer);
                loadAnswers();
            }
        });
    }

    private void showUpdateDialog(answers answer) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Update Answer");
        dialog.setHeaderText("Update answer details");
        

        dialog.getDialogPane().setMinWidth(600);
        dialog.getDialogPane().setMinHeight(400);


        TextArea answerText = new TextArea(answer.gettext_ans());
        answerText.setPromptText("Enter answer text");
        answerText.setPrefRowCount(3);
        answerText.setPrefWidth(500);

        ComboBox<String> questionComboBox = new ComboBox<>();
        questionComboBox.setPromptText("Select Question");
        questionComboBox.getItems().addAll(questionTexts.values());
        questionComboBox.setValue(questionTexts.get(answer.getId_quest()));

        CheckBox correctCheckBox = new CheckBox("Correct Answer");
        correctCheckBox.setSelected(answer.getis_correct() == 1);


        dialog.getDialogPane().setContent(new VBox(10, 
            new Label("Answer Text:"), answerText,
            new Label("Question:"), questionComboBox,
            correctCheckBox
        ));


        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);


        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String text = answerText.getText().trim();
                String selectedQuestionText = questionComboBox.getValue();

                if (text.isEmpty() || selectedQuestionText == null) {
                    showAlert("Error", "Please fill in all fields");
                    return;
                }


                int questionId = -1;
                for (Map.Entry<Integer, String> entry : questionTexts.entrySet()) {
                    if (entry.getValue().equals(selectedQuestionText)) {
                        questionId = entry.getKey();
                        break;
                    }
                }

                if (questionId == -1) {
                    showAlert("Error", "Invalid question selection");
                    return;
                }


                answer.setText_ans(text);
                answer.setId_quest(questionId);
                answer.setIs_correct((byte) (correctCheckBox.isSelected() ? 1 : 0));
                
                // Update in database
                answerService.modifier(answer);
                
                // Refresh the table
                loadAnswers();
                

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Answer updated successfully!");
                successAlert.showAndWait();
            }
        });
    }

    @FXML
    private void handleCreateAnswer() {
        // First dialog to select quiz
        Dialog<ButtonType> quizDialog = new Dialog<>();
        quizDialog.setTitle("Select Quiz");
        quizDialog.setHeaderText("Choose a quiz first");

        ComboBox<String> quizComboBox = new ComboBox<>();
        quizComboBox.setPromptText("Select Quiz");
        

        for (quiz q : quizService.rechercher()) {
            quizComboBox.getItems().add(q.getName());
        }

        quizDialog.getDialogPane().setContent(new VBox(10, 
            new Label("Quiz:"), quizComboBox
        ));

        quizDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);


        quizDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String selectedQuizName = quizComboBox.getValue();
                if (selectedQuizName == null) {
                    showAlert("Error", "Please select a quiz");
                    return;
                }


                final quiz selectedQuiz = quizService.rechercher().stream()
                    .filter(q -> q.getName().equals(selectedQuizName))
                    .findFirst()
                    .orElse(null);

                if (selectedQuiz == null) {
                    showAlert("Error", "Invalid quiz selection");
                    return;
                }

                final int selectedQuizId = selectedQuiz.getId();
                final boolean isMultipleChoice = "Multiple Choice".equals(selectedQuiz.getType());


                Dialog<ButtonType> answerDialog = new Dialog<>();
                answerDialog.setTitle("Create New Answers");
                answerDialog.setHeaderText("Enter answer details for " + selectedQuizName);
                

                answerDialog.getDialogPane().setMinWidth(600);
                answerDialog.getDialogPane().setMinHeight(400);


                VBox answersContainer = new VBox(10);
                Button addAnswerButton = new Button("Add Another Answer");
                Button removeAnswerButton = new Button("Remove Last Answer");
                HBox buttonContainer = new HBox(10, addAnswerButton, removeAnswerButton);


                VBox answerFields = new VBox(10);
                answerFields.getChildren().add(createAnswerField());


                addAnswerButton.setOnAction(e -> {
                    answerFields.getChildren().add(createAnswerField());
                });

                removeAnswerButton.setOnAction(e -> {
                    if (answerFields.getChildren().size() > 1) {
                        answerFields.getChildren().remove(answerFields.getChildren().size() - 1);
                    }
                });

                ComboBox<String> questionComboBox = new ComboBox<>();
                questionComboBox.setPromptText("Select Question");
                

                questionService.rechercher().stream()
                    .filter(q -> q.getquiz_id() == selectedQuizId)
                    .forEach(q -> questionComboBox.getItems().add(q.getText()));


                ScrollPane scrollPane = new ScrollPane(answerFields);
                scrollPane.setFitToWidth(true);
                scrollPane.setPrefHeight(200);
                scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);


                answersContainer.getChildren().addAll(
                    new Label("Quiz Type: " + selectedQuiz.getType()),
                    new Label("Question:"), questionComboBox,
                    new Label("Answers:"), scrollPane,
                    buttonContainer
                );

                answerDialog.getDialogPane().setContent(answersContainer);
                answerDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);


                answerDialog.showAndWait().ifPresent(answerResponse -> {
                    if (answerResponse == ButtonType.OK) {
                        String selectedQuestionText = questionComboBox.getValue();
                        if (selectedQuestionText == null) {
                            showAlert("Error", "Please select a question");
                            return;
                        }


                        final int questionId = questionService.rechercher().stream()
                            .filter(q -> q.getText().equals(selectedQuestionText) && q.getquiz_id() == selectedQuizId)
                            .findFirst()
                            .map(question::getId_ques)
                            .orElse(-1);

                        if (questionId == -1) {
                            showAlert("Error", "Invalid question selection");
                            return;
                        }


                        long correctAnswersCount = answerFields.getChildren().stream()
                            .filter(node -> node instanceof HBox)
                            .map(node -> (HBox) node)
                            .filter(hbox -> ((CheckBox) hbox.getChildren().get(1)).isSelected())
                            .count();

                        if (correctAnswersCount == 0) {
                            showAlert("Error", "At least one answer must be marked as correct");
                            return;
                        }

                        if (!isMultipleChoice && correctAnswersCount > 1) {
                            showAlert("Error", "Single choice quiz can only have one correct answer");
                            return;
                        }


                        for (javafx.scene.Node node : answerFields.getChildren()) {
                            if (node instanceof HBox) {
                                HBox answerField = (HBox) node;
                                TextArea textArea = (TextArea) answerField.getChildren().get(0);
                                CheckBox correctCheckBox = (CheckBox) answerField.getChildren().get(1);
                                
                                String text = textArea.getText().trim();
                                if (!text.isEmpty()) {
                                    answers newAnswer = new answers(questionId, text, (byte) (correctCheckBox.isSelected() ? 1 : 0));
                                    answerService.ajouter(newAnswer);
                                }
                            }
                        }
                        loadAnswers();
                    }
                });
            }
        });
    }

    private HBox createAnswerField() {
        TextArea answerText = new TextArea();
        answerText.setPromptText("Enter answer text");
        answerText.setPrefRowCount(2);
        answerText.setPrefWidth(500);

        CheckBox correctCheckBox = new CheckBox("Correct Answer");
        
        HBox answerField = new HBox(10, answerText, correctCheckBox);
        answerField.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        return answerField;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleQuizNavigation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/quiz.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) answerTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleQuestionNavigation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/question.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) answerTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAnswerNavigation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/answer.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) answerTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTakeQuizNavigation() {
        navigateTo("take_quiz.fxml");
    }

    private void navigateTo(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
            Stage stage = (Stage) answerTable.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 
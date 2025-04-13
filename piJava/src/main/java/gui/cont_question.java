package gui;

import entities.question;
import entities.quiz;
import services.QuestionService;
import services.QuizService;
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

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class cont_question implements Initializable {
    @FXML
    private TableView<question> questionTable;
    @FXML
    private TableColumn<question, String> textColumn;
    @FXML
    private TableColumn<question, String> quizColumn;
    @FXML
    private TableColumn<question, Void> actionColumn;
    @FXML
    private Button createQuestionButton;

    private final QuestionService questionService = new QuestionService();
    private final QuizService quizService = new QuizService();
    private final ObservableList<question> questionList = FXCollections.observableArrayList();
    private final Map<Integer, String> quizNames = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadQuizzes();
        loadQuestions();
    }

    private void setupTableColumns() {
        textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        

        quizColumn.setCellFactory(column -> new TableCell<question, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    question q = getTableView().getItems().get(getIndex());
                    setText(quizNames.get(q.getquiz_id()));
                }
            }
        });


        actionColumn.setCellFactory(new Callback<TableColumn<question, Void>, TableCell<question, Void>>() {
            @Override
            public TableCell<question, Void> call(final TableColumn<question, Void> param) {
                return new TableCell<question, Void>() {
                    private final Button updateButton = new Button("Update");
                    private final Button deleteButton = new Button("Delete");
                    private final HBox buttons = new HBox(5, updateButton, deleteButton);

                    {
                        updateButton.setOnAction(event -> {
                            question question = getTableView().getItems().get(getIndex());
                            showUpdateDialog(question);
                        });

                        deleteButton.setOnAction(event -> {
                            question question = getTableView().getItems().get(getIndex());
                            deleteQuestion(question);
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
        for (quiz q : quizService.rechercher()) {
            quizNames.put(q.getId(), q.getName());
        }
    }

    private void loadQuestions() {
        questionList.clear();
        questionList.addAll(questionService.rechercher());
        questionTable.setItems(questionList);
    }

    private void deleteQuestion(question question) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Question");
        alert.setHeaderText("Are you sure you want to delete this question?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                questionService.supprimer(question);
                loadQuestions();
            }
        });
    }

    private void showUpdateDialog(question question) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Update Question");
        dialog.setHeaderText("Update question details");


        TextArea questionText = new TextArea(question.getText());
        questionText.setPromptText("Enter question text");
        questionText.setPrefRowCount(3);

        ComboBox<String> quizComboBox = new ComboBox<>();
        quizComboBox.setPromptText("Select Quiz");
        quizComboBox.getItems().addAll(quizNames.values());
        quizComboBox.setValue(quizNames.get(question.getquiz_id()));

        dialog.getDialogPane().setContent(new VBox(10, 
            new Label("Question Text:"), questionText,
            new Label("Quiz:"), quizComboBox
        ));


        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);


        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String text = questionText.getText().trim();
                String selectedQuizName = quizComboBox.getValue();

                if (text.isEmpty() || selectedQuizName == null) {
                    showAlert("Error", "Please fill in all fields");
                    return;
                }


                if (!text.endsWith("?")) {
                    showAlert("Error", "Question must end with a question mark (?)");
                    return;
                }


                int quizId = -1;
                for (Map.Entry<Integer, String> entry : quizNames.entrySet()) {
                    if (entry.getValue().equals(selectedQuizName)) {
                        quizId = entry.getKey();
                        break;
                    }
                }

                if (quizId == -1) {
                    showAlert("Error", "Invalid quiz selection");
                    return;
                }


                question.setText(text);
                question.setQuiz_id(quizId);
                questionService.modifier(question);
                loadQuestions();
            }
        });
    }

    @FXML
    private void handleCreateQuestion() {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Create New Questions");
        dialog.setHeaderText("Enter question details");


        TextArea questionText = new TextArea();
        questionText.setPromptText("Enter questions (one per line). Each question must end with a question mark (?)");
        questionText.setPrefRowCount(5);

        ComboBox<String> quizComboBox = new ComboBox<>();
        quizComboBox.setPromptText("Select Quiz");
        quizComboBox.getItems().addAll(quizNames.values());


        dialog.getDialogPane().setContent(new VBox(10, 
            new Label("Questions (one per line):"), questionText,
            new Label("Quiz:"), quizComboBox
        ));


        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);


        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String[] questions = questionText.getText().trim().split("\\n");
                String selectedQuizName = quizComboBox.getValue();

                if (questions.length == 0 || selectedQuizName == null) {
                    showAlert("Error", "Please fill in all fields");
                    return;
                }


                int quizId = -1;
                for (Map.Entry<Integer, String> entry : quizNames.entrySet()) {
                    if (entry.getValue().equals(selectedQuizName)) {
                        quizId = entry.getKey();
                        break;
                    }
                }

                if (quizId == -1) {
                    showAlert("Error", "Invalid quiz selection");
                    return;
                }

                // Validate and create questions
                boolean hasInvalidQuestions = false;
                StringBuilder invalidQuestions = new StringBuilder();

                for (String text : questions) {
                    text = text.trim();
                    if (!text.isEmpty()) {
                        if (!text.endsWith("?")) {
                            hasInvalidQuestions = true;
                            invalidQuestions.append("- ").append(text).append("\n");
                        }
                    }
                }

                if (hasInvalidQuestions) {
                    showAlert("Error", "The following questions must end with a question mark (?):\n\n" + invalidQuestions);
                    return;
                }

                // Create and save new questions
                for (String text : questions) {
                    if (!text.trim().isEmpty()) {
                        question newQuestion = new question(0, quizId, text.trim());
                        questionService.ajouter(newQuestion);
                    }
                }
                loadQuestions();
            }
        });
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("quiz.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) questionTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleQuestionNavigation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("question.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) questionTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAnswerNavigation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("answer.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) questionTable.getScene().getWindow();
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
            Parent root = FXMLLoader.load(getClass().getResource("" + fxmlFile));
            Stage stage = (Stage) questionTable.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 
package gui;

import entities.question;
import entities.quiz;
import javafx.scene.input.KeyCombination;
import services.QuestionService;
import services.QuizService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import javafx.geometry.Insets;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Predicate;

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
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> searchTypeComboBox;

    private final QuestionService questionService = new QuestionService();
    private final QuizService quizService = new QuizService();
    private final ObservableList<question> questionList = FXCollections.observableArrayList();
    private final Map<Integer, String> quizNames = new HashMap<>();
    private FilteredList<question> filteredQuestions;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadQuizzes();
        setupTableColumns();
        setupSearchControls();
        setupSearch();
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

    private void setupSearchControls() {
        // Initialize the search type combo box
        searchTypeComboBox.getItems().addAll("Question Text", "Quiz Name");
        searchTypeComboBox.setValue("Question Text");
        
        // Add listener for sorting
        searchTypeComboBox.setOnAction(event -> {
            String sortType = searchTypeComboBox.getValue();
            switch (sortType) {
                case "Question Text":
                    questionList.sort((q1, q2) -> q1.getText().compareToIgnoreCase(q2.getText()));
                    break;
                case "Quiz Name":
                    questionList.sort((q1, q2) -> {
                        String quiz1Name = quizNames.get(q1.getquiz_id());
                        String quiz2Name = quizNames.get(q2.getquiz_id());
                        return quiz1Name.compareToIgnoreCase(quiz2Name);
                    });
                    break;
            }
            questionTable.setItems(questionList);
        });
    }

    private void setupSearch() {
        // Initialize filtered list
        filteredQuestions = new FilteredList<>(questionList, p -> true);
        
        // Add listener to search field
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredQuestions.setPredicate(createPredicate(newValue));
        });

        // Add listener to search type combo box
        searchTypeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredQuestions.setPredicate(createPredicate(searchField.getText()));
        });

        // Bind the filtered list to the table
        questionTable.setItems(filteredQuestions);
    }

    private Predicate<question> createPredicate(String searchText) {
        return question -> {
            if (searchText == null || searchText.isEmpty()) return true;
            
            String lowerCaseFilter = searchText.toLowerCase();
            String searchType = searchTypeComboBox.getValue();

            // Get quiz name for the question
            String quizName = quizNames.get(question.getquiz_id());

            switch (searchType) {
                case "Question Text":
                    return question.getText().toLowerCase().contains(lowerCaseFilter);
                case "Quiz":
                    return quizName != null && quizName.toLowerCase().contains(lowerCaseFilter);
                default: // "All"
                    return question.getText().toLowerCase().contains(lowerCaseFilter)
                        || (quizName != null && quizName.toLowerCase().contains(lowerCaseFilter));
            }
        };
    }

    private void loadQuizzes() {
        for (quiz q : quizService.rechercher()) {
            quizNames.put(q.getId(), q.getName());
        }
    }

    private void loadQuestions() {
        questionList.clear();
        questionList.addAll(questionService.rechercher());
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
        dialog.setHeaderText(null);
        
        // Apply CSS styling
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/quiz.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-dialog");

        // Create form container with proper styling
        VBox formContainer = new VBox(15);
        formContainer.getStyleClass().add("dialog-form");
        formContainer.setPadding(new Insets(20, 20, 10, 10));

        TextArea questionText = new TextArea(question.getText());
        questionText.setPromptText("Enter question text");
        questionText.setPrefRowCount(3);

        ComboBox<String> quizComboBox = new ComboBox<>();
        quizComboBox.setPromptText("Select Quiz");
        quizComboBox.getItems().addAll(quizNames.values());
        quizComboBox.setValue(quizNames.get(question.getquiz_id()));

        formContainer.getChildren().addAll(
            new Label("Question Text:"), questionText,
            new Label("Quiz:"), quizComboBox
        );

        dialog.getDialogPane().setContent(formContainer);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String text = questionText.getText().trim();
                String selectedQuizName = quizComboBox.getValue();

                if (text.isEmpty() || selectedQuizName == null) {
                    showStyledAlert("Error", "Please fill in all fields", Alert.AlertType.ERROR);
                    return;
                }

                if (!text.endsWith("?")) {
                    showStyledAlert("Error", "Question must end with a question mark (?)", Alert.AlertType.ERROR);
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
                    showStyledAlert("Error", "Invalid quiz selection", Alert.AlertType.ERROR);
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
        dialog.setHeaderText(null);
        
        // Apply CSS styling
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/quiz.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-dialog");

        // Create form container with proper styling
        VBox formContainer = new VBox(15);
        formContainer.getStyleClass().add("dialog-form");
        formContainer.setPadding(new Insets(20, 20, 10, 10));

        TextArea questionText = new TextArea();
        questionText.setPromptText("Enter questions (one per line). Each question must end with a question mark (?)");
        questionText.setPrefRowCount(5);

        ComboBox<String> quizComboBox = new ComboBox<>();
        quizComboBox.setPromptText("Select Quiz");
        quizComboBox.getItems().addAll(quizNames.values());

        formContainer.getChildren().addAll(
            new Label("Questions (one per line):"), questionText,
            new Label("Quiz:"), quizComboBox
        );

        dialog.getDialogPane().setContent(formContainer);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String[] questions = questionText.getText().trim().split("\\n");
                String selectedQuizName = quizComboBox.getValue();

                if (questions.length == 0 || selectedQuizName == null) {
                    showStyledAlert("Error", "Please fill in all fields", Alert.AlertType.ERROR);
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
                    showStyledAlert("Error", "Invalid quiz selection", Alert.AlertType.ERROR);
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
                    showStyledAlert("Error", "The following questions must end with a question mark (?):\n\n" + invalidQuestions, Alert.AlertType.ERROR);
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

    private void showStyledAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        // Apply CSS styling to the alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/quiz.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-dialog");
        
        alert.showAndWait();
    }

    @FXML
    private void handleQuizNavigation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("quiz.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) questionTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setFullScreenExitHint(""); // No "Press ESC to exit fullscreen" message
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // Disable ESC key exit
            stage.setFullScreen(true); // Go fullscreen!
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
            stage.setFullScreenExitHint(""); // No "Press ESC to exit fullscreen" message
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // Disable ESC key exit
            stage.setFullScreen(true); // Go fullscreen!
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
            stage.setFullScreenExitHint(""); // No "Press ESC to exit fullscreen" message
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // Disable ESC key exit
            stage.setFullScreen(true); // Go fullscreen!
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
            stage.setFullScreenExitHint(""); // No "Press ESC to exit fullscreen" message
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // Disable ESC key exit
            stage.setFullScreen(true); // Go fullscreen!
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 
package gui ;

import entities.answers;
import entities.question;
import entities.quiz;
import javafx.scene.input.KeyCombination;
import services.AnswerService;
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
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.collections.transformation.FilteredList;
import java.util.function.Predicate;

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
    @FXML
    private ComboBox<String> searchTypeComboBox;
    @FXML
    private TextField searchField;

    private final AnswerService answerService = new AnswerService();
    private final QuestionService questionService = new QuestionService();
    private final QuizService quizService = new QuizService();
    private final ObservableList<answers> answerList = FXCollections.observableArrayList();
    private final Map<Integer, String> questionTexts = new HashMap<>();
    private FilteredList<answers> filteredAnswers;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupSearchControls();
        setupSearch();
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

    private void setupSearchControls() {
        // Initialize the search type combo box
        searchTypeComboBox.getItems().addAll("Answer Text", "Question", "Correct/Incorrect");
        searchTypeComboBox.setValue("Answer Text");

        // Add listener for sorting
        searchTypeComboBox.setOnAction(event -> {
            String sortType = searchTypeComboBox.getValue();
            switch (sortType) {
                case "Answer Text":
                    answerList.sort((a1, a2) -> a1.gettext_ans().compareToIgnoreCase(a2.gettext_ans()));
                    break;
                case "Question":
                    answerList.sort((a1, a2) -> {
                        String question1Text = questionTexts.get(a1.getId_quest());
                        String question2Text = questionTexts.get(a2.getId_quest());
                        return question1Text.compareToIgnoreCase(question2Text);
                    });
                    break;
                case "Correct/Incorrect":
                    answerList.sort((a1, a2) -> Boolean.compare(a2.getis_correct() == 1, a1.getis_correct() == 1));
                    break;
            }
            answerTable.setItems(answerList);
        });
    }

    private void setupSearch() {
        // Initialize filtered list
        filteredAnswers = new FilteredList<>(answerList, p -> true);

        // Add listener to search field
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredAnswers.setPredicate(createPredicate(newValue));
        });

        // Add listener to search type combo box
        searchTypeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredAnswers.setPredicate(createPredicate(searchField.getText()));
        });

        // Bind the filtered list to the table
        answerTable.setItems(filteredAnswers);
    }

    private Predicate<answers> createPredicate(String searchText) {
        return answer -> {
            if (searchText == null || searchText.isEmpty()) return true;

            String lowerCaseFilter = searchText.toLowerCase();
            String searchType = searchTypeComboBox.getValue();

            switch (searchType) {
                case "Answer Text":
                    return answer.gettext_ans().toLowerCase().contains(lowerCaseFilter);
                case "Question":
                    String questionText = questionTexts.get(answer.getId_quest());
                    return questionText != null && questionText.toLowerCase().contains(lowerCaseFilter);
                case "Correct/Incorrect":
                    if (lowerCaseFilter.contains("correct")) {
                        return answer.getis_correct() == 1;
                    } else if (lowerCaseFilter.contains("incorrect")) {
                        return answer.getis_correct() == 0;
                    }
                    return true;
                default:
                    return answer.gettext_ans().toLowerCase().contains(lowerCaseFilter) ||
                            (questionTexts.get(answer.getId_quest()) != null &&
                                    questionTexts.get(answer.getId_quest()).toLowerCase().contains(lowerCaseFilter));
            }
        };
    }

    private void loadQuestions() {

        for (question q : questionService.rechercher()) {
            questionTexts.put(q.getId_ques(), q.getText());
        }
    }

    private void loadAnswers() {
        answerList.clear();
        answerList.addAll(answerService.rechercher());
        // The table will be updated through the filtered list binding
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
        dialog.setHeaderText(null);

        // Apply CSS styling
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/quiz.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-dialog");
        dialogPane.setMinWidth(600);
        dialogPane.setMinHeight(400);

        // Create form container with proper styling
        VBox formContainer = new VBox(15);
        formContainer.getStyleClass().add("dialog-form");
        formContainer.setPadding(new Insets(20, 20, 10, 10));

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

        formContainer.getChildren().addAll(
                new Label("Answer Text:"), answerText,
                new Label("Question:"), questionComboBox,
                correctCheckBox
        );

        dialog.getDialogPane().setContent(formContainer);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String text = answerText.getText().trim();
                String selectedQuestionText = questionComboBox.getValue();

                if (text.isEmpty() || selectedQuestionText == null) {
                    showStyledAlert("Error", "Please fill in all fields", Alert.AlertType.ERROR);
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
                    showStyledAlert("Error", "Invalid question selection", Alert.AlertType.ERROR);
                    return;
                }

                answer.setText_ans(text);
                answer.setId_quest(questionId);
                answer.setIs_correct((byte) (correctCheckBox.isSelected() ? 1 : 0));

                // Update in database
                answerService.modifier(answer);

                // Refresh the table
                loadAnswers();

                showStyledAlert("Success", "Answer updated successfully!", Alert.AlertType.INFORMATION);
            }
        });
    }

    @FXML
    private void handleCreateAnswer() {
        // First dialog to select quiz
        Dialog<ButtonType> quizDialog = new Dialog<>();
        quizDialog.setTitle("Select Quiz");
        quizDialog.setHeaderText(null);

        // Apply CSS styling
        DialogPane quizDialogPane = quizDialog.getDialogPane();
        quizDialogPane.getStylesheets().add(getClass().getResource("/styles/quiz.css").toExternalForm());
        quizDialogPane.getStyleClass().add("custom-dialog");

        // Create form container with proper styling
        VBox quizFormContainer = new VBox(15);
        quizFormContainer.getStyleClass().add("dialog-form");
        quizFormContainer.setPadding(new Insets(20, 20, 10, 10));

        ComboBox<String> quizComboBox = new ComboBox<>();
        quizComboBox.setPromptText("Select Quiz");
        for (quiz q : quizService.rechercher()) {
            quizComboBox.getItems().add(q.getName());
        }

        quizFormContainer.getChildren().addAll(
                new Label("Quiz:"), quizComboBox
        );

        quizDialog.getDialogPane().setContent(quizFormContainer);
        quizDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        quizDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String selectedQuizName = quizComboBox.getValue();
                if (selectedQuizName == null) {
                    showStyledAlert("Error", "Please select a quiz", Alert.AlertType.ERROR);
                    return;
                }

                final quiz selectedQuiz = quizService.rechercher().stream()
                        .filter(q -> q.getName().equals(selectedQuizName))
                        .findFirst()
                        .orElse(null);

                if (selectedQuiz == null) {
                    showStyledAlert("Error", "Invalid quiz selection", Alert.AlertType.ERROR);
                    return;
                }

                final int selectedQuizId = selectedQuiz.getId();
                final boolean isMultipleChoice = "Multiple Choice".equals(selectedQuiz.getType());

                Dialog<ButtonType> answerDialog = new Dialog<>();
                answerDialog.setTitle("Create New Answers");
                answerDialog.setHeaderText(null);

                // Apply CSS styling
                DialogPane answerDialogPane = answerDialog.getDialogPane();
                answerDialogPane.getStylesheets().add(getClass().getResource("/styles/quiz.css").toExternalForm());
                answerDialogPane.getStyleClass().add("custom-dialog");
                answerDialogPane.setMinWidth(600);
                answerDialogPane.setMinHeight(400);

                // Create form container with proper styling
                VBox answerFormContainer = new VBox(15);
                answerFormContainer.getStyleClass().add("dialog-form");
                answerFormContainer.setPadding(new Insets(20, 20, 10, 10));

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

                answerFormContainer.getChildren().addAll(
                        new Label("Quiz Type: " + selectedQuiz.getType()),
                        new Label("Question:"), questionComboBox,
                        new Label("Answers:"), scrollPane,
                        buttonContainer
                );

                answerDialog.getDialogPane().setContent(answerFormContainer);
                answerDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                answerDialog.showAndWait().ifPresent(answerResponse -> {
                    if (answerResponse == ButtonType.OK) {
                        String selectedQuestionText = questionComboBox.getValue();
                        if (selectedQuestionText == null) {
                            showStyledAlert("Error", "Please select a question", Alert.AlertType.ERROR);
                            return;
                        }

                        final int questionId = questionService.rechercher().stream()
                                .filter(q -> q.getText().equals(selectedQuestionText) && q.getquiz_id() == selectedQuizId)
                                .findFirst()
                                .map(question::getId_ques)
                                .orElse(-1);

                        if (questionId == -1) {
                            showStyledAlert("Error", "Invalid question selection", Alert.AlertType.ERROR);
                            return;
                        }

                        long correctAnswersCount = answerFields.getChildren().stream()
                                .filter(node -> node instanceof HBox)
                                .map(node -> (HBox) node)
                                .filter(hbox -> ((CheckBox) hbox.getChildren().get(1)).isSelected())
                                .count();

                        if (correctAnswersCount == 0) {
                            showStyledAlert("Error", "At least one answer must be marked as correct", Alert.AlertType.ERROR);
                            return;
                        }

                        if (!isMultipleChoice && correctAnswersCount > 1) {
                            showStyledAlert("Error", "Single choice quiz can only have one correct answer", Alert.AlertType.ERROR);
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
            Stage stage = (Stage) answerTable.getScene().getWindow();
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
            Stage stage = (Stage) answerTable.getScene().getWindow();
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
            Stage stage = (Stage) answerTable.getScene().getWindow();
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
            Stage stage = (Stage) answerTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setFullScreenExitHint(""); // No "Press ESC to exit fullscreen" message
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // Disable ESC key exit
            stage.setFullScreen(true); // Go fullscreen!
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 
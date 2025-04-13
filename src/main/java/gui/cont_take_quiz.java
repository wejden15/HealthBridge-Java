package gui;

import entities.answers;
import entities.question;
import entities.quiz;
import services.QuestionService;
import services.QuizService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class cont_take_quiz {
    @FXML
    private TableView<quiz> quizTable;
    @FXML
    private TableColumn<quiz, String> nameColumn;
    @FXML
    private TableColumn<quiz, String> typeColumn;
    @FXML
    private TableColumn<quiz, Void> actionColumn;

    private final QuizService quizService = new QuizService();
    private final QuestionService questionService = new QuestionService();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadQuizzes();
    }

    private void setupTableColumns() {
        // Debug log to verify column setup
        System.out.println("Setting up table columns...");
        
        // Set up name column
        nameColumn.setCellValueFactory(cellData -> {
            String name = cellData.getValue().getName();
            System.out.println("Name value: " + name); // Debug log
            return new javafx.beans.property.SimpleStringProperty(name);
        });
        
        // Set up type column
        typeColumn.setCellValueFactory(cellData -> {
            String type = cellData.getValue().getType();
            System.out.println("Type value: " + type); // Debug log
            return new javafx.beans.property.SimpleStringProperty(type);
        });

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button takeButton = new Button("Take");

            {
                takeButton.setOnAction(event -> {
                    quiz quiz = getTableView().getItems().get(getIndex());
                    handleTakeQuiz(quiz);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(takeButton);
                }
            }
        });
    }

    private void loadQuizzes() {
        List<quiz> quizList = quizService.rechercher();
        System.out.println("Quizzes from database: " + quizList); // Debug log
        
        ObservableList<quiz> quizzes = FXCollections.observableArrayList(quizList);
        System.out.println("Observable list created with size: " + quizzes.size()); // Debug log
        
        quizTable.setItems(quizzes);
        System.out.println("Table items set. Table items size: " + quizTable.getItems().size()); // Debug log
    }

    private void handleTakeQuiz(quiz quiz) {
        try {

            List<question> questions = questionService.rechercher().stream()
                    .filter(q -> q.getquiz_id() == quiz.getId())
                    .toList();
            

            Map<Integer, answers> userAnswers = new HashMap<>();
            

            ToggleGroup answerToggleGroup = new ToggleGroup();
            

            FXMLLoader loader = new FXMLLoader(getClass().getResource("take_quiz_dialog.fxml"));
            Parent root = loader.load();
            cont_take_quiz_dialog controller = loader.getController();
            

            controller.initialize(quiz, questions, userAnswers, 0, answerToggleGroup);

            Stage stage = new Stage();
            stage.setTitle("Taking Quiz: " + quiz.getName());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleQuizNavigation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("quiz.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) quizTable.getScene().getWindow();
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
            Stage stage = (Stage) quizTable.getScene().getWindow();
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
            Stage stage = (Stage) quizTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
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
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navigateTo(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
            Stage stage = (Stage) quizTable.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 
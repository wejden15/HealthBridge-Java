package com.esprit.controllers;

import com.esprit.models.answers;
import com.esprit.models.question;
import com.esprit.models.quiz;
import com.esprit.services.QuestionService;
import com.esprit.services.QuizService;
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
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

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
        ObservableList<quiz> quizzes = FXCollections.observableArrayList(quizService.rechercher());
        quizTable.setItems(quizzes);
    }

    private void handleTakeQuiz(quiz quiz) {
        try {

            List<question> questions = questionService.rechercher().stream()
                    .filter(q -> q.getquiz_id() == quiz.getId())
                    .toList();
            

            Map<Integer, answers> userAnswers = new HashMap<>();
            

            ToggleGroup answerToggleGroup = new ToggleGroup();
            

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/take_quiz_dialog.fxml"));
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
        navigateTo("quiz.fxml");
    }

    @FXML
    private void handleQuestionNavigation() {
        navigateTo("question.fxml");
    }

    @FXML
    private void handleAnswerNavigation() {
        navigateTo("answer.fxml");
    }

    @FXML
    private void handleTakeQuizNavigation() {
        navigateTo("take_quiz.fxml");
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
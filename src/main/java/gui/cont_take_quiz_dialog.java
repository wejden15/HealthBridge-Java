package gui;

import entities.answers;
import entities.question;
import entities.quiz;
import services.AnswerService;
import services.QuestionService;
import services.QuizService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.application.Platform;
import services.EmailService;
import services.UserSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class cont_take_quiz_dialog {
    @FXML
    private Label quizNameLabel;
    @FXML
    private Label quizTypeLabel;
    @FXML
    private Label questionNumberLabel;
    @FXML
    private Label questionTextLabel;
    @FXML
    private VBox answersContainer;
    @FXML
    private Button prevButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button submitButton;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressLabel;
    @FXML
    private Label timerLabel;

    private final AnswerService answerService = new AnswerService();
    private quiz currentQuiz;
    private List<question> questions;
    private Map<Integer, answers> userAnswers;
    private int currentQuestionIndex = 0;
    private ToggleGroup answerToggleGroup;
    private Timeline timer;
    private int timeLeft = 30;

    public void initialize(quiz quiz, List<question> questions, Map<Integer, answers> userAnswers,
                         int currentQuestionIndex, ToggleGroup answerToggleGroup) {
        this.currentQuiz = quiz;
        this.questions = questions;
        this.userAnswers = userAnswers;
        this.currentQuestionIndex = currentQuestionIndex;
        this.answerToggleGroup = answerToggleGroup;

        quizNameLabel.setText(quiz.getName());
        quizTypeLabel.setText("Type: " + quiz.getType());

        // Initialize timer
        initializeTimer();
        
        updateQuestionDisplay();
    }

    private void initializeTimer() {
        timerLabel.setText("Time left: " + timeLeft + " seconds");
        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeLeft--;
            timerLabel.setText("Time left: " + timeLeft + " seconds");
            
            if (timeLeft <= 0) {
                timer.stop();
                Platform.runLater(() -> handleSubmit());
            }
        }));
        timer.setCycleCount(30);
        timer.play();
    }

    private void updateQuestionDisplay() {
        if (currentQuestionIndex >= 0 && currentQuestionIndex < questions.size()) {
            question currentQuestion = questions.get(currentQuestionIndex);
            questionNumberLabel.setText("Question " + (currentQuestionIndex + 1) + " of " + questions.size());
            questionTextLabel.setText(currentQuestion.getText());

            answersContainer.getChildren().clear();
            answerToggleGroup.getToggles().clear();

            List<answers> answersList = answerService.rechercher().stream()
                    .filter(a -> a.getId_quest() == currentQuestion.getId_ques())
                    .toList();
            
            List<answers> mutableAnswers = new ArrayList<>(answersList);
            
            Collections.shuffle(mutableAnswers);

            for (answers answer : mutableAnswers) {
                RadioButton radioButton = new RadioButton(answer.gettext_ans());
                radioButton.setToggleGroup(answerToggleGroup);
                radioButton.setUserData(answer);
                answersContainer.getChildren().add(radioButton);

                if (userAnswers.containsKey(currentQuestion.getId_ques()) &&
                        userAnswers.get(currentQuestion.getId_ques()).getId_ans() == answer.getId_ans()) {
                    radioButton.setSelected(true);
                }
            }

            prevButton.setDisable(currentQuestionIndex == 0);
            nextButton.setDisable(currentQuestionIndex == questions.size() - 1);
            submitButton.setVisible(currentQuestionIndex == questions.size() - 1);

            double progress = (double) (currentQuestionIndex + 1) / questions.size();
            progressBar.setProgress(progress);
            progressLabel.setText((currentQuestionIndex + 1) + " / " + questions.size());
        }
    }

    @FXML
    private void handlePreviousQuestion() {
        if (currentQuestionIndex > 0) {
            saveCurrentAnswer();
            currentQuestionIndex--;
            updateQuestionDisplay();
        }
    }

    @FXML
    private void handleNextQuestion() {
        if (currentQuestionIndex < questions.size() - 1) {
            saveCurrentAnswer();
            currentQuestionIndex++;
            updateQuestionDisplay();
        }
    }

    private void saveCurrentAnswer() {
        if (answerToggleGroup.getSelectedToggle() != null) {
            RadioButton selectedButton = (RadioButton) answerToggleGroup.getSelectedToggle();
            answers selectedAnswer = (answers) selectedButton.getUserData();
            userAnswers.put(questions.get(currentQuestionIndex).getId_ques(), selectedAnswer);
        }
    }

    @FXML
    private void handleSubmit() {
        saveCurrentAnswer();
        showResults();
    }

    private void showResults() {
        // Stop the timer when showing results
        if (timer != null) {
            timer.stop();
        }
        
        final int correctAnswers = calculateCorrectAnswers();
        final int totalQuestions = questions.size();
        final double score = (double) correctAnswers / totalQuestions * 100;
        final String quizName = currentQuiz.getName();

        Platform.runLater(() -> {
            try {
                // Get current user's email
                String userEmail = UserSession.getCurrentUser().getUsername();

                // Send email with results
                EmailService emailService = new EmailService();
                emailService.sendQuizResults(
                    userEmail,           // recipient email
                    quizName,      // quiz name
                    score,              // score as percentage
                    correctAnswers,     // number of correct answers
                    totalQuestions      // total number of questions
                );

                // Show success alert
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Quiz Results");
                alert.setHeaderText("Quiz: " + quizName);
                alert.setContentText(String.format("""
                    You scored %.1f%%
                    Correct Answers: %d/%d
                    
                    Results have been sent to your email: %s""",
                    score, correctAnswers, totalQuestions, userEmail));
                alert.showAndWait();

            } catch (Exception e) {
                System.err.println("Error sending email: " + e.getMessage());
                // Show error to user but continue with results display
                Alert errorAlert = new Alert(Alert.AlertType.WARNING);
                errorAlert.setTitle("Email Notification");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Could not send results via email, but you can still view them here.");
                errorAlert.showAndWait();
            } finally {
                // Close the quiz dialog
                Stage stage = (Stage) quizNameLabel.getScene().getWindow();
                stage.close();
            }
        });
    }

    private int calculateCorrectAnswers() {
        int correctAnswers = 0;
        for (question q : questions) {
            answers userAnswer = userAnswers.get(q.getId_ques());
            if (userAnswer != null && userAnswer.getis_correct() == 1) {
                correctAnswers++;
            }
        }
        return correctAnswers;
    }
} 
package gui;

import entities.quiz;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.VBox;
import services.QuizService;
import java.net.URL;
import java.time.LocalDate;
import java.sql.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.geometry.Insets;
import javafx.scene.layout.Priority;
import java.util.Comparator;
import java.util.stream.Collectors;

public class CalendarController implements Initializable {
    @FXML
    private DatePicker calendar;
    
    @FXML
    private VBox eventList;
    
    @FXML
    private VBox upcomingQuizzes;

    private QuizService quizService;

    public CalendarController() {
        quizService = new QuizService();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupCalendar();
        loadUpcomingQuizzes();
    }

    private void setupCalendar() {
        // Get all quizzes
        List<quiz> quizzes = quizService.getAll();

        // Custom day cell factory
        calendar.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                if (date != null && !empty) {
                    // Check if this date has any quizzes
                    List<quiz> quizzesForDate = quizzes.stream()
                        .filter(quiz -> quiz.getDate().toLocalDate().equals(date))
                        .collect(Collectors.toList());

                    if (!quizzesForDate.isEmpty()) {
                        // Mark dates with quizzes
                        setStyle("-fx-background-color: #ff8c00;"); // Orange background
                        setTextFill(Color.WHITE);
                        
                        // Create a VBox to hold the date and quiz names
                        VBox content = new VBox(2);
                        content.setPadding(new Insets(2));
                        
                        // Add the date
                        Text dateText = new Text(String.valueOf(date.getDayOfMonth()));
                        dateText.setFill(Color.WHITE);
                        content.getChildren().add(dateText);
                        
                        // Add quiz names
                        quizzesForDate.forEach(quiz -> {
                            Text quizText = new Text(quiz.getName());
                            quizText.setFill(Color.WHITE);
                            quizText.setStyle("-fx-font-size: 8px;");
                            content.getChildren().add(quizText);
                        });
                        
                        setGraphic(content);
                    }
                }
            }
        });

        // Add date selection listener
        calendar.setOnAction(e -> {
            LocalDate selectedDate = calendar.getValue();
            showQuizzesForDate(selectedDate);
        });

        // Set initial date to today
        calendar.setValue(LocalDate.now());
    }

    private void loadUpcomingQuizzes() {
        // Clear previous upcoming quizzes
        upcomingQuizzes.getChildren().clear();

        // Add header
        Label headerLabel = new Label("Upcoming Quizzes");
        headerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 0 0 10 0;");
        upcomingQuizzes.getChildren().add(headerLabel);

        // Get current date
        LocalDate currentDate = LocalDate.now();

        // Get all quizzes, filter upcoming ones, and sort by date
        List<quiz> upcomingQuizzesList = quizService.getAll().stream()
            .filter(quiz -> !quiz.getDate().toLocalDate().isBefore(currentDate))
            .sorted(Comparator.comparing(quiz -> quiz.getDate().toLocalDate()))
            .collect(Collectors.toList());

        if (!upcomingQuizzesList.isEmpty()) {
            for (quiz quiz : upcomingQuizzesList) {
                VBox quizBox = createQuizBox(quiz);
                upcomingQuizzes.getChildren().add(quizBox);
            }
        } else {
            Label noQuizLabel = new Label("No upcoming quizzes");
            noQuizLabel.setStyle("-fx-font-style: italic;");
            upcomingQuizzes.getChildren().add(noQuizLabel);
        }
    }

    private void showQuizzesForDate(LocalDate date) {
        // Clear previous events
        eventList.getChildren().clear();

        // Add header
        Label dateLabel = new Label(date.toString());
        dateLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        eventList.getChildren().add(dateLabel);

        // Get quizzes for selected date and sort by name
        List<quiz> quizzesForDate = quizService.getAll().stream()
            .filter(quiz -> quiz.getDate().toLocalDate().equals(date))
            .sorted(Comparator.comparing(quiz -> quiz.getName().toLowerCase()))
            .collect(Collectors.toList());

        if (!quizzesForDate.isEmpty()) {
            for (quiz quiz : quizzesForDate) {
                VBox quizBox = createQuizBox(quiz);
                eventList.getChildren().add(quizBox);
            }
        } else {
            Label noQuizLabel = new Label("No quizzes scheduled for this date");
            noQuizLabel.setStyle("-fx-font-style: italic;");
            eventList.getChildren().add(noQuizLabel);
        }
    }

    private VBox createQuizBox(quiz quiz) {
        VBox quizBox = new VBox(5);
        quizBox.setStyle("-fx-padding: 10; -fx-background-color: #f0f0f0; -fx-background-radius: 5;");

        Label nameLabel = new Label("Quiz: " + quiz.getName());
        nameLabel.setStyle("-fx-font-weight: bold;");

        Label typeLabel = new Label("Type: " + quiz.getType());
        
        Label dateLabel = new Label("Date: " + quiz.getDate().toLocalDate().toString());
        dateLabel.setStyle("-fx-text-fill: #666666;");

        quizBox.getChildren().addAll(nameLabel, typeLabel, dateLabel);
        return quizBox;
    }
} 
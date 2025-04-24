package gui;

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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final AnswerService answerService = new AnswerService();

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
            Stage stage = (Stage) quizTable.getScene().getWindow();
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
            Stage stage = (Stage) quizTable.getScene().getWindow();
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("take_quiz.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) quizTable.getScene().getWindow();
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
    public void handleExportPdf() {
        quiz selectedQuiz = quizTable.getSelectionModel().getSelectedItem();
        if (selectedQuiz == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Quiz Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a quiz to export.");
            alert.showAndWait();
            return;
        }

        List<question> quizQuestions = questionService.rechercher().stream()
                .filter(q -> q.getquiz_id() == selectedQuiz.getId())
                .toList();

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Quiz PDF");
            fileChooser.setInitialFileName("quiz_" + selectedQuiz.getName() + "_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            
            File file = fileChooser.showSaveDialog(quizTable.getScene().getWindow());
            if (file != null) {
                try (PDDocument document = new PDDocument()) {
                    PDPage currentPage = new PDPage();
                    document.addPage(currentPage);
                    PDPageContentStream contentStream = new PDPageContentStream(document, currentPage);
                    
                    // Constants for layout
                    float margin = 50;
                    float yPosition = currentPage.getMediaBox().getHeight() - margin;
                    float lineHeight = 20;
                    float pageHeight = currentPage.getMediaBox().getHeight() - 2 * margin;
                    
                    // Add quiz title
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText(selectedQuiz.getName());
                    contentStream.endText();
                    yPosition -= lineHeight * 2;

                    // Add quiz type
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 14);
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText("Type: " + selectedQuiz.getType());
                    contentStream.endText();
                    yPosition -= lineHeight * 2;

                    // Add questions and answers
                    contentStream.setFont(PDType1Font.HELVETICA, 12);

                    for (int i = 0; i < quizQuestions.size(); i++) {
                        question q = quizQuestions.get(i);
                        
                        // Check if we need a new page
                        if (yPosition < margin + (lineHeight * 5)) { // Reserve space for at least 5 lines
                            contentStream.close();
                            currentPage = new PDPage();
                            document.addPage(currentPage);
                            contentStream = new PDPageContentStream(document, currentPage);
                            yPosition = currentPage.getMediaBox().getHeight() - margin;
                            contentStream.setFont(PDType1Font.HELVETICA, 12);
                        }
                        
                        // Add question
                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                        contentStream.newLineAtOffset(margin, yPosition);
                        contentStream.showText("Question " + (i + 1) + ": " + q.getText());
                        contentStream.endText();
                        yPosition -= lineHeight;

                        // Add answers
                        List<answers> answersList = answerService.rechercher().stream()
                                .filter(a -> a.getId_quest() == q.getId_ques())
                                .toList();

                        for (answers answer : answersList) {
                            // Check if we need a new page for answers
                            if (yPosition < margin + lineHeight) {
                                contentStream.close();
                                currentPage = new PDPage();
                                document.addPage(currentPage);
                                contentStream = new PDPageContentStream(document, currentPage);
                                yPosition = currentPage.getMediaBox().getHeight() - margin;
                                contentStream.setFont(PDType1Font.HELVETICA, 12);
                            }
                            
                            // Draw checkbox
                            contentStream.setLineWidth(1);
                            contentStream.addRect(margin, yPosition - 12, 12, 12);
                            contentStream.stroke();
                            
                            // Add answer text
                            contentStream.beginText();
                            contentStream.setFont(PDType1Font.HELVETICA, 12);
                            contentStream.newLineAtOffset(margin + 20, yPosition);
                            contentStream.showText(answer.gettext_ans());
                            contentStream.endText();
                            yPosition -= lineHeight;
                        }
                        yPosition -= lineHeight; // Extra space between questions
                    }
                    
                    contentStream.close();
                    document.save(file);

                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Export Successful");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Quiz has been exported to PDF successfully!");
                    successAlert.showAndWait();
                }
            }
        } catch (IOException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Export Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Failed to export quiz to PDF: " + e.getMessage());
            errorAlert.showAndWait();
        }
    }

    private void navigateTo(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
            Stage stage = (Stage) quizTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setFullScreenExitHint(""); // No "Press ESC to exit fullscreen" message
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // Disable ESC key exit
            stage.setFullScreen(true); // Go fullscreen!
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 
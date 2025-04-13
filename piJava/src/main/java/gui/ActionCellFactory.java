package gui ;

import entities.quiz;
import services.QuizService;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Alert;
import javafx.util.Callback;
import javafx.scene.layout.HBox;

public class ActionCellFactory implements Callback<TableColumn<quiz, String>, TableCell<quiz, String>> {

    private QuizService quizService = new QuizService();

    @Override
    public TableCell<quiz, String> call(TableColumn<quiz, String> param) {
        return new TableCell<>() {
            final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    quiz selectedQuiz = getTableView().getItems().get(getIndex());
                    quizService.supprimer(selectedQuiz);
                    getTableView().getItems().remove(selectedQuiz);
                    showAlert("Success", "Quiz deleted successfully!");
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        };
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 
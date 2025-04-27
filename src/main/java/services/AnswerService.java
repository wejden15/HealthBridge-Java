package services;

import entities.answers;
import utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnswerService implements IService<answers> {

    Connection connection = MyDB.getInstance().getConx();

    @Override
    public void ajouter(answers answer) {
        String req = "INSERT INTO answer (question_id, text, is_correct) VALUES (?,?,?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, answer.getId_quest());
            pst.setString(2, answer.gettext_ans());
            pst.setByte(3, answer.getis_correct());
            pst.executeUpdate();
            System.out.println("Réponse ajoutée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(answers answer) {
        String req = "UPDATE answer SET question_id=?, text=?, is_correct=? WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, answer.getId_quest());
            pst.setString(2, answer.gettext_ans());
            pst.setByte(3, answer.getis_correct());
            pst.setInt(4, answer.getId_ans());
            
            System.out.println("Updating answer with ID: " + answer.getId_ans());
            System.out.println("Question ID: " + answer.getId_quest());
            System.out.println("Text: " + answer.gettext_ans());
            System.out.println("Is Correct: " + answer.getis_correct());
            
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Réponse modifiée avec succès");
            } else {
                System.out.println("Aucune réponse n'a été modifiée - Vérifiez l'ID de la réponse");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification: " + e.getMessage());
        }
    }

    @Override
    public void supprimer(answers answer) {
        String req = "DELETE FROM answer WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, answer.getId_ans());
            pst.executeUpdate();
            System.out.println("Réponse supprimée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<answers> rechercher() {
        List<answers> answers = new ArrayList<>();
        String req = "SELECT * FROM answer";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int questionId = rs.getInt("question_id");
                String text = rs.getString("text");
                byte isCorrect = rs.getByte("is_correct");
                
                System.out.println("Found answer - ID: " + id + ", Question ID: " + questionId + 
                    ", Text: " + text + ", Is Correct: " + isCorrect);
                
                answers.add(new answers(id, questionId, text, isCorrect));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche: " + e.getMessage());
        }
        return answers;
    }
} 
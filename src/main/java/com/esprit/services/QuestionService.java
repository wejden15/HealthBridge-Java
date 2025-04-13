package com.esprit.services;

import com.esprit.models.question;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionService implements IService<question> {

    Connection connection = DataSource.getInstance().getConnection();

    @Override
    public void ajouter(question question) {
        String req = "INSERT INTO question (quiz_id, text) VALUES (?,?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, question.getquiz_id());
            pst.setString(2, question.getText());
            pst.executeUpdate();
            System.out.println("Question ajoutée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(question question) {
        String req = "UPDATE question SET quiz_id=?, text=? WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, question.getquiz_id());
            pst.setString(2, question.getText());
            pst.setInt(3, question.getId_ques());
            pst.executeUpdate();
            System.out.println("Question modifiée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void supprimer(question question) {
        String req = "DELETE FROM question WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, question.getId_ques());
            pst.executeUpdate();
            System.out.println("Question supprimée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<question> rechercher() {
        List<question> questions = new ArrayList<>();
        String req = "SELECT * FROM question";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                questions.add(new question(rs.getInt("id"), rs.getInt("quiz_id"), rs.getString("text")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return questions;
    }
} 
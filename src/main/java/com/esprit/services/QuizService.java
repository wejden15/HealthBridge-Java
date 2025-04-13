package com.esprit.services;

import com.esprit.models.quiz;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizService implements IService<quiz> {

    Connection connection = DataSource.getInstance().getConnection();

    public void add(quiz quiz) {
        String req = "INSERT INTO quiz (name, type) VALUES (?,?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, quiz.getName());
            pst.setString(2, quiz.getType());
            pst.executeUpdate();
            System.out.println("Quiz added");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void update(quiz quiz) {
        String req = "UPDATE quiz SET name=?, type=? WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, quiz.getName());
            pst.setString(2, quiz.getType());
            pst.setInt(3, quiz.getId());
            pst.executeUpdate();
            System.out.println("Quiz updated");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void delete(int id) {
        String req = "DELETE FROM quiz WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Quiz deleted");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<quiz> getAll() {
        List<quiz> quizzes = new ArrayList<>();
        String req = "SELECT * FROM quiz";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                quizzes.add(new quiz(rs.getInt("id"), rs.getString("name"), rs.getString("type")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return quizzes;
    }

    // Keep the old methods for backward compatibility
    @Override
    public void ajouter(quiz quiz) {
        add(quiz);
    }

    @Override
    public void modifier(quiz quiz) {
        update(quiz);
    }

    @Override
    public void supprimer(quiz quiz) {
        delete(quiz.getId());
    }

    @Override
    public List<quiz> rechercher() {
        return getAll();
    }
} 
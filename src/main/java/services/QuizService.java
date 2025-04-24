package services;

import entities.quiz;
import utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizService implements services.IService<quiz> {

    Connection connection = MyDB.getInstance().getConx();

    public void add(quiz quiz) {
        String req = "INSERT INTO quiz (name, type, date) VALUES (?,?,?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, quiz.getName());
            pst.setString(2, quiz.getType());
            
            // If no date is set, use current date
            Date quizDate = quiz.getDate();
            if (quizDate == null) {
                quizDate = new Date(System.currentTimeMillis());
            }
            pst.setDate(3, quizDate);
            
            pst.executeUpdate();
            System.out.println("Quiz added with date: " + quizDate);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void update(quiz quiz) {
        String req = "UPDATE quiz SET name=?, type=?, date=? WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, quiz.getName());
            pst.setString(2, quiz.getType());
            pst.setDate(3, quiz.getDate());
            pst.setInt(4, quiz.getId());
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
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String type = rs.getString("type");
                Date date = rs.getDate("date");
                
                quiz q = new quiz(id, name, type, date);
                quizzes.add(q);
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
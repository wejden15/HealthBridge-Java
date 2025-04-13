package com.esprit.models;

public class question {
    private int id_ques;
    private int quiz_id;
    private String text;

    public question(int id_ques, int quiz_id, String text) {
        this.id_ques = id_ques;
        this.quiz_id = quiz_id;
        this.text = text;
    }

    public question(int quiz_id, String text) {
        this.quiz_id = quiz_id;
        this.text = text;
    }

    public int getId_ques() {
        return id_ques;
    }

    public void setId_ques(int id_ques) {
        this.id_ques = id_ques;
    }

    public int getquiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(int quiz_id) {
        this.quiz_id = quiz_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Personne{" +
                "id=" + id_ques +
                ", quizid='" + quiz_id + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}

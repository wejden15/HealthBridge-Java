package com.esprit.models;

public class answers {
    private int id_ans;
    private int id_quest;
    private String text_ans;
    private byte is_correct;

    public answers(int id_ans, int id_quest, String text_ans, byte is_correct) {
        this.id_ans = id_ans;
        this.id_quest = id_quest;
        this.text_ans = text_ans;
        this.is_correct = is_correct;
    }

    public answers(int id_quest, String text_ans, byte is_correct) {
        this.id_quest =id_quest ;
        this.text_ans = text_ans;
        this.is_correct = is_correct;
    }

    public int getId_ans() {
        return id_ans;
    }

    public void setId_ans(int id_ans) {
        this.id_ans = id_ans;
    }

    public int getId_quest() {
        return id_quest;
    }

    public void setId_quest(int id_quest) {
        this.id_quest = id_quest;
    }

    public String gettext_ans() {
        return text_ans;
    }

    public void setText_ans(String text_ans) {
        this.text_ans = text_ans;
    }
    public byte getis_correct() {
        return is_correct;
    }

    public void setIs_correct(byte is_correct) {
        this.is_correct = is_correct;
    }
    @Override
    public String toString() {
        return "Personne{" +
                "id_ans=" + id_ans +
                ", id_quest='" + id_quest + '\'' +
                ", text_ans='" + text_ans + '\'' +
                ", is_correct='" + is_correct + '\'' +
                '}';
    }
}

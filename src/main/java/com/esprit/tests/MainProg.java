package com.esprit.tests;

import com.esprit.models.answers;
import com.esprit.models.question;
import com.esprit.models.quiz;
import com.esprit.services.AnswerService;
import com.esprit.services.QuestionService;
import com.esprit.services.QuizService;

public class MainProg {
    public static void main(String[] args) {
        // quizajout
        QuizService quizService = new QuizService();
        quiz quiz = new quiz("Java Quiz", "single choice");
        quizService.ajouter(quiz);
        System.out.println("Quizzes after adding: " + quizService.rechercher());

        /*/quiz update
        quiz.setId(12);
        quiz.setname("Advanced Java Quiz");
        quiz.setType("Advanced");
        quizService.modifier(quiz);
        System.out.println("Quizzes after updating: " + quizService.rechercher());

        // quest ajout
        QuestionService questionService = new QuestionService();
        question question = new question(12, "What is Java?");
        questionService.ajouter(question);
        System.out.println("Questions after adding: " + questionService.rechercher());

        // modif quest
        question.setId_ques(13);
        question.setText("What are the main features of Java?");
        questionService.modifier(question);
        System.out.println("Questions after updating: " + questionService.rechercher());

        //answer ajout
        AnswerService answerService = new AnswerService();
        answers answer = new answers(13, "Java is a programming language", (byte)1);
        answerService.ajouter(answer);
        System.out.println("Answers after adding: " + answerService.rechercher());

        //modif answer
        answer.setId_ans(1);
        answer.setText_ans("Java is an object-oriented programming language");
        answer.setIs_correct((byte)1);
        answerService.modifier(answer);
        System.out.println("Answers after updating: " + answerService.rechercher());


        // supp answer
        answerService.supprimer(answer);
        System.out.println("Answers after deleting: " + answerService.rechercher());

        // supp question
        questionService.supprimer(question);
        System.out.println("Questions after deleting: " + questionService.rechercher());

        // supp quiz
        quizService.supprimer(quiz);
        System.out.println("Quizzes after deleting: " + quizService.rechercher());*/
    }
}

package gui;

import entities.answers;
import entities.question;
import entities.quiz;
import services.AnswerService;
import services.QuestionService;
import services.QuizService;
import java.sql.Date;

public class MainProg {
    public static void main(String[] args) {
        // Create a quiz with current date
        Date currentDate = new Date(System.currentTimeMillis());
        quiz q = new quiz("Quiz Name", "Quiz Type", currentDate);
        
        // Or if you need a specific date
        // quiz q = new quiz("Quiz Name", "Quiz Type", Date.valueOf("2024-04-27"));
        
        // quizajout
        QuizService quizService = new QuizService();
        quizService.ajouter(q);
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
        quizService.supprimer(q);
        System.out.println("Quizzes after deleting: " + quizService.rechercher());*/
    }
}

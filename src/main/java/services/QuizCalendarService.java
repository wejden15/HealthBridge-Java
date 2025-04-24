package services;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import entities.quiz;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

public class QuizCalendarService {
    private final GoogleCalendarService googleCalendarService;
    private final QuizService quizService;

    public QuizCalendarService() {
        this.googleCalendarService = new GoogleCalendarService();
        this.quizService = new QuizService();
    }

    public void syncQuizzesToCalendar() {
        try {
            List<quiz> quizzes = quizService.rechercher();
            for (quiz quiz : quizzes) {
                // Create event for each quiz
                Event event = new Event()
                    .setSummary("Quiz: " + quiz.getName())
                    .setDescription("Type: " + quiz.getType());

                // Get current date/time since quiz doesn't have a date field
                DateTime startDateTime = new DateTime(System.currentTimeMillis());
                
                // Set event start time
                EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone("Africa/Tunis");
                event.setStart(start);

                // Set event end time (1 hour after start)
                DateTime endDateTime = new DateTime(System.currentTimeMillis() + 3600000);
                EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone("Africa/Tunis");
                event.setEnd(end);

                // Add event to calendar
                googleCalendarService.createEvent(event);
            }
        } catch (IOException e) {
            System.err.println("Error syncing quizzes to calendar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void addQuizToCalendar(quiz quiz) {
        try {
            Event event = new Event()
                .setSummary("Quiz: " + quiz.getName())
                .setDescription("Type: " + quiz.getType());

            // Get current date/time since quiz doesn't have a date field
            DateTime startDateTime = new DateTime(System.currentTimeMillis());
            EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Africa/Tunis");
            event.setStart(start);

            DateTime endDateTime = new DateTime(System.currentTimeMillis() + 3600000);
            EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Africa/Tunis");
            event.setEnd(end);

            googleCalendarService.createEvent(event);
        } catch (IOException e) {
            System.err.println("Error adding quiz to calendar: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 
package models;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import entities.quiz;
import services.QuizService;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;

public class GoogleCalendar {
    private static final String APPLICATION_NAME = "Quiz Calendar";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "/client_secret_341752110583-ec1tofpr1r32h4bcsbji4i3t5edovpg2.apps.googleusercontent.com.json";
    private static final String TOKENS_DIRECTORY_PATH = System.getProperty("user.home") + "/quiz_calendar_tokens";
    private static final String CALENDAR_ID = "primary";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String TIME_ZONE = "UTC";

    // Get the Calendar service
    private static Calendar getCalendarService() throws IOException, GeneralSecurityException {
        // Load client secrets
        InputStream in = GoogleCalendar.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Please place your client_secret.json file in src/main/resources");
        }

        GoogleClientSecrets clientSecrets;
        try {
            clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        } catch (IOException e) {
            throw new IOException("Error loading client secrets file. Make sure it's properly formatted: " + e.getMessage());
        }

        // Set up HTTP transport
        NetHttpTransport httpTransport;
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException e) {
            throw new GeneralSecurityException("Failed to initialize HTTP transport: " + e.getMessage());
        }

        // Create tokens directory with full permissions
        java.io.File tokensDir = new java.io.File(TOKENS_DIRECTORY_PATH);
        if (!tokensDir.exists()) {
            if (!tokensDir.mkdirs()) {
                throw new IOException("Failed to create tokens directory at: " + TOKENS_DIRECTORY_PATH);
            }
        }

        // Build the authorization flow
        GoogleAuthorizationCodeFlow flow;
        try {
            flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(tokensDir))
                    .setAccessType("offline")
                    .build();
        } catch (Exception e) {
            throw new IOException("Failed to build authorization flow: " + e.getMessage());
        }

        // Try different ports for the custom HTTP server
        int[] ports = {8888, 8889, 8890, 8891, 8892};
        Exception lastException = null;
        Credential credential = null;

        for (int port : ports) {
            try {
                CustomServerReceiver receiver = new CustomServerReceiver(port);
                credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
                break;
            } catch (Exception e) {
                lastException = e;
                System.err.println("Failed to use port " + port + ": " + e.getMessage());
                if (port == ports[ports.length - 1]) {
                    throw new IOException("Failed to authorize. All ports are in use or there was an error with the OAuth flow: " + e.getMessage());
                }
            }
        }

        if (credential == null) {
            throw new IOException("Failed to obtain credentials. Last error: " + (lastException != null ? lastException.getMessage() : "Unknown error"));
        }

        // Build and return the calendar service
        try {
            return new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (Exception e) {
            throw new IOException("Failed to build calendar service: " + e.getMessage());
        }
    }

    private static class CustomServerReceiver implements VerificationCodeReceiver {
        private final int port;
        private ServerSocket serverSocket;
        private String code;

        public CustomServerReceiver(int port) {
            this.port = port;
        }

        @Override
        public String getRedirectUri() {
            return "http://localhost:" + port + "/Callback";
        }

        @Override
        public String waitForCode() throws IOException {
            serverSocket = new ServerSocket(port);
            code = null;

            System.out.println("Local server is listening on port " + port);

            try {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                
                // Read the HTTP request
                String line = in.readLine();
                if (line != null) {
                    // Extract the authorization code from the query string
                    int codeIndex = line.indexOf("code=");
                    if (codeIndex != -1) {
                        code = line.substring(codeIndex + 5);
                        int endIndex = code.indexOf(" HTTP/1.1");
                        if (endIndex != -1) {
                            code = code.substring(0, endIndex);
                        }
                        endIndex = code.indexOf("&");
                        if (endIndex != -1) {
                            code = code.substring(0, endIndex);
                        }
                    }
                }

                // Send HTTP response
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type: text/html");
                out.println();
                out.println("<html><head><title>Authorization Complete</title></head>");
                out.println("<body>Authorization completed! You can close this window and return to the application.</body></html>");

                clientSocket.close();
            } finally {
                stop();
            }

            return code;
        }

        @Override
        public void stop() throws IOException {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        }
    }

    public static void addQuizzesToCalendar() throws IOException, GeneralSecurityException {
        Calendar service = getCalendarService();
        System.out.println("Successfully connected to Google Calendar service.");

        try {
            // Clear existing events first to avoid duplicates
            clearExistingEvents(service);

            // Fetch all quizzes
            QuizService quizService = new QuizService();
            List<quiz> quizzes = quizService.rechercher();

            if (quizzes == null || quizzes.isEmpty()) {
                throw new IOException("No quizzes found in the database.");
            }

            System.out.println("Found " + quizzes.size() + " quizzes to process.");

            // Add each quiz as an event
            for (quiz quiz : quizzes) {
                Event event = createEventFromQuiz(quiz);
                try {
                    event = service.events().insert(CALENDAR_ID, event).execute();
                    System.out.printf("Successfully created event: %s (%s)\n", event.getSummary(), event.getHtmlLink());
                } catch (IOException e) {
                    System.err.printf("Failed to create event for quiz %d: %s\n",
                            quiz.getId(), e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error in addQuizzesToCalendar: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Failed to add quizzes to calendar: " + e.getMessage(), e);
        }
    }

    private static Event createEventFromQuiz(quiz quiz) {
        // Use the quiz's actual date
        LocalDate quizDate = quiz.getDate().toLocalDate();
        LocalTime startTime = LocalTime.of(10, 0); // Default to 10:00 AM
        LocalTime endTime = startTime.plusHours(1);

        // Convert to UTC DateTime
        java.util.Date startDate = java.util.Date.from(
            quizDate.atTime(startTime).atZone(java.time.ZoneId.of(TIME_ZONE)).toInstant()
        );
        java.util.Date endDate = java.util.Date.from(
            quizDate.atTime(endTime).atZone(java.time.ZoneId.of(TIME_ZONE)).toInstant()
        );

        // Create event description
        String description = String.format(
                "Quiz: %s\nType: %s",
                quiz.getName(),
                quiz.getType()
        );

        // Create and return the event
        return new Event()
                .setSummary(quiz.getName() + " - Quiz")
                .setDescription(description)
                .setStart(new EventDateTime()
                        .setDateTime(new DateTime(startDate))
                        .setTimeZone(TIME_ZONE))
                .setEnd(new EventDateTime()
                        .setDateTime(new DateTime(endDate))
                        .setTimeZone(TIME_ZONE));
    }

    private static void clearExistingEvents(Calendar service) throws IOException {
        try {
            // Delete all existing events
            Events events = service.events().list(CALENDAR_ID)
                    .setTimeMin(new DateTime(System.currentTimeMillis()))
                    .execute();

            if (events.getItems() != null) {
                for (Event event : events.getItems()) {
                    try {
                        service.events().delete(CALENDAR_ID, event.getId()).execute();
                        System.out.println("Deleted event: " + event.getSummary());
                    } catch (IOException e) {
                        System.err.println("Failed to delete event: " + event.getSummary() + " - " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            throw new IOException("Failed to clear existing events: " + e.getMessage(), e);
        }
    }

    public static void displayCalendar() throws IOException, GeneralSecurityException {
        Calendar service = getCalendarService();

        try {
            // Get the current time and events
            DateTime now = new DateTime(System.currentTimeMillis());
            Events events = service.events().list(CALENDAR_ID)
                    .setMaxResults(100)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();

            List<Event> items = events.getItems();

            // Create JavaFX UI components
            Stage calendarStage = new Stage();
            HBox mainContainer = new HBox(20);
            mainContainer.setPadding(new Insets(20));

            // Left side - Calendar
            VBox calendarContainer = new VBox(15);
            calendarContainer.setPrefWidth(700);

            // Add header with title and month navigation
            HBox header = new HBox(20);
            header.setAlignment(javafx.geometry.Pos.CENTER);

            Button prevMonth = new Button("◀");
            prevMonth.setId("navButton");

            Label titleLabel = new Label("Quiz Calendar");
            titleLabel.setId("titleLabel");

            Button nextMonth = new Button("▶");
            nextMonth.setId("navButton");

            DatePicker monthPicker = new DatePicker(LocalDate.now());
            monthPicker.setId("monthPicker");

            header.getChildren().addAll(prevMonth, titleLabel, monthPicker, nextMonth);

            // Create calendar grid
            GridPane calendarGrid = new GridPane();
            calendarGrid.setHgap(5);
            calendarGrid.setVgap(5);
            calendarGrid.setPadding(new Insets(10));
            calendarGrid.setId("calendarGrid");

            // Add day headers
            String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
            for (int i = 0; i < days.length; i++) {
                Label dayLabel = new Label(days[i]);
                dayLabel.getStyleClass().add("day-header");
                calendarGrid.add(dayLabel, i, 0);

                // Set column constraints for equal width
                ColumnConstraints colConstraints = new ColumnConstraints();
                colConstraints.setPercentWidth(100.0 / 7);
                calendarGrid.getColumnConstraints().add(colConstraints);
            }

            // Get current month details
            LocalDate currentDate = monthPicker.getValue();
            LocalDate firstOfMonth = currentDate.withDayOfMonth(1);
            int monthLength = currentDate.getMonth().length(currentDate.isLeapYear());
            int firstDayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;

            // Create a map of dates to events
            java.util.Map<String, List<Event>> eventsByDate = new java.util.HashMap<>();
            if (items != null) {
                for (Event event : items) {
                    DateTime eventDate = event.getStart().getDateTime();
                    if (eventDate != null) {
                        String dateKey = eventDate.toString().substring(0, 10);
                        eventsByDate.computeIfAbsent(dateKey, k -> new java.util.ArrayList<>()).add(event);
                    }
                }
            }

            // Fill in the calendar grid
            int day = 1;
            int week = 1;

            // Add empty cells for days before the first of the month
            for (int i = 0; i < firstDayOfWeek; i++) {
                VBox emptyBox = new VBox();
                emptyBox.getStyleClass().add("day-box");
                emptyBox.getStyleClass().add("empty-day");
                calendarGrid.add(emptyBox, i, week);
            }

            while (day <= monthLength) {
                for (int i = (week == 1 ? firstDayOfWeek : 0); i < 7 && day <= monthLength; i++) {
                    VBox dayBox = new VBox(5);
                    dayBox.getStyleClass().add("day-box");
                    if (day == currentDate.getDayOfMonth() && currentDate.equals(LocalDate.now())) {
                        dayBox.getStyleClass().add("today");
                    }

                    Label dateLabel = new Label(String.valueOf(day));
                    dateLabel.getStyleClass().add("date-label");
                    dayBox.getChildren().add(dateLabel);

                    // Add events for this day
                    String dateKey = String.format("%d-%02d-%02d",
                            currentDate.getYear(), currentDate.getMonthValue(), day);
                    List<Event> dayEvents = eventsByDate.get(dateKey);
                    if (dayEvents != null) {
                        for (Event event : dayEvents) {
                            Label eventLabel = new Label(event.getSummary());
                            eventLabel.getStyleClass().add("event-label");
                            eventLabel.setWrapText(true);
                            eventLabel.setMaxWidth(100);
                            dayBox.getChildren().add(eventLabel);
                        }
                    }

                    calendarGrid.add(dayBox, i, week);
                    day++;
                }
                week++;
            }

            // Fill remaining cells of the last week
            int lastDayColumn = (firstDayOfWeek + monthLength - 1) % 7;
            for (int i = lastDayColumn + 1; i < 7; i++) {
                VBox emptyBox = new VBox();
                emptyBox.getStyleClass().add("day-box");
                emptyBox.getStyleClass().add("empty-day");
                calendarGrid.add(emptyBox, i, week - 1);
            }

            // Right side - Upcoming Events
            VBox upcomingEventsContainer = new VBox(10);
            upcomingEventsContainer.setPrefWidth(300);
            upcomingEventsContainer.setId("upcomingEventsPanel");

            Label upcomingTitle = new Label("Upcoming Events");
            upcomingTitle.getStyleClass().add("section-title");

            ListView<VBox> upcomingListView = new ListView<>();
            upcomingListView.setId("upcomingList");
            upcomingListView.setPrefHeight(500);

            if (items.isEmpty()) {
                Label noEvents = new Label("No upcoming events");
                noEvents.getStyleClass().add("no-events");
                VBox noEventBox = new VBox(noEvents);
                noEventBox.setAlignment(javafx.geometry.Pos.CENTER);
                upcomingListView.getItems().add(noEventBox);
            } else {
                for (Event event : items) {
                    VBox eventBox = new VBox(5);
                    eventBox.getStyleClass().add("upcoming-event-box");

                    Label titleText = new Label(event.getSummary());
                    titleText.getStyleClass().add("upcoming-event-title");

                    DateTime start = event.getStart().getDateTime();
                    Label timeText = new Label("🕒 " + start.toString());
                    timeText.getStyleClass().add("upcoming-event-time");

                    Label locationText = new Label("📍 " +
                            (event.getLocation() != null ? event.getLocation() : "No location specified"));
                    locationText.getStyleClass().add("upcoming-event-location");

                    eventBox.getChildren().addAll(titleText, timeText, locationText);
                    upcomingListView.getItems().add(eventBox);
                }
            }

            upcomingEventsContainer.getChildren().addAll(upcomingTitle, upcomingListView);

            // Add navigation functionality
            prevMonth.setOnAction(e -> {
                LocalDate newDate = monthPicker.getValue().minusMonths(1);
                monthPicker.setValue(newDate);
                updateCalendarGrid(calendarGrid, service, newDate, eventsByDate);
            });

            nextMonth.setOnAction(e -> {
                LocalDate newDate = monthPicker.getValue().plusMonths(1);
                monthPicker.setValue(newDate);
                updateCalendarGrid(calendarGrid, service, newDate, eventsByDate);
            });

            monthPicker.setOnAction(e -> {
                updateCalendarGrid(calendarGrid, service, monthPicker.getValue(), eventsByDate);
            });

            // Create control buttons
            HBox controls = new HBox(10);
            controls.setAlignment(javafx.geometry.Pos.CENTER);

            Button refreshButton = new Button("Refresh Calendar");
            refreshButton.setId("refreshButton");
            refreshButton.setOnAction(e -> {
                try {
                    displayCalendar();
                    calendarStage.close();
                } catch (Exception ex) {
                    showError("Error refreshing calendar", ex.getMessage());
                }
            });

            controls.getChildren().add(refreshButton);

            // Add all components to containers
            calendarContainer.getChildren().addAll(header, calendarGrid, controls);
            mainContainer.getChildren().addAll(calendarContainer, upcomingEventsContainer);

            // Create scene and show stage
            Scene scene = new Scene(mainContainer, 1100, 700);
            String cssPath = GoogleCalendar.class.getResource("/styles/calendar.css").toExternalForm();
            scene.getStylesheets().add(cssPath);

            calendarStage.setTitle("Quiz Calendar");
            calendarStage.setScene(scene);
            calendarStage.show();

        } catch (IOException e) {
            showError("Calendar Error", "Failed to display calendar: " + e.getMessage());
            throw new IOException("Failed to display calendar: " + e.getMessage());
        }
    }

    private static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static void updateCalendarGrid(GridPane calendarGrid, Calendar service, LocalDate currentDate, java.util.Map<String, List<Event>> eventsByDate) {
        try {
            // Clear existing grid content (except headers)
            calendarGrid.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);

            // Get month details
            LocalDate firstOfMonth = currentDate.withDayOfMonth(1);
            int monthLength = currentDate.getMonth().length(currentDate.isLeapYear());
            int firstDayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;

            // Fill in the calendar grid
            int day = 1;
            int week = 1;

            // Add empty cells for days before the first of the month
            for (int i = 0; i < firstDayOfWeek; i++) {
                VBox emptyBox = new VBox();
                emptyBox.getStyleClass().add("day-box");
                emptyBox.getStyleClass().add("empty-day");
                calendarGrid.add(emptyBox, i, week);
            }

            while (day <= monthLength) {
                for (int i = (week == 1 ? firstDayOfWeek : 0); i < 7 && day <= monthLength; i++) {
                    VBox dayBox = new VBox(5);
                    dayBox.getStyleClass().add("day-box");
                    if (day == LocalDate.now().getDayOfMonth() &&
                            currentDate.getMonth() == LocalDate.now().getMonth() &&
                            currentDate.getYear() == LocalDate.now().getYear()) {
                        dayBox.getStyleClass().add("today");
                    }

                    Label dateLabel = new Label(String.valueOf(day));
                    dateLabel.getStyleClass().add("date-label");
                    dayBox.getChildren().add(dateLabel);

                    // Add events for this day
                    String dateKey = String.format("%d-%02d-%02d",
                            currentDate.getYear(), currentDate.getMonthValue(), day);
                    List<Event> dayEvents = eventsByDate.get(dateKey);
                    if (dayEvents != null) {
                        for (Event event : dayEvents) {
                            Label eventLabel = new Label(event.getSummary());
                            eventLabel.getStyleClass().add("event-label");
                            eventLabel.setWrapText(true);
                            eventLabel.setMaxWidth(100);
                            dayBox.getChildren().add(eventLabel);
                        }
                    }

                    calendarGrid.add(dayBox, i, week);
                    day++;
                }
                week++;
            }

            // Fill remaining cells of the last week
            int lastDayColumn = (firstDayOfWeek + monthLength - 1) % 7;
            for (int i = lastDayColumn + 1; i < 7; i++) {
                VBox emptyBox = new VBox();
                emptyBox.getStyleClass().add("day-box");
                emptyBox.getStyleClass().add("empty-day");
                calendarGrid.add(emptyBox, i, week - 1);
            }
        } catch (Exception e) {
            showError("Calendar Update Error", "Failed to update calendar grid: " + e.getMessage());
        }
    }
} 
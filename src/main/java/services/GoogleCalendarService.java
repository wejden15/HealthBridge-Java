package services;

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
import entities.Appointment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

public class GoogleCalendarService {
    private static final String APPLICATION_NAME = "Quiz Calendar";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_EVENTS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private final Calendar service;
    private boolean isAuthorized = false;
    private Credential credential;

    public GoogleCalendarService() {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Failed to create Google Calendar service", e);
        }
    }

    public void authorize() throws IOException {
        try {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            credential = getCredentials(HTTP_TRANSPORT);
            isAuthorized = true;
        } catch (GeneralSecurityException e) {
            throw new IOException("Failed to authorize Google Calendar service", e);
        }
    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets
        InputStream in = GoogleCalendarService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public Event createEvent(Event event) throws IOException {
        if (!isAuthorized) {
            throw new IllegalStateException("Google Calendar service is not authorized. Call authorize() first.");
                }
        return service.events().insert("primary", event).execute();
    }

    public void createAppointmentEvent(Appointment appointment) throws IOException {
        if (!isAuthorized) {
            throw new IllegalStateException("Google Calendar service is not authorized. Call authorize() first.");
        }

        try {
            System.out.println("Creating calendar event for appointment: " + appointment);
            
            Event event = new Event()
                    .setSummary("Medical Appointment with " + appointment.getClient_name())
                    .setDescription("Medical appointment scheduled through the Medical Appointment System");

            LocalDateTime appointmentDateTime = appointment.getAppointment_date();
            java.util.Date startDate = java.util.Date.from(appointmentDateTime.atZone(ZoneId.systemDefault()).toInstant());
            java.util.Date endDate = java.util.Date.from(appointmentDateTime.plusHours(1).atZone(ZoneId.systemDefault()).toInstant());

            System.out.println("Appointment time: " + startDate + " to " + endDate);

            event.setStart(new EventDateTime().setDateTime(new DateTime(startDate)));
            event.setEnd(new EventDateTime().setDateTime(new DateTime(endDate)));

            String calendarId = "primary";
            System.out.println("Inserting event into calendar...");
            event = service.events().insert(calendarId, event).execute();
            System.out.println("Event created successfully: " + event.getHtmlLink());
        } catch (Exception e) {
            System.err.println("Error creating calendar event: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void deleteAppointmentEvent(String eventId) throws IOException {
        if (!isAuthorized) {
            throw new IllegalStateException("Google Calendar service is not authorized. Call authorize() first.");
        }

        try {
            System.out.println("Deleting calendar event: " + eventId);
            service.events().delete("primary", eventId).execute();
            System.out.println("Event deleted successfully");
        } catch (Exception e) {
            System.err.println("Error deleting calendar event: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
} 
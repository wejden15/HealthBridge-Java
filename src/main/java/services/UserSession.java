package services;

import entities.User;

public class UserSession {
    private static User currentUser;
    private static boolean isLoggedIn = false;

    // Private constructor to prevent instantiation
    private UserSession() {}

    public static void login(User user) {
        currentUser = user;
        isLoggedIn = true;
    }

    public static void logout() {
        currentUser = null;
        isLoggedIn = false;
    }

    public static boolean isLoggedIn() {
        return isLoggedIn;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static int getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }

    public static String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : null;
    }

    public static String getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }
}
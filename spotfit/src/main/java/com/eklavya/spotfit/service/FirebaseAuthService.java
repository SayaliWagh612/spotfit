package com.eklavya.spotfit.service;

public class FirebaseAuthService {

    private static FirebaseAuthService instance;
    private String currentUserEmail;
    private String currentUserName;
    private String currentRole;
    private boolean isLoggedIn = false;

    private FirebaseAuthService() {}

    public static synchronized FirebaseAuthService getInstance() {
        if (instance == null) {
            instance = new FirebaseAuthService();
        }
        return instance;
    }

    public boolean signInUser(String email, String password, String roleStr) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        this.currentUserEmail = email.trim();
        this.currentUserName = email.split("@")[0];
        this.currentRole = roleStr;
        this.isLoggedIn = true;
        return true;
    }

    public boolean signUpUser(String email, String password, String name, String roleStr) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        this.currentUserEmail = email.trim();
        this.currentUserName = (name != null && !name.trim().isEmpty()) ? name.trim() : email.split("@")[0];
        this.currentRole = roleStr;
        this.isLoggedIn = true;
        return true;
    }

    public void signOut() {
        this.currentUserEmail = null;
        this.currentUserName = null;
        this.currentRole = null;
        this.isLoggedIn = false;
    }

    public String getCurrentUserEmail() {
        return currentUserEmail;
    }

    public String getCurrentUserName() {
        return currentUserName;
    }

    public String getCurrentRole() {
        return currentRole;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }
}

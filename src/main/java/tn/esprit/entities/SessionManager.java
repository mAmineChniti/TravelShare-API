package tn.esprit.entities;

public class SessionManager {
    private static SessionManager instance;
    private Utilisateur currentUtilisateur;  // L'utilisateur connecté

    // Private constructor to prevent instantiation
    private SessionManager() {}

    // Public method to get the single instance of SessionManager
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // Getter for the current user
    public Utilisateur getCurrentUtilisateur() {
        return currentUtilisateur;
    }

    // Setter for the current user (when the user logs in)
    public void setCurrentUtilisateur(Utilisateur utilisateur) {
        this.currentUtilisateur = utilisateur;
    }
}
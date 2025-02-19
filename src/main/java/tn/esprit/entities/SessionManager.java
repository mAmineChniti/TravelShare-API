package tn.esprit.entities;

public class SessionManager {
    private static Utilisateur currentUtilisateur;  // L'utilisateur connecté

    // Getter pour l'utilisateur courant
    public static Utilisateur getCurrentUtilisateur() {
        return currentUtilisateur;
    }

    // Setter pour l'utilisateur courant (quand l'utilisateur se connecte)
    public static void setCurrentUtilisateur(Utilisateur utilisateur) {
        currentUtilisateur = utilisateur;
    }
}



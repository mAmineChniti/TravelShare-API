package tn.esprit.entities;

import java.sql.Date;

public class Reclamation {
    private int reclamation_id, user_id;
    private String title, description;
    private Date date_reclamation;

    // Relation ManyToOne avec Utilisateur
    // Relation bidirectionnelle avec Utilisateur
    private Utilisateur utilisateur; // Un utilisateur peut avoir plusieurs réclamations

    // Relation OneToOne avec Reponse
    // Relation unidirectionnelle avec Reponse
    private Reponse reponse; // Une réclamation a une seule réponse

    // Constructeur par défaut
    public Reclamation() {}

    // Constructeur paramétré (y compris l'ID)
    public Reclamation(int reclamation_id, int user_id, String title, String description, Date date_reclamation) {
        this.reclamation_id = reclamation_id;
        this.user_id = user_id;
        this.title = title;
        this.description = description;
        this.date_reclamation = date_reclamation;
    }

    // Constructeur paramétré (sans l'ID)
    public Reclamation(int user_id, String title, String description, Date date_reclamation) {
        this.user_id = user_id;
        this.title = title;
        this.description = description;
        this.date_reclamation = date_reclamation;
    }

    // Getters et setters
    public int getReclamation_id() {
        return reclamation_id;
    }

    public void setReclamation_id(int reclamation_id) {
        this.reclamation_id = reclamation_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate_reclamation() {
        return date_reclamation;
    }

    public void setDate_reclamation(Date date_reclamation) {
        this.date_reclamation = date_reclamation;
    }

    // Getters et setters pour utilisateur et reponse
    public Utilisateur getUtilisateur() { return utilisateur; }

    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }

    public Reponse getReponse() { return reponse; }

    public void setReponse(Reponse reponse) { this.reponse = reponse; }

    // Méthode toString pour afficher les informations de la reclamation
    @Override
    public String toString() {
        return "Reclamation{" +
                "reclamation_id=" + reclamation_id +
                ", user_id=" + user_id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date_reclamation=" + date_reclamation +
                '}';
    }
}


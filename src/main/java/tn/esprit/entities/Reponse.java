package tn.esprit.entities;

import java.sql.Date;
import java.util.List;

public class Reponse {
    private int reponse_id, reclamation_id;
    private String contenu;
    private Date date_reponse;

    // Relation OneToOne avec Reclamation
    // Relation unidirectionnelle avec Reclamation
    private Reclamation reclamation; // Une réclamation est associée à une seule réponse

    // Constructeur par défaut
    public Reponse() {}

    // Constructeur paramétré (y compris l'ID)
    public Reponse(int reponse_id, int reclamation_id, String contenu, Date date_reponse) {
        this.reponse_id = reponse_id;
        this.reclamation_id = reclamation_id;
        this.contenu = contenu;
        this.date_reponse = date_reponse;
    }

    // Constructeur paramétré (sans l'ID)
    public Reponse(int reclamation_id, String contenu, Date date_reponse) {
        this.reclamation_id = reclamation_id;
        this.contenu = contenu;
        this.date_reponse = date_reponse;
    }

    // Getters et Setters
    public int getReponse_id() {
        return reponse_id;
    }

    public void setReponse_id(int reponse_id) {
        this.reponse_id = reponse_id;
    }

    public int getReclamation_id() {
        return reclamation_id;
    }

    public void setReclamation_id(int reclamation_id) {
        this.reclamation_id = reclamation_id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Date getDate_reponse() {
        return date_reponse;
    }

    public void setDate_reponse(Date date_reponse) {
        this.date_reponse = date_reponse;
    }

    // Getter et setter pour reclamation
    public Reclamation getReclamation() { return reclamation; }

    public void setReclamation(Reclamation reclamation) { this.reclamation = reclamation; }

    // Méthode toString pour afficher les informations de la reponse
    @Override
    public String toString() {
        return "Reponse{" +
                "reponse_id=" + reponse_id +
                ", reclamation_id=" + reclamation_id +
                ", contenu='" + contenu + '\'' +
                ", date_reponse=" + date_reponse +
                '}';
    }
}


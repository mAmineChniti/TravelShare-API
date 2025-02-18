package tn.esprit.entities;

import java.sql.Date;

public class OffreVoyages {
    private  int offres_voyage_id;
    private String titre;
    private String destination;
    private String description;
    private Date date_depart;
    private Date date_retour;
    private Double prix;
    private int places_disponibles;

    public int getOffres_voyage_id() {
        return offres_voyage_id;
    }

    public void setOffres_voyage_id(int offres_voyage_id) {
        this.offres_voyage_id = offres_voyage_id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate_depart() {
        return date_depart;
    }

    public void setDate_depart(Date date_depart) {
        this.date_depart = date_depart;
    }

    public Date getDate_retour() {
        return date_retour;
    }

    public void setDate_retour(Date date_retour) {
        this.date_retour = date_retour;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public int getPlaces_disponibles() {
        return places_disponibles;
    }

    public void setPlaces_disponibles(int places_disponibles) {
        this.places_disponibles = places_disponibles;
    }
}

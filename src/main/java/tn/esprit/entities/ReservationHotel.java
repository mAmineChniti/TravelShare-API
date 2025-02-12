package tn.esprit.entities;

import java.util.Date;

public class ReservationHotel {
    private int reservation_hotel_id;
    private int client_id;
    private int chambre_id;
    private Date date_debut;
    private Date date_fin;
    private String status_enu;
    private int prix_totale;

    // Constructeur par défaut
    public ReservationHotel() {}

    // Constructeur paramétré
    public ReservationHotel(int reservation_hotel_id, int client_id, int chambre_id, Date date_debut, Date date_fin, String status_enu, int prix_totale) {
        this.reservation_hotel_id = reservation_hotel_id;
        this.client_id = client_id;
        this.chambre_id = chambre_id;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.status_enu = status_enu;
        this.prix_totale = prix_totale;
    }

    // Getters et Setters
    public int getReservation_hotel_id() {
        return reservation_hotel_id;
    }

    public void setReservation_hotel_id(int reservation_hotel_id) {
        this.reservation_hotel_id = reservation_hotel_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public int getChambre_id() {
        return chambre_id;
    }

    public void setChambre_id(int chambre_id) {
        this.chambre_id = chambre_id;
    }

    public Date getDate_debut() {
        return date_debut;
    }

    public void setDate_debut(Date date_debut) {
        this.date_debut = date_debut;
    }

    public Date getDate_fin() {
        return date_fin;
    }

    public void setDate_fin(Date date_fin) {
        this.date_fin = date_fin;
    }

    public String getStatus_enu() {
        return status_enu;
    }

    public void setStatus_enu(String status_enu) {
        this.status_enu = status_enu;
    }

    public int getPrix_totale() {
        return prix_totale;
    }

    public void setPrix_totale(int prix_totale) {
        this.prix_totale = prix_totale;
    }
    @Override
    public String toString() {
        return "ReservationHotel{" +
                "reservation_hotel_id=" + reservation_hotel_id +
                ", client_id=" + client_id +
                ", chambre_id=" + chambre_id +
                ", date_debut=" + date_debut +
                ", date_fin=" + date_fin +
                ", status_enu='" + status_enu + '\'' +
                ", prix_totale=" + prix_totale +
                '}';
    }
}


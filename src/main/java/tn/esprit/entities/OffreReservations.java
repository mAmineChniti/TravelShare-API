package tn.esprit.entities;

import java.util.Date;

public class OffreReservations {
    private int reservation_id;
    private int client_id;
    private int offre_id;
    private Date date_reserved;
    private boolean reserved;
    private int nbr_place;
    private double prix;

    public int getReservation_id() {
        return reservation_id;
    }

    public void setReservation_id(int reservation_id) {
        this.reservation_id = reservation_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public int getOffre_id() {
        return offre_id;
    }

    public void setOffre_id(int offre_id) {
        this.offre_id = offre_id;
    }

    public Date getDate_reserved() {
        return date_reserved;
    }

    public void setDate_reserved(Date date_reserved) {
        this.date_reserved = date_reserved;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    public int getNbr_place() {
        return nbr_place;
    }

    public void setNbr_place(int nbr_place) {
        this.nbr_place = nbr_place;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }
}

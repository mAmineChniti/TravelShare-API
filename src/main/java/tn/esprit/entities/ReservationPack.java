package tn.esprit.entities;

import java.sql.Date;

public class ReservationPack {
    private int reservation_pack_id;
    private int client_id;
    private int pack_id;
    private Date date_reservation;
    private statusReservationPack status;
    private Double prix_total;

    public int getReservation_pack_id() {
        return reservation_pack_id;
    }

    public void setReservation_pack_id(int reservation_pack_id) {
        this.reservation_pack_id = reservation_pack_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public int getPack_id() {
        return pack_id;
    }

    public void setPack_id(int pack_id) {
        this.pack_id = pack_id;
    }

    public Date getDate_reservation() {
        return date_reservation;
    }

    public void setDate_reservation(Date date_reservation) {
        this.date_reservation = date_reservation;
    }

    public statusReservationPack getStatus() {
        return status;
    }

    public void setStatus(statusReservationPack status) {
        this.status = status;
    }

    public Double getPrix_total() {
        return prix_total;
    }

    public void setPrix_total(Double prix_total) {
        this.prix_total = prix_total;
    }
}

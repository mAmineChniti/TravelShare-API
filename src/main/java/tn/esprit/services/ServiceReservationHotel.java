package tn.esprit.services;

import tn.esprit.entities.ReservationHotel;
import tn.esprit.utils.dbCon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservationHotel implements IService<ReservationHotel> {
    private final Connection connection;

    public ServiceReservationHotel() {
        connection = dbCon.getInstance().getConnection();
    }

    @Override
    public void add(ReservationHotel reservationHotel) throws SQLException {
        String req = "INSERT INTO reservation_hotel (client_id, chambre_id, date_debut, date_fin, status_enu, prix_totale) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, reservationHotel.getClient_id());
            ps.setInt(2, reservationHotel.getChambre_id());
            ps.setDate(3, new java.sql.Date(reservationHotel.getDate_debut().getTime()));
            ps.setDate(4, new java.sql.Date(reservationHotel.getDate_fin().getTime()));
            ps.setString(5, reservationHotel.getStatus_enu());
            ps.setInt(6, reservationHotel.getPrix_totale());
            ps.executeUpdate();
        }
        System.out.println("Réservation ajoutée avec succès !");
    }

    @Override
    public void update(ReservationHotel reservationHotel) throws SQLException {
        String req = "UPDATE reservation_hotel SET client_id=?, chambre_id=?, date_debut=?, date_fin=?, status_enu=?, prix_totale=? WHERE reservation_hotel_id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, reservationHotel.getClient_id());
            ps.setInt(2, reservationHotel.getChambre_id());
            ps.setDate(3, new java.sql.Date(reservationHotel.getDate_debut().getTime()));
            ps.setDate(4, new java.sql.Date(reservationHotel.getDate_fin().getTime()));
            ps.setString(5, reservationHotel.getStatus_enu());
            ps.setInt(6, reservationHotel.getPrix_totale());
            ps.setInt(7, reservationHotel.getReservation_hotel_id());
            ps.executeUpdate();
        }
        System.out.println("Réservation mise à jour avec succès !");
    }

    @Override
    public void delete(int id) throws SQLException {
        String req = "DELETE FROM reservation_hotel WHERE reservation_hotel_id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
        System.out.println("Réservation supprimée avec succès !");
    }

    @Override
    public List<ReservationHotel> ListAll() throws SQLException {
        List<ReservationHotel> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservation_hotel";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                ReservationHotel reservationHotel = new ReservationHotel();
                reservationHotel.setReservation_hotel_id(rs.getInt("reservation_hotel_id"));
                reservationHotel.setClient_id(rs.getInt("client_id"));
                reservationHotel.setChambre_id(rs.getInt("chambre_id"));
                reservationHotel.setDate_debut(rs.getDate("date_debut"));
                reservationHotel.setDate_fin(rs.getDate("date_fin"));
                reservationHotel.setStatus_enu(rs.getString("status_enu"));
                reservationHotel.setPrix_totale(rs.getInt("prix_totale"));
                reservations.add(reservationHotel);
            }
        }
        return reservations;
    }
}
package tn.esprit.services;

import tn.esprit.entities.OffreReservations;
import tn.esprit.utils.dbCon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OffreReservationService implements IService<OffreReservations> {
    Connection con;

    public OffreReservationService() {
        con = dbCon.getInstance().getConnection();
    }

    @Override
    public void add(OffreReservations offreReservations) throws SQLException {
        String addReq = "INSERT INTO reservation_offres_voyage (client_id, offre_id, date_reserved, status) VALUES (?,?,?,?)";
        try (PreparedStatement prepStat = con.prepareStatement(addReq)) {
            prepStat.setInt(1, offreReservations.getClient_id());
            prepStat.setInt(2, offreReservations.getOffre_id());
            prepStat.setDate(3, new java.sql.Date(offreReservations.getDate_reserved().getTime()));
            prepStat.setBoolean(4, offreReservations.isReserved());
            prepStat.executeUpdate();
        }
    }

    @Override
    public void update(OffreReservations offreReservations) throws SQLException {
        String updateReq = "UPDATE reservation_offres_voyage SET client_id =?, offre_id =?, date_reserved =?, status =? WHERE reservation_id = ?";
        try (PreparedStatement prepStat = con.prepareStatement(updateReq)) {
            prepStat.setInt(1, offreReservations.getClient_id());
            prepStat.setInt(2, offreReservations.getOffre_id());
            prepStat.setDate(3, new java.sql.Date(offreReservations.getDate_reserved().getTime()));
            prepStat.setBoolean(4, offreReservations.isReserved());
            prepStat.setInt(5, offreReservations.getReservation_id());
            prepStat.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String deleteReq = "DELETE FROM reservation_offres_voyage WHERE reservation_id = ?";
        try (PreparedStatement prepStat = con.prepareStatement(deleteReq)) {
            prepStat.setInt(1, id);
            prepStat.executeUpdate();
        }
    }

    @Override
    public List<OffreReservations> ListAll() throws SQLException {
        List<OffreReservations> reservations = new ArrayList<>();
        String listReq = "SELECT * FROM reservation_offres_voyage";
        try (PreparedStatement prepStat = con.prepareStatement(listReq)) {
            ResultSet rs = prepStat.executeQuery();
            while (rs.next()) {
                OffreReservations reservation = new OffreReservations();
                reservation.setReservation_id(rs.getInt("reservation_id"));
                reservation.setClient_id(rs.getInt("client_id"));
                reservation.setOffre_id(rs.getInt("offre_id"));
                reservation.setDate_reserved(rs.getDate("date_reserved"));
                reservation.setReserved(rs.getBoolean("status"));
                reservations.add(reservation);
            }
        }
        return reservations;
    }
}
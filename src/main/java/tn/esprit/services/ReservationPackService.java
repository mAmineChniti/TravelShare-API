package tn.esprit.services;

import tn.esprit.entities.ReservationPack;
import tn.esprit.utils.dbCon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReservationPackService implements IService<ReservationPack>{
    Connection con;

    public ReservationPackService() {
        con = dbCon.getInstance().getConnection();
    }
    @Override
    public void add(ReservationPack reservationPack) throws SQLException {
        String addReq ="INSERT INTO reservation_packs (client_id, pack_id, date_reservation, statut, prix_total) VALUES (?,?,?,?,?) ";
        try (PreparedStatement prepStat = con.prepareStatement(addReq)) {
            prepStat.setInt(1, reservationPack.getClient_id());
            prepStat.setInt(2, reservationPack.getPack_id());
            prepStat.setDate(3, reservationPack.getDate_reservation());
            // enum
            prepStat.setDouble(5, reservationPack.getPrix_total());
            prepStat.executeUpdate();
        }

    }

    @Override
    public void update(ReservationPack reservationPack) throws SQLException {
    String updateReq = "UPDATE reservation_packs SET client_id = ?, pack_id = ?, date_reservation = ?, statut = ?, prix_total = ?, WHERE reservation_packs_id";
    try (PreparedStatement prepStat = con.prepareStatement(updateReq)) {
        prepStat.setInt(1, reservationPack.getClient_id());
        prepStat.setInt(2, reservationPack.getPack_id());
        prepStat.setDate(3, reservationPack.getDate_reservation());
        // enum
        prepStat.setDouble(5, reservationPack.getPrix_total());
        prepStat.setInt(6, reservationPack.getReservation_pack_id());
        prepStat.executeUpdate();
    }
    }

    @Override
    public void delete(int id) throws SQLException {
        String deleteReq = "DELETE FROM reservation_packs  WHERE reservation_packs_id = ?";
        try (PreparedStatement prepStat = con.prepareStatement(deleteReq)) {
            prepStat.setInt(1, id);
            prepStat.executeUpdate();
        }

    }

    @Override
    public List<ReservationPack> ListAll() throws SQLException {
        List<ReservationPack> reservationPacks = new ArrayList<>();
        String listAllReq = "SELECT * FROM reservation_packs";
        try (PreparedStatement prepStat = con.prepareStatement(listAllReq)) {
            ResultSet rs = prepStat.executeQuery();
            while (rs.next()) {
                ReservationPack reservationPack = new ReservationPack();
                reservationPack.setReservation_pack_id(rs.getInt("reservation_packs_id"));
                reservationPack.setClient_id(rs.getInt("client_id"));
                reservationPack.setPack_id(rs.getInt("pack_id"));
                reservationPack.setDate_reservation(rs.getDate("date_reservation"));
                // enum
                reservationPack.setPrix_total(rs.getDouble("prix_total"));
                reservationPacks.add(reservationPack);
            }
        }
        return reservationPacks;

    }
}

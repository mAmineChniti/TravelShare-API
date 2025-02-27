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
        String getPrixQuery = "SELECT prix, places_disponibles FROM offres_voyage WHERE offres_voyage_id = ?";
        String addReq = "INSERT INTO reservation_offres_voyage (client_id, offre_id, date_reserved, status, nbr_place, prix) VALUES (?,?,?,?,?,?)";
        String updatePlacesQuery = "UPDATE offres_voyage SET places_disponibles = places_disponibles - ? WHERE offres_voyage_id = ?";

        try (PreparedStatement getPrixStmt = con.prepareStatement(getPrixQuery)) {
            getPrixStmt.setInt(1, offreReservations.getOffre_id());
            ResultSet rs = getPrixStmt.executeQuery();
            if (rs.next()) {
                double prixVoyage = rs.getDouble("prix");
                int placesDisponibles = rs.getInt("places_disponibles");

                if (offreReservations.getNbr_place() > placesDisponibles) {
                    throw new SQLException("Not enough available places for this reservation.");
                }

                double totalPrix = prixVoyage * offreReservations.getNbr_place();

                try (PreparedStatement prepStat = con.prepareStatement(addReq)) {
                    prepStat.setInt(1, offreReservations.getClient_id());
                    prepStat.setInt(2, offreReservations.getOffre_id());
                    prepStat.setDate(3, new java.sql.Date(offreReservations.getDate_reserved().getTime()));
                    prepStat.setBoolean(4, offreReservations.isReserved());
                    prepStat.setInt(5, offreReservations.getNbr_place());
                    prepStat.setDouble(6, totalPrix);
                    prepStat.executeUpdate();
                }

                try (PreparedStatement updatePlacesStmt = con.prepareStatement(updatePlacesQuery)) {
                    updatePlacesStmt.setInt(1, offreReservations.getNbr_place());
                    updatePlacesStmt.setInt(2, offreReservations.getOffre_id());
                    updatePlacesStmt.executeUpdate();
                }
            }
        }
    }

    @Override
    public void update(OffreReservations offreReservations) throws SQLException {
        String getOldReservationQuery = "SELECT nbr_place FROM reservation_offres_voyage WHERE reservation_id = ?";
        String updateReq = "UPDATE reservation_offres_voyage SET client_id =?, offre_id =?, date_reserved =?, status =?, nbr_place =?, prix =? WHERE reservation_id = ?";
        String updatePlacesQuery = "UPDATE offres_voyage SET places_disponibles = places_disponibles + ? - ? WHERE offres_voyage_id = ?";

        try (PreparedStatement getOldReservationStmt = con.prepareStatement(getOldReservationQuery)) {
            getOldReservationStmt.setInt(1, offreReservations.getReservation_id());
            ResultSet rs = getOldReservationStmt.executeQuery();
            if (rs.next()) {
                int oldNbrPlace = rs.getInt("nbr_place");

                try (PreparedStatement getPrixStmt = con.prepareStatement("SELECT prix FROM offres_voyage WHERE offres_voyage_id = ?")) {
                    getPrixStmt.setInt(1, offreReservations.getOffre_id());
                    ResultSet rsPrix = getPrixStmt.executeQuery();
                    if (rsPrix.next()) {
                        double prixVoyage = rsPrix.getDouble("prix");
                        double totalPrix = prixVoyage * offreReservations.getNbr_place();

                        try (PreparedStatement prepStat = con.prepareStatement(updateReq)) {
                            prepStat.setInt(1, offreReservations.getClient_id());
                            prepStat.setInt(2, offreReservations.getOffre_id());
                            prepStat.setDate(3, new java.sql.Date(offreReservations.getDate_reserved().getTime()));
                            prepStat.setBoolean(4, offreReservations.isReserved());
                            prepStat.setInt(5, offreReservations.getNbr_place());
                            prepStat.setDouble(6, totalPrix);
                            prepStat.setInt(7, offreReservations.getReservation_id());
                            prepStat.executeUpdate();
                        }

                        try (PreparedStatement updatePlacesStmt = con.prepareStatement(updatePlacesQuery)) {
                            updatePlacesStmt.setInt(1, oldNbrPlace);
                            updatePlacesStmt.setInt(2, offreReservations.getNbr_place());
                            updatePlacesStmt.setInt(3, offreReservations.getOffre_id());
                            updatePlacesStmt.executeUpdate();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String getNbrPlaceQuery = "SELECT offre_id, nbr_place FROM reservation_offres_voyage WHERE reservation_id = ?";
        String deleteReq = "DELETE FROM reservation_offres_voyage WHERE reservation_id = ?";
        String updatePlacesQuery = "UPDATE offres_voyage SET places_disponibles = places_disponibles + ? WHERE offres_voyage_id = ?";

        try (PreparedStatement getNbrPlaceStmt = con.prepareStatement(getNbrPlaceQuery)) {
            getNbrPlaceStmt.setInt(1, id);
            ResultSet rs = getNbrPlaceStmt.executeQuery();
            if (rs.next()) {
                int offreId = rs.getInt("offre_id");
                int nbrPlace = rs.getInt("nbr_place");

                try (PreparedStatement deleteStmt = con.prepareStatement(deleteReq)) {
                    deleteStmt.setInt(1, id);
                    deleteStmt.executeUpdate();
                }

                try (PreparedStatement updatePlacesStmt = con.prepareStatement(updatePlacesQuery)) {
                    updatePlacesStmt.setInt(1, nbrPlace);
                    updatePlacesStmt.setInt(2, offreId);
                    updatePlacesStmt.executeUpdate();
                }
            }
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
                reservation.setNbr_place(rs.getInt("nbr_place"));
                reservation.setPrix(rs.getDouble("prix"));
                reservations.add(reservation);
            }
        }
        return reservations;
    }
}
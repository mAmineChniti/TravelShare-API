package tn.esprit.services;

import tn.esprit.entities.OffreVoyages;
import tn.esprit.utils.dbCon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OffreVoyageService implements IService<OffreVoyages> {
    Connection con;

    public OffreVoyageService() {
        con = dbCon.getInstance().getConnection();
    }

    @Override
    public void add(OffreVoyages offreVoyages) throws SQLException {
        String addReq = "INSERT INTO offres_voyage (titre,destination,description,date_depart,date_retour,prix,places_disponibles) VALUES (?,?,?,?,?,?,?) ";
        try (PreparedStatement prepStat = con.prepareStatement(addReq)) {
            prepStat.setString(1, offreVoyages.getTitre());
            prepStat.setString(2, offreVoyages.getDestination());
            prepStat.setString(3, offreVoyages.getDescription());
            prepStat.setDate(4, offreVoyages.getDate_depart());
            prepStat.setDate(5, offreVoyages.getDate_retour());
            prepStat.setDouble(6, offreVoyages.getPrix());
            prepStat.setInt(7, offreVoyages.getPlaces_disponibles());
            prepStat.executeUpdate();
        }
    }

    @Override
    public void update(OffreVoyages offreVoyages) throws SQLException {
        String updateReq = "UPDATE offres_voyage SET titre =?, destination =?, description =?, date_depart =?, date_retour =?, prix =?, places_disponibles =? WHERE offres_voyage_id  = ?";
        try (PreparedStatement prepStat = con.prepareStatement(updateReq)) {
            prepStat.setString(1, offreVoyages.getTitre());
            prepStat.setString(2, offreVoyages.getDestination());
            prepStat.setString(3, offreVoyages.getDescription());
            prepStat.setDate(4, offreVoyages.getDate_depart());
            prepStat.setDate(5, offreVoyages.getDate_retour());
            prepStat.setDouble(6, offreVoyages.getPrix());
            prepStat.setInt(7, offreVoyages.getPlaces_disponibles());
            prepStat.setInt(8, offreVoyages.getOffres_voyage_id());
            prepStat.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String deleteReq = "DELETE FROM offres_voyage  WHERE offres_voyage_id = ?";
        try (PreparedStatement prepStat = con.prepareStatement(deleteReq)) {
            prepStat.setInt(1, id);
            prepStat.executeUpdate();
        }
    }

    @Override
    public List<OffreVoyages> ListAll() throws SQLException {
        List<OffreVoyages> voyages = new ArrayList<>();
        String listReq = "SELECT * FROM offres_voyage";
        try (PreparedStatement prepStat = con.prepareStatement(listReq)) {
            ResultSet rs = prepStat.executeQuery();
            while (rs.next()) {
                OffreVoyages voyage = new OffreVoyages();
                voyage.setOffres_voyage_id(rs.getInt("offres_voyage_id"));
                voyage.setTitre(rs.getString("titre"));
                voyage.setDestination(rs.getString("destination"));
                voyage.setDescription(rs.getString("description"));
                voyage.setDate_depart(rs.getDate("date_depart"));
                voyage.setDate_retour(rs.getDate("date_retour"));
                voyage.setPrix(rs.getDouble("prix"));
                voyage.setPlaces_disponibles(rs.getInt("places_disponibles"));
                voyages.add(voyage);
            }
        }
        return voyages;
    }
}

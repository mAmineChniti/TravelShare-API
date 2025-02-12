package tn.esprit.services;

import tn.esprit.utils.dbCon;
import tn.esprit.entities.Hotels;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceHotels implements IService<Hotels> {
    private Connection connection;

    public ServiceHotels() {
        connection = dbCon.getInstance().getConnection();
    }

    @Override
    public void add(Hotels hotel) throws SQLException {
        String req = "INSERT INTO hotels (nom, adress, telephone, capacite_totale) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, hotel.getNom());
            ps.setString(2, hotel.getAdress());
            ps.setString(3, hotel.getTelephone());
            ps.setInt(4, hotel.getCapacite_totale());
            ps.executeUpdate();
        }
        System.out.println("Hôtel ajouté avec succès");
    }

    @Override
    public void update(Hotels hotel) throws SQLException {
        String req = "UPDATE hotels SET nom=?, adress=?, telephone=?, capacite_totale=? WHERE hotel_id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, hotel.getNom());
            ps.setString(2, hotel.getAdress());
            ps.setString(3, hotel.getTelephone());
            ps.setInt(4, hotel.getCapacite_totale());
            ps.setInt(5, hotel.getHotel_id());
            ps.executeUpdate();
        }
        System.out.println("Hôtel mis à jour avec succès");
    }

    @Override
    public void delete(int id) throws SQLException {
        String req = "DELETE FROM hotels WHERE hotel_id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
        System.out.println("Hôtel supprimé avec succès");
    }

    @Override
    public List<Hotels> ListAll() throws SQLException {
        List<Hotels> hotels = new ArrayList<>();
        String req = "SELECT * FROM hotels";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                Hotels hotel = new Hotels();
                hotel.setHotel_id(rs.getInt("hotel_id"));
                hotel.setNom(rs.getString("nom"));
                hotel.setAdress(rs.getString("adress"));
                hotel.setTelephone(rs.getString("telephone"));
                hotel.setCapacite_totale(rs.getInt("capacite_totale"));
                hotels.add(hotel);
            }
        }
        return hotels;
    }
}
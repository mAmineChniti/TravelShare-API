package tn.esprit.services;

import tn.esprit.entities.Chambres;
import tn.esprit.utils.dbCon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceChambre implements IService<Chambres> {
    private Connection connection;

    public ServiceChambre() {
        connection = dbCon.getInstance().getConnection();
    }

    @Override
    public void add(Chambres chambre) throws SQLException {
        String req = "INSERT INTO chambres (numero_chambre, type_enu, prix_par_nuit, disponible, hotel_id) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setString(1, chambre.getNumero_chambre());
        preparedStatement.setString(2, chambre.getType_enu());
        preparedStatement.setDouble(3, chambre.getPrix_par_nuit());
        preparedStatement.setBoolean(4, chambre.isDisponible());
        preparedStatement.setInt(5, chambre.getHotel_id());
        preparedStatement.executeUpdate();
        System.out.println("Chambre ajoutée avec succès !");
    }

    @Override
    public void update(Chambres chambre) throws SQLException {
        String req = "UPDATE chambres SET numero_chambre = ?, type_enu = ?, prix_par_nuit = ?, disponible = ?, hotel_id = ? WHERE chambre_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setString(1, chambre.getNumero_chambre());
        preparedStatement.setString(2, chambre.getType_enu());
        preparedStatement.setDouble(3, chambre.getPrix_par_nuit());
        preparedStatement.setBoolean(4, chambre.isDisponible());
        preparedStatement.setInt(5, chambre.getHotel_id());
        preparedStatement.setInt(6, chambre.getChambre_id());
        preparedStatement.executeUpdate();
        System.out.println("Chambre mise à jour avec succès !");
    }

    @Override
    public void delete(int id) throws SQLException {
        String req = "DELETE FROM chambres WHERE chambre_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        System.out.println("Chambre supprimée avec succès !");
    }

    @Override
    public List<Chambres> ListAll() throws SQLException {
        List<Chambres> chambres = new ArrayList<>();
        String req = "SELECT * FROM chambres";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(req);

        while (rs.next()) {
            Chambres chambre = new Chambres(
                    rs.getInt("chambre_id"),
                    rs.getInt("hotel_id"),
                    rs.getString("numero_chambre"),
                    rs.getString("type_enu"),
                    rs.getDouble("prix_par_nuit"),
                    rs.getBoolean("disponible")
            );
            chambres.add(chambre);
        }
        return chambres;
    }
}

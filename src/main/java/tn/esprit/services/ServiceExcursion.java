package tn.esprit.services;

import tn.esprit.entities.Excursions;
import tn.esprit.utils.dbCon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceExcursion implements IService<Excursions> {
    Connection connection;

    public ServiceExcursion() {
        connection = dbCon.getInstance().getConnection();
    }

    @Override
    public void add(Excursions excursions) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM excursions WHERE title = ? OR description = ?";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setString(1, excursions.getTitle());
            checkStmt.setString(2, excursions.getDescription());

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Cette excursion existe déjà dans la base de données !");
                    return;
                }
            }
        }

        String req = "INSERT INTO excursions (guide_id, title, description, date_fin, date_excursion, image,prix) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setInt(1, excursions.getGuide_id());
            statement.setString(2, excursions.getTitle());
            statement.setString(3, excursions.getDescription());
            statement.setDate(4, excursions.getDate_fin());
            statement.setDate(5, excursions.getDate_excursion());
            statement.setString(6, excursions.getImage());
            statement.setDouble(7, excursions.getPrix());

            statement.executeUpdate();
            System.out.println("Excursion ajoutée avec succès !");
        }
    }

    @Override
    public void update(Excursions excursions) throws SQLException {
        String req = "UPDATE excursions SET date_fin=?, title=?, description=?, date_excursion=?, guide_id=?, image=? , prix=? WHERE excursion_id=?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setDate(1, excursions.getDate_fin());
            preparedStatement.setString(2, excursions.getTitle());
            preparedStatement.setString(3, excursions.getDescription());
            preparedStatement.setDate(4, excursions.getDate_excursion());
            preparedStatement.setInt(5, excursions.getGuide_id());
            preparedStatement.setString(6, excursions.getImage());
            preparedStatement.setDouble(7, excursions.getPrix());
            preparedStatement.setInt(8, excursions.getExcursion_id());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Excursion modifiée avec succès !");
            } else {
                System.out.println("Aucune ligne modifiée.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'excursion : " + e.getMessage());
            throw e;  // Rejeter l'exception ou effectuer un traitement approprié
        }

    }

    @Override
    public void delete(int id) throws SQLException {
        String req = "DELETE FROM excursions WHERE excursion_id=?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            System.out.println("Excursion supprimée avec succès !");
        }
    }

    @Override
    public List<Excursions> ListAll() throws SQLException {
        List<Excursions> excursionsList = new ArrayList<>();
        String req = "SELECT * FROM excursions";
        try (Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(req)) {
            while (rs.next()) {
                Excursions excursion = new Excursions();
                excursion.setExcursion_id(rs.getInt("excursion_id"));
                excursion.setTitle(rs.getString("title"));
                excursion.setDescription(rs.getString("description"));
                excursion.setDate_excursion(rs.getDate("date_excursion"));
                excursion.setDate_fin(rs.getDate("date_fin"));
                excursion.setImage(rs.getString("image"));
                excursion.setPrix(rs.getDouble("prix"));
                excursionsList.add(excursion);
            }
        }
        return excursionsList;
    }


    public List<Excursions> search(String searchQuery) throws SQLException {
        List<Excursions> excursionsList = new ArrayList<>();

        // Préparer la requête SQL avec des paramètres de recherche dans le titre ou la description
        String req = "SELECT * FROM excursions WHERE title LIKE ? OR description LIKE ?";

        try (PreparedStatement statement = connection.prepareStatement(req)) {
            // Remplacer les ? par le critère de recherche
            String searchPattern = "%" + searchQuery + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);

            // Exécuter la requête et traiter le résultat
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Excursions excursion = new Excursions();
                    excursion.setExcursion_id(rs.getInt("excursion_id"));
                    excursion.setGuide_id(rs.getInt("guide_id"));
                    excursion.setTitle(rs.getString("title"));
                    excursion.setDescription(rs.getString("description"));
                    excursion.setDate_fin(rs.getDate("date_fin"));
                    excursion.setDate_excursion(rs.getDate("date_excursion"));
                    excursion.setImage(rs.getString("image"));
                    excursion.setPrix(rs.getDouble("prix"));

                    excursionsList.add(excursion);
                }
            }
        }

        return excursionsList;
    }

}
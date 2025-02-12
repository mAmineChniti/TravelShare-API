package tn.esprit.services;

import tn.esprit.entities.Excursions;
import tn.esprit.entities.Guides;
import tn.esprit.utils.dbCon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceExcursion implements IService<Excursions> {
    Connection connection;
    public ServiceExcursion() {
        connection= dbCon.getInstance().getConnection();
    }
    @Override
    public void add(Excursions excursions) throws SQLException {
        //controle de saisie pour experience
        if (excursions.getGuide_id() <= 0) {
            throw new IllegalArgumentException("L'ID du guide doit être positif.");
        }
        //controle de saisie pour titre
        if (excursions.getTitle() == null || excursions.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Le titre ne doit pas être vide.");
        }
        //controle de saisie pour description
        if (excursions.getDescription() == null || excursions.getDescription().isEmpty()) {
            throw new IllegalArgumentException("La description ne doit pas être vide.");
        }
        //controle de saisie pour duree
        if (excursions.getDuration() <= 0) {
            throw new IllegalArgumentException("La durée doit être positive.");
        }
        //controle de saisie pour date
        if (excursions.getDate_excursion() == null) {
            throw new IllegalArgumentException("La date de l'excursion ne doit pas être vide.");
        }

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

        String req = "INSERT INTO excursions (guide_id, title, description, duration, date_excursion) " +
                "VALUES (?, ?, ?, ?,?)";
        try (java.sql.PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setInt(1, excursions.getGuide_id());
            statement.setString(2, excursions.getTitle());
            statement.setString(3, excursions.getDescription());
            statement.setInt(4, excursions.getDuration());
            statement.setDate(5, excursions.getDate_excursion());


            statement.executeUpdate();
            System.out.println("Excursion ajouté avec succès !");
        }
    }

    @Override
    public void update(Excursions excursions) throws SQLException {
        //controle de saisie pour guide id
        if (excursions.getGuide_id() <= 0) {
            throw new IllegalArgumentException("L'ID du guide doit être positif.");
        }
        //controle de saisie pour titre
        if (excursions.getTitle() == null || excursions.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Le titre ne doit pas être vide.");
        }
        //controle de saisie pour description
        if (excursions.getDescription() == null || excursions.getDescription().isEmpty()) {
            throw new IllegalArgumentException("La description ne doit pas être vide.");
        }
        //controle de saisie pour duree
        if (excursions.getDuration() <= 0) {
            throw new IllegalArgumentException("La durée doit être positive.");
        }
        //controle de saisie pour date
        if (excursions.getDate_excursion() == null) {
            throw new IllegalArgumentException("La date de l'excursion ne doit pas être vide.");
        }

        String req = "UPDATE excursions SET duration=?, title=?, description=?," +
                " date_excursion=?, guide_id=? WHERE excursion_id=?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, excursions.getDuration());
        preparedStatement.setString(2, excursions.getTitle());
        preparedStatement.setString(3, excursions.getDescription());
        preparedStatement.setDate(4, excursions.getDate_excursion());
        preparedStatement.setInt(5, excursions.getGuide_id());
        preparedStatement.setInt(6, excursions.getExcursion_id());
        preparedStatement.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String req = "DELETE FROM excursions WHERE excursion_id=?";

        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1,id);

        preparedStatement.executeUpdate();
    }

    @Override
    public List<Excursions> ListAll() throws SQLException {
        List<Excursions> excursionsList = new ArrayList<>();
        String req = "SELECT * FROM excursions";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {

            while (rs.next()) {
                Excursions excursion = new Excursions();
                excursion.setExcursion_id(rs.getInt("excursion_id"));
                excursion.setGuide_id(rs.getInt("guide_id"));
                excursion.setTitle(rs.getString("title"));
                excursion.setDescription(rs.getString("description"));
                excursion.setDuration(rs.getInt("duration"));
                excursion.setDate_excursion(rs.getDate("date_excursion"));

                excursionsList.add(excursion);
            }
        }

        return excursionsList;
    }
}

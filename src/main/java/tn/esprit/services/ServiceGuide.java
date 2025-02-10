package tn.esprit.services;

import tn.esprit.entities.Guides;
import tn.esprit.utils.dbCon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public  class ServiceGuide implements IService <Guides>{
    Connection connection;
    public ServiceGuide(){
        connection= dbCon.getInstance().getConnection();
    }


    @Override
    public void add(Guides guides) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM guides WHERE email = ? OR phone_num = ?";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setString(1, guides.getEmail());
            checkStmt.setString(2, guides.getPhone_num());

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Ce guide existe déjà dans la base de données !");
                    return;
                }
            }
        }
        String req = "INSERT INTO guides (experience, name, last_name, email, phone_num, language) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (java.sql.PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setInt(1, guides.getExperience());
            statement.setString(2, guides.getName());
            statement.setString(3, guides.getLastname());
            statement.setString(4, guides.getEmail());
            statement.setString(5, guides.getPhone_num());
            statement.setString(6, guides.getLanguage());

            statement.executeUpdate();
            System.out.println("Guide ajouté avec succès !");
        }
    }

    @Override
    public void update(Guides guides) throws SQLException {

        String req = "UPDATE guides SET experience=?, name=?, last_name=?, email=?, phone_num=?, language=? WHERE guide_id=?";

        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, guides.getExperience());
        preparedStatement.setString(2, guides.getName());
        preparedStatement.setString(3, guides.getLastname());
        preparedStatement.setString(4, guides.getEmail());
        preparedStatement.setString(5, guides.getPhone_num());
        preparedStatement.setString(6, guides.getLanguage());
        preparedStatement.setInt(7, guides.getId_guide());
        preparedStatement.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String req = "DELETE FROM guides WHERE guide_id=?";

        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1,id);

        preparedStatement.executeUpdate();
    }

    @Override
    public List<Guides> ListAll() throws SQLException {
        List<Guides> guidesList = new ArrayList<>();
        String req = "SELECT * FROM guides";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {

            while (rs.next()) {
                Guides guide = new Guides();
                guide.setId_guide(rs.getInt("guide_id"));
                guide.setExperience(rs.getInt("experience"));
                guide.setName(rs.getString("name"));
                guide.setLastname(rs.getString("last_name"));
                guide.setEmail(rs.getString("email"));
                guide.setPhone_num(rs.getString("phone_num"));
                guide.setLanguage(rs.getString("language"));

                guidesList.add(guide);
            }
        }

        return guidesList;
    }
}

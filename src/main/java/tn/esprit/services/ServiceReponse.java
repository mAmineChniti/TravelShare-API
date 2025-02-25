package tn.esprit.services;

import tn.esprit.entities.Reponse;
import tn.esprit.utils.dbCon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReponse implements IService<Reponse> {
    // Initialiser la connexion à la base de données
    Connection con;

    public ServiceReponse() {
        con = dbCon.getInstance().getConnection();
    }

    // Méthode pour ajouter une réponse
    @Override
    public void add(Reponse reponse) throws SQLException {
        // Requête SQL pour insérer une nouvelle réponse
        String req = "INSERT INTO reponses (reclamation_id, contenu, date_reponse) " +
                "VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = con.prepareStatement(req)) {
            // Définir les valeurs des paramètres
            preparedStatement.setInt(1, reponse.getReclamation_id());
            preparedStatement.setString(2, reponse.getContenu());
            preparedStatement.setDate(3, reponse.getDate_reponse());

            // Exécuter la requête
            preparedStatement.executeUpdate();
            System.out.println("✅ Réponse ajoutée avec succès !");
        }
    }

    // Méthode pour modifier une réponse
    @Override
    public void update(Reponse reponse) throws SQLException {
        // Requête SQL pour la mise à jour des données
        String req = "UPDATE reponses SET reclamation_id = ?, contenu = ?, date_reponse = ? WHERE reponse_id = ?";

        try (PreparedStatement preparedStatement = con.prepareStatement(req)) {
            // Définir les valeurs des paramètres
            preparedStatement.setInt(1, reponse.getReclamation_id());
            preparedStatement.setString(2, reponse.getContenu());
            preparedStatement.setDate(3, reponse.getDate_reponse());
            preparedStatement.setInt(4, reponse.getReponse_id());

            // Exécuter la requête
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Réponse mise à jour avec succès !");
            } else {
                System.out.println("⚠️ Aucune réponse trouvée avec cet ID.");
            }
        }
    }

    // Méthode pour supprimer une réponse
    @Override
    public void delete(int reponse_id) throws SQLException {
        // Requête SQL pour la suppression
        String req = "DELETE FROM reponses WHERE reponse_id = ?";

        try (PreparedStatement preparedStatement = con.prepareStatement(req)) {
            // Définir la valeur du paramètre
            preparedStatement.setInt(1, reponse_id);

            // Exécuter la requête
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Réponse supprimée avec succès !");
            } else {
                System.out.println("⚠️ Aucune réponse trouvée avec cet ID.");
            }
        }
    }

    // Méthode pour afficher la liste des réponses
    @Override
    public List<Reponse> ListAll() throws SQLException {
        // Liste pour stocker les réponses récupérées
        List<Reponse> reponses = new ArrayList<>();

        // Requête SQL pour récupérer toutes les réponses
        String req = "SELECT * FROM reponses";
        Statement statement = con.createStatement();

        // Exécuter la requête et obtenir les résultats
        ResultSet rs = statement.executeQuery(req);

        while (rs.next()) {
            // Créer un nouvel objet Reponse et remplir ses attributs à partir des résultats
            Reponse reponse = new Reponse();
            reponse.setReponse_id(rs.getInt("reponse_id"));
            reponse.setReclamation_id(rs.getInt("reclamation_id"));
            reponse.setContenu(rs.getString("contenu"));
            reponse.setDate_reponse(rs.getDate("date_reponse"));

            // Ajouter la réponse à la liste
            reponses.add(reponse);
        }

        return reponses;
    }

    public int getReponseIdByContenu(String contenu) throws SQLException {
        String query = "SELECT reponse_id FROM reponses WHERE contenu = ?";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, contenu);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("reponse_id");
            }
        }
        return -1;  // Retourne -1 si la réponse n'est pas trouvée
    }


    public List<String> getReponsesWithUserInfoAndRec() throws SQLException {
        List<String> affichageList = new ArrayList<>();

        // Requête avec jointure entre `users`, `reclamations`, et `reponses`
        String req = "SELECT u.name, u.email, r.title, rp.contenu, rp.date_reponse " +
                "FROM reponses rp " +
                "JOIN reclamations r ON rp.reclamation_id = r.reclamation_id " +
                "JOIN users u ON r.user_id = u.user_id";

        try (Statement statement = con.createStatement();
             ResultSet rs = statement.executeQuery(req)) {

            while (rs.next()) {
                String affichage = "Nom : " + rs.getString("name") +
                        " , Email : " + rs.getString("email") +
                        " , Titre : " + rs.getString("title") +
                        " , Contenu : " + rs.getString("contenu") +
                        " , Date : " + rs.getDate("date_reponse");
                affichageList.add(affichage);
            }
        }

        return affichageList;
    }

    public void updateReponseContenu(int reponse_id, String newContenu) throws SQLException {
        String query = "UPDATE reponses SET contenu = ? WHERE reponse_id = ?";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, newContenu);
            statement.setInt(2, reponse_id);
            statement.executeUpdate();
        }
    }

}

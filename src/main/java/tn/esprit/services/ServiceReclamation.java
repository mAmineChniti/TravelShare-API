package tn.esprit.services;

import tn.esprit.entities.Reclamation;
import tn.esprit.utils.dbCon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReclamation implements IService<Reclamation> {
    // Initialiser la connexion à la base de données
    Connection con;

    public ServiceReclamation() {
        con = dbCon.getInstance().getConnection();
    }

    // Méthode pour ajouter une réclamation
    @Override
    public void add(Reclamation reclamation) throws SQLException {
        // Requête SQL pour insérer une nouvelle réclamation
        String req = "INSERT INTO reclamations (user_id, title, description, date_reclamation) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = con.prepareStatement(req)) {
            // Définir les valeurs des paramètres
            preparedStatement.setInt(1, reclamation.getUser_id());
            preparedStatement.setString(2, reclamation.getTitle());
            preparedStatement.setString(3, reclamation.getDescription());
            preparedStatement.setDate(4, reclamation.getDate_reclamation());

            // Exécuter la requête
            preparedStatement.executeUpdate();
            System.out.println("✅ Réclamation ajoutée avec succès !");
        }
    }

    // Méthode pour modifier une réclamation
    @Override
    public void update(Reclamation reclamation) throws SQLException {
        // Requête SQL pour la mise à jour des données
        String req = "UPDATE reclamations SET user_id = ?, title = ?, description = ?, date_reclamation = ? WHERE reclamation_id = ?";

        try (PreparedStatement preparedStatement = con.prepareStatement(req)) {
            // Définir les valeurs des paramètres
            preparedStatement.setInt(1, reclamation.getUser_id());
            preparedStatement.setString(2, reclamation.getTitle());
            preparedStatement.setString(3, reclamation.getDescription());
            preparedStatement.setDate(4, reclamation.getDate_reclamation());
            preparedStatement.setInt(5, reclamation.getReclamation_id());

            // Exécuter la requête
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Réclamation mise à jour avec succès !");
            } else {
                System.out.println("⚠️ Aucune réclamation trouvée avec cet ID.");
            }
        }
    }

    // Méthode pour supprimer une réclamation
    @Override
    public void delete(int reclamation_id) throws SQLException {
        // Requête SQL pour la suppression
        String req = "DELETE FROM reclamations WHERE reclamation_id = ?";

        try (PreparedStatement preparedStatement = con.prepareStatement(req)) {
            // Définir la valeur du paramètre
            preparedStatement.setInt(1, reclamation_id);

            // Exécuter la requête
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Réclamation supprimée avec succès !");
            } else {
                System.out.println("⚠️ Aucune réclamation trouvée avec cet ID.");
            }
        }
    }

    // Méthode pour afficher la liste des réclamations
    @Override
    public List<Reclamation> ListAll() throws SQLException {
        // Liste pour stocker les réclamations récupérées
        List<Reclamation> reclamations = new ArrayList<>();

        // Requête SQL pour récupérer toutes les réclamations
        String req = "SELECT * FROM reclamations";
        Statement statement = con.createStatement();

        // Exécuter la requête et obtenir les résultats
        ResultSet rs = statement.executeQuery(req);

        while (rs.next()) {
            // Créer un nouvel objet Reclamation et remplir ses attributs à partir des résultats
            Reclamation reclamation = new Reclamation();
            reclamation.setReclamation_id(rs.getInt("reclamation_id"));
            reclamation.setUser_id(rs.getInt("user_id"));
            reclamation.setTitle(rs.getString("title"));
            reclamation.setDescription(rs.getString("description"));
            reclamation.setDate_reclamation(rs.getDate("date_reclamation"));

            // Ajouter la réclamation à la liste
            reclamations.add(reclamation);
        }

        return reclamations;
    }

    public int getReclamationIdByTitle(String title) throws SQLException {
        String query = "SELECT reclamation_id FROM reclamations WHERE title = ?";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, title);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("reclamation_id"); // Retourner l'ID trouvé
            }
        }
        return -1; // Si aucun ID trouvé
    }

    public List<String> getReclamationsWithUserInfo() throws SQLException {
        List<String> affichageList = new ArrayList<>();

        // Requête avec jointure entre `users` et `reclamations`
        String req = "SELECT u.name, u.email, r.title, r.description, r.date_reclamation " +
                "FROM reclamations r " +
                "JOIN users u ON r.user_id = u.user_id";

        try (Statement statement = con.createStatement();
             ResultSet rs = statement.executeQuery(req)) {

            while (rs.next()) {
                String affichage = "Nom : " + rs.getString("name") +
                        " - Email : " + rs.getString("email") +
                        " - Titre : " + rs.getString("title") +
                        " - Description : " + rs.getString("description") +
                        " - Date de reclamation : " + rs.getDate("date_reclamation");
                affichageList.add(affichage);
            }
        }

        return affichageList;
    }


}

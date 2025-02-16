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
        // Contrôle de saisie
        if (reclamation.getUser_id() <= 0) {
            System.out.println("⚠️ L'ID de l'utilisateur doit être valide.");
            return;
        }

        if (reclamation.getTitle() == null || reclamation.getTitle().isEmpty()) {
            System.out.println("⚠️ Le titre ne peut pas être vide.");
            return;
        }

        if (reclamation.getDescription() == null || reclamation.getDescription().isEmpty()) {
            System.out.println("⚠️ La description ne peut pas être vide.");
            return;
        }

        if (reclamation.getDate_reclamation() == null) {
            System.out.println("⚠️ La date de réclamation ne peut pas être nulle.");
            return;
        }

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
        // Contrôle de saisie
        if (reclamation.getReclamation_id() <= 0) {
            System.out.println("⚠️ L'ID de la réclamation doit être valide.");
            return;
        }

        if (reclamation.getUser_id() <= 0) {
            System.out.println("⚠️ L'ID de l'utilisateur doit être valide.");
            return;
        }

        if (reclamation.getTitle() == null || reclamation.getTitle().isEmpty()) {
            System.out.println("⚠️ Le titre ne peut pas être vide.");
            return;
        }

        if (reclamation.getDescription() == null || reclamation.getDescription().isEmpty()) {
            System.out.println("⚠️ La description ne peut pas être vide.");
            return;
        }

        if (reclamation.getDate_reclamation() == null) {
            System.out.println("⚠️ La date de réclamation ne peut pas être nulle.");
            return;
        }

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
        // Contrôle de saisie
        if (reclamation_id <= 0) {
            System.out.println("⚠️ L'ID de la réclamation doit être valide.");
            return;
        }

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
}

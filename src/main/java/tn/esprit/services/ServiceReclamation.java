package tn.esprit.services;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tn.esprit.entities.Reclamation;
import tn.esprit.utils.dbCon;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        String req = "INSERT INTO reclamations (user_id, title, description, date_reclamation, etat) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = con.prepareStatement(req)) {
            // Définir les valeurs des paramètres
            preparedStatement.setInt(1, reclamation.getUser_id());
            preparedStatement.setString(2, reclamation.getTitle());
            preparedStatement.setString(3, reclamation.getDescription());
            preparedStatement.setDate(4, reclamation.getDate_reclamation());
            preparedStatement.setString(5, reclamation.getEtat());


            // Exécuter la requête
            preparedStatement.executeUpdate();
            System.out.println("✅ Réclamation ajoutée avec succès !");
        }
    }

    // Méthode pour modifier une réclamation
    @Override
    public void update(Reclamation reclamation) throws SQLException {
        // Requête SQL pour la mise à jour des données, y compris l'état
        String req = "UPDATE reclamations SET user_id = ?, title = ?, description = ?, date_reclamation = ?, etat = ? WHERE reclamation_id = ?";

        try (PreparedStatement preparedStatement = con.prepareStatement(req)) {
            // Définir les valeurs des paramètres
            preparedStatement.setInt(1, reclamation.getUser_id());
            preparedStatement.setString(2, reclamation.getTitle());
            preparedStatement.setString(3, reclamation.getDescription());
            preparedStatement.setDate(4, reclamation.getDate_reclamation());
            preparedStatement.setString(5, reclamation.getEtat());  // Ajouter l'état
            preparedStatement.setInt(6, reclamation.getReclamation_id());

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

        // Requête SQL pour récupérer toutes les réclamations, y compris l'état
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
            reclamation.setEtat(rs.getString("etat"));  // Ajouter la récupération de l'état

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
        String req = "SELECT u.name, u.email, u.photo, r.title, r.description, r.date_reclamation, r.etat " +
                "FROM reclamations r " +
                "JOIN users u ON r.user_id = u.user_id";

        try (Statement statement = con.createStatement();
             ResultSet rs = statement.executeQuery(req)) {

            while (rs.next()) {
                // Créer une chaîne qui contient toutes les informations à afficher
                String affichage =
                        " Nom : " + rs.getString("name") +
                        " - Email : " + rs.getString("email") +
                        " - Titre : " + rs.getString("title") +
                        " - Description : " + rs.getString("description") +
                        " - Date de réclamation : " + rs.getDate("date_reclamation") +
                        " - Etat : " + rs.getString("etat");

                // Ajouter l'affichage à la liste
                affichageList.add(affichage);
            }
        }

        return affichageList;
    }

    public void updateEtatReclamation(int reclamationId, String nouvelEtat) throws SQLException {
        String query = "UPDATE reclamations SET etat = ? WHERE reclamation_id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, nouvelEtat);
            pstmt.setInt(2, reclamationId);
            pstmt.executeUpdate();
        }
    }

    public List<String> filtrerReclamationsParEtat(String etat) throws SQLException {
        // Récupérer toutes les réclamations avec leurs informations
        List<String> allReclamations = getReclamationsWithUserInfo();

        // Si "Tous" est sélectionné, retourner la liste complète
        if (etat.equals("Tous")) {
            return allReclamations; // Retourne toutes les réclamations
        } else {
            // Sinon, filtrer en fonction de l'état (ex. "En cours", "Répondu")
            return allReclamations.stream()
                    .filter(reclamation -> reclamation.toLowerCase().contains(etat.toLowerCase()))
                    .collect(Collectors.toList());
        }
    }

}

package tn.esprit.services;

import tn.esprit.entities.Utilisateur;
import tn.esprit.utils.dbCon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceUtilisateur implements IService<Utilisateur> {
    // Initialiser la connexion à la base de données
    Connection con;

    public ServiceUtilisateur() {
        con = dbCon.getInstance().getConnection();
    }

    // Méthode pour ajouter un utilisateur avec contrôle de saisie
    @Override
    public void add(Utilisateur utilisateur) throws SQLException {
        // Vérifier si l'utilisateur existe déjà dans la base de données via son email
        String checkQuery = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkQuery)) {
            checkStmt.setString(1, utilisateur.getEmail());

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("⚠️ Cet utilisateur existe déjà dans la base de données !");
                    return;
                }
            }
        }

        // Requête pour insérer un nouvel utilisateur

        // Assigner un rôle par défaut à 0 pour l'utilisateur
        utilisateur.setRole((byte) 0);
        String req = "INSERT INTO users (name, last_name, email, password, phone_num, address) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = con.prepareStatement(req)) {
            statement.setString(1, utilisateur.getName());
            statement.setString(2, utilisateur.getLast_name());
            statement.setString(3, utilisateur.getEmail());
            statement.setString(4, utilisateur.getPassword());
            statement.setInt(5, utilisateur.getPhone_num());
            statement.setString(6, utilisateur.getAddress());

            // Exécuter la requête
            statement.executeUpdate();
            System.out.println("✅ Utilisateur ajouté avec succès !");
        }
    }

    // Méthode pour modifier un utilisateur avec contrôle de saisie
    @Override
    public void update(Utilisateur utilisateur) throws SQLException {
        // Requête SQL pour la mise à jour des données
        String req = "UPDATE users SET name = ?, last_name = ?, email = ?, password = ?, phone_num = ?, address = ? WHERE user_id = ?";
        PreparedStatement preparedStatement = con.prepareStatement(req);

        // Définir les valeurs des paramètres
        preparedStatement.setString(1, utilisateur.getName());
        preparedStatement.setString(2, utilisateur.getLast_name());
        preparedStatement.setString(3, utilisateur.getEmail());
        preparedStatement.setString(4, utilisateur.getPassword());
        preparedStatement.setInt(5, utilisateur.getPhone_num());
        preparedStatement.setString(6, utilisateur.getAddress());
        preparedStatement.setInt(7, utilisateur.getUser_id());

        // Exécuter la requête
        int rowsUpdated = preparedStatement.executeUpdate();

        // Vérifier si une ligne a été mise à jour
        if (rowsUpdated > 0) {
            System.out.println("✅ Modification effectuée avec succès !");
        } else {
            System.out.println("⚠️ Aucune modification n'a été effectuée. Veuillez vérifier l'ID de l'utilisateur.");
        }
    }

    // Méthode pour supprimer un utilisateur
    @Override
    public void delete(int user_id) throws SQLException {
        // Requête SQL pour la suppression
        String req = "DELETE FROM users WHERE user_id = ?";
        PreparedStatement preparedStatement = con.prepareStatement(req);

        // Définir la valeur du paramètre
        preparedStatement.setInt(1, user_id);

        // Exécuter la requête et récupérer le nombre de lignes affectées
        int rowsAffected = preparedStatement.executeUpdate();

        // Vérifier si une ligne a été supprimée
        if (rowsAffected > 0) {
            System.out.println("✅ Suppression effectuée avec succès !");
        } else {
            System.out.println("⚠️ Aucun utilisateur trouvé avec cet ID. Suppression non effectuée.");
        }
    }

    // Méthode pour afficher la liste des utilisateurs
    @Override
    public List<Utilisateur> ListAll() throws SQLException {
        // Liste pour stocker les utilisateurs récupérés
        List<Utilisateur> utilisateurs = new ArrayList<>();

        // Requête SQL pour récupérer tous les utilisateurs
        String req = "SELECT * FROM users";
        Statement statement = con.createStatement();

        // Exécuter la requête et obtenir les résultats
        ResultSet rs = statement.executeQuery(req);

        while (rs.next()) {
            // Créer un nouvel objet Utilisateur et remplir ses attributs à partir des résultats
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setUser_id(rs.getInt("user_id"));
            utilisateur.setName(rs.getString("name"));
            utilisateur.setLast_name(rs.getString("last_name"));
            utilisateur.setEmail(rs.getString("email"));
            utilisateur.setPassword(rs.getString("password"));
            utilisateur.setPhone_num(rs.getInt("phone_num"));
            utilisateur.setAddress(rs.getString("address"));
            utilisateur.setRole(rs.getByte("role")); // Récupération du rôle

            // Ajouter l'utilisateur à la liste
            utilisateurs.add(utilisateur);
        }

        return utilisateurs;
    }

    // Méthode pour authentifier un utilisateur
    public Utilisateur authenticate(String email, String password) throws SQLException {
        // Requête SQL pour vérifier les informations d'identification
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";  // On récupère toutes les colonnes nécessaires

        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    // L'utilisateur existe avec les informations d'identification valides
                    Utilisateur utilisateur = new Utilisateur();
                    utilisateur.setUser_id(rs.getInt("user_id"));
                    utilisateur.setName(rs.getString("name"));
                    utilisateur.setLast_name(rs.getString("last_name"));
                    utilisateur.setEmail(rs.getString("email"));
                    utilisateur.setPassword(rs.getString("password"));
                    utilisateur.setPhone_num(rs.getInt("phone_num"));
                    utilisateur.setAddress(rs.getString("address"));
                    utilisateur.setRole(rs.getByte("role"));

                    System.out.println("✅ Authentification réussie !");
                    return utilisateur;  // Retourne l'utilisateur authentifié
                } else {
                    // L'utilisateur n'a pas été trouvé ou les informations sont incorrectes
                    System.out.println("⚠️ Email ou mot de passe incorrect !");
                    return null;  // Retourne null si l'authentification échoue
                }
            }
        }
    }

    public boolean emailExiste(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = dbCon.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Retourne vrai si l'email existe déjà
                }
            }
        }
        return false;
    }

}

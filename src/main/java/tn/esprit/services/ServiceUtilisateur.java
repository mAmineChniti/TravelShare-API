package tn.esprit.services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import tn.esprit.entities.Utilisateur;
import tn.esprit.utils.dbCon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String req = "INSERT INTO users (name, last_name, email, password, phone_num, address, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String hashedPassword = BCrypt.withDefaults().hashToString(12, utilisateur.getPassword().toCharArray());
        try (PreparedStatement statement = con.prepareStatement(req)) {
            statement.setString(1, utilisateur.getName());
            statement.setString(2, utilisateur.getLast_name());
            statement.setString(3, utilisateur.getEmail());
            statement.setString(4, hashedPassword);
            statement.setInt(5, utilisateur.getPhone_num());
            statement.setString(6, utilisateur.getAddress());
            statement.setByte(7, utilisateur.getRole());
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
        String hashedPassword = BCrypt.withDefaults().hashToString(12, utilisateur.getPassword().toCharArray());
        // Définir les valeurs des paramètres
        preparedStatement.setString(1, utilisateur.getName());
        preparedStatement.setString(2, utilisateur.getLast_name());
        preparedStatement.setString(3, utilisateur.getEmail());
        preparedStatement.setString(4, hashedPassword);
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
            // utilisateur.setPassword(rs.getString("password"));
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
        String query = "SELECT * FROM users WHERE email = ?";

        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setString(1, email);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    // Get stored hashed password from DB
                    String storedHashedPassword = rs.getString("password");

                    // Verify entered password against stored hash
                    BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), storedHashedPassword);

                    if (result.verified) {  // Correct password
                        Utilisateur utilisateur = new Utilisateur();
                        utilisateur.setUser_id(rs.getInt("user_id"));
                        utilisateur.setName(rs.getString("name"));
                        utilisateur.setLast_name(rs.getString("last_name"));
                        utilisateur.setEmail(rs.getString("email"));
                        utilisateur.setPhone_num(rs.getInt("phone_num"));
                        utilisateur.setAddress(rs.getString("address"));
                        utilisateur.setRole(rs.getByte("role"));

                        System.out.println("✅ Authentification réussie !");
                        return utilisateur;
                    } else {
                        System.out.println("⚠️ Email ou mot de passe incorrect !");
                        return null;
                    }
                } else {
                    System.out.println("⚠️ Utilisateur non trouvé !");
                    return null;
                }
            }
        }
    }
}

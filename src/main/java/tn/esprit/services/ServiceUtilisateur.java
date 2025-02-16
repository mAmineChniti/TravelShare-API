package tn.esprit.services;

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
        // Contrôle des champs vides
        if (utilisateur.getName() == null || utilisateur.getName().isEmpty()) {
            System.out.println("⚠️ Le nom ne peut pas être vide.");
            return;
        }
        if (utilisateur.getLast_name() == null || utilisateur.getLast_name().isEmpty()) {
            System.out.println("⚠️ Le prénom ne peut pas être vide.");
            return;
        }
        if (utilisateur.getEmail() == null || utilisateur.getEmail().isEmpty()) {
            System.out.println("⚠️ L'email ne peut pas être vide.");
            return;
        }
        if (utilisateur.getPassword() == null || utilisateur.getPassword().isEmpty()) {
            System.out.println("⚠️ Le mot de passe ne peut pas être vide.");
            return;
        }
        if (utilisateur.getPhone_num() == 0) {
            System.out.println("⚠️ Le numéro de téléphone ne peut pas être vide.");
            return;
        }
        if (utilisateur.getAddress() == null || utilisateur.getAddress().isEmpty()) {
            System.out.println("⚠️ L'adresse ne peut pas être vide.");
            return;
        }

        // Contrôle du format de l'email (doit être de la forme chaine@chaine.com)
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        Pattern patternEmail = Pattern.compile(emailPattern);
        Matcher matcherEmail = patternEmail.matcher(utilisateur.getEmail());

        if (!matcherEmail.matches()) {
            System.out.println("⚠️ Format d'email invalide ! L'email doit être de la forme chaine@chaine.com");
            return; // Arrêter l'ajout si l'email est invalide
        }

        // Contrôle du mot de passe : au moins une lettre, un chiffre et un caractère spécial
        String passwordPattern = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{6,}$";
        Pattern patternPassword = Pattern.compile(passwordPattern);
        Matcher matcherPassword = patternPassword.matcher(utilisateur.getPassword());

        if (!matcherPassword.matches()) {
            System.out.println("⚠️ Le mot de passe doit contenir au moins une lettre, un chiffre et un caractère spécial !");
            return; // Arrêter l'ajout si le mot de passe est invalide
        }

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
        String req = "INSERT INTO users (name, last_name, email, password, phone_num, address, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = con.prepareStatement(req)) {
            statement.setString(1, utilisateur.getName());
            statement.setString(2, utilisateur.getLast_name());
            statement.setString(3, utilisateur.getEmail());
            statement.setString(4, utilisateur.getPassword());
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
        // Contrôle des champs vides
        if (utilisateur.getName() == null || utilisateur.getName().isEmpty()) {
            System.out.println("⚠️ Le nom ne peut pas être vide.");
            return;
        }
        if (utilisateur.getLast_name() == null || utilisateur.getLast_name().isEmpty()) {
            System.out.println("⚠️ Le prénom ne peut pas être vide.");
            return;
        }
        if (utilisateur.getEmail() == null || utilisateur.getEmail().isEmpty()) {
            System.out.println("⚠️ L'email ne peut pas être vide.");
            return;
        }
        if (utilisateur.getPassword() == null || utilisateur.getPassword().isEmpty()) {
            System.out.println("⚠️ Le mot de passe ne peut pas être vide.");
            return;
        }
        if (utilisateur.getPhone_num() == 0) {
            System.out.println("⚠️ Le numéro de téléphone ne peut pas être vide.");
            return;
        }
        if (utilisateur.getAddress() == null || utilisateur.getAddress().isEmpty()) {
            System.out.println("⚠️ L'adresse ne peut pas être vide.");
            return;
        }

        // Contrôle du format de l'email (doit être de la forme chaine@chaine.com)
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        Pattern patternEmail = Pattern.compile(emailPattern);
        Matcher matcherEmail = patternEmail.matcher(utilisateur.getEmail());

        if (!matcherEmail.matches()) {
            System.out.println("⚠️ Format d'email invalide ! L'email doit être de la forme chaine@chaine.com");
            return; // Arrêter la modification si l'email est invalide
        }

        // Contrôle du mot de passe : au moins une lettre, un chiffre et un caractère spécial
        String passwordPattern = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{6,}$";
        Pattern patternPassword = Pattern.compile(passwordPattern);
        Matcher matcherPassword = patternPassword.matcher(utilisateur.getPassword());

        if (!matcherPassword.matches()) {
            System.out.println("⚠️ Le mot de passe doit contenir au moins une lettre, un chiffre et un caractère spécial !");
            return; // Arrêter la modification si le mot de passe est invalide
        }

        // Requête SQL pour la mise à jour des données
        String req = "UPDATE users SET name = ?, last_name = ?, email = ?, password = ?, phone_num = ?, address = ?, role = ? WHERE user_id = ?";
        PreparedStatement preparedStatement = con.prepareStatement(req);

        // Définir les valeurs des paramètres
        preparedStatement.setString(1, utilisateur.getName());
        preparedStatement.setString(2, utilisateur.getLast_name());
        preparedStatement.setString(3, utilisateur.getEmail());
        preparedStatement.setString(4, utilisateur.getPassword());
        preparedStatement.setInt(5, utilisateur.getPhone_num());
        preparedStatement.setString(6, utilisateur.getAddress());
        preparedStatement.setByte(7, utilisateur.getRole());
        preparedStatement.setInt(8, utilisateur.getUser_id());

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
        // Contrôle de saisie : Vérifier si l'ID de l'utilisateur est valide
        if (user_id <= 0) {
            System.out.println("⚠️ L'ID de l'utilisateur doit être positif.");
            return; // Arrêter la suppression si l'ID est invalide
        }

        // Vérification si l'utilisateur existe dans la base de données
        String checkQuery = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, user_id);

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("⚠️ Aucun utilisateur trouvé avec cet ID. Suppression non effectuée.");
                    return; // Arrêter si l'utilisateur n'existe pas
                }
            }
        }

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
}

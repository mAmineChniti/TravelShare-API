package tn.esprit.services;

import at.favre.lib.crypto.bcrypt.BCrypt;
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

    // Méthode pour ajouter un utilisateur
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

        // Assigner un rôle par défaut à 0 pour l'utilisateur
        utilisateur.setRole((byte) 0);

        // Requête d'insertion incluant la photo par défaut
        String req = "INSERT INTO users (name, last_name, email, password, phone_num, address, role, photo, compte) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Hashage du mot de passe
        String hashedPassword = BCrypt.withDefaults().hashToString(12, utilisateur.getPassword().toCharArray());

        try (PreparedStatement statement = con.prepareStatement(req)) {
            statement.setString(1, utilisateur.getName());
            statement.setString(2, utilisateur.getLast_name());
            statement.setString(3, utilisateur.getEmail());
            statement.setString(4, hashedPassword);
            statement.setInt(5, utilisateur.getPhone_num());
            statement.setString(6, utilisateur.getAddress());
            statement.setByte(7, utilisateur.getRole());
            statement.setBytes(8, utilisateur.getPhoto());  // Insertion automatique de la photo par défaut
            statement.setByte(9, utilisateur.getRole());

            // Exécuter la requête
            statement.executeUpdate();
            System.out.println("✅ Utilisateur ajouté avec succès !");
        }
    }


    @Override
    public void update(Utilisateur utilisateur) throws SQLException {
        // Vérifier si l'utilisateur existe
        String checkQuery = "SELECT password, photo FROM users WHERE user_id = ?";
        byte[] existingPhoto = null;
        String existingPassword = null;

        try (PreparedStatement checkStmt = con.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, utilisateur.getUser_id());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    existingPassword = rs.getString("password"); // Récupérer l'ancien mot de passe
                    existingPhoto = rs.getBytes("photo"); // Récupérer l'ancienne photo sous forme de tableau d'octets
                } else {
                    System.out.println("⚠️ L'utilisateur avec cet ID n'existe pas !");
                    return;
                }
            }
        }

        // Déterminer la photo à utiliser (soit une nouvelle photo, soit conserver l'ancienne)
        byte[] newPhoto = (utilisateur.getPhoto() != null && utilisateur.getPhoto().length > 0)
                ? utilisateur.getPhoto() // Utiliser la nouvelle photo si elle est fournie
                : existingPhoto; // Sinon, conserver l'ancienne photo

        // Vérifier si le mot de passe a changé
        String newPassword = (utilisateur.getPassword() != null && !utilisateur.getPassword().isEmpty()
                && !utilisateur.getPassword().equals(existingPassword))
                ? BCrypt.withDefaults().hashToString(12, utilisateur.getPassword().toCharArray())
                : existingPassword;

        // Requête SQL pour la mise à jour
        String req = "UPDATE users SET name = ?, last_name = ?, email = ?, password = ?, phone_num = ?, address = ?, photo = ? WHERE user_id = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(req)) {
            preparedStatement.setString(1, utilisateur.getName());
            preparedStatement.setString(2, utilisateur.getLast_name());
            preparedStatement.setString(3, utilisateur.getEmail());
            preparedStatement.setString(4, newPassword);
            preparedStatement.setInt(5, utilisateur.getPhone_num());
            preparedStatement.setString(6, utilisateur.getAddress());

            // Si la photo est présente, l'envoyer sous forme de tableau d'octets, sinon conserver l'ancienne
            if (newPhoto != null) {
                preparedStatement.setBytes(7, newPhoto); // Utiliser setBytes pour envoyer la photo binaire
            } else {
                preparedStatement.setNull(7, java.sql.Types.BLOB); // Si aucune photo, mettre NULL
            }

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
            // utilisateur.setPassword(rs.getString("password")); // Si vous souhaitez récupérer le mot de passe, laissez cette ligne
            utilisateur.setPhone_num(rs.getInt("phone_num"));
            utilisateur.setAddress(rs.getString("address"));
            utilisateur.setRole(rs.getByte("role")); // Récupération du rôle
            utilisateur.setCompte(rs.getByte("compte")); // Récupération de l'état du compte

            // Récupérer la photo sous forme de byte[] et l'assigner à l'utilisateur
            byte[] photo = rs.getBytes("photo"); // Récupérer la photo en tant que tableau d'octets
            utilisateur.setPhoto(photo); // Assigner la photo à l'utilisateur

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
                    // Vérifier si le compte est bloqué avant de vérifier le mot de passe
                    if (rs.getByte("compte") == 1) {
                        // Le compte est bloqué, retourner un utilisateur avec un champ spécifique indiquant le blocage
                        Utilisateur blockedUser = new Utilisateur();
                        blockedUser.setCompte((byte) 1); // Indicateur que le compte est bloqué
                        return blockedUser;
                    }

                    // Récupérer le mot de passe haché stocké dans la base de données
                    String storedHashedPassword = rs.getString("password");

                    // Vérifier le mot de passe saisi par rapport au hachage stocké
                    BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), storedHashedPassword);

                    if (result.verified) {  // Mot de passe correct
                        Utilisateur utilisateur = new Utilisateur();
                        utilisateur.setUser_id(rs.getInt("user_id"));
                        utilisateur.setName(rs.getString("name"));
                        utilisateur.setLast_name(rs.getString("last_name"));
                        utilisateur.setEmail(rs.getString("email"));
                        utilisateur.setPhone_num(rs.getInt("phone_num"));
                        utilisateur.setAddress(rs.getString("address"));
                        utilisateur.setRole(rs.getByte("role"));
                        utilisateur.setCompte(rs.getByte("compte")); // Récupération de l'état du compte

                        // Récupérer la photo sous forme de byte[] et l'affecter à l'utilisateur
                        byte[] photo = rs.getBytes("photo");
                        utilisateur.setPhoto(photo); // Affectation de la photo

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


    public boolean emailExists(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public void updatePassword(String email, String newPassword) {
        // Hachage du mot de passe avant de le stocker
        String hashedPassword = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray());

        String query = "UPDATE users SET password = ? WHERE email = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, hashedPassword);  // Stocker le mot de passe haché
            pst.setString(2, email);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePhoto(int userId, byte[] newPhoto) throws SQLException {
        // Vérifier si l'utilisateur existe
        String checkQuery = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, userId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("⚠️ Utilisateur non trouvé !");
                    return;
                }
            }
        }

        // Requête SQL pour la mise à jour de la photo
        String updateQuery = "UPDATE users SET photo = ? WHERE user_id = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(updateQuery)) {
            // Mettre à jour la photo en tant que tableau d'octets
            if (newPhoto != null && newPhoto.length > 0) {
                preparedStatement.setBytes(1, newPhoto);
            } else {
                // Si la nouvelle photo est vide ou nulle, la mettre à NULL dans la base de données
                preparedStatement.setNull(1, java.sql.Types.BLOB);
            }
            preparedStatement.setInt(2, userId);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Photo mise à jour avec succès !");
            } else {
                System.out.println("⚠️ La mise à jour de la photo a échoué.");
            }
        }
    }

    public void bloquerUtilisateur(int user_id) throws SQLException {
        String req = "UPDATE users SET compte = 1 WHERE user_id = ?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, user_id);
        ps.executeUpdate();
    }

    public void debloquerUtilisateur(int user_id) throws SQLException {
        String req = "UPDATE users SET compte = 0 WHERE user_id = ?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, user_id);
        ps.executeUpdate();
    }

}

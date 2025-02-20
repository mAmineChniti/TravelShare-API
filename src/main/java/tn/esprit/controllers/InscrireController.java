package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.ServiceUtilisateur;

import java.io.IOException;
import java.sql.SQLException;

public class InscrireController {

    @FXML
    private TextField addressField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField lastnameField;

    @FXML
    private AnchorPane layer1;

    @FXML
    private AnchorPane layer2;

    @FXML
    private TextField nameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField phoneField;

    @FXML
    private Label u1;

    @FXML
    private Label u2;


    @FXML
    void sinscrire(ActionEvent event) {
        // Récupérer les valeurs des champs
        String name = nameField.getText();
        String last_name = lastnameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();

        // Vérification si tous les champs sont remplis
        if (name.isEmpty() || last_name.isEmpty() || email.isEmpty() || password.isEmpty() ||
                phone.isEmpty() || address.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs manquants", "Tous les champs doivent être remplis !");
            return;
        }

        // Vérification du format de l'email
        if (!email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
            showAlert(Alert.AlertType.ERROR, "Erreur de format", "L'email doit être valide (exemple@domaine.com)");
            return;
        }

        // Vérification du mot de passe
        if (password.length() < 4 || !password.matches(".*\\d.*") || !password.matches(".*[!@#$%^&*].*")) {
            showAlert(Alert.AlertType.ERROR, "Erreur de format", "Le mot de passe doit contenir au moins 4 caractères, un chiffre et un caractère spécial.");
            return;
        }

        try {
            ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

            // Vérifier si l'email existe déjà dans la base de données
            if (serviceUtilisateur.emailExiste(email)) {
                showAlert(Alert.AlertType.ERROR, "Email existant", "Cet email est déjà utilisé. Veuillez en choisir un autre.");
                return;
            }

            // Convertir les valeurs numériques
            int phoneNum = Integer.parseInt(phone);

            // Créer un objet Utilisateur
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setName(name);
            utilisateur.setLast_name(last_name);
            utilisateur.setEmail(email);
            utilisateur.setPassword(password);
            utilisateur.setPhone_num(phoneNum);
            utilisateur.setAddress(address);

            // Ajouter l'utilisateur à la base de données
            serviceUtilisateur.add(utilisateur);
            System.out.println("Utilisateur ajouté avec succès !");

            // Rediriger vers la page Connecter.fxml après une inscription réussie
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Connecter.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Veuillez entrer un numéro de téléphone valide.");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue. Veuillez réessayer plus tard.");
        }
    }

    // Méthode pour afficher les alertes
    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    @FXML
    void switchToSignIn(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Connecter.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();  // Affiche l'erreur dans la console
        }
    }

}


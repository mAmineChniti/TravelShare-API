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
    private TextField roleField;

    @FXML
    private Label u1;

    @FXML
    private Label u2;


    @FXML
    void switchToAccueil(ActionEvent event) {
        // Récupérer les valeurs des champs
        String name = nameField.getText();
        String lastname = lastnameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();
        String role = roleField.getText();

        // Vérification si tous les champs sont remplis
        if (name.isEmpty() || lastname.isEmpty() || email.isEmpty() || password.isEmpty() ||
                phone.isEmpty() || address.isEmpty() || role.isEmpty()) {
            // Créer une alerte d'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Champs manquants");
            alert.setHeaderText("Tous les champs doivent être remplis !");
            alert.setContentText("Veuillez remplir tous les champs avant de vous inscrire.");
            alert.showAndWait();
            return; // Arrêter l'exécution si des champs sont vides
        }

        try {
            // Convertir les valeurs numériques
            int phoneNum = Integer.parseInt(phone);
            byte roleByte = Byte.parseByte(role);

            // Créer un objet Utilisateur
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setName(name);
            utilisateur.setLast_name(lastname);
            utilisateur.setEmail(email);
            utilisateur.setPassword(password);
            utilisateur.setPhone_num(phoneNum);
            utilisateur.setAddress(address);
            utilisateur.setRole(roleByte);

            // Ajouter l'utilisateur à la base de données
            ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
            serviceUtilisateur.add(utilisateur);
            System.out.println("Utilisateur ajouté avec succès !");

            // Rediriger vers la page Accueil.fxml après une inscription réussie
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Accueil.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (NumberFormatException e) {
            // Gérer les erreurs de conversion
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText("Format incorrect !");
            alert.setContentText("Veuillez entrer un numéro de téléphone et un rôle valides.");
            alert.showAndWait();
        } catch (SQLException | IOException e) {
            // Gérer les erreurs SQL ou de navigation
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Une erreur est survenue.");
            alert.setContentText("Veuillez réessayer plus tard.");
            alert.showAndWait();
        }
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


package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entities.SessionManager;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.ServiceUtilisateur;

import java.io.IOException;
import java.sql.SQLException;

public class ConnecterController {

    @FXML
    private TextField emailField1;

    @FXML
    private TextField passwordField1;

    @FXML
    private Label emailError;

    @FXML
    private Label passwordError;

    @FXML
    private Label u1;

    @FXML
    private Label u2;

    @FXML
    private Hyperlink forgetpassword;

    private final ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();


    @FXML
    void btnsignin(ActionEvent event) {
        String email = emailField1.getText();
        String password = passwordField1.getText();

        // Masquer les erreurs au début
        emailError.setVisible(false);
        passwordError.setVisible(false);

        // Réinitialiser la bordure des champs
        emailField1.getStyleClass().remove("text-field-error");
        passwordField1.getStyleClass().remove("text-field-error");

        // Vérification des champs vides
        if (email.isEmpty() && password.isEmpty()) {
            emailError.setText("Email requis.");
            passwordError.setText("Mot de passe requis.");
            emailError.setVisible(true); // Affiche l'erreur
            passwordError.setVisible(true); // Affiche l'erreur
            emailField1.getStyleClass().add("text-field-error"); // Ajoute la classe d'erreur
            passwordField1.getStyleClass().add("text-field-error"); // Ajoute la classe d'erreur
            return;
        } else if (email.isEmpty()) {
            emailError.setText("Email requis.");
            emailError.setVisible(true); // Affiche l'erreur
            emailField1.getStyleClass().add("text-field-error"); // Ajoute la classe d'erreur
            return;
        } else if (password.isEmpty()) {
            passwordError.setText("Mot de passe requis.");
            passwordError.setVisible(true); // Affiche l'erreur
            passwordField1.getStyleClass().add("text-field-error"); // Ajoute la classe d'erreur
            return;
        }

        try {
            // Authentification de l'utilisateur
            Utilisateur utilisateur = serviceUtilisateur.authenticate(email, password);

            if (utilisateur != null) {
                // Si l'utilisateur est trouvé, on le sauvegarde dans la session
                SessionManager.getInstance().setCurrentUtilisateur(utilisateur);

                // Vérification du rôle de l'utilisateur
                FXMLLoader loader;
                if (utilisateur.getRole() == 0) {
                    // Rôle utilisateur : rediriger vers Accueil.fxml
                    loader = new FXMLLoader(getClass().getResource("/Accueil.fxml"));
                } else if (utilisateur.getRole() == 1) {
                    // Rôle admin : rediriger vers AccueilAdmin.fxml
                    loader = new FXMLLoader(getClass().getResource("/AccueilAdmin.fxml"));
                } else {
                    // Rôle inconnu, afficher un message d'erreur
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Rôle inconnu");
                    alert.setHeaderText("Rôle d'utilisateur non valide");
                    alert.setContentText("Impossible de déterminer le rôle de l'utilisateur.");
                    alert.showAndWait();
                    return;
                }

                // Charger la page correspondante
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            } else {
                // Si l'authentification échoue
                emailError.setText("Email ou mot de passe incorrect !");
                emailError.setVisible(true); // Affiche l'erreur
                passwordError.setText("Email ou mot de passe incorrect !");
                passwordError.setVisible(true); // Affiche l'erreur
                return;
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Un problème est survenu");
            alert.setContentText("Veuillez réessayer plus tard.");
            alert.showAndWait();
        }
    }

    @FXML
    void forgetpassword(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ForgetPassword.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @FXML
    void switchToSignUp(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Inscrire.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



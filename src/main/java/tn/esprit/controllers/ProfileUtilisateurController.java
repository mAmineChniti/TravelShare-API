package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import tn.esprit.entities.Reclamation;
import tn.esprit.entities.SessionManager;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.ServiceUtilisateur;
import tn.esprit.services.ServiceReclamation;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;

import static tn.esprit.entities.SessionManager.getCurrentUtilisateur;

public class ProfileUtilisateurController {

    @FXML
    private TextField addressField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField lastnameField;

    @FXML
    private TextField nameField;

    @FXML
    private TextArea objetField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField phoneField;

    @FXML
    private Hyperlink deleteProfileLink;

    @FXML
    private ImageView showPasswordIcon;

    @FXML
    private Button saveButton;

    @FXML
    private Hyperlink listeRec;

    private final ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
    private Utilisateur utilisateur;
    private final ServiceReclamation serviceReclamation = new ServiceReclamation();

    private boolean isPasswordVisible = false;

    @FXML
    void deleteProfile(ActionEvent event) {
        // Créer une alerte de confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Vous êtes sur le point de supprimer votre profil.");
        alert.setContentText("Voulez-vous vraiment supprimer votre profil ?");

        // Si l'utilisateur confirme
        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                // Vérifier si un utilisateur est connecté
                Utilisateur currentUtilisateur = SessionManager.getCurrentUtilisateur();
                if (currentUtilisateur != null) {
                    int user_id = currentUtilisateur.getUser_id(); // Récupérer l'ID de l'utilisateur connecté

                    // Supprimer le profil de la base de données
                    try {
                        serviceUtilisateur.delete(user_id);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                    // Déconnecter l'utilisateur et rediriger vers la page de connexion
                    try {
                        // Charger la page de connexion
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Connecter.fxml"));
                        Parent root = loader.load();

                        // Obtenir la scène actuelle
                        Stage stage = (Stage) deleteProfileLink.getScene().getWindow();

                        // Rediriger vers la page de connexion sans fermer la fenêtre actuelle
                        stage.setScene(new Scene(root));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // Si l'utilisateur n'est pas connecté, afficher une erreur
                    System.out.println("Aucun utilisateur connecté !");
                }
            }
        });
    }


    @FXML
    void updateProfile(ActionEvent event) {
        // Récupérer les valeurs des champs
        String name = nameField.getText();
        String last_name = lastnameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();

        // Valider les champs (vérification de base)
        if (name.isEmpty() || last_name.isEmpty() || email.isEmpty() || password.isEmpty()
                || phone.isEmpty() || address.isEmpty()) {
            // Afficher une alerte si un champ est vide
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur de validation");
            alert.setHeaderText("Tous les champs doivent être remplis.");
            alert.showAndWait();
            return;
        }

        // Récupérer l'utilisateur connecté et son ID
        Utilisateur currentUser = getCurrentUtilisateur(); // Correction ici
        int user_id = currentUser.getUser_id(); // Extraire l'ID

        // Créer un utilisateur avec les nouvelles données
        Utilisateur utilisateur = new Utilisateur(user_id, name, last_name, email, password, Integer.parseInt(phone), address);

        // Appeler la méthode update de ServiceUtilisateur pour enregistrer les modifications dans la base
        try {
            serviceUtilisateur.update(utilisateur);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Après la mise à jour, rediriger l'utilisateur vers la page de profil avec les nouvelles informations
        try {
            // Charger la page de profil avec les informations mises à jour
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Accueil.fxml"));
            Parent root = loader.load();

            // Rediriger vers la page de profil
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void envoyerReclamation(ActionEvent event) {
        // Récupérer les valeurs des champs title et description
        String title = objetField.getText();
        String description = descriptionField.getText();

        // Vérifier si les champs ne sont pas vides
        if (title.isEmpty() || description.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur");
            alert.setHeaderText("Tous les champs doivent être remplis.");
            alert.showAndWait();
            return;
        }

        // Récupérer l'ID de l'utilisateur (exemple : si l'utilisateur est déjà connecté)
        int user_id = getCurrentUtilisateur().getUser_id();  // Méthode pour récupérer l'ID utilisateur

        // Obtenir la date du jour (automatiquement)
        Date currentDate = new Date(System.currentTimeMillis());  // Date actuelle du système

        // Créer l'objet Reclamation avec title, description et la date actuelle
        Reclamation reclamation = new Reclamation(user_id, title, description, currentDate);

        // Afficher une alerte pour confirmer l'envoi
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Êtes-vous sûr de vouloir envoyer cette réclamation ?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Ajouter la réclamation dans la base de données
                    serviceReclamation.add(reclamation);  // Méthode pour ajouter la réclamation

                    // Rediriger vers la page d'accueil après envoi
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Accueil.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();  // Récupérer la fenêtre actuelle
                    stage.setScene(new Scene(root));  // Charger la nouvelle scène
                    stage.show();  // Afficher la nouvelle scène

                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @FXML
    void listeReclamation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListReclamations.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) listeRec.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void initialize() {
        try {
            utilisateur = getCurrentUtilisateur(); // Récupérer l'utilisateur connecté

            if (utilisateur != null) {
                nameField.setText(utilisateur.getName());
                lastnameField.setText(utilisateur.getLast_name());
                emailField.setText(utilisateur.getEmail());
                passwordField.setText(utilisateur.getPassword());
                phoneField.setText(String.valueOf(utilisateur.getPhone_num()));
                addressField.setText(utilisateur.getAddress());
            } else {
                System.out.println("Utilisateur non trouvé.");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement de l'utilisateur : " + e.getMessage());
        }
    }

    @FXML
    void deconnexion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Connecter.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void switchToAccueil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Accueil.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void togglePasswordVisibility(MouseEvent event) {
    }

}

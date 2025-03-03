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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import tn.esprit.entities.Reclamation;
import tn.esprit.entities.SessionManager;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.ServiceUtilisateur;
import tn.esprit.services.ServiceReclamation;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ProfileUtilisateurController {

    @FXML
    private ComboBox<String> countryComboBox;

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
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmedPasswordField;

    @FXML
    private TextField phoneField;

    @FXML
    private Label passwordError;

    @FXML
    private Label confirmedPasswordError;

    @FXML
    private Hyperlink deleteProfileLink;

    @FXML
    private ImageView profileImageView;

    @FXML
    private Button saveButton;

    @FXML
    private Hyperlink listeRec;

    @FXML
    private Button changerPhoto;

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
                SessionManager session = SessionManager.getInstance();
                Utilisateur currentUtilisateur = session.getCurrentUtilisateur();
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
        // Récupérer l'utilisateur connecté
        SessionManager session = SessionManager.getInstance();
        Utilisateur currentUser = session.getCurrentUtilisateur();
        int user_id = currentUser.getUser_id();

        // Masquer les erreurs et réinitialiser les styles au début
        passwordError.setVisible(false);
        confirmedPasswordError.setVisible(false);
        passwordField.getStyleClass().remove("text-field-error");
        confirmedPasswordField.getStyleClass().remove("text-field-error");

        // Récupérer les valeurs des champs (laisser ceux qui ne sont pas modifiés vides)
        String name = nameField.getText().isEmpty() ? currentUser.getName() : nameField.getText();
        String last_name = lastnameField.getText().isEmpty() ? currentUser.getLast_name() : lastnameField.getText();
        String email = emailField.getText().isEmpty() ? currentUser.getEmail() : emailField.getText();
        String password = passwordField.getText();
        String confirmedPassword = confirmedPasswordField.getText();
        String phone = phoneField.getText().isEmpty() ? String.valueOf(currentUser.getPhone_num()) : phoneField.getText();
        String address = countryComboBox.getValue().isEmpty() ? currentUser.getAddress() : countryComboBox.getValue();

        boolean hasError = false;

        // Vérification des champs de mot de passe et de confirmation
        if (!password.isEmpty() || !confirmedPassword.isEmpty()) {
            if (password.isEmpty()) {
                passwordError.setText("Mot de passe requis.");
                passwordError.setVisible(true);
                passwordField.getStyleClass().add("text-field-error");
                hasError = true;
            }
            if (confirmedPassword.isEmpty()) {
                confirmedPasswordError.setText("Confirmation du mot de passe requise.");
                confirmedPasswordError.setVisible(true);
                confirmedPasswordField.getStyleClass().add("text-field-error");
                hasError = true;
            }
            if (!password.equals(confirmedPassword)) {
                passwordError.setText("Les mots de passe ne correspondent pas !");
                passwordError.setVisible(true);
                passwordField.getStyleClass().add("text-field-error");
                confirmedPasswordField.getStyleClass().add("text-field-error");
                hasError = true;
            }
        }

        // Créer un objet utilisateur avec les nouvelles valeurs
        Utilisateur utilisateur = new Utilisateur(user_id, name, last_name, email, password, Integer.parseInt(phone), address);

        // Si des erreurs existent, ne pas envoyer les données
        if (hasError) {
            return; // Ne pas continuer l'exécution si des erreurs sont présentes
        }

        // Mettre à jour l'utilisateur dans la base de données
        try {
            serviceUtilisateur.update(utilisateur);

            // Mettre à jour les informations dans l'interface
            currentUser.setName(name);
            currentUser.setLast_name(last_name);
            currentUser.setPhone_num(Integer.parseInt(phone));
            currentUser.setAddress(address);

            // Mettre à jour les champs affichés
            nameField.setText(name);
            lastnameField.setText(last_name);
            phoneField.setText(phone);
            countryComboBox.setValue(address);

            // Redirection selon le cas
            if (!password.isEmpty()) {
                redirectTo("Connecter.fxml"); // Redirection vers connexion si le mot de passe a changé
            } else {
                redirectTo("ProfileUtilisateur.fxml"); // Rester sur la page profil sinon
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour changer de scène
    private void redirectTo(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
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
        SessionManager session = SessionManager.getInstance();
        int user_id = session.getCurrentUtilisateur().getUser_id();  // Méthode pour récupérer l'ID utilisateur

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
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListReclamations.fxml"));
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
            loadCountries(); // Charger la liste des pays
            loadUserProfile();
            SessionManager session = SessionManager.getInstance();
            utilisateur = session.getCurrentUtilisateur(); // Récupérer l'utilisateur connecté

            if (utilisateur != null) {
                nameField.setText(utilisateur.getName());
                lastnameField.setText(utilisateur.getLast_name());
                emailField.setText(utilisateur.getEmail());
                phoneField.setText(String.valueOf(utilisateur.getPhone_num()));
                countryComboBox.setValue(utilisateur.getAddress());

                // Récupérer et afficher la photo de profil
                byte[] photoData = utilisateur.getPhoto();
                if (photoData != null && photoData.length > 0) {
                    // Convertir le tableau d'octets en Image
                    Image profileImage = new Image(new ByteArrayInputStream(photoData));
                    profileImageView.setImage(profileImage);
                } else {
                    // Charger l’image par défaut depuis les ressources
                    Image defaultImage = new Image(getClass().getResource("/images/default_photo.png").toExternalForm());
                    profileImageView.setImage(defaultImage);
                }
            } else {
                System.out.println("Utilisateur non trouvé.");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement de l'utilisateur : " + e.getMessage());
        }
    }


    private void loadCountries() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("https://restcountries.com/v3.1/all");

            // Exécuter la requête
            CloseableHttpResponse response = client.execute(request);

            // Vérifier le code de statut de la réponse (HttpResponse.getCode())
            if (response.getCode() == 200) {
                // Si la requête réussie (200 OK), parser la réponse
                String jsonResponse = null;
                try {
                    jsonResponse = EntityUtils.toString(response.getEntity());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                // Traiter les données JSON
                JSONArray countriesArray = new JSONArray(jsonResponse);
                List<String> countryNames = new ArrayList<>();

                for (int i = 0; i < countriesArray.length(); i++) {
                    JSONObject countryObject = countriesArray.getJSONObject(i);
                    JSONObject nameObject = countryObject.getJSONObject("name");
                    String countryName = nameObject.getString("common"); // Nom du pays
                    countryNames.add(countryName);
                }

                // Trier la liste des pays par ordre alphabétique
                countryNames.sort(String::compareTo);

                // Ajouter les pays à ton ComboBox
                countryComboBox.getItems().addAll(countryNames);
            } else {
                // Gérer une erreur si le statut n'est pas OK
                System.out.println("Erreur lors de la récupération des pays. Code d'erreur: " + response.getCode());
            }
        } catch (IOException e) {
            // Gestion des exceptions d'IO
            e.printStackTrace();
        }
    }


    @FXML
    void changerPhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        SessionManager session = SessionManager.getInstance();
        Utilisateur currentUtilisateur = session.getCurrentUtilisateur();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                // Lire le fichier et le convertir en byte[]
                byte[] photoData = Files.readAllBytes(selectedFile.toPath());

                // Afficher l'image à partir du chemin local (facultatif, juste pour la prévisualisation)
                Image newImage = new Image(new FileInputStream(selectedFile));
                profileImageView.setImage(newImage);  // Afficher l'image à partir du chemin local

                // Mettre à jour la photo dans la base de données
                ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
                serviceUtilisateur.updatePhoto(currentUtilisateur.getUser_id(), photoData);  // Mettre à jour la BDD avec le tableau d'octets

                // Mettre à jour l'image dans l'objet utilisateur
                currentUtilisateur.setPhoto(photoData);  // Mettez à jour l'image dans l'objet utilisateur

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Photo de profil mise à jour avec succès !");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de lire le fichier image.");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de mettre à jour la photo.");
            }
        }
    }

    private void loadUserProfile() {
        // Vérifier si un utilisateur est connecté
        SessionManager session = SessionManager.getInstance();
        Utilisateur user = session.getCurrentUtilisateur();

        if (user != null) {
            // Vérifier si la photo n'est pas nulle ou vide
            if (user.getPhoto() != null && user.getPhoto().length > 0) {
                try {
                    // Créer un flux d'entrée à partir du tableau d'octets pour charger l'image
                    Image userImage = new Image(new ByteArrayInputStream(user.getPhoto()));
                    profileImageView.setImage(userImage);
                } catch (Exception e) {
                    System.out.println("Erreur lors du chargement de l'image de profil : " + e.getMessage());
                }
            } else {
                // Si aucune photo n'est enregistrée, utiliser une image par défaut
                // Définir une URL par défaut pour l'image si vous n'avez pas DEFAULT_PHOTO_URL
                String defaultPhotoUrl = "https://cdn-icons-png.flaticon.com/512/9187/9187604.png"; // Exemple d'URL par défaut
                profileImageView.setImage(new Image(defaultPhotoUrl));
            }
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

    public void SwitchToPosts(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Posts.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void SwitchToVoyages(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Voyages.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void SwitchToHotels(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Hotel.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SwitchToExcursion(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListExcursionGuide.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

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
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.ServiceUtilisateur;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InscrireController {

    @FXML
    private ComboBox<String> countryComboBox;

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
    private PasswordField confirmedPasswordField;

    @FXML
    private TextField phoneField;

    @FXML
    private Label nameError;

    @FXML
    private Label lastNameError;

    @FXML
    private Label emailError;

    @FXML
    private Label passwordError;

    @FXML
    private Label confirmedPasswordError;

    @FXML
    private Label phoneError;

    @FXML
    private Label addressError;

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
        String confirmedPassword = confirmedPasswordField.getText();
        String phone = phoneField.getText();
        String address = countryComboBox.getValue();

        // Masquer les erreurs au début
        nameError.setVisible(false);
        lastNameError.setVisible(false);
        emailError.setVisible(false);
        passwordError.setVisible(false);
        confirmedPasswordError.setVisible(false);
        phoneError.setVisible(false);
        addressError.setVisible(false);

        // Réinitialiser les bordures
        nameField.getStyleClass().remove("text-field-error");
        lastnameField.getStyleClass().remove("text-field-error");
        emailField.getStyleClass().remove("text-field-error");
        passwordField.getStyleClass().remove("text-field-error");
        confirmedPasswordField.getStyleClass().remove("text-field-error");
        phoneField.getStyleClass().remove("text-field-error");
        countryComboBox.getStyleClass().remove("text-field-error");

        // Vérification des champs manquants et ajout de bordures rouges
        boolean hasError = false;

        if (name.isEmpty()) {
            nameError.setText("Nom requis.");
            nameError.setVisible(true);
            nameField.getStyleClass().add("text-field-error");
            hasError = true;
        }
        if (last_name.isEmpty()) {
            lastNameError.setText("Prénom requis.");
            lastNameError.setVisible(true);
            lastnameField.getStyleClass().add("text-field-error");
            hasError = true;
        }
        if (email.isEmpty()) {
            emailError.setText("Email requis.");
            emailError.setVisible(true);
            emailField.getStyleClass().add("text-field-error");
            hasError = true;
        }
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
        if (phone.isEmpty()) {
            phoneError.setText("Numéro de téléphone requis.");
            phoneError.setVisible(true);
            phoneField.getStyleClass().add("text-field-error");
            hasError = true;
        }
        if (address.isEmpty()) {
            addressError.setText("Adresse requise.");
            addressError.setVisible(true);
            countryComboBox.getStyleClass().add("text-field-error");
            hasError = true;
        }

        // Si des erreurs existent, on arrête le processus et ne tente pas d'ajouter l'utilisateur
        if (hasError) {
            return;
        }

        // Vérification si l'email existe déjà dans la base de données
        ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
        try {
            if (serviceUtilisateur.emailExists(email)) {
                emailError.setText("L'email existe déjà.");
                emailError.setVisible(true);
                emailField.getStyleClass().add("text-field-error");
                return;  // Arrêter le processus si l'email existe
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la vérification de l'email.");
            return;
        }

        // Vérifications simultanées de l'email et du mot de passe
        boolean validEmail = email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
        boolean validPassword = password.length() >= 4 && password.matches(".*\\d.*") && password.matches(".*[!@#$%^&*].*");

        if (!validEmail || !validPassword) {
            if (!validEmail) {
                emailError.setText("Format d'email invalide.");
                emailError.setVisible(true);
                emailField.getStyleClass().add("text-field-error");
            }
            if (!validPassword) {
                passwordError.setText("Le mot de passe doit contenir au moins 4 caractères...");
                passwordError.setVisible(true);
                passwordField.getStyleClass().add("text-field-error");
            }
            return;
        }

        // Vérification si les mots de passe correspondent
        if (!password.equals(confirmedPassword)) {
            passwordError.setText("Les mots de passe ne correspondent pas.");
            passwordError.setVisible(true);
            passwordField.getStyleClass().add("text-field-error");
            confirmedPasswordField.getStyleClass().add("text-field-error");
            return;
        }

        try {
            // Convertir les valeurs numériques
            int phoneNum;
            try {
                phoneNum = Integer.parseInt(phone);
            } catch (NumberFormatException e) {
                phoneError.setText("Numéro de téléphone invalide.");
                phoneError.setVisible(true);
                phoneField.getStyleClass().add("text-field-error");
                return;
            }

            // Créer un objet Utilisateur avec une photo par défaut
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setName(name);
            utilisateur.setLast_name(last_name);
            utilisateur.setEmail(email);
            utilisateur.setPassword(password);
            utilisateur.setPhone_num(phoneNum);
            utilisateur.setAddress(address);

            // Ajouter un chemin de photo par défaut
            try {
                // Charger l'image par défaut en tant que tableau d'octets
                byte[] defaultPhotoPath = Files.readAllBytes(Paths.get("/images/default_photo.png"));
                utilisateur.setPhoto(defaultPhotoPath);
            } catch (IOException e) {
                System.out.println("Erreur lors du chargement de l'image par défaut : " + e.getMessage());
            }

            // Ajouter l'utilisateur à la base de données
            serviceUtilisateur.add(utilisateur);
            System.out.println("Utilisateur ajouté avec succès !");

            // Rediriger vers la page Connecter.fxml après une inscription réussie
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Connecter.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue. Veuillez réessayer plus tard.");
        }
    }

    @FXML
    public void initialize() {
        loadCountries(); // Charger la liste des pays
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


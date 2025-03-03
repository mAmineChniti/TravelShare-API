package tn.esprit.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Guides;
import tn.esprit.services.ServiceGuide;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

public class AddGuideController {

    @FXML
    private ComboBox<String> languageCB, experienceTF;  // Ajout du ComboBox pour l'expérience
    @FXML
    private TextField emailTF, lastnameTF, nameTF, phoneTF;  // Le champ experienceTF est supprimé
    @FXML
    private Label nameError, lastnameError, emailError, phoneError, languageError, experienceError;

    @FXML
    public void initialize() {
        fetchLanguages();
        populateExperienceComboBox();  // Remplir le ComboBox avec les options d'expérience
    }

    // Fonction pour valider l'email
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.matches(emailRegex, email);
    }

    // Fonction pour ajouter un guide
    @FXML
    void addGuide(ActionEvent event) {
        clearErrors();

        String name = nameTF.getText().trim();
        String lastname = lastnameTF.getText().trim();
        String email = emailTF.getText().trim();
        String phone = phoneTF.getText().trim();
        String language = languageCB.getValue();  // Récupérer la valeur sélectionnée pour la langue
        String experienceText = experienceTF.getValue();  // Récupérer la valeur sélectionnée pour l'expérience

        boolean hasError = false;

        if (name.isEmpty()) { nameError.setText("Please enter a valid name"); hasError = true; }
        if (lastname.isEmpty()) { lastnameError.setText("Please enter a valid lastname"); hasError = true; }
        if (email.isEmpty()) { emailError.setText("Please enter a valid email"); hasError = true; }
        else if (!isValidEmail(email)) { emailError.setText("Invalid email"); hasError = true; }
        if (phone.isEmpty()) { phoneError.setText("Please enter a valid phone number"); hasError = true; }
        if (language == null || language.isEmpty()) { languageError.setText("Please select a language"); hasError = true; }

        int experience = 0;
        if (experienceText == null || experienceText.isEmpty()) { experienceError.setText("Please select an experience level"); hasError = true; }
        else {
            try {
                // Extraire la valeur numérique (par exemple, 2 à partir de "+2")
                experience = Integer.parseInt(experienceText.substring(1));  // Retirer le "+" et convertir en entier
            } catch (NumberFormatException e) {
                experienceError.setText("Invalid experience value");
                hasError = true;
            }
        }

        if (hasError) return;

        Guides guide = new Guides(experience, name, lastname, email, phone, language);
        ServiceGuide serviceGuide = new ServiceGuide();
        try {
            serviceGuide.add(guide);
            showAlert("Success", "Guide added successfully", Alert.AlertType.INFORMATION);
            clearFields();
        } catch (Exception e) {
            showAlert("Error", "Error while adding: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }



    private void fetchLanguages() {
        new Thread(() -> {
            String apiUrl = "https://restcountries.com/v3.1/all";
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONArray countries = new JSONArray(response.toString());
                Platform.runLater(() -> {
                    for (int i = 0; i < countries.length(); i++) {
                        JSONObject country = countries.getJSONObject(i);
                        if (country.has("languages")) {
                            JSONObject languages = country.getJSONObject("languages");
                            for (String key : languages.keySet()) {
                                String language = languages.getString(key);
                                if (!languageCB.getItems().contains(language)) {
                                    languageCB.getItems().add(language);
                                }
                            }
                        }
                    }
                });
            } catch (Exception e) {
                System.out.println("Error fetching languages: " + e.getMessage());
            }
        }).start();
    }


    // Remplir le ComboBox de l'expérience avec les options prédéfinies
    private void populateExperienceComboBox() {
        experienceTF.getItems().addAll("+2", "+5", "+10", "+15", "+20");
    }

    // Fonction pour afficher une alerte
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Fonction pour réinitialiser les messages d'erreur
    private void clearErrors() {
        nameError.setText("");
        lastnameError.setText("");
        emailError.setText("");
        phoneError.setText("");
        languageError.setText("");
        experienceError.setText("");
    }

    // Fonction pour réinitialiser les champs du formulaire
    private void clearFields() {
        nameTF.clear();
        lastnameTF.clear();
        emailTF.clear();
        phoneTF.clear();
        experienceTF.setValue(null);
        languageCB.setValue(null);
    }

    // Fonction pour revenir à la page précédente
    @FXML
    void goBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ListGuide.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Page loading error ListGuide.fxml: " + e.getMessage());
        }
    }
}

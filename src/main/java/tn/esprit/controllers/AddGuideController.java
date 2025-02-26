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
    private ComboBox<String> languageCB;
    @FXML
    private TextField emailTF, experienceTF, lastnameTF, nameTF, phoneTF;
    @FXML
    private Label nameError, lastnameError, emailError, phoneError, languageError, experienceError;

    @FXML
    public void initialize() {
        fetchLanguages();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.matches(emailRegex, email);
    }

    @FXML
    void addGuide(ActionEvent event) {
        clearErrors();

        String name = nameTF.getText().trim();
        String lastname = lastnameTF.getText().trim();
        String email = emailTF.getText().trim();
        String phone = phoneTF.getText().trim();
        String language = languageCB.getValue(); // Récupérer la valeur sélectionnée
        String experienceText = experienceTF.getText().trim();

        boolean hasError = false;

        if (name.isEmpty()) { nameError.setText("Please enter a valid name"); hasError = true; }
        if (lastname.isEmpty()) { lastnameError.setText("Please enter a valid lastname"); hasError = true; }
        if (email.isEmpty()) { emailError.setText("Please enter a valid email"); hasError = true; }
        else if (!isValidEmail(email)) { emailError.setText("Invalid email"); hasError = true; }
        if (phone.isEmpty()) { phoneError.setText("Please enter a valid phone number"); hasError = true; }
        if (language == null || language.isEmpty()) { languageError.setText("Please select a language"); hasError = true; }

        int experience = 0;
        if (experienceText.isEmpty()) { experienceError.setText("Please enter a valid experience"); hasError = true; }
        else {
            try { experience = Integer.parseInt(experienceText); }
            catch (NumberFormatException e) { experienceError.setText("Must be a number"); hasError = true; }
        }

        if (hasError) return;

        Guides guide = new Guides(experience, name, lastname, email, phone, language);
        ServiceGuide serviceGuide = new ServiceGuide();
        try {
            serviceGuide.add(guide);
            showAlert("Succes", "Guide added successfully", Alert.AlertType.INFORMATION);
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

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearErrors() {
        nameError.setText("");
        lastnameError.setText("");
        emailError.setText("");
        phoneError.setText("");
        languageError.setText("");
        experienceError.setText("");
    }

    private void clearFields() {
        nameTF.clear();
        lastnameTF.clear();
        emailTF.clear();
        phoneTF.clear();
        experienceTF.clear();
        languageCB.setValue(null);
    }

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
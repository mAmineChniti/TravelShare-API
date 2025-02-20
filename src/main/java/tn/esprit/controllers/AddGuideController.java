package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entities.Guides;
import tn.esprit.services.ServiceGuide;

import java.io.IOException;

public class AddGuideController {

    @FXML
    private TextField emailTF;

    @FXML
    private TextField experienceTF;

    @FXML
    private TextField languageTF;

    @FXML
    private TextField lastnameTF;

    @FXML
    private TextField nameTF;

    @FXML
    private TextField phoneTF;

    @FXML
    void addGuide(ActionEvent event) {
        // Récupérer les valeurs des champs du formulaire
        String name = nameTF.getText();
        String lastname = lastnameTF.getText();
        String email = emailTF.getText();
        String phone = phoneTF.getText();
        String language = languageTF.getText();
        int experience;

        // Validation des champs
        if (name.isEmpty() || lastname.isEmpty() || email.isEmpty() || phone.isEmpty() || language.isEmpty() || experienceTF.getText().isEmpty()) {
            showAlert("Error", "Please fill in all fields\n", Alert.AlertType.ERROR);
            return;
        }

        try {
            experience = Integer.parseInt(experienceTF.getText());
        } catch (NumberFormatException e) {
            showAlert("Error", "Experience must be an integer", Alert.AlertType.ERROR);
            return;
        }

        // Créer l'objet Guides avec les valeurs récupérées
        Guides guide = new Guides();
        guide.setName(name);
        guide.setLastname(lastname);
        guide.setEmail(email);
        guide.setPhone_num(phone);
        guide.setLanguage(language);
        guide.setExperience(experience);

        // Ajouter le guide via le service
        ServiceGuide serviceGuide = new ServiceGuide();
        try {
            serviceGuide.add(guide);  // Appel à la méthode add du service
            showAlert("Succès", "Guide added successfully", Alert.AlertType.INFORMATION);
            clearFields();  // Effacer les champs après l'ajout
        } catch (Exception e) {
            showAlert("Error", "Error adding guide : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Méthode pour afficher une alerte
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour vider les champs après ajout
    private void clearFields() {
        nameTF.clear();
        lastnameTF.clear();
        emailTF.clear();
        phoneTF.clear();
        languageTF.clear();
        experienceTF.clear();
    }
    @FXML
    void goBack(ActionEvent event) {
        try {
            // Charger la scène ListGuide.fxml
            Parent root = FXMLLoader.load(getClass().getResource("/ListGuide.fxml"));
            // Créer une nouvelle scène et la mettre sur le stage actuel
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);  // Remplacer la scène actuelle par ListGuide.fxml
            stage.show();  // Afficher la nouvelle scène

        } catch (IOException e) {
            System.out.println("Page loading error ListGuide.fxml : " + e.getMessage());
        }
    }


}

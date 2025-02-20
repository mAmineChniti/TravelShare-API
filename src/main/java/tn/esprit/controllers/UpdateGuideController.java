package tn.esprit.controllers;

import java.io.IOException;
import java.sql.SQLException;
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

public class UpdateGuideController {

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

    // Instance de ServiceGuide pour les opérations sur la base de données
    private ServiceGuide serviceGuide = new ServiceGuide();
    private Guides guideToEdit;

    // Méthode pour recevoir le guide à modifier depuis le HomeController
    public void setGuideToEdit(Guides guide) {
        this.guideToEdit = guide;

        // Pré-remplir les champs de texte avec les données du guide
        nameTF.setText(guide.getName());
        lastnameTF.setText(guide.getLastname());
        emailTF.setText(guide.getEmail());
        experienceTF.setText(String.valueOf(guide.getExperience()));
        phoneTF.setText(guide.getPhone_num());
        languageTF.setText(guide.getLanguage());
    }

    @FXML
    void updateGuide(ActionEvent event) {
        // Vérification des champs
        if (nameTF.getText().isEmpty() || lastnameTF.getText().isEmpty() ||
                emailTF.getText().isEmpty() || experienceTF.getText().isEmpty() ||
                phoneTF.getText().isEmpty() || languageTF.getText().isEmpty()) {

            // Alerte en cas de champ vide
            showAlert(Alert.AlertType.WARNING, "Empty fields", "All fields must be completed.");
            return;
        }

        // Vérification de l'expérience (doit être un entier)
        try {
            Integer.parseInt(experienceTF.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Invalid experience", "Experience must be an integer.");
            return;
        }

        // Mise à jour du guide avec les nouvelles informations
        guideToEdit.setName(nameTF.getText());
        guideToEdit.setLastname(lastnameTF.getText());
        guideToEdit.setEmail(emailTF.getText());
        guideToEdit.setExperience(Integer.parseInt(experienceTF.getText()));
        guideToEdit.setPhone_num(phoneTF.getText());
        guideToEdit.setLanguage(languageTF.getText());

        try {
            serviceGuide.update(guideToEdit);

            // Alerte de succès
            showAlert(Alert.AlertType.INFORMATION, "Succes", "Guide updated successfully !");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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

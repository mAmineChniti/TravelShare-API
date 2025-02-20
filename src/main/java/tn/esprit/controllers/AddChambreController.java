package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entities.Chambres;
import tn.esprit.entities.Hotels;
import tn.esprit.services.ServiceChambre;

import java.sql.SQLException;

public class AddChambreController {

    @FXML
    private TextField numeroField;

    @FXML
    private TextField typeField;

    @FXML
    private TextField prixField;

    @FXML
    private TextField disponibleField;

    private ServiceChambre serviceChambre = new ServiceChambre();
    private Hotels selectedHotel; // L'hôtel auquel la chambre sera ajoutée

    public void setSelectedHotel(Hotels hotel) {
        this.selectedHotel = hotel;
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleAdd() {
        // Retrieve input values from the fields
        String numero = numeroField.getText();
        String type = typeField.getText();
        String prix = prixField.getText();
        String disponible = disponibleField.getText();

        // Validate the inputs (you can add more validation if needed)
        if (numero.isEmpty() || type.isEmpty() || prix.isEmpty() || disponible.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.", Alert.AlertType.ERROR);
            return;
        }

        // Validate if prix is a valid number
        double prixValue;
        try {
            prixValue = Double.parseDouble(prix);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le prix doit être un nombre valide.", Alert.AlertType.ERROR);
            return;
        }

        // Validate if disponible is a valid boolean (true/false)
        boolean disponibleValue;
        if (!disponible.equalsIgnoreCase("true") && !disponible.equalsIgnoreCase("false")) {
            showAlert("Erreur", "La disponibilité doit être 'true' ou 'false'.", Alert.AlertType.ERROR);
            return;
        }
        disponibleValue = Boolean.parseBoolean(disponible);

        // Assuming you save the data here or do something with it
        // Example: Save the data to the database or pass it to a service

        showAlert("Succès", "Chambre ajoutée avec succès.", Alert.AlertType.INFORMATION);

        // Clear fields after adding
        numeroField.clear();
        typeField.clear();
        prixField.clear();
        disponibleField.clear();
    }

    @FXML
    private void handleCancel() {
        // Fermer la fenêtre pop-up sans ajouter
        Stage stage = (Stage) numeroField.getScene().getWindow();
        stage.close();
    }
}
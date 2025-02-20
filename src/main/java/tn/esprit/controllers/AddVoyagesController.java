package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entities.OffreVoyages;
import tn.esprit.services.OffreVoyageService;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class AddVoyagesController {

    @FXML
    private TextField titreField;

    @FXML
    private TextField destinationField;

    @FXML
    private TextField descriptionField;

    @FXML
    private DatePicker dateDepartPicker;

    @FXML
    private DatePicker dateRetourPicker;

    @FXML
    private TextField prixField;

    @FXML
    private TextField placesField;

    @FXML
    private Button submitButton;

    private final OffreVoyageService offreVoyageService = new OffreVoyageService();

    @FXML
    void submitVoyage(ActionEvent event) {
        if (!validateInputs()) {
            return; // Stop submission if validation fails
        }

        try {
            String titre = titreField.getText();
            String destination = destinationField.getText();
            String description = descriptionField.getText();
            Date dateDepart = Date.valueOf(dateDepartPicker.getValue());
            Date dateRetour = Date.valueOf(dateRetourPicker.getValue());
            double prix = Double.parseDouble(prixField.getText());
            int places = Integer.parseInt(placesField.getText());

            OffreVoyages newVoyage = new OffreVoyages();
            newVoyage.setTitre(titre);
            newVoyage.setDestination(destination);
            newVoyage.setDescription(description);
            newVoyage.setDate_depart(dateDepart);
            newVoyage.setDate_retour(dateRetour);
            newVoyage.setPrix(prix);
            newVoyage.setPlaces_disponibles(places);

            offreVoyageService.add(newVoyage);

            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Voyage Added");
            alert.setContentText("The new voyage has been successfully added.");
            alert.showAndWait();

            // Navigate back to the main Voyages page
            switchToVoyages(event);
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Failed to Add Voyage", "An error occurred while adding the voyage: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        // Validate title field
        if (titreField.getText().trim().isEmpty()) {
            showErrorAlert("Validation Error", "The title field cannot be empty.");
            return false;
        }

        // Validate destination field
        if (destinationField.getText().trim().isEmpty()) {
            showErrorAlert("Validation Error", "The destination field cannot be empty.");
            return false;
        }

        // Validate description field
        if (descriptionField.getText().trim().isEmpty()) {
            showErrorAlert("Validation Error", "The description field cannot be empty.");
            return false;
        }

        // Validate departure date
        if (dateDepartPicker.getValue() == null) {
            showErrorAlert("Validation Error", "The departure date must be selected.");
            return false;
        }

        // Validate return date
        if (dateRetourPicker.getValue() == null) {
            showErrorAlert("Validation Error", "The return date must be selected.");
            return false;
        }

        if (!dateDepartPicker.getValue().isBefore(dateRetourPicker.getValue())) {
            showErrorAlert("Validation Error", "The return date must be after the departure date.");
            return false;
        }

        // Validate price field
        if (prixField.getText().trim().isEmpty()) {
            showErrorAlert("Validation Error", "The price field cannot be empty.");
            return false;
        }
        try {
            double prix = Double.parseDouble(prixField.getText().trim());
            if (prix <= 0) {
                showErrorAlert("Validation Error", "The price must be a positive number.");
                return false;
            }
        } catch (NumberFormatException e) {
            showErrorAlert("Validation Error", "The price must be a valid number.");
            return false;
        }

        // Validate places field
        if (placesField.getText().trim().isEmpty()) {
            showErrorAlert("Validation Error", "The number of available seats cannot be empty.");
            return false;
        }
        try {
            int places = Integer.parseInt(placesField.getText().trim());
            if (places <= 0) {
                showErrorAlert("Validation Error", "The number of available seats must be a positive integer.");
                return false;
            }
        } catch (NumberFormatException e) {
            showErrorAlert("Validation Error", "The number of available seats must be a valid integer.");
            return false;
        }

        return true; // All validations passed
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void switchToVoyages(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Voyages.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

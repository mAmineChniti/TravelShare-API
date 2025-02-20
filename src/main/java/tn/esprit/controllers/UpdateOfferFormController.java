package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entities.OffreVoyages;
import tn.esprit.services.OffreVoyageService;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class UpdateOfferFormController {

    @FXML
    private TextField titleField;
    @FXML
    private TextField destinationField;
    @FXML
    private TextField descriptionField;
    @FXML
    private DatePicker departureDatePicker;
    @FXML
    private DatePicker returnDatePicker;
    @FXML
    private TextField priceField;
    @FXML
    private TextField availableSeatsField;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private final OffreVoyageService offreVoyageService = new OffreVoyageService();
    private OffreVoyages currentOffer;
    private VoyagesController voyagesController;

    public void setVoyagesController(VoyagesController voyagesController) {
        this.voyagesController = voyagesController;
    }

    public void setOfferDetails(OffreVoyages offer) {
        this.currentOffer = offer;

        // Pre-fill form fields with offer details
        titleField.setText(offer.getTitre());
        destinationField.setText(offer.getDestination());
        descriptionField.setText(offer.getDescription());
        departureDatePicker.setValue(offer.getDate_depart().toLocalDate());
        returnDatePicker.setValue(offer.getDate_retour().toLocalDate());
        priceField.setText(String.valueOf(offer.getPrix()));
        availableSeatsField.setText(String.valueOf(offer.getPlaces_disponibles()));
    }

    @FXML
    private void saveOffer(ActionEvent event) {
        // Validate input fields
        if (!validateInputs()) {
            return;
        }

        try {
            // Update the current offer with new details
            currentOffer.setTitre(titleField.getText());
            currentOffer.setDestination(destinationField.getText());
            currentOffer.setDescription(descriptionField.getText());
            currentOffer.setDate_depart(Date.valueOf(departureDatePicker.getValue()));
            currentOffer.setDate_retour(Date.valueOf(returnDatePicker.getValue()));
            currentOffer.setPrix(Double.parseDouble(priceField.getText()));
            currentOffer.setPlaces_disponibles(Integer.parseInt(availableSeatsField.getText()));

            // Save updated offer to the database
            offreVoyageService.update(currentOffer);
            showAlert("Success", "Offer updated successfully!");

            // Refresh the grid in VoyagesController by calling displayOffers()
            if (voyagesController != null) {
                voyagesController.displayOffers();
            }

            // Close the form
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            showAlert("Error", "Failed to update offer: " + e.getMessage());
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        // Close the form
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private boolean validateInputs() {
        // Title validation
        if (titleField.getText().isEmpty()) {
            showAlert("Validation Error", "The title field cannot be empty.");
            return false;
        }

        // Destination validation
        if (destinationField.getText().isEmpty()) {
            showAlert("Validation Error", "The destination field cannot be empty.");
            return false;
        }

        // Description validation
        if (descriptionField.getText().isEmpty()) {
            showAlert("Validation Error", "The description field cannot be empty.");
            return false;
        }

        // Departure date validation
        if (departureDatePicker.getValue() == null) {
            showAlert("Validation Error", "The departure date must be selected.");
            return false;
        }

        // Return date validation
        if (returnDatePicker.getValue() == null) {
            showAlert("Validation Error", "The return date must be selected.");
            return false;
        }

        if (!departureDatePicker.getValue().isBefore(returnDatePicker.getValue())) {
            showAlert("Validation Error", "The return date must be after the departure date.");
            return false;
        }

        // Price validation
        if (priceField.getText().isEmpty()) {
            showAlert("Validation Error", "The price field cannot be empty.");
            return false;
        }
        try {
            double price = Double.parseDouble(priceField.getText());
            if (price <= 0) {
                showAlert("Validation Error", "The price must be a positive value.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "The price must be a valid number.");
            return false;
        }

        // Available seats validation
        if (availableSeatsField.getText().isEmpty()) {
            showAlert("Validation Error", "The available seats field cannot be empty.");
            return false;
        }
        try {
            int seats = Integer.parseInt(availableSeatsField.getText());
            if (seats <= 0) {
                showAlert("Validation Error", "The available seats must be a positive integer.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "The available seats must be a valid integer.");
            return false;
        }

        return true;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

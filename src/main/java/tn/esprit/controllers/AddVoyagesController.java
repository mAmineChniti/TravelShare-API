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
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Add Voyage");
            alert.setContentText("An error occurred while adding the voyage. Please check your input.");
            alert.showAndWait();
        }
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
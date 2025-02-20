package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import tn.esprit.entities.OffreVoyages;

import java.io.IOException;

public class OfferDetailsController {

    @FXML
    private Label titleLabel;

    @FXML
    private Label destinationLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label departureDateLabel;

    @FXML
    private Label returnDateLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label availableSeatsLabel;

    @FXML
    private Button reserveButton;

    private OffreVoyages currentOffer;

    public void SwitchToPackages(ActionEvent actionEvent) {
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

    public void setOfferDetails(OffreVoyages offer) {
        this.currentOffer = offer;

        titleLabel.setText(offer.getTitre());
        destinationLabel.setText(offer.getDestination());
        descriptionLabel.setText(offer.getDescription());
        departureDateLabel.setText("Departure: " + offer.getDate_depart());
        returnDateLabel.setText("Return: " + offer.getDate_retour());
        priceLabel.setText("Price: " + offer.getPrix() + " $");
        availableSeatsLabel.setText("Seats: " + offer.getPlaces_disponibles());
    }

    @FXML
    public void reserveOffer() {
        // Logic to reserve the offer (e.g., database update)
        System.out.println("Reserving offer: " + currentOffer.getTitre());
    }
}


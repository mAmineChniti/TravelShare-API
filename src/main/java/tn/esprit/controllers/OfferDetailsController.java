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
import tn.esprit.entities.OffreReservations;
import tn.esprit.entities.OffreVoyages;
import tn.esprit.entities.SessionManager;
import tn.esprit.services.OffreReservationService;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;

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
        if (currentOffer != null) {
            OffreReservationService resOffre = new OffreReservationService();
            OffreReservations reservation = new OffreReservations();
            try {
                reservation.setOffre_id(currentOffer.getOffres_voyage_id());
                reservation.setClient_id(SessionManager.getCurrentUtilisateur().getUser_id());
                reservation.setDate_reserved(new Date(new java.util.Date().getTime()));
                reservation.setReserved(true);
                resOffre.add(reservation);
            } catch (SQLException e){
                e.printStackTrace();
            }
        } else {
            System.out.println("Offre null");
        }
    }
}


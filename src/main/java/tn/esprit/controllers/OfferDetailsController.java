package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    private Label titleLabel, destinationLabel, descriptionLabel, departureDateLabel,
            returnDateLabel, priceLabel, availableSeatsLabel, reservationStatusLabel;

    @FXML
    private Button reserveButton;

    @FXML
    private Spinner<Integer> placesSpinner;

    private OffreVoyages currentOffer;
    @FXML
    public void SwitchToAccueil(ActionEvent actionEvent) {
        try {
            String AccueilLink = SessionManager.getInstance().getCurrentUtilisateur().getRole() == 1 ? "/AccueilAdmin.fxml" : "/Accueil.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AccueilLink));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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
    @FXML
    void SwitchToHotels(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Hotel.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SwitchToPosts(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Posts.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void switchToProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfileUtilisateur.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void deconnexion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Connecter.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        System.out.println("Initializing OfferDetailsController...");
        System.out.println("placesSpinner: " + placesSpinner); // Debugging

        if (placesSpinner == null) {
            System.out.println("placesSpinner is null! Check FXML fx:id.");
        } else {
            SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1, 1);
            placesSpinner.setValueFactory(valueFactory);
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

        // Update spinner max value based on available places
        placesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, offer.getPlaces_disponibles(), 1));
    }

    @FXML
    public void reserveOffer() {
        System.out.println("Reserve button clicked!");
        System.out.println("Current offer: " + currentOffer);
        System.out.println("placesSpinner: " + placesSpinner);

        if (placesSpinner == null) {
            reservationStatusLabel.setText("Error: Spinner not initialized.");
            reservationStatusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        int nbr_places = placesSpinner.getValue(); // Possible issue here!
        System.out.println("Selected places: " + nbr_places);

        if (currentOffer == null) {
            reservationStatusLabel.setText("Offer not found.");
            reservationStatusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (nbr_places > currentOffer.getPlaces_disponibles() || nbr_places < 1) {
            reservationStatusLabel.setText("Invalid number of places.");
            reservationStatusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            OffreReservationService resOffre = new OffreReservationService();
            OffreReservations reservation = new OffreReservations();

            reservation.setOffre_id(currentOffer.getOffres_voyage_id());
            reservation.setClient_id(SessionManager.getInstance().getCurrentUtilisateur().getUser_id());
            reservation.setDate_reserved(new Date(new java.util.Date().getTime()));
            reservation.setReserved(true);
            reservation.setNbr_place(nbr_places);
            reservation.setPrix(nbr_places * currentOffer.getPrix());

            resOffre.add(reservation);

            // Update available places
            currentOffer.setPlaces_disponibles(currentOffer.getPlaces_disponibles() - nbr_places);
            availableSeatsLabel.setText("Seats: " + currentOffer.getPlaces_disponibles());

            placesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                    1, currentOffer.getPlaces_disponibles(), 1));

            reservationStatusLabel.setText("Reservation successful!");
            reservationStatusLabel.setStyle("-fx-text-fill: green;");
        } catch (SQLException e) {
            e.printStackTrace();
            reservationStatusLabel.setText("Reservation failed. Try again.");
            reservationStatusLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.OffreVoyages;
import tn.esprit.services.OffreVoyageService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class VoyagesController {

    @FXML
    private GridPane gridPane;

    private final OffreVoyageService offreVoyageService = new OffreVoyageService();

    @FXML
    public void initialize() {
        try {
            displayOffers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
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
    public void SwitchToAccueil(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Accueil.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayOffers() throws SQLException {
        List<OffreVoyages> offers = offreVoyageService.ListAll();
        int column = 0;
        int row = 0;

        for (OffreVoyages offer : offers) {
            VBox card = createOfferCard(offer);

            // Add click event to navigate to the details page
            card.setOnMouseClicked(event -> {
                try {
                    goToOfferDetails(event, offer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            if (column == 3) { // Adjust number of columns as needed
                column = 0;
                row++;
            }

            gridPane.add(card, column++, row);
        }
    }

    private void goToOfferDetails(javafx.scene.input.MouseEvent event, OffreVoyages offer) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/OfferDetails.fxml"));
        Parent root = loader.load();

        // Pass the selected offer to the details controller
        OfferDetailsController detailsController = loader.getController();
        detailsController.setOfferDetails(offer);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private VBox createOfferCard(OffreVoyages offer) {
        VBox card = new VBox();
        card.setPadding(new Insets(10));
        card.setSpacing(8);
        card.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5; -fx-background-color: white; -fx-background-radius: 5;");
        card.setPrefWidth(250);

        Label title = new Label("Title: " + offer.getTitre());
        Label destination = new Label("Destination: " + offer.getDestination());
        Label description = new Label("Description: " + offer.getDescription());
        Label departureDate = new Label("Departure: " + offer.getDate_depart());
        Label returnDate = new Label("Return: " + offer.getDate_retour());
        Label price = new Label("Price: " + offer.getPrix() + " $");
        Label availableSeats = new Label("Seats: " + offer.getPlaces_disponibles());

        card.getChildren().addAll(title, destination, description, departureDate, returnDate, price, availableSeats);
        return card;
    }
}
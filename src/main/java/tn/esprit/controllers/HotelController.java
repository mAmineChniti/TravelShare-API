package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tn.esprit.services.ServiceHotels;
import tn.esprit.entities.Hotels;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class HotelController {

    @FXML
    private VBox hotelContainer;

    private final ServiceHotels serviceHotels = new ServiceHotels();

    @FXML
    public void initialize() {
        loadHotels();
    }

    private void loadHotels() {
        try {
            List<Hotels> hotels = serviceHotels.ListAll();
            hotelContainer.getChildren().clear();
            hotelContainer.setSpacing(15);
            hotelContainer.setStyle("-fx-padding: 20;");

            for (Hotels hotel : hotels) {
                // Create hotel card container
                VBox card = new VBox();
                card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 15;");
                card.setSpacing(8);

                // Hotel name with stars
                Label nameLabel = new Label(hotel.getNom() + " ★★★★");
                nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                // Address
                Label addressLabel = new Label("Adresse: " + hotel.getAdress());
                addressLabel.setStyle("-fx-text-fill: #666666;");

                // Telephone
                Label phoneLabel = new Label("Téléphone: " + hotel.getTelephone());
                phoneLabel.setStyle("-fx-text-fill: #666666;");

                // Capacity
                Label capacityLabel = new Label("Capacité totale: " + hotel.getCapacite_totale() + " personnes");
                capacityLabel.setStyle("-fx-text-fill: #666666;");

                // Availability button
                Button availabilityButton = new Button("Voir disponibilité");
                availabilityButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
                availabilityButton.setOnAction(event -> navigateToChambre(hotel));

                card.getChildren().addAll(
                        nameLabel,
                        addressLabel,
                        phoneLabel,
                        capacityLabel,
                        availabilityButton
                );

                hotelContainer.getChildren().add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void navigateToChambre(Hotels hotel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Chambre.fxml"));
            Parent root = loader.load();

            // Pass selected hotel to ChambreController
            ChambreController chambreController = loader.getController();
            chambreController.setSelectedHotel(hotel);

            Stage stage = (Stage) hotelContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
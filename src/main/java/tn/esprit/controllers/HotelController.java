package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import tn.esprit.services.ServiceHotels;
import tn.esprit.entities.Hotels;

import java.sql.SQLException;
import java.util.List;

public class HotelController {

    @FXML
    private VBox hotelContainer = new VBox();

    private final ServiceHotels serviceHotels = new ServiceHotels();

    @FXML
    public void initialize() {
        loadHotels();
    }

    private void loadHotels() {
        try {
            List<Hotels> hotels = serviceHotels.ListAll();
            hotelContainer.getChildren().clear();

            for (Hotels hotel : hotels) {
                Button hotelButton = new Button(hotel.getNom());
                hotelButton.setPrefWidth(230);
                hotelButton.setPrefHeight(50);
                hotelButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px;");

                hotelButton.setOnAction(event -> handleHotelClick(hotel));

                hotelContainer.getChildren().add(hotelButton);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleHotelClick(Hotels hotel) {
        System.out.println("Hôtel sélectionné : " + hotel.getNom());
    }
}

package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import tn.esprit.entities.Chambres;
import tn.esprit.entities.Hotels;
import tn.esprit.services.ServiceChambre;

import java.sql.SQLException;
import java.util.List;

public class ChambreController {

    @FXML
    private VBox chambreContainer;

    private Hotels selectedHotel;
    private final ServiceChambre serviceChambre = new ServiceChambre();

    public void setSelectedHotel(Hotels hotel) {
        this.selectedHotel = hotel;
        loadChambres();
    }

    private void loadChambres() {
        try {
            List<Chambres> chambres = serviceChambre.ListByHotelId(selectedHotel.getHotel_id());
            chambreContainer.getChildren().clear();
            chambreContainer.setSpacing(15);
            chambreContainer.setPadding(new Insets(20));
            chambreContainer.setStyle("-fx-background-color: #f5f6fa;");

            for (Chambres chambre : chambres) {
                // Carte principale
                VBox card = new VBox();
                card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
                card.setPadding(new Insets(15));
                card.setSpacing(10);

                // En-tête
                HBox header = new HBox();
                header.setSpacing(20);
                Label numeroLabel = new Label("Chambre #" + chambre.getNumero_chambre());
                numeroLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #2d3436;");

                Label typeLabel = new Label(chambre.getType_enu());
                typeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #636e72; -fx-font-style: italic;");

                header.getChildren().addAll(numeroLabel, typeLabel);

                // Détails
                Label prixLabel = new Label(String.format("Prix/nuit: TND %.2f", chambre.getPrix_par_nuit()));
                prixLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #0984e3; -fx-font-weight: bold;");

                // Statut de disponibilité
                HBox statusBox = new HBox(10);
                Label statusDot = new Label("•");
                statusDot.setStyle("-fx-font-size: 24px;");
                statusDot.setTextFill(chambre.isDisponible() ? Color.valueOf("#00b894") : Color.valueOf("#d63031"));

                Label statusText = new Label(chambre.isDisponible() ? "Disponible" : "Non disponible");
                statusText.setStyle("-fx-font-size: 14px; -fx-text-fill: " + (chambre.isDisponible() ? "#00b894" : "#d63031") + ";");

                statusBox.getChildren().addAll(statusDot, statusText);

                // Bouton de réservation
                Button reserverBtn = new Button("Réserver maintenant");
                reserverBtn.setStyle("-fx-background-color: #0984e3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 8 20;");
                reserverBtn.setOnAction(e -> handleReservation(chambre));

                if(!chambre.isDisponible()) {
                    reserverBtn.setDisable(true);
                    reserverBtn.setStyle("-fx-background-color: #dfe6e9; -fx-text-fill: #636e72;");
                }

                card.getChildren().addAll(header, prixLabel, statusBox, reserverBtn);
                chambreContainer.getChildren().add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleReservation(Chambres chambre) {
        System.out.println("Début de réservation pour la chambre : " + chambre.getNumero_chambre());
        // Ajouter la logique de réservation ici
    }
}
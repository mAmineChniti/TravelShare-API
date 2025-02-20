package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Chambres;
import tn.esprit.entities.ReservationHotel;
import tn.esprit.entities.SessionManager;
import tn.esprit.services.ServiceReservationHotel;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;

public class ReservationChambreController {

    @FXML
    private Label numeroChambreLabel, typeLabel, prixLabel, statutLabel;

    @FXML
    private DatePicker startDatePicker, endDatePicker;

    @FXML
    private Button confirmButton, cancelButton;

    private Chambres chambre;
    private final ServiceReservationHotel serviceReservation = new ServiceReservationHotel();

    public void setChambre(Chambres chambre) {
        this.chambre = chambre;
        displayChambreDetails();
    }

    private void displayChambreDetails() {
        numeroChambreLabel.setText("Chambre #" + chambre.getNumero_chambre());
        typeLabel.setText(chambre.getType_enu());
        prixLabel.setText(String.format("Prix/nuit: TND %.2f", chambre.getPrix_par_nuit()));
        statutLabel.setText(chambre.isDisponible() ? "Disponible" : "Non disponible");
    }

    @FXML
    private void confirmReservation() {
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner les dates.");
            return;
        }
        if (!endDatePicker.getValue().isAfter(startDatePicker.getValue())) {
            // Show an alert if the end date is the same as or before the start date
            showAlert(Alert.AlertType.WARNING, "La date de fin doit être postérieure à la date de début.");
            return;
        }
        if (startDatePicker.getValue().isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.WARNING,"La date de début ne peut pas être antérieure à aujourd'hui.");
            return;
        }

        ReservationHotel reservation = new ReservationHotel();
        reservation.setClient_id(SessionManager.getInstance().getCurrentUtilisateur().getUser_id()); // Remplacez avec l'ID du client connecté
        reservation.setChambre_id(chambre.getChambre_id());
        reservation.setDate_debut(java.sql.Date.valueOf(startDatePicker.getValue()));
        reservation.setDate_fin(java.sql.Date.valueOf(endDatePicker.getValue()));
        reservation.setStatus_enu("en attente");

        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDatePicker.getValue(), endDatePicker.getValue());
        reservation.setPrix_totale((int) (daysBetween * chambre.getPrix_par_nuit()));

        try {
            serviceReservation.add(reservation);
            showAlert(Alert.AlertType.INFORMATION, "Réservation confirmée !");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de la réservation.");
            e.printStackTrace();
        }
    }

    @FXML
    private void cancelReservation() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
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
}
package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entities.Hotels;
import tn.esprit.entities.SessionManager;
import tn.esprit.services.ServiceHotels;

import java.io.IOException;
import java.sql.SQLException;

public class UpdateHotelController {

    @FXML private TextField nomField;
    @FXML private TextField adresseField;
    @FXML private TextField telephoneField;
    @FXML private TextField capaciteField;

    private Hotels selectedHotel;
    private final ServiceHotels serviceHotels = new ServiceHotels();

    public void setSelectedHotel(Hotels hotel) {
        this.selectedHotel = hotel;
        nomField.setText(hotel.getNom());
        adresseField.setText(hotel.getAdress());
        telephoneField.setText(hotel.getTelephone());
        capaciteField.setText(String.valueOf(hotel.getCapacite_totale()));
    }

    @FXML
    private void handleSave() {
        try {
            // Validation
            if (nomField.getText().isEmpty() || adresseField.getText().isEmpty() ||
                    telephoneField.getText().isEmpty() || capaciteField.getText().isEmpty()) {
                showAlert("Erreur", "Veuillez remplir tous les champs.", Alert.AlertType.ERROR);
                return;
            }

            // Update hotel
            selectedHotel.setNom(nomField.getText());
            selectedHotel.setAdress(adresseField.getText());
            selectedHotel.setTelephone(telephoneField.getText());
            selectedHotel.setCapacite_totale(Integer.parseInt(capaciteField.getText()));

            serviceHotels.update(selectedHotel);

            // Return to hotel list
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Hotel.fxml"));
            Parent root = loader.load();

            // Refresh data
            HotelController controller = loader.getController();
            controller.loadHotels();

            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (NumberFormatException e) {
            showAlert("Erreur", "La capacité doit être un nombre valide.", Alert.AlertType.ERROR);
        } catch (SQLException | IOException e) {
            showAlert("Erreur", "Erreur lors de la mise à jour : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Hotel.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Navigation methods
    @FXML
    private void SwitchToAccueil(ActionEvent event) {
        switchScene(event, SessionManager.getInstance().getCurrentUtilisateur().getRole() == 1 ? "/AccueilAdmin.fxml" : "/Accueil.fxml");
    }

    @FXML
    private void SwitchToVoyages(ActionEvent event) {
        switchScene(event, "/Voyages.fxml");
    }

    @FXML
    private void SwitchToHotels(ActionEvent event) {
        switchScene(event, "/Hotel.fxml");
    }

    @FXML
    private void SwitchToPosts(ActionEvent event) {
        switchScene(event, "/Posts.fxml");
    }

    @FXML
    private void switchToProfile(ActionEvent event) {
        switchScene(event, "/ProfileUtilisateur.fxml");
    }

    @FXML
    private void deconnexion(ActionEvent event) {
        switchScene(event, "/Connecter.fxml");
    }

    private void switchScene(ActionEvent event, String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
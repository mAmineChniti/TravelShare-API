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

public class AddHotelController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField adresseField;

    @FXML
    private TextField telephoneField;

    @FXML
    private TextField capaciteField;

    private final ServiceHotels serviceHotels = new ServiceHotels();

    @FXML
    private void handleAdd() {
        try {
            // Validate input fields
            if (nomField.getText().isEmpty() || adresseField.getText().isEmpty() ||
                    telephoneField.getText().isEmpty() || capaciteField.getText().isEmpty()) {
                showAlert("Erreur", "Veuillez remplir tous les champs.", Alert.AlertType.ERROR);
                return;
            }

            // Create new hotel
            Hotels newHotel = new Hotels();
            newHotel.setNom(nomField.getText());
            newHotel.setAdress(adresseField.getText());
            newHotel.setTelephone(telephoneField.getText());
            newHotel.setCapacite_totale(Integer.parseInt(capaciteField.getText()));

            // Add to database
            serviceHotels.add(newHotel);

            // Navigate back to Hotel.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Hotel.fxml"));
            Parent root = loader.load();

            // Refresh hotel list
            HotelController controller = loader.getController();
            controller.loadHotels();

            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (NumberFormatException e) {
            showAlert("Erreur", "La capacité doit être un nombre valide.", Alert.AlertType.ERROR);
        } catch (SQLException | IOException e) {
            showAlert("Erreur", "Une erreur s'est produite : " + e.getMessage(), Alert.AlertType.ERROR);
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
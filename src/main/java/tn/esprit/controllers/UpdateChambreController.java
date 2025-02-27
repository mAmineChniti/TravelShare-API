package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entities.Chambres;
import tn.esprit.entities.Hotels;
import tn.esprit.entities.SessionManager;
import tn.esprit.services.ServiceChambre;

import java.io.IOException;
import java.sql.SQLException;

public class UpdateChambreController {

    @FXML
    private TextField numeroField;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField prixField;

    private Chambres selectedChambre;
    private Hotels selectedHotel;
    private final ServiceChambre serviceChambre = new ServiceChambre();

    @FXML
    public void initialize() {
        typeComboBox.getItems().addAll("simple", "double", "suite");
    }

    public void setSelectedChambre(Chambres chambre) {
        this.selectedChambre = chambre;
        numeroField.setText(chambre.getNumero_chambre());
        typeComboBox.setValue(chambre.getType_enu());
        prixField.setText(String.valueOf(chambre.getPrix_par_nuit()));
    }

    public void setSelectedHotel(Hotels hotel) {
        this.selectedHotel = hotel;
    }

    @FXML
    private void handleSave() {
        try {
            selectedChambre.setNumero_chambre(numeroField.getText());
            selectedChambre.setType_enu(typeComboBox.getValue());
            selectedChambre.setPrix_par_nuit(Double.parseDouble(prixField.getText()));

            serviceChambre.update(selectedChambre);

            // Navigate back to Chambre.fxml and refresh data
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Chambre.fxml"));
            Parent root = loader.load();

            ChambreController controller = loader.getController();
            controller.setSelectedHotel(selectedHotel); // Reload the hotel data

            Stage stage = (Stage) numeroField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (NumberFormatException | SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Chambre.fxml"));
            Parent root = loader.load();

            ChambreController controller = loader.getController();
            controller.setSelectedHotel(selectedHotel); // Reload the hotel data

            Stage stage = (Stage) numeroField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Navigation methods (same as before)
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
}
package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entities.Chambres;
import tn.esprit.entities.Hotels;
import tn.esprit.entities.SessionManager;
import tn.esprit.services.ServiceChambre;

import java.io.IOException;
import java.sql.SQLException;

public class AddChambreController {

    @FXML
    private TextField numeroField;

    @FXML
    private ComboBox<String> typeComboBox; // ComboBox for type

    @FXML
    private TextField prixField;

    private Hotels selectedHotel;
    private final ServiceChambre serviceChambre = new ServiceChambre();

    @FXML
    public void initialize() {
        typeComboBox.getItems().addAll("simple", "double", "suite");
        typeComboBox.setValue("simple"); // Set default value
    }

    // Set the selected hotel
    public void setSelectedHotel(Hotels hotel) {
        this.selectedHotel = hotel;
    }

    @FXML
    private void handleAdd() {
        String numero = numeroField.getText();
        String type = typeComboBox.getValue(); // Get selected type from ComboBox
        String prix = prixField.getText();

        // Validate input fields
        if (numero.isEmpty() || type.isEmpty() || prix.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.", Alert.AlertType.ERROR);
            return;
        }

        // Validate prix (must be a valid double)
        double prixValue;
        try {
            prixValue = Double.parseDouble(prix);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le prix doit être un nombre valide.", Alert.AlertType.ERROR);
            return;
        }

        // Ensure a hotel is selected
        if (selectedHotel == null) {
            showAlert("Erreur", "Aucun hôtel sélectionné.", Alert.AlertType.ERROR);
            return;
        }

        boolean disponibleValue = true;

        // Create a new Chambres object
        Chambres newChambre = new Chambres(0, selectedHotel.getHotel_id(), numero, type, prixValue, disponibleValue);

        try {
            serviceChambre.add(newChambre);
            showAlert("Succès", "Chambre ajoutée avec succès.", Alert.AlertType.INFORMATION);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Chambre.fxml"));
            Parent root = loader.load();

            ChambreController controller = loader.getController();
            controller.setSelectedHotel(selectedHotel);

            Stage stage = (Stage) numeroField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (SQLException e) {
            showAlert("Erreur", "Une erreur s'est produite lors de l'ajout de la chambre : " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (IOException e) {
            showAlert("Erreur", "Une erreur s'est produite lors du chargement de la page.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Chambre.fxml"));
            Parent root = loader.load();

            ChambreController controller = loader.getController();
            controller.setSelectedHotel(selectedHotel);

            Stage stage = (Stage) numeroField.getScene().getWindow();
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
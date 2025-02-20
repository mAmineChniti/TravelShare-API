package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entities.Hotels;
import tn.esprit.services.ServiceHotels;

import java.sql.SQLException;

public class UpdateHotelController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField adresseField;

    @FXML
    private TextField telephoneField;

    @FXML
    private TextField capaciteField;

    private Hotels selectedHotel;
    private ServiceHotels serviceHotels = new ServiceHotels();

    public void setSelectedHotel(Hotels hotel) {
        this.selectedHotel = hotel;
        // Remplir les champs avec les données de l'hôtel sélectionné
        nomField.setText(hotel.getNom());
        adresseField.setText(hotel.getAdress());
        telephoneField.setText(hotel.getTelephone());
        capaciteField.setText(String.valueOf(hotel.getCapacite_totale()));
    }

    @FXML
    private void handleSave() {
        try {
            // Mettre à jour les données de l'hôtel
            selectedHotel.setNom(nomField.getText());
            selectedHotel.setAdress(adresseField.getText());
            selectedHotel.setTelephone(telephoneField.getText());
            selectedHotel.setCapacite_totale(Integer.parseInt(capaciteField.getText()));

            // Appeler la méthode de mise à jour du service
            serviceHotels.update(selectedHotel);
            System.out.println("Hôtel mis à jour avec succès !");

            // Fermer la fenêtre pop-up
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            System.err.println("Erreur de format : La capacité doit être un nombre.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la mise à jour de l'hôtel : " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        // Fermer la fenêtre pop-up sans sauvegarder
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
}
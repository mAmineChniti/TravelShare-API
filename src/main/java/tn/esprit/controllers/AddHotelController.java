package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entities.Hotels;
import tn.esprit.services.ServiceHotels;

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

    private ServiceHotels serviceHotels = new ServiceHotels();

    @FXML
    private void handleAdd() {
        try {
            // Créer un nouvel hôtel avec les données du formulaire
            Hotels newHotel = new Hotels();
            newHotel.setNom(nomField.getText());
            newHotel.setAdress(adresseField.getText());
            newHotel.setTelephone(telephoneField.getText());
            newHotel.setCapacite_totale(Integer.parseInt(capaciteField.getText()));

            // Appeler la méthode d'ajout du service
            serviceHotels.add(newHotel);
            System.out.println("Hôtel ajouté avec succès !");

            // Fermer la fenêtre pop-up
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            System.err.println("Erreur de format : La capacité doit être un nombre.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'ajout de l'hôtel : " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        // Fermer la fenêtre pop-up sans ajouter
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
}
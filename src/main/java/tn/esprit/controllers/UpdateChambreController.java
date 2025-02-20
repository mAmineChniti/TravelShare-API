package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entities.Chambres;
import tn.esprit.services.ServiceChambre;

import java.sql.SQLException;

public class UpdateChambreController {

    @FXML
    private TextField numeroField;

    @FXML
    private TextField typeField;

    @FXML
    private TextField prixField;

    @FXML
    private TextField disponibleField;

    private Chambres selectedChambre;
    private ServiceChambre serviceChambre = new ServiceChambre();

    public void setSelectedChambre(Chambres chambre) {
        this.selectedChambre = chambre;
        // Remplir les champs avec les données de la chambre sélectionnée
        numeroField.setText(chambre.getNumero_chambre());
        typeField.setText(chambre.getType_enu());
        prixField.setText(String.valueOf(chambre.getPrix_par_nuit()));
        disponibleField.setText(String.valueOf(chambre.isDisponible()));
    }

    @FXML
    private void handleSave() {
        try {
            // Mettre à jour les données de la chambre
            selectedChambre.setNumero_chambre(numeroField.getText());
            selectedChambre.setType_enu(typeField.getText());
            selectedChambre.setPrix_par_nuit(Double.parseDouble(prixField.getText()));
            selectedChambre.setDisponible(Boolean.parseBoolean(disponibleField.getText()));

            // Appeler la méthode de mise à jour du service
            serviceChambre.update(selectedChambre);
            System.out.println("Chambre mise à jour avec succès !");

            // Fermer la fenêtre pop-up
            Stage stage = (Stage) numeroField.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            System.err.println("Erreur de format : Le prix doit être un nombre.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la mise à jour de la chambre : " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        // Fermer la fenêtre pop-up sans sauvegarder
        Stage stage = (Stage) numeroField.getScene().getWindow();
        stage.close();
    }
}
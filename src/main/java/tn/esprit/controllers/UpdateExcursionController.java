package tn.esprit.controllers;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.Excursions;
import tn.esprit.entities.Guides;
import tn.esprit.services.ServiceExcursion;
import tn.esprit.services.ServiceGuide;

public class UpdateExcursionController {

    @FXML
    private TextField titleTF;

    @FXML
    private TextArea descriptionTA;

    @FXML
    private DatePicker datePicker;

    @FXML
    private DatePicker datePicker2;

    @FXML
    private ChoiceBox<String> choiceBoxGuide;

    @FXML
    private TextField priceTF;

    @FXML
    private Label imagePathLabel;

    // Instance de ServiceExcursion pour gérer les opérations de la base de données
    private ServiceExcursion serviceExcursion = new ServiceExcursion();
    private Excursions excursionToEdit;
    private List<Guides> guidesList; // Liste des guides

    // Méthode pour recevoir l'excursion à modifier depuis un autre contrôleur
    public void setExcursionToEdit(Excursions excursion) {
        this.excursionToEdit = excursion;

        // Pré-remplir les champs avec les données de l'excursion à modifier
        titleTF.setText(excursion.getTitle());
        descriptionTA.setText(excursion.getDescription());
        datePicker.setValue(excursion.getDate_excursion().toLocalDate());
        datePicker2.setValue(excursion.getDate_fin().toLocalDate());
        priceTF.setText(String.valueOf(excursion.getPrix()));


        loadGuides();

        // Sélectionner automatiquement le guide correspondant
        if (excursion.getGuide_id() != 0) {
            // Trouver le nom du guide correspondant à l'ID
            for (Guides guide : guidesList) {
                if (guide.getGuide_id() == excursion.getGuide_id()) {
                    choiceBoxGuide.setValue(guide.getName());
                    break;
                }
            }
        }
    }

    private void loadGuides() {
        try {
            ServiceGuide serviceGuide = new ServiceGuide();
            guidesList = serviceGuide.ListAll();
            if (guidesList.isEmpty()) {
                System.out.println("No guides found.");
            }
            guidesList.forEach(guide -> choiceBoxGuide.getItems().add(guide.getName()));
            System.out.println("Guides loaded successfully.");
        } catch (SQLException e) {
            System.err.println("Error loading guides: " + e.getMessage());
        }
    }


    @FXML
    void chooseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(null); // Ouvrir la fenêtre de sélection de fichier

        if (selectedFile != null) {
            // Mettre à jour le Label avec le chemin du fichier sélectionné
            imagePathLabel.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    void updateExcursion(ActionEvent event) {
        // Vérification des champs
        if (titleTF.getText().isEmpty() || descriptionTA.getText().isEmpty() ||
                datePicker.getValue() == null || datePicker2.getValue() == null || priceTF.getText().isEmpty() ||
                choiceBoxGuide.getValue() == null || imagePathLabel.getText().isEmpty()) {

            // Alerte si des champs sont vides
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Empty fields");
            alert.setHeaderText(null);
            alert.setContentText("All fields must be completed.");
            alert.showAndWait();
            return;
        }

        // Mise à jour de l'excursion avec les nouvelles informations
        excursionToEdit.setTitle(titleTF.getText());
        excursionToEdit.setDescription(descriptionTA.getText());
        excursionToEdit.setDate_excursion(Date.valueOf(datePicker.getValue()));
        excursionToEdit.setDate_fin(Date.valueOf(datePicker2.getValue()));
        excursionToEdit.setImage(imagePathLabel.getText());
        excursionToEdit.setPrix(Double.parseDouble(priceTF.getText()));

        String selectedGuideName = choiceBoxGuide.getValue(); // Récupérer le nom du guide sélectionné
        if (selectedGuideName != null) {
            for (Guides guide : guidesList) {
                if (guide.getName().equals(selectedGuideName)) {
                    excursionToEdit.setGuide_id(guide.getGuide_id()); // Mettre à jour le guide_id
                    break;
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Guide Selection Error");
            alert.setContentText("Please select a guide.");
            alert.showAndWait();
        }


        try {
            serviceExcursion.update(excursionToEdit);

            // Alerte de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Excursion successfully updated !");
            alert.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while updating.");
            alert.showAndWait();
        }
    }

    @FXML
    void goBack1(ActionEvent event) {
        try {
            // Charger la scène ListExcursion.fxml
            Parent root = FXMLLoader.load(getClass().getResource("/ListExcursion.fxml"));

            // Créer une nouvelle scène et la mettre sur le stage actuel
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);  // Remplacer la scène actuelle par ListExcursion.fxml
            stage.show();  // Afficher la nouvelle scène

        } catch (IOException e) {
            System.out.println("Error loading page ListExcursion.fxml : " + e.getMessage());
        }
    }
}
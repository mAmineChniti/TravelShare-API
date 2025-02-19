package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Excursions;
import tn.esprit.entities.Guides;
import tn.esprit.services.ServiceExcursion;
import tn.esprit.services.ServiceGuide;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class AddExcursionController implements Initializable {

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextArea descriptionTA;

    @FXML
    private TextField durationTF;

    @FXML
    private TextField titleTF;

    @FXML
    private ChoiceBox<String> choiceBoxGuide;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Récupérer la liste des guides depuis la base de données
        ServiceGuide serviceGuide = new ServiceGuide();
        List<Guides> guides = null;
        try {
            guides = serviceGuide.ListAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Ajouter les noms des guides au ChoiceBox
        for (Guides guide : guides) {
            choiceBoxGuide.getItems().add(guide.getName());
        }
    }

    @FXML
    void addExcursion(ActionEvent event) {
        // Récupérer les valeurs des champs du formulaire
        String title = titleTF.getText();
        String description = descriptionTA.getText();
        String durationStr = durationTF.getText();
        String selectedGuide = choiceBoxGuide.getValue();
        LocalDate excursionDate = datePicker.getValue();

        // Validation des champs
        if (title.isEmpty() || description.isEmpty() || durationStr.isEmpty() || selectedGuide == null || excursionDate == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
            return;
        }

        int duration;
        try {
            duration = Integer.parseInt(durationStr);  // Vérifier si la durée est un nombre entier
        } catch (NumberFormatException e) {
            showAlert("Erreur", "La durée doit être un nombre entier", Alert.AlertType.ERROR);
            return;
        }

        // Créer l'objet Excursion avec les valeurs récupérées
        Excursions excursion = new Excursions();
        excursion.setTitle(title);
        excursion.setDescription(description);
        excursion.setDuration(duration);
        excursion.setDate_excursion(Date.valueOf(excursionDate));

        // Récupérer le guide sélectionné et l'associer à l'excursion
        ServiceGuide serviceGuide = new ServiceGuide();
        Guides guide = null;
        try {
            guide = serviceGuide.getGuideByName(selectedGuide);  // Récupérer le guide par son nom
            excursion.setGuide_id(guide.getGuide_id());  // Associer le guide à l'excursion
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la récupération du guide : " + e.getMessage(), Alert.AlertType.ERROR);
            return;
        }

        // Ajouter l'excursion via le service
        ServiceExcursion serviceExcursion = new ServiceExcursion();
        try {
            serviceExcursion.add(excursion);  // Appel à la méthode add du service
            showAlert("Succès", "Excursion ajoutée avec succès", Alert.AlertType.INFORMATION);
            clearFields();  // Effacer les champs après l'ajout
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout de l'excursion : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour vider les champs après ajout
    private void clearFields() {
        titleTF.clear();
        descriptionTA.clear();
        durationTF.clear();
        choiceBoxGuide.getSelectionModel().clearSelection();
        datePicker.setValue(null);
    }

    @FXML
    void goBack1(ActionEvent event) {
        try {
            // Charger la scène ListGuide.fxml
            Parent root = FXMLLoader.load(getClass().getResource("/ListExcursion.fxml"));

            // Créer une nouvelle scène et la mettre sur le stage actuel
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);  // Remplacer la scène actuelle par ListGuide.fxml
            stage.show();  // Afficher la nouvelle scène

        } catch (IOException e) {
            System.out.println("Erreur de chargement de la page ListGuide.fxml : " + e.getMessage());
        }
    }
}

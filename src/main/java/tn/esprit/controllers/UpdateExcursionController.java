package tn.esprit.controllers;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entities.Excursions;
import tn.esprit.services.ServiceExcursion;

public class UpdateExcursionController {

    @FXML
    private ChoiceBox<?> choiceBoxGuide;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextArea descriptionTA;

    @FXML
    private TextField durationTF;

    @FXML
    private TextField titleTF;

    // Instance de ServiceExcursion pour gérer les opérations de la base de données
    private ServiceExcursion serviceExcursion = new ServiceExcursion();
    private Excursions excursionToEdit;

    // Méthode pour recevoir l'excursion à modifier depuis un autre contrôleur
    public void setExcursionToEdit(Excursions excursion) {
        this.excursionToEdit = excursion;

        // Pré-remplir les champs avec les données de l'excursion à modifier
        titleTF.setText(excursion.getTitle());
        descriptionTA.setText(excursion.getDescription());
        durationTF.setText(String.valueOf(excursion.getDuration()));
        datePicker.setValue(excursion.getDate_excursion().toLocalDate());

        // Ici, vous pouvez également peupler le ChoiceBox avec les guides disponibles
      //   choiceBoxGuide.getItems().addAll(listOfGuides);
      //   choiceBoxGuide.setValue(excursion.getGuide());
    }

    @FXML
    void updateExcursion(ActionEvent event) {
        // Vérification des champs
        if (titleTF.getText().isEmpty() || descriptionTA.getText().isEmpty() ||
                durationTF.getText().isEmpty() || datePicker.getValue() == null ||
                choiceBoxGuide.getValue() == null) {

            // Alerte si des champs sont vides
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs vides");
            alert.setHeaderText(null);
            alert.setContentText("Tous les champs doivent être remplis.");
            alert.showAndWait();
            return;
        }

        // Mise à jour de l'excursion avec les nouvelles informations
        excursionToEdit.setTitle(titleTF.getText());
        excursionToEdit.setDescription(descriptionTA.getText());
        excursionToEdit.setDuration(Integer.parseInt(durationTF.getText()));
        excursionToEdit.setDate_excursion(Date.valueOf(datePicker.getValue()));
    //    excursionToEdit.setGuide_id((Guides) choiceBoxGuide.getValue()); // Assuming Guide is the type of object in choiceBox

        try {
            serviceExcursion.update(excursionToEdit);

            // Alerte de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Excursion mise à jour avec succès !");
            alert.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur est survenue lors de la mise à jour.");
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
            System.out.println("Erreur de chargement de la page ListExcursion.fxml : " + e.getMessage());
        }
    }
}

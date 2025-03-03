package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Reponse;
import tn.esprit.services.ServiceReclamation;
import tn.esprit.services.ServiceReponse;
import tn.esprit.services.ServiceUtilisateur;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;


public class ReponseAdminController {

    @FXML
    private Hyperlink listeRec;

    @FXML
    private TextArea reponseField;

    @FXML
    private Button envoyerButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button delButton;

    @FXML
    private ListView<String> reclamationListView; // ListView pour afficher les réclamations

    private final ServiceReponse serviceReponse = new ServiceReponse();
    private final ServiceReclamation serviceReclamation = new ServiceReclamation();


    @FXML
    void initialize() {
        try {
            List<String> reclamations = serviceReclamation.getReclamationsWithUserInfo();

            // Convertir la liste en observable et l'afficher dans la ListView
            ObservableList<String> observableList = FXCollections.observableArrayList(reclamations);
            reclamationListView.setItems(observableList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void envoyerReponse(ActionEvent event) {
        try {
            // Récupérer la réclamation sélectionnée dans la ListView
            String selectedReclamationText = reclamationListView.getSelectionModel().getSelectedItem();

            if (selectedReclamationText == null || selectedReclamationText.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Aucune réclamation sélectionnée", "Veuillez sélectionner une réclamation avant d'envoyer une réponse.");
                return;
            }

            // Extraire le titre de la réclamation
            String[] parts = selectedReclamationText.split(" - Titre : ");
            if (parts.length < 2) {
                showAlert(Alert.AlertType.ERROR, "Erreur de format", "Impossible d'extraire le titre de la réclamation.");
                return;
            }
            String titreReclamation = parts[1].split(" - ")[0].trim(); // On prend le titre avant la prochaine info

            System.out.println("Titre extrait : " + titreReclamation); // Debugging

            // Récupérer l'ID de la réclamation
            int reclamationId = serviceReclamation.getReclamationIdByTitle(titreReclamation);
            if (reclamationId == -1) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de trouver l'ID de la réclamation.");
                return;
            }

            // Récupérer le contenu de la réponse
            String contenuReponse = reponseField.getText().trim();
            if (contenuReponse.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Champ vide", "Veuillez saisir une réponse avant d'envoyer.");
                return;
            }

            // Créer une instance de Reponse et l'enregistrer
            Date currentDate = new Date(System.currentTimeMillis());
            Reponse reponse = new Reponse(reclamationId, contenuReponse, currentDate);
            serviceReponse.add(reponse);

            // Afficher un message de confirmation
            showAlert(Alert.AlertType.INFORMATION, "Succès", " Réponse envoyée avec succès !");

            // Rediriger vers la page ListReponse
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListReponse.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) envoyerButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Une erreur est survenue lors de l'enregistrement de la réponse.");
            e.printStackTrace();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur Navigation", "Impossible de charger la page ListReponse.");
            e.printStackTrace();
        }
    }

    @FXML
    void deleteReponse(ActionEvent event) {

    }

    @FXML
    void updateReponse(ActionEvent event) {

    }

    @FXML
    void deconnexion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Connecter.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void switchToAccueil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AccueilAdmin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void listeReclamation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListReponse.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) listeRec.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import tn.esprit.entities.Reclamation;
import tn.esprit.entities.Reponse;
import tn.esprit.services.ServiceReclamation;
import tn.esprit.services.ServiceReponse;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import static tn.esprit.entities.SessionManager.getCurrentUtilisateur;

public class ListReponseController {

    @FXML
    private TextArea reponseField;

    @FXML
    private Button saveButton;

    @FXML
    private Button delButton;

    @FXML
    private ListView<String> reponseListView; // ListView pour afficher les réponses

    private final ServiceReponse serviceReponse = new ServiceReponse();
    private final ServiceReclamation serviceReclamation = new ServiceReclamation();


    @FXML
    void initialize() {
        try {
            List<String> reponses = serviceReponse.getReponsesWithUserInfoAndRec();

            // Convertir la liste en observable et l'afficher dans la ListView
            ObservableList<String> observableList = FXCollections.observableArrayList(reponses);
            reponseListView.setItems(observableList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void updateReponse(ActionEvent event) {
        // Récupérer la réponse sélectionnée
        String selectedReponseText = reponseListView.getSelectionModel().getSelectedItem();

        if (selectedReponseText != null) {
            try {
                // Extraire l'ancien contenu de la réponse
                String oldReponse = selectedReponseText.split(" - Contenu : ")[0];
                System.out.println("Ancien contenu: " + oldReponse);  // Afficher le contenu pour vérifier

                // Récupérer l'ID de la réponse via l'ancien contenu
                int reponse_id = serviceReponse.getReponseIdByContenu(oldReponse);
                System.out.println("ID de la réponse: " + reponse_id);  // Vérifier l'ID récupéré

                if (reponse_id != -1) {
                    // Récupérer la nouvelle réponse
                    String newContenu = reponseField.getText().trim();

                    if (!newContenu.isEmpty()) {
                        // Mettre à jour le contenu de la réponse dans la base de données
                        serviceReponse.updateReponseContenu(reponse_id, newContenu);

                        // Récupérer l'index sélectionné
                        int selectedIndex = reponseListView.getSelectionModel().getSelectedIndex();

                        // Récupérer les anciennes valeurs pour les afficher correctement
                        String[] parts = selectedReponseText.split(" - ");
                        String name = parts[0].split(": ")[1]; // Extraire le nom
                        String email = parts[1].split(": ")[1]; // Extraire l'email
                        String title = parts[2].split(": ")[1]; // Extraire le titre
                        String dateReponse = parts[4].split(": ")[1]; // Extraire la date

                        // Mettre à jour l'affichage dans la ListView
                        String newAffichage = "Nom : " + name +
                                " , Email : " + email +
                                " , Titre : " + title +
                                " , Contenu : " + newContenu +
                                " , Date : " + dateReponse;

                        reponseListView.getItems().set(selectedIndex, newAffichage);

                        // Afficher un message de succès
                        showAlert(Alert.AlertType.INFORMATION, "Mise à jour réussie", "Réponse mise à jour avec succès !");
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs !");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Réponse introuvable !");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de mettre à jour la réponse.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une réponse.");
        }
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

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

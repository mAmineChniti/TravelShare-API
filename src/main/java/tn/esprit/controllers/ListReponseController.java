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
            ObservableList<String> observableList = FXCollections.observableArrayList(reponses);
            reponseListView.setItems(observableList);

            // Ajouter un listener pour afficher le contenu dans reponseField
            reponseListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue != null) {
                    // Extraction du contenu de la réponse
                    String[] parts = newValue.split(" , ");
                    if (parts.length >= 4) {
                        String contenu = parts[3].split(": ")[1].trim();
                        reponseField.setText(contenu);
                    } else {
                        reponseField.clear(); // Effacer si problème de format
                    }
                }
            });

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
                // Extraction correcte du contenu
                String[] parts = selectedReponseText.split(" , ");
                if (parts.length >= 4) {  // Vérifier que la structure est correcte
                    String oldReponse = parts[3].split(": ")[1].trim();
                    System.out.println("Ancien contenu extrait : " + oldReponse);

                    // Récupérer l'ID de la réponse via l'ancien contenu
                    int reponse_id = serviceReponse.getReponseIdByContenu(oldReponse);
                    System.out.println("ID de la réponse trouvé : " + reponse_id);

                    if (reponse_id != -1) {
                        // Récupérer la nouvelle réponse
                        String newContenu = reponseField.getText().trim();

                        if (!newContenu.isEmpty()) {
                            // Mettre à jour la réponse
                            serviceReponse.updateReponseContenu(reponse_id, newContenu);

                            // Mise à jour de l'affichage
                            int selectedIndex = reponseListView.getSelectionModel().getSelectedIndex();
                            String name = parts[0].split(": ")[1];
                            String email = parts[1].split(": ")[1];
                            String title = parts[2].split(": ")[1];
                            String dateReponse = parts[4].split(": ")[1];

                            String newAffichage = "Nom : " + name +
                                    " , Email : " + email +
                                    " , Titre : " + title +
                                    " , Contenu : " + newContenu +
                                    " , Date : " + dateReponse;

                            reponseListView.getItems().set(selectedIndex, newAffichage);

                            showAlert(Alert.AlertType.INFORMATION, "Mise à jour réussie", "Réponse mise à jour avec succès !");
                        } else {
                            showAlert(Alert.AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs !");
                        }
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Réponse introuvable !");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Format de la réponse incorrect !");
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
    void deletereponse(ActionEvent event) {
        // Récupérer la réponse sélectionnée
        String selectedReponseText = reponseListView.getSelectionModel().getSelectedItem();

        if (selectedReponseText != null) {
            try {
                // Extraction correcte du contenu
                String[] parts = selectedReponseText.split(" , ");
                if (parts.length >= 4) {  // Vérifier que la structure est correcte
                    String oldReponse = parts[3].split(": ")[1].trim();
                    System.out.println("Ancien contenu extrait : " + oldReponse);

                    // Récupérer l'ID de la réponse via l'ancien contenu
                    int reponse_id = serviceReponse.getReponseIdByContenu(oldReponse);
                    System.out.println("ID de la réponse trouvé : " + reponse_id);

                    if (reponse_id != -1) {
                        // Demander confirmation avant suppression
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirmation de suppression");
                        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette réponse ?");
                        alert.setContentText("Cette action est irréversible.");
                        if (alert.showAndWait().get() == ButtonType.OK) {
                            // Supprimer la réponse de la base de données
                            serviceReponse.delete(reponse_id);
                            System.out.println("Réponse supprimée avec succès.");

                            // Mise à jour de l'affichage
                            int selectedIndex = reponseListView.getSelectionModel().getSelectedIndex();
                            reponseListView.getItems().remove(selectedIndex);

                            showAlert(Alert.AlertType.INFORMATION, "Suppression réussie", "Réponse supprimée avec succès !");
                        }
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Réponse introuvable !");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Format de la réponse incorrect !");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de supprimer la réponse.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une réponse à supprimer.");
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

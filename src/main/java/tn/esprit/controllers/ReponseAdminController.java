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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tn.esprit.entities.Reponse;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.ServiceReclamation;
import tn.esprit.services.ServiceReponse;
import tn.esprit.services.ServiceUtilisateur;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ReponseAdminController {

    @FXML
    private Hyperlink listeRec;

    @FXML
    private TextArea reponseField;

    @FXML
    private Button envoyerButton;

    @FXML
    private ComboBox<String> etatrec;


    @FXML
    private ListView<String> reclamationListView; // ListView pour afficher les réclamations

    private final ServiceReponse serviceReponse = new ServiceReponse();
    private final ServiceReclamation serviceReclamation = new ServiceReclamation();
    private final ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();


    @FXML
    void initialize() {

            // Ajouter les options de filtrage au ComboBox
            etatrec.getItems().addAll("Tous", "en cours", "repondu");
            etatrec.setValue("Tous"); // Par défaut, afficher tout

            // Charger et afficher toutes les réclamations au démarrage
            mettreAJourListView("Tous");

            // Écouteur sur le ComboBox pour filtrer dynamiquement les réclamations
            etatrec.setOnAction(event -> mettreAJourListView(etatrec.getValue()));
    }

    private void mettreAJourListView(String etat) {
        try {
            // Appeler la méthode filtrée dans ServiceReclamation
            List<String> filteredReclamations = serviceReclamation.filtrerReclamationsParEtat(etat);

            // Convertir la liste en ObservableList et l'affecter à la ListView
            ObservableList<String> observableList = FXCollections.observableArrayList(filteredReclamations);
            reclamationListView.setItems(observableList);

            // Personnaliser l'affichage des cellules de la ListView
            reclamationListView.setCellFactory(param -> new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setGraphic(new Label(item)); // Affichage simplifié
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void envoyerReponse(ActionEvent event) {
        try {
            // Récupérer la réclamation sélectionnée
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
            String titreReclamation = parts[1].split(" - ")[0].trim();

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

            // Ajouter la réponse
            Date currentDate = new Date(System.currentTimeMillis());
            Reponse reponse = new Reponse(reclamationId, contenuReponse, currentDate);
            serviceReponse.add(reponse);

            // ✅ Mettre à jour l'état de la réclamation à "repondu"
            serviceReclamation.updateEtatReclamation(reclamationId, "repondu");

            // Afficher un message de confirmation
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réponse envoyée et réclamation mise à jour !");

            // Rediriger vers la page ListReponse
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReponseAdmin.fxml"));
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

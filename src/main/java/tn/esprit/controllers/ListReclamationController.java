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
import tn.esprit.entities.SessionManager;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.ServiceReclamation;
import tn.esprit.entities.Reclamation;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ListReclamationController {

    @FXML
    private TextArea objetField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ComboBox<String> etatrec;

    @FXML
    private Button delButton;

    @FXML
    private Button saveButton;

    @FXML
    private ListView<String> reclamationListView;

    private final ServiceReclamation serviceReclamation = new ServiceReclamation();

    @FXML
    private void initialize() {
            SessionManager session = SessionManager.getInstance();
            Utilisateur currentUser = session.getCurrentUtilisateur();
            int user_id = currentUser.getUser_id();

            // Ajouter les options de filtrage au ComboBox
            etatrec.getItems().addAll("Tous", "en cours", "repondu");
            etatrec.setValue("Tous"); // Par défaut, afficher toutes les réclamations

            // Charger et afficher toutes les réclamations au démarrage
            mettreAJourListView("Tous");

            // Écouteur sur le ComboBox pour filtrer dynamiquement les réclamations
            etatrec.setOnAction(event -> mettreAJourListView(etatrec.getValue()));
            
    }

    private void mettreAJourListView(String etat) {
        try {
            // Récupérer les réclamations filtrées par état depuis le service
            SessionManager session = SessionManager.getInstance();
            Utilisateur currentUser = session.getCurrentUtilisateur();
            int user_id = currentUser.getUser_id();

            List<Reclamation> allReclamations = serviceReclamation.ListAll();

            // Filtrer les réclamations de l'utilisateur par état
            List<Reclamation> userReclamations = allReclamations.stream()
                    .filter(reclamation -> reclamation.getUser_id() == user_id)
                    .collect(Collectors.toList());

            List<Reclamation> filteredReclamations;
            if (etat.equals("Tous")) {
                filteredReclamations = userReclamations; // Toutes les réclamations
            } else {
                filteredReclamations = userReclamations.stream()
                        .filter(reclamation -> reclamation.getEtat().equalsIgnoreCase(etat))
                        .collect(Collectors.toList());
            }

            // Convertir la liste filtrée en ObservableList
            ObservableList<String> items = FXCollections.observableArrayList();
            filteredReclamations.forEach(reclamation ->
                    items.add(reclamation.getTitle() + " - " + reclamation.getDescription() + " - " + reclamation.getEtat()));

            // Mettre à jour la ListView avec les réclamations filtrées
            reclamationListView.setItems(items);

            // 🎯 Corriger la récupération du titre et de la description
            reclamationListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    // Extraire uniquement le titre depuis newValue
                    String[] parts = newValue.split(" - ");
                    String titre = parts[0]; // Le titre est toujours en premier
                    String description = parts.length > 1 ? parts[1] : ""; // Si une description existe, on la prend

                    // Mettre à jour les champs
                    objetField.setText(titre);
                    descriptionField.setText(description);
                }
            });

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la mise à jour des réclamations.");
            e.printStackTrace();
        }
    }


    @FXML
    void deleteReclamation(ActionEvent event) {
        // Récupérer l'élément sélectionné (title - description)
        String selectedReclamation = reclamationListView.getSelectionModel().getSelectedItem();

        if (selectedReclamation != null) {
            // Extraire uniquement le title
            String[] parts = selectedReclamation.split(" - ", 2); // Diviser en 2 parties (title et description)
            if (parts.length < 1) {
                // Si le titre est introuvable, afficher une alerte
                showAlert(Alert.AlertType.ERROR, "Erreur", "Format de réclamation invalide.");
                return;
            }
            String title = parts[0].trim(); // Prendre uniquement le titre

            // Confirmer la suppression
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmer la suppression");
            confirmationAlert.setHeaderText("Voulez-vous vraiment supprimer cette réclamation ?");
            confirmationAlert.setContentText("Cette action est irréversible.");

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    // Récupérer l'ID de la réclamation à partir du titre
                    int reclamation_id = serviceReclamation.getReclamationIdByTitle(title);

                    if (reclamation_id != -1) { // Si un ID a été trouvé
                        serviceReclamation.delete(reclamation_id); // Supprimer de la BD

                        // Supprimer de la liste affichée
                        reclamationListView.getItems().remove(selectedReclamation);

                        // Afficher un message de succès
                        showAlert(Alert.AlertType.INFORMATION, "Suppression réussie", "Réclamation supprimée avec succès.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Réclamation introuvable.");
                    }
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la suppression.");
                    e.printStackTrace();
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une réclamation.");
        }
    }

    @FXML
    void updateReclamation(ActionEvent event) {
        // Récupérer la réclamation sélectionnée
        String selectedReclamationText = reclamationListView.getSelectionModel().getSelectedItem();

        if (selectedReclamationText != null) {
            try {
                // Extraire l'ancien titre
                String oldTitle = selectedReclamationText.split(" - ")[0];

                // Récupérer l'ID de la réclamation via l'ancien titre
                int reclamation_id = serviceReclamation.getReclamationIdByTitle(oldTitle);

                if (reclamation_id != -1) {
                    // Récupérer les nouvelles valeurs
                    String newTitle = objetField.getText().trim();
                    String newDescription = descriptionField.getText().trim();

                    if (!newTitle.isEmpty() && !newDescription.isEmpty()) {
                        // Récupérer la date système actuelle en utilisant java.util.Date
                        Date currentDate = new Date(System.currentTimeMillis());

                        // Récupérer l'ID de l'utilisateur actuel (assumant que cette méthode existe)
                        SessionManager session = SessionManager.getInstance();
                        int user_id = session.getCurrentUtilisateur().getUser_id();

                        // Mettre à jour la réclamation dans la base de données avec la date et les autres informations
                        Reclamation updatedReclamation = new Reclamation(reclamation_id, user_id, newTitle, newDescription, currentDate);

                        // Mettre à jour la réclamation dans le service
                        serviceReclamation.update(updatedReclamation);

                        // Mettre à jour la ListView
                        int selectedIndex = reclamationListView.getSelectionModel().getSelectedIndex();
                        reclamationListView.getItems().set(selectedIndex, newTitle + " - " + newDescription);

                        // Recharger la page
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListReclamations.fxml"));
                        Parent root = loader.load();
                        Scene scene = new Scene(root);
                        Stage stage = (Stage) saveButton.getScene().getWindow();
                        stage.setScene(scene);
                        stage.show();

                        // Afficher un message de succès
                        showAlert(Alert.AlertType.INFORMATION, "Mise à jour réussie", "Réclamation mise à jour avec succès !");
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs !");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Réclamation introuvable !");
                }
            } catch (SQLException | IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de mettre à jour la réclamation.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une réclamation.");
        }
    }


    @FXML
    void switchToAccueil(ActionEvent event) {
        changeScene(event, "/Accueil.fxml");
    }

    @FXML
    void deconnexion(ActionEvent event) {
        changeScene(event, "/Connecter.fxml");
    }

    @FXML
    void VoirReponse(ActionEvent event) {
        changeScene(event, "/VoirReponse.fxml");
    }


    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void changeScene(ActionEvent event, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page.");
            e.printStackTrace();
        }
    }

    private void refreshScene(ActionEvent event) throws IOException {
        changeScene(event, "/ListReclamations.fxml");
    }
}
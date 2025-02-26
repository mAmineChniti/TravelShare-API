package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import tn.esprit.entities.Chambres;
import tn.esprit.entities.Hotels;
import tn.esprit.entities.SessionManager;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.ServiceChambre;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ChambreController {

    @FXML
    private VBox chambreContainer;

    @FXML
    private ScrollPane chambreScrollPane;

    @FXML
    private Button btnAdd;

    @FXML
    private VBox addChambreForm; // Formulaire d'ajout de chambre

    @FXML
    private TextField numeroField; // Champs du formulaire

    @FXML
    private TextField typeField;

    @FXML
    private TextField prixField;

    @FXML
    private TextField disponibleField;

    private Hotels selectedHotel;
    private final ServiceChambre serviceChambre = new ServiceChambre();

    // Méthode pour basculer vers l'accueil
    @FXML
    public void SwitchToAccueil(ActionEvent actionEvent) {
        switchScene(actionEvent, SessionManager.getInstance().getCurrentUtilisateur().getRole() == 1 ? "/AccueilAdmin.fxml" : "/Accueil.fxml");
    }

    // Méthode pour basculer vers la page des voyages
    @FXML
    public void SwitchToVoyages(ActionEvent actionEvent) {
        switchScene(actionEvent, "/Voyages.fxml");
    }

    // Méthode pour basculer vers la page des hôtels
    @FXML
    void SwitchToHotels(ActionEvent event) {
        switchScene(event, "/Hotel.fxml");
    }

    // Méthode pour basculer vers la page des posts
    @FXML
    public void SwitchToPosts(ActionEvent actionEvent) {
        switchScene(actionEvent, "/Posts.fxml");
    }

    // Méthode pour basculer vers le profil utilisateur
    @FXML
    void switchToProfile(ActionEvent event) {
        switchScene(event, "/ProfileUtilisateur.fxml");
    }

    // Méthode pour déconnecter l'utilisateur
    @FXML
    void deconnexion(ActionEvent event) {
        switchScene(event, "/Connecter.fxml");
    }

    // Méthode générique pour changer de scène
    private void switchScene(ActionEvent event, String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour définir l'hôtel sélectionné et charger les chambres
    public void setSelectedHotel(Hotels hotel) {
        this.selectedHotel = hotel;
        loadChambres();
    }

    // Méthode pour charger les chambres de l'hôtel sélectionné
    private void loadChambres() {
        try {
            List<Chambres> chambres = serviceChambre.ListByHotelId(selectedHotel.getHotel_id());
            chambreContainer.getChildren().clear();
            chambreContainer.setSpacing(15);
            chambreContainer.setPadding(new Insets(20));
            chambreContainer.setStyle("-fx-background-color: #f5f6fa;");

            Utilisateur currentUser = SessionManager.getInstance().getCurrentUtilisateur();
            boolean isAdmin = currentUser != null && currentUser.getRole() == 1;

            btnAdd.setVisible(isAdmin);

            for (Chambres chambre : chambres) {
                VBox card = createChambreCard(chambre, isAdmin);
                chambreContainer.getChildren().add(card);
            }

            chambreScrollPane.setContent(chambreContainer);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour créer une carte de chambre
    private VBox createChambreCard(Chambres chambre, boolean isAdmin) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
        card.setPadding(new Insets(15));
        card.setSpacing(10);

        HBox header = new HBox();
        header.setSpacing(20);
        Label numeroLabel = new Label("Chambre #" + chambre.getNumero_chambre());
        numeroLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #2d3436;");

        Label typeLabel = new Label(chambre.getType_enu());
        typeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #636e72; -fx-font-style: italic;");

        header.getChildren().addAll(numeroLabel, typeLabel);

        Label prixLabel = new Label(String.format("Prix/nuit: TND %.2f", chambre.getPrix_par_nuit()));
        prixLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #0984e3; -fx-font-weight: bold;");

        HBox statusBox = new HBox(10);
        Label statusDot = new Label("•");
        statusDot.setStyle("-fx-font-size: 24px;");
        statusDot.setTextFill(chambre.isDisponible() ? Color.valueOf("#00b894") : Color.valueOf("#d63031"));

        Label statusText = new Label(chambre.isDisponible() ? "Disponible" : "Non disponible");
        statusText.setStyle("-fx-font-size: 14px; -fx-text-fill: " + (chambre.isDisponible() ? "#00b894" : "#d63031") + ";");

        statusBox.getChildren().addAll(statusDot, statusText);

        HBox actionButtons = new HBox(10);
        if (isAdmin) {
            Button btnUpdate = new Button("Modifier");
            btnUpdate.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;");
            btnUpdate.setOnAction(e -> handleUpdateChambre(chambre));

            Button btnDelete = new Button("Supprimer");
            btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
            btnDelete.setOnAction(e -> handleDeleteChambre(chambre));

            actionButtons.getChildren().addAll(btnUpdate, btnDelete);
        }

        Button reserverBtn = new Button("Réserver maintenant");
        reserverBtn.setStyle("-fx-background-color: #0984e3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 8 20;");
        reserverBtn.setOnAction(e -> handleReservation(chambre));

        if (!chambre.isDisponible()) {
            reserverBtn.setDisable(true);
            reserverBtn.setStyle("-fx-background-color: #dfe6e9; -fx-text-fill: #636e72;");
        }

        card.getChildren().addAll(header, prixLabel, statusBox, reserverBtn);
        if (!actionButtons.getChildren().isEmpty()) {
            card.getChildren().add(actionButtons);
        }

        return card;
    }

    @FXML
    private void handleAddChambre() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddChambreForm.fxml"));
            Parent root = loader.load();

            AddChambreController controller = loader.getController();
            controller.setSelectedHotel(selectedHotel); // Pass the selected hotel to the form

            Stage stage = (Stage) btnAdd.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter une chambre");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour annuler l'ajout d'une chambre
    @FXML
    private void handleCancelAdd() {
        addChambreForm.setVisible(false); // Cacher le formulaire
        clearFormFields(); // Réinitialiser les champs
    }

    // Méthode pour confirmer l'ajout d'une chambre
    @FXML
    private void handleConfirmAdd() {
        String numero = numeroField.getText();
        String type = typeField.getText();
        String prix = prixField.getText();
        String disponible = disponibleField.getText();

        if (numero.isEmpty() || type.isEmpty() || prix.isEmpty() || disponible.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.", Alert.AlertType.ERROR);
            return;
        }

        double prixValue;
        try {
            prixValue = Double.parseDouble(prix);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le prix doit être un nombre valide.", Alert.AlertType.ERROR);
            return;
        }

        boolean disponibleValue;
        if (!disponible.equalsIgnoreCase("true") && !disponible.equalsIgnoreCase("false")) {
            showAlert("Erreur", "La disponibilité doit être 'true' ou 'false'.", Alert.AlertType.ERROR);
            return;
        }
        disponibleValue = Boolean.parseBoolean(disponible);

        Chambres newChambre = new Chambres(0, selectedHotel.getHotel_id(), numero, type, prixValue, disponibleValue);
        try {
            serviceChambre.add(newChambre);
            showAlert("Succès", "Chambre ajoutée avec succès.", Alert.AlertType.INFORMATION);
            loadChambres(); // Rafraîchir la liste des chambres
            addChambreForm.setVisible(false); // Cacher le formulaire
            clearFormFields(); // Réinitialiser les champs
        } catch (SQLException e) {
            showAlert("Erreur", "Une erreur s'est produite lors de l'ajout de la chambre : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Méthode pour réinitialiser les champs du formulaire
    private void clearFormFields() {
        numeroField.clear();
        typeField.clear();
        prixField.clear();
        disponibleField.clear();
    }

    // Méthode pour afficher une alerte
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthodes existantes pour la réservation, la mise à jour et la suppression des chambres
    private void handleReservation(Chambres chambre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReservationChambre.fxml"));
            Parent root = loader.load();

            ReservationChambreController controller = loader.getController();
            controller.setChambre(chambre);

            Stage stage = (Stage) chambreContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Réserver Chambre");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleUpdateChambre(Chambres chambre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateChambreForm.fxml"));
            Parent root = loader.load();

            UpdateChambreController controller = loader.getController();
            controller.setSelectedChambre(chambre);
            controller.setSelectedHotel(selectedHotel); // Pass the selected hotel

            Stage stage = (Stage) chambreContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteChambre(Chambres chambre) {
        try {
            serviceChambre.delete(chambre.getChambre_id());
            loadChambres();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
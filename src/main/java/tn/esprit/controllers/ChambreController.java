package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import javafx.scene.control.ScrollPane;

public class ChambreController {

    @FXML
    private VBox chambreContainer;

    @FXML
    private ScrollPane chambreScrollPane;  // Reference to the ScrollPane

    @FXML
    private Button btnAdd; // Bouton pour ajouter une chambre

    private Hotels selectedHotel;
    private final ServiceChambre serviceChambre = new ServiceChambre();

    public void setSelectedHotel(Hotels hotel) {
        this.selectedHotel = hotel;
        loadChambres();
    }

    private void loadChambres() {
        try {
            List<Chambres> chambres = serviceChambre.ListByHotelId(selectedHotel.getHotel_id());
            chambreContainer.getChildren().clear();
            chambreContainer.setSpacing(15);
            chambreContainer.setPadding(new Insets(20));
            chambreContainer.setStyle("-fx-background-color: #f5f6fa;");

            Utilisateur currentUser = SessionManager.getCurrentUtilisateur();
            boolean isAdmin = currentUser != null && currentUser.getRole() == 1;

            btnAdd.setVisible(isAdmin);

            for (Chambres chambre : chambres) {
                // Carte principale
                VBox card = new VBox();
                card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
                card.setPadding(new Insets(15));
                card.setSpacing(10);

                // En-tête
                HBox header = new HBox();
                header.setSpacing(20);
                Label numeroLabel = new Label("Chambre #" + chambre.getNumero_chambre());
                numeroLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #2d3436;");

                Label typeLabel = new Label(chambre.getType_enu());
                typeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #636e72; -fx-font-style: italic;");

                header.getChildren().addAll(numeroLabel, typeLabel);

                // Détails
                Label prixLabel = new Label(String.format("Prix/nuit: TND %.2f", chambre.getPrix_par_nuit()));
                prixLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #0984e3; -fx-font-weight: bold;");

                // Statut de disponibilité
                HBox statusBox = new HBox(10);
                Label statusDot = new Label("•");
                statusDot.setStyle("-fx-font-size: 24px;");
                statusDot.setTextFill(chambre.isDisponible() ? Color.valueOf("#00b894") : Color.valueOf("#d63031"));

                Label statusText = new Label(chambre.isDisponible() ? "Disponible" : "Non disponible");
                statusText.setStyle("-fx-font-size: 14px; -fx-text-fill: " + (chambre.isDisponible() ? "#00b894" : "#d63031") + ";");

                statusBox.getChildren().addAll(statusDot, statusText);

                // Boutons d'action (admin uniquement)
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

                // Bouton de réservation
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
                chambreContainer.getChildren().add(card);
            }

            // Set the VBox inside the ScrollPane for scrolling
            chambreScrollPane.setContent(chambreContainer);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    @FXML
    private void handleAddChambre() {
        try {
            // Charger le fichier FXML du formulaire d'ajout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddChambreForm.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur et passer l'hôtel sélectionné
            AddChambreController controller = loader.getController();
            controller.setSelectedHotel(selectedHotel);

            // Créer une nouvelle scène et une nouvelle fenêtre (pop-up)
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter une chambre");
            stage.showAndWait(); // Afficher la fenêtre et attendre sa fermeture

            // Rafraîchir la liste des chambres après l'ajout
            loadChambres();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'ouverture du formulaire d'ajout : " + e.getMessage());
        }
    }

    private void handleUpdateChambre(Chambres chambre) {
        try {
            // Charger le fichier FXML du formulaire de mise à jour
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateChambreForm.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur et passer la chambre sélectionnée
            UpdateChambreController controller = loader.getController();
            controller.setSelectedChambre(chambre);

            // Créer une nouvelle scène et une nouvelle fenêtre (pop-up)
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier la chambre");
            stage.showAndWait(); // Afficher la fenêtre et attendre sa fermeture

            // Rafraîchir la liste des chambres après la mise à jour
            loadChambres();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'ouverture du formulaire de mise à jour : " + e.getMessage());
        }
    }

    private void handleDeleteChambre(Chambres chambre) {
        try {
            // Appeler la méthode delete de ServiceChambre
            serviceChambre.delete(chambre.getChambre_id());
            System.out.println("Chambre supprimée : " + chambre.getNumero_chambre());

            // Rafraîchir la liste des chambres après la suppression
            loadChambres();
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'exception (par exemple, afficher un message d'erreur à l'utilisateur)
            System.err.println("Échec de la suppression de la chambre : " + e.getMessage());
        }
    }
}

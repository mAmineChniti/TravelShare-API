package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tn.esprit.entities.SessionManager;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.ServiceHotels;
import tn.esprit.entities.Hotels;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class HotelController {

    @FXML
    private VBox hotelContainer;
    @FXML
    private Button addHotelButton; // Button to add a new hotel (only for admin)

    private final ServiceHotels serviceHotels = new ServiceHotels();

    @FXML
    public void initialize() {
        loadHotels();
        Utilisateur currentUser = SessionManager.getInstance().getCurrentUtilisateur();
        // Show "Add Hotel" button only for admin users
        if (currentUser != null && currentUser.getRole()==1) {
            addHotelButton.setVisible(true);
            addHotelButton.setOnAction(event -> openAddHotelForm());
        } else {
            addHotelButton.setVisible(false);
        }
    }

    private void loadHotels() {
        try {
            List<Hotels> hotels = serviceHotels.ListAll();
            hotelContainer.getChildren().clear();
            hotelContainer.setSpacing(15);
            hotelContainer.setStyle("-fx-padding: 20;");

            for (Hotels hotel : hotels) {
                // Create hotel card container
                VBox card = new VBox();
                card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 15;");
                card.setSpacing(8);

                // Hotel name with stars
                Label nameLabel = new Label(hotel.getNom() + " ★★★★");
                nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                // Address
                Label addressLabel = new Label("Adresse: " + hotel.getAdress());
                addressLabel.setStyle("-fx-text-fill: #666666;");

                // Telephone
                Label phoneLabel = new Label("Téléphone: " + hotel.getTelephone());
                phoneLabel.setStyle("-fx-text-fill: #666666;");

                // Capacity
                Label capacityLabel = new Label("Capacité totale: " + hotel.getCapacite_totale() + " personnes");
                capacityLabel.setStyle("-fx-text-fill: #666666;");

                // Availability button
                Button availabilityButton = new Button("Voir disponibilité");
                availabilityButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
                availabilityButton.setOnAction(event -> navigateToChambre(hotel));

                // Create an HBox for buttons (only for admin)
                HBox adminButtons = new HBox(10);
                Utilisateur currentUser = SessionManager.getInstance().getCurrentUtilisateur();
                if (currentUser != null && currentUser.getRole()==1) {
                    Button updateButton = new Button("Modifier");
                    updateButton.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: white;");
                    updateButton.setOnAction(event -> updateHotel(hotel));

                    Button deleteButton = new Button("Supprimer");
                    deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                    deleteButton.setOnAction(event -> deleteHotel(hotel));

                    adminButtons.getChildren().addAll(updateButton, deleteButton);
                }

                // Add components to the card
                card.getChildren().addAll(
                        nameLabel,
                        addressLabel,
                        phoneLabel,
                        capacityLabel,
                        availabilityButton
                );

                if (!adminButtons.getChildren().isEmpty()) {
                    card.getChildren().add(adminButtons);
                }

                hotelContainer.getChildren().add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void navigateToChambre(Hotels hotel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Chambre.fxml"));
            Parent root = loader.load();

            // Pass selected hotel to ChambreController
            ChambreController chambreController = loader.getController();
            chambreController.setSelectedHotel(hotel);

            Stage stage = (Stage) hotelContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateHotel(Hotels hotel) {
        try {
            // Charger le fichier FXML du formulaire de mise à jour
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateHotelForm.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur et passer l'hôtel sélectionné
            UpdateHotelController controller = loader.getController();
            controller.setSelectedHotel(hotel);

            // Créer une nouvelle scène et une nouvelle fenêtre (pop-up)
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier l'hôtel");
            stage.showAndWait(); // Afficher la fenêtre et attendre sa fermeture

            // Rafraîchir la liste des hôtels après la mise à jour
            loadHotels();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'ouverture du formulaire de mise à jour : " + e.getMessage());
        }
    }

    private void deleteHotel(Hotels hotel) {
        try {
            // Call the delete method from ServiceHotels
            serviceHotels.delete(hotel.getHotel_id());
            System.out.println("Hotel deleted: " + hotel.getNom());

            // Refresh the hotel list to reflect the deletion
            loadHotels();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception (e.g., show an error message to the user)
            System.err.println("Failed to delete hotel: " + e.getMessage());
        }
    }

    private void openAddHotelForm() {
        try {
            // Charger le fichier FXML du formulaire d'ajout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddHotelForm.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène et une nouvelle fenêtre (pop-up)
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un hôtel");
            stage.showAndWait(); // Afficher la fenêtre et attendre sa fermeture

            // Rafraîchir la liste des hôtels après l'ajout
            loadHotels();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'ouverture du formulaire d'ajout : " + e.getMessage());
        }
    }
}
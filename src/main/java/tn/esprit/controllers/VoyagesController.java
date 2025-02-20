package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.OffreVoyages;
import tn.esprit.entities.SessionManager;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.OffreVoyageService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class VoyagesController {

    private final OffreVoyageService offreVoyageService = new OffreVoyageService();
    @FXML
    private GridPane gridPane;

    @FXML
    private javafx.scene.control.Button addVoyageButton;

    @FXML
    void switchToVoyageAddition(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddVoyage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        try {
            displayOffers();

            // Initialize the add voyage button
            addVoyageButton.setOnAction(this::switchToVoyageAddition);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void SwitchToPackages(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Voyages.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*public void SwitchToAccueil(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Accueil.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public void displayOffers() throws SQLException {
        gridPane.getChildren().clear(); // Clear the grid before reloading
        try {
            List<OffreVoyages> offers = offreVoyageService.ListAll();
            int column = 0;
            int row = 0;

            for (OffreVoyages offer : offers) {
                VBox card = createOfferCard(offer);

                // Add click event to navigate to the details page
                card.setOnMouseClicked(event -> {
                    try {
                        goToOfferDetails(event, offer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                if (column == 3) { // Adjust number of columns as needed
                    column = 0;
                    row++;
                }

                gridPane.add(card, column++, row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void goToOfferDetails(javafx.scene.input.MouseEvent event, OffreVoyages offer) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/OfferDetails.fxml"));
        Parent root = loader.load();

        // Pass the selected offer to the details controller
        OfferDetailsController detailsController = loader.getController();
        detailsController.setOfferDetails(offer);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private VBox createOfferCard(OffreVoyages offer) {
        VBox card = new VBox();
        card.setPadding(new Insets(10));
        card.setSpacing(8);
        card.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5; -fx-background-color: white; -fx-background-radius: 5;");
        card.setPrefWidth(250);

        Label title = new Label("Title: " + offer.getTitre());
        Label destination = new Label("Destination: " + offer.getDestination());
        Label description = new Label("Description: " + offer.getDescription());
        Label departureDate = new Label("Departure: " + offer.getDate_depart());
        Label returnDate = new Label("Return: " + offer.getDate_retour());
        Label price = new Label("Price: " + offer.getPrix() + " $");
        Label availableSeats = new Label("Seats: " + offer.getPlaces_disponibles());

        card.getChildren().addAll(title, destination, description, departureDate, returnDate, price, availableSeats);

        // Get the current user
        Utilisateur currentUser = SessionManager.getInstance().getCurrentUtilisateur();

        // Check if the user is an admin (role = 0)
        if (currentUser != null && currentUser.getRole() == 1) {
            // Add buttons for admin
            javafx.scene.control.Button updateButton = new javafx.scene.control.Button("Update");
            javafx.scene.control.Button deleteButton = new javafx.scene.control.Button("Delete");

            // Style buttons
            updateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-border-radius: 5;");
            deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-border-radius: 5;");

            // Set actions for buttons
            updateButton.setOnAction(event -> handleUpdateOffer(offer));
            deleteButton.setOnAction(event -> handleDeleteOffer(offer));

            card.getChildren().addAll(updateButton, deleteButton);
        }

        return card;
    }

    private void handleUpdateOffer(OffreVoyages offer) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateOfferForm.fxml"));
                Parent root = loader.load();

                // Pass the selected offer to the UpdateOfferFormController
                UpdateOfferFormController updateController = loader.getController();
                updateController.setOfferDetails(offer);

                // Open the update form in the current stage
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Update Offer");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        private void handleDeleteOffer(OffreVoyages offer) {
        try {
            // Call service to delete the offer
            offreVoyageService.delete(offer.getOffres_voyage_id());
            System.out.println("Offer deleted successfully: " + offer.getTitre());
            // Refresh the grid to reflect changes
            displayOffers();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
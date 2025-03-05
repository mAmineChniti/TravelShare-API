package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tn.esprit.entities.SessionManager;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.ServiceHotels;
import tn.esprit.entities.Hotels;

import java.io.ByteArrayInputStream;
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
        if (currentUser != null && currentUser.getRole() == 1) {
            addHotelButton.setVisible(true);
            addHotelButton.setOnAction(event -> openAddHotelForm());
        } else {
            addHotelButton.setVisible(false);
        }
    }

    // --- NAVIGATION METHODS ---

    @FXML
    public void SwitchToAccueil(ActionEvent actionEvent) {
        try {
            String AccueilLink = (SessionManager.getInstance().getCurrentUtilisateur().getRole() == 1)
                    ? "/AccueilAdmin.fxml"
                    : "/Accueil.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(AccueilLink));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void SwitchToVoyages(ActionEvent actionEvent) {
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

    @FXML
    void SwitchToHotels(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Hotel.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SwitchToPosts(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Posts.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void switchToProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfileUtilisateur.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
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


    private Image convertBytesToImage(byte[] imageData) {
        if (imageData == null || imageData.length == 0) {
            // Use your default image path here
            return new Image("src/main/resources/images/default-hotel.png");
        }
        return new Image(new ByteArrayInputStream(imageData));
    }

    void loadHotels() {
        try {
            List<Hotels> hotels = serviceHotels.ListAll();
            hotelContainer.getChildren().clear();
            hotelContainer.setSpacing(15);
            hotelContainer.setStyle("-fx-padding: 20;");

            for (Hotels hotel : hotels) {
                HBox card = new HBox();
                card.setSpacing(15);
                card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e0e0e0; "
                        + "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 15;");

                VBox textContainer = new VBox();
                textContainer.setSpacing(8);

                Label nameLabel = new Label(hotel.getNom() + " ★★★★");
                nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                Label addressLabel = new Label("Adresse: " + hotel.getAdress());
                addressLabel.setStyle("-fx-text-fill: #666666;");

                Label phoneLabel = new Label("Téléphone: " + hotel.getTelephone());
                phoneLabel.setStyle("-fx-text-fill: #666666;");

                Label capacityLabel = new Label("Capacité totale: " + hotel.getCapacite_totale() + " personnes");
                capacityLabel.setStyle("-fx-text-fill: #666666;");

                Button availabilityButton = new Button("Voir disponibilité");
                availabilityButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
                availabilityButton.setOnAction(event -> navigateToChambre(hotel));

                // Admin buttons (only if user is admin)
                HBox adminButtons = new HBox(10);
                Utilisateur currentUser = SessionManager.getInstance().getCurrentUtilisateur();
                if (currentUser != null && currentUser.getRole() == 1) {
                    Button updateButton = new Button("Modifier");
                    updateButton.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: white;");
                    updateButton.setOnAction(event -> updateHotel(hotel));

                    Button deleteButton = new Button("Supprimer");
                    deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                    deleteButton.setOnAction(event -> deleteHotel(hotel));

                    adminButtons.getChildren().addAll(updateButton, deleteButton);
                }

                textContainer.getChildren().addAll(
                        nameLabel,
                        addressLabel,
                        phoneLabel,
                        capacityLabel,
                        availabilityButton
                );
                if (!adminButtons.getChildren().isEmpty()) {
                    textContainer.getChildren().add(adminButtons);
                }

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                ImageView imageView = new ImageView();
                imageView.setFitWidth(200);
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(true);
                imageView.setImage(convertBytesToImage(hotel.getImage_h()));

                card.getChildren().addAll(textContainer, spacer, imageView);

                hotelContainer.getChildren().add(card);
            }
        } catch (Exception e) {
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateHotelForm.fxml"));
            Parent root = loader.load();

            UpdateHotelController controller = loader.getController();
            controller.setSelectedHotel(hotel);

            Stage stage = (Stage) hotelContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteHotel(Hotels hotel) {
        try {
            serviceHotels.delete(hotel.getHotel_id());
            System.out.println("Hotel deleted: " + hotel.getNom());
            // Refresh the hotel list
            loadHotels();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to delete hotel: " + e.getMessage());
        }
    }

    private void openAddHotelForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddHotelForm.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) hotelContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

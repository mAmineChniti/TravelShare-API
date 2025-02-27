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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.Excursions;
import tn.esprit.entities.Guides;
import tn.esprit.services.ServiceExcursion;
import tn.esprit.services.ServiceGuide;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ListExcursionGuideController {

    @FXML
    private VBox excursionContainer; // Conteneur principal des excursions

    private ServiceExcursion serviceExcursion = new ServiceExcursion();
    private ServiceGuide serviceGuide = new ServiceGuide();

    @FXML
    public void initialize() {
        try {
            List<Excursions> excursionsList = serviceExcursion.ListAll(); // Récupérer la liste des excursions

            if (excursionsList.isEmpty()) {
                excursionContainer.getChildren().clear();
                excursionContainer.getChildren().add(new Label("Aucune excursion disponible."));
                return;
            }

            for (Excursions excursion : excursionsList) {
                // Créer un conteneur pour chaque excursion
                AnchorPane excursionItemPane = new AnchorPane();
                excursionItemPane.setPrefHeight(120);
                excursionItemPane.setPrefWidth(580);
                excursionItemPane.setStyle("-fx-background-color: #F0F0F0; -fx-padding: 10; -fx-border-radius: 10; -fx-border-color: #B0B0B0;");

                // Récupérer et afficher l'image de l'excursion
                String imagePath = excursion.getImage();
                ImageView imageView = new ImageView();
                if (imagePath != null && !imagePath.isEmpty()) {
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        Image image = new Image(imageFile.toURI().toString());
                        imageView.setImage(image);
                        imageView.setFitHeight(80);
                        imageView.setFitWidth(80);
                        imageView.setLayoutX(10);
                        imageView.setLayoutY(10);
                        excursionItemPane.getChildren().add(imageView);
                    }
                }

                // Ajouter les labels avec les informations de l'excursion
                Label titleLabel = new Label(excursion.getTitle());
                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                titleLabel.setLayoutX(100);
                titleLabel.setLayoutY(10);

                Label descriptionLabel = new Label(excursion.getDescription());
                descriptionLabel.setLayoutX(100);
                descriptionLabel.setLayoutY(35);

                Label dateDLabel = new Label("Début: " + excursion.getDate_excursion());
                dateDLabel.setLayoutX(100);
                dateDLabel.setLayoutY(60);

                Label dateFLabel = new Label("Fin: " + excursion.getDate_fin());
                dateFLabel.setLayoutX(100);
                dateFLabel.setLayoutY(85);

                // Récupérer le guide associé à l'excursion
                Guides guide = serviceGuide.getGuideById(excursion.getGuide_id());
                Label guideLabel = new Label("Guide: " + (guide != null ? guide.getName() : "Non assigné"));
                guideLabel.setLayoutX(100);
                guideLabel.setLayoutY(110);

                // **Ajout du bouton "Réserver"**
                Button reserverButton = new Button("Réserver");
                reserverButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5;");
                reserverButton.setLayoutX(450); // Positionnement à droite
                reserverButton.setLayoutY(60); // Centrage vertical
                reserverButton.setOnAction(event -> handleReservation(event, excursion));

                // Ajouter les éléments au panneau de l'excursion
                excursionItemPane.getChildren().addAll(titleLabel, descriptionLabel, dateDLabel, dateFLabel, guideLabel, reserverButton);

                // Ajouter le panneau à la VBox
                excursionContainer.getChildren().add(excursionItemPane);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'initialisation du contrôleur.");
            e.printStackTrace();
        }
    }

    /**
     * Méthode pour gérer la réservation et rediriger vers la page de paiement.
     */
    private void handleReservation(ActionEvent event, Excursions excursion) {
        try {
            // Charger la scène de paiement
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PaymentExcursion.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de la page PaymentExcursion
            PaymentExcursionController paymentController = loader.getController();

            // Envoyer les données de l'excursion sélectionnée
            paymentController.setExcursionData(excursion);

            // Changer la scène
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

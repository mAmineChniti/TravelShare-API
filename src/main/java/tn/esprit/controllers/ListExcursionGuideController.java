package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class ListExcursionGuideController {

    @FXML
    private VBox excursionContainer; // Conteneur principal des excursions

    @FXML
    private TextField searchField; // Champ de recherche

    private ServiceExcursion serviceExcursion = new ServiceExcursion();
    private ServiceGuide serviceGuide = new ServiceGuide();
    private static List<Excursions> favoriteExcursions = new ArrayList<>();

    private void saveFavorites() {
        try {
            Gson gson = new Gson();
            FileWriter writer = new FileWriter("favorites.json");
            gson.toJson(favoriteExcursions, writer); // Sauvegarder la liste des favoris
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void initialize() {
        loadExcursionsList("");  // Charger toutes les excursions au début
    }

    @FXML
    private void onSearch() {
        String searchQuery = searchField.getText(); // Récupérer le texte de recherche
        loadExcursionsList(searchQuery); // Charger les excursions filtrées
    }

    private void loadExcursionsList(String searchQuery) {
        try {
            List<Excursions> excursionsList = serviceExcursion.search(searchQuery); // Recherche basée sur la chaîne

            // Vider le conteneur avant de le remplir avec de nouvelles données
            excursionContainer.getChildren().clear();

            if (excursionsList.isEmpty()) {
                excursionContainer.getChildren().add(new Label("Aucune excursion trouvée."));
            } else {
                for (Excursions excursion : excursionsList) {
                    addExcursionToView(excursion); // Ajouter chaque excursion à l'affichage
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des excursions.");
            e.printStackTrace();
        }
    }

    private void addExcursionToView(Excursions excursion) {
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
        Guides guide = null;
        try {
            guide = serviceGuide.getGuideById(excursion.getGuide_id());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Label guideLabel = new Label("Guide: " + (guide != null ? guide.getName() : "Non assigné"));
        guideLabel.setLayoutX(100);
        guideLabel.setLayoutY(110);

        Label prixLabel = new Label("Prix: " + excursion.getPrix() + " TND");
        prixLabel.setLayoutX(100);
        prixLabel.setLayoutY(135);

        // Ajouter le bouton "Réserver"
        Button reserverButton = new Button("Réserver");
        reserverButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5;");
        reserverButton.setLayoutX(450);
        reserverButton.setLayoutY(60);
        reserverButton.setOnAction(event -> handleReservation(event, excursion));

        // Bouton Favori (Étoile)
        Button favoriteButton = new Button("⭐");
        favoriteButton.setStyle("-fx-background-color: transparent; -fx-font-size: 30px;");
        favoriteButton.setLayoutX(20);
        favoriteButton.setLayoutY(80);
        favoriteButton.setOnAction(event -> addToFavorites(excursion));

        // Ajouter les éléments au panneau de l'excursion
        excursionItemPane.getChildren().addAll(titleLabel, descriptionLabel, dateDLabel, dateFLabel, guideLabel, prixLabel, reserverButton, favoriteButton);

        // Ajouter le panneau à la VBox
        excursionContainer.getChildren().add(excursionItemPane);
    }

    private void addToFavorites(Excursions excursion) {
        if (!favoriteExcursions.contains(excursion)) {
            favoriteExcursions.add(excursion);
            System.out.println("Ajouté aux favoris : " + excursion.getTitle());
            saveFavorites(); // Sauvegarder les favoris après ajout
        } else {
            System.out.println("Déjà dans les favoris !");
        }
    }



    private void handleReservation(ActionEvent event, Excursions excursion) {
        try {
            // Créez un objet avec l'excursion (peut être un objet ou une valeur comme prix)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PaymentExcursion.fxml"));
            Parent root = loader.load();

            // Passer les informations de l'excursion au contrôleur de la page de paiement
            PaymentExcursionController paymentController = loader.getController();
            paymentController.setExcursion(excursion); // Méthode à définir dans PaymentExcursionController

            // Changer la scène
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static List<Excursions> getFavoriteExcursions() {
        return favoriteExcursions;
    }



    @FXML
    void goToFavorisPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListFavoris.fxml"));
            Parent root = loader.load();

            // Changer la scène
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void SwitchToAcceuil(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Acceuil.fxml"));
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
    void SwitchToProfile(ActionEvent event) {
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
    void déconnexion(ActionEvent event) {
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
    void SwitchToExcursions(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListExcursionGuide.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

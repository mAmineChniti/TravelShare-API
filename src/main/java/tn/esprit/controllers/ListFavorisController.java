package tn.esprit.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ListFavorisController {

    @FXML
    private VBox favorisContainer;
    private static List<Excursions> favoriteExcursions = ListExcursionGuideController.getFavoriteExcursions();

    @FXML
    public void initialize() {
        loadFavorites();  // Charger les favoris depuis le fichier
        if (favoriteExcursions != null) {
            for (Excursions excursion : favoriteExcursions) {
                addExcursionToView(excursion);  // Ajouter chaque excursion favorite à la vue
            }
        }
    }

    private void loadFavorites() {
        try {
            Gson gson = new Gson();
            FileReader reader = new FileReader("favorites.json");
            Type listType = new TypeToken<List<Excursions>>(){}.getType();
            List<Excursions> loadedFavorites = gson.fromJson(reader, listType);
            reader.close();
            if (loadedFavorites != null) {
                favoriteExcursions = loadedFavorites;  // Mettre à jour la liste des favoris
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void addExcursionToView(Excursions excursion) {
        // Créer une nouvelle instance d'AnchorPane pour chaque excursion
        AnchorPane excursionItemPane = new AnchorPane();
        excursionItemPane.setPrefHeight(120);
        excursionItemPane.setPrefWidth(580);
        excursionItemPane.setStyle("-fx-background-color: #F0F0F0; -fx-padding: 10; -fx-border-radius: 10; -fx-border-color: #B0B0B0;");

        // Créer des labels pour afficher les informations de l'excursion
        Label titleLabel = new Label(excursion.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        titleLabel.setLayoutX(100);
        titleLabel.setLayoutY(10);

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

        // Ajouter le label au panneau de l'excursion
        excursionItemPane.getChildren().add(titleLabel);

        // Ajouter l'excursion au conteneur
        favorisContainer.getChildren().add(excursionItemPane);

        // Ajouter un bouton pour supprimer l'excursion des favoris
        Button removeButton = new Button("Supprimer");
        removeButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        removeButton.setLayoutX(450);
        removeButton.setLayoutY(80);
        removeButton.setOnAction(event -> removeFromFavorites(excursion));

        // Ajouter le bouton de suppression au panneau de l'excursion
        excursionItemPane.getChildren().add(removeButton);
    }

    private void removeFromFavorites(Excursions excursion) {

    }

    private void saveFavorites() {
    }


}

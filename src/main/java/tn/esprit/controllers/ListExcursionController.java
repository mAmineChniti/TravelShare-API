package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.Excursions;
import tn.esprit.services.ServiceExcursion;
import tn.esprit.services.ServiceGuide;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ListExcursionController {


    @FXML
    private VBox excursionContainer; // Conteneur principal des excursions

    private ServiceExcursion serviceExcursion = new ServiceExcursion();
    private ServiceGuide serviceGuide = new ServiceGuide();

    @FXML
    void initialize() {
        List<Excursions> excursionsList;
        try {
            excursionsList = serviceExcursion.ListAll();
        } catch (SQLException e) {
            System.err.println("Error retrieving excursions.");
            e.printStackTrace();
            return;
        }

        if (excursionsList.isEmpty()) {
            excursionContainer.getChildren().add(new Label("No excursions available."));
            return;
        }

        for (Excursions excursion : excursionsList) {
            // Créer un conteneur pour chaque excursion
            AnchorPane excursionItemPane = new AnchorPane();
            excursionItemPane.setPrefHeight(150);  // Hauteur ajustée pour inclure le prix
            excursionItemPane.setPrefWidth(580);
            excursionItemPane.setStyle("-fx-background-color: #F0F0F0; -fx-padding: 10; -fx-border-radius: 10; -fx-border-color: #B0B0B0;");

            // Ajouter l'image de l'excursion si elle existe
            String imagePath = excursion.getImage();
            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    ImageView imageView = new ImageView(imageFile.toURI().toString());
                    imageView.setFitHeight(80);
                    imageView.setFitWidth(80);
                    imageView.setLayoutX(10);
                    imageView.setLayoutY(20);
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

            Label dateDLabel = new Label("Début : " + excursion.getDate_excursion());
            dateDLabel.setLayoutX(100);
            dateDLabel.setLayoutY(60);

            Label dateFLabel = new Label("Fin : " + excursion.getDate_fin());
            dateFLabel.setLayoutX(100);
            dateFLabel.setLayoutY(85);

            Label prixLabel = new Label("Prix : " + excursion.getPrix() +" TND");
            prixLabel.setLayoutX(10);
            prixLabel.setLayoutY(110);


            // Ajouter un bouton "Supprimer" et "Mettre à jour"
            Button btnDelete = new Button("Delete");
            btnDelete.setOnAction(event -> deleteExcursion(event, excursion));
            btnDelete.setStyle("-fx-background-color: #E63946;" // Rouge corail
                    + "-fx-text-fill: white;"
                    + "-fx-font-weight: bold;"
                    + "-fx-border-radius: 5px;");
            btnDelete.setLayoutX(130);
            btnDelete.setLayoutY(120);

            Button btnUpdate = new Button("Update");
            btnUpdate.setOnAction(event -> updateExcursion(event, excursion));
            btnUpdate.setStyle("-fx-background-color: #457B9D;" // Bleu acier
                    + "-fx-text-fill: white;"
                    + "-fx-font-weight: bold;"
                    + "-fx-border-radius: 5px;");
            btnUpdate.setLayoutX(200);
            btnUpdate.setLayoutY(120);

            // Ajouter les éléments à l'AnchorPane
            excursionItemPane.getChildren().addAll(titleLabel, descriptionLabel, dateDLabel, dateFLabel, prixLabel, btnDelete, btnUpdate);

            // Ajouter l'AnchorPane dans le conteneur principal
            excursionContainer.getChildren().add(excursionItemPane);
        }
    }


    private void deleteExcursion(ActionEvent event, Excursions excursion) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Delete Excursion");
        alert.setContentText("Are you sure you want to delete this excursion?");

        alert.showAndWait().ifPresent(result -> {
            if (result.getText().equals("OK")) {
                try {
                    serviceExcursion.delete(excursion.getExcursion_id());
                    refreshGrid();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateExcursion(ActionEvent event, Excursions excursion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateExcursion.fxml"));
            Parent root = loader.load();
            UpdateExcursionController controller = loader.getController();
            controller.setExcursionToEdit(excursion);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshGrid() {
        excursionContainer.getChildren().clear();
        initialize();
    }

    // Méthode pour ajouter une excursion
    @FXML
    public void ajouterExcursion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddExcursion.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Page loading error FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }



    @FXML
    public void SwitchToAcceuil(ActionEvent actionEvent){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AcceuilAdmin.fxml"));
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
    void SwitchToReclamations(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReponseAdmin.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListExcursion.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void SwitchToGuides(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListGuide.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
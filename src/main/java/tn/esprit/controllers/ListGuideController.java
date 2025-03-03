package tn.esprit.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.Guides;
import tn.esprit.services.ServiceGuide;

public class ListGuideController {

    @FXML
    private VBox guideContainer; // Conteneur principal des excursions

    private ServiceGuide serviceGuide = new ServiceGuide();

    @FXML
    void initialize() {
        List<Guides> guidesList;
        try {
            guidesList = serviceGuide.ListAll();
        } catch (SQLException e) {
            System.err.println("Error retrieving guides.");
            e.printStackTrace();
            return;
        }

        if (guidesList.isEmpty()) {
            guideContainer.getChildren().add(new Label("No guides available."));
            return;
        }

        for (Guides guide : guidesList) {
            // Créer un conteneur pour chaque excursion
            AnchorPane guideItemPane = new AnchorPane();
            guideItemPane.setPrefHeight(150);  // Hauteur ajustée pour inclure le prix
            guideItemPane.setPrefWidth(300);
            guideItemPane.setStyle("-fx-background-color: #F0F0F0; -fx-padding: 10; -fx-border-radius: 10; -fx-border-color: #B0B0B0;");


            Label nameLabel1 = new Label("Name : ");
            nameLabel1.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            nameLabel1.setLayoutX(10);
            nameLabel1.setLayoutY(10);
            // Ajouter les labels avec les informations de l'excursion
            Label nameLabel = new Label(guide.getName());
            nameLabel.setLayoutX(60);
            nameLabel.setLayoutY(12);

            Label lastnameLabel1 = new Label("Lastname : ");
            lastnameLabel1.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            lastnameLabel1.setLayoutX(10);
            lastnameLabel1.setLayoutY(35);

            Label lastnameLabel = new Label(guide.getLastname());
            lastnameLabel.setLayoutX(82);
            lastnameLabel.setLayoutY(38);


            Label emailLabel1 = new Label("Email : ");
            emailLabel1.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            emailLabel1.setLayoutX(10);
            emailLabel1.setLayoutY(60);

            Label emailLabel = new Label(guide.getEmail());
            emailLabel.setLayoutX(58);
            emailLabel.setLayoutY(62);

            Label experienceLabel1 = new Label("Experience");
            experienceLabel1.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            experienceLabel1.setLayoutX(10);
            experienceLabel1.setLayoutY(85);

            Label experienceLabel = new Label(" : " + guide.getExperience());
            experienceLabel.setLayoutX(80);
            experienceLabel.setLayoutY(87);

            Label phoneLabel1 = new Label("Phone : ");
            phoneLabel1.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            phoneLabel1.setLayoutX(10);
            phoneLabel1.setLayoutY(110);

            Label phoneLabel = new Label(guide.getPhone_num());
            phoneLabel.setLayoutX(61);
            phoneLabel.setLayoutY(112);

            Label languageLabel1 = new Label("Language : ");
            languageLabel1.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            languageLabel1.setLayoutX(10);
            languageLabel1.setLayoutY(135);

            Label languageLabel = new Label(guide.getLanguage());
            languageLabel.setLayoutX(84);
            languageLabel.setLayoutY(137);

            // Ajouter un bouton "Supprimer" et "Mettre à jour"
            Button btnDelete = new Button("Delete");
            btnDelete.setOnAction(event -> deleteGuide(event, guide));
            btnDelete.setStyle("-fx-background-color: #E63946;" // Rouge corail
                    + "-fx-text-fill: white;"
                    + "-fx-font-weight: bold;"
                    + "-fx-border-radius: 5px;");
            btnDelete.setLayoutX(200);
            btnDelete.setLayoutY(120);

            Button btnUpdate = new Button("Update");
            btnUpdate.setOnAction(event -> updateGuide(event, guide));
            btnUpdate.setStyle("-fx-background-color: #457B9D;" // Bleu acier
                    + "-fx-text-fill: white;"
                    + "-fx-font-weight: bold;"
                    + "-fx-border-radius: 5px;");
            btnUpdate.setLayoutX(300);
            btnUpdate.setLayoutY(120);





            // Ajouter les éléments à l'AnchorPane
            guideItemPane.getChildren().addAll(nameLabel1,nameLabel, lastnameLabel,lastnameLabel1, emailLabel,emailLabel1, experienceLabel,experienceLabel1, phoneLabel,phoneLabel1,languageLabel,languageLabel1,btnDelete,btnUpdate);

            // Ajouter l'AnchorPane dans le conteneur principal
            guideContainer.getChildren().add(guideItemPane);


        }
    }

    private void deleteGuide(ActionEvent event, Guides guide) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Delete Guide");
        alert.setContentText("Are you sure you want to delete this guide?");

        alert.showAndWait().ifPresent(result -> {
            if (result.getText().equals("OK")) {
                try {
                    serviceGuide.delete(guide.getGuide_id());
                    refreshGrid();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateGuide(ActionEvent event, Guides guide) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateGuide.fxml"));
            Parent root = loader.load();
            UpdateGuideController controller = loader.getController();
            controller.setGuideToEdit(guide);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshGrid() {
        guideContainer.getChildren().clear();
        initialize();
    }

    @FXML
    public void ajouterGuide(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddGuide.fxml"));
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
    public void SwitchtoVoyages(ActionEvent actionEvent) {
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
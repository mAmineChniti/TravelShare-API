package tn.esprit.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import tn.esprit.entities.Guides;
import tn.esprit.services.ServiceGuide;

public class ListGuideController {

    @FXML
    private GridPane gridPane;

    private ServiceGuide serviceGuide = new ServiceGuide();

    @FXML
    void initialize() {
        assert gridPane != null : "fx:id=\"gridPane\" was not injected: check your FXML file 'ListGuide.fxml'.";

        List<Guides> guidesList = null;
        try {
            // Récupérer la liste des guides
            guidesList = serviceGuide.ListAll();
        } catch (SQLException e) {
            System.err.println("Error retrieving guides from database.");
            e.printStackTrace();
            return;
        }

        if (guidesList.isEmpty()) {
            gridPane.add(new Label("No guide available."), 0, 0);
            return;
        }

        // Afficher les guides dans le GridPane
        int row = 0;
        for (Guides guide : guidesList) {
            gridPane.add(new Label(guide.getName()), 0, row);
            gridPane.add(new Label(guide.getLastname()), 1, row);
            gridPane.add(new Label(guide.getEmail()), 2, row);
            gridPane.add(new Label(String.valueOf(guide.getExperience())), 3, row);
            gridPane.add(new Label(guide.getPhone_num()), 4, row);
            gridPane.add(new Label(guide.getLanguage()), 5, row);

            javafx.scene.control.Button btnDelete = new javafx.scene.control.Button("X");
            btnDelete.setOnAction(event -> {
                // Créer une boîte de dialogue de confirmation
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Deletion confirmation");
                alert.setHeaderText(null);
                alert.setContentText("You are about to delete your profile. Are you sure?");

                // Attendre la réponse de l'utilisateur
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    try {
                        // Supprimer d'abord les excursions associées
                        serviceGuide.deleteExcursionsByGuide(guide.getGuide_id());

                        // Ensuite, supprimer le guide
                        serviceGuide.delete(guide.getGuide_id());

                        // Trouver l'index de la ligne du bouton cliqué
                        Integer rowIndex = GridPane.getRowIndex(btnDelete);
                        if (rowIndex != null) {
                            // Supprimer tous les nœuds de cette ligne
                            gridPane.getChildren().removeIf(node ->
                                    GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node).equals(rowIndex)
                            );
                        }

                    } catch (SQLException e) {
                        System.err.println("Error deleting guide.");
                        e.printStackTrace();
                    }
                }
            });


            //button update
            Button updateButton = new Button("!");
            updateButton.setOnAction(event -> {
                // Créer une boîte de dialogue de confirmation
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Change confirmation");
                alert.setHeaderText(null);
                alert.setContentText("You are about to edit your profile. Do you want to continue?");

                // Attendre la réponse de l'utilisateur
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateGuide.fxml"));
                        Parent root = loader.load();

                        // Obtenir le contrôleur de la page UpdateGuide
                        UpdateGuideController updateController = loader.getController();

                        // Passer les informations du guide sélectionné
                        updateController.setGuideToEdit(guide);

                        // Charger la nouvelle scène
                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            gridPane.add(btnDelete, 6, row);
            gridPane.add(updateButton, 7, row);

            row++;
        }
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
    void goToExcursion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListExcursion.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }

    }

    @FXML
    void goToHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

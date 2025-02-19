package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import tn.esprit.entities.Excursions;
import tn.esprit.entities.Guides;
import tn.esprit.services.ServiceExcursion;
import tn.esprit.services.ServiceGuide;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ListExcursionController {

    @FXML
    private GridPane gridPane;

    private ServiceExcursion serviceExcursion = new ServiceExcursion();
    private ServiceGuide serviceGuide = new ServiceGuide();  // Ajout du ServiceGuide

    @FXML
    void initialize() {
        assert gridPane != null : "fx:id=\"gridPane\" was not injected: check your FXML file 'ListExcursion.fxml'.";

        List<Excursions> excursionsList = null;
        try {
            // Récupérer la liste des excursions
            excursionsList = serviceExcursion.ListAll();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des excursions depuis la base de données.");
            e.printStackTrace();
            return;
        }

        if (excursionsList.isEmpty()) {
            gridPane.add(new Label("Aucune excursion disponible."), 0, 0);
            return;
        }

        // Afficher les excursions dans le GridPane
        int row = 0;
        for (Excursions excursion : excursionsList) {
            gridPane.add(new Label(excursion.getTitle()), 0, row);
            gridPane.add(new Label(" " + excursion.getDescription()), 1, row);
            gridPane.add(new Label(String.valueOf("  " + excursion.getDuration()) + " jours"), 2, row);
            gridPane.add(new Label(String.valueOf(excursion.getDate_excursion())), 3, row);


            // Bouton Supprimer
            javafx.scene.control.Button btnDelete = new javafx.scene.control.Button("X");
            btnDelete.setOnAction(event -> {
                try {
                    // Ensuite, supprimer l'excursion
                    serviceExcursion.delete(excursion.getExcursion_id());

                    // Trouver l'index de la ligne du bouton cliqué
                    Integer rowIndex = GridPane.getRowIndex(btnDelete);
                    if (rowIndex != null) {
                        // Supprimer tous les nœuds de cette ligne
                        gridPane.getChildren().removeIf(node ->
                                GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node).equals(rowIndex)
                        );
                    }

                } catch (SQLException e) {
                    System.err.println("Erreur lors de la suppression du guide.");
                    e.printStackTrace();
                }
            });

            //button update
            Button updateButton = new Button("!");
            updateButton.setOnAction(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateGuide.fxml"));
                    Parent root = loader.load();

                    // Obtenir le contrôleur de la page UpdateGuide
                    UpdateExcursionController updateController = loader.getController();

                    // Passer les informations du guide sélectionné
                    updateController.setExcursionToEdit(excursion);

                    // Charger la nouvelle scène
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // Utiliser l'ID du guide pour récupérer l'objet Guide
            try {
                Guides guide = serviceGuide.getGuideById(excursion.getGuide_id()); // Utilisation de getGuideById
                String guideName = (guide != null) ? guide.getName() : "Guide inconnu";
                gridPane.add(new Label(guideName), 4, row);
            } catch (SQLException e) {
                gridPane.add(new Label("Erreur guide"), 4, row);
                e.printStackTrace();
            }

            gridPane.add(btnDelete, 6, row);
            gridPane.add(updateButton, 7, row);
            row++;
        }
    }



    @FXML
    public void ajouterExcursion(ActionEvent event) {
        try {
            // Charger le fichier FXML pour la nouvelle page
            Parent root = FXMLLoader.load(getClass().getResource("/AddExcursion.fxml"));

            // Remplacer la racine de la scène actuelle par celle de la nouvelle page
            ((Node) event.getSource()).getScene().setRoot(root);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    @FXML
    void goToGuide(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListGuide.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de l'FXML: " + e.getMessage());
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
            System.err.println("Erreur lors du chargement de l'FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

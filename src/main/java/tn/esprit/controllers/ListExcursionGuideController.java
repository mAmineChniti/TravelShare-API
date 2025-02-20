package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import tn.esprit.entities.Excursions;
import tn.esprit.entities.Guides;
import tn.esprit.services.ServiceExcursion;
import tn.esprit.services.ServiceGuide;

import java.sql.SQLException;
import java.util.List;

public class ListExcursionGuideController {

    @FXML
    private GridPane gridPane;

    private ServiceExcursion serviceExcursion = new ServiceExcursion();
    private ServiceGuide serviceGuide = new ServiceGuide();

    @FXML
    public void initialize() {
        try {
            assert gridPane != null : "fx:id=\"gridPane\" was not injected: check your FXML file 'ListExcursion.fxml'.";

            List<Excursions> excursionsList = null;
            try {
                // Récupérer la liste des excursions
                excursionsList = serviceExcursion.ListAll();
            } catch (SQLException e) {
                System.err.println("Error retrieving excursions from database.");
                e.printStackTrace();
                return;
            }

            if (excursionsList.isEmpty()) {
                gridPane.add(new Label("No excursions available."), 0, 0);
                return;
            }

            // Afficher les excursions dans le GridPane
            int row = 0;
            for (Excursions excursion : excursionsList) {
                gridPane.add(new Label(excursion.getTitle()), 0, row);
                gridPane.add(new Label(" " + excursion.getDescription()), 1, row);
                gridPane.add(new Label(String.valueOf("  " + excursion.getDuration()) + " days"), 2, row);
                gridPane.add(new Label(String.valueOf(excursion.getDate_excursion())), 3, row);

                // Récupérer le guide associé à l'excursion
                Guides guide = serviceGuide.getGuideById(excursion.getGuide_id());
                gridPane.add(new Label(guide != null ? guide.getName() : "No guide"), 4, row);

                // Ajouter un bouton pour réserver l'excursion
                Button reserveButton = new Button("Reserve");
                reserveButton.setOnAction(e -> handleReserveExcursion(excursion, guide));
                gridPane.add(reserveButton, 5, row);

                row++;
            }

        } catch (SQLException e) {
            System.err.println("Error initializing the controller.");
            e.printStackTrace();
        }
    }

    private void handleReserveExcursion(Excursions excursion, Guides guide) {
        // Vérifier si un guide est disponible pour cette excursion
        if (guide != null) {
            // Créer l'alerte avec le numéro du guide
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Guide Information");
            alert.setHeaderText("Guide for " + excursion.getTitle());
            alert.setContentText("The guide's phone number is: " + guide.getPhone_num());

            // Afficher l'alerte
            alert.showAndWait();
        } else {
            // Si aucun guide n'est associé, afficher un message
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("No Guide Available");
            alert.setHeaderText("No guide for this excursion");
            alert.setContentText("This excursion has no guide assigned.");
            alert.showAndWait();
        }
    }
}

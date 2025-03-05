package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import tn.esprit.services.ServiceReclamation;
import tn.esprit.services.ServiceReponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class VoirReponseController {

    @FXML
    private ListView<String> listReponse;


    ServiceReclamation serviceReclamation = new ServiceReclamation();
    ServiceReponse serviceReponse = new ServiceReponse();


    public void initialize() {
        System.out.println("initialize called");

        try {
            List<String> reponses = serviceReponse.getReponsesWithUserInfoAndRec();

            if (!reponses.isEmpty()) {
                listReponse.getItems().addAll(reponses);
            } else {
                listReponse.getItems().add("Aucune réponse disponible.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            listReponse.getItems().add("Erreur lors du chargement des réponses.");
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

    @FXML
    void switchToAccueil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Accueil.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void retour(Event event, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page.");
            e.printStackTrace();
        }
    }

    @FXML
    void retourpage(MouseEvent event) {
        retour(event, "/ListReponse.fxml");

    }
}

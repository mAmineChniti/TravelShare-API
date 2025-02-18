package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ConnecterController {

    @FXML
    private Button btnsignup;

    @FXML
    private AnchorPane layer1;

    @FXML
    private AnchorPane layer2;

    @FXML
    private Label u1;

    @FXML
    private Label u2;

    @FXML
    void btnsignup(ActionEvent event) {

    }

    @FXML
    void switchToSignUp(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Inscrire.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();  // Affiche l'erreur dans la console
        }

    }

}


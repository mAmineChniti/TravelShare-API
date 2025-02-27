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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.Excursions;

import java.io.IOException;

public class PaymentExcursionController {

    @FXML
    private Label lblExcursionName;
    @FXML
    private Label lblExcursionDescription;
    @FXML
    private Label lblExcursionPrice;
    @FXML
    private TextField cardNumberField;
    @FXML
    private TextField cardHolderField;
    @FXML
    private TextField cardCvcField;
    @FXML
    private Button btnPayer;

    private Excursions excursion;

    public void setExcursionData(Excursions excursion) {
        this.excursion = excursion;
        lblExcursionName.setText(excursion.getTitle());
        lblExcursionDescription.setText(excursion.getDescription());
        lblExcursionPrice.setText(String.valueOf(excursion.getPrix()));
    }

    @FXML
    public void initialize() {
        btnPayer.setOnAction(event -> handlePayment());
    }

    private void handlePayment() {
        // Logique pour simuler un paiement (par exemple, un appel API ou une logique de validation)
        System.out.println("Paiement effectué pour : " + excursion.getTitle());
        System.out.println("Numéro de carte : " + cardNumberField.getText());
        System.out.println("Titulaire : " + cardHolderField.getText());
        System.out.println("CVC : " + cardCvcField.getText());

        // Ajouter la logique pour traiter les paiements (par exemple, via une API ou une base de données)
        // Afficher un message ou effectuer une redirection vers une confirmation de paiement, etc.
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListExcursionGuide.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Page loading error FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

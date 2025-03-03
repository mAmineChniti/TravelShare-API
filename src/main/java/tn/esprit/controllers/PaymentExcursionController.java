package tn.esprit.controllers;

import tn.esprit.entities.Excursions;
import tn.esprit.services.PaymentService;
import com.stripe.model.PaymentIntent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class PaymentExcursionController {


    @FXML
    private Button btnPayer;
    @FXML
    private WebView webView; // Déclarez un WebView dans votre fichier FXML

    private final PaymentService paymentService = new PaymentService();
    private Excursions excursion; // L'excursion passée depuis la page précédente

    @FXML
    public void initialize() {
        // Charger la page HTML dans le WebView
        WebEngine webEngine = webView.getEngine();
        try {
            webEngine.load(getClass().getResource("/payment_form.html").toExternalForm()); // Charger le fichier HTML
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de chargement");
            alert.setHeaderText("Le fichier HTML n'a pas pu être chargé");
            alert.setContentText("Détails de l'erreur: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void setExcursion(Excursions excursion) {
        this.excursion = excursion;
    }

    @FXML
    public void onPayButtonClick(ActionEvent event) {
        try {
            // Montant de l'excursion (en centimes)
            long amount = (long) (excursion.getPrix() * 100); // Convertir en centimes (si le prix est en TND)

            // Créer PaymentIntent
            PaymentIntent paymentIntent = paymentService.createPaymentIntent(amount);

            // Passer le client_secret au frontend via JavaScript
            String clientSecret = paymentIntent.getClientSecret();

            // Charger le client secret dans le WebView pour que le paiement se fasse
            WebEngine webEngine = webView.getEngine();
            webEngine.executeScript("window.clientSecret = '" + clientSecret + "';");

            // Afficher une alerte de confirmation
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Paiement Initié");
            alert.setHeaderText("Votre paiement a été initié avec succès !");
            alert.setContentText("ID de paiement: " + paymentIntent.getId());
            alert.showAndWait();
        } catch (Exception e) {
            // Afficher une alerte d'erreur
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur de Paiement");
            alert.setHeaderText("Une erreur est survenue");
            alert.setContentText("Détails de l'erreur: " + e.getMessage());
            alert.showAndWait();
        }
    }

}

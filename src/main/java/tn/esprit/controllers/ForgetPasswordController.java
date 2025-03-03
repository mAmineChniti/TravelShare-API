package tn.esprit.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import javafx.util.Duration;
import tn.esprit.services.EmailSender;
import tn.esprit.services.ServiceUtilisateur;

import java.io.IOException;
import java.sql.SQLException;


public class ForgetPasswordController {

    @FXML
    private TextField codeField;

    @FXML
    private TextField emailField;

    @FXML
    private Label timerLabel;

    @FXML
    private AnchorPane layer1;

    @FXML
    private AnchorPane layer2;

    @FXML
    private Label messageLabel;

    @FXML
    private Label messageLabel1;

    @FXML
    private Label messageLabel2;

    @FXML
    private TextField newPasswordField;

    @FXML
    private Label u1;

    @FXML
    private Label u2;

    @FXML
    private Button sendCodeButton;

    @FXML
    private Button resetPasswordButton;

    private final ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
    private String generatedCode;  // Code de vérification généré
    private LocalDateTime expirationTime; // Temps d'expiration du code
    private Timeline countdown;   // Timeline pour gérer le chrono

    @FXML
    void sendCode(ActionEvent event) {
        String email = emailField.getText();

        if (email.isEmpty()) {
            messageLabel.setText("Veuillez entrer votre email !");
            messageLabel.setTextFill(javafx.scene.paint.Color.RED);
            messageLabel.setVisible(true);
            return;
        }

        try {
            if (!serviceUtilisateur.emailExists(email)) {
                messageLabel.setText("Cet email n'est pas enregistré !");
                messageLabel.setTextFill(javafx.scene.paint.Color.RED);
                messageLabel.setVisible(true);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Générer et envoyer le code
        generatedCode = generateCode();
        EmailSender.sendEmail(email, "Réinitialisation du mot de passe", "Votre code de vérification est : " + generatedCode);

        // Définir l'heure d'expiration du code (5 minutes)
        expirationTime = LocalDateTime.now().plusMinutes(5);

        // Afficher le message de succès
        messageLabel.setText("Un code de vérification a été envoyé à votre email.");
        messageLabel.setTextFill(javafx.scene.paint.Color.GREEN);
        messageLabel.setVisible(true);

        // Préparer l'affichage du chrono
        timerLabel.setText("");
        timerLabel.setVisible(true);

        // Démarrer le chrono
        startTimer();
    }

    @FXML
    void resetPassword(ActionEvent event) {
        String codeEntered = codeField.getText();
        String newPassword = newPasswordField.getText();

        // Cacher le message "Code incorrect"
        messageLabel1.setVisible(false);

        // Vérifier si le code est incorrect ou expiré
        if (!codeEntered.equals(generatedCode)) {
            messageLabel1.setText("Code incorrect !");
            messageLabel1.setTextFill(javafx.scene.paint.Color.RED);
            messageLabel1.setVisible(true);
            return;
        }

        if (LocalDateTime.now().isAfter(expirationTime)) {
            messageLabel1.setText("Code expiré !");
            messageLabel1.setTextFill(javafx.scene.paint.Color.RED);
            messageLabel1.setVisible(true);
            return;
        }

        // Vérifier si le champ mot de passe est vide
        if (newPassword.isEmpty()) {
            messageLabel2.setText("Veuillez entrer un nouveau mot de passe !");
            messageLabel2.setTextFill(javafx.scene.paint.Color.RED);
            messageLabel2.setVisible(true);
            return;
        }

        // Mettre à jour le mot de passe
        serviceUtilisateur.updatePassword(emailField.getText(), newPassword);

        // Afficher un message de succès
        messageLabel2.setText("Votre mot de passe a été réinitialisé !");
        messageLabel2.setTextFill(javafx.scene.paint.Color.GREEN);
        messageLabel2.setVisible(true);

        // Naviguer vers la page de connexion
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Connecter.fxml"));
            Parent root = loader.load();
            Scene scene = messageLabel1.getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void startTimer() {
        if (countdown != null) {
            countdown.stop(); // Arrêter un éventuel timer en cours
        }

        countdown = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTimer()));
        countdown.setCycleCount(Timeline.INDEFINITE);
        countdown.play();
    }

    private void updateTimer() {
        long secondsRemaining = ChronoUnit.SECONDS.between(LocalDateTime.now(), expirationTime);

        if (secondsRemaining < 0) {
            timerLabel.setText("Le code a expiré !");
            timerLabel.setTextFill(javafx.scene.paint.Color.RED);
            countdown.stop(); // Arrêter le chrono après affichage de "00:00"
            return;
        }

        long minutes = secondsRemaining / 60;
        long seconds = secondsRemaining % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
        timerLabel.setTextFill(javafx.scene.paint.Color.BLUE);
        timerLabel.setVisible(true);

        // Arrêter le chrono juste après affichage de "00:00"
        if (secondsRemaining == 0) {
            countdown.stop();
        }
    }

    private String generateCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    @FXML
    public void initialize() {
        timerLabel.setVisible(false);
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
            e.printStackTrace();
        }
    }
}

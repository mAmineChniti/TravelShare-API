package tn.esprit.controllers;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tn.esprit.entities.Chambres;
import tn.esprit.entities.ReservationHotel;
import tn.esprit.entities.SessionManager;
import tn.esprit.services.ServiceReservationHotel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;

public class ReservationChambreController {

    private static final OkHttpClient client = new OkHttpClient();
    private static final String QR_API_URL = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=";
    private final ServiceReservationHotel serviceReservation = new ServiceReservationHotel();
    @FXML
    private Label numeroChambreLabel, typeLabel, prixLabel, statutLabel;
    @FXML
    private DatePicker startDatePicker, endDatePicker;
    @FXML
    private Button confirmButton, cancelButton;
    private Chambres chambre;

    @FXML
    public void SwitchToAccueil(ActionEvent actionEvent) {
        try {
            String AccueilLink = SessionManager.getInstance().getCurrentUtilisateur().getRole() == 1 ? "/AccueilAdmin.fxml" : "/Accueil.fxml";
            Parent root = FXMLLoader.load(getClass().getResource(AccueilLink));
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
            Parent root = FXMLLoader.load(getClass().getResource("/Voyages.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void SwitchToHotels(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Hotel.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void SwitchToPosts(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Posts.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToProfile(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ProfileUtilisateur.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void deconnexion(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Connecter.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setChambre(Chambres chambre) {
        this.chambre = chambre;
        displayChambreDetails();
    }

    private void displayChambreDetails() {
        numeroChambreLabel.setText("Chambre #" + chambre.getNumero_chambre());
        typeLabel.setText(chambre.getType_enu());
        prixLabel.setText(String.format("Prix/nuit: TND %.2f", chambre.getPrix_par_nuit()));
        statutLabel.setText(chambre.isDisponible() ? "Disponible" : "Non disponible");
    }

    @FXML
    private void confirmReservation() {
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner les dates.");
            return;
        }
        if (!endDatePicker.getValue().isAfter(startDatePicker.getValue())) {
            showAlert(Alert.AlertType.WARNING, "La date de fin doit être postérieure à la date de début.");
            return;
        }
        if (startDatePicker.getValue().isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.WARNING, "La date de début ne peut pas être antérieure à aujourd'hui.");
            return;
        }
        ReservationHotel reservation = new ReservationHotel();
        reservation.setClient_id(SessionManager.getInstance().getCurrentUtilisateur().getUser_id());
        reservation.setChambre_id(chambre.getChambre_id());
        reservation.setDate_debut(java.sql.Date.valueOf(startDatePicker.getValue()));
        reservation.setDate_fin(java.sql.Date.valueOf(endDatePicker.getValue()));
        reservation.setStatus_enu("en attente");
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDatePicker.getValue(), endDatePicker.getValue());
        reservation.setPrix_totale((int) (daysBetween * chambre.getPrix_par_nuit()));
        try {
            serviceReservation.add(reservation);
            sendConfirmationEmail(reservation);
            showAlert(Alert.AlertType.INFORMATION, "Réservation confirmée !");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de la réservation.");
            e.printStackTrace();
        }
    }

    private void sendConfirmationEmail(ReservationHotel reservation) {
        final String host = "smtp.gmail.com";
        final String username = Dotenv.load().get("EMAIL_ADDRESS");
        final String password = Dotenv.load().get("EMAIL_PASSWORD");
        System.out.println("Email Address: " + username); // Print the email address
        System.out.println("Password: " + password);
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(SessionManager.getInstance().getCurrentUtilisateur().getEmail()));
            message.setSubject("Your booking details and confirmation.");
            Multipart multipart = new MimeMultipart();
            MimeBodyPart textBodyPart = new MimeBodyPart();
            String htmlContent = "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; " +
                    "border: 1px solid #ddd; border-radius: 10px; background-color: #f9f9f9;\">" +
                    "<h2 style=\"color: #007bff; text-align: center;\">Votre réservation est confirmée !</h2>" +
                    "<p style=\"font-size: 16px; color: #333;\">Merci d'avoir réservé avec nous.</p>" +
                    "<p style=\"font-size: 16px; color: #333;\">Voici les détails de votre réservation :</p>" +
                    "<ul style=\"list-style: none; padding: 0;\">" +
                    "<li style=\"background: #f1f1f1; padding: 10px; border-radius: 5px; margin-bottom: 8px;\"><b>Chambre :</b> " +
                    "<span style=\"color: #007bff;\">" + chambre.getNumero_chambre() + "</span></li>" +
                    "<li style=\"background: #f1f1f1; padding: 10px; border-radius: 5px; margin-bottom: 8px;\"><b>Date d'arrivée :</b> " +
                    "<span style=\"color: #007bff;\">" + reservation.getDate_debut() + "</span></li>" +
                    "<li style=\"background: #f1f1f1; padding: 10px; border-radius: 5px; margin-bottom: 8px;\"><b>Date de départ :</b> " +
                    "<span style=\"color: #007bff;\">" + reservation.getDate_fin() + "</span></li>" +
                    "<li style=\"background: #f1f1f1; padding: 10px; border-radius: 5px; margin-bottom: 8px;\"><b>Prix total :</b> " +
                    "<span style=\"color: #007bff;\">" + reservation.getPrix_totale() + " TND</span></li>" +
                    "</ul>" +
                    "<p style=\"text-align: center;\"><img src=\"cid:qrCode\" alt=\"QR Code\" style=\"max-width: 200px; margin-top: 15px; " +
                    "border: 2px solid #ddd; border-radius: 10px; padding: 5px;\"></p>" +
                    "</div>";
            textBodyPart.setContent(htmlContent, "text/html; charset=utf-8");
            multipart.addBodyPart(textBodyPart);
            String qrFilePath = fetchQRCode(reservation);
            if (qrFilePath != null) {
                MimeBodyPart imagePart = new MimeBodyPart();
                imagePart.attachFile(new File(qrFilePath));
                imagePart.setContentID("<qrCode>");
                imagePart.setDisposition(MimeBodyPart.INLINE);
                multipart.addBodyPart(imagePart);
            }
            message.setContent(multipart);
            Transport.send(message);
            System.out.println("Sent message successfully");
        } catch (MessagingException | IOException e) {
            System.out.println(e);
        }
    }

    public static class EmailTest {
        public static void main(String[] args) {
            final String username = Dotenv.load().get("EMAIL_ADDRESS");
            final String password = Dotenv.load().get("EMAIL_PASSWORD");

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("recipient@example.com"));
                message.setSubject("Test Email");
                message.setText("This is a test email.");
                Transport.send(message);
                System.out.println("Email sent successfully!");
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    public String fetchQRCode(ReservationHotel reservation) {
        try {
            String qrText = "Réservation confirmée:\n" +
                    "Chambre: " + chambre.getNumero_chambre() + "\n" +
                    "Date d'arrivée: " + reservation.getDate_debut() + "\n" +
                    "Date de départ: " + reservation.getDate_fin() + "\n" +
                    "Prix total: " + reservation.getPrix_totale() + " TND";
            String encodedQrText = URLEncoder.encode(qrText, StandardCharsets.UTF_8);
            String apiUrl = QR_API_URL + encodedQrText;
            Request request = new Request.Builder().url(apiUrl).build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    File tempFile = File.createTempFile("qrcode", ".png");
                    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                        fos.write(response.body().bytes());
                    }
                    return tempFile.getAbsolutePath();
                } else {
                    System.err.println("Error: " + response.code() + " - " + response.message());
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void cancelReservation() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

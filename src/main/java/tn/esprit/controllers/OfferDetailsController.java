package tn.esprit.controllers;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import tn.esprit.entities.OffreReservations;
import tn.esprit.entities.OffreVoyages;
import tn.esprit.entities.SessionManager;
import tn.esprit.services.OffreReservationService;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.SQLException;

public class OfferDetailsController {

    public static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";
    public static final String GEMINI_API_KEY = Dotenv.load().get("GEMINI_API_KEY");
    @FXML
    private Label titleLabel, destinationLabel, descriptionLabel, departureDateLabel,
            returnDateLabel, priceLabel, availableSeatsLabel, reservationStatusLabel;
    @FXML
    private Label activitiesLabel;
    @FXML
    private Button reserveButton;
    @FXML
    private Spinner<Integer> placesSpinner;
    private OffreVoyages currentOffer;

    @FXML
    public void SwitchToAccueil(ActionEvent actionEvent) {
        try {
            String AccueilLink = SessionManager.getInstance().getCurrentUtilisateur().getRole() == 1 ? "/AccueilAdmin.fxml" : "/Accueil.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AccueilLink));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void switchToVoyages(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Voyages.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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
    void switchToProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfileUtilisateur.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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
    public void initialize() {
        System.out.println("Initializing OfferDetailsController...");
        System.out.println("placesSpinner: " + placesSpinner); // Debugging

        if (placesSpinner == null) {
            System.out.println("placesSpinner is null! Check FXML fx:id.");
        } else {
            SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1, 1);
            placesSpinner.setValueFactory(valueFactory);
        }
    }

    public void setOfferDetails(OffreVoyages offer) {
        this.currentOffer = offer;
        titleLabel.setText(offer.getTitre());
        destinationLabel.setText(offer.getDestination());
        descriptionLabel.setText(offer.getDescription());
        departureDateLabel.setText("Departure: " + offer.getDate_depart());
        returnDateLabel.setText("Return: " + offer.getDate_retour());
        priceLabel.setText("Price: " + offer.getPrix() + " $");
        availableSeatsLabel.setText("Seats: " + offer.getPlaces_disponibles());

        // Update spinner max value based on available places
        placesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, offer.getPlaces_disponibles(), 1));
        try {
            String activities = getActivitiesbyCountry(offer.getDestination());
            activitiesLabel.setText(activities);
        } catch (IOException e) {
            activitiesLabel.setText("Could not fetch activities.");
        }
    }

    public String getActivitiesbyCountry(String country) throws IOException {
        JSONObject request = new JSONObject();
        JSONArray contents = new JSONArray();
        JSONObject contentObject = new JSONObject();
        JSONArray parts = new JSONArray();
        JSONObject textPart = new JSONObject();

        textPart.put("text", "Describe to me in 500 characters maximum " + country + " and what are some activities I can do in " + country);
        parts.put(textPart);
        contentObject.put("parts", parts);
        contents.put(contentObject);
        request.put("contents", contents);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(request.toString(), MediaType.parse("application/json"));
        Request fetcher = new Request.Builder()
                .url(API_URL + GEMINI_API_KEY)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(fetcher).execute()) {
            //System.out.println("Request sent to API.");
            //System.out.println("Response code: " + response.code());

            if (!response.isSuccessful()) {
                System.out.println("Request failed: " + response.message());
                System.out.println("Response body: " + response.body().string());
                throw new IOException("Failed to fetch activities: " + response.code());
            }

            String responseBody = response.body().string();
            //System.out.println("API Response: " + responseBody);

            JSONObject responseObject = new JSONObject(responseBody);
            JSONArray candidates = responseObject.getJSONArray("candidates");
            contentObject = candidates.getJSONObject(0).getJSONObject("content");
            JSONArray partsArray = contentObject.getJSONArray("parts");
            return partsArray.getJSONObject(0).getString("text");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            return "Error fetching activities: "; // + e.getMessage();
        }
    }

    @FXML
    public void reserveOffer() {
        System.out.println("Reserve button clicked!");

        if (placesSpinner == null) {
            reservationStatusLabel.setText("Error: Spinner not initialized.");
            reservationStatusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        int nbr_places = placesSpinner.getValue();
        if (currentOffer == null) {
            reservationStatusLabel.setText("Offer not found.");
            reservationStatusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (nbr_places > currentOffer.getPlaces_disponibles() || nbr_places < 1) {
            reservationStatusLabel.setText("Invalid number of places.");
            reservationStatusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            OffreReservationService resOffre = new OffreReservationService();
            OffreReservations reservation = new OffreReservations();
            reservation.setOffre_id(currentOffer.getOffres_voyage_id());
            reservation.setClient_id(SessionManager.getInstance().getCurrentUtilisateur().getUser_id());
            reservation.setDate_reserved(new Date(new java.util.Date().getTime()));
            reservation.setReserved(true);
            reservation.setNbr_place(nbr_places);
            reservation.setPrix(nbr_places * currentOffer.getPrix());

            resOffre.add(reservation);

            // Update available seats
            currentOffer.setPlaces_disponibles(currentOffer.getPlaces_disponibles() - nbr_places);
            availableSeatsLabel.setText("Seats: " + currentOffer.getPlaces_disponibles());

            placesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                    0, currentOffer.getPlaces_disponibles(), 1));

            // Generate PDF receipt
            generatePDF(reservation);

            reservationStatusLabel.setText("Reservation successful! PDF generated.");
            reservationStatusLabel.setStyle("-fx-text-fill: green;");
        } catch (SQLException e) {
            e.printStackTrace();
            reservationStatusLabel.setText("Reservation failed. Try again.");
            reservationStatusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void generatePDF(OffreReservations reservation) {
        String pdfPath = "reservation_" + reservation.getOffre_id() + ".pdf";
        try {
            PdfWriter writer = new PdfWriter(new FileOutputStream(pdfPath));
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Add Logo
            // Chargement de l'image depuis le classpath
            InputStream imageStream = getClass().getResourceAsStream("/images/agence.png");

            if (imageStream != null) {
                byte[] imageBytes = imageStream.readAllBytes();
                ImageData imageData = ImageDataFactory.create(imageBytes);
                Image logo = new Image(imageData);
                logo.setWidth(100).setHeight(100);
                document.add(logo);
            } else {
                System.out.println("Logo file not found in resources!");
            }

            // Title
            document.add(new Paragraph("Reservation Confirmation")
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph(" "));

            // Reservation Details
            document.add(new Paragraph("Reservation ID: " + reservation.getOffre_id()));
            document.add(new Paragraph("Client ID: " + reservation.getClient_id()));
            document.add(new Paragraph("Date Reserved: " + reservation.getDate_reserved().toString()));
            document.add(new Paragraph("Number of Places: " + reservation.getNbr_place()));
            document.add(new Paragraph("Total Price: " + reservation.getPrix() + " €"));
            document.add(new Paragraph(" "));

            // Offer Details
            document.add(new Paragraph("Offer Details")
                    .setFontSize(14));

            document.add(new Paragraph("Destination: " + currentOffer.getDestination()));
            document.add(new Paragraph("Departure Date: " + currentOffer.getDate_depart()));
            document.add(new Paragraph("Available Seats After Booking: " + currentOffer.getPlaces_disponibles()));

            document.close();

            System.out.println("PDF generated: " + pdfPath);

            // Automatically download the PDF
            File pdfFile = new File(pdfPath);
            if (pdfFile.exists()) {
                Desktop.getDesktop().open(pdfFile);
            }
            // catch
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
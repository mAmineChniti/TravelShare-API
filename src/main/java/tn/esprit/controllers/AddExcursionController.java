package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.Excursions;
import tn.esprit.entities.Guides;
import tn.esprit.services.ServiceExcursion;
import tn.esprit.services.ServiceGuide;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AddExcursionController {

    @FXML private TextField titleTF;
    @FXML private TextArea descriptionTA;
    @FXML private DatePicker datePicker;
    @FXML private DatePicker datePicker2;
    @FXML private ChoiceBox<String> choiceBoxGuide;
    @FXML private Label errorTitle, errorDescription, errorDateDebut, errorDateFin, errorGuide, imagePathLabel,errorPrice;
    @FXML private TextField priceTF;

    private File selectedFile;

    @FXML
    public void initialize() {
        loadGuides();
        configureDatePickers();
    }

    private void loadGuides() {
        try {
            ServiceGuide serviceGuide = new ServiceGuide();
            List<Guides> guides = serviceGuide.ListAll();
            guides.forEach(guide -> choiceBoxGuide.getItems().add(guide.getName()));
            System.out.println("Guides loaded successfully.");
        } catch (SQLException e) {
            System.err.println("Error loading guides: " + e.getMessage());
        }
    }

    private void configureDatePickers() {
        datePicker.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) setDisable(true);
            }
        });

        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            datePicker2.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    if (newVal != null && date.isBefore(newVal)) setDisable(true);
                }
            });

            if (newVal != null && datePicker2.getValue() != null && datePicker2.getValue().isBefore(newVal)) {
                datePicker2.setValue(null);
            }
        });
    }

    @FXML
    void uploadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        selectedFile = fileChooser.showOpenDialog(((javafx.scene.Node) event.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            // Vérifier si le dossier "images" existe, sinon le créer
            File imagesDir = new File("images");
            if (!imagesDir.exists()) {
                imagesDir.mkdirs();
            }

            // Renommer le fichier si nécessaire
            String destinationPath = "images/" + selectedFile.getName();
            try {
                Files.copy(selectedFile.toPath(), new File(destinationPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
                imagePathLabel.setText(selectedFile.getName());
                System.out.println("Selected file: " + selectedFile.getName());
            } catch (IOException e) {
                imagePathLabel.setText("Error copying file: " + e.getMessage());
                System.err.println("Error copying file: " + e.getMessage());
            }
        } else {
            imagePathLabel.setText("No file selected");
        }
    }


    @FXML
    void addExcursion(ActionEvent event) {
        resetErrorMessages();
        if (!validateInputs()) return;

        try {
            Excursions excursion = new Excursions();
            excursion.setTitle(titleTF.getText().trim());
            excursion.setDescription(descriptionTA.getText().trim());
            excursion.setDate_excursion(Date.valueOf(datePicker.getValue()));
            excursion.setDate_fin(Date.valueOf(datePicker2.getValue()));
            excursion.setPrix(Double.parseDouble(priceTF.getText().trim()));
            String selectedGuide = choiceBoxGuide.getValue();

            ServiceGuide serviceGuide = new ServiceGuide();
            Guides guide = serviceGuide.getGuideByName(selectedGuide);
            if (guide != null) {
                excursion.setGuide_id(guide.getGuide_id());
            } else {
                showError("Guide selection is invalid.");
                return;
            }

            if (selectedFile != null) {
                String destinationPath = "images/" + selectedFile.getName();
                Files.copy(selectedFile.toPath(), new File(destinationPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
                excursion.setImage(destinationPath);
            }

            ServiceExcursion serviceExcursion = new ServiceExcursion();
            serviceExcursion.add(excursion);

            showConfirmation("Excursion added successfully!");

            // Optionally, navigate to another scene after adding the excursion
            // navigateToSomeOtherScene(event);

        } catch (SQLException | IOException e) {
            showError("Error adding excursion: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (titleTF.getText().trim().isEmpty()) {
            errorTitle.setText("Title is required.");
            errorTitle.setVisible(true);
            isValid = false;
        }
        if (descriptionTA.getText().trim().isEmpty()) {
            errorDescription.setText("Description is required.");
            errorDescription.setVisible(true);
            isValid = false;
        }
        if (datePicker.getValue() == null) {
            errorDateDebut.setText("Start date is required.");
            errorDateDebut.setVisible(true);
            isValid = false;
        }
        if (datePicker2.getValue() == null) {
            errorDateFin.setText("End date is required.");
            errorDateFin.setVisible(true);
            isValid = false;
        }
        if (choiceBoxGuide.getValue() == null) {
            errorGuide.setText("Guide is required.");
            errorGuide.setVisible(true);
            isValid = false;
        }
        if (priceTF.getText().trim().isEmpty()) {
            errorPrice.setText("Price is required.");
            errorPrice.setVisible(true);
            isValid = false;
        }

        return isValid;
    }

    private void resetErrorMessages() {
        errorTitle.setVisible(false);
        errorDescription.setVisible(false);
        errorDateDebut.setVisible(false);
        errorDateFin.setVisible(false);
        errorGuide.setVisible(false);
        errorPrice.setVisible(false);
    }

    @FXML
    void goBack1(ActionEvent event) throws IOException {
        Parent homePageParent = FXMLLoader.load(getClass().getResource("/ListExcursion.fxml"));
        Scene homePageScene = new Scene(homePageParent);
        Stage appStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        appStage.setScene(homePageScene);
        appStage.show();
    }
}

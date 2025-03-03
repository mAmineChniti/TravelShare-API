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
    @FXML private Label errorTitle, errorDescription, errorDateDebut, errorDateFin, errorGuide, imagePathLabel, errorPrice;
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

            // Vérification du contenu
            System.out.println("Guides récupérés depuis la BD : " + guides);

            for (Guides guide : guides) {
                choiceBoxGuide.getItems().add(guide.getName());
            }

            System.out.println("Guides affichés dans ChoiceBox : " + choiceBoxGuide.getItems());
        } catch (SQLException e) {
            System.err.println("Erreur chargement guides: " + e.getMessage());
        }
    }


    // Configurer les DatePickers
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
            File destinationFile = new File(destinationPath);
            if (destinationFile.exists()) {
                imagePathLabel.setText("File already exists, choosing a different name.");
            } else {
                try {
                    Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    imagePathLabel.setText(selectedFile.getName());
                    System.out.println("Selected file: " + selectedFile.getName());
                } catch (IOException e) {
                    imagePathLabel.setText("Error copying file: " + e.getMessage());
                    System.err.println("Error copying file: " + e.getMessage());
                }
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

            // Vérification et conversion du prix
            try {
                excursion.setPrix(Double.parseDouble(priceTF.getText().trim()));
            } catch (NumberFormatException e) {
                errorPrice.setText("Format du prix invalide.");
                errorPrice.setVisible(true);
                return;
            }

            // Vérification du guide
            String selectedGuide = choiceBoxGuide.getValue();
            if (selectedGuide == null) {
                showError("Veuillez sélectionner un guide.");
                return;
            }

            ServiceGuide serviceGuide = new ServiceGuide();
            Guides guide = serviceGuide.getGuideByName(selectedGuide);
            if (guide == null) {
                showError("Aucun guide trouvé avec ce nom.");
                return;
            }

            excursion.setGuide_id(guide.getGuide_id());

            // Gestion de l'image sélectionnée
            if (selectedFile != null) {
                String destinationPath = "images/" + selectedFile.getName();
                Files.copy(selectedFile.toPath(), new File(destinationPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
                excursion.setImage(destinationPath);
            }

            // Ajouter l'excursion
            ServiceExcursion serviceExcursion = new ServiceExcursion();
            serviceExcursion.add(excursion);

            showConfirmation("Excursion ajoutée avec succès !");
            resetFields();
        } catch (SQLException | IOException e) {
            showError("Erreur lors de l'ajout : " + e.getMessage());
        }
    }







    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (titleTF.getText().trim().isEmpty()) {
            errorTitle.setText("Le titre est obligatoire.");
            errorTitle.setVisible(true);
            isValid = false;
        }
        if (descriptionTA.getText().trim().isEmpty()) {
            errorDescription.setText("La description est obligatoire.");
            errorDescription.setVisible(true);
            isValid = false;
        }
        if (datePicker.getValue() == null) {
            errorDateDebut.setText("Date de début requise.");
            errorDateDebut.setVisible(true);
            isValid = false;
        }
        if (datePicker2.getValue() == null) {
            errorDateFin.setText("Date de fin requise.");
            errorDateFin.setVisible(true);
            isValid = false;
        }
        if (choiceBoxGuide.getValue() == null) {
            errorGuide.setText("Guide requis.");
            errorGuide.setVisible(true);
            isValid = false;
        }
        if (priceTF.getText().trim().isEmpty()) {
            errorPrice.setText("Prix requis.");
            errorPrice.setVisible(true);
            isValid = false;
        }

        return isValid;
    }

    private void resetFields() {
        titleTF.clear();
        descriptionTA.clear();
        datePicker.setValue(null);
        datePicker2.setValue(null);
        choiceBoxGuide.getSelectionModel().clearSelection();
        priceTF.clear();
        imagePathLabel.setText("Aucune image sélectionnée");
        selectedFile = null;
        resetErrorMessages();
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

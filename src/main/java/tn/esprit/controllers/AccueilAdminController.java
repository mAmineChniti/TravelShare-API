package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.ServiceUtilisateur;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AccueilAdminController {

    @FXML
    private ListView<Utilisateur> listuser;

    private final ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

    @FXML
    void initialize() {
        try {
            // 1️⃣ Charger la liste des utilisateurs depuis la base
            List<Utilisateur> utilisateurs = serviceUtilisateur.ListAll();
            ObservableList<Utilisateur> observableList = FXCollections.observableArrayList(utilisateurs);

            listuser.setCellFactory(lv -> new ListCell<Utilisateur>() {
                @Override
                protected void updateItem(Utilisateur utilisateur, boolean empty) {
                    super.updateItem(utilisateur, empty);

                    if (empty || utilisateur == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        // 2️⃣ Création des éléments d'affichage
                        HBox hbox = new HBox(10);

                        Label labelNom = new Label(utilisateur.getName());
                        Label labelPrenom = new Label(utilisateur.getLast_name());
                        Label labelEmail = new Label(utilisateur.getEmail());
                        Label labelTelephone = new Label(String.valueOf(utilisateur.getPhone_num()));
                        Label labelAdresse = new Label(utilisateur.getAddress());

                        // 3️⃣ Image de profil
                        ImageView imageView = new ImageView();
                        imageView.setFitWidth(50);
                        imageView.setFitHeight(50);
                        imageView.setPreserveRatio(true);
                        if (utilisateur.getPhoto() != null && utilisateur.getPhoto().length > 0) {
                            imageView.setImage(new Image(new ByteArrayInputStream(utilisateur.getPhoto())));
                        } else {
                            imageView.setImage(new Image("https://cdn-icons-png.flaticon.com/512/9187/9187604.png"));
                        }

                        // 4️⃣ Bouton avec icône 🚫 ou ✅
                        Button btnBlocage = new Button(utilisateur.getCompte() == 0 ? "🚫" : "✅");
                        btnBlocage.setStyle(utilisateur.getCompte() == 0 ? "-fx-background-color: red; -fx-text-fill: white; font-size: 14px;"
                                : "-fx-background-color: green; -fx-text-fill: white; font-size: 14px;");

                        // 5️⃣ Action du bouton
                        btnBlocage.setOnAction(event -> {
                            try {
                                if (utilisateur.getCompte() == 0) { // 🔴 Bloquer l'utilisateur
                                    serviceUtilisateur.bloquerUtilisateur(utilisateur.getUser_id()); // Mise à jour en BD
                                    utilisateur.setCompte((byte) 1); // Mise à jour locale
                                    btnBlocage.setText("✅"); // Icône Débloquer
                                    btnBlocage.setStyle("-fx-background-color: green; -fx-text-fill: white; font-size: 14px;");
                                } else { // 🟢 Débloquer l'utilisateur
                                    serviceUtilisateur.debloquerUtilisateur(utilisateur.getUser_id()); // Mise à jour en BD
                                    utilisateur.setCompte((byte) 0); // Mise à jour locale
                                    btnBlocage.setText("🚫"); // Icône Bloquer
                                    btnBlocage.setStyle("-fx-background-color: red; -fx-text-fill: white; font-size: 14px;");
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        });

                        // 6️⃣ Ajouter les éléments au HBox
                        hbox.getChildren().addAll(imageView, labelNom, labelPrenom, labelEmail, labelTelephone, labelAdresse, btnBlocage);
                        setGraphic(hbox);
                    }
                }
            });

            listuser.setItems(observableList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    @FXML
    public void SwitchToVoyages(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Voyages.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
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
    void switchToReclamation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReponseAdmin.fxml"));
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
    void SwitchToGuide(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListGuide.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}